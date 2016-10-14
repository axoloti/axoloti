/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
#include "virtual_control.h"
#include "flash.h"
#include "exceptions.h"
#include "crc32.h"
#include "flash.h"
#include "watchdog.h"
#include "usbcfg.h"
#include "bulk_usb.h"
#include "midi.h"
#include "midi_usb.h"
#include "watchdog.h"
#include "sysmon.h"

//#define DEBUG_SERIAL 1

void BootLoaderInit(void);

uint32_t fwid;

static WORKING_AREA(waThreadUSBDMidi, 256);

__attribute__((noreturn))
    static msg_t ThreadUSBDMidi(void *arg) {
  (void)arg;
#if CH_USE_REGISTRY
  chRegSetThreadName("usbdmidi");
#endif
  uint8_t r[4];
  while (1) {
    chnReadTimeout(&MDU1, &r[0], 4, TIME_INFINITE);
    MidiInMsgHandler(MIDI_DEVICE_USB_DEVICE, ((r[0] & 0xF0) >> 4) + 1, r[1],
                     r[2], r[3]);
  }
}

void InitPConnection(void) {

  extern int32_t _flash_end;
  fwid = CalcCRC32((uint8_t *)(FLASH_BASE_ADDR),
                   (uint32_t)(&_flash_end) & 0x07FFFFF);

  /*
   * Initializes a serial-over-USB CDC driver.
   */
  mduObjectInit(&MDU1);
  mduStart(&MDU1, &midiusbcfg);
  bduObjectInit(&BDU1);
  bduStart(&BDU1, &bulkusbcfg);

  /*
   * Activates the USB driver and then the USB bus pull-up on D+.
   * Note, a delay is inserted in order to not have to disconnect the cable
   * after a reset.
   */
  usbDisconnectBus(midiusbcfg.usbp);
  chThdSleepMilliseconds(1000);
  usbStart(midiusbcfg.usbp, &usbcfg);
  usbConnectBus(midiusbcfg.usbp);

  chThdCreateStatic(waThreadUSBDMidi, sizeof(waThreadUSBDMidi), NORMALPRIO,
                    ThreadUSBDMidi, NULL);
}

int AckPending = 0;
bool connected = 0;

int GetFirmwareID(void) {
  return fwid;
}

void TransmitDisplayPckt(void) {
  if (patchMeta.pDisplayVector == 0)
    return;
  unsigned int length = 12 + (patchMeta.pDisplayVector[2] * 4);
  if (length > 2048)
    return; // FIXME
  chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                          (const unsigned char* )&patchMeta.pDisplayVector[0],
                          length);
}

void LogTextMessage(const char* format, ...) {
  if ((usbGetDriverStateI(BDU1.config->usbp) == USB_ACTIVE) && (connected)) {
    int h = 0x546F7841; // "AxoT"
    chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                            (const unsigned char* )&h, 4);

    va_list ap;
    va_start(ap, format);
    chvprintf((BaseSequentialStream *)&BDU1, format, ap);
    va_end(ap);
    chSequentialStreamPut((BaseSequentialStream * )&BDU1, 0);
  }
}

void PExTransmit(void) {
  if (!chOQIsEmptyI(&BDU1.oqueue)) {
    chThdSleepMilliseconds(1);
    BDU1.oqueue.q_notify(&BDU1.oqueue);
  }
  else {
    if (AckPending) {
      int ack[7];
      ack[0] = 0x416F7841; // "AxoA"
      ack[1] = 0; // reserved
      ack[2] = dspLoadPct;
      ack[3] = patchMeta.patchID;
      ack[4] = sysmon_getVoltage10() + (sysmon_getVoltage50()<<16);
      if (patchStatus) {
        ack[5] = UNINITIALIZED;
      } else {
        ack[5] = loadPatchIndex;
      }
      ack[6] = fs_ready;
      chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                              (const unsigned char* )&ack[0], 7 * 4);

#ifdef DEBUG_SERIAL
      chprintf((BaseSequentialStream * )&SD2,"ack!\r\n");
#endif

      if (!patchStatus)
        TransmitDisplayPckt();

      connected = 1;
      exception_checkandreport();
      AckPending = 0;
    }
    if (!patchStatus) {
      unsigned int i;
      for (i = 0; i < patchMeta.numPEx; i++) {
        if (patchMeta.pPExch[i].signals & 0x01) {
          int v = (patchMeta.pPExch)[i].value;
          patchMeta.pPExch[i].signals &= ~0x01;
          PExMessage msg;
          msg.header = 0x516F7841; //"AxoQ"
          msg.patchID = patchMeta.patchID;
          msg.index = i;
          msg.value = v;
          chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                                  (const unsigned char* )&msg, sizeof(msg));
        }
      }
    }
  }
}

