/**
 * Copyright (C) 2013 - 2017 Johannes Taelman
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
#include "watchdog.h"
#include "bulk_usb.h"
#include "midi.h"
#include "midi_usb.h"
#include "watchdog.h"
#include "sysmon.h"
#include "firmware_chunks.h"
#include "axoloti_math.h"
#include "sdram.h"

//#define DEBUG_SERIAL 1

static uint32_t fwid;

static thread_t * thd_bulk_Writer;
static thread_t * thd_bulk_Reader;

static int isConnected = 0;

static char FileName[256];
static FIL pFile;
static int pFileSize;
static FILINFO fno;

static void CloseFile(void) {
  FRESULT err;
  err = f_close(&pFile);
  if (err != FR_OK) {
    report_fatfs_error(err,&FileName[0]);
  }
  if (!FileName[0]) {
    // and set timestamp
    FILINFO fno;
    fno.fdate = FileName[2] + (FileName[3]<<8);
    fno.ftime = FileName[4] + (FileName[5]<<8);
    err = f_utime(&FileName[6],&fno);
    if (err != FR_OK) {
      report_fatfs_error(err,&FileName[6]);
    }
  }
}

static uint8_t bulk_rxbuf[64];

#define BulkUsbTransmit(data,size) usbTransmit(&USBD1, USBD2_DATA_REQUEST_EP, data, size);

static msg_t BulkUsbTransmitPacket(const uint8_t * data, size_t size) {
	msg_t res;
	res = usbTransmit(&USBD1, USBD2_DATA_REQUEST_EP, data, size);
	if (res != MSG_OK) return res;
	if ((size & 0x3F) == 0) {
		// multiple of 64 bytes, append zero-length packet
		res = usbTransmit(&USBD1, USBD2_DATA_REQUEST_EP, data, 0);
	}
	return res;
}

static uint32_t offset;
static uint32_t value;

// in order of high to low priority
#define evt_bulk_tx_ack  (1<<0)
#define evt_bulk_fw_ver  (1<<1)
#define evt_bulk_memrd32 (1<<2)
#define evt_bulk_memrdx  (1<<3)
#define evt_bulk_tx_fileinfo (1<<5)
#define evt_bulk_tx_logmessage (1<<6)
#define evt_bulk_tx_dirlist (1<<7)
#define evt_bulk_tx_paramchange (1<<8)

#define tx_hdr_acknowledge 0x416F7841   // "AxoA"
#define tx_hdr_fwid        0x566f7841   // "AxoV"
#define tx_hdr_log         0x546F7841   // "AxoT"
#define tx_hdr_memrd32     0x796f7841   // "Axoy"
#define tx_hdr_memrdx      0x726f7841   // "Axor"
#define tx_hdr_paramchange 0x516F7841   // "AxoQ"
#define tx_hdr_fileinfo    0x666F7841   // "Axof"

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
		.underruns = 0
};

static msg_t bulk_tx_ack(void) {
	tx_pckt_ack_v2.dspload = dspLoadPct;
	tx_pckt_ack_v2.patchID = patchMeta.patchID;
	tx_pckt_ack_v2.voltage = sysmon_getVoltage10() + (sysmon_getVoltage50() << 16);
	if (patchStatus) {
		tx_pckt_ack_v2.loadPatchIndex = UNINITIALIZED;
	} else {
		tx_pckt_ack_v2.loadPatchIndex = loadPatchIndex;
	}
	tx_pckt_ack_v2.fs_ready = fs_ready;
	return BulkUsbTransmit((const unsigned char* )&tx_pckt_ack_v2, sizeof(tx_pckt_ack_v2));
}

typedef struct {
	uint32_t header;
	uint8_t version[4];
	uint8_t fwid[4];
	uint8_t patch_mainloc[4];
} tx_pckt_fwversion_t;

static msg_t bulk_tx_fw_version(void) {
	tx_pckt_fwversion_t pckt;
    pckt.header = tx_hdr_fwid;
	pckt.version[0] = FWVERSION1; // major
	pckt.version[1] = FWVERSION2; // minor
	pckt.version[2] = FWVERSION3;
	pckt.version[3] = FWVERSION4;
	uint32_t fwid = GetFirmwareID();
	pckt.fwid[0] = (uint8_t) (fwid >> 24);
	pckt.fwid[1] = (uint8_t) (fwid >> 16);
	pckt.fwid[2] = (uint8_t) (fwid >> 8);
	pckt.fwid[3] = (uint8_t) (fwid);
	uint32_t chunk_addr = (uint32_t)chunk_fw_root_data;
	pckt.patch_mainloc[0] = (uint8_t) (chunk_addr >> 24);
	pckt.patch_mainloc[1] = (uint8_t) (chunk_addr >> 16);
	pckt.patch_mainloc[2] = (uint8_t) (chunk_addr >> 8);
	pckt.patch_mainloc[3] = (uint8_t) (chunk_addr);
	return BulkUsbTransmit((const unsigned char* )(&pckt), sizeof(pckt));
}

typedef struct {
	uint32_t header;
	uint32_t offset;
	uint32_t value;
} tx_pckt_memrd32_t;

static msg_t bulk_tx_memrd32(void) {
    tx_pckt_memrd32_t pckt;
    pckt.header = tx_hdr_memrd32;
    pckt.offset = offset;
    pckt.value = *((uint32_t*)offset);;
    return BulkUsbTransmit((const unsigned char* )(&pckt), sizeof(pckt));
}

typedef struct {
	uint32_t header;
	uint32_t offset;
	uint32_t size;
} tx_pckt_memrdx_t;

static msg_t bulk_tx_memrdx(void) {
    tx_pckt_memrdx_t pckt;
    pckt.header = tx_hdr_memrdx;
    pckt.offset = offset;
    pckt.size = value;
    msg_t m = BulkUsbTransmit((const unsigned char* )(&pckt), sizeof(pckt));
    if (m!=MSG_OK) return m;
    m = BulkUsbTransmitPacket((const unsigned char* )(offset), value);
    return m;
}

static msg_t bulk_tx_fileinfo(void) {
    char *msg = &((char*)fbuff)[0];
    *((int32_t*)fbuff) = tx_hdr_fileinfo;
    *(int32_t *)(&msg[4]) = fno.fsize;
    *(int32_t *)(&msg[8]) = fno.fdate + (fno.ftime<<16);
    strcpy(&msg[12], &FileName[6]);
    int l = strlen(&msg[12]) + 13;
    return BulkUsbTransmitPacket((const unsigned char* )msg, l);
}

static FRESULT scan_files(char *path) {
  FRESULT res;
  FILINFO fno;
  DIR dir;
  int i;
  char *fn;
  char *msg = &((char*)fbuff)[64];
  fno.lfname = &FileName[0];
  fno.lfsize = sizeof(FileName);
  res = f_opendir(&dir, path);
  if (res == FR_OK) {
    i = strlen(path);
    for (;;) {
      res = f_readdir(&dir, &fno);
      if (res != FR_OK || fno.fname[0] == 0)
        break;
      if (fno.fname[0] == '.')
        continue;
#if _USE_LFN
      fn = *fno.lfname ? fno.lfname : fno.fname;
#else
      fn = fno.fname;
#endif
      if (fn[0] == '.')
        continue;
      if (fno.fattrib & AM_HID)
        continue;
      if (fno.fattrib & AM_DIR) {
        path[i] = '/';
        strcpy(&path[i+1], fn);
        msg[0] = 'A';
        msg[1] = 'x';
        msg[2] = 'o';
        msg[3] = 'f';
        *(int32_t *)(&msg[4]) = fno.fsize;
        *(int32_t *)(&msg[8]) = fno.fdate + (fno.ftime<<16);
        strcpy(&msg[12], &path[1]);
        int l = strlen(&msg[12]);
        msg[12+l] = '/';
        msg[13+l] = 0;
        BulkUsbTransmitPacket((const unsigned char* )msg, l+14);
        res = scan_files(path);
        path[i] = 0;
        if (res != FR_OK) break;
      } else {
        msg[0] = 'A';
        msg[1] = 'x';
        msg[2] = 'o';
        msg[3] = 'f';
        *(int32_t *)(&msg[4]) = fno.fsize;
        *(int32_t *)(&msg[8]) = fno.fdate + (fno.ftime<<16);
        strcpy(&msg[12], &path[1]);
        msg[12+i-1] = '/';
        strcpy(&msg[12+i], fn);
        int l = strlen(&msg[12]);
        BulkUsbTransmitPacket((const unsigned char* )msg, l+13);
      }
    }
  } else {
	  report_fatfs_error(res,0);
  }
  return res;
}

static void bulk_tx_dirlist(void) {
  FATFS *fsp;
  uint32_t clusters;
  FRESULT err;

  err = f_getfree("/", &clusters, &fsp);
  if (err != FR_OK) {
	report_fatfs_error(err,0);
    return;
  }
  /*
   chprintf(chp,
   "FS: %lu free clusters, %lu sectors per cluster, %lu bytes free\r\n",
   clusters, (uint32_t)SDC_FS.csize,
   clusters * (uint32_t)SDC_FS.csize * (uint32_t)MMCSD_BLOCK_SIZE);
   */
  ((char*)fbuff)[0] = 'A';
  ((char*)fbuff)[1] = 'x';
  ((char*)fbuff)[2] = 'o';
  ((char*)fbuff)[3] = 'd';
  fbuff[1] = clusters;
  fbuff[2] = fsp->csize;
  fbuff[3] = MMCSD_BLOCK_SIZE;
  BulkUsbTransmit((const unsigned char* )(&fbuff[0]), 16);
  chThdSleepMilliseconds(10);
  fbuff[0] = '/';
  fbuff[1] = 0;
  scan_files((char *)&fbuff[0]);

  char *msg = &((char*)fbuff)[64];
  msg[0] = 'A';
  msg[1] = 'x';
  msg[2] = 'o';
  msg[3] = 'f';
  *(int32_t *)(&msg[4]) = 0;
  *(int32_t *)(&msg[8]) = 0;
  msg[12] = '/';
  msg[13] = 0;
  BulkUsbTransmit((const unsigned char* )msg, 14);
}


