#ifndef EXPORTS_LIBC_EXPORTS_H
#define EXPORTS_LIBC_EXPORTS_H

#include <string.h>

/*
 * To avoid including an implementation of memset and memcpy in every elf,
 * they can link to these wrapper symbol by using LD flags
 *   --wrap memset
 *   --wrap memcpy
 * This saves ~456 bytes of binary code...
 */

static void * __wrap_memset(void *str, int c, size_t n) {
  return memset(str,c,n);
}
static void * __wrap_memcpy(void *str1, const void *str2, size_t n) {
  return memcpy(str1,str2,n);
}

#define EXPORTS_LIBC_SYMBOLS \
  SYM(__wrap_memset), \
  SYM(__wrap_memcpy)

#endif
