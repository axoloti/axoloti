#ifndef LOADER_CONFIG_H_
#define LOADER_CONFIG_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "ch.h"
#include <ff.h>

#include "logging.h"
#include "axoloti_memory.h"
#include "exports.h"

#define LOADER_MAX_SYM_LENGTH 33

static void arch_jumpTo(entry_t entry) {
  entry();
}

static int is_streq(const char *s1, const char *s2) {
  while(*s1 && *s2) {
    if (*s1 != *s2)
      return 0;
    s1++;
    s2++;
  }
  return *s1 == *s2;
}

static void *do_alloc(size_t size, size_t align, ELFSecPerm_t perm) {
  if (perm & ELF_SEC_EXEC) {
    return ax_malloc_align(size, mem_type_can_execute, align);
  } else {
    return ax_malloc_align(size, 0, align);
  }
}

static void *do_alloc_sdram(size_t size, size_t align, ELFSecPerm_t perm) {
    return ax_malloc_align(size, mem_type_hint_large, align);
}

static void loader_close(userdata_t userdata) {
  extern void closeReferencedLibs(userdata_t * userdata);
  closeReferencedLibs(&userdata);
  userdata.vfile_ops->vf_close(userdata.fd);
}

#if 0 // semihosting, dead code
#define LOADER_FD_T int
#define LOADER_OPEN_FOR_RD(path) _open(path, O_RDONLY)
#define LOADER_FD_VALID(fd) (fd != -1)
#define LOADER_READ(fd, buffer, size) _read(fd, buffer, size)
#define LOADER_WRITE(fd, buffer, size) _write(fd, buffer, size)
#define LOADER_CLOSE(fd) _close(fd)
#define LOADER_SEEK_FROM_START(fd, off) (_lseek(fd, off, SEEK_SET) == -1)
#define LOADER_TELL(fd) _lseek(fd, 0, SEEK_CUR)
#endif

#define LOADER_OPEN_FOR_RD(userdata,path) userdata.fd = (FIL *)(userdata.vfile_ops->vf_open(path))
#define LOADER_FD_VALID(userdata) (userdata.fd != NULL)
#define LOADER_READ(userdata, buffer, size) userdata.vfile_ops->vf_read(userdata.fd, buffer, size)
#define LOADER_CLOSE(userdata) loader_close(userdata)
#define LOADER_SEEK_FROM_START(userdata, off) userdata.vfile_ops->vf_seek(userdata.fd, off)
#define LOADER_TELL(userdata) userdata.vfile_ops->vf_tell(userdata.fd)
#define LOADER_ALIGN_ALLOC(size, align, perm) do_alloc(size, align, perm)
#define LOADER_ALIGN_ALLOC_SDRAM(size, align, perm) do_alloc_sdram(size, align, perm)
#define LOADER_FREE(ptr) ax_free(ptr)
#define LOADER_STREQ(s1, s2) (is_streq(s1, s2))
#define LOADER_JUMP_TO(entry) arch_jumpTo(entry)

#define DBG(...)
#define ERR(...) LogTextMessage("ELF:err: " __VA_ARGS__); chThdSleepMilliseconds(10)
#define MSG(msg)
//#define MSG(msg) LogTextMessage("ELF:msg: " msg)

#define LOADER_GETUNDEFSYMADDR(userdata, name) getUndefinedSymbol(userdata, name)

#endif /* LOADER_CONFIG_H_ */
