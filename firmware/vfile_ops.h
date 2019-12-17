#ifndef VFILE_OPS_H
#define VFILE_OPS_H

typedef struct fdt * filehandle;

typedef struct vfile_ops {
  filehandle(*vf_open)(const char *);
  void(*vf_close)(filehandle);
  int(*vf_read)(filehandle, void *, int);
  int(*vf_seek)(filehandle, int);
  int(*vf_tell)(filehandle);
} vfile_ops_t;

#endif
