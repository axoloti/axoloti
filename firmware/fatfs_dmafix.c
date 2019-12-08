#include "fatfs_dmafix.h"
#include "../api/logging.h"

// perhaps this issue is better tackled in DMA setup?

/*
 * f_read1
 * buffers pointing to sram1 remapped to 0x00000000-0x00ffffff do not support DMA
 * so we need to use the non-remapped address
 */
FRESULT f_read1 (FIL* fp, void* buff, UINT btr, UINT* br) {
	if (((int)buff & 0xFF000000) == 0) {
		buff = (void*)(0x20000000 | (int)buff);
	}
	// LogTextMessage("fp %08X, b %08X", fp, buff);
	return f_read(fp, buff, btr, br);
}

/*
 * f_write1
 * buffers pointing to sram1 remapped to 0x00000000-0x00ffffff do not support DMA
 * so we need to use the non-remapped address
 */
FRESULT f_write1 (FIL* fp, const void* buff, UINT btw, UINT* bw) {
	if (((int)buff & 0xFF000000) == 0) {
		buff = (void*)(0x20000000 | (int)buff);
	}
	return f_write(fp, buff, btw, bw);
}
