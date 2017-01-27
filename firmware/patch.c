/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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
#include "watchdog.h"
#include "pconnection.h"
#include "sysmon.h"
#include "codec.h"
#include "axoloti_memory.h"

patchMeta_t patchMeta;

volatile patchStatus_t patchStatus;

void InitPatch0(void) {
  patchStatus = STOPPED;
  patchMeta.fptr_patch_init = 0;
  patchMeta.fptr_patch_dispose = 0;
  patchMeta.fptr_dsp_process = 0;
  patchMeta.fptr_MidiInHandler = 0;
  patchMeta.fptr_applyPreset = 0;
  patchMeta.pPExch = NULL;
  patchMeta.numPEx = 0;
  patchMeta.pDisplayVector = 0;
  patchMeta.initpreset_size = 0;
  patchMeta.npresets = 0;
  patchMeta.npreset_entries = 0;
  patchMeta.pPresets = 0;
  patchMeta.patchID = 0;
}

int dspLoadPct; // DSP load in percent
unsigned int DspTime;
char loadFName[64] = "";
loadPatchIndex_t loadPatchIndex = UNINITIALIZED;

static int32_t inbuf[32];
static int32_t *outbuf;

static int nThreadsBeforePatch;
#define STACKSPACE_MARGIN 32

static WORKING_AREA(waThreadDSP, 7200) __attribute__ ((section (".ccmramend")));
static Thread *pThreadDSP = 0;
static const char *index_fn = "/index.axb";

static int GetNumberOfThreads(void){
#ifdef CH_USE_REGISTRY
  int i=1;
  Thread *thd1 = chRegFirstThread();
  while(thd1){
    i++;
    thd1 = chRegNextThread (thd1);
  }
  return i;
#else
  return -1;
#endif
}

void CheckStackOverflow(void) {
#ifdef CH_USE_REGISTRY
#ifdef CH_DBG_FILL_THREADS
  Thread *thd = chRegFirstThread();
  // skip 1st thread, main thread
  thd = chRegNextThread (thd);
  int critical = 0;
  int nfree = 0;
  while(thd){
    char *stk = (char *)(thd+1);
    nfree = 0;
    while(*stk == CH_STACK_FILL_VALUE) {
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
    const char *name = chRegGetThreadName(thd);
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
  UIGoSafe();
  InitPatch0();
  sysmon_enable_blinker();
}

static int StartPatch1(void) {
  KVP_ClearObjects();
  sdcard_attemptMountIfUnmounted();
  // reinit pin configuration for adc
  adc_configpads();
  int32_t *ccm; // clear ccmram area declared in ramlink.ld
  for (ccm = (int32_t *)0x10000000; ccm < (int32_t *)(0x10000000 + 0x0000C000);
      ccm++)
    *ccm = 0;
  patchMeta.fptr_dsp_process = 0;
  nThreadsBeforePatch = GetNumberOfThreads();
  patchMeta.fptr_patch_init = (fptr_patch_init_t)(PATCHMAINLOC + 1);
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
  return 0;
}

static msg_t ThreadDSP(void *arg) {
  (void)(arg);
#if CH_USE_REGISTRY
  chRegSetThreadName("dsp");
#endif
  codec_clearbuffer();
  while (1) {
    // codec dsp cycle
    eventmask_t evt = chEvtWaitOne((eventmask_t)7);
    if (evt == 1) {
      static unsigned int tStart;
      tStart = hal_lld_get_counter_value();
      watchdog_feed();
      if (patchStatus == RUNNING) { // running
#if (BOARD_STM32F4DISCOVERY)||(BOARD_AXOLOTI_V03)
          // swap halfwords...
          int i;
          int32_t *p = inbuf;
          for (i = 0; i < 32; i++) {
            __ASM
            volatile ("ror %0, %1, #16" : "=r" (*p) : "r" (*p));
            p++;
          }
#endif
        (patchMeta.fptr_dsp_process)(inbuf, outbuf);
#if (BOARD_STM32F4DISCOVERY)||(BOARD_AXOLOTI_V03)
        p = outbuf;
        for (i = 0; i < 32; i++) {
          __ASM
          volatile ("ror %0, %1, #16" : "=r" (*p) : "r" (*p));
          p++;
        }
#endif
      }
      else if (patchStatus == STOPPING){
        codec_clearbuffer();
        StopPatch1();
        patchStatus = STOPPED;
        codec_clearbuffer();
      }
      else if (patchStatus == STOPPED){
        codec_clearbuffer();
      }
      adc_convert();
      DspTime = RTT2US(hal_lld_get_counter_value() - tStart);
      dspLoadPct = (100 * DspTime) / (1000000 / 3000);
      if (dspLoadPct > 99) {
        // overload:
        // clear output buffers
        // and give other processes a chance
        codec_clearbuffer();
        //      LogTextMessage("dsp overrun");
        chThdSleepMilliseconds(1);
      }
    }
    else if (evt == 2) {
      // load patch event
      codec_clearbuffer();
      StopPatch1();
      patchStatus = STOPPED;
      if (loadFName[0]) {
        int res = sdcard_loadPatch1(loadFName);
        if (!res) StartPatch1();
      }
      else if (loadPatchIndex == START_FLASH) {
        // patch in flash sector 11
        memcpy((uint8_t *)PATCHMAINLOC, (uint8_t *)PATCHFLASHLOC,
               PATCHFLASHSIZE);
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
    else if (evt == 4) {
      // start patch
      codec_clearbuffer();
      StartPatch1();
    }
  }
  return (msg_t)0;
}

void StopPatch(void) {
  if (!patchStatus) {
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
  chEvtSignal(pThreadDSP, (eventmask_t)4);
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
    pThreadDSP = chThdCreateStatic(waThreadDSP, sizeof(waThreadDSP), HIGHPRIO,
                                   ThreadDSP, NULL);
}

void computebufI(int32_t *inp, int32_t *outp) {
  int i;
  for (i = 0; i < 32; i++) {
    inbuf[i] = inp[i];
  }
  outbuf = outp;
  chSysLockFromIsr()
  ;
  chEvtSignalI(pThreadDSP, (eventmask_t)1);
  chSysUnlockFromIsr();
}

void MidiInMsgHandler(midi_device_t dev, uint8_t port, uint8_t status,
                      uint8_t data1, uint8_t data2) {
  if (patchStatus == RUNNING) {
    (patchMeta.fptr_MidiInHandler)(dev, port, status, data1, data2);
  }
}

void LoadPatch(const char *name) {
  strcpy(loadFName, name);
  loadPatchIndex = BY_FILENAME;
  chEvtSignal(pThreadDSP, (eventmask_t)2);
}

void LoadPatchStartSD(void) {
  strcpy(loadFName, "/START.BIN");
  loadPatchIndex = START_SD;
  chEvtSignal(pThreadDSP, (eventmask_t)2);
  chThdSleepMilliseconds(50);
}

void LoadPatchStartFlash(void) {
  loadPatchIndex = START_FLASH;
  chEvtSignal(pThreadDSP, (eventmask_t)2);
}

void LoadPatchIndexed(uint32_t index) {
  loadPatchIndex = index;
  loadFName[0] = 0;
  chEvtSignal(pThreadDSP, (eventmask_t)2);
}

loadPatchIndex_t GetIndexOfCurrentPatch(void) {
  return loadPatchIndex;
}
