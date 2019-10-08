#include "mcuconf.h"
#include "ch.h"
#include "sdram.h"
#include "axoloti_memory.h"

static char sram1_heap_data[60*1024]  __attribute__ ((section (".ram0")));
static char ccm_heap_data[48*1024]  CCM;
static char sram3_heap_data[64*1024] SRAM3;

static memory_heap_t sdram_heap;
static memory_heap_t sram1_heap;
static memory_heap_t sram3_heap;
static memory_heap_t ccm_heap;

void axoloti_mem_init(void) {
//  chHeapObjectInit(&sram1_heap, (void *)(0x0FFFFFFF /*aliased*/ & (int)sram1_heap_data), sizeof(sram1_heap_data));
  chHeapObjectInit(&sram1_heap, (void *)sram1_heap_data, sizeof(sram1_heap_data));
  chHeapObjectInit(&sram3_heap, (void *)sram3_heap_data, sizeof(sram3_heap_data));
  chHeapObjectInit(&sdram_heap, (void *)SDRAM_BANK_ADDR, 8*1024*1024);
  chHeapObjectInit(&ccm_heap, (void *)ccm_heap_data, sizeof(ccm_heap_data));
}

int sram1_available(void) {
  size_t sram1_available, sram1_largestp;
  chHeapStatus(&sram1_heap, &sram1_available, &sram1_largestp);
  return sram1_available;
}

int sram3_available(void) {
  size_t sram3_available, sram3_largestp;
  chHeapStatus(&sram3_heap, &sram3_available, &sram3_largestp);
  return sram3_available;
}

int ccmram_available(void) {
  size_t ccm_available, ccm_largestp;
  chHeapStatus(&ccm_heap, &ccm_available, &ccm_largestp);
  return ccm_available;
}

int sdram_available(void) {
  size_t sdram_available, sdram_largestp;
  chHeapStatus(&sdram_heap, &sdram_available, &sdram_largestp);
  return sdram_available;
}

void* ax_malloc_align(size_t size, mem_type_flags_t mem_type, unsigned align) {
  if (size == 0) {
      size = 4;
      //chHeapAlloc does not like 0-size allocations
  }
  if (mem_type & mem_type_can_execute) {
    return chHeapAllocAligned(&sram1_heap, size, align);
  }
  if (mem_type & mem_type_hint_large) {
    return chHeapAllocAligned(&sdram_heap, size, align);
  }
//  if (mem_type & mem_type_hint_tiny_dma) {
//    return chHeapAllocAligned(&sram2_heap, size, align);
//  }
  if (mem_type & mem_type_hint_no_dma) {
    return chHeapAllocAligned(&ccm_heap, size, align);
  }
  return chHeapAllocAligned(&sram3_heap, size, align);
}

void ax_free(void *ptr) {
    chHeapFree(ptr);
}
