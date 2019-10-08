#include <string.h>
#include "hal.h"
#include "chprintf.h"
#include "dlfcn.h"
#include "elfloader/loader.h"
#include "axoloti_memory.h"
#include "logging.h"
#include "vfile_ops.h"
#include "vfile_ops/vfile_fatfs.h"

// TODO: mutexify dlfcn

enum {
  dl_max_name_length = 32
};

typedef struct dllib {
  struct dllib *next;
  int refcount;
  ELFExec_t *elf_data;
  char name[dl_max_name_length];
} dllib_t;

static dllib_t * dllibs = 0;

int dlclose(void * handle) {
  dllib_t *dlhandle = (dllib_t *)handle;
  dllib_t *dllib = dllibs;
  dllib_t *prev = 0;
  while(dllib!=0) {
    if (dllib == handle) {
      dllib->refcount--;
      if (dllib->refcount == 0) {
        unload_elf(dllib->elf_data);
        if (prev) {
          prev->next = dllib->next;
        } else {
          dllibs = dllib->next;
        }
        ax_free(dllib);
      }
      return 0;
    }
    prev = dllib;
    dllib = dllib->next;
  }
  log_printf("dlclose() failed!\n");
  return 0;
}

static const char * err = 0;
static char err_msg[64];

const char *dlerror(void) {
  const char *err1 = err;
  err = 0;
  return err1;
}

void *dlopen(const char * name, int mode) {
  // find name
  dllib_t *dllib = dllibs;
  while(dllib!=0) {
    if (!strncmp(name, dllib->name, dl_max_name_length-1)) {
      dllib->refcount++;
      return dllib;
    }
    dllib = dllib->next;
  }
  // name not matched, load
  dllib = (dllib_t *)ax_malloc(sizeof(dllib_t), mem_type_hint_large);
  dllib->refcount=1;
  strncpy(dllib->name,name,dl_max_name_length-1);
  char path[dl_max_name_length+10];
  chsnprintf(path, sizeof(path), "/lib/%s.elf", name);
  userdata_t loader_env = {
      .vfile_ops = &vfile_ops_fatfs
  };
  int retval = load_elf(path,loader_env, &dllib->elf_data);
  if (retval) {
    chsnprintf(err_msg, sizeof(err_msg), "failed loading lib \"%s\"",path);
    err = err_msg;
    return 0;
  }
  dllib->next = dllibs;
  dllibs = dllib;
  return dllib;
}

void *dlsym(void *restrict handle, const char *restrict name) {
  dllib_t *dlhandle = (dllib_t *)handle;
  void * retval;
  retval = get_func(dlhandle->elf_data, name);
  if (!retval) {
    retval = get_obj(dlhandle->elf_data, name);
  }
  if (!retval) {
    chsnprintf(err_msg, sizeof(err_msg), "symbol not found \"%s\"",name);
    err = err_msg;
    return 0;
  }
  return retval;
}

const char * dlname(void *restrict handle) {
  dllib_t *dlhandle = (dllib_t *)handle;
  return dlhandle->name;
}

static void dbg_print_dllib(dllib_t *dllib) {
  log_printf("  name=\"%s\", refcnt=%d\n",dllib->name, dllib->refcount);
}

void dbg_dump_dlopen(void) {
  log_printf("dllibs = {\n");
  dllib_t *dllib = dllibs;
  while(dllib!=0) {
    dbg_print_dllib(dllib);
    dllib = dllib->next;
  }
  log_printf("}\n");
}

void dbg_dl_test(void) {
  void * dlhandle1;
  void * dlhandle2;
  dbg_dump_dlopen();
  log_printf("open test\n");
  dlhandle1 = dlopen("test",0);
  dbg_dump_dlopen();
  log_printf("close test\n");
  dlclose(dlhandle1);
  dbg_dump_dlopen();
  log_printf("open test + test2\n");
  dlhandle1 = dlopen("test",0);
  dlhandle2 = dlopen("test",0);
  dbg_dump_dlopen();
  log_printf("close test + test2\n");
  dlclose(dlhandle1);
  dlclose(dlhandle2);
  dbg_dump_dlopen();
  log_printf("open test + test2\n");
  dlhandle1 = dlopen("test",0);
  dlhandle2 = dlopen("test",0);
  dbg_dump_dlopen();
  log_printf("close test2 + test\n");
  dlclose(dlhandle2);
  dlclose(dlhandle1);
  dbg_dump_dlopen();
}
