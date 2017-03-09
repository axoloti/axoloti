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

#include "axoloti_defines.h"
#include "ui.h"
#include "ch.h"
#include "hal.h"
#include "midi.h"
#include "axoloti_math.h"
#include "patch.h"
#include "sdcard.h"
#include "pconnection.h"
#include "axoloti_board.h"
#include "axoloti_control.h"
#include "glcdfont.h"
#include "ff.h"
#include <string.h>
#include "spilink.h"

#define LCD_COL_INDENT 6
#define LCD_COL_RIGHT 0
#define LCD_COL_LEFT 97
#define STATUSROW 7

Btn_Nav_States_struct Btn_Nav_CurStates;
Btn_Nav_States_struct Btn_Nav_PrevStates;
Btn_Nav_States_struct Btn_Nav_Or;
Btn_Nav_States_struct Btn_Nav_And;

int8_t EncBuffer[2];

#define MAXOBJECTS 256
KeyValuePair_t *ObjectKvps[MAXOBJECTS];
#define MAXTMPMENUITEMS 15
KeyValuePair_t TmpMenuKvps[MAXTMPMENUITEMS];

#define MainMenu_length 6
#define SdcMenu_length 2
#define ADCMenu_length 15
#define FoodMenu_length 3

extern KeyValuePair_t MainMenu[MainMenu_length];
KeyValuePair_t *ObjectKvpRoot = &MainMenu[0];
extern const KeyValuePair_t SdcMenu[SdcMenu_length];
extern const KeyValuePair_t ADCMenu[ADCMenu_length];
extern const KeyValuePair_t RootMenu;
extern const KeyValuePair_t FoodMenu[FoodMenu_length];


typedef struct {
	const KeyValuePair_t *parent;
	int currentpos;
} menu_stack_t;

#define menu_stack_size 10

menu_stack_t menu_stack[menu_stack_size] = {
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0},
	{&RootMenu, 0}
};

int menu_stack_position = 0;

void EnterMenuLoad(void) {
// TODO: implement
#if 0
  memp = (uint8_t *)&fbuff[0];
  FRESULT res;
  FILINFO fno;
  DIR dir;
  int index = 0;
  char *fn;
#if _USE_LFN
  fno.lfname = 0;
  fno.lfsize = 0;
#endif
  res = f_opendir(&dir, "");
  if (res == FR_OK) {
    for (;;) {
      res = f_readdir(&dir, &fno);
      if (res != FR_OK || fno.fname[0] == 0)
        break;
      if (fno.fname[0] == '.')
        continue;
      fn = fno.fname;
      if (fno.fattrib & AM_DIR) {
        // ignore subdirectories for now
      }
      else {
        int l = strlen(fn);
        if ((fn[l - 4] == '.') && (fn[l - 3] == 'B') && (fn[l - 2] == 'I')
            && (fn[l - 1] == 'N')) {
          char *s;
          s = (char *)memp;
          strcpy(s, fn);
          memp += l + 1;
          SetKVP_FNCTN(&TmpMenuKvps[index], NULL, s, &EnterMenuLoadFile);
          index++;
        }
      }
    }
    SetKVP_AVP(&LoadMenu, &KvpsHead, "Load SD", index, &TmpMenuKvps[0]);
    KvpsDisplay = &LoadMenu;
  }
  // TBC: error messaging
#endif
}