typedef struct bulk_tx_logmessage_pckt {
	int header;
	// + null terminated string
} bulk_tx_logmessage_pckt_t;

static binary_semaphore_t logsem;

#define LOG_BUFFERS_NUMBER 3
#define LOG_BUFFERS_SIZE 64

/*
 * LogStream specific data.
 */
#define _log_stream_data                                                    \
  _base_sequential_stream_data                                              \
  uint8_t ob[BQ_BUFFER_SIZE(LOG_BUFFERS_NUMBER, LOG_BUFFERS_SIZE)];         \
  output_buffers_queue_t obqueue;

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
	msg_t r = obqWriteTimeout(&msp->obqueue, bp,
						 n, TIME_INFINITE);
	(void)r;
	return n;
}

static size_t reads(void *ip, uint8_t *bp, size_t n){
	return 0;
}

static msg_t put(void *ip, uint8_t b) {
	LogStream *msp = ip;
	return obqPutTimeout(&msp->obqueue, b, TIME_INFINITE);
}

static msg_t get(void *ip) {
	return 0;
}

static const struct LogStreamVMT vmt = {writes, reads, put, get};

LogStream logstream;

/**
 * @brief   Notification of filled buffer inserted into the output buffers queue.
 *
 * @param[in] bqp       the buffers queue pointer.
 */
