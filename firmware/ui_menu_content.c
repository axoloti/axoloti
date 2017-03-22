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


// ------ Parameter menu stuff ------

ui_node_t ParamMenu = {
  node_type_param, "Param", .param = {0}
};

// ------ Object menu stuff ------

// todo: use a stack of ObjMenu's
char objname[MAX_PARAMETER_NAME_LENGTH + 1];

ui_node_t ObjMenu = {
  node_type_object, objname, .obj = {0}
};

// ------ SDCard menu stuff ------

int load_patch_index = 0;

void EnterMenuPatchLoad(void) {
	LoadPatchIndexed(load_patch_index);
	// TODO: show success/fail somehow
}

#define PatchLoadMenu_length 2
const ui_node_t PatchLoadMenu[PatchLoadMenu_length] = {
  { node_type_integer_value, "Bank Index", .intValue = {.pvalue=&load_patch_index,.minvalue=0,.maxvalue=1<<15}},
  { node_type_action_function, "Load", .fnctn = {&EnterMenuPatchLoad}}
};

static const ui_node_t SdcFormatFailed1 =
  { node_type_action_function, "Format failed", .fnctn = {0}};
static const ui_node_t SdcFormatFailed =
  { node_type_node_list, "Format", .nodeList = {&SdcFormatFailed1, 1}};

const ui_node_t SdcFormatOK1 =
  { node_type_action_function, "Format OK", .fnctn = {0}};
static const ui_node_t SdcFormatOK =
  { node_type_node_list, "Format", .nodeList = {&SdcFormatOK1, 1}};

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
  { node_type_node_list, "Load patch", .nodeList = {&PatchLoadMenu, PatchLoadMenu_length}},
  { node_type_action_function, "Format", .fnctn = {&EnterMenuFormat}}
};

// ------ ADC menu stuff ------

#define ADCMenu_length 15
const ui_node_t ADCMenu[ADCMenu_length] = {
  { node_type_short_value, "ADC0", .shortValue = {(int16_t *)&adcvalues[0]}},
  { node_type_short_value, "ADC1", .shortValue = {(int16_t *)&adcvalues[1]}},
  { node_type_short_value, "ADC2", .shortValue = {(int16_t *)&adcvalues[2]}},
  { node_type_short_value, "ADC3", .shortValue = {(int16_t *)&adcvalues[3]}},
  { node_type_short_value, "ADC4", .shortValue = {(int16_t *)&adcvalues[4]}},
  { node_type_short_value, "ADC5", .shortValue = {(int16_t *)&adcvalues[5]}},
  { node_type_short_value, "ADC6", .shortValue = {(int16_t *)&adcvalues[6]}},
  { node_type_short_value, "ADC7", .shortValue = {(int16_t *)&adcvalues[7]}},
  { node_type_short_value, "ADC8", .shortValue = {(int16_t *)&adcvalues[8]}},
  { node_type_short_value, "ADC9", .shortValue = {(int16_t *)&adcvalues[9]}},
  { node_type_short_value, "ADC10", .shortValue = {(int16_t *)&adcvalues[10]}},
  { node_type_short_value, "ADC11", .shortValue = {(int16_t *)&adcvalues[11]}},
  { node_type_short_value, "ADC12", .shortValue = {(int16_t *)&adcvalues[12]}},
  { node_type_short_value, "ADC13", .shortValue = {(int16_t *)&adcvalues[13]}},
  { node_type_short_value, "ADC14", .shortValue = {(int16_t *)&adcvalues[14]}}
};

// ------ Test menu stuff ------

void TestDisplayFunction(void *x, int initial) {
	if (initial) {
		LCD_grey();
	}
}

