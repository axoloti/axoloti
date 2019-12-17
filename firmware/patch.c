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
#include "patch.h"
#include "sdcard.h"
#include "string.h"
#include "axoloti_board.h"
#include "midi.h"
#include "midi_usb.h"
#include "exceptions.h"
#include "pconnection.h"
#include "sysmon.h"
#include "spilink.h"
#include "codec.h"
#include "axoloti_memory_impl.h"
#include "menu_content/main_menu.h"
#include "elfloader/loader.h"
#include "exports.h"
#include "patch_impl.h"
#include "logging.h"
#include "ff.h"
#include "vfile_ops/vfile_fatfs.h"
#include "vfile_ops/vfile_mem.h"
#include "loader_userdata.h"
#include "patch_wrapper.h"
#include "midi_clock.h"
#include "error_codes_impl.h"
#include "axoloti_api_version.h"

//#define DEBUG_INT_ON_GPIO 1

static mutex_t mtxWorker;
static THD_WORKING_AREA(waThreadWorker, 3000);
static const char *err_msg;
static char err_string[64];

#define n_patch_slots 4
static patch_t patchMeta1[n_patch_slots];

patch_t * patch_iter_first(void) {
  return &patchMeta1[0];
}
int patch_iter_done(patch_t * cur) {
  return cur<&patchMeta1[n_patch_slots];
}
patch_t * patch_iter_next(patch_t * cur) {
  return cur+1;
}

static void patch_setError(char *format, ...) {
  va_list args;
  va_start (args, format);
  chvsnprintf(err_string, sizeof(err_string),format,args);
  va_end (args);
  err_msg = err_string;
}

static void patch_setConstError(const char *err) {
  err_msg = err;
}

const char * patch_getError() {
  const char *r = err_msg;
  err_msg = 0;
  return r;
}

static int validatePatch(patch_t *patch) {
  int r = (patch >= &patchMeta1[0]) && (patch <= &patchMeta1[n_patch_slots-1]);
  if (!r) chSysHalt("invalid patch arg");
  return r;
}

patch_t * getPatchMeta(int slot) {
  return &patchMeta1[slot];
}

extern msg_t tx_patchList(void); // TODO: cleanup

static void InitPatch0(patch_t * patch) {
  if (!validatePatch(patch)) return;
  patch->patchStatus = STOPPED;
  patch->patchobject = 0;
}

int dspLoadPct; // DSP load in percent

static int32_t inbuf[32];
static int32_t *outbuf;

#define STACKSPACE_MARGIN 32

static THD_WORKING_AREA(waThreadDSP, 7200) CCM_fw;
static thread_t *pThreadDSP = 0;

#define THREAD_DSP_EVT_MASK_COMPUTE ((eventmask_t)1)

void CheckStackOverflow(void) {
#if CH_DBG_FILL_THREADS
  thread_t *thd = chRegFirstThread();
  // skip 1st thread, main thread
  thd = chRegNextThread (thd);
  int critical = 0;
  int nfree = 0;
  while(thd){
    if (THD_WORKING_AREA_END(thd) == THD_WORKING_AREA_BASE(thd)) {
	thd = chRegNextThread(thd);
        continue;
    }

//    char *stk = (char *)(thd+1);
//    char *stk = (char *)THD_WORKING_AREA_END(thd) - 4;
    char *stk = (char *)THD_WORKING_AREA_BASE(thd) + 64;
    nfree = 0;
    while(*stk == CH_DBG_STACK_FILL_VALUE) {
      nfree++;
      stk++;
      if (nfree>=STACKSPACE_MARGIN) break;
    }    
    if (nfree<STACKSPACE_MARGIN) {
       critical = 1;
       break;
    }
    thd = chRegNextThread(thd);
  }
  if (critical) {
    const char *name = chRegGetThreadNameX(thd);
    if (name!=0)
      if (nfree)
        LogTextMessage("Thread %s : stack critical %d",name,nfree);
      else
        LogTextMessage("Thread %s : stack overflow",name);
    else
      if (nfree)
        LogTextMessage("Thread ?? : stack critical %d",nfree);
      else
        LogTextMessage("Thread ?? : stack overflow");
    LogTextMessage("b = %08X e = %08X", THD_WORKING_AREA_BASE(thd), THD_WORKING_AREA_END(thd));
  }
#endif
}

