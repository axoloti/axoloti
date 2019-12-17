#include "adc_menu.h"
#include "axoloti_board.h"

const ui_node_t ADCMenu[ADCMenu_length] = {
  {&nodeFunctionTable_short_value, "PA0", .shortValue = {.pvalue =(int16_t *)&adcvalues[0], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PA1", .shortValue = {.pvalue =(int16_t *)&adcvalues[1], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PA2", .shortValue = {.pvalue =(int16_t *)&adcvalues[2], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PA3", .shortValue = {.pvalue =(int16_t *)&adcvalues[3], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PA4", .shortValue = {.pvalue =(int16_t *)&adcvalues[4], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PA5", .shortValue = {.pvalue =(int16_t *)&adcvalues[5], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PA6", .shortValue = {.pvalue =(int16_t *)&adcvalues[6], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PA7", .shortValue = {.pvalue =(int16_t *)&adcvalues[7], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PB0", .shortValue = {.pvalue =(int16_t *)&adcvalues[8], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PB1", .shortValue = {.pvalue =(int16_t *)&adcvalues[9], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PC0", .shortValue = {.pvalue =(int16_t *)&adcvalues[10], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PC1", .shortValue = {.pvalue =(int16_t *)&adcvalues[11], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PC2", .shortValue = {.pvalue =(int16_t *)&adcvalues[12], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PC3", .shortValue = {.pvalue =(int16_t *)&adcvalues[13], .minvalue = 0, .scale=0x100}},
  {&nodeFunctionTable_short_value, "PC4", .shortValue = {.pvalue =(int16_t *)&adcvalues[14], .minvalue = 0, .scale=0x100}},
};
