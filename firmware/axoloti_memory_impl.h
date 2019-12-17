#ifndef AXOLOTI_MEMORY_H
#define AXOLOTI_MEMORY_H

/*
 * dynamic memory allocator
 */

#ifdef __cplusplus
extern "C" {
#endif

#include "axoloti_memory.h"

void axoloti_mem_init(void);


int sram1_available(void);
int sram3_available(void);
int ccmram_available(void);
int sdram_available(void);

#ifdef __cplusplus
}
#endif

#endif
