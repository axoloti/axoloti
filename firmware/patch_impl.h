#ifndef PATCH_IMPL_H
#define PATCH_IMPL_H

#include "elfloader/loader.h"

typedef struct patch {
  void * patchobject;
  patchStatus_t patchStatus;
  ELFExec_t *elf;
  char name[32];
  patch_callback_t patch_callback;
} patch_t;

patch_t * patch_iter_first(void);
int patch_iter_done(patch_t * cur);
patch_t * patch_iter_next(patch_t * cur);

extern int dspLoadPct; // DSP load in percent

void start_dsp_thread(void);

#endif