static void obnotify(io_buffers_queue_t *bqp) {
    chEvtSignalI(thd_bulk_Writer,evt_bulk_tx_logmessage);
}

static void logObjectInit(LogStream *msp) {
  msp->vmt    = &vmt;
  obqObjectInit(&msp->obqueue, msp->ob,
                LOG_BUFFERS_SIZE, LOG_BUFFERS_NUMBER,
                obnotify, msp);
}

static msg_t bulk_tx_logmessage(void) {
  msg_t error = 0;
  size_t s;
  int cont = 1;
  int h = tx_hdr_log; // "AxoT"
  while(cont) {
	  chSysLock();
	  uint8_t *buf = obqGetFullBufferI(&logstream.obqueue,
								 &s);
	  chSchRescheduleS();
	  chSysUnlock();

	  if (buf) {
		  error = BulkUsbTransmit((const unsigned char* )&h, 4);
		  error = BulkUsbTransmitPacket(buf, s);
		  chSysLock();
		  obqReleaseEmptyBufferI(&logstream.obqueue);
		  chSchRescheduleS();
		  chSysUnlock();
	  } else {
		  cont = 0;
#if 0 // diagnostics
		  if (s==0) {
			  error = BulkUsbTransmit((const unsigned char* )&h, 4);
			  error = BulkUsbTransmit((const unsigned char* )"emptyb", 7);
		  } else {
			  error = BulkUsbTransmit((const unsigned char* )&h, 4);
			  error = BulkUsbTransmit((const unsigned char* )"nobuf", 6);
		  }
#endif
	  }
  }
  chBSemSignal(&logsem);
  return error;
}

