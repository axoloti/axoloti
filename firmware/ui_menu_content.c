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

#include "ui.h"
#include "ui_menu_content.h"
#include "axoloti_control.h"
#include "ff.h"
#include "axoloti_board.h"
#include "patch.h"


// ------ SDCard menu stuff ------

int load_patch_index = 0;

void EnterMenuPatchLoad(void) {
	LoadPatchIndexed(load_patch_index);
	// TODO: show success/fail somehow
}

#define PatchLoadMenu_length 2
const ui_node_t PatchLoadMenu[PatchLoadMenu_length] = {
  { &nodeFunctionTable_integer_value, "Bank Index", .intValue = {.pvalue=&load_patch_index,.minvalue=0,.maxvalue=1<<15}},
  { &nodeFunctionTable_action_function, "Load", .fnctn = {&EnterMenuPatchLoad}}
};

static const ui_node_t SdcFormatFailed1 =
  { &nodeFunctionTable_action_function, "Format failed", .fnctn = {0}};
static const ui_node_t SdcFormatFailed =
  { &nodeFunctionTable_node_list, "Format", .nodeList = {&SdcFormatFailed1, 1}};

const ui_node_t SdcFormatOK1 =
  { &nodeFunctionTable_action_function, "Format OK", .fnctn = {0}};
static const ui_node_t SdcFormatOK =
  { &nodeFunctionTable_node_list, "Format", .nodeList = {&SdcFormatOK1, 1}};

void EnterMenuFormat(void) {
	FRESULT err;
	err = f_mkfs(0, 0, 0);
	if (err != FR_OK)
		ui_enter_node(&SdcFormatFailed);
	else
		ui_enter_node(&SdcFormatOK);
}

#define SdcMenu_length 2
const ui_node_t SdcMenu[SdcMenu_length] = {
  { &nodeFunctionTable_node_list, "Load patch", .nodeList = {&PatchLoadMenu, PatchLoadMenu_length}},
  { &nodeFunctionTable_action_function, "Format", .fnctn = {&EnterMenuFormat}}
};

// ------ ADC menu stuff ------

#define ADCMenu_length 15
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

// ------ Test menu stuff ------

static uint8_t val0;
static uint8_t val1;
static uint8_t val2;
static uint8_t val3;

static void fhandle_evt(const struct ui_node * node, ui_event evt) {
	if (evt.fields.button==btn_encoder) {
		switch (evt.fields.quadrant) {
		case quadrant_topleft : val0 += evt.fields.value;
		break;
		case quadrant_topright : val1 += evt.fields.value;
		break;
		case quadrant_bottomleft : val2 += evt.fields.value;
		break;
		case quadrant_bottomright : val3 += evt.fields.value;
		break;
		default: break;
		}
		return;
	}
}

static void fpaint_screen_initial(const struct ui_node * node) {
	LCD_grey();
}

static void fpaint_screen_update(const struct ui_node * node) {
	LED_setOne(LED_RING_TOPLEFT, val0 % 16);
	LED_setOne(LED_RING_TOPRIGHT, val1 % 16);
	LED_setOne(LED_RING_BOTTOMLEFT, val2 % 16);
	LED_setOne(LED_RING_BOTTOMRIGHT, val3 % 16);
	const int lmid = 25;
	const int rmid = 43;

	LCD_drawStringInvN(lmid, 1, "TEST", LCDWIDTH);
	LCD_drawNumber3DInv(10, 2, val0);
	LCD_drawCharInv(10, 3, val0);
	LCD_drawNumber3DInv(44, 2, val1);
	LCD_drawCharInv(40, 3, val1);

	LCD_drawNumber3DInv(10, 4, val2);
	LCD_drawCharInv(10, 5, val2);
	LCD_drawNumber3DInv(44, 4, val3);
	LCD_drawCharInv(40, 5, val3);
}

static void fpaint_line_initial(const struct ui_node * node, int y) {
}

static void fpaint_line_initial_inv(const struct ui_node * node, int y) {
}

static void fpaint_line_update(const struct ui_node * node, int y) {
}

static void fpaint_line_update_inv(const struct ui_node * node, int y) {
}

nodeFunctionTable nodeFunctionTable_custom = {
		fhandle_evt,
		fpaint_screen_initial,
		fpaint_screen_update,
		fpaint_line_update,
		fpaint_line_update_inv,
		fpaint_line_initial,
		fpaint_line_initial_inv
};

// ------ Food menu stuff ------
// just a silly test...

#define AppleMenu_length 4
const ui_node_t AppleMenu[AppleMenu_length] = {
  { &nodeFunctionTable_action_function, "JamesGrieve", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "GrannySmith", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Jonagold", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Cox", .fnctn = {0}},
};

#define NutsMenu_length 10
const ui_node_t NutsMenu[NutsMenu_length] = {
  { &nodeFunctionTable_action_function, "Cashew", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Peanut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Pecan", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Walnut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Pistachio", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Hazelnut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Coconut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Brazil nut", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Macadamia", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Almond", .fnctn = {0}}
};

#define DishMenu_length 8
const ui_node_t DishMenu[DishMenu_length] = {
  { &nodeFunctionTable_action_function, "Cake", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Salad", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Soup", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Waffle", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Ice cream", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Rice", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Spaghetti", .fnctn = {0}},
  { &nodeFunctionTable_action_function, "Pizza", .fnctn = {0}}
};

#define FoodMenu_length 3
const ui_node_t FoodMenu[FoodMenu_length] = {
  { &nodeFunctionTable_node_list, "Nuts", .nodeList = {NutsMenu, NutsMenu_length}},
  { &nodeFunctionTable_node_list, "Apple", .nodeList = {AppleMenu, AppleMenu_length}},
  { &nodeFunctionTable_node_list, "Dish", .nodeList = {DishMenu, DishMenu_length}}
};

// ------ Main menu stuff ------

ui_node_t MainMenu[MainMenu_length] = {
  { &nodeFunctionTable_object_list, "Objects", .objList = {0,0}}, // at MAIN_MENU_INDEX_PATCH
  { &nodeFunctionTable_param_list, "Params", .paramList = {0,0}}, // at MAIN_MENU_INDEX_PARAMS
  { &nodeFunctionTable_node_list, "SDCard", .nodeList = {SdcMenu, SdcMenu_length}},
  { &nodeFunctionTable_node_list, "ADCs", .nodeList = {ADCMenu, ADCMenu_length}},
  { &nodeFunctionTable_integer_value, "dsp%", .intValue = {&dspLoadPct, 0, 100}},
  { &nodeFunctionTable_custom, "Test" },
  { &nodeFunctionTable_node_list, "Food", .nodeList = {FoodMenu, FoodMenu_length}}
};

// ------ Root menu stuff ------

const ui_node_t RootMenu = {
  &nodeFunctionTable_node_list, "--- AXOLOTI ---", .nodeList = {MainMenu, MainMenu_length}
};


void ui_deinit_patch(void) {
	ui_go_home();
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.objs = 0;
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.nobjs = 0;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.params = 0;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.param_names = 0;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.nparams = 0;
}

void ui_init_patch(void) {
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.objs = patchMeta.objects;
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.nobjs = patchMeta.nobjects;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.params = patchMeta.params;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.param_names = patchMeta.param_names;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.nparams = patchMeta.nparams;
}
