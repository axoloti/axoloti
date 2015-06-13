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

/* Virtual serial port over USB.*/
SerialUSBDriver SDU1;

void BootLoaderInit(void);

uint32_t fwid;

void InitPConnection(void) {

  extern int32_t _flash_end;
  fwid = CalcCRC32((uint8_t *)(FLASH_BASE_ADDR),
                   (uint32_t)(&_flash_end) & 0x07FFFFF);

  /*
   * Initializes a serial-over-USB CDC driver.
   */
  sduObjectInit(&SDU1);
  sduStart(&SDU1, &serusbcfg);

  /*
   * Activates the USB driver and then the USB bus pull-up on D+.
   * Note, a delay is inserted in order to not have to disconnect the cable
   * after a reset.
   */
  usbDisconnectBus(serusbcfg.usbp);
  chThdSleepMilliseconds(1000);
  usbStart(serusbcfg.usbp, &usbcfg);
  usbConnectBus(serusbcfg.usbp);
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
  chSequentialStreamWrite((BaseSequentialStream * )&SDU1,
                          (const unsigned char* )&patchMeta.pDisplayVector[0],
                          length);
}

void LogTextMessage(const char* format, ...) {
  if (connected ) {
    int h = 0x546F7841; // "AxoT"
    chSequentialStreamWrite((BaseSequentialStream * )&SDU1,
                            (const unsigned char* )&h, 4);

    va_list ap;
    va_start(ap, format);
    chvprintf((BaseSequentialStream * )&SDU1, format, ap);
    va_end(ap);
    chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);
  }
}

void PExTransmit(void) {
  int i;
  if (chOQIsEmptyI(&SDU1.oqueue)) {
    if (AckPending) {
      int ack[7];
      ack[0] = 0x416F7841; // "AxoA"
      ack[1] = GetFirmwareID();
      ack[2] = dspLoadPct;
      ack[3] = patchMeta.patchID;
      ack[4] = *((int*)0x1FFF7A10); // CPU unique ID
      ack[5] = *((int*)0x1FFF7A14); // CPU unique ID
      ack[6] = *((int*)0x1FFF7A18); // CPU unique ID
      chSequentialStreamWrite((BaseSequentialStream * )&SDU1,
                              (const unsigned char* )&ack[0], 7 * 4);

      if (!patchStatus)
        TransmitDisplayPckt();

      connected = 1;
      exception_checkandreport();
      AckPending = 0;
    }
    TransmitLCDoverUSB();
    if (!patchStatus) {
      for (i = 0; i < patchMeta.numPEx; i++) {
        if (patchMeta.pPExch[i].signals & 0x01) {
          int v = (patchMeta.pPExch)[i].value;
          patchMeta.pPExch[i].signals &= ~0x01;
          PExMessage msg;
          msg.header = 0x506F7841; //"AxoP"
          msg.index = i;
          msg.value = v;
          chSequentialStreamWrite((BaseSequentialStream * )&SDU1,
                                  (const unsigned char* )&msg, sizeof(msg));
        }
      }
    }
  }
}

static FRESULT scan_files(char *path) {
  FRESULT res;
  FILINFO fno;
  DIR dir;
  int i;
  char *fn;

#if _USE_LFN
  fno.lfname = 0;
  fno.lfsize = 0;
#endif
  res = f_opendir(&dir, path);
  if (res == FR_OK) {
    i = strlen(path);
    for (;;) {
      res = f_readdir(&dir, &fno);
      if (res != FR_OK || fno.fname[0] == 0)
        break;
      if (fno.fname[0] == '.')
        continue;
      fn = fno.fname;
      if (fno.fattrib & AM_DIR) {
        path[i++] = '/';
        strcpy(&path[i], fn);
        res = scan_files(path);
        if (res != FR_OK)
          break;
        path[--i] = 0;
      }
      else {
        //chprintf(chp, "%s/%s\r\n", path, fn);
        ((char*)fbuff)[0] = 'A';
        ((char*)fbuff)[1] = 'x';
        ((char*)fbuff)[2] = 'o';
        ((char*)fbuff)[3] = 'f';
        fbuff[1] = fno.fsize;
        /*
         strcpy(&((char*)fbuff)[4],path);
         int l = strlen((char *)(&fbuff[0]));
         ((char*)&fbuff[0])[l] = '/';
         strcpy(&((char*)&fbuff[0])[l+1],fn);
         l = strlen((char *)(&fbuff[0]));
         */
        strcpy(&((char*)fbuff)[8], fn);
        int l = strlen((char *)(&fbuff[2]));
        chSequentialStreamWrite((BaseSequentialStream * )&SDU1,
                                (const unsigned char* )fbuff, l + 9);
      }
    }
  }
  return res;
}