void EnterMenuFormat(void) {
  FRESULT err;
  err = f_mkfs(0, 0, 0);
  if (err != FR_OK) {
    SetKVP_AVP(&TmpMenuKvps[0], &RootMenu, "Format failed", 0, 0);
//    KvpsDisplay = &TmpMenuKvps[0];
  }
  else {
    SetKVP_AVP(&TmpMenuKvps[0], &RootMenu, "Format OK", 0, 0);
//    KvpsDisplay = &TmpMenuKvps[0];
  }
}

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
	int i=0;
	for(i=0;i<16;i++) {
		LED_addOne(LED_STEPS,i,button[i]);
	}

	const int lmid = 50;
	const int rmid = 86;
    LCD_drawStringInvN(lmid, 1, "TEST", LCDWIDTH);
    LCD_drawStringInvN(lmid, 2, "", rmid);
    LCD_drawNumber3DInv(20, 4, val0);
    LCD_drawCharInv(20, 5, val0);
    LCD_drawNumber3DInv(rmid, 4, val1);
    LCD_drawCharInv(rmid, 5, val1);

    if (Btn_Nav_CurStates.fields.btn_nav_Up) {
    	LCD_drawStringInvN(lmid, 3, "Up", rmid);
    	LCD_drawChar(0,3,' ');
    } else {
    	LCD_drawCharInv(0,3,'-');
    }
    if (Btn_Nav_CurStates.fields.btn_nav_Down) {
    	LCD_drawStringInvN(lmid, 3, "Down", rmid);
    	LCD_drawChar(0,5,' ');
    } else {
    	LCD_drawCharInv(0,5,'-');
    }
    if (Btn_Nav_CurStates.fields.btn_nav_Enter) {
    	LCD_drawStringInvN(lmid, 3, "Enter", rmid);
    	LCD_drawChar(121,7,' ');
	} else {
		LCD_drawCharInv(121,7,'-');
	}
    if (Btn_Nav_CurStates.fields.btn_nav_Back) {
    	LCD_drawStringInvN(lmid, 3, "Back", rmid);
    	LCD_drawChar(0,7,' ');
    } else {
    	LCD_drawCharInv(0,7,'-');
    }
    if (Btn_Nav_CurStates.fields.btn_nav_Home) {
    	LCD_drawStringInvN(lmid, 3, "Home", rmid);
    }
    if (Btn_Nav_CurStates.fields.btn_nav_Left) {
    	LCD_drawStringInvN(lmid, 3, "Left", rmid);
    	LCD_drawChar(121,3,' ');
	} else {
		LCD_drawCharInv(121,3,'-');
	}
    if (Btn_Nav_CurStates.fields.btn_nav_Right) {
    	LCD_drawStringInvN(lmid, 3, "Right", rmid);
    	LCD_drawChar(121,5,' ');
	} else {
		LCD_drawCharInv(121,5,'-');
	}
    if (Btn_Nav_CurStates.fields.btn_nav_Shift) {
    	LCD_drawStringInvN(lmid, 3, "Shift", rmid);
    }
}

const KeyValuePair_t RootMenu = {
  KVP_TYPE_AVP, "--- AXOLOTI ---", .avp = {MainMenu, MainMenu_length}
};

KeyValuePair_t MainMenu[MainMenu_length] = {
  { KVP_TYPE_APVP, "Patch", .apvp = {(int *)&ObjectKvps, 0}},
  { KVP_TYPE_AVP, "SDCard", .avp = {SdcMenu, SdcMenu_length}},
  { KVP_TYPE_AVP, "ADCs", .avp = {ADCMenu, ADCMenu_length}},
  { KVP_TYPE_IVP, "dsp%", .ivp = {&dspLoadPct, 0, 100}},
  { KVP_TYPE_CUSTOM, "Test", .custom = {&TestDisplayFunction, TestButtonFunction, 0}},
  { KVP_TYPE_AVP, "Food", .avp = {FoodMenu, FoodMenu_length}}
};

const KeyValuePair_t SdcMenu[SdcMenu_length] = {
  { KVP_TYPE_FNCTN, "Load patch", .fnctnvp = {&EnterMenuLoad}},
  { KVP_TYPE_FNCTN, "Format", .fnctnvp = {&EnterMenuFormat}}
};

const KeyValuePair_t ADCMenu[ADCMenu_length] = {
  { KVP_TYPE_SVP, "ADC0", .svp = {(int16_t *)&adcvalues[0]}},
  { KVP_TYPE_SVP, "ADC1", .svp = {(int16_t *)&adcvalues[1]}},
  { KVP_TYPE_SVP, "ADC2", .svp = {(int16_t *)&adcvalues[2]}},
  { KVP_TYPE_SVP, "ADC3", .svp = {(int16_t *)&adcvalues[3]}},
  { KVP_TYPE_SVP, "ADC4", .svp = {(int16_t *)&adcvalues[4]}},
  { KVP_TYPE_SVP, "ADC5", .svp = {(int16_t *)&adcvalues[5]}},
  { KVP_TYPE_SVP, "ADC6", .svp = {(int16_t *)&adcvalues[6]}},
  { KVP_TYPE_SVP, "ADC7", .svp = {(int16_t *)&adcvalues[7]}},
  { KVP_TYPE_SVP, "ADC8", .svp = {(int16_t *)&adcvalues[8]}},
  { KVP_TYPE_SVP, "ADC9", .svp = {(int16_t *)&adcvalues[9]}},
  { KVP_TYPE_SVP, "ADC10", .svp = {(int16_t *)&adcvalues[10]}},
  { KVP_TYPE_SVP, "ADC11", .svp = {(int16_t *)&adcvalues[11]}},
  { KVP_TYPE_SVP, "ADC12", .svp = {(int16_t *)&adcvalues[12]}},
  { KVP_TYPE_SVP, "ADC13", .svp = {(int16_t *)&adcvalues[13]}},
  { KVP_TYPE_SVP, "ADC14", .svp = {(int16_t *)&adcvalues[14]}}
};

#define AppleMenu_length 4
const KeyValuePair_t AppleMenu[AppleMenu_length] = {
  { KVP_TYPE_FNCTN, "JamesGrieve", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "GrannySmith", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Jonagold", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Cox", .fnctnvp = {NULL}},
};

