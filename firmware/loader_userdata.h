#ifndef LOADER_USER_DATA_H
#define LOADER_USER_DATA_H

typedef struct deps {
  void * dlhandle;
  struct deps *next;
} deps_t;

typedef struct userdata {
  const struct vfile_ops * vfile_ops;
  void * fd;
  deps_t deps;
} userdata_t;

#define LOADER_USERDATA_T userdata_t

#endif
