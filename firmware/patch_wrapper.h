#ifndef PATCH_WRAPPER_H_
#define PATCH_WRAPPER_H_

#ifdef __cplusplus
extern "C" {
#endif

int patch_getStatus(patch_t *patch);
const char * patch_getName(patch_t *patch);

int patch_getNParams(patch_t *patch);
Parameter_t * patch_getParam(patch_t *patch, int index);
const char * patch_getParamName(patch_t *patch, int index);
void patch_changeParam(patch_t *patch, uint32_t param_index, int param_value, uint32_t mask);

int patch_getDisplayVectorSize(patch_t *patch);
int32_t * patch_getDisplayVector(patch_t *patch);

void patch_dsp_process(patch_t *patch, int32_t * audio_in, int32_t * audio_out);
void patch_midiInHandler(patch_t *patch, uint32_t midi);

int patch_getNObjects(patch_t *patch);
ui_object_t * patch_getObject(patch_t *patch, int index);

void patch_applyPreset(patch_t *patch, int index);
void * patch_getPresetData(patch_t *patch);

void patch_dispose(patch_t *patch);

#ifdef __cplusplus
} // extern "C"
#endif

#endif // PATCH_WRAPPER_H_
