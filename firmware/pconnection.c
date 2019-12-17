/**
 * Copyright (C) 2013 - 2019 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
#include "ch.h"
#include "hal.h"
#include "chprintf.h"
#include "pconnection.h"
#include "axoloti_control.h"
#include "parameters.h"
#include "patch.h"
#include "patch_impl.h"
#include "ff.h"
#include "codec.h"
#include "usbcfg.h"
#include "midi.h"
#include "sdcard.h"
#include "ui.h"
#include "string.h"
#include "flash.h"
#include "exceptions.h"
#include "crc32.h"
#include "midi.h"
#include "midi_usb.h"
#include "sysmon.h"
#include "firmware_chunks.h"
#include "axoloti_math.h"
#include "axoloti_memory_impl.h"
#include "patch_wrapper.h"
#include "logging.h"
#include "axoloti_board.h"


static uint32_t fwid;
static thread_t * thd_bulk_Reader;
static thread_t * thd_log_consumer;
static uint32_t fbuff[256];
static FIL file;
static int file_is_open = 0;
enum {fileRef = 0xf113};

#define MAX_FNAME_LENGTH 64

static mutex_t mtxTransmit;
static void log_resume(void);

/*
 * in_stream
 * methods
 */

typedef struct {
  uint8_t *data;
  size_t remaining;
  uint8_t *buf;
} in_stream_t;

static msg_t inStreamRead(in_stream_t *stream) {
  if (stream->remaining) {
    chSysHalt("in_stream buffer not empty");
  }
  msg_t msg = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
      stream->buf, 64);
  if (msg<MSG_OK) {
    return msg;
  }
  stream->data = stream->buf;
  stream->remaining = msg;
  return MSG_OK;
}

static msg_t getInt32(in_stream_t *stream, int32_t *value) {
  typedef struct {
    int32_t value;
    uint8_t data[];
  } int_data_t;
  if (!stream->remaining) {
     msg_t msg = inStreamRead(stream);
     if (msg!=MSG_OK) return msg;
  }
  if (stream->remaining < 4) {
    return MSG_RESET;
  }
  int_data_t * d = (int_data_t *)stream->data;
  *value = d->value;
  stream->data = d->data;
  stream->remaining -= 4;
  return MSG_OK;
}

static msg_t getChar(in_stream_t *stream, char *value) {
  if (!stream->remaining) {
    msg_t msg = inStreamRead(stream);
    if (msg!=MSG_OK) return msg;
  }
  if (!stream->remaining) {
    return MSG_RESET;
  }
  *value = *stream->data;
  stream->data++;
  stream->remaining--;
  return MSG_OK;
}

static msg_t getUInt16(in_stream_t *stream, uint16_t *value) {
  typedef struct {
    uint16_t value;
    uint8_t data[];
  } uint16_data_t;
  if (!stream->remaining) {
    msg_t msg = inStreamRead(stream);
    if (msg!=MSG_OK) return msg;
  }
  if (stream->remaining < 2) {
    return MSG_RESET;
  }
  uint16_data_t * d = (uint16_data_t *)stream->data;
  *value = d->value;
  stream->data = d->data;
  stream->remaining -= 2;
  return MSG_OK;
}

static msg_t getCString(in_stream_t *stream, char *value, size_t max_length) {
  while(max_length--) {
    char c;
    msg_t msg = getChar(stream, &c);
    if (msg != MSG_OK) return msg;
    *value++ = c;
    if (!c) return MSG_OK;
  }
  value[max_length-1] = 0;
  return MSG_OK; // but string is truncated
}

static msg_t getByteArray(in_stream_t *stream, uint8_t *dest, size_t length) {
  while (length) {
    if (!stream->remaining) {
      msg_t msg = inStreamRead(stream);
      if (msg!=MSG_OK) return msg;
    }
    stream->remaining--;
    *dest++ = *stream->data++;
    length--;
  }
  return MSG_OK;
}

static msg_t getUInt32(in_stream_t *stream, uint32_t *value) {
  return getInt32(stream, (int32_t *)value);
}

static msg_t getPatch(in_stream_t *stream, patch_t **value) {
  return getInt32(stream, (int32_t *)value);
}

static void handleUsbErr(msg_t err) {
}

/*
 * Transmit a single packet of 0 to 64 bytes.
 */
static msg_t BulkUsbTransmit(const uint8_t * data, size_t size) {
  bool b = chMtxTryLock(&mtxTransmit);
  if (b) chSysHalt("mtxTransmit was not locked");
  msg_t r = usbTransmit(&USBD1, USBD2_DATA_REQUEST_EP, data, size);
  return r;
}

/*
 * Transmit a single packet of any length.
 * Last transmission is either less than 64 bytes
 * or a zero-length packet.
 */
static msg_t BulkUsbTransmitPacket(const uint8_t * data, size_t size) {
	msg_t res;
	res = BulkUsbTransmit(data, size);
	if (res != MSG_OK) return res;
	if ((size) && ((size & 0x3F) == 0)) {
		// multiple of 64 bytes, append zero-length packet
		res = BulkUsbTransmit(data, 0);
	}
	return res;
}

/*
 * Transmit a single packet of any length.
 * Last transmission is either less than 64 bytes
 * or a zero-length packet.
 *
 * Locks and unlocks mtxTransmit, so only suitable for replies that are covered in a single packet.
 */

