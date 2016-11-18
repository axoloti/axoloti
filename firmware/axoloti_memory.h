#ifndef AXOLOTI_MEMORY_H
#define AXOLOTI_MEMORY_H

/*
 * pseudo dynamic memory allocator for sdram used in patches
 * can only free the last allocated memory
 * it is recommended to only use this only during patch object initialization
 */

#ifdef __cplusplus
extern "C" {
#endif


/*
 * sdram_init initializes the bounds of the allocator
 * called in patch startup
 */
void sdram_init(char *base_addr, char *end_addr);

/*
 * sdram_malloc allocates a segment of memory
 */
void* sdram_malloc(size_t size);

/*
 * sdram_free frees a slice of allocated memory
 * can only free the last allocated segment!
 */
void sdram_free(void *ptr);

/*
 * returns size available, <0 when overflow happened
 */
int32_t sdram_get_free(void);

#ifdef __cplusplus
}
#endif

#endif
