#include <inttypes.h>
#include "parameters.h"
#include "patch.h"
#include "patch_class.h"
#include "patch_impl.h"
#include "patch_wrapper.h"

extern "C" {

int patch_getStatus(patch_t *patch) {
  return patch->patchStatus;
}

int patch_getNParams(patch_t *patch) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  return (int)instance->getProperty(ax_prop_nparams,0);
}

Parameter_t * patch_getParam(patch_t *patch, int index) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  return (Parameter_t *)instance->getProperty(ax_prop_param,index);
}

const char * patch_getParamName(patch_t *patch, int index) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  return (const char *)instance->getProperty(ax_prop_paramName, index);
}

int patch_getDisplayVectorSize(patch_t *patch) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  return (int)instance->getProperty(ax_prop_displayvector_size,0);
}

int32_t * patch_getDisplayVector(patch_t *patch) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  return (int32_t *)instance->getProperty(ax_prop_displayvector,0);
}

const char * patch_getName(patch_t *patch) {
  return patch->name;
}

void patch_changeParam(patch_t *patch, uint32_t param_index, int param_value, uint32_t mask) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  if (param_index<(int)instance->getProperty(ax_prop_nparams,0)) {
    Parameter_t *param = patch_getParam(patch, param_index);
    parameter_setVal(param, param_value, mask);
  }
}

void patch_dsp_process(patch_t *patch, int32_t * audio_in, int32_t * audio_out) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  instance->tick(audio_in, audio_out);
}

void patch_midiInHandler(patch_t *patch, uint32_t midi) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  instance->midiInHandler(midi);
}

void patch_dispose(patch_t *patch) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  instance->dispose();
}

void patch_applyPreset(patch_t *patch, int index) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  instance->setProperty(ax_prop_applyPreset,index,0);
}

void * patch_getPresetData(patch_t *patch) {
  PatchInstance *instance = (PatchInstance *)patch->patchobject;
  return instance->getProperty(ax_prop_presetData,0);
}

int patch_getNObjects(patch_t *patch) {
  // TODO: implement embedded UI
  return 0;
}

ui_object_t * patch_getObject(patch_t *patch, int index) {
  // TODO: implement embedded UI
  return 0;
}

}