static msg_t BulkUsbTransmitPacket1(const uint8_t * data, size_t size) {
  chMtxLock(&mtxTransmit);
  msg_t res = BulkUsbTransmitPacket(data, size);
  chMtxUnlock(&mtxTransmit);
  return res;
}

#define tx_hdr_acknowledge 0x416F7841   // "AxoA"
#define tx_hdr_fwid        0x566f7841   // "AxoV"
#define tx_hdr_log         0x546F7841   // "AxoT"
#define tx_hdr_memrd       0x726f7841   // "Axor"
#define tx_hdr_patch_paramchange 0x71507841   // "AxPq"
#define tx_hdr_patch_disp  0x64507841   // "AxPd"
#define tx_hdr_patch_list  0x6C507841   // "AxPl"
#define tx_hdr_f_info      0x69467841   // "AxFi"
#define tx_hdr_f_read      0x72467841   // "AxFr"
#define tx_hdr_f_dir       0x64467841   // "AxFd"
#define tx_hdr_f_dir_end   0x44467841   // "AxFD"
#define tx_hdr_f_result    0x65467841   // "AxFe"
#define tx_hdr_result_ptr  0x536F7841   // "AxoS"
#define tx_hdr_cstring     0x736F7841   // "Axos"

tx_pckt_ack_v2_t tx_pckt_ack_v2 = {
		.header = tx_hdr_acknowledge,
		.version = 1,
		.dspload = 0,
		.patchID = 0,
		.voltage = 0,
		.loadPatchIndex = -1,
		.fs_ready = 0,
		.vu_input = {0,0},
		.vu_output = {0,0},
		.underruns = 0,
		.sram1_free = 0,
    .sram3_free = 0,
    .ccmram_free = 0,
    .sdram_free = 0
};

static msg_t tx_ack(void) {
	tx_pckt_ack_v2.dspload = dspLoadPct;
	tx_pckt_ack_v2.patchID = 0; // patchMeta.patchID;
	tx_pckt_ack_v2.voltage = sysmon_getVoltage10() + (sysmon_getVoltage50() << 16);
	tx_pckt_ack_v2.fs_ready = fs_ready;
	tx_pckt_ack_v2.sram1_free = sram1_available();
  tx_pckt_ack_v2.sram3_free = sram3_available();
  tx_pckt_ack_v2.ccmram_free = ccmram_available();
  tx_pckt_ack_v2.sdram_free = sdram_available();
  chMtxLock(&mtxTransmit);
	msg_t msg = BulkUsbTransmitPacket((const unsigned char* )&tx_pckt_ack_v2, sizeof(tx_pckt_ack_v2));
  chMtxUnlock(&mtxTransmit);
  return msg;
}

msg_t tx_patchList(void) {
  uint32_t data[16]; // assumes no more than 15 patches
  data[0] = tx_hdr_patch_list;
  int i = 1;
  patch_t * patch;
  for(patch = patch_iter_first();patch_iter_done(patch);patch=patch_iter_next(patch)) {
    if (patch_getStatus(patch) == RUNNING)
      data[i++] = (uint32_t)patch;
  }
  return BulkUsbTransmitPacket1((const unsigned char* )&data[0], i*sizeof(int32_t));
}

msg_t tx_cstring(const char *str) {
  // TODO: handle long strings, currently truncated at 59 chars
  uint8_t pckt[64];
  *(int32_t *)pckt = tx_hdr_cstring;
  uint8_t *p = &pckt[4];
  if (str!=0) {
    while(*str != 0) {
      *p++ = *str++;
      if (p == &pckt[62]) {
        break;
      }
    }
  }
  *p++ = 0;
  return BulkUsbTransmitPacket1(&pckt[0], p-pckt);
}

static msg_t rcv_getfwid(in_stream_t *in_stream) {
  typedef struct {
    uint32_t header;
    uint8_t version[4];
    uint32_t fw_crc;
    uint32_t fw_chunkaddr;
  } tx_pckt_fwversion_t;
  tx_pckt_fwversion_t pckt = {
    .header = tx_hdr_fwid,
    .version = {FWVERSION1, FWVERSION2, FWVERSION3, FWVERSION4},
    .fw_crc =  fwid,
    .fw_chunkaddr = (uint32_t)chunk_fw_root_data
  };
  msg_t msg = BulkUsbTransmitPacket1((const unsigned char* )(&pckt), sizeof(pckt));
  log_resume();
  if (in_stream->remaining == 0) {
    LogTextMessage("Firmware is newer than the software release,");
    LogTextMessage("firmware update will fail,");
    LogTextMessage("and connection will fail too.");
    LogTextMessage("You need to downgrade firmware via the rescue (DFU) method!");
  }
  return msg;
}

