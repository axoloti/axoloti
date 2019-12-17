#ifndef UI_NODES_COMMON_H
#define UI_NODES_COMMON_H

#include "../axoloti_control.h"
#include "qgfx.h"

// todo: change these to event handlers
extern void ProcessEncoderParameter(Parameter_t *p, int8_t v);
extern void ProcessStepButtonsParameter(Parameter_t *p);

extern void ShowParameterOnButtonArrayLEDs(led_array_t *led_array, Parameter_t *p);
extern void drawParamValue(int line, int x, Parameter_t *param);
extern void drawParamValueInv(int line, int x, Parameter_t *param);
extern void drawDispValue(int line, int x, Display_meta_t *disp);
extern void drawDispValueInv(int line, int x, Display_meta_t *disp);

void drawParamValue1(const gfxq *gfx, Parameter_t *param);

#endif
