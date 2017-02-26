#include <ch.h>
//#include <stddef.h>
//#include <stdint.h>
#include "axoloti_memory.h"
#include "exceptions.h"
#include "pconnection.h"

static void *sdram_base_addr;
static void *sdram_current_addr;
static void *sdram_last_addr;
static void *sdram_end_addr;
static int32_t sdram_total_remaining;

void sdram_init(char *base_addr, char *end_addr) {
  // assume base-address is a multiple of 4
  sdram_base_addr = base_addr;
  sdram_current_addr = base_addr;
  sdram_end_addr = end_addr;
  sdram_last_addr = 0;
  sdram_total_remaining = end_addr - base_addr;
}

void* sdram_malloc(size_t size) {
  // round up size to a multiple of 4
  if (size & 3)
    size = (size & ~3) + 4;

  if (sdram_current_addr == 0) return 0; // not initialized

  sdram_total_remaining -= size;

  if (sdram_current_addr + size > sdram_end_addr) {
    report_patchLoadSDRamOverflow("m",sdram_total_remaining);
    chThdSleepMilliseconds(200); // get the report out
    return sdram_base_addr; // not available, but return valid area to avoid a memory fault
  }

  sdram_last_addr = sdram_current_addr;
  sdram_current_addr += size;
  return sdram_last_addr;
}

void sdram_free(void *ptr) {
  if (ptr == sdram_last_addr) {
    sdram_total_remaining += sdram_current_addr - sdram_last_addr;
    sdram_current_addr = sdram_last_addr;
  }
}

int32_t sdram_get_free(void) {
  return sdram_total_remaining;
}