static msg_t tx_fileinfo(FILINFO *fileinfo, FRESULT err) {
  struct {
    int32_t header;
    int32_t err;
    int32_t fsize;
    int16_t date;
    int16_t time;
    char filename[51];
  } tx_pckt_fileinfo;
  tx_pckt_fileinfo.header = tx_hdr_f_info;
  tx_pckt_fileinfo.err = err;
  tx_pckt_fileinfo.fsize = fileinfo->fsize;
  tx_pckt_fileinfo.date = fileinfo->fdate;
  tx_pckt_fileinfo.time = fileinfo->ftime;
#if _USE_LFN
      const char *fn = *fileinfo->lfname ? fileinfo->lfname : fileinfo->fname;
#else
      const char *fn = fileinfo->fname;
#endif
  strncpy(tx_pckt_fileinfo.filename, fn, sizeof(tx_pckt_fileinfo.filename));
  if (fileinfo->fattrib & AM_DIR) {
    int i = strlen(tx_pckt_fileinfo.filename);
    tx_pckt_fileinfo.filename[i] = '/';
    tx_pckt_fileinfo.filename[i+1] = 0;
  }
  msg_t msg = BulkUsbTransmitPacket((uint8_t *)&tx_pckt_fileinfo, 16 + strlen(tx_pckt_fileinfo.filename));
  return msg;
}

#define PIPE_SIZE 128

/*
 * LogStream specific data.
 */
#define _log_stream_data                                                    \
  _base_sequential_stream_data                                              \
  uint8_t buffer[PIPE_SIZE];                                                \
  pipe_t pipe;

/*
 * LogStream virtual methods table, nothing added.
 */
struct LogStreamVMT {
  _base_sequential_stream_methods
};
/**
 * Memory stream object.
 */
typedef struct {
  /** @brief Virtual Methods Table.*/
  const struct LogStreamVMT *vmt;
  _log_stream_data
} LogStream;

static void logObjectInit(LogStream *msp);


static size_t writes(void *ip, const uint8_t *bp, size_t n) {
  LogStream *msp = ip;
  msg_t r = chPipeWriteTimeout(&msp->pipe, bp, n, TIME_INFINITE);
  (void)r;
  return n;
}

static size_t reads(void *ip, uint8_t *bp, size_t n){
	return 0;
}

static msg_t put(void *ip, uint8_t b) {
  LogStream *msp = ip;
  msg_t r = chPipeWriteTimeout(&msp->pipe, &b, 1, TIME_INFINITE);
  (void)r;
  return MSG_OK;
}

static msg_t get(void *ip) {
	return 0;
}

static const struct LogStreamVMT vmt = {(size_t)0, writes, reads, put, get};

LogStream logstream;
static char log_tx_buf[63];

static THD_WORKING_AREA(waLogConsumer, 256);
static THD_FUNCTION(LogConsumer, arg) {
  (void)arg;
  chRegSetThreadName("logconsumer");
  *(int *)(&log_tx_buf) = tx_hdr_log;
  while(1) {
    size_t nread;
    nread = chPipeReadTimeout(&logstream.pipe, &log_tx_buf[4],
                               sizeof(log_tx_buf)-4, TIME_MS2I(10));
    if (nread > 0) {
      msg_t error;
      chMtxLock(&mtxTransmit);
      error = BulkUsbTransmitPacket(log_tx_buf, nread + 4);
      chMtxUnlock(&mtxTransmit);
    } else if (logstream.pipe.reset) {
      chThdSleep(10);
    }
  }
}

static void logObjectInit(LogStream *msp) {
  msp->vmt    = &vmt;
  chPipeObjectInit(&msp->pipe, &msp->buffer[0], PIPE_SIZE);
  chPipeReset(&msp->pipe);
}

static void log_suspend(void) {
  chPipeReset(&logstream.pipe);
}

static void log_resume(void) {
  chPipeResume(&logstream.pipe);
}

static msg_t bulk_tx_patch_paramchange1(patch_t * patch) {
  msg_t r = 0;
  if (patch_getStatus(patch) == RUNNING) {
    chMtxLock(&mtxTransmit);
    unsigned int i;
    int n = patch_getNParams(patch);
    for (i = 0; i < n; i++) {
      Parameter_t * param =  patch_getParam(patch,i);
      if (param->signals & 0x01) {
        int v = param->d.frac.value;  // FIXME: can't assume parameter type is t_frac
        param->signals &= ~0x01;
        struct {
          int32_t header;
          patch_t * patchID;
          int32_t value;
          int32_t index;
        } tx_pckt_paramchange;
        tx_pckt_paramchange.header = tx_hdr_patch_paramchange; //"AxoQ"
        tx_pckt_paramchange.patchID = patch;
        tx_pckt_paramchange.value = v;
        tx_pckt_paramchange.index = i;
        r = BulkUsbTransmit((const unsigned char* )&tx_pckt_paramchange, sizeof(tx_pckt_paramchange));
        if (r<0) break;
      }
    }
    chMtxUnlock(&mtxTransmit);
  }
  return r;
}

static msg_t tx_patch_paramchange(void) {
	msg_t r = 0;
	patch_t * patch;
  for(patch = patch_iter_first();patch_iter_done(patch);patch=patch_iter_next(patch)) {
    r = bulk_tx_patch_paramchange1(patch);
    if (r<0) break;
  }
	return r;
}

static msg_t tx_reply_ptr(void * v) {
  struct {
    uint32_t header;
    void * value;
  } tx_pckt_result_ptr;
  tx_pckt_result_ptr.header = tx_hdr_result_ptr;
  tx_pckt_result_ptr.value = v;
  chMtxLock(&mtxTransmit);
  msg_t msg = BulkUsbTransmit((const unsigned char* )(&tx_pckt_result_ptr),
      sizeof(tx_pckt_result_ptr));
  chMtxUnlock(&mtxTransmit);
  return msg;
}