int getPatchNameFromIndex(int patchIndex, char *patchName) {
  static const char *index_fn = "/index.axb";
  FRESULT err;
  FIL f;
  uint32_t bytes_read;
  err = f_open(&f, index_fn, FA_READ | FA_OPEN_EXISTING);
  if (err) {
    patch_setError("can't open patchbank file %s",index_fn);
    report_fatfs_error(err, index_fn);
    return -1;
  }
  int pos = 0;
  int index = 0;
  uint8_t buf[64];
  while(1) {
    err = f_read(&f, &buf[0], sizeof(buf), (void *)&bytes_read);
    if (err != FR_OK) {
      report_fatfs_error(err, index_fn);
      f_close(&f);
      return -2;
    }
    int p = 0;
    while((buf[p]!='\n') && (p<bytes_read)) {
      p++;
    }
    if (p==bytes_read) {
      patch_setError("index %d not in patchbank",patchIndex);
      f_close(&f);
      return -3;
    }
    if (index == patchIndex) {
      buf[p]=0;
      f_close(&f);
      // modify extension into .elf
      int l = strlen(&buf[0]);
      if (buf[l-4] == '.') {
        buf[l-3] = 'e';
        buf[l-2] = 'l';
        buf[l-1] = 'f';
      }
      strcpy(patchName, &buf[0]);
      f_close(&f);
      return 0;
    }
    index++;
    pos += p+1;
    f_lseek(&f, pos);
  }
  f_close(&f);
  return -4;
}