#define NutsMenu_length 10
const KeyValuePair_t NutsMenu[NutsMenu_length] = {
  { KVP_TYPE_FNCTN, "Cashew", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Peanut", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Pecan", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Walnut", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Pistachio", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Hazelnut", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Coconut", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Brazil nut", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Macadamia", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Almond", .fnctnvp = {NULL}}
};

#define DishMenu_length 8
const KeyValuePair_t DishMenu[DishMenu_length] = {
  { KVP_TYPE_FNCTN, "Cake", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Salad", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Soup", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Waffle", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Ice cream", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Rice", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Spaghetti", .fnctnvp = {NULL}},
  { KVP_TYPE_FNCTN, "Pizza", .fnctnvp = {NULL}}
};

const KeyValuePair_t FoodMenu[FoodMenu_length] = {
  { KVP_TYPE_AVP, "Nuts", .avp = {NutsMenu, NutsMenu_length}},
  { KVP_TYPE_AVP, "Apple", .avp = {AppleMenu, AppleMenu_length}},
  { KVP_TYPE_AVP, "Dish", .avp = {DishMenu, DishMenu_length}}
};

void SetKVP_APVP(KeyValuePair_t *kvp, KeyValuePair_t *parent,
                 const char *keyName, int length, KeyValuePair_t **array) {
  kvp->kvptype = KVP_TYPE_APVP;
  kvp->keyname = keyName;
  kvp->apvp.length = length;
  kvp->apvp.array = (void *)array;
}

void SetKVP_AVP(KeyValuePair_t *kvp, const KeyValuePair_t *parent,
                const char *keyName, int length, const KeyValuePair_t *array) {
  kvp->kvptype = KVP_TYPE_AVP;
  kvp->keyname = keyName;
  kvp->avp.length = length;
  kvp->avp.array = array;
}

void SetKVP_IVP(KeyValuePair_t *kvp, KeyValuePair_t *parent,
                const char *keyName, int *value, int min, int max) {
  kvp->kvptype = KVP_TYPE_IVP;
  kvp->keyname = keyName;
  kvp->ivp.value = value;
  kvp->ivp.minvalue = min;
  kvp->ivp.maxvalue = max;
}

void SetKVP_IPVP(KeyValuePair_t *kvp, KeyValuePair_t *parent,
                 const char *keyName, ParameterExchange_t *PEx, int min,
                 int max) {
  PEx->signals = 0x0F;
  kvp->kvptype = KVP_TYPE_IPVP;
  kvp->keyname = keyName;
  kvp->ipvp.PEx = PEx;
  kvp->ipvp.minvalue = min;
  kvp->ipvp.maxvalue = max;
}

void SetKVP_FNCTN(KeyValuePair_t *kvp, KeyValuePair_t *parent,
                  const char *keyName, VoidFunction fnctn) {
  kvp->kvptype = KVP_TYPE_FNCTN;
  kvp->keyname = keyName;
  kvp->fnctnvp.fnctn = fnctn;
}

void SetKVP_CUSTOM(KeyValuePair_t *kvp, KeyValuePair_t *parent,
                  const char *keyName, DisplayFunction dispfnctn, ButtonFunction btnfnctn, void* userdata) {
  kvp->kvptype = KVP_TYPE_CUSTOM;
  kvp->keyname = keyName;
  kvp->custom.displayFunction = dispfnctn;
  kvp->custom.buttonFunction = btnfnctn;
  kvp->custom.userdata = userdata;
}

inline void KVP_Increment(const KeyValuePair_t *kvp) {
  switch (kvp->kvptype) {
  case KVP_TYPE_IVP:
    if (*kvp->ivp.value < kvp->ivp.maxvalue)
      (*kvp->ivp.value)++;
    break;
  case KVP_TYPE_AVP: {
    if (menu_stack[menu_stack_position].currentpos < (kvp->avp.length - 1))
    	menu_stack[menu_stack_position].currentpos++;
	LED_set(0,0);
	LED_addOne(0, (15*menu_stack[menu_stack_position].currentpos)/(kvp->avp.length - 1) ,1 );
  } break;
  case KVP_TYPE_APVP:
    if (menu_stack[menu_stack_position].currentpos < (kvp->apvp.length - 1))
    	menu_stack[menu_stack_position].currentpos++;
    break;
  case KVP_TYPE_U7VP:
    if (*kvp->u7vp.value < kvp->u7vp.maxvalue)
      (*kvp->u7vp.value) += 1;
    break;
  case KVP_TYPE_IPVP: {
    int32_t nval = kvp->ipvp.PEx->value + (1 << 20);
    if (nval < kvp->ipvp.maxvalue) {
      PExParameterChange(kvp->ipvp.PEx, nval, 0xFFFFFFE7);
    }
    else {
      PExParameterChange(kvp->ipvp.PEx, kvp->ipvp.maxvalue, 0xFFFFFFE7);
    }
  }
    break;
  default:
    break;
  }
}

inline void KVP_Decrement(const KeyValuePair_t *kvp) {
  switch (kvp->kvptype) {
  case KVP_TYPE_IVP:
    if (*kvp->ivp.value > kvp->ivp.minvalue)
      (*kvp->ivp.value)--;
    break;
  case KVP_TYPE_AVP:
    if (menu_stack[menu_stack_position].currentpos > 0)
      menu_stack[menu_stack_position].currentpos--;
	LED_set(0,0);
	LED_addOne(0, (15*menu_stack[menu_stack_position].currentpos)/(kvp->avp.length - 1) ,1 );
    break;
  case KVP_TYPE_APVP:
    if (menu_stack[menu_stack_position].currentpos > 0)
    	menu_stack[menu_stack_position].currentpos--;
    break;
  case KVP_TYPE_U7VP:
    if (*kvp->u7vp.value > kvp->u7vp.minvalue)
      (*kvp->u7vp.value)--;
    break;
  case KVP_TYPE_IPVP: {
    int32_t nval = kvp->ipvp.PEx->value - (1 << 20);
    if (nval > kvp->ipvp.minvalue) {
      PExParameterChange(kvp->ipvp.PEx, nval, 0xFFFFFFE7);
    }
    else {
      PExParameterChange(kvp->ipvp.PEx, kvp->ipvp.minvalue, 0xFFFFFFE7);
    }
  }
    break;
  default:
    break;
  }
}

/*
 * Create menu tree from file tree
 */

//KeyValuePair_t LoadMenu;

void EnterMenuLoadFile(void) {
// Todo: implement patch load menu
/*
  KeyValuePair_t *F =
      &((KeyValuePair_t *)(LoadMenu.avp.array))[LoadMenu.avp.current];

  char str[20] = "0:";
  strcat(str, F->keyname);

  LoadPatch(str);
*/
}

static void UIUpdateLCD(void);
static void UIPollButtons(void);

void AxolotiControlUpdate(void) {
    UIPollButtons();
    UIUpdateLCD();
}

void (*pControlUpdate)(void) = AxolotiControlUpdate;

static WORKING_AREA(waThreadUI2, 512);
static THD_FUNCTION(ThreadUI2, arg) {
  (void)(arg);
  chRegSetThreadName("ui2");
  while (1) {
    if(pControlUpdate != 0L) {
        pControlUpdate();
    }
    chThdSleepMilliseconds(15);
  }
}


void UIGoSafe(void) {
	menu_stack_position = 0;
}

#if 0 // obsolete?
static KeyValuePair_t* userDisplay;

void UISetUserDisplay(DisplayFunction dispfnctn, ButtonFunction btnfnctn, void* userdata) {
	if(userDisplay!=0) {
		userDisplay->custom.displayFunction = dispfnctn;
		userDisplay->custom.buttonFunction = btnfnctn;
		userDisplay->custom.userdata = userdata;
	}
}
#endif

void ui_init(void) {
  Btn_Nav_Or.word = 0;
  Btn_Nav_And.word = ~0;

  chThdCreateStatic(waThreadUI2, sizeof(waThreadUI2), NORMALPRIO, ThreadUI2, NULL);
  axoloti_control_init();

  int i;
  for(i=0;i<2;i++) {
	  EncBuffer[i]=0;
  }
}

void KVP_ClearObjects(void) {
  ObjectKvpRoot->apvp.length = 0;
  menu_stack_position = 0;
}

void KVP_RegisterObject(KeyValuePair_t *kvp) {
  if (ObjectKvpRoot->apvp.length < MAXOBJECTS) {
	ObjectKvps[ObjectKvpRoot->apvp.length] = kvp;
    ObjectKvpRoot->apvp.length++;
  }
}

#define LCD_COL_EQ 91
#define LCD_COL_VAL 92
#define LCD_COL_ENTER LCD_COL_LEFT

void KVP_DisplayInv(int x, int y, const KeyValuePair_t *kvp) {
  LCD_drawStringInvN(x, y, kvp->keyname, LCD_COL_EQ);
  switch (kvp->kvptype) {
  case KVP_TYPE_U7VP:
    LCD_drawCharInv(LCD_COL_EQ, y, '=');
    LCD_drawNumber3DInv(LCD_COL_VAL, y, (*kvp->u7vp.value));
    break;
  case KVP_TYPE_IVP:
    LCD_drawCharInv(LCD_COL_EQ, y, '=');
    LCD_drawNumber3DInv(LCD_COL_VAL, y, *kvp->ivp.value);
    break;
  case KVP_TYPE_FVP:
    LCD_drawStringInvN(LCD_COL_EQ, y, "     F", LCDWIDTH);
    break;
  case KVP_TYPE_AVP:
    LCD_drawStringInvN(LCD_COL_EQ, y, "     *", LCDWIDTH);
    break;
  case KVP_TYPE_IDVP:
    LCD_drawIBAR(LCD_COL_EQ, y, *kvp->idvp.value, LCDWIDTH);
    break;
  case KVP_TYPE_SVP:
    LCD_drawCharInv(LCD_COL_EQ, y, '=');
    LCD_drawNumber5DInv(LCD_COL_VAL, y, *kvp->svp.value);
    break;
  case KVP_TYPE_APVP:
    LCD_drawStringInvN(LCD_COL_EQ, y, "     #", LCDWIDTH);
    break;
  case KVP_TYPE_CUSTOM:
    LCD_drawStringInvN(LCD_COL_EQ, y, "     @", LCDWIDTH);
    break;
  case KVP_TYPE_IPVP:
    LCD_drawCharInv(LCD_COL_EQ, y, '=');
    LCD_drawNumber3DInv(LCD_COL_VAL, y, (kvp->ipvp.PEx->value) >> 20);
    break;
  default:
    break;
  }
}

void KVP_Display(int x, int y, const KeyValuePair_t *kvp) {
  LCD_drawStringN(x, y, kvp->keyname, LCD_COL_EQ);
  switch (kvp->kvptype) {
  case KVP_TYPE_U7VP:
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber3D(LCD_COL_VAL, y, (*kvp->u7vp.value));
    break;
  case KVP_TYPE_IVP:
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber3D(LCD_COL_VAL, y, *kvp->ivp.value);
    break;
  case KVP_TYPE_FVP:
    LCD_drawStringN(LCD_COL_EQ, y, "     F", LCDWIDTH);
    break;
  case KVP_TYPE_AVP:
    LCD_drawStringN(LCD_COL_EQ, y, "     *", LCDWIDTH);
    break;
  case KVP_TYPE_IDVP:
    LCD_drawIBAR(LCD_COL_EQ, y, *kvp->idvp.value, LCDWIDTH);
    break;
  case KVP_TYPE_SVP:
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber5D(LCD_COL_VAL, y, *kvp->svp.value);
    break;
  case KVP_TYPE_APVP:
    LCD_drawStringN(LCD_COL_EQ, y, "     #", LCDWIDTH);
    break;
  case KVP_TYPE_CUSTOM:
    LCD_drawStringN(LCD_COL_EQ, y, "     @", LCDWIDTH);
    break;
  case KVP_TYPE_IPVP:
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber3D(LCD_COL_VAL, y, (kvp->ipvp.PEx->value) >> 20);
    break;
  case KVP_TYPE_INTDISPLAY:
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber3D(LCD_COL_VAL, y, *kvp->idv.value >> 20);
//      LCD_drawChar(43+24+x,y,'.');
//      LCD_drawChar(43+24+6+x,y,'0'+(((10*(*kvp->idv.value)&0xfffff))>>20));
    break;
  case KVP_TYPE_PITCHDISPLAY:
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber3D(LCD_COL_VAL, y, *kvp->pdv.value >> 20);
//      LCD_drawChar(43+24+x,y,'.');
//      LCD_drawChar(43+24+6+x,y,'0'+(((10*(*kvp->pdv.value)&0xfffff))>>20));
    break;
  case KVP_TYPE_FREQDISPLAY: {
    int f10 = ___SMMUL(*kvp->freqdv.value, 48000);
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber5D(LCD_COL_VAL, y, f10);
  }
    break;
  case KVP_TYPE_FRACTDISPLAY: {
    int f10 = ___SMMUL(*kvp->fractdv.value, 160);
    LCD_drawChar(LCD_COL_EQ, y, '=');
    LCD_drawNumber3D(LCD_COL_VAL, y, f10 / 10);
    LCD_drawChar(LCD_COL_VAL + 24 + x, y, '.');
    LCD_drawChar(LCD_COL_VAL + 24 + 6 + x, y, '0' + (f10 - (10 * (f10 / 10))));
  }
    break;
  default:
    break;
  }
}



/*
 * We need one uniform state for the buttons, whether controlled from the GUI or from Axoloti Control.
 * btn_or is a true if the button was down during the last time interval
 * btn_and is false if the button was released after being held down in the last time interval
 *
 * say btn_or1, btn_and1 is from control source 1
 * say btn_or2, btn_and2 is from control source 2
 *
 * Current state |= (btn_or1 | btn_or2)
 * process_buttons()
 * Prev_state = Current state & btn_and1 & btn_and2
 *
 *
 * a click within a time interval is transmitted as btn_or = 1, btn_and = 0
 * It is desirable that the current state is true during a whole process interval.
 *
 *
 *
 * btn1or    0 0 1 0
 * btn1and   1 1 0 1
 * curstate  0 0 1 0
 * prevstate 0 0 0 0
 *               down_evt
 *                 no up_evt detectable from cur/prev!
 */


static void UIPollButtons(void) {
  Btn_Nav_CurStates.word = Btn_Nav_CurStates.word | Btn_Nav_Or.word;
  Btn_Nav_Or.word = 0;
  const KeyValuePair_t * KvpsDisplay = menu_stack[menu_stack_position].parent;
  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    KeyValuePair_t *cur =
        &((KeyValuePair_t *)(KvpsDisplay->avp.array))[menu_stack[menu_stack_position].currentpos];
    if (BTN_NAV_DOWN(btn_nav_Down))
      KVP_Increment(KvpsDisplay);
    if (EncBuffer[0]>0) {
    	KVP_Increment(KvpsDisplay);
    	EncBuffer[0]=0;
    }
    if (BTN_NAV_DOWN(btn_nav_Up))
      KVP_Decrement(KvpsDisplay);
    if (EncBuffer[0]<0) {
    	KVP_Decrement(KvpsDisplay);
    	EncBuffer[0]=0;
    }
    if (BTN_NAV_DOWN(btn_nav_Left))
      KVP_Decrement(cur);
    if (BTN_NAV_DOWN(btn_nav_Right))
      KVP_Increment(cur);
    if (BTN_NAV_DOWN(btn_nav_Enter)) {
      if ((cur->kvptype == KVP_TYPE_AVP) || (cur->kvptype == KVP_TYPE_APVP)
          || (cur->kvptype == KVP_TYPE_CUSTOM)) {
    	if (menu_stack_position < menu_stack_size-1) {
        	LCD_clear();
			menu_stack[menu_stack_position+1].parent = cur;
			menu_stack[menu_stack_position+1].currentpos = 0;
			menu_stack_position++;
			if (cur->kvptype == KVP_TYPE_CUSTOM) {
				const KeyValuePair_t * KvpsDisplay = menu_stack[menu_stack_position].parent;
			    if (KvpsDisplay->custom.displayFunction != 0) (KvpsDisplay->custom.displayFunction)(KvpsDisplay->custom.userdata, 1);
			}
    	}
      } else if (cur->kvptype == KVP_TYPE_FNCTN)
        if (cur->fnctnvp.fnctn != 0)
          (cur->fnctnvp.fnctn)();
    }
    if (BTN_NAV_DOWN(btn_nav_Back)) {
    	if (menu_stack_position > 0) menu_stack_position--;
    	LCD_clear();
    }
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_APVP) {
    const KeyValuePair_t *cur =
        (const KeyValuePair_t *)(KvpsDisplay->apvp.array[menu_stack[menu_stack_position].currentpos]);
    if (BTN_NAV_DOWN(btn_nav_Down))
      KVP_Increment(KvpsDisplay);
    if (BTN_NAV_DOWN(btn_nav_Up))
      KVP_Decrement(KvpsDisplay);
    if (BTN_NAV_DOWN(btn_nav_Left))
      KVP_Decrement(cur);
    if (BTN_NAV_DOWN(btn_nav_Right))
      KVP_Increment(cur);
    if (BTN_NAV_DOWN(btn_nav_Enter)) {
      if ((cur->kvptype == KVP_TYPE_AVP) || (cur->kvptype == KVP_TYPE_APVP)
          || (cur->kvptype == KVP_TYPE_CUSTOM)) {
      	if (menu_stack_position < menu_stack_size-1) {
			menu_stack[menu_stack_position+1].parent = cur;
			menu_stack[menu_stack_position+1].currentpos = 0;
			menu_stack_position++;
			if (cur->kvptype == KVP_TYPE_CUSTOM) {
				const KeyValuePair_t * KvpsDisplay = menu_stack[menu_stack_position].parent;
			    if (KvpsDisplay->custom.displayFunction != 0) (KvpsDisplay->custom.displayFunction)(KvpsDisplay->custom.userdata, 1);
			}
      	}
      } else if (cur->kvptype == KVP_TYPE_FNCTN)
        if (cur->fnctnvp.fnctn != 0)
          (cur->fnctnvp.fnctn)();
    }
    if (BTN_NAV_DOWN(btn_nav_Back))
      if (menu_stack_position > 0) menu_stack_position--;
      LCD_clear();
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_CUSTOM) {
    if (KvpsDisplay->custom.buttonFunction != 0) (KvpsDisplay->custom.buttonFunction)(KvpsDisplay->custom.userdata);
    if (BTN_NAV_DOWN(btn_nav_Back)) {
	  if (menu_stack_position > 0) menu_stack_position--;
      LCD_clear();
    }
  }


// process encoder // todo: more than just one encoder...
  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    KeyValuePair_t *cur =
        &((KeyValuePair_t *)(KvpsDisplay->avp.array))[menu_stack[menu_stack_position].currentpos];
    if ((cur->kvptype == KVP_TYPE_IVP) || (cur->kvptype == KVP_TYPE_IPVP)) {
      while (EncBuffer[0] > 0) {
        KVP_Increment(cur);
        EncBuffer[0]--;
      }
      while (EncBuffer[0] < 0) {
        KVP_Decrement(cur);
        EncBuffer[0]++;
      }
    }
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_APVP) {
    KeyValuePair_t *cur =
        (KeyValuePair_t *)(KvpsDisplay->apvp.array[menu_stack[menu_stack_position].currentpos]);
    if (EncBuffer[0] > 0) {
      KVP_Increment(KvpsDisplay);
      EncBuffer[0] = 0;
    } else if (EncBuffer[0] < 0) {
	  KVP_Decrement(KvpsDisplay);
	  EncBuffer[0] = 0;
    }
    if (BTN_NAV_DOWN(btn_nav_Up))
      KVP_Decrement(KvpsDisplay);
    if ((cur->kvptype == KVP_TYPE_IVP) || (cur->kvptype == KVP_TYPE_IPVP)) {
      while (EncBuffer[1] > 0) {
        KVP_Increment(cur);
        EncBuffer[1]--;
      }
      while (EncBuffer[1] < 0) {
        KVP_Decrement(cur);
        EncBuffer[1]++;
      }
    }
  }

  Btn_Nav_CurStates.word = Btn_Nav_CurStates.word & ~Btn_Nav_And.word;
  Btn_Nav_PrevStates = Btn_Nav_CurStates;
  Btn_Nav_And.word = 0;
}

