#include <inttypes.h>
#include "exports.h"
#include "logging.h"
#include "exports/all.h"
#include "loader_userdata.h"

/**
 * Exported symbol struct
 */
typedef struct {
  const char *name; /*!< Name of symbol */
  void *ptr; /*!< Pointer of symbol in memory */
} ELFSymbol_t;

/**
 * Environment for execution
 */
typedef struct ELFEnv {
  const ELFSymbol_t *exported; /*!< Pointer to exported symbols array */
  unsigned int exported_size; /*!< Elements on exported symbol array */
} ELFEnv_t;

#define xstr(s) str(s)
#define str(s) #s
#define SYM(x) { xstr(x), &x }
#define SYM2(x,y) { x, &y }

static const ELFSymbol_t exports[] = {
  API_ALL
};

const ELFEnv_t patch_exports = { exports, sizeof(exports) / sizeof(*exports) };

static int is_streq(const char *s1, const char *s2) {
  while(*s1 && *s2) {
    if (*s1 != *s2)
      return 0;
    s1++;
    s2++;
  }
  return *s1 == *s2;
}

void closeReferencedLibs(userdata_t * userdata) {
  deps_t *deps = &userdata->deps;
  while (deps && deps->dlhandle != 0) {
    dlclose(deps->dlhandle);
    if (deps!=&userdata->deps) {
      ax_free(deps);
    }
    deps = deps->next;
  }
}

static void * findReferencedLib(userdata_t * userdata, const char *libname) {
  deps_t *deps = &userdata->deps;
  while (deps && deps->dlhandle != 0) {
    if (!strcmp(libname,dlname(deps->dlhandle))) {
      return deps->dlhandle;
    }
    deps = deps->next;
  }
  return 0;
}

static void addReferencedLib(userdata_t * userdata, void * dlhandle) {
  deps_t *deps = &userdata->deps;
  if ((deps->next == 0) && (deps->dlhandle == 0)) {
    deps->dlhandle = dlhandle;
    return;
  }
  deps_t *dep = ax_malloc(sizeof(deps_t),0);
  dep->next = deps->next;
  dep->dlhandle = dlhandle;
  deps->next = dep;
}

uint32_t getUndefinedSymbol(void *userdata, const char *sName) {
  userdata_t * userdata_ = (userdata_t *)userdata;
  int i;
  for (i = 0; i < patch_exports.exported_size; i++)
    if (is_streq(patch_exports.exported[i].name, sName))
      return (uint32_t) (patch_exports.exported[i].ptr);
  // get libname and symname from sName
  char libname[32];
  if (sName[0]=='_') {
    log_printf("  Can't find address for symbol %s (0)\n", sName);
    return 0xffffffff;
  }
  for(i=0;i<31;i++) {
    char c = sName[i];
    libname[i] = c;
    if (c=='_') {
      libname[i] = 0;
      i++;
      break;
    }
    if (c==0) {
      log_printf("Can't find address for symbol %s (1)\n", sName);
      return 0xffffffff;
    }
  }
  void *handle = findReferencedLib(userdata_,libname);
  if (handle) {
    void *sym = dlsym(handle, sName);
    if (sym==0) {
      sym = (void *)0xffffffff;
      log_printf("Can't find symbol %s in lib %s\n", sName, libname);
    }
    return (uint32_t)sym;
  } else {
    handle = dlopen(libname,0);
    if (!handle) {
      log_printf("Can't load lib for symbol %s (2)\n", sName);
      return 0xffffffff;
    }
    void *sym = dlsym(handle, sName);
    if (sym==0) sym = (void *)0xffffffff;
    if (sym == (void *)0xFFFFFFFF) {
      dlclose(handle);
      log_printf("Can't find symbol %s in lib %s\n", sName, libname);
    } else {
      addReferencedLib(userdata, handle);
      // corresponding dlclose is in closeReferencedLibs()
      //LogTextMessage("  Sym %s : 0x%08X\n", sName, sym);
    }
    return (uint32_t)sym;
  }
}

void list_all_symbols(void) {
  LogTextMessage("exported symbols = {");
  int i;
  for (i = 0; i < patch_exports.exported_size; i++) {
    LogTextMessage("  %s = 0x%08X,", patch_exports.exported[i].name, patch_exports.exported[i].ptr);
  }
  LogTextMessage("}");
}