static msg_t tx_fresult(FRESULT err, int fref) {
  struct {
    uint32_t header;
    int fref;
    int32_t err;
  } tx_pckt_ferror;
  tx_pckt_ferror.header = tx_hdr_f_result;
  tx_pckt_ferror.err = err;
  tx_pckt_ferror.fref = fref;
  chMtxLock(&mtxTransmit);
  msg_t msg = BulkUsbTransmit((const unsigned char* )(&tx_pckt_ferror),
      sizeof(tx_pckt_ferror));
  chMtxUnlock(&mtxTransmit);
  return msg;
}

static msg_t rcv_ping(in_stream_t *in_stream) {
  handleUsbErr(tx_ack());
  if (!logstream.pipe.reset) {
	  exception_checkandreport();
  }
  handleUsbErr(tx_patch_paramchange());
  return MSG_OK;
}

static msg_t rcv_patch_get_disp(in_stream_t *in_stream) {

  patch_t *patch;
  msg_t msg = getPatch(in_stream, &patch);
  if (msg!=MSG_OK) return msg;

  struct {
    uint32_t header;
    uint32_t size;
    patch_t * patch;
  } tx_pckt_disp;

  if (patch_getStatus(patch) == RUNNING) {
    tx_pckt_disp.header = tx_hdr_patch_disp;
    tx_pckt_disp.size = 4*patch_getDisplayVectorSize(patch);
    tx_pckt_disp.patch = patch;
    chMtxLock(&mtxTransmit);
    msg = BulkUsbTransmit((const unsigned char* )(&tx_pckt_disp), sizeof(tx_pckt_disp));
    if (msg==MSG_OK) {
      if (tx_pckt_disp.size) {
        msg = BulkUsbTransmitPacket((const unsigned char* )patch_getDisplayVector(patch), tx_pckt_disp.size);
      }
    }
    chMtxUnlock(&mtxTransmit);
  } else {
    tx_pckt_disp.header = tx_hdr_patch_disp;
    tx_pckt_disp.size = 0;
    tx_pckt_disp.patch = 0;
    chMtxLock(&mtxTransmit);
    msg = BulkUsbTransmit((const unsigned char* )(&tx_pckt_disp), sizeof(tx_pckt_disp));
    chMtxUnlock(&mtxTransmit);
  }
  return msg;
}

static msg_t rcv_mem_read(in_stream_t *in_stream) {
  uint32_t offset;
  uint32_t size;
  getUInt32(in_stream, &offset);
  getUInt32(in_stream, &size);

  typedef struct {
    uint32_t header;
    uint32_t offset;
    uint32_t size;
  } tx_pckt_memrdx_t;

  tx_pckt_memrdx_t pckt;
  pckt.header = tx_hdr_memrd;
  pckt.offset = offset;
  pckt.size = size;
  chMtxLock(&mtxTransmit);
  msg_t msg = BulkUsbTransmit((const unsigned char* )(&pckt), sizeof(pckt));
  if (msg==MSG_OK) {
    msg = BulkUsbTransmitPacket((const unsigned char* )(offset), size);
  }
  chMtxUnlock(&mtxTransmit);
  return msg;
}

static msg_t rcv_patch_stop(in_stream_t *in_stream) {
  // TODO: reply success/fail
  if (in_stream->remaining) {
    patch_t *patch;
    msg_t msg = getPatch(in_stream, &patch);
    if (msg!=MSG_OK) return msg;
    patch_stop(patch);
  } else {
    patch_stop(0);
  }
  return MSG_OK;
}

static msg_t rcv_patch_start(in_stream_t *in_stream) {
  if (*in_stream->data == 0) {
    // load patch from index
    uint32_t patch_index;
    msg_t msg = getUInt32(in_stream, &patch_index);
    if (msg!=MSG_OK) return msg;
    patch_t * patch = patch_loadIndex(patch_index,0);
    return tx_reply_ptr(patch);
  } else {
    char patch_name[MAX_FNAME_LENGTH];
    getCString(in_stream, patch_name, sizeof(patch_name));
    patch_t * patch = patch_load(patch_name,0);
    return tx_reply_ptr(patch);
  }
}

static msg_t rcv_mem_write(in_stream_t *in_stream) {
  uint32_t offset1;
  uint32_t size;
  msg_t msg;
  msg = getUInt32(in_stream, &offset1);
  msg = getUInt32(in_stream, &size);

  int rem_length = size;
  uint8_t * offset = (uint8_t * )offset1;
#if 0
  if (in_stream->remaining>0) {
    int s = in_stream->remaining;
    if (s>rem_length) s = rem_length;
    int i;
    for(i=0;i<s;i++) {
      *offset++ = *in_stream->data++;
    }
    rem_length -= s;
  }
#endif
  while (rem_length>0) {
    msg = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
            (uint8_t *)offset, rem_length);
    if (msg<0) break;
    rem_length -= msg;
    offset +=msg;
  }
// TODO: reply
//  return tx_ack();
  return msg;
}

static msg_t rcv_mem_alloc(in_stream_t *in_stream) {
  uint32_t size, typeflags, alignment;
  msg_t msg;
  msg = getUInt32(in_stream, &size);
  msg = getUInt32(in_stream, &typeflags);
  msg = getUInt32(in_stream, &alignment);
  void * r = ax_malloc_align(size, typeflags, alignment);
  msg = tx_reply_ptr(r);
  return msg;
}