static void UIUpdateLCD(void) {
  const KeyValuePair_t * KvpsDisplay = menu_stack[menu_stack_position].parent;
  KVP_DisplayInv(0, 0, KvpsDisplay);
  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    int c = menu_stack[menu_stack_position].currentpos;
    int l = KvpsDisplay->avp.length;
    KeyValuePair_t *k = (KeyValuePair_t *)KvpsDisplay->avp.array;
	int offset = 0;
	if (c > 3) offset = c - 3;
	if ((l-c) < 3) offset = l - 6;
	if (l < STATUSROW) offset = 0;
	LCD_drawChar(0,3,c>0?CHAR_ARROW_UP:0);
	LCD_drawChar(0,5,c<(l-1)?CHAR_ARROW_DOWN:0);
	LCD_drawChar(122,3,'+');
	LCD_drawChar(122,5,'-');
	int line;
	for (line = 0; line < (STATUSROW-1); line++) {
		if (offset+line < l) {
			if (offset+line == c)
			   KVP_DisplayInv(LCD_COL_INDENT, line+1, &k[offset+line]);
			else
			   KVP_Display(LCD_COL_INDENT, line+1, &k[offset+line]);
		} else // blank
		  LCD_drawStringN(LCD_COL_INDENT, line+1, " ", LCDWIDTH-6);
	}
    if (menu_stack_position > 0) {
      LCD_drawStringInv(0, STATUSROW, "BACK");
      LCD_drawString(24, STATUSROW, "     ");
    }
    else
      LCD_drawString(0, STATUSROW, "          ");
    KeyValuePair_t * sel = &k[menu_stack[menu_stack_position].currentpos];
    if ((sel->kvptype == KVP_TYPE_AVP)
        || (sel->kvptype == KVP_TYPE_APVP)
        || (sel->kvptype == KVP_TYPE_CUSTOM))
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else if (sel->kvptype == KVP_TYPE_FNCTN)
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else
      LCD_drawString(LCD_COL_ENTER, STATUSROW, "     ");
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_APVP) {
    int c = menu_stack[menu_stack_position].currentpos;
    int l = KvpsDisplay->apvp.length;
    KeyValuePair_t **k = (KeyValuePair_t **)KvpsDisplay->apvp.array;
	int offset = 0;
	if (c > 3) offset = c - 3;
	if ((l-c) < 3) offset = l - 6;
	if (l < STATUSROW) offset = 0;
	LCD_drawChar(0,3,c>0?CHAR_ARROW_UP:0);
	LCD_drawChar(0,5,c<(l-1)?CHAR_ARROW_DOWN:0);
	LCD_drawChar(122,3,'-');
	LCD_drawChar(122,5,'+');
	int line;
	for (line = 0; line < (STATUSROW-1); line++) {
		if (offset+line < l) {
			if (offset+line == c)
			   KVP_DisplayInv(LCD_COL_INDENT, line+1, k[offset+line]);
			else
			   KVP_Display(LCD_COL_INDENT, line+1, k[offset+line]);
		} else // blank
		  LCD_drawStringN(LCD_COL_INDENT, line+1, " ", LCDWIDTH);
	}
    if (menu_stack_position > 0) {
      LCD_drawStringInv(0, STATUSROW, "BACK");
      LCD_drawString(24, STATUSROW, "     ");
    }
    else
      LCD_drawString(0, STATUSROW, "          ");
    KeyValuePair_t * sel = k[menu_stack[menu_stack_position].currentpos];
    if ((sel->kvptype == KVP_TYPE_AVP)
        || (sel->kvptype == KVP_TYPE_APVP)
        || (sel->kvptype == KVP_TYPE_CUSTOM))
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else if (sel->kvptype == KVP_TYPE_FNCTN)
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else
      LCD_drawString(LCD_COL_ENTER, STATUSROW, "     ");
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_CUSTOM) {
	  if (KvpsDisplay->custom.displayFunction != 0) (KvpsDisplay->custom.displayFunction)(KvpsDisplay->custom.userdata, 0);
  }

