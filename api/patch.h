#ifndef API_PATCH_H
#define API_PATCH_H

#include <stdint.h>


#ifdef __cplusplus
extern "C" {
#endif


typedef struct {
  int32_t pexIndex;
  int32_t value;
} PresetParamChange_t;

typedef struct patch patch_t;

typedef enum {
  patch_callback_stop = 1
} patch_callback_type;

typedef void (*patch_callback_t)(patch_t *, patch_callback_type);

// warning: can't call patch_load, patch_loadIndex, patch_stop from within patch init or dispose
patch_t * patch_load(const char *name, patch_callback_t patch_callback);
patch_t * patch_loadIndex(int index, patch_callback_t patch_callback);
void patch_stop(patch_t * patch);

#ifdef __cplusplus
} // extern "C"
#endif

#endif