static msg_t rcv_mem_free(in_stream_t *in_stream) {
  uint32_t ptr;
  msg_t msg;
  msg = getUInt32(in_stream, &ptr);
  ax_free((void *)ptr);
  return msg;
}

static int CopyPatchToFlash(uint32_t pdest, uint32_t psrc, uint32_t psize) {
  if (pdest == FLASH_BASE_ADDR) {
    uint32_t ccrc = CalcCRC32((uint8_t *)(4 + psrc), psize - 4);
    if (ccrc != *(int *)psrc) {
      LogTextMessage("verify crc 0x%08X 0x%08X",ccrc, *(int *)psrc);
      chThdSleep(100); // get the message through
      return -3;
    }
    psrc += 4;
    codec_clearbuffer();
    return flash_write(pdest, psrc, psize);
  } else if (pdest == FLASH_PATCH_ADDR) {
    // no CRC check
    codec_clearbuffer();
    return flash_write(pdest, psrc, psize);
  }
  LogTextMessage("flash to addr 0x%08X not implemented",pdest);
  return -1;
}

static msg_t rcv_mem_write_flash(in_stream_t *in_stream) {
  uint32_t pdest;
  uint32_t psrc;
  uint32_t size;
  msg_t msg;
  msg = getUInt32(in_stream, &pdest);
  msg = getUInt32(in_stream, &psrc);
  msg = getUInt32(in_stream, &size);
  int err = CopyPatchToFlash(pdest, psrc, size);
  if (err) {
    LogTextMessage("Flashing failed!");
    msg = tx_reply_ptr((void *)err);
  } else {
    LogTextMessage("Flashing done.",pdest);
    msg = tx_reply_ptr(0);
  }
  return msg;
}

static msg_t rcv_patch_paramchange(in_stream_t *in_stream) {
  patch_t *patch;
  uint32_t index;
  int32_t value;
  msg_t msg;
  msg = getPatch(in_stream, &patch);
  msg = getUInt32(in_stream, &index);
  msg = getInt32(in_stream, &value);
  patch_changeParam(patch, index, value, 0xFFFFFFEE);
  return MSG_OK;
}

static msg_t rcv_f_open(in_stream_t *in_stream) {
  if (file_is_open) {
    return tx_fresult(FR_TOO_MANY_OPEN_FILES, 0);
  } else {
    char fname[MAX_FNAME_LENGTH];
    getCString(in_stream, fname, sizeof(fname));
    FRESULT r = f_open(&file, fname, FA_READ|FA_OPEN_EXISTING);
    if (r == FR_OK) {
      file_is_open = 1;
    }
    return tx_fresult(r, fileRef);
  }
}

static msg_t rcv_midi(in_stream_t *in_stream) {
  midi_message_t m;
  msg_t msg = getInt32(in_stream, &m.word);
  midi_input_buffer_put(&midi_input_buffer, m);
  return msg;
}

static msg_t rcv_f_open_write(in_stream_t *in_stream) {
  if (file_is_open) {
    return tx_fresult(FR_TOO_MANY_OPEN_FILES, 0);
  } else {
    char fname[MAX_FNAME_LENGTH];
    getCString(in_stream, fname, sizeof(fname));
    FRESULT r = f_open(&file, fname, FA_WRITE|FA_CREATE_ALWAYS);
    if (r == FR_OK) {
      file_is_open = 1;
    }
    return tx_fresult(r, fileRef);
  }
}

static msg_t rcv_f_close(in_stream_t *in_stream) {
  int32_t file_ref;
  msg_t msg = getInt32(in_stream, &file_ref);
  if ((msg != MSG_OK) || !file_is_open || (file_ref != fileRef)) {
    return tx_fresult(FR_INVALID_PARAMETER, 0);
  } else {
    FRESULT r = f_close(&file);
    file_is_open = 0;
    return tx_fresult(r, 0);
  }
}

static msg_t rcv_f_seek(in_stream_t *in_stream) {
  int32_t file_ref;
  uint32_t pos;
  msg_t msg;
  msg = getInt32(in_stream, &file_ref);
  if (msg != MSG_OK) goto err;
  msg = getUInt32(in_stream, &pos);
  if (msg != MSG_OK) goto err;
  if (!file_is_open || (file_ref != fileRef)) goto err;
  FRESULT r = f_lseek(&file, pos);
  return tx_fresult(r, 0);

err:
  return tx_fresult(FR_INVALID_PARAMETER, 0);
}