#if 0 // show protocol diagnostics
  static int counter = 0;
  counter++;
  LCD_drawNumberHex32(0, 0, spilink_rx[0].header);
  LCD_drawNumberHex32(0, 1, spilink_rx[0].frameno);
  LCD_drawNumberHex32(0, 2, spilink_rx[0].control_type);
  LCD_drawNumberHex32(0, 3, Btn_Nav_CurStates.word);
  LCD_drawNumberHex32(0, 4, Btn_Nav_And.word);
  LCD_drawNumberHex32(0, 5, Btn_Nav_Or.word);
  LCD_drawNumberHex32(0, 6, counter);
#endif
}

#if 0 // obsolete

void k_scope_disp_frac32_64(void * userdata) {
// userdata  int32_t[64], one sample per column
  const int indent = (128 - (15 + 64)) / 2 ;
  int i;
  LCD_clear();
  for (i = 0; i < 48; i++) {
    LCD_setPixel(index + 14, i);
  }
  LCD_drawString(indent + 5, 0, "1");
  LCD_drawString(indent + 5, 2, "0");
  LCD_drawString(indent +0, 4, "-1");
  LCD_setPixel(indent + 13, 21);
  LCD_setPixel(indent + 12, 21);
  LCD_setPixel(indent + 13, 21 + 16);
  LCD_setPixel(indent + 12, 21 + 16);
  LCD_setPixel(indent + 13, 21 - 16);
  LCD_setPixel(indent + 12, 21 - 16);
  LCD_drawStringInv(0, STATUSROW, "BACK");
  LCD_drawStringInv(LCD_COL_LEFT, STATUSROW, "HOLD");
  for (i = 0; i < 64; i++) {
    int y = ((int *)userdata)[i];
    y = 21 - (y >> 23);
    if (y < 1)
      y = 1;
    if (y > 47)
      y = 47;
    LCD_setPixel(indent + i + 15, y);
  }
}

