#ifndef EXPORTS_DLFCN_H
#define EXPORTS_DLFCN_H

#include "../api/dlfcn.h"

#define EXPORTS_DLFCN_SYMBOLS \
  SYM(dlopen), \
  SYM(dlclose), \
  SYM(dlsym), \
  SYM(dlerror)

#endif