static THD_FUNCTION(ThreadDSP, arg) {
  (void)(arg);
  chRegSetThreadName("dsp");
  codec_clearbuffer();

  while (1) {
    // codec dsp cycle
    eventmask_t evt = chEvtWaitOne(
        THREAD_DSP_EVT_MASK_COMPUTE);

    if (evt == THREAD_DSP_EVT_MASK_COMPUTE) {
      static unsigned int tStart;
      tStart = port_rt_get_counter_value();
      // prepare audio
      int32_t inbuf_noninterleaved[BUFSIZE*2];
      {
        int i;
        int32_t *pOutbuf = outbuf;
        int32_t *pInbufNIL = &inbuf_noninterleaved[0];
        int32_t *pInbufNIR = &inbuf_noninterleaved[BUFSIZE];
        int32_t *pInbuf = &inbuf[0];
        // clear outbuf
        // and interleave input, shift for 4 bits of headroom
        for(i=BUFSIZE;i-- > 0;) {
          *pInbufNIL++ = (*pInbuf++)>>4;
          *pInbufNIR++ = (*pInbuf++)>>4;
          *pOutbuf++ = 0;
          *pOutbuf++ = 0;
        }
      }
      // audio payload
      patch_t * patch;
      for(patch = patch_iter_first();patch_iter_done(patch);patch=patch_iter_next(patch)) {
        if (patch->patchStatus == RUNNING) { // running
          int32_t patch_out[32];
          patch_dsp_process(patch, inbuf_noninterleaved, patch_out);
          // sum and interleave
          int i;
          int32_t *pOutbuf = outbuf;
          int32_t *pPatchOutL = &patch_out[0];
          int32_t *pPatchOutR = &patch_out[BUFSIZE];
          for(i=BUFSIZE;i-- > 0;) {
            *pOutbuf++ += *pPatchOutL++;
            *pOutbuf++ += *pPatchOutR++;
          }
        } else if (patch->patchStatus == STOPPING) {
          patch->patchStatus = STOPPED;
        }
      }
      // saturate and shift audio output to full range
      {
        int i;
        int32_t *pOutbuf = outbuf;
        for(i=BUFSIZE;i-- > 0;){
          *pOutbuf = __SSAT(*pOutbuf,28)<<4;
          pOutbuf++;
          *pOutbuf = __SSAT(*pOutbuf,28)<<4;
          pOutbuf++;
        }
      }
      // midi input

      // perhaps throttle midi input processing when dsp_process took a lot of time
      midi_message_t midi_in;
      msg_t msg = midi_input_buffer_get(&midi_input_buffer, &midi_in);
      while (msg == MSG_OK) {
        for(patch = patch_iter_first();patch_iter_done(patch);patch=patch_iter_next(patch)) {
          if (patch->patchStatus == RUNNING) { // running
            patch_midiInHandler(patch, midi_in.word);
          }
        }
        msg = midi_input_buffer_get(&midi_input_buffer, &midi_in);
      }
      // notify usbd output
      midi_output_buffer_notify(&midi_output_usbd);

      adc_convert();
      unsigned int DspTime;
      DspTime = (port_rt_get_counter_value() - tStart);
      dspLoadPct = (DspTime) / (STM32_SYSCLK / 300000);
      if (dspLoadPct > 98) {
        // overload:
        // clear output buffers
        // and give other processes a chance
        codec_clearbuffer();
        tx_pckt_ack_v2.underruns++;
        //      LogTextMessage("dsp overrun");
        // dsp overrun penalty,
        // keeping cooperative with lower priority threads
        chThdSleepMilliseconds(1);
      }
      if (dspLoadPct < 95) {
    	  // RMS input/output level computation
    	  int32_t *psi = inbuf;
    	  int32_t *pso = outbuf;
    	  float vu_accf0 = tx_pckt_ack_v2.vu_input[0];
    	  float vu_accf1 = tx_pckt_ack_v2.vu_input[1];
    	  float vu_accf2 = tx_pckt_ack_v2.vu_output[0];
    	  float vu_accf3 = tx_pckt_ack_v2.vu_output[1];
    	  for(int i=0;i<BUFSIZE;i++) {
    		  float s0 = *psi++;
    		  float s1 = *psi++;
    		  float s2 = *pso++;
    		  float s3 = *pso++;
    		  vu_accf0 += s0*s0;
    		  vu_accf1 += s1*s1;
    		  vu_accf2 += s2*s2;
    		  vu_accf3 += s3*s3;
    	  }
    	  float g = 1.0f/128;
    	  vu_accf0 -= g*vu_accf0;
    	  vu_accf1 -= g*vu_accf1;
    	  vu_accf2 -= g*vu_accf2;
    	  vu_accf3 -= g*vu_accf3;
    	  tx_pckt_ack_v2.vu_input[0] = vu_accf0;
    	  tx_pckt_ack_v2.vu_input[1] = vu_accf1;
    	  tx_pckt_ack_v2.vu_output[0] = vu_accf2;
    	  tx_pckt_ack_v2.vu_output[1] = vu_accf3;
      }
      spilink_clear_audio_tx();
    }
    pollProcessUIEvent();
#ifdef DEBUG_INT_ON_GPIO
	palClearPad(GPIOA, 2);
#endif
  }
}

void start_dsp_thread(void) {
  chMtxObjectInit(&mtxWorker);

  patch_t * patch;
  for(patch = patch_iter_first();patch_iter_done(patch);patch=patch_iter_next(patch)) {
    patch->patchStatus = STOPPED;
  }

  if (!pThreadDSP)
    pThreadDSP = chThdCreateStatic(waThreadDSP, sizeof(waThreadDSP), HIGHPRIO-2,
                                   ThreadDSP, NULL);
}

midi_clock_t midi_clock = {
	.active = 0,
	.period = 125,
	.counter = 1,
	.song_position = 0, // in 24 ppq counts
};