typedef struct {
  int32_t header;
  uint32_t patchID;
  int32_t value;
  int32_t index;
} tx_pckt_paramchange;

static msg_t bulk_tx_paramchange(void) {
	msg_t r = 0;
	if (!patchStatus) {
		unsigned int i;
		for (i = 0; i < patchMeta.nparams; i++) {
			if (patchMeta.params[i].signals & 0x01) {
				int v = (patchMeta.params)[i].d.frac.value;  // FIXME: can't assume parameter type is t_frac
				patchMeta.params[i].signals &= ~0x01;
				tx_pckt_paramchange pch;
				pch.header = tx_hdr_paramchange; //"AxoQ"
				pch.patchID = patchMeta.patchID;
				pch.value = v;
				pch.index = i;
				r = BulkUsbTransmit((const unsigned char* )&pch, sizeof(pch));
				if (r<0) break;
			}
		}
	}
	return r;
}

static THD_WORKING_AREA(waBulkWriter, 1536);
static THD_FUNCTION(BulkWriter, arg) {

	(void) arg;
	chRegSetThreadName("bulksend");
	while (true) {
		eventmask_t evt = chEvtWaitOne(0xFFFFFFFF);
		msg_t msg=0;
		switch (evt) {
		case evt_bulk_tx_ack:
			msg = bulk_tx_ack();
			exception_checkandreport();
			break;
		case evt_bulk_fw_ver:
			msg = bulk_tx_fw_version();
			break;
		case evt_bulk_memrd32:
			msg = bulk_tx_memrd32();
			break;
		case evt_bulk_memrdx:
			msg = bulk_tx_memrdx();
			break;
		case evt_bulk_tx_fileinfo:
			msg = bulk_tx_fileinfo();
			break;
		case evt_bulk_tx_logmessage:
			msg = bulk_tx_logmessage();
			break;
		case evt_bulk_tx_dirlist:
			bulk_tx_dirlist();
			break;
		case evt_bulk_tx_paramchange:
			msg = bulk_tx_paramchange();
			break;
		default:
			;
		}
		if (msg == MSG_RESET)
			chThdSleepMilliseconds(500);
	}
}

typedef struct {
   uint32_t header;
} rcv_pckt_header_t;

typedef struct {
   uint32_t header;
   uint32_t offset;
} rcv_pckt_memrd32_t;

typedef struct rcv_pckt_offset_value {
   uint32_t header;
   uint32_t offset;
   uint32_t size;
   // uint8_t data[size];
} rcv_pckt_memrdx_t;

