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
#include "stdbool.h"
#include "input_evt.h"

extern int8_t EncBuffer[4];

typedef struct {
  int *pvalue;
  int minvalue;
  int maxvalue;
} ui_node_integer_adjustable;

typedef struct {
  const void *array; // pointer to ui_node_t array
  int length;
} ui_node_node_list_t;

typedef struct {
  int16_t *pvalue;
  int16_t minvalue;
  int16_t scale;
} ui_node_short_value_t;


struct ui_node;

typedef uint32_t (*nodeFunctionHandleEvent)(const struct ui_node * node, input_event evt);
typedef void (*nodeFunctionPaintScreen)(const struct ui_node * node, uint32_t dirtyflags);
typedef void (*nodeFunctionPaintLine)(const struct ui_node * node, int line, uint32_t dirtyflags);

typedef struct {
	nodeFunctionHandleEvent handle_evt;
	nodeFunctionPaintScreen paint_screen_update;
	nodeFunctionPaintLine paint_line_update;
	nodeFunctionPaintLine paint_line_update_inv;
} nodeFunctionTable;

extern nodeFunctionTable nodeFunctionTable_custom;

typedef void (*VoidFunction)(void);

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

typedef struct ui_node {
  const nodeFunctionTable *functions;
  const char *name;
  union {
    ui_node_integer_adjustable intValue;
    ui_node_short_value_t shortValue;
    ui_node_action_function_t fnctn;
    ui_node_node_list_t nodeList;
    ui_node_param_list_t paramList;
    ui_node_param_t param;
    ui_node_object_list_t objList;
    ui_node_object_t obj;
  };
} ui_node_t;

extern const nodeFunctionTable nodeFunctionTable_integer_value;
extern const nodeFunctionTable nodeFunctionTable_short_value;
extern const nodeFunctionTable nodeFunctionTable_node_list;
extern const nodeFunctionTable nodeFunctionTable_action_function;
extern const nodeFunctionTable nodeFunctionTable_param_list;
extern const nodeFunctionTable nodeFunctionTable_param;
extern const nodeFunctionTable nodeFunctionTable_object_list;
extern const nodeFunctionTable nodeFunctionTable_object;

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
                 const char *keyName, Parameter_t *PEx, int min,
                 int max) __attribute__ ((deprecated));
void SetKVP_FNCTN(ui_node_t *node, ui_node_t *parent,
                  const char *keyName, VoidFunction fnctn) __attribute__ ((deprecated));
#endif

//void UISetUserDisplay(DisplayFunction dispfnctn, ButtonFunction btnfnctn, void* userdata);
//void AxolotiControlUpdate(void);
//extern void (*pControlUpdate)(void);

void ui_init(void);
void ui_go_home(void);
uint32_t ui_enter_node(const ui_node_t *node);

#define LCD_COL_EQ 45
#define LCD_COL_EQ_LENGTH 6
#define LCD_COL_VAL 45

// last line on the screen is for status
#define STATUSROW 7

// normal content has a indent to allow indicators on first column:
#define LCD_COL_INDENT 3

// the last column to write a character on:
#define LCD_COL_RIGHT 61

#define LCD_COL_ENTER 48

#define LCD_VALUE_POSITION 36


typedef struct {
	const ui_node_t *parent;
	int currentpos;
} menu_stack_t;

#define menu_stack_size 10
extern menu_stack_t menu_stack[menu_stack_size];
extern int menu_stack_position;

// ------ LCD dirty flags ------
// for selective repainting
// in high to low priority order:
#define lcd_dirty_flag_clearscreen  (1<<0)
#define lcd_dirty_flag_header  (1<<1)
#define lcd_dirty_flag_initial (1<<2)
#define lcd_dirty_flag_listnav (1<<3)
#define lcd_dirty_flag_usr0 (1<<4)
#define lcd_dirty_flag_usr1 (1<<5)
#define lcd_dirty_flag_usr2 (1<<6)
#define lcd_dirty_flag_usr3 (1<<7)
#define lcd_dirty_flag_usr4 (1<<8)
#define lcd_dirty_flag_usr5 (1<<9)
#define lcd_dirty_flag_usr6 (1<<10)
#define lcd_dirty_flag_usr7 (1<<11)
#define lcd_dirty_flag_input (1<<30)

extern uint32_t list_nav_down(const ui_node_t *node, int maxposition);
extern uint32_t list_nav_up(const ui_node_t *node);
extern void update_list_nav(int current_menu_length);

extern ui_node_t ObjMenu;
extern ui_node_t ParamMenu;

typedef struct led_array led_array_t;

void ShowParameterOnEncoderLEDRing(led_array_t *led_array, Parameter_t *p);
void ShowParameterOnButtonArrayLEDs(led_array_t *led_array, Parameter_t *p);

void ProcessEncoderParameter(Parameter_t *p, int8_t v);
void ProcessStepButtonsParameter(Parameter_t *p);

void pollProcessUIEvent(void);

#endif
