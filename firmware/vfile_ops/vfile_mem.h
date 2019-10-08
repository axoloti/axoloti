#ifndef VFILE_MEM_H
#define VFILE_MEM_H

/*
 * Virtual file operations for a memory mapped region.
 * mem_fopen() takes a memory address rather than a filename
 * mem_close() frees the memory,
 * so the memory address is supposed to be obtained from ax_malloc(...)
 */

extern const vfile_ops_t vfile_ops_mem;


/*
 * Virtual file operations for a write-only memory mapped region.
 * mem_fopen() takes a memory address rather than a filename
 * mem_close() does nothing,
 */

extern const vfile_ops_t vfile_ops_flash;

#endif