void computebufI(int32_t *inp, int32_t *outp) {
  int i;
  for (i = 0; i < 32; i++) {
    inbuf[i] = inp[i];
  }
  outbuf = outp;
  chSysLockFromISR();
  // generate midi clock in codec interrupt
  // advantage: zero quarter note jitter
  // no clock loss when audio computation suffers a buffer underrun
  if (midi_clock.active) {
	  // todo: clock regeneration?
	  midi_clock.counter-=2;
	  if (midi_clock.counter<=0) {
		  midi_clock.counter += midi_clock.period;
		  midi_clock.song_position++;
		  midi_message_t m;
		  m.bytes.ph = 0xF;
		  m.bytes.b0 = MIDI_TIMING_CLOCK;
		  m.bytes.b1 = 0;
		  m.bytes.b2 = 0;
		  // perhaps better not to queue
		  midi_input_buffer_put(&midi_input_buffer, m);
		  // todo: route to selected midi output ports
	  }
  }
  chEvtSignalI(pThreadDSP, THREAD_DSP_EVT_MASK_COMPUTE);
  chSysUnlockFromISR();
}

static patch_t * allocPatch(void) {
  patch_t * patch;
  for(patch = patch_iter_first();patch_iter_done(patch);patch=patch_iter_next(patch)) {
    if ((patch->patchStatus == STOPPED)||(patch->patchStatus == STARTFAILED)) {
      return patch;
    }
  }
  return 0;
}

static void * addr_from_hexstring(const char *str) {
  // eg. "C0000100" to 0xC0000100
  uint32_t i,a=0;
  for(i=0;i<8;i++) {
    char c = *str++;
    int v = ((c<'A')?c-'0':10+c-'A') & 0x0F;
    a = (a<<4) | v;
  }
  return (void *)a;
}

static THD_FUNCTION(threadLoadPatch, arg) {
  const char *name = (const char *)arg;
  sdcard_attemptMountIfUnmounted();
  char name2[32];
  if (*name == 0) {
    int index = (*(int*)arg)>>8;
    int r = getPatchNameFromIndex(index,&name2[0]);
    if (r) {
      chThdExit(0);
    }
    name = &name2[0];
  }
  patch_t * patch = allocPatch();
  if (!patch) {
    patch_setConstError("can't allocate patch");
    chThdExit(0);
  }
  ui_deinit_patch();
  if (name[0] == '@') {
    // name is assumed to be eg. "@12345678:patchname" where
    // 0x12345678 is the memory address of a memory-mapped file
    void * a = addr_from_hexstring(&name[1]);
    name = &name[10];
    if (((int)a >> 24) == 0x08) {
        // addr 0x08?????? is in flash
      userdata_t loader_env = {
          .vfile_ops = &vfile_ops_flash
      };
      load_elf((const char *)a, loader_env, &patch->elf);
    } else {
        // addr not in flash
      userdata_t loader_env = {
          .vfile_ops = &vfile_ops_mem
      };
      load_elf((const char *)a, loader_env, &patch->elf);
    }
  } else {
    userdata_t loader_env = {
        .vfile_ops = &vfile_ops_fatfs
    };
    load_elf(name, loader_env, &patch->elf);
  }
  strncpy(patch->name, name, sizeof(patch->name)-1);
  if (!patch->elf) {
    patch_setConstError("failed to load patch");
    chThdExit(0);
  }
  if (!validatePatch(patch)) {
    chSysHalt("invalid patch?");
  }
  int patch_api_version = (int)get_sym(patch->elf, "axoloti_api_version", 0);
  if (!patch_api_version)
	  LogTextMessage("No API version found in patch.");
  else if (patch_api_version > axoloti_api_version)
	  LogTextMessage("Patch uses newer API version (%8X) than the firmware provides (%8X).", patch_api_version, axoloti_api_version);
  else if (patch_api_version != axoloti_api_version)
	  LogTextMessage("Patch uses different API version (%8X) than the firmware provides (%8X).", patch_api_version, axoloti_api_version);

  ui_deinit_patch();
  sdcard_attemptMountIfUnmounted();
  int (*getInstanceSize)(void);
  getInstanceSize = get_func(patch->elf, "getInstanceSize");
  if (getInstanceSize==0) {
    patch_setConstError("patch missing getInstanceSize");
    chThdExit(0);
  }
  int instanceSize = getInstanceSize();
  patch->patchobject = ax_malloc(instanceSize,0);
  if (!patch->patchobject) {
    patch_setConstError("patch instance allocation: out of memory");
    chThdExit(0);
  }
  memset(patch->patchobject,0,instanceSize);
  int (*initInstance)(void *);
  initInstance = get_func(patch->elf, "initInstance");
  if (initInstance==0) {
    ax_free(patch->patchobject);
    patch_setConstError("patch missing initInstance");
    chThdExit(0);
  }
  int result = initInstance(patch->patchobject);
  if (result != 0) {
    const char *err_msg = errorCodeToString(result);
    if (!err_msg) {
        patch_setError("patch initialization failed, result = %d (0x%08x)", result, result);
    } else {
        patch_setError("patch initialization failed, reason: %s", err_msg);
    }
//    patch_dispose(patch);
    ax_free(patch->patchobject);
    chThdExit(0);
  }
  patch->patchStatus = RUNNING;
  ui_init_patch();
  tx_pckt_ack_v2.underruns = 0;
  tx_patchList();
  chThdExit((msg_t)patch);
}