typedef struct rcv_pckt_offset_value rcv_pckt_memwrx_t;

typedef struct {
   uint32_t header;
   uint32_t patch_id;
   int32_t value;
   uint16_t index;
} rcv_pckt_paramchange_t;

typedef struct {
   uint32_t header;
   uint32_t midiword;
} rcv_pckt_midi_t;

typedef struct {
   uint32_t header;
   uint32_t fsize;
   char fn[58];
} rcv_pckt_fs_create_t;

typedef struct {
   uint32_t header;
   uint32_t fsize;
} rcv_pckt_fs_append_t;

typedef struct {
   uint32_t header;
   uint8_t index;
} rcv_pckt_preset_apply_t;

typedef struct {
   uint32_t header;
   uint32_t size;
   // uint8_t data[size];
} rcv_pckt_preset_write_t;

typedef struct {
	uint32_t header;
	input_event input_event;
} rcv_pckt_virtual_input_event_t;

#define rcv_hdr_ping           0x706f7841 // "Axop"
#define rcv_hdr_getfwid        0x566f7841 // "AxoV"
#define rcv_hdr_memrd32        0x796f7841 // "Axoy"
#define rcv_hdr_memrdx         0x726f7841 // "Axor"
#define rcv_hdr_stop           0x536f7841 // "AxoS"
#define rcv_hdr_start          0x736f7841 // "Axos"
#define rcv_hdr_memwr          0x576f7841 // "AxoW"
#define rcv_hdr_paramchange    0x506f7841 // "AxoP"
#define rcv_hdr_midi           0x4D6f7841 // "AxoM"
#define rcv_hdr_fs_create      0x436f7841 // "AxoC"
#define rcv_hdr_fs_dirlist     0x646f7841 // "Axod"
#define rcv_hdr_copy_to_flash  0x466f7841 // "AxoF"
#define rcv_hdr_activate_dfu   0x446f7841 // "AxoD"
#define rcv_hdr_fs_close       0x636f7841 // "Axoc"
#define rcv_hdr_fs_append      0x416f7841 // "AxoA"
#define rcv_hdr_preset_apply   0x546f7841 // "AxoT"
#define rcv_hdr_preset_write   0x526f7841 // "AxoR"
#define rcv_hdr_virtual_input_event  0x426f7841 // "AxoB"

static void ManipulateFile(void) {
  sdcard_attemptMountIfUnmounted();
  if (FileName[0]) {
    // backwards compatibility
    FRESULT err;
    err = f_open(&pFile, &FileName[0], FA_WRITE | FA_CREATE_ALWAYS);
    if (err != FR_OK) {
      report_fatfs_error(err,&FileName[0]);
    }
    err = f_lseek(&pFile, pFileSize);
    if (err != FR_OK) {
      report_fatfs_error(err,&FileName[0]);
    }
    err = f_lseek(&pFile, 0);
    if (err != FR_OK) {
      report_fatfs_error(err,&FileName[0]);
    }
  } else {
    // filename[0] == 0
    if (FileName[1]=='d') {
      // create directory
      FRESULT err;
      err = f_mkdir(&FileName[6]);
      if ((err != FR_OK) && (err != FR_EXIST)) {
        report_fatfs_error(err,&FileName[6]);
      }
      // and set timestamp
      fno.fdate = FileName[2] + (FileName[3]<<8);
      fno.ftime = FileName[4] + (FileName[5]<<8);
      err = f_utime(&FileName[6],&fno);
      if (err != FR_OK) {
        report_fatfs_error(err,&FileName[6]);
      }
    } else if (FileName[1]=='f') {
      // create file
      FRESULT err;
      err = f_open(&pFile, &FileName[6], FA_WRITE | FA_CREATE_ALWAYS);
      if (err != FR_OK) {
        report_fatfs_error(err,&FileName[6]);
      }
      err = f_lseek(&pFile, pFileSize);
      if (err != FR_OK) {
        report_fatfs_error(err,&FileName[6]);
      }
      err = f_lseek(&pFile, 0);
      if (err != FR_OK) {
        report_fatfs_error(err,&FileName[6]);
      }
    } else if (FileName[1]=='D') {
      // delete
      FRESULT err;
      err = f_unlink(&FileName[6]);
      if (err != FR_OK) {
        report_fatfs_error(err,&FileName[6]);
      }
    } else if (FileName[1]=='C') {
      // change working directory
      FRESULT err;
      err = f_chdir(&FileName[6]);
      if (err != FR_OK) {
        report_fatfs_error(err,&FileName[6]);
      }
    } else if (FileName[1]=='I') {
      // get file info
      FRESULT err;
      FILINFO fno;
      fno.lfname = &((char*)fbuff)[0];
      fno.lfsize = 256;
      err =  f_stat(&FileName[6],&fno);
      if (err == FR_OK) { // condition?
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_fileinfo);
      }
    }
  }
}