void k_scope_disp_frac32_minmax_64(void * userdata) {
// userdata  int32_t[64][2], minimum and maximum per column
  const int indent = (128 - (15 + 64)) / 2 ;
  int i;
  LCD_clear();
  for (i = 0; i < 48; i++) {
    LCD_setPixel(indent + 14, i);
  }
  LCD_drawString(indent + 5, 0, "1");
  LCD_drawString(indent + 5, 2, "0");
  LCD_drawString(indent + 0, 4, "-1");
  LCD_setPixel(indent + 13, 21);
  LCD_setPixel(indent + 12, 21);
  LCD_setPixel(indent + 13, 21 + 16);
  LCD_setPixel(indent + 12, 21 + 16);
  LCD_setPixel(indent + 13, 21 - 16);
  LCD_setPixel(indent + 12, 21 - 16);
  LCD_drawStringInv(0, STATUSROW, "BACK");
  LCD_drawStringInv(LCD_COL_LEFT, STATUSROW, "HOLD");

  for (i = 0; i < 64; i++) {
    int y = ((int *)userdata)[i * 2 + 1];
    y = 21 - (y >> 23);
    if (y < 1)
      y = 1;
    if (y > 47)
      y = 47;
    int y2 = ((int *)userdata)[i * 2];
    y2 = 21 - (y2 >> 23);
    if (y2 < 1)
      y2 = 1;
    if (y2 > 47)
      y2 = 47;
    int j;

    if (y2 <= (y))
      y2 = y + 1;
    for (j = y; j < y2; j++)
      LCD_setPixel(indent + i + 15, j);
  }
}

void k_scope_disp_frac32buffer_64(void * userdata) {
	k_scope_disp_frac32_64(userdata);
}

#endif