static THD_FUNCTION(threadStopPatch, arg) {
  patch_t * patch = (patch_t *)arg;
  if (!validatePatch(patch)) return;
  if (patch->patchStatus == RUNNING) {
    patch->patchStatus = STOPPING;
    while (1) {
      chThdSleepMilliseconds(1);
      if (patch->patchStatus == STOPPED)
        break;
    }
    if (!validatePatch(patch)) return;
    patch_dispose(patch);
    ax_free(patch->patchobject);
    ui_deinit_patch();
    InitPatch0(patch);
    sysmon_enable_blinker();
    tx_patchList();
    if (patch->elf) {
      unload_elf(patch->elf);
      patch->elf = 0;
    }
    patch->patchStatus = STOPPED;
  }
}

patch_t * patch_load(const char *name, patch_callback_t patch_callback) {
  chMtxLock(&mtxWorker);
  thread_t * job = chThdCreateStatic(waThreadWorker, sizeof(waThreadWorker), NORMALPRIO,
      threadLoadPatch , (void *)name);
  msg_t msg = chThdWait(job);
  chMtxUnlock(&mtxWorker);
  patch_t * patch = (patch_t *)msg;
  if (patch) {
    patch->patch_callback = patch_callback;
  }
  return patch;
}

patch_t * patch_loadIndex(int index, patch_callback_t patch_callback) {
  // passing the index value as string works
  // as long as index >=0 and index < 2^24
  if ((index<0)||(index>(1<<23))) {
    chSysHalt("LoadPatchIndexed arg overflow");
    return 0;
  }
  int idx8 = index<<8;
  return patch_load((const char *)&idx8, patch_callback);
}

patch_t * patch_loadStartSD(patch_callback_t patch_callback) {
  if (!fs_ready) return 0;
  const char *fn = "/start.elf";
  return patch_load(fn, patch_callback);
}

void patch_stop(patch_t *patch) {
  if (!patch) {
    // stop all patches...
    for(patch = patch_iter_first();patch_iter_done(patch);patch=patch_iter_next(patch)) {
      if (patch->patchStatus == RUNNING) {
        patch_stop(patch);
      }
    }
    return;
  } else {
    patch_callback_t patch_callback = patch->patch_callback;
    chMtxLock(&mtxWorker);
    thread_t * job = chThdCreateStatic(waThreadWorker, sizeof(waThreadWorker), NORMALPRIO,
        threadStopPatch , (void *)patch);
    msg_t msg = chThdWait(job);
    chMtxUnlock(&mtxWorker);
    if (patch_callback) {
      patch_callback(patch, patch_callback_stop);
    }
  }
}