char FileName[256];

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
        chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                                (const unsigned char* )msg, l+14);
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
        chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                                (const unsigned char* )msg, l+13);
      }
    }
  } else {
	  report_fatfs_error(res,0);
  }
  return res;
}

void ReadDirectoryListing(void) {
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
  chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                          (const unsigned char* )(&fbuff[0]), 16);
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
  chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                          (const unsigned char* )msg, 14);
}

/* input data decoder state machine
 *
 * "AxoP" (int value, int16 index) -> parameter set
 * "AxoR" (int length, data) -> preset data set
 * "AxoW" (int length, int addr, char[length] data) -> generic memory write
 * "Axow" (int length, int offset, char[12] filename, char[length] data) -> data write to sdcard
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
 * "AxoB (int or) (int and)" buttons for virtual Axoloti Control
 */

FIL pFile;
int pFileSize;

void ManipulateFile(void) {
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
      FILINFO fno;
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
      if (err == FR_OK) {
        char *msg = &((char*)fbuff)[0];
        msg[0] = 'A';
        msg[1] = 'x';
        msg[2] = 'o';
        msg[3] = 'f';
        *(int32_t *)(&msg[4]) = fno.fsize;
        *(int32_t *)(&msg[8]) = fno.fdate + (fno.ftime<<16);
        strcpy(&msg[12], &FileName[6]);
        int l = strlen(&msg[12]);
        chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                                (const unsigned char* )msg, l+13);
      }
    }
  }
}

void CloseFile(void) {
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

void CopyPatchToFlash(void) {
  flash_unlock();
  flash_Erase_sector(11);
  int src_addr = PATCHMAINLOC;
  int flash_addr = PATCHFLASHLOC;
  int c;
  for (c = 0; c < PATCHFLASHSIZE;) {
    flash_ProgramWord(flash_addr, *(int32_t *)src_addr);
    src_addr += 4;
    flash_addr += 4;
    c += 4;
  }
  // verify
  src_addr = PATCHMAINLOC;
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
    while (1) {
      // flash verify fail
    }
  }
  AckPending = 1;
}

void ReplyFWVersion(void) {
  uint8_t reply[16];
  reply[0] = 'A';
  reply[1] = 'x';
  reply[2] = 'o';
  reply[3] = 'V';
  reply[4] = FWVERSION1; // major
  reply[5] = FWVERSION2; // minor
  reply[6] = FWVERSION3;
  reply[7] = FWVERSION4;
  uint32_t fwid = GetFirmwareID();
  reply[8] = (uint8_t)(fwid>>24);
  reply[9] = (uint8_t)(fwid>>16);
  reply[10] = (uint8_t)(fwid>>8);
  reply[11] = (uint8_t)(fwid);
  reply[12] = (uint8_t)(PATCHMAINLOC>>24);
  reply[13] = (uint8_t)(PATCHMAINLOC>>16);
  reply[14] = (uint8_t)(PATCHMAINLOC>>8);
  reply[15] = (uint8_t)(PATCHMAINLOC);
  chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                          (const unsigned char* )(&reply[0]), 16);
}