static msg_t rcv_f_read(in_stream_t *in_stream) {
  int32_t file_ref;
  uint32_t length;
  msg_t msg;
  msg = getInt32(in_stream, &file_ref);
  msg = getUInt32(in_stream, &length);
  struct {
    uint32_t header;
    int32_t err;
    uint32_t bytes_read;
  } tx_pckt_fs_read;

  tx_pckt_fs_read.header = tx_hdr_f_read;

  chMtxLock(&mtxTransmit);

  if (!file_is_open || (file_ref != fileRef) || (length > sizeof(fbuff))) {
    msg_t msg;
    tx_pckt_fs_read.bytes_read = 0;
    tx_pckt_fs_read.err = FR_INVALID_PARAMETER;
    msg = BulkUsbTransmit((const unsigned char* )(&tx_pckt_fs_read), sizeof(tx_pckt_fs_read));
    handleUsbErr(msg);
    msg = BulkUsbTransmit(0, 0); // ZLP
    handleUsbErr(msg);
  } else {
    char *buf = (char *)fbuff;
    size_t bytes_read;
    FRESULT err = f_read(&file, buf, length,
                               &bytes_read);
    tx_pckt_fs_read.bytes_read = bytes_read;
    tx_pckt_fs_read.err = err;
    msg_t msg;
    msg = BulkUsbTransmit((const unsigned char* )(&tx_pckt_fs_read), sizeof(tx_pckt_fs_read));
    handleUsbErr(msg);
    msg = BulkUsbTransmitPacket((uint8_t *)fbuff, bytes_read);
    handleUsbErr(msg);
  }
  chMtxUnlock(&mtxTransmit);
  return MSG_OK;
}

static msg_t rcv_f_write(in_stream_t *in_stream) {
  int32_t file_ref;
  uint32_t fsize;
  msg_t msg;
  msg = getInt32(in_stream, &file_ref);
  msg = getUInt32(in_stream, &fsize);

  if (fsize > sizeof(fbuff)) {
    chSysHalt("f_write too long");
  }

  if (!file_is_open || (file_ref != fileRef)) {
    return tx_fresult(FR_INVALID_PARAMETER, 0);
  }

  unsigned char *pos = (unsigned char *)fbuff;
  int i;
  int l1 = in_stream->remaining;
  for (i=0;i<l1;i++) {
    *pos++ = *in_stream->data++;
  }
  in_stream->remaining = 0;

  int rem_length = fsize - l1;
  while (rem_length > 0) {
    msg_t rxbuf_length = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
            pos, rem_length);
    if (rxbuf_length<0) {
      break;
    }
    pos += rxbuf_length;
    rem_length -= rxbuf_length;
  }
  size_t bytes_written;
  FRESULT err = f_write(&file, (char *)fbuff, fsize, &bytes_written);
  return tx_fresult(err, 0);
}

static msg_t rcv_f_dirlist(in_stream_t *in_stream) {
  char path[MAX_FNAME_LENGTH];
  getCString(in_stream, path, sizeof(path));
  FATFS *fsp = 0;
  uint32_t clusters = 0;
  FRESULT err = 0;
  err = f_getfree("/", &clusters, &fsp);
  /*
   chprintf(chp,
   "FS: %lu free clusters, %lu sectors per cluster, %lu bytes free\r\n",
   clusters, (uint32_t)SDC_FS.csize,
   clusters * (uint32_t)SDC_FS.csize * (uint32_t)MMCSD_BLOCK_SIZE);
   */
  chMtxLock(&mtxTransmit);
  struct {
    int32_t header;
    int32_t clusters;
    int32_t clusterSize;
    int32_t blockSize;
  } tx_pckt_dirlist;
  tx_pckt_dirlist.header = tx_hdr_f_dir;
  tx_pckt_dirlist.clusters = clusters;
  tx_pckt_dirlist.blockSize = MMCSD_BLOCK_SIZE;
  if (fsp) tx_pckt_dirlist.clusterSize = fsp->csize;
  msg_t msg = BulkUsbTransmit((unsigned char* )&tx_pckt_dirlist, sizeof(tx_pckt_dirlist));
  handleUsbErr(msg);
  if (err == FR_OK) {
    DIR dir;
    FILINFO fno;

    err = f_opendir(&dir, path);
    if (err == FR_OK) {
      for (;;) {
        err = f_readdir(&dir, &fno);
        if (err != FR_OK || fno.fname[0] == 0)
          break;
        if (fno.fname[0] == '.')
          continue;
#if _USE_LFN
        const char *fn = *fno.lfname ? fno.lfname : fno.fname;
#else
        const char *fn = fno.fname;
#endif
        if (fn[0] == '.')
          continue;
        if (fno.fattrib & AM_HID)
          continue;
        if (fno.fattrib & AM_DIR) {
          tx_fileinfo(&fno, 0);
        } else {
          tx_fileinfo(&fno, 0);
        }
      }
    } else {
      //report_fatfs_error(res,0);
    }
  }
  struct {
    int32_t header;
    int32_t err;
  } tx_pckt_dirlist_end;
  tx_pckt_dirlist_end.header = tx_hdr_f_dir_end;
  tx_pckt_dirlist_end.err = err;
  msg = BulkUsbTransmit((unsigned char* )&tx_pckt_dirlist_end, sizeof(tx_pckt_dirlist_end));
  chMtxUnlock(&mtxTransmit);
  return msg;
}

static msg_t rcv_f_getinfo(in_stream_t *in_stream) {
  FILINFO fno;
  char fname[MAX_FNAME_LENGTH];
  getCString(in_stream, fname, sizeof(fname));
  FRESULT err = f_stat(fname, &fno);
  chMtxLock(&mtxTransmit);
  msg_t msg = tx_fileinfo(&fno, err);
  chMtxUnlock(&mtxTransmit);
  return msg;
}