static void CopyPatchToFlash(void) {
  flash_unlock();
  flash_Erase_sector(11);
  int src_addr = SDRAM_BANK_ADDR;
  int flash_addr = PATCHFLASHLOC;
  int c;
  for (c = 0; c < PATCHFLASHSIZE;) {
    flash_ProgramWord(flash_addr, *(int32_t *)src_addr);
    src_addr += 4;
    flash_addr += 4;
    c += 4;
  }
  // verify
  src_addr = SDRAM_BANK_ADDR;
  flash_addr = PATCHFLASHLOC;
  int err = 0;
  for (c = 0; c < PATCHFLASHSIZE;) {
    if (*(int32_t *)flash_addr != *(int32_t *)src_addr)
      err++;
    src_addr += 4;
    flash_addr += 4;
    c += 4;
  }
  if (err) {
	  chSysHalt("Flashing failed");
  }
}

/*
 * USB reader thread
 */
static THD_WORKING_AREA(waBulkReader, 1024);
static THD_FUNCTION(BulkReader, arg) {

  (void)arg;
  chRegSetThreadName("bulkrdr");
  while (true) {
    msg_t msg = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
    		bulk_rxbuf, sizeof (bulk_rxbuf));
    if (msg == MSG_RESET) {
      isConnected = 0;
      chThdSleepMilliseconds(500);
    }
    else {
      uint32_t header = ((rcv_pckt_header_t *)bulk_rxbuf)->header;
      if (header == rcv_hdr_ping) {
    	  // AxoP : ping
          isConnected = 1;
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
    	  if (!patchStatus) {
        	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_paramchange);
    	  }
      } else if (header == rcv_hdr_getfwid) {
    	  // AxoV : get firmware version
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_fw_ver);
      } else if (header == rcv_hdr_memrd32) {
    	  // Axoy : read memory single 32bit
    	  rcv_pckt_memrd32_t * b = (rcv_pckt_memrd32_t *)bulk_rxbuf;
    	  offset = b->offset;
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_memrd32);
      } else if (header == rcv_hdr_memrdx) {
    	  // Axor : read memory
    	  rcv_pckt_memrdx_t *p = (rcv_pckt_memrdx_t *)bulk_rxbuf;
    	  offset = p->offset;
    	  value = p->size;
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_memrdx);
      } else if (header == rcv_hdr_stop) {
    	  // AxoS : stop patch
          StopPatch();
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_start) {
    	  // Axos : start patch
          loadPatchIndex = LIVE;
          StartPatch();
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_memwr) {
    	  // AxoW : write memory
    	  rcv_pckt_memwrx_t *p = (rcv_pckt_memwrx_t *)bulk_rxbuf;
    	  int rem_length = p->size;
    	  uint8_t * offset = (uint8_t * )p->offset;
    	  if (msg > (int)sizeof(rcv_pckt_memwrx_t)) {
    		  int s = msg - sizeof(rcv_pckt_memwrx_t);
    		  if (s>rem_length) s = rem_length;
    		  int i;
    		  for(i=0;i<s;i++) {
    			  *offset++ = bulk_rxbuf[i+sizeof(rcv_pckt_memwrx_t)];
    		  }
    	  	  rem_length -= s;
    	  }
      	  while (rem_length>0) {
        	  msg = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
        	      		(uint8_t *)offset, rem_length);
        	  if (msg<0) break;
        	  rem_length -= msg;
        	  offset +=msg;
      	  }
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_paramchange) {
    	  // AxoP : parameter change
    	  rcv_pckt_paramchange_t *p = (rcv_pckt_paramchange_t *)bulk_rxbuf;
          if ((p->patch_id == patchMeta.patchID) &&
              (p->index < patchMeta.nparams)) {
            ParameterChange(&(patchMeta.params)[p->index], p->value, 0xFFFFFFEE);
          }
      } else if (header == rcv_hdr_midi) {
    	  // AxoM : midi injection
    	  rcv_pckt_midi_t *p = (rcv_pckt_midi_t *)bulk_rxbuf;
       	  midi_message_t m;
       	  m.word = p->midiword;
       	  midi_input_buffer_put(&midi_input_buffer, m);
      } else if (header == rcv_hdr_fs_create) {
    	  rcv_pckt_fs_create_t *p = (rcv_pckt_fs_create_t *)bulk_rxbuf;
    	  if (p->fn[0]) {
			  FileName[0] = p->fn[0];
			  int i=1;
			  char c = p->fn[i];
			  while(c && (i<(msg-8))) {
				  c = p->fn[i];
				  FileName[i] = c;
				  i++;
			  }
	    	  FileName[i] = 0;
			  // FIXME: filename/path length limited to ~50 characters,
			  // need to read another buffer if terminating null was not found
    	  } else {
    		  // extended mode
    		  int i;
    		  for (i=0;i<14;i++) {
    			  FileName[i] = p->fn[i];
    		  }
			  char c = p->fn[i];
			  while(c && (i<(msg-8))) {
				  c = p->fn[i];
				  FileName[i] = c;
				  i++;
			  }
	    	  FileName[i] = 0;
			  // FIXME: filename/path length limited to ~50 characters,
			  // need to read another buffer if terminating null was not found
    	  }
    	  ManipulateFile();
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_fs_dirlist) {
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_dirlist);
      } else if (header == rcv_hdr_copy_to_flash) {
          StopPatch();
    	  CopyPatchToFlash();
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_activate_dfu) {
    	  StopPatch();
    	  exception_initiate_dfu();
      } else if (header == rcv_hdr_fs_close) {
    	  CloseFile();
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_fs_append) {
          StopPatch();
          rcv_pckt_fs_append_t *p = (rcv_pckt_fs_append_t *)bulk_rxbuf;
    	  int length = p->fsize;
    	  unsigned char *pos = (unsigned char *)PATCHMAINLOC;
    	  int i;
    	  for (i=8;i<msg;i++) {
    		  pos[i] = bulk_rxbuf[i];
    	  }
    	  int rem_length = length - (msg - 8);
    	  while (rem_length > 0){
    		  msg = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
    		      		pos, rem_length);
    		  if (msg<0) {
    			  break;
    		  }
    		  pos += msg;
    		  rem_length -= msg;
    	  }
          int bytes_written;
          FRESULT err = f_write(&pFile, (char *)PATCHMAINLOC, length,
                        (void *)&bytes_written);
          if (err != FR_OK) {
            report_fatfs_error(err,0);
          }
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_preset_apply) {
    	  rcv_pckt_preset_apply_t *p = (rcv_pckt_preset_apply_t *)bulk_rxbuf;
    	  ApplyPreset(p->index);
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_paramchange);
      } else if (header == rcv_hdr_preset_write) {
    	  if (patchMeta.pPresets) {
			  rcv_pckt_preset_write_t *p = (rcv_pckt_preset_write_t *)bulk_rxbuf;
			  int rem_length = p->size;
			  uint8_t * offset = (uint8_t *)patchMeta.pPresets;
			  if (msg > (int)sizeof(rcv_pckt_memwrx_t)) {
				  int s = msg - sizeof(rcv_pckt_memwrx_t);
				  if (s>rem_length) s = rem_length;
				  int i;
				  for(i=0;i<s;i++) {
					  *offset++ = bulk_rxbuf[i+sizeof(rcv_pckt_memwrx_t)];
				  }
				  rem_length -= s;
			  }
			  while (rem_length>0) {
				  msg = usbReceive(&USBD1, USBD2_DATA_AVAILABLE_EP,
							(uint8_t *)offset, rem_length);
				  if (msg<0) break;
				  rem_length -= msg;
				  offset +=msg;
			  }
    	  }
    	  chEvtSignal(thd_bulk_Writer,evt_bulk_tx_ack);
      } else if (header == rcv_hdr_virtual_input_event) {
    	  rcv_pckt_virtual_input_event_t *p = (rcv_pckt_virtual_input_event_t *)bulk_rxbuf;
    	  chSysLock();
    	  queueInputEventI(p->input_event);
    	  chSysUnlock();
      } else {
    	  // unidentified header!
    	  int i=100;
    	  while(i--){
    	  }
      }
    }
  }
}

