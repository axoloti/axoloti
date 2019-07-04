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
#include "chprintf.h"
#include "ff.h"
#include "patch.h"
#include "sdcard.h"
#include <string.h>
#include "chprintf.h"
#include "exceptions.h"
#include "patch_name.h"

/*===========================================================================*/
/* SDCard                                                                    */
/*===========================================================================*/

/* FS object.*/
FATFS SDC_FS;

/* FS mounted and ready.*/
bool fs_ready = FALSE;

/*
 * Card insertion event.
 */
static void InsertHandler(eventid_t id) {
  FRESULT err;

  (void)id;
  /*
   * On insertion SDC initialization and FS mount.
   */
  if (fs_ready) {
    sdcDisconnect(&SDCD1);
    fs_ready = FALSE;
  }

  if (sdcConnect(&SDCD1))
    return;

  err = f_mount(&SDC_FS, "/", 1);
  if (err != FR_OK) {
    sdcDisconnect(&SDCD1);
    return;
  }
  fs_ready = TRUE;
}

/*
 * Card removal event.
 */
static void RemoveHandler(eventid_t id) {

  (void)id;
  sdcDisconnect(&SDCD1);
  fs_ready = FALSE;
}

void sdcard_init(void) {
  /*
   static const evhandler_t evhndl[] = {
   InsertHandler,
   RemoveHandler
   };
   struct EventListener el0, el1;
   */
  palSetPadMode(GPIOC, 8, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 9, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 10, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 11, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 12, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOD, 2, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  chThdSleepMicroseconds(1);
  sdcStart(&SDCD1, NULL);
  chThdSleepMilliseconds(50);

  InsertHandler(0);

  FRESULT err;
  uint32_t clusters;
  FATFS *fsp;

  err = f_getfree("/", &clusters, &fsp);
  int retries = 3;
  while (err != FR_OK) {
    InsertHandler(0);
    chThdSleepMilliseconds(20);
    err = f_getfree("/", &clusters, &fsp);
    chThdSleepMilliseconds(50);
    retries--;
    if (!retries)
      break;
  }
}

void sdcard_attemptMountIfUnmounted() {
  if (fs_ready)
    return;
  InsertHandler(0);
}

void sdcard_unmount(void){
  RemoveHandler(0);
}

/* Generic large buffer.*/
uint32_t fbuff[256] DMA_MEM_FW;


typedef struct {
	uint32_t fourcc;
	uint32_t chunk_size;
	uint32_t segment_source_offset;
	uint32_t segment_dest_addr;
	uint32_t segment_length;
} bin_segment_t;

typedef struct {
	uint32_t fourcc; // "AxMS": Axoloti Memory Segments
	uint32_t length; // = 12 + 5 * nsegments
	uint32_t arch;
	uint32_t version;
	uint32_t nsegments;
	bin_segment_t segments[0]; // nsegments...
} bin_header_t;

#define MAX_SEGMENTS 3
uint8_t patch_header[sizeof(bin_header_t) + MAX_SEGMENTS*sizeof(bin_segment_t)] DMA_MEM_FW;

int bin_loader_flash(uint8_t *startloc, uint32_t size) {
	bin_header_t *pre = (bin_header_t *) startloc;
	bin_segment_t *segment = (bin_segment_t *) (&pre->segments);

	// TODO: check signature...

	// support only up to 10 segments...
	if (pre->nsegments > 10)
		return -4;
	if (!pre->nsegments)
		return -5; // zero elements?

	// clear target segments for diagnostics
	memset((char *) 0x20000000, 0x66, 64 * 1024); // sram1
	memset((char *) 0x20020000, 0x66, 64 * 1024); // sram3
	memset((char *) 0xC0000000, 0x66, 64 * 1024); // sdram

	memcpy(patch_header, startloc, sizeof(patch_header));

	unsigned int i;
	// TODO: parse chunks cfr. readchunk_patch_root
	for (i = 0; i < pre->nsegments; i++) {
		uint32_t segment_length = segment->segment_length;
		uint8_t * segment_dest_addr = (uint8_t *) segment->segment_dest_addr;
		uint8_t * segment_src = segment->segment_source_offset + startloc;
		// TODO: validate addr/size by region
		if (!((segment_dest_addr == (void *) 0x0)
				|| (segment_dest_addr == (void *) 0x20000000)
				|| (segment_dest_addr == (void *) 0x20020000)
				|| (segment_dest_addr == (void *) 0xC0000000)))
			chSysHalt("LDRADDR");
		memcpy(segment_dest_addr, segment_src, segment_length);
		segment++;
	}
	return 0;
}

static int bin_writer_f(FIL *FileObject) {
	FRESULT err;
	UINT bytes_written;
	bin_header_t *pre = (bin_header_t *) patch_header;
	err = f_write(FileObject, patch_header, sizeof(patch_header), &bytes_written);
	if (err != FR_OK) {
		report_fatfs_error(err, "writer");
		return -1;
	}
	unsigned int i;
	for (i = 0; i < pre->nsegments; i++) {
		err = f_write(FileObject, (uint8_t *)pre->segments[i].segment_dest_addr, pre->segments[i].segment_length, &bytes_written);
		if (err != FR_OK) {
			report_fatfs_error(err, "writer");
			return -1;
		}
	}
	return 0;
}

static int bin_loader_f(FIL *FileObject) {
	FRESULT err;
	uint32_t bytes_read;

	err = f_read(FileObject, patch_header, sizeof(fbuff), (void *) &bytes_read);
	if (err != FR_OK) {
		report_fatfs_error(err, "loader");
		return -1;
	}
	bin_header_t *pre = (bin_header_t *) patch_header;
	bin_segment_t *segment = (bin_segment_t *) (&pre->segments);

	// TODO: check signature...

	// support only up to 10 segments...
	if (pre->nsegments > 10)
		return -4;
	if (!pre->nsegments)
		return -5; // zero segments?

	// clear target segments for diagnostics
	memset((char *) 0x20000000, 0x66, 64 * 1024); // sram1
	memset((char *) 0x20020000, 0x66, 64 * 1024); // sram3
	memset((char *) 0xC0000000, 0x66, 64 * 1024); // sdram

	unsigned int i;
	// TODO: parse chunks cfr. readchunk_patch_root
	for (i = 0; i < pre->nsegments; i++) {
		uint32_t segment_length = segment->segment_length;
		uint8_t * segment_dest_addr = (uint8_t *) segment->segment_dest_addr;
		uint32_t segment_src = segment->segment_source_offset;
		// TODO: validate addr/size by region
		if (!((segment_dest_addr == (void *) 0x0)
				|| (segment_dest_addr == (void *) 0x20000000)
				|| (segment_dest_addr == (void *) 0x20020000)
				|| (segment_dest_addr == (void *) 0xC0000000)))
			chSysHalt("LDRADDR");
		f_lseek(FileObject, segment_src);
		if (err != FR_OK) {
			report_fatfs_error(err, "fseek");
			return -1;
		}
		err = f_read(FileObject, segment_dest_addr, segment_length,
				(void *) &bytes_read);
		if (err != FR_OK) {
			report_fatfs_error(err, "f_read");
			return -1;
		}
		if (bytes_read != segment_length)
			chSysHalt("LDRFILE");
		segment++;
	}
	return 0;
}

int sdcard_bin_writer(char *fname) {
	// TODO: avoid changing directory
	  FIL FileObject;
	  FRESULT err;
	  StopPatch();
      err = f_chdir("/");
	  if (err != FR_OK) {
	    report_fatfs_error(err,"chdir1");
	    return -1;
	  }
	  err = f_mkdir(fname);
	  if ((err != FR_OK) && (err != FR_EXIST)) {
	    report_fatfs_error(err,"mkdir1");
	    return -1;
	  }
      err = f_chdir(fname);
	  if (err != FR_OK) {
	    report_fatfs_error(err,"chdir2");
	    return -1;
	  }
	  err = f_open(&FileObject, "patch.bin", FA_WRITE | FA_CREATE_ALWAYS);
	  if (err != FR_OK) {
	    report_fatfs_error(err,fname);
	    return -1;
	  }
	  int err1 = bin_writer_f(&FileObject);
	  err = f_close(&FileObject);
	  return err1;
}

int sdcard_bin_loader(char *fname) {
	  FIL FileObject;
	  FRESULT err;
	  StopPatch();
	  err = f_open(&FileObject, fname, FA_READ | FA_OPEN_EXISTING);
	  if (err != FR_OK) {
	    *patch_name = 0;
	    report_fatfs_error(err,fname);
	    return -1;
	  }
	  int err1 = bin_loader_f(&FileObject);
	  err = f_close(&FileObject);
	  return err1;
}

int sdcard_loadPatch1(char *fname) {
  FRESULT err;

  StopPatch();
  strncpy(patch_name, fname, sizeof(patch_name)-1);
//  LogTextMessage("load %s",fname);

  // change working directory

  int i=0;
  for(i=strlen(fname);i;i--){
    if (fname[i]=='/')
      break;
  }
  if (i>0) {
    fname[i]=0;
//    LogTextMessage("chdir %s",fname);
    err = f_chdir("/");
    if (err != FR_OK) {
      report_fatfs_error(err,fname);
      return -1;
    }
    err = f_chdir(fname);
    if (err != FR_OK) {
      report_fatfs_error(err,fname);
      return -1;
    }
    fname = &fname[i+1];
  } else {
    f_chdir("/");
  }
  int err1 = sdcard_bin_loader(fname);
  if (err1) return err1;

  chThdSleepMilliseconds(10);
  return 0;
}