void ReadDirectoryListing(void) {
  FATFS *fsp;
  uint32_t clusters;
  FRESULT err;

  err = f_getfree("/", &clusters, &fsp);
  if (err != FR_OK) {
    LogTextMessage("FS: f_getfree() failed\r\n");
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
  chSequentialStreamWrite((BaseSequentialStream * )&SDU1,
                          (const unsigned char* )(&fbuff[0]), 16);
  chThdSleepMilliseconds(10);
  fbuff[0] = 0;
  scan_files((char *)&fbuff[0]);
}

/* input data decoder state machine
 *
 * "AxoP" (int value, int16 index) -> parameter set
 * "AxoR" (int length, data) -> preset data set
 * "AxoW" (int length, int addr, char[length] data) -> data write
 * "Axow" (int length, int offset, char[12] filename, char[length] data) -> data write to sdcard
 * "AxoS" -> start patch
 * "Axos" -> stop patch
 * "AxoT" (char number) -> apply preset
 * "AxoM" (char char char) -> 3 byte midi message
 * "AxoD" go to DFU mode
 * "AxoF" copy patch code to flash (assumes patch is stopped)
 * "Axod" read directory listing
 * "AxoC (int length) (char[] filename)" create and open file on sdcard
 * "Axoc" close file on sdcard
 * "AxoA (int length) (byte[] data)" append data on open file on sdcard
 * "AxoB (int or) (int and)" buttons for virtual Axoloti Control
 */

char FileName[16];

FIL pFile;
int pFileSize;

void CreateFile(void) {
  sdAttemptMountIfUnmounted();
  FRESULT err;
  err = f_open(&pFile, &FileName[0], FA_WRITE | FA_CREATE_ALWAYS);
  if (err != FR_OK) {
    LogTextMessage("File open failed");
  }
  err = f_lseek(&pFile, pFileSize);
  if (err != FR_OK) {
    LogTextMessage("File resize failed");
  }
  err = f_lseek(&pFile, 0);
  if (err != FR_OK) {
    LogTextMessage("File seek failed");
  }
}

void CloseFile(void) {
  FRESULT err;
  err = f_close(&pFile);
  if (err != FR_OK) {
    LogTextMessage("File close failed");
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

void PExReceiveByte(unsigned char c) {
  static char header = 0;
  static int state = 0;
  static int index;
  static int value;
  static int position;
  static int offset;
  static int length;
  static int a;
  static int b;

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
        StartPatch();
        AckPending = 1;
      }
      else if (c == 'p') { // ping
        state = 0;
        header = 0;
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
      state++;
      break;
    case 8:
      index = c;
      state++;
      break;
    case 9:
      index += c << 8;
      state = 0;
      header = 0;
      if (index < patchMeta.numPEx) {
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
          sdAttemptMountIfUnmounted();
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
    default:
      if (c) {
        FileName[state - 8] = c;
        state++;
      }
      else {
        FileName[state - 8] = 0;
        CreateFile();
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
      position = 0x20010000;
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
          err = f_write(&pFile, (char *)0x20010000, length,
                        (void *)&bytes_written);
          if (err != FR_OK) {
            LogTextMessage("File write failed");
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
  else {
    header = 0;
    state = 0;
  }
}

void PExReceive(void) {
  if (!AckPending) {
    unsigned char received;
    while (chnReadTimeout(&SDU1, &received, 1, TIME_IMMEDIATE)) {
      PExReceiveByte(received);
    }
  }
}
