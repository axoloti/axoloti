#include "ff.h"
#include "vfile_ops.h"

extern const struct ELFEnv patch_exports;

uint32_t getUndefinedSymbol(void *userdata, const char *sName);
