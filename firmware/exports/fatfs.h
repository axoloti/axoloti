#ifndef EXPORTS_FATFS_H
#define EXPORTS_FATFS_H

#include "ff.h"
#include "fatfs_dmafix.h"

#define EXPORTS_FATFS_SYMBOLS \
    SYM(f_open), \
    SYM(f_close), \
    SYM2("f_read",f_read1), \
    SYM2("f_write",f_write1), \
    SYM(f_lseek), \
    SYM(f_truncate), \
    SYM(f_sync), \
    SYM(f_opendir), \
    SYM(f_closedir), \
    SYM(f_readdir), \
    SYM(f_mkdir), \
    SYM(f_unlink), \
    SYM(f_rename), \
    SYM(f_stat), \
    SYM(f_chmod), \
    SYM(f_utime), \
    SYM(f_chdir), \
    SYM(f_getcwd), \
    SYM(f_getfree), \
    SYM(f_mount), \
    SYM(f_mkfs)

#define UNIMPLEMENTED_FATFS_SYMBOLS \
    SYM(f_findfirst), \
    SYM(f_findnext), \
    SYM(f_chdrive), \
    SYM(f_getlabel), \
    SYM(f_setlabel), \
    SYM(f_forward), \
    SYM(f_expand), \
    SYM(f_fdisk), \
    SYM(f_setcp), \
    SYM(f_putc), \
    SYM(f_puts), \
    SYM(f_printf), \
    SYM(f_gets)

#endif
