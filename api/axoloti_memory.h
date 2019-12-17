#ifndef API_AXOLOTI_MEMORY_H
#define API_AXOLOTI_MEMORY_H

#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {

  mem_type_can_execute = (1<<2),

  mem_type_hint_no_dma = (1<<16),
  mem_type_hint_large = (1<<17),
  mem_type_hint_tiny_dma = (1<<18),

} mem_type_flags_t;

void* ax_malloc_align(size_t size, mem_type_flags_t mem_type, unsigned align);
static inline void* ax_malloc(size_t size, mem_type_flags_t mem_type) {
  return ax_malloc_align(size, mem_type, 8);
}
void ax_free(void *ptr);

// legacy wrapper
#define sdram_malloc(X) ax_malloc(X, mem_type_hint_large)

// for statically allocated memory, use these macros:
#define SECTION_SDRAM_DATA __attribute__ ((section ( ".sdram_data" )))
#define SECTION_SDRAM_BSS __attribute__ ((section ( ".sdram_bss" )))
#define SECTION_DMADATA __attribute__ ((section ( ".sram2" )))

#ifdef __cplusplus
} // extern "C"
#endif

#endif
