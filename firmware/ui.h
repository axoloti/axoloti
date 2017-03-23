/**
 * Copyright (C) 2013 - 2017 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef __UI_H
#define __UI_H

#include "parameters.h"

typedef union {
  struct {
    int btn_1 :1;
    int btn_2 :1;
    int btn_3 :1;
    int btn_4 :1;
    int btn_5 :1;
    int btn_6 :1;
    int btn_7 :1;
    int btn_8 :1;
    int btn_9 :1;
    int btn_10 :1;
    int btn_11 :1;
    int btn_12 :1;
    int btn_13 :1;
    int btn_14 :1;
    int btn_15 :1;
    int btn_16 :1;
    int unused :8;
    int btn_nav_Up :1;
    int btn_nav_Down :1;
    int btn_nav_Left :1;
    int btn_nav_Right :1;
    int btn_nav_Back :1;
    int btn_nav_Enter :1;
    int btn_nav_Home :1;
    int btn_nav_Shift :1;
  } fields;
  int32_t word;
} Btn_Nav_States_struct;

extern Btn_Nav_States_struct Btn_Nav_Or;
extern Btn_Nav_States_struct Btn_Nav_And;
extern int8_t EncBuffer[2];

extern Btn_Nav_States_struct Btn_Nav_CurStates;
extern Btn_Nav_States_struct Btn_Nav_PrevStates;

#define BTN_NAV_DOWN(x) \
  (Btn_Nav_CurStates.fields.x && !Btn_Nav_PrevStates.fields.x)

typedef struct {
  int *pvalue;
  int minvalue;
  int maxvalue;
} ui_node_type_integer_adjustable;

typedef struct {
  const void *array; // pointer to ui_node_t array
  int length;
} ui_node_type_node_list_t;

typedef struct {
  int16_t *pvalue;
  int16_t minvalue;
  int16_t scale;
} ui_node_type_short_value_t;

typedef void (*DisplayFunction)(void * userdata, int initialize);
typedef void (*ButtonFunction)(void * userdata);
typedef void (*VoidFunction)(void);

typedef struct {
  DisplayFunction displayFunction; // function pointer
  ButtonFunction buttonFunction;
  void * userdata;
} ui_node_custom_t;

typedef struct {
  VoidFunction fnctn;
} ui_node_action_function_t;

typedef struct {
	Parameter_t *params;
	Parameter_name_t *param_names;
	int32_t nparams;
} ui_node_param_list_t;

typedef struct {
	Parameter_t *param;
	Parameter_name_t *param_name;
} ui_node_param_t;

typedef struct {
	ui_object_t *obj;
} ui_node_object_t;

typedef struct node_object_list {
	ui_object_t *objs;
	int32_t nobjs;
} ui_node_object_list_t;

typedef enum {
  node_type_integer_value,
  node_type_short_value,
  node_type_custom,
//  node_type_INTDISPLAY,
  node_type_node_list,
  node_type_action_function,
  node_type_param_list,
  node_type_param,
  node_type_object_list,
  node_type_object
} ui_node_type;

typedef struct {
  ui_node_type node_type;
  const char *name;
  union {
    ui_node_type_integer_adjustable intValue;
    ui_node_type_short_value_t shortValue;
    ui_node_custom_t custom;
    ui_node_action_function_t fnctn;
    ui_node_type_node_list_t nodeList;
    ui_node_param_list_t paramList;
    ui_node_param_t param;
    ui_node_object_list_t objList;
    ui_node_object_t obj;
  };
} ui_node_t;

#if 0
// OBSOLETE
void UINode_Draw(int x, int y, const ui_node_t *node);
void UINode_Increment(const ui_node_t *node);
void UINode_Decrement(const ui_node_t *node);

void node_SendMetaDataUSB(ui_node_t *node);
void node_SendDataUSB(ui_node_t *node);
void node_ReceiveDataUSB(char *data);

void node_ClearObjects(void);
#endif

#if 1
// OBSOLETE API!
void KVP_RegisterObject(ui_node_t *node) __attribute__ ((deprecated));

void SetKVP_APVP(ui_node_t *node, ui_node_t *parent,
                 const char *keyName, int length, ui_node_t **array) __attribute__ ((deprecated));
void SetKVP_AVP(ui_node_t *node, const ui_node_t *parent,
                const char *keyName, int length, const ui_node_t *array) __attribute__ ((deprecated));
void SetKVP_IVP(ui_node_t *node, ui_node_t *parent,
                const char *keyName, int *value, int min, int max) __attribute__ ((deprecated));
void SetKVP_IPVP(ui_node_t *node, ui_node_t *parent,
                 const char *keyName, ParameterExchange_t *PEx, int min,
                 int max) __attribute__ ((deprecated));
void SetKVP_FNCTN(ui_node_t *node, ui_node_t *parent,
                  const char *keyName, VoidFunction fnctn) __attribute__ ((deprecated));
#endif

//void UISetUserDisplay(DisplayFunction dispfnctn, ButtonFunction btnfnctn, void* userdata);
//void AxolotiControlUpdate(void);
//extern void (*pControlUpdate)(void);

void ui_go_home(void);
void ui_init(void);
void ui_enter_node(const ui_node_t *node);


#endif
