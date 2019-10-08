#include "vfile_ops.h"
#include "vfile_fatfs.h"
#include "axoloti_memory.h"

typedef struct {
  char * base_addr;
  char * fp;
} mem_fd_t;

static filehandle mem_fopen(const char * path) {
  mem_fd_t *f = ax_malloc(sizeof(mem_fd_t), 0);
  if (!f) return 0;
  char * base_addr = (char *)path;
  f->base_addr = base_addr;
  f->fp = base_addr;
  return (filehandle)f;
}

static void mem_fclose(filehandle f) {
  mem_fd_t * fd = (mem_fd_t *)f;
  ax_free(fd->base_addr);
}

static void mem_fclose_flash(filehandle f) {
}

static int mem_fread(filehandle f, void * buf, int size) {
  mem_fd_t * fd = (mem_fd_t *)f;
  int i;
  char * cbuf = (char *)buf;
  for(i=0;i<size;i++) {
    *cbuf++ = *fd->fp++;
  }
  return size;
}

static int mem_fseek(filehandle f, int pos) {
  mem_fd_t * fd = (mem_fd_t *)f;
  fd->fp = fd->base_addr + pos;
  return 0;
}

static int mem_ftell(filehandle f) {
  mem_fd_t * fd = (mem_fd_t *)f;
  return fd->fp - fd->base_addr;
}

const vfile_ops_t vfile_ops_mem = {
    mem_fopen,
    mem_fclose,
    mem_fread,
    mem_fseek,
    mem_ftell
};

const vfile_ops_t vfile_ops_flash = {
    mem_fopen,
    mem_fclose_flash,
    mem_fread,
    mem_fseek,
    mem_ftell
};