void PExReceiveByte(unsigned char c) {
  static char header = 0;
  static int state = 0;
  static unsigned int index;
  static int value;
  static int position;
  static int offset;
  static int length;
  static int a;
  static int b;
  static uint32_t patchid;

  if (!header) {
    switch (state) {
    case 0:
      if (c == 'A')
        state++;
      break;
    case 1:
      if (c == 'x')
        state++;
      else
        state = 0;
      break;
    case 2:
      if (c == 'o')
        state++;
      else
        state = 0;
      break;
    case 3:
      header = c;
      if (c == 'P') { // param change
        state = 4;
      }
      else if (c == 'R') { // preset change
        state = 4;
      }
      else if (c == 'W') { // write
        state = 4;
      }
      else if (c == 'w') { // write file to SD
        state = 4;
      }
      else if (c == 'T') { // change preset
        state = 4;
      }
      else if (c == 'M') { // midi command
        state = 4;
      }
      else if (c == 'B') { // virtual Axoloti Control buttons
        state = 4;
      }
      else if (c == 'C') { // create sdcard file
        state = 4;
      }
      else if (c == 'A') { // append data to sdcard file
        state = 4;
      }
      else if (c == 'r') { // generic read
        state = 4;
      }
      else if (c == 'y') { // generic read
        state = 4;
      }
      else if (c == 'S') { // stop patch
        state = 0;
        header = 0;
        StopPatch();
        AckPending = 1;
      }
      else if (c == 'D') { // go to DFU mode
        state = 0;
        header = 0;
        StopPatch();
        exception_initiate_dfu();
      }
      else if (c == 'F') { // copy to flash
        state = 0;
        header = 0;
        StopPatch();
        CopyPatchToFlash();
      }
      else if (c == 'd') { // read directory listing
        state = 0;
        header = 0;
        StopPatch();
        ReadDirectoryListing();
      }
      else if (c == 's') { // start patch
        state = 0;
        header = 0;
        loadPatchIndex = LIVE;
        StartPatch();
        AckPending = 1;
      }
      else if (c == 'V') { // FW version number
        state = 0;
        header = 0;
        ReplyFWVersion();
        AckPending = 1;
      }
      else if (c == 'p') { // ping
        state = 0;
        header = 0;
#ifdef DEBUG_SERIAL
        chprintf((BaseSequentialStream * )&SD2,"ping\r\n");
#endif
        AckPending = 1;
      }
      else if (c == 'c') { // close sdcard file
        state = 0;
        header = 0;
        CloseFile();
        AckPending = 1;
      }
      else
        state = 0;
      break;
    }
  }
  else if (header == 'P') { // param change
    switch (state) {
    case 4:
      patchid = c;
      state++;
      break;
    case 5:
      patchid += c << 8;
      state++;
      break;
    case 6:
      patchid += c << 16;
      state++;
      break;
    case 7:
      patchid += c << 24;
      state++;
      break;
    case 8:
      value = c;
      state++;
      break;
    case 9:
      value += c << 8;
      state++;
      break;
    case 10:
      value += c << 16;
      state++;
      break;
    case 11:
      value += c << 24;
      state++;
      break;
    case 12:
      index = c;
      state++;
      break;
    case 13:
      index += c << 8;
      state = 0;
      header = 0;
      if ((patchid == patchMeta.patchID) &&
          (index < patchMeta.numPEx)) {
        PExParameterChange(&(patchMeta.pPExch)[index], value, 0xFFFFFFEE);
      }
      break;
    default:
      state = 0;
      header = 0;
    }
  }
  else if (header == 'W') {
    switch (state) {
    case 4:
      offset = c;
      state++;
      break;
    case 5:
      offset += c << 8;
      state++;
      break;
    case 6:
      offset += c << 16;
      state++;
      break;
    case 7:
      offset += c << 24;
      state++;
      break;
    case 8:
      value = c;
      state++;
      break;
    case 9:
      value += c << 8;
      state++;
      break;
    case 10:
      value += c << 16;
      state++;
      break;
    case 11:
      value += c << 24;
      state++;
      break;
    default:
      if (value > 0) {
        value--;
        *((unsigned char *)offset) = c;
        offset++;
        if (value == 0) {
          header = 0;
          state = 0;
          AckPending = 1;
        }
      }
      else {
        header = 0;
        state = 0;
        AckPending = 1;
      }
    }
  }
  else if (header == 'w') {
    switch (state) {
    case 4:
      offset = c;
      state++;
      break;
    case 5:
      offset += c << 8;
      state++;
      break;
    case 6:
      offset += c << 16;
      state++;
      break;
    case 7:
      offset += c << 24;
      state++;
      break;
    case 8:
      value = c;
      state++;
      break;
    case 9:
      value += c << 8;
      state++;
      break;
    case 10:
      value += c << 16;
      state++;
      break;
    case 11:
      value += c << 24;
      length = value;
      position = offset;
      state++;
      break;
    case 12:
    case 13:
    case 14:
    case 15:
    case 16:
    case 17:
    case 18:
    case 19:
    case 20:
    case 21:
    case 22:
    case 23:
      FileName[state - 12] = c;
      state++;
      break;
    default:
      if (value > 0) {
        value--;
        *((unsigned char *)position) = c;
        position++;
        if (value == 0) {
          FRESULT err;
          header = 0;
          state = 0;
          sdcard_attemptMountIfUnmounted();
          err = f_open(&pFile, &FileName[0], FA_WRITE | FA_CREATE_ALWAYS);
          if (err != FR_OK) {
            LogTextMessage("File open failed");
          }
          int bytes_written;
          err = f_write(&pFile, (char *)offset, length, (void *)&bytes_written);
          if (err != FR_OK) {
            LogTextMessage("File write failed");
          }
          err = f_close(&pFile);
          if (err != FR_OK) {
            LogTextMessage("File close failed");
          }
          AckPending = 1;
        }
      }
      else {
        header = 0;
        state = 0;
      }
    }
  }
  else if (header == 'T') { // Apply Preset
    ApplyPreset(c);
    AckPending = 1;
    header = 0;
    state = 0;
  }
  else if (header == 'M') { // Midi message
    static uint8_t midi_r[3];
    switch (state) {
    case 4:
      midi_r[0] = c;
      state++;
      break;
    case 5:
      midi_r[1] = c;
      state++;
      break;
    case 6:
      midi_r[2] = c;
      MidiInMsgHandler(MIDI_DEVICE_INTERNAL, 1, midi_r[0], midi_r[1],
                       midi_r[2]);
      header = 0;
      state = 0;
      break;
    default:
      header = 0;
      state = 0;
    }
  }
  else if (header == 'C') {
    switch (state) {
    case 4:
      pFileSize = c;
      state++;
      break;
    case 5:
      pFileSize += c << 8;
      state++;
      break;
    case 6:
      pFileSize += c << 16;
      state++;
      break;
    case 7:
      pFileSize += c << 24;
      state++;
      break;
    case 8:
      FileName[state - 8] = c;
      // filename starting with null means there are attributes present
      state++;
      break;
    default:
      if (c || ((!FileName[0])&&(state<14))) {
        FileName[state - 8] = c;
        state++;
      }
      else {
        FileName[state - 8] = 0;
        ManipulateFile();
        header = 0;
        state = 0;
        AckPending = 1;
      }
    }
  }
  else if (header == 'A') {
    switch (state) {
    case 4:
      value = c;
      state++;
      break;
    case 5:
      value += c << 8;
      state++;
      break;
    case 6:
      value += c << 16;
      state++;
      break;
    case 7:
      value += c << 24;
      length = value;
      position = PATCHMAINLOC;
      state++;
      break;
    default:
      if (value > 0) {
        value--;
        *((unsigned char *)position) = c;
        position++;
        if (value == 0) {
          FRESULT err;
          header = 0;
          state = 0;
          int bytes_written;
          err = f_write(&pFile, (char *)PATCHMAINLOC, length,
                        (void *)&bytes_written);
          if (err != FR_OK) {
            report_fatfs_error(err,0);
          }
          AckPending = 1;
        }
      }
      else {
        header = 0;
        state = 0;
      }
    }
  }
  else if (header == 'B') {
    switch (state) {
    case 4:
      a = c;
      state++;
      break;
    case 5:
      a += c << 8;
      state++;
      break;
    case 6:
      a += c << 16;
      state++;
      break;
    case 7:
      a += c << 24;
      state++;
      break;
    case 8:
      b = c;
      state++;
      break;
    case 9:
      b += c << 8;
      state++;
      break;
    case 10:
      b += c << 16;
      state++;
      break;
    case 11:
      b += c << 24;
      state++;
      break;
    case 12:
      EncBuffer[0] += c;
      state++;
      break;
    case 13:
      EncBuffer[1] += c;
      state++;
      break;
    case 14:
      EncBuffer[2] += c;
      state++;
      break;
    case 15:
      EncBuffer[3] += c;
      header = 0;
      state = 0;
      Btn_Nav_Or.word = Btn_Nav_Or.word | a;
      Btn_Nav_And.word = Btn_Nav_And.word & b;
      break;
    }
  }
  else if (header == 'R') {
    switch (state) {
    case 4:
      length = c;
      state++;
      break;
    case 5:
      length += c << 8;
      state++;
      break;
    case 6:
      length += c << 16;
      state++;
      break;
    case 7:
      length += c << 24;
      state++;
      offset = (int)patchMeta.pPresets;
      break;
    default:
      if (length > 0) {
        length--;
        if (offset) {
          *((unsigned char *)offset) = c;
          offset++;
        }
        if (length == 0) {
          header = 0;
          state = 0;
          AckPending = 1;
        }
      }
      else {
        header = 0;
        state = 0;
        AckPending = 1;
      }
    }
  }
  else if (header == 'r') { // generic read
    switch (state) {
    case 4:
      offset = c;
      state++;
      break;
    case 5:
      offset += c << 8;
      state++;
      break;
    case 6:
      offset += c << 16;
      state++;
      break;
    case 7:
      offset += c << 24;
      state++;
      break;
    case 8:
      value = c;
      state++;
      break;
    case 9:
      value += c << 8;
      state++;
      break;
    case 10:
      value += c << 16;
      state++;
      break;
    case 11:
      value += c << 24;

      uint32_t read_repy_header[3];
      ((char*)read_repy_header)[0] = 'A';
      ((char*)read_repy_header)[1] = 'x';
      ((char*)read_repy_header)[2] = 'o';
      ((char*)read_repy_header)[3] = 'r';
      read_repy_header[1] = offset;
      read_repy_header[2] = value;
      chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                              (const unsigned char* )(&read_repy_header[0]), 3 * 4);
      chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                              (const unsigned char* )(offset), value);

      AckPending = true;
      header = 0;
      state = 0;
      break;
    }
  }
  else if (header == 'y') { // generic read, 32bit
    switch (state) {
    case 4:
      offset = c;
      state++;
      break;
    case 5:
      offset += c << 8;
      state++;
      break;
    case 6:
      offset += c << 16;
      state++;
      break;
    case 7:
      offset += c << 24;

      uint32_t read_repy_header[3];
      ((char*)read_repy_header)[0] = 'A';
      ((char*)read_repy_header)[1] = 'x';
      ((char*)read_repy_header)[2] = 'o';
      ((char*)read_repy_header)[3] = 'y';
      read_repy_header[1] = offset;
      read_repy_header[2] = *((uint32_t*)offset);
      chSequentialStreamWrite((BaseSequentialStream * )&BDU1,
                              (const unsigned char* )(&read_repy_header[0]), 3 * 4);

      AckPending = true;
      header = 0;
      state = 0;
      break;
    }
  }  else {
    header = 0;
    state = 0;
  }
}

void PExReceive(void) {
  if (!AckPending) {
    unsigned char received;
    while (chnReadTimeout(&BDU1, &received, 1, TIME_IMMEDIATE)) {
      PExReceiveByte(received);
    }
  }
}

/*
 void USBDMidiPoll(void) {
 uint8_t r[4];
 while (chnReadTimeout(&MDU1, &r, 4, TIME_IMMEDIATE)) {
 MidiInMsgHandler(MIDI_DEVICE_USB_DEVICE, (( r[0] & 0xF0) >> 4)+ 1, r[1], r[2], r[3]);
 }
 }
 */