void InitPConnection(void) {

  extern int32_t _flash_end;
  fwid = CalcCRC32((uint8_t *)(FLASH_BASE_ADDR),
                   (uint32_t)(&_flash_end) & 0x07FFFFF);
  logObjectInit(&logstream);
  chBSemObjectInit(&logsem,0);
  /*
   * Activates the USB driver and then the USB bus pull-up on D+.
   * Note, a delay is inserted in order to not have to disconnect the cable
   * after a reset.
   */
  usbDisconnectBus(&USBD1);
  chThdSleepMilliseconds(1000);
  usbStart(&USBD1, &usbcfg);
  usbConnectBus(&USBD1);

  thd_bulk_Writer = chThdCreateStatic(waBulkWriter, sizeof(waBulkWriter), NORMALPRIO, BulkWriter, NULL);
  thd_bulk_Reader =  chThdCreateStatic(waBulkReader, sizeof(waBulkReader), NORMALPRIO, BulkReader, NULL);
}

int GetFirmwareID(void) {
  return fwid;
}

void LogTextMessage(const char* format, ...) {
	if (isConnected) {
		va_list ap;
		va_start(ap, format);
		chvprintf((BaseSequentialStream *)&logstream, format, ap);
		va_end(ap);
		chSequentialStreamPut((BaseSequentialStream *)&logstream,0);
		obqFlush(&logstream.obqueue);
		// would like to remove this semaphore, but that seems to cause data loss?
		chBSemWait(&logsem);
	}
}


/* input data decoder state machine
 *
 * "AxoP" (int value, int16 index) -> parameter set
 * "AxoR" (int length, data) -> preset data set
 * "AxoW" (int length, int addr, char[length] data) -> generic memory write
 * "Axow" (int length, int offset, char[12] filename, char[length] data) -> data write to sdcard
 *        (obsolete)
 * "Axor" (int offset, int length) -> generic memory read
 * "Axoy" (int offset) -> generic memory read, single 32bit aligned
 * "AxoS" -> start patch
 * "Axos" -> stop patch
 * "AxoT" (char number) -> apply preset
 * "AxoM" (char char char) -> 3 byte midi message
 * "AxoD" go to DFU mode
 * "AxoV" reply FW version number (4 bytes)
 * "AxoF" copy patch code to flash (assumes patch is stopped)
 * "Axod" read directory listing
 * "AxoC (int length) (char[] filename)" create and open file on sdcard
 * "Axoc" close file on sdcard
 * "AxoA (int length) (byte[] data)" append data on open file on sdcard
 * "AxoB (int or) (int and)" buttons for virtual Axoloti Control -> currently not implemented
 */

