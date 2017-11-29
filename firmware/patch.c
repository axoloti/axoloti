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
#include "patch.h"
#include "sdcard.h"
#include "string.h"
#include "axoloti_board.h"
#include "midi.h"
#include "midi_usb.h"
#include "watchdog.h"
#include "pconnection.h"
#include "sysmon.h"
#include "spilink.h"
#include "codec.h"
#include "axoloti_memory.h"
#include "menu_content/main_menu.h"

//#define DEBUG_INT_ON_GPIO 1

patchMeta_t patchMeta;

volatile patchStatus_t patchStatus;

void InitPatch0(void) {
  patchStatus = STOPPED;
  patchMeta.fptr_patch_init = 0;
  patchMeta.fptr_patch_dispose = 0;
  patchMeta.fptr_dsp_process = 0;
  patchMeta.fptr_MidiInHandler = 0;
  patchMeta.fptr_applyPreset = 0;
  patchMeta.params = NULL;
  patchMeta.nparams = 0;
  patchMeta.pDisplayVector = 0;
  patchMeta.initpreset_size = 0;
  patchMeta.npresets = 0;
  patchMeta.npreset_entries = 0;
  patchMeta.pPresets = 0;
  patchMeta.patchID = 0;
  patchMeta.nobjects = 0;
}

int dspLoadPct; // DSP load in percent
unsigned int DspTime;
char loadFName[64] = "";
loadPatchIndex_t loadPatchIndex = UNINITIALIZED;

static int32_t inbuf[32];
static int32_t *outbuf;

static int nThreadsBeforePatch;
#define STACKSPACE_MARGIN 32

static WORKING_AREA(waThreadDSP, 7200) CCM;
static Thread *pThreadDSP = 0;
static const char *index_fn = "/index.axb";

#define THREAD_DSP_EVT_MASK_COMPUTE ((eventmask_t)1)
#define THREAD_DSP_EVT_MASK_LOAD ((eventmask_t)2)
#define THREAD_DSP_EVT_MASK_START ((eventmask_t)4)
#define THREAD_DSP_EVT_MASK_WRITE ((eventmask_t)8)

static int GetNumberOfThreads(void){
  int i=1;
  Thread *thd1 = chRegFirstThread();
  while(thd1){
    i++;
    thd1 = chRegNextThread (thd1);
  }
  return i;
}

void CheckStackOverflow(void) {
#if CH_DBG_FILL_THREADS
  Thread *thd = chRegFirstThread();
  // skip 1st thread, main thread
  thd = chRegNextThread (thd);
  int critical = 0;
  int nfree = 0;
  while(thd){
    char *stk = (char *)(thd+1);
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
  }
#endif
}

static void StopPatch1(void) {
  if (patchMeta.fptr_patch_dispose != 0) {
    CheckStackOverflow();
    (patchMeta.fptr_patch_dispose)();
    // check if the number of threads after patch disposal is the same as before
    int j=20;
    int i = GetNumberOfThreads();
    // try sleeping up to 1 second so threads can terminate
    while( (j--) && (i!=nThreadsBeforePatch)) {
      chThdSleepMilliseconds(50);
      i = GetNumberOfThreads();
    }
    if (i!=nThreadsBeforePatch) {
       LogTextMessage("error: patch stopped but did not terminate its thread(s)");
    }
  }
  ui_deinit_patch();
  InitPatch0();
  sysmon_enable_blinker();
}

static int StartPatch1(void) {
  ui_deinit_patch();
  sdcard_attemptMountIfUnmounted();
  // reinit pin configuration for adc
  adc_configpads();
  int32_t *ccm; // clear ccmram area declared in ramlink.ld
  for (ccm = (int32_t *)0x10000000; ccm < (int32_t *)(0x10000000 + 0x0000C000);
      ccm++)
    *ccm = 0;
  patchMeta.fptr_dsp_process = 0;
  nThreadsBeforePatch = GetNumberOfThreads();

  fourcc_t signature = *(fourcc_t *)PATCHMAINLOC;
  if (signature != FOURCC('a','x','x','2')) {
	    report_patchLoadFail((const char *)&loadFName[0]);
	    patchStatus = STARTFAILED;
	    return -1;
  }
  chunk_header_t *patch_root_chunk = *(chunk_header_t * *)(PATCHMAINLOC+4);
  readchunk_patch_root(patch_root_chunk);
  if (patchMeta.fptr_patch_init == 0){
	    report_patchLoadFail((const char *)&loadFName[0]);
	    patchStatus = STARTFAILED;
	    return -1;
  }
//  patchMeta.fptr_patch_init = (fptr_patch_init_t)(PATCHMAINLOC_ALIASED + 1);
// PATCHMAINLOC_ALIASED + 1 for THUMB mode
  (patchMeta.fptr_patch_init)(GetFirmwareID());
  if (patchMeta.fptr_dsp_process == 0) {
    report_patchLoadFail((const char *)&loadFName[0]);
    patchStatus = STARTFAILED;
    return -1;
  }
  int32_t sdrem = sdram_get_free();
  if (sdrem<0) {
    StopPatch1();
    patchStatus = STARTFAILED;
    patchMeta.patchID = 0;
    report_patchLoadSDRamOverflow((const char *)&loadFName[0],-sdrem);
    return -1;
  }
  patchStatus = RUNNING;
  ui_init_patch();
  tx_pckt_ack_v2.underruns = 0;
  return 0;
}

