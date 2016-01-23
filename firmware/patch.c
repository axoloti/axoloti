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

patchMeta_t patchMeta;

volatile int patchStatus;

void InitPatch0(void) {
  patchStatus = 2;
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
}

int dspLoadPct; // DSP load in percent
unsigned int CycleTime;
unsigned int DspTime;

static int32_t inbuf[32];
static int32_t *outbuf;

static WORKING_AREA(waThreadDSP, 7200) __attribute__ ((section (".ccmramend")));
static Thread *pThreadDSP = 0;
static msg_t ThreadDSP(void *arg) {
  (void)(arg);
#if CH_USE_REGISTRY
  chRegSetThreadName("dsp");
#endif
  while (1) {
    chEvtWaitOne((eventmask_t)1);
    static unsigned int tStart;
    CycleTime = RTT2US(hal_lld_get_counter_value() - tStart);
    tStart = hal_lld_get_counter_value();
    watchdog_feed();
    if (!patchStatus) { // running
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
    else { // stopping or stopped
      patchStatus = 1;
      int i;
      for (i = 0; i < 32; i++) {
        outbuf[i] = 0;
      }
    }
    adc_convert();
    DspTime = RTT2US(hal_lld_get_counter_value() - tStart);
    dspLoadPct = (100 * DspTime) / CycleTime;
  }
  return (msg_t)0;
}

void StopPatch(void) {
  if (!patchStatus) {
    patchStatus = 2;
    while (pThreadDSP) {
      if (patchStatus == 1)
        break;
    }
    if (patchMeta.fptr_patch_dispose != 0)
      (patchMeta.fptr_patch_dispose)();
    UIGoSafe();
    InitPatch0();
    sysmon_enable_blinker();
  }
}

void StartPatch(void) {
  KVP_ClearObjects();
  sdcard_attemptMountIfUnmounted();
  // reinit pin configuration for adc
  adc_configpads();
  patchMeta.fptr_dsp_process = 0;
  patchMeta.fptr_patch_init = (fptr_patch_init_t)(PATCHMAINLOC + 1);
  (patchMeta.fptr_patch_init)(GetFirmwareID());
  if (patchMeta.fptr_dsp_process == 0) {
    // failed, incompatible firmwareID?
    return;
  }
  patchStatus = 0;
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
  if (pThreadDSP) {
    chSysLockFromIsr()
    ;
    chEvtSignalI(pThreadDSP, (eventmask_t)1);
    chSysUnlockFromIsr()
    ;
  }
  else
    for (i = 0; i < 32; i++) {
      outp[i] = 0;
    }
}

void MidiInMsgHandler(midi_device_t dev, uint8_t port, uint8_t status,
                      uint8_t data1, uint8_t data2) {
  if (!patchStatus) {
    (patchMeta.fptr_MidiInHandler)(dev, port, status, data1, data2);
  }
}

// Thread to load a new patch from within a patch

static const char *index_fn = "0:index.axb";

static char loadFName[16];
static WORKING_AREA(waThreadLoader, 1024);
static Thread *pThreadLoader;
static msg_t ThreadLoader(void *arg) {
  (void)arg;
#if CH_USE_REGISTRY
  chRegSetThreadName("loader");
#endif
  while (1) {
    chEvtWaitOne((eventmask_t)1);
    StopPatch();
    if (loadFName[0])
      sdcard_loadPatch(loadFName);
    else {
      FRESULT err;
      FIL f;
      uint32_t bytes_read;
      uint32_t index = *(uint32_t *)(&loadFName[4]);
      err = f_open(&f,index_fn,FA_READ | FA_OPEN_EXISTING);
      if (err) report_fatfs_error(err,index_fn);
      err = f_read(&f, (uint8_t *)PATCHMAINLOC, 0xE000,
                   (void *)&bytes_read);
      if (err != FR_OK) {
        report_fatfs_error(err,index_fn);
        continue;
      }
      err = f_close(&f);
      if (err != FR_OK) {
        report_fatfs_error(err,index_fn);
        continue;
      }
      char *t;
      t = (char *)PATCHMAINLOC;
      uint32_t cindex = 0;

      //LogTextMessage("load %d %d %x",index, bytes_read, t);
      while (bytes_read) {
        //LogTextMessage("scan %d",*t);
        if (cindex == index) {
          //LogTextMessage("match %d",index);
          char *p, *e;
          p = t; e = t;
          while((*e != '\n') && bytes_read){
            e++;
            bytes_read--;
          }
          if (bytes_read) {
            *e = 0;
            sdcard_loadPatch(p);
          }
          goto cont;
        }
        if (*t == '\n'){
          cindex++;
        }
        t++;
        bytes_read--;
      }
cont:
      ;
    }
  }
  return (msg_t)0;
}

void StartLoadPatchTread(void) {
  pThreadLoader = chThdCreateStatic(waThreadLoader, sizeof(waThreadLoader),
  NORMALPRIO, ThreadLoader, NULL);
}

void LoadPatch(const char *name) {
  strcpy(loadFName, name);
  chEvtSignal(pThreadLoader, (eventmask_t)1);
}

void LoadPatchIndexed(uint32_t index) {
  loadFName[0] = 0;
  *(uint32_t *)(&loadFName[4]) = index;
  chEvtSignal(pThreadLoader, (eventmask_t)1);
}
