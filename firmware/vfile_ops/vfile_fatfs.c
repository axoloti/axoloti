#include "vfile_ops.h"
#include "vfile_fatfs.h"
#include "ff.h"
#include "axoloti_memory.h"
#include "fatfs_dmafix.h"

static filehandle ff_fopen(const char * path) {
  FIL *f = (void *)((int)ax_malloc(sizeof(FIL), 0));
  if (!f) return 0;
  FRESULT ferr = f_open(f, path, FA_READ);
  if (ferr != FR_OK) {
    ax_free(f);
    return 0;
  }
  return (filehandle)f;
}

static void ff_fclose(filehandle f) {
  FIL *fd = (FIL *)f;
  if (fd) {
    f_close(fd);
    ax_free(fd);
  }
}

static int ff_fread(filehandle f, void * buf, int size) {
  FIL *fd = (FIL *)f;
  UINT br;
  f_read1(fd, buf, size, &br);
  return br;
}

static int ff_fseek(filehandle f, int pos) {
  FIL *fd = (FIL *)f;
  return f_lseek(fd, pos);
}

static int ff_ftell(filehandle f) {
  FIL *fd = (FIL *)f;
  return f_tell(fd);
}

const vfile_ops_t vfile_ops_fatfs = {
    ff_fopen,
    ff_fclose,
    ff_fread,
    ff_fseek,
    ff_ftell
};