void TestButtonFunction(void *x) {
	static uint8_t val0;
	val0 += EncBuffer[0];
	static uint8_t val1;
	val1 += EncBuffer[1];
	EncBuffer[0] = 0;
	EncBuffer[1] = 0;

	LED_setOne(LED_RING_LEFT, val0 % 16);
	LED_setOne(LED_RING_RIGHT, val1 % 16);

	static int button[16];
	if (BTN_NAV_DOWN(btn_1))  button[0] =   (button[0]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_2))  button[1] =   (button[1]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_3))  button[2] =   (button[2]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_4))  button[3] =   (button[3]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_5))  button[4] =   (button[4]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_6))  button[5] =   (button[5]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_7))  button[6] =   (button[6]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_8))  button[7] =   (button[7]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_9))  button[8] =   (button[8]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_10)) button[9] =   (button[9]  + 1) % 4;
	if (BTN_NAV_DOWN(btn_11)) button[10] =  (button[10] + 1) % 4;
	if (BTN_NAV_DOWN(btn_12)) button[11] =  (button[11] + 1) % 4;
	if (BTN_NAV_DOWN(btn_13)) button[12] =  (button[12] + 1) % 4;
	if (BTN_NAV_DOWN(btn_14)) button[13] =  (button[13] + 1) % 4;
	if (BTN_NAV_DOWN(btn_15)) button[14] =  (button[14] + 1) % 4;
	if (BTN_NAV_DOWN(btn_16)) button[15] =  (button[15] + 1) % 4;

	LED_clear(LED_STEPS);
	int i = 0;
	for (i = 0; i < 16; i++) {
		LED_addOne(LED_STEPS, i, button[i]);
	}

	const int lmid = 25;
	const int rmid = 43;
	LCD_drawStringInvN(lmid, 1, "TEST", LCDWIDTH);
	LCD_drawStringInvN(lmid, 2, "", 5);
	LCD_drawNumber3DInv(20, 4, val0);
	LCD_drawCharInv(20, 5, val0);
	LCD_drawNumber3DInv(rmid, 4, val1);
	LCD_drawCharInv(rmid, 5, val1);

	if (Btn_Nav_CurStates.fields.btn_nav_Up) {
		LCD_drawStringInvN(lmid, 3, "Up", 5);
		LCD_drawChar(0, 3, ' ');
	} else {
		LCD_drawCharInv(0, 3, '-');
	}
	if (Btn_Nav_CurStates.fields.btn_nav_Down) {
		LCD_drawStringInvN(lmid, 3, "Down", 5);
		LCD_drawChar(0, 5, ' ');
	} else {
		LCD_drawCharInv(0, 5, '-');
	}
	if (Btn_Nav_CurStates.fields.btn_nav_Enter) {
		LCD_drawStringInvN(lmid, 3, "Enter", 5);
		LCD_drawChar(61, 7, ' ');
	} else {
		LCD_drawCharInv(61, 7, '-');
	}
	if (Btn_Nav_CurStates.fields.btn_nav_Back) {
		LCD_drawStringInvN(lmid, 3, "Back", 5);
		LCD_drawChar(0, 7, ' ');
	} else {
		LCD_drawCharInv(0, 7, '-');
	}
	if (Btn_Nav_CurStates.fields.btn_nav_Home) {
		LCD_drawStringInvN(lmid, 3, "Home", 5);
	}
	if (Btn_Nav_CurStates.fields.btn_nav_Left) {
		LCD_drawStringInvN(lmid, 3, "Left", 5);
		LCD_drawChar(61, 3, ' ');
	} else {
		LCD_drawCharInv(61, 3, '-');
	}
	if (Btn_Nav_CurStates.fields.btn_nav_Right) {
		LCD_drawStringInvN(lmid, 3, "Right", 5);
		LCD_drawChar(61, 5, ' ');
	} else {
		LCD_drawCharInv(61, 5, '-');
	}
	if (Btn_Nav_CurStates.fields.btn_nav_Shift) {
		LCD_drawStringInvN(lmid, 3, "Shift", 5);
	}
}

// ------ Food menu stuff ------
// just a silly test...

#define AppleMenu_length 4
const ui_node_t AppleMenu[AppleMenu_length] = {
  { node_type_action_function, "JamesGrieve", .fnctn = {0}},
  { node_type_action_function, "GrannySmith", .fnctn = {0}},
  { node_type_action_function, "Jonagold", .fnctn = {0}},
  { node_type_action_function, "Cox", .fnctn = {0}},
};

#define NutsMenu_length 10
const ui_node_t NutsMenu[NutsMenu_length] = {
  { node_type_action_function, "Cashew", .fnctn = {0}},
  { node_type_action_function, "Peanut", .fnctn = {0}},
  { node_type_action_function, "Pecan", .fnctn = {0}},
  { node_type_action_function, "Walnut", .fnctn = {0}},
  { node_type_action_function, "Pistachio", .fnctn = {0}},
  { node_type_action_function, "Hazelnut", .fnctn = {0}},
  { node_type_action_function, "Coconut", .fnctn = {0}},
  { node_type_action_function, "Brazil nut", .fnctn = {0}},
  { node_type_action_function, "Macadamia", .fnctn = {0}},
  { node_type_action_function, "Almond", .fnctn = {0}}
};

#define DishMenu_length 8
const ui_node_t DishMenu[DishMenu_length] = {
  { node_type_action_function, "Cake", .fnctn = {0}},
  { node_type_action_function, "Salad", .fnctn = {0}},
  { node_type_action_function, "Soup", .fnctn = {0}},
  { node_type_action_function, "Waffle", .fnctn = {0}},
  { node_type_action_function, "Ice cream", .fnctn = {0}},
  { node_type_action_function, "Rice", .fnctn = {0}},
  { node_type_action_function, "Spaghetti", .fnctn = {0}},
  { node_type_action_function, "Pizza", .fnctn = {0}}
};

#define FoodMenu_length 3
const ui_node_t FoodMenu[FoodMenu_length] = {
  { node_type_node_list, "Nuts", .nodeList = {NutsMenu, NutsMenu_length}},
  { node_type_node_list, "Apple", .nodeList = {AppleMenu, AppleMenu_length}},
  { node_type_node_list, "Dish", .nodeList = {DishMenu, DishMenu_length}}
};

// ------ Main menu stuff ------

ui_node_t MainMenu[MainMenu_length] = {
  { node_type_object_list, "Objects", .objList = {0,0}}, // at MAIN_MENU_INDEX_PATCH
  { node_type_param_list, "Params", .paramList = {0,0}}, // at MAIN_MENU_INDEX_PARAMS
  { node_type_node_list, "SDCard", .nodeList = {SdcMenu, SdcMenu_length}},
  { node_type_node_list, "ADCs", .nodeList = {ADCMenu, ADCMenu_length}},
  { node_type_integer_value, "dsp%", .intValue = {&dspLoadPct, 0, 100}},
  { node_type_custom, "Test", .custom = {&TestDisplayFunction, TestButtonFunction, 0}},
  { node_type_node_list, "Food", .nodeList = {FoodMenu, FoodMenu_length}}
};

// ------ Root menu stuff ------

const ui_node_t RootMenu = {
  node_type_node_list, "--- AXOLOTI ---", .nodeList = {MainMenu, MainMenu_length}
};