static msg_t rcv_f_setinfo(in_stream_t *in_stream) {
  uint16_t date;
  uint16_t time;
  msg_t msg;
  msg = getUInt16(in_stream, &date);
  msg = getUInt16(in_stream, &time);
  char fname[MAX_FNAME_LENGTH];
  getCString(in_stream, fname, sizeof(fname));
  FILINFO fno;
  fno.fdate = date;
  fno.ftime = time;
  FRESULT err = f_utime(fname,&fno);
  return tx_fresult(err,0);
}

static msg_t rcv_f_delete(in_stream_t *in_stream) {
  char fname[MAX_FNAME_LENGTH];
  getCString(in_stream, fname, sizeof(fname));
  FRESULT err = f_unlink(fname);
  return tx_fresult(err,0);
}

static msg_t rcv_f_mkdir(in_stream_t *in_stream) {
  char fname[MAX_FNAME_LENGTH];
  getCString(in_stream, fname, sizeof(fname));
  FRESULT err = f_mkdir(fname);
  return tx_fresult(err,0);
}

static msg_t rcv_activate_dfu(in_stream_t *in_stream) {
  (void)in_stream;
  patch_stop(0);
  exception_initiate_dfu();
  return MSG_OK;
}

static msg_t rcv_patch_preset_apply(in_stream_t *in_stream) {
  patch_t * patch;
  int32_t index;
  msg_t msg;
  msg = getPatch(in_stream, &patch);
  msg = getInt32(in_stream, &index);
  patch_applyPreset(patch, index);
  handleUsbErr(tx_patch_paramchange());
  return msg;
}

static msg_t rcv_patch_preset_write(in_stream_t *in_stream) {
  patch_t * patch;
  int32_t size;
  msg_t msg;
  msg = getPatch(in_stream, &patch);
  msg = getInt32(in_stream, &size);
  uint8_t * pdata = patch_getPresetData(patch);
  if (pdata) {
    // TODO: preset size check
    msg = getByteArray(in_stream, pdata, size);
  }
  return msg;
}

static msg_t rcv_patch_get_name(in_stream_t *in_stream) {
  patch_t * patch;
  msg_t msg;
  msg = getPatch(in_stream, &patch);
  return tx_cstring(patch_getName(patch));
}

static msg_t rcv_patch_get_error(in_stream_t *in_stream) {
  return tx_cstring(patch_getError());
}

static msg_t rcv_patch_get_list(in_stream_t *in_stream) {
  return tx_patchList();
}

static msg_t rcv_virtual_input_event(in_stream_t *in_stream) {
  input_event input_evt;
  msg_t msg = getInt32(in_stream, &input_evt.word);
  if (msg == MSG_OK) {
    chSysLock();
    queueInputEventI(input_evt);
    chSysUnlock();
  }
  return msg;
}

static msg_t rcv_extra(in_stream_t *in_stream) {
  int32_t arg;
  msg_t msg = getInt32(in_stream, &arg);
  switch(arg) {
  case 0: {
    extern void list_all_symbols(void);
    list_all_symbols();
  } break;
  case 1: {
    extern void dbg_dump_dlopen(void);
    dbg_dump_dlopen();
  } break;
  case 2: {
    extern void dbg_dl_test(void);
    dbg_dl_test();
  } break;
  case 3: {
    extern void dbg_dump_threads(void);
    dbg_dump_threads();
  } break;
  default:
    log_printf("unknown arg %d\n", arg);
  }
  return MSG_OK;
}

#define rcv_hdr_ping                 0x706f7841 // "Axop"
#define rcv_hdr_getfwid              0x566f7841 // "AxoV"

#define rcv_hdr_mem_read             0x724d7841 // "AxMr"
#define rcv_hdr_mem_write            0x774d7841 // "AxMw"
#define rcv_hdr_mem_alloc            0x614d7841 // "AxMa"
#define rcv_hdr_mem_free             0x664d7841 // "AxMf"
#define rcv_hdr_mem_write_flash      0x464d7841 // "AxMF"

#define rcv_hdr_patch_stop           0x53507841 // "AxPS"
#define rcv_hdr_patch_start          0x73507841 // "AxPs"
#define rcv_hdr_patch_paramchange    0x70507841 // "AxPp"
#define rcv_hdr_patch_get_disp       0x64507841 // "AxPd"
#define rcv_hdr_patch_preset_apply   0x54507841 // "AxPT"
#define rcv_hdr_patch_preset_write   0x52507841 // "AxPR"
#define rcv_hdr_patch_get_name       0x6E507841 // "AxPn"
#define rcv_hdr_patch_get_list       0x6C507841 // "AxPl"
#define rcv_hdr_patch_get_error      0x65507841 // "AxPe"

#define rcv_hdr_virtual_input_event  0x426f7841 // "AxoB"
#define rcv_hdr_midi                 0x4D6f7841 // "AxoM"
#define rcv_hdr_activate_dfu         0x446f7841 // "AxoD"
#define rcv_hdr_extra                0x586f7841 // "AxoX" temporary messages, for development purposes only!

#define rcv_hdr_f_open               0x6f467841 // "AxFo"
#define rcv_hdr_f_open_write         0x4f467841 // "AxFO"
#define rcv_hdr_f_close              0x63467841 // "AxFc"
#define rcv_hdr_f_seek               0x73467841 // "AxFs"
#define rcv_hdr_f_read               0x72467841 // "AxFr"
#define rcv_hdr_f_write              0x77467841 // "AxFw"
#define rcv_hdr_f_dirlist            0x64467841 // "AxFd"
#define rcv_hdr_f_getinfo            0x69467841 // "AxFi"
#define rcv_hdr_f_setinfo            0x49467841 // "AxFI"
#define rcv_hdr_f_delete             0x52467841 // "AxFR"
#define rcv_hdr_f_mkdir              0x6D467841 // "AxFm"