static THD_FUNCTION(ThreadDSP, arg) {
  (void)(arg);
  chRegSetThreadName("dsp");
  codec_clearbuffer();
  while (1) {
    // codec dsp cycle
    eventmask_t evt = chEvtWaitOne(
    		THREAD_DSP_EVT_MASK_COMPUTE
    		| THREAD_DSP_EVT_MASK_LOAD
			| THREAD_DSP_EVT_MASK_START
			| THREAD_DSP_EVT_MASK_WRITE);

    if (evt == THREAD_DSP_EVT_MASK_COMPUTE) {
      static unsigned int tStart;
      tStart = port_rt_get_counter_value();
      watchdog_feed();
      if (patchStatus == RUNNING) { // running
    	// audio payload
        (patchMeta.fptr_dsp_process)(inbuf, outbuf);
        // midi input
        // perhaps throttle midi input processing when dsp_process took a lot of time
        midi_message_t midi_in;
        msg_t msg = midi_input_buffer_get(&midi_input_buffer, &midi_in);
        while (msg == MSG_OK) {
        	(patchMeta.fptr_MidiInHandler)(midi_in.fields.port, 0,
        			midi_in.fields.b0,
					midi_in.fields.b1,
					midi_in.fields.b2);
            msg = midi_input_buffer_get(&midi_input_buffer, &midi_in);
        }
        // notify usbd output
        midi_output_buffer_notify(&midi_output_usbd);
      }
      else if (patchStatus == STOPPING) {
        codec_clearbuffer();
        StopPatch1();
        patchStatus = STOPPED;
        codec_clearbuffer();
      }
      else if (patchStatus == STOPPED) {
        codec_clearbuffer();
        // flush midi input
        midi_message_t midi_in;
        msg_t msg;
        do {
        	msg = midi_input_buffer_get(&midi_input_buffer, &midi_in);
        } while (msg == MSG_OK);
      }
      adc_convert();
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
    else if (evt == THREAD_DSP_EVT_MASK_LOAD) {
      // load patch event
      codec_clearbuffer();
      StopPatch1();
      patchStatus = STOPPED;
      if (loadFName[0]) {
        int res = sdcard_loadPatch1(loadFName);
        if (!res) StartPatch1();
      }
      else if (loadPatchIndex == START_FLASH) {
    	*(uint32_t *)PATCHMAINLOC = 0;
    	bin_loader_flash(PATCHFLASHLOC, PATCHFLASHSIZE);
        if ((*(uint32_t *)PATCHMAINLOC != 0xFFFFFFFF)
            && (*(uint32_t *)PATCHMAINLOC != 0)) {
          StartPatch1();
        }
      } else
      if (loadPatchIndex == START_SD) {
        strcpy(&loadFName[0], "/start.bin");
        int res = sdcard_loadPatch1(loadFName);
        if (!res) StartPatch1();
      }
      else {
        FRESULT err;
        FIL f;
        uint32_t bytes_read;
        err = f_open(&f, index_fn, FA_READ | FA_OPEN_EXISTING);
        if (err)
          report_fatfs_error(err, index_fn);
        err = f_read(&f, (uint8_t *)PATCHMAINLOC, 0xE000, (void *)&bytes_read);
        if (err != FR_OK) {
          report_fatfs_error(err, index_fn);
          continue;
        }
        err = f_close(&f);
        if (err != FR_OK) {
          report_fatfs_error(err, index_fn);
          continue;
        }
        char *t;
        t = (char *)PATCHMAINLOC;
        int32_t cindex = 0;

        //LogTextMessage("load %d %d %x",index, bytes_read, t);
        while (bytes_read) {
          //LogTextMessage("scan %d",*t);
          if (cindex == loadPatchIndex) {
            //LogTextMessage("match %d",index);
            char *p, *e;
            p = t;
            e = t;
            while ((*e != '\n') && bytes_read) {
              e++;
              bytes_read--;
            }
            if (bytes_read) {
              e = e - 4;
              *e++ = '/';
              *e++ = 'p';
              *e++ = 'a';
              *e++ = 't';
              *e++ = 'c';
              *e++ = 'h';
              *e++ = '.';
              *e++ = 'b';
              *e++ = 'i';
              *e++ = 'n';
              *e = 0;
              loadFName[0] = '/';
              strcpy(&loadFName[1], p);
              int res = sdcard_loadPatch1(loadFName);
              if (!res) {
                StartPatch1();
              }
              if (patchStatus != RUNNING) {
                loadPatchIndex = START_SD;
                strcpy(&loadFName[0], "/start.bin");
                res = sdcard_loadPatch1(loadFName);
                if (!res) StartPatch1();
              }
            }
            goto cont;
          }
          if (*t == '\n') {
            cindex++;
          }
          t++;
          bytes_read--;
        }
        if (!bytes_read) {
          LogTextMessage("patch load out-of-range %d", loadPatchIndex);
          loadPatchIndex = START_SD;
          strcpy(&loadFName[0], "/start.bin");
          int res = sdcard_loadPatch1(loadFName);
          if (!res) StartPatch1();
        }
        cont: ;
      }
    }
    else if (evt == THREAD_DSP_EVT_MASK_START) {
      // start patch
      codec_clearbuffer();
      StartPatch1();
    }
    else if (evt == THREAD_DSP_EVT_MASK_WRITE) {
    	// write patch
        codec_clearbuffer();
        StopPatch1();
    	sdcard_bin_writer("written");
    }
    pollProcessUIEvent();
#ifdef DEBUG_INT_ON_GPIO
	palClearPad(GPIOA, 2);
#endif
  }
}

void StopPatch(void) {
  if (patchStatus != STOPPED) {
    patchStatus = STOPPING;
    while (1) {
      chThdSleepMilliseconds(1);
      if (patchStatus == STOPPED)
        break;
    }
    StopPatch1();
    patchStatus = STOPPED;
  }
}

int StartPatch(void) {
  chEvtSignal(pThreadDSP, THREAD_DSP_EVT_MASK_START);
  while ((patchStatus != RUNNING) && (patchStatus != STARTFAILED)) {
    chThdSleepMilliseconds(1);
  }
  if (patchStatus == STARTFAILED) {
    patchStatus = STOPPED;
    LogTextMessage("patch start failed",patchStatus);
  }
  return 0;
}

void start_dsp_thread(void) {
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
  chSysLockFromIsr();
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
  chSysUnlockFromIsr();
}

// MidiByte0 is only to be used by patches, avoids queuing
void MidiInMsgHandler(midi_device_t dev, uint8_t port, uint8_t status,
                      uint8_t data1, uint8_t data2) {
  if (patchStatus == RUNNING) {
    (patchMeta.fptr_MidiInHandler)(dev, port, status, data1, data2);
  }
}

void LoadPatch(const char *name) {
  strcpy(loadFName, name);
  loadPatchIndex = BY_FILENAME;
  chEvtSignal(pThreadDSP, THREAD_DSP_EVT_MASK_LOAD);
}

void LoadPatchStartSD(void) {
  if (!palReadPad(SW2_PORT, SW2_PIN)) {
	  strcpy(loadFName, "/START.BIN");
	  loadPatchIndex = START_SD;
	  chEvtSignal(pThreadDSP, THREAD_DSP_EVT_MASK_LOAD);
	  chThdSleepMilliseconds(50);
  }
}

void LoadPatchStartFlash(void) {
  loadPatchIndex = START_FLASH;
  chEvtSignal(pThreadDSP, THREAD_DSP_EVT_MASK_LOAD);
}

void LoadPatchIndexed(uint32_t index) {
  loadPatchIndex = index;
  loadFName[0] = 0;
  chEvtSignal(pThreadDSP, THREAD_DSP_EVT_MASK_LOAD);
}

loadPatchIndex_t GetIndexOfCurrentPatch(void) {
  return loadPatchIndex;
}

void WritePatch(void) {
  chEvtSignal(pThreadDSP, THREAD_DSP_EVT_MASK_WRITE);
}
