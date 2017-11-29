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
#include "shell.h"
#include "ff.h"
#include "patch.h"
#include "sdcard.h"
#include <string.h>
#include "chprintf.h"
#include "exceptions.h"

/*===========================================================================*/
/* SDCard                                                                    */
/*===========================================================================*/

/* FS object.*/
FATFS SDC_FS;

/* FS mounted and ready.*/
bool_t fs_ready = FALSE;

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

#if 0 // obsolete DFU loader code

typedef struct { // big endian
	uint8_t szSignature[5];
	uint8_t bVersion;
	uint8_t DFUImageSize[4];
	uint8_t bTargets;
} dfu_prefix_t;

typedef struct {
	uint8_t cdDeviceLo;
	uint8_t bcdDeviceHi;
	uint8_t idProductLo;
	uint8_t idProductHi;
	uint8_t idVendorLo;
	uint8_t idVendorHi;
	uint8_t bcdDFULo; // 0x1A
	uint8_t bcdDFUHi; // 0x01
	uint8_t ucDfuSignature[3];
	uint8_t bLength;
	uint8_t dwCRC[4];
} dfu_suffix_t;

typedef struct {
	uint8_t szSignature[6]; // "Target"
	uint8_t bAlternateSetting;
	uint8_t bTargetNamed[4];
	uint8_t szTargetName[255];
	uint8_t szTargetSize[4];
	uint8_t dwNbElements[4];
} dfu_target_t;

typedef struct {
	uint8_t dwElementAddress[4];
	uint8_t dwElementSize[4];
} dfu_image_element_t;


static int dfu_loader_f(FIL *f) {
	  FRESULT err;
	  uint32_t bytes_read;

	  uint8_t *bbuff = (uint8_t *)fbuff;

	  err = f_read(&FileObject, bbuff, sizeof(fbuff),
	               (void *)&bytes_read);
	  if (err != FR_OK) {
	    report_fatfs_error(err,"loader");
	    return -1;
	  }

	  const int dfu_target_offset = 11;
	  const int dfu_1st_element_offset = 285;
	  volatile dfu_prefix_t *pre = (dfu_prefix_t *)bbuff;
	  volatile dfu_target_t *target = (dfu_target_t *)(bbuff + dfu_target_offset);
	  volatile dfu_image_element_t *elem = (dfu_image_element_t *)(bbuff + dfu_1st_element_offset);

	  if (pre->szSignature[0] != 'D' ||
			  pre->szSignature[1] != 'f' ||
			  pre->szSignature[2] != 'u' ||
			  pre->szSignature[3] != 'S' ||
			  pre->szSignature[4] != 'e' ||
			  pre->bVersion != 1
			  ) return -2; // invalid prefix
	  if (target->szSignature[0] != 'T' ||
			  target->szSignature[1] != 'a' ||
			  target->szSignature[2] != 'r' ||
			  target->szSignature[3] != 'g' ||
			  target->szSignature[4] != 'e' ||
			  target->szSignature[5] != 't'
			  ) return -3; // invalid target
	  int nelements = target->dwNbElements[0];
	  // support only up to 255 elements...
	  if (target->dwNbElements[1] != 0 ||
			  target->dwNbElements[2] != 0 ||
			  target->dwNbElements[3] != 0
			  ) return -4;
	  if (!nelements) return -5; // zero elements?

	  // clear target sections for diagnostics
	  memset((char *)0x20000000, 0x66, 64*1024); // sram1
	  memset((char *)0x20020000, 0x66, 64*1024); // sram3
	  memset((char *)0xC0000000, 0x66, 64*1024); // sdram

	  uint8_t *pbuf = bbuff + dfu_1st_element_offset + sizeof(dfu_image_element_t);
	  int remaining_buffer = bytes_read - dfu_1st_element_offset - sizeof(dfu_image_element_t);
	  int fpos = dfu_1st_element_offset + sizeof(dfu_image_element_t);
	  int remaining_length=elem->dwElementSize[0] + (elem->dwElementSize[1]<<8) + (elem->dwElementSize[2]<<16) + (elem->dwElementSize[3]<<24);
	  uint8_t * ptarget_offset = (uint8_t *)(elem->dwElementAddress[0] + (elem->dwElementAddress[1]<<8) + (elem->dwElementAddress[2]<<16) + (elem->dwElementAddress[3]<<24));
	  while (1) {
		  if (remaining_length > remaining_buffer) {
			  memcpy(ptarget_offset, pbuf, remaining_buffer);
			  fpos += remaining_buffer;
			  ptarget_offset += remaining_buffer;
			  remaining_length -= remaining_buffer;
			  // unfinished, read another buffer
			  err = f_read(&FileObject, bbuff, sizeof(fbuff),
			               (void *)&bytes_read);
			  if (bytes_read == 0) chSysHalt("EOF");
			  remaining_buffer = bytes_read;
			  pbuf = bbuff;
		  } else {
			  memcpy(ptarget_offset, pbuf, remaining_length);
			  fpos += remaining_length;
			  // element complete...
			  // scan for next element header
			  nelements--;
			  if (!nelements) break; // done
			  // next elements header is expected at file offset (fpos+remaining_length+pbuf-bbuff)
			  // we seek to that position, but rounded down to a sector size multiple.
			  int elem_pos = fpos;
			  int seek_pos = (elem_pos)&~(_MIN_SS - 1);
			  f_lseek(&FileObject,seek_pos);
			  err = f_read(&FileObject, bbuff, sizeof(fbuff),
			               (void *)&bytes_read);
			  // the elements header is now expected at
			  int element_offset = elem_pos-seek_pos;
			  elem = (dfu_image_element_t *)&bbuff[element_offset];
			  pbuf = bbuff + element_offset + sizeof(dfu_image_element_t);
			  remaining_buffer = bytes_read - element_offset - sizeof(dfu_image_element_t);
			  fpos += sizeof(dfu_image_element_t);
			  remaining_length = elem->dwElementSize[0] + (elem->dwElementSize[1]<<8) + (elem->dwElementSize[2]<<16) + (elem->dwElementSize[3]<<24);
			  ptarget_offset = (uint8_t *)(elem->dwElementAddress[0] + (elem->dwElementAddress[1]<<8) + (elem->dwElementAddress[2]<<16) + (elem->dwElementAddress[3]<<24));
			  if (!((ptarget_offset == (void *)0x0) ||
					  (ptarget_offset == (void *)0x20000000) ||
					  (ptarget_offset == (void *)0x20020000) ||
					  (ptarget_offset == (void *)0xC0000000))) chSysHalt("DFUADDR");
		  }
	  }
	  return 0;
}

int dfu_loader(char *fname) {
	  FRESULT err;
	  StopPatch();
	  err = f_open(&FileObject, fname, FA_READ | FA_OPEN_EXISTING);
	  if (err != FR_OK) {
	    report_fatfs_error(err,fname);
	    return -1;
	  }
	  int err1 = dfu_loader_f(&FileObject);
	  err = f_close(&FileObject);
	  return err1;
}

#endif