static msg_t dispatch_rcv_header(in_stream_t *in_stream) {
  uint32_t header;
  msg_t msg = getUInt32(in_stream, &header);
  if (msg != MSG_OK) return msg;

  switch (header) {
    case rcv_hdr_ping:
      return rcv_ping(in_stream);
    case rcv_hdr_patch_get_disp:
      return rcv_patch_get_disp(in_stream);
    case rcv_hdr_patch_paramchange:
      return rcv_patch_paramchange(in_stream);
    case rcv_hdr_midi:
      return rcv_midi(in_stream);
    case rcv_hdr_mem_read:
      return rcv_mem_read(in_stream);
    case rcv_hdr_mem_write:
      return rcv_mem_write(in_stream);
    case rcv_hdr_mem_alloc:
      return rcv_mem_alloc(in_stream);
    case rcv_hdr_mem_free:
      return rcv_mem_free(in_stream);
    case rcv_hdr_mem_write_flash:
      return rcv_mem_write_flash(in_stream);
    case rcv_hdr_patch_stop:
      return rcv_patch_stop(in_stream);
    case rcv_hdr_patch_start:
      return rcv_patch_start(in_stream);
    case rcv_hdr_patch_preset_apply:
      return rcv_patch_preset_apply(in_stream);
    case rcv_hdr_patch_preset_write:
      return rcv_patch_preset_write(in_stream);
    case rcv_hdr_patch_get_name:
      return rcv_patch_get_name(in_stream);
    case rcv_hdr_patch_get_list:
      return rcv_patch_get_list(in_stream);
    case rcv_hdr_patch_get_error:
      return rcv_patch_get_error(in_stream);
    case rcv_hdr_virtual_input_event:
      return rcv_virtual_input_event(in_stream);
    case rcv_hdr_f_open:
      return rcv_f_open(in_stream);
    case rcv_hdr_f_open_write:
      return rcv_f_open_write(in_stream);
    case rcv_hdr_f_close:
      return rcv_f_close(in_stream);
    case rcv_hdr_f_seek:
      return rcv_f_seek(in_stream);
    case rcv_hdr_f_read:
      return rcv_f_read(in_stream);
    case rcv_hdr_f_write:
      return rcv_f_write(in_stream);
    case rcv_hdr_f_dirlist:
      return rcv_f_dirlist(in_stream);
    case rcv_hdr_f_getinfo:
      return rcv_f_getinfo(in_stream);
    case rcv_hdr_f_setinfo:
      return rcv_f_setinfo(in_stream);
    case rcv_hdr_f_delete:
      return rcv_f_delete(in_stream);
    case rcv_hdr_f_mkdir:
      return rcv_f_mkdir(in_stream);
    case rcv_hdr_getfwid:
      return rcv_getfwid(in_stream);
    case rcv_hdr_activate_dfu:
      return rcv_activate_dfu(in_stream);
    case rcv_hdr_extra:
      return rcv_extra(in_stream);
    default:
      // TODO: find cause of unidentified header
      LogTextMessage("unidentified header : 0x%08X", header);
      return MSG_OK;
  }
}

/*
 * USB reader thread
 */
static THD_WORKING_AREA(waBulkReader, 2048);
static THD_FUNCTION(BulkReader, arg) {
  (void)arg;
  chRegSetThreadName("bulkrdr");
  static uint8_t bulk_rxbuf[64];
  while (true) {
    msg_t msg = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
    		bulk_rxbuf, sizeof (bulk_rxbuf));
    if (msg == MSG_RESET) {
      log_suspend();
      chThdSleepMilliseconds(50);
    } else {
      in_stream_t in_stream;
      in_stream.buf = bulk_rxbuf;
      in_stream.data = bulk_rxbuf;
      in_stream.remaining = msg;
      dispatch_rcv_header(&in_stream);
    }
  }
}

void InitPConnection(void) {
  chMtxObjectInit(&mtxTransmit);
  extern int32_t _flash_end;
  fwid = CalcCRC32((uint8_t *)(FLASH_BASE_ADDR),
                   (uint32_t)(&_flash_end) & 0x07FFFFF);
  logObjectInit(&logstream);

  /*
   * Activates the USB driver and then the USB bus pull-up on D+.
   * Note, a delay is inserted in order to not have to disconnect the cable
   * after a reset.
   */
  usbDisconnectBus(&USBD1);
  chThdSleepMilliseconds(1000);
  usbStart(&USBD1, &usbcfg);
  usbConnectBus(&USBD1);

  thd_bulk_Reader =  chThdCreateStatic(waBulkReader, sizeof(waBulkReader), NORMALPRIO, BulkReader, NULL);
  thd_log_consumer =  chThdCreateStatic(waLogConsumer, sizeof(waLogConsumer), NORMALPRIO, LogConsumer, NULL);
}

void log_vprintf(const char * format, va_list arg ) {
  chvprintf((BaseSequentialStream *)&logstream, format, arg);
}
