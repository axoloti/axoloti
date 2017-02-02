/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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
#include "ff.h"
#include <string.h>

Btn_Nav_States_struct Btn_Nav_CurStates;
Btn_Nav_States_struct Btn_Nav_PrevStates;
Btn_Nav_States_struct Btn_Nav_Or;
Btn_Nav_States_struct Btn_Nav_And;

int8_t EncBuffer[4];

struct KeyValuePair KvpsHead;
struct KeyValuePair *KvpsDisplay;
struct KeyValuePair *ObjectKvpRoot;
#define MAXOBJECTS 256
struct KeyValuePair *ObjectKvps[MAXOBJECTS];
#define MAXTMPMENUITEMS 15
KeyValuePair_s TmpMenuKvps[MAXTMPMENUITEMS];
KeyValuePair_s ADCkvps[3];

//const char stat = 2;

void SetKVP_APVP(KeyValuePair_s *kvp, KeyValuePair_s *parent,
                 const char *keyName, int length, KeyValuePair_s *array) {
  kvp->kvptype = KVP_TYPE_APVP;
  kvp->parent = (void *)parent;
  kvp->keyname = keyName;
  kvp->apvp.length = length;
  kvp->apvp.current = 0;
  kvp->apvp.array = (void *)array;
}

void SetKVP_AVP(KeyValuePair_s *kvp, KeyValuePair_s *parent,
                const char *keyName, int length, KeyValuePair_s *array) {
  kvp->kvptype = KVP_TYPE_AVP;
  kvp->parent = (void *)parent;
  kvp->keyname = keyName;
  kvp->avp.length = length;
  kvp->avp.current = 0;
  kvp->avp.array = array;
}

void SetKVP_IVP(KeyValuePair_s *kvp, KeyValuePair_s *parent,
                const char *keyName, int *value, int min, int max) {
  kvp->kvptype = KVP_TYPE_IVP;
  kvp->parent = (void *)parent;
  kvp->keyname = keyName;
  kvp->ivp.value = value;
  kvp->ivp.minvalue = min;
  kvp->ivp.maxvalue = max;
}

void SetKVP_IPVP(KeyValuePair_s *kvp, KeyValuePair_s *parent,
                 const char *keyName, ParameterExchange_t *PEx, int min,
                 int max) {
  PEx->signals = 0x0F;
  kvp->kvptype = KVP_TYPE_IPVP;
  kvp->parent = (void *)parent;
  kvp->keyname = keyName;
  kvp->ipvp.PEx = PEx;
  kvp->ipvp.minvalue = min;
  kvp->ipvp.maxvalue = max;
}

void SetKVP_FNCTN(KeyValuePair_s *kvp, KeyValuePair_s *parent,
                  const char *keyName, VoidFunction fnctn) {
  kvp->kvptype = KVP_TYPE_FNCTN;
  kvp->parent = (void *)parent;
  kvp->keyname = keyName;
  kvp->fnctnvp.fnctn = fnctn;
}

inline void KVP_Increment(KeyValuePair_s *kvp) {
  switch (kvp->kvptype) {
  case KVP_TYPE_IVP:
    if (*kvp->ivp.value < kvp->ivp.maxvalue)
      (*kvp->ivp.value)++;
    break;
  case KVP_TYPE_AVP:
    if (kvp->avp.current < (kvp->avp.length - 1))
      kvp->avp.current++;
    break;
  case KVP_TYPE_APVP:
    if (kvp->apvp.current < (kvp->apvp.length - 1))
      kvp->apvp.current++;
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

inline void KVP_Decrement(KeyValuePair_s *kvp) {
  switch (kvp->kvptype) {
  case KVP_TYPE_IVP:
    if (*kvp->ivp.value > kvp->ivp.minvalue)
      (*kvp->ivp.value)--;
    break;
  case KVP_TYPE_AVP:
    if (kvp->avp.current > 0)
      kvp->avp.current--;
    break;
  case KVP_TYPE_APVP:
    if (kvp->apvp.current > 0)
      kvp->apvp.current--;
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

void k_scope_DisplayFunction(void * userdata) {
// userdata  int32_t[64], one sample per column
  int i;
  LCD_clearDisplay();
  for (i = 0; i < 48; i++) {
    LCD_setPixel(14, i);
  }
  LCD_drawString(5, 0, "1");
  LCD_drawString(5, 2, "0");
  LCD_drawString(0, 4, "-1");
  LCD_setPixel(13, 21);
  LCD_setPixel(12, 21);
  LCD_setPixel(13, 21 + 16);
  LCD_setPixel(12, 21 + 16);
  LCD_setPixel(13, 21 - 16);
  LCD_setPixel(12, 21 - 16);
  LCD_drawStringInv(0, 5, "BACK");
  LCD_drawStringInv(58, 5, "HOLD");
  for (i = 0; i < 64; i++) {
    int y = ((int *)userdata)[i];
    y = 21 - (y >> 23);
    if (y < 1)
      y = 1;
    if (y > 47)
      y = 47;
    LCD_setPixel(i + 15, y);
  }
}

void k_scope_DisplayFunction2(void * userdata) {
// userdata  int32_t[64][2], minimum and maximum per column
  int i;
  LCD_clearDisplay();
  for (i = 0; i < 48; i++) {
    LCD_setPixel(14, i);
  }
  LCD_drawString(5, 0, "1");
  LCD_drawString(5, 2, "0");
  LCD_drawString(0, 4, "-1");
  LCD_setPixel(13, 21);
  LCD_setPixel(12, 21);
  LCD_setPixel(13, 21 + 16);
  LCD_setPixel(12, 21 + 16);
  LCD_setPixel(13, 21 - 16);
  LCD_setPixel(12, 21 - 16);
  LCD_drawStringInv(0, 5, "BACK");
  LCD_drawStringInv(58, 5, "HOLD");

  LCD_drawString(27, 5, "-.ms");

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
//			LCD_setPixel(i,y);
//			LCD_setPixel(i,y2);

    if (y2 <= (y))
      y2 = y + 1;
    for (j = y; j < y2; j++)
      LCD_setPixel(i + 15, j);
  }
}

void k_scope_DisplayFunction3(void * userdata) {
  (void)userdata;
}

void k_scope_DisplayFunction4(void * userdata) {
  (void)userdata;
}

void k_value_DisplayFunction(void * userdata) {
  (void)userdata;
}

#define POLLENC(NAME, INCREMENT_FUNCTION, DECREMENT_FUNCTION)  \
      if (!expander_PrevStates.NAME##A) {                 \
          if (!expander_PrevStates.NAME##B) {             \
              if (expander_CurStates.NAME##B) {           \
                  expander_PrevStates.NAME##B = 1;        \
                  DECREMENT_FUNCTION                      \
              } else if (expander_CurStates.NAME##A) {    \
                  expander_PrevStates.NAME##A = 1;        \
                  INCREMENT_FUNCTION                      \
              }                                           \
          } else {                                        \
              if (expander_CurStates.NAME##A) {           \
                  expander_PrevStates.NAME##A = 1;        \
              } else if (!expander_CurStates.NAME##B) {   \
                  expander_PrevStates.NAME##B = 0;        \
              }                                           \
          }                                               \
      } else {                                            \
          if (expander_PrevStates.NAME##B) {              \
              if (!expander_CurStates.NAME##B) {          \
                  expander_PrevStates.NAME##B = 0;        \
              } else if (!expander_CurStates.NAME##A) {   \
                  expander_PrevStates.NAME##A = 0;        \
              }                                           \
          } else {                                        \
              if (!expander_CurStates.NAME##A) {          \
                  expander_PrevStates.NAME##A = 0;        \
              } else if (expander_CurStates.NAME##B) {    \
                  expander_PrevStates.NAME##B = 1;        \
              }                                           \
          }                                               \
      }

/*
 * Create menu tree from file tree
 */

uint8_t *memp;
KeyValuePair_s LoadMenu;

void EnterMenuLoadFile(void) {
  KeyValuePair_s *F =
      &((KeyValuePair_s *)(LoadMenu.avp.array))[LoadMenu.avp.current];

  char str[20] = "0:";
  strcat(str, F->keyname);

  LoadPatch(str);
}

void EnterMenuLoad(void) {
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
}

void EnterMenuFormat(void) {
  FRESULT err;
  err = f_mkfs(0, 0, 0);
  if (err != FR_OK) {
    SetKVP_AVP(&TmpMenuKvps[0], &KvpsHead, "Format failed", 0, 0);
    KvpsDisplay = &TmpMenuKvps[0];
  }
  else {
    SetKVP_AVP(&TmpMenuKvps[0], &KvpsHead, "Format OK", 0, 0);
    KvpsDisplay = &TmpMenuKvps[0];
  }
}

#define NEWBOARD 1
#ifndef NEWBOARD
/*
 * This is a periodic thread that handles display/buttons
 */
static void UIPollButtons(void);
static void UIUpdateLCD(void);

void AxolotiControlUpdate(void) {
  static int refreshLCD=0;
    UIPollButtons();
    if (!(0x0F & refreshLCD++)) {
      UIUpdateLCD();
      LCD_display();
    }
}


void (*pControlUpdate)(void) = AxolotiControlUpdate;

static WORKING_AREA(waThreadUI, 2048);
static msg_t ThreadUI(void *arg) {
  while(1) {
    if(pControlUpdate != 0L) {
        pControlUpdate();
    }
    AxoboardADCConvert();
    PollMidiIn();
    PExTransmit();
    PExReceive();
    chThdSleepMilliseconds(2);
  }
}
#else
static void UIUpdateLCD(void);
static void UIPollButtons2(void);

void AxolotiControlUpdate(void) {
#if ((BOARD_AXOLOTI_V03)||(BOARD_AXOLOTI_V05))
    do_axoloti_control();
    UIPollButtons2();
    UIUpdateLCD();
#endif
}

void (*pControlUpdate)(void) = AxolotiControlUpdate;

static WORKING_AREA(waThreadUI, 1172);
static msg_t ThreadUI(void *arg) {
  (void)(arg);
#if CH_USE_REGISTRY
  chRegSetThreadName("ui");
#endif
  while (1) {
//    AxoboardADCConvert();
    PExTransmit();
    PExReceive();
    if(pControlUpdate != 0L) {
        pControlUpdate();
    }
    chThdSleepMilliseconds(2);
  }
  return (msg_t)0;
}
#endif

void UIGoSafe(void) {
  KvpsDisplay = &KvpsHead;
}

void ui_init(void) {
  Btn_Nav_Or.word = 0;
  Btn_Nav_And.word = ~0;

  KeyValuePair_s *p1 = chCoreAlloc(sizeof(KeyValuePair_s) * 6);
  KeyValuePair_s *q1 = p1;
  SetKVP_FNCTN(q1++, &KvpsHead, "Info", 0);
  SetKVP_FNCTN(q1++, &KvpsHead, "Format", &EnterMenuFormat);

  KeyValuePair_s *p = chCoreAlloc(sizeof(KeyValuePair_s) * 6);
//  KeyValuePair *q = p;
  int entries = 0;

  SetKVP_APVP(&p[entries++], &KvpsHead, "Patch", 0, &ObjectKvps[0]);
  SetKVP_IVP(&p[entries++], &KvpsHead, "Running", &patchStatus, 0, 15);
  if (fs_ready)
    SetKVP_FNCTN(&p[entries++], &KvpsHead, "Load SD", &EnterMenuLoad);
  else
    SetKVP_FNCTN(&p[entries++], &KvpsHead, "No SDCard", NULL);
  SetKVP_AVP(&p[entries++], &KvpsHead, "SDCard Tools", 2, &p1[0]);
  SetKVP_AVP(&p[entries++], &KvpsHead, "ADCs", 3, &ADCkvps[0]);
  SetKVP_IVP(&p[entries++], &KvpsHead, "dsp%", &dspLoadPct, 0, 100);

  SetKVP_AVP(&KvpsHead, NULL, "--- AXOLOTI ---", entries, &p[0]);

  KvpsDisplay = &KvpsHead;

  int i;
  for (i = 0; i < 3; i++) {
    ADCkvps[i].kvptype = KVP_TYPE_SVP;
    ADCkvps[i].svp.value = (int16_t *)&adcvalues[i];
    char *str = chCoreAlloc(6);
    str[0] = 'A';
    str[1] = 'D';
    str[2] = 'C';
    str[3] = '0' + i;
    str[4] = 0;
//    sprintf(str,"CC%i",i);
    ADCkvps[i].keyname = str;    //(char *)i;
  }

  ObjectKvpRoot = &p[0];

  chThdCreateStatic(waThreadUI, sizeof(waThreadUI), NORMALPRIO, ThreadUI, NULL);
}

void KVP_ClearObjects(void) {
  ObjectKvpRoot->apvp.length = 0;
  KvpsDisplay = &KvpsHead;
}

void KVP_RegisterObject(KeyValuePair_s *kvp) {
  ObjectKvps[ObjectKvpRoot->apvp.length] = kvp;
//	kvp->parent = ObjectKvpRoot;
  ObjectKvpRoot->apvp.length++;
}

#define LCD_COL_INDENT 5
#define LCD_COL_EQ 91
#define LCD_COL_VAL 97
#define LCD_COL_ENTER 97
#define STATUSROW 7

void KVP_DisplayInv(int x, int y, KeyValuePair_s *kvp) {
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

void KVP_Display(int x, int y, KeyValuePair_s *kvp) {
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

static void UIPollButtons(void) {
#if 0
  expander_CurStates.i = ~read_ioexpander();

  IF_EXPANDER_BTN_DOWN(S1)
  MidiInNoteOn(0,30,20+80*Btn_Nav_CurStates.btn_nav_Shift);
  IF_EXPANDER_BTN_UP(S1)
  MidiInNoteOff(0,30,20+80*Btn_Nav_CurStates.btn_nav_Shift);

  IF_EXPANDER_BTN_DOWN(S2)
  MidiInNoteOn(0,42,20+80*Btn_Nav_CurStates.btn_nav_Shift);
  IF_EXPANDER_BTN_UP(S2)
  MidiInNoteOff(0,42,20+80*Btn_Nav_CurStates.btn_nav_Shift);

  IF_EXPANDER_BTN_DOWN(S3)
  MidiInNoteOn(0,54,20+80*Btn_Nav_CurStates.btn_nav_Shift);
  IF_EXPANDER_BTN_UP(S3)
  MidiInNoteOff(0,54,20+80*Btn_Nav_CurStates.btn_nav_Shift);

  IF_EXPANDER_BTN_DOWN(S4)
  MidiInNoteOn(0,66,20+80*Btn_Nav_CurStates.btn_nav_Shift);
  IF_EXPANDER_BTN_UP(S4)
  MidiInNoteOff(0,66,20+80*Btn_Nav_CurStates.btn_nav_Shift);

  IF_EXPANDER_BTN_DOWN(S5)
  ApplyPreset(0);

  IF_EXPANDER_BTN_DOWN(S6)
  ApplyPreset(1);

  IF_EXPANDER_BTN_DOWN(S7)
  ApplyPreset(2);

  IF_EXPANDER_BTN_DOWN(S8)
  ApplyPreset(3);

  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    KeyValuePair_s *cur = &((KeyValuePair_s *)(KvpsDisplay->avp.array))[KvpsDisplay->avp.current];
    if ((cur->kvptype == KVP_TYPE_IVP)||(cur->kvptype == KVP_TYPE_IPVP)) {
      POLLENC(ENC1, KVP_Increment(cur);,KVP_Decrement(cur););
    }
    else {
      //POLLENC(ENC1, controller[0]++;,if(controller[0]>0)controller[0]--;);
    }
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_APVP) {
    KeyValuePair_s *cur = (KeyValuePair_s *)( KvpsDisplay->apvp.array[KvpsDisplay->apvp.current]);
    if ((cur->kvptype == KVP_TYPE_IVP)||(cur->kvptype == KVP_TYPE_IPVP)) {
      POLLENC(ENC1, KVP_Increment(cur);,KVP_Decrement(cur););
    }
    else {
      //POLLENC(ENC1, controller[0]++;,if(controller[0]>0)controller[0]--;);
    }
  } //else
    //POLLENC(ENC1, controller[0]++;,if(controller[0]>0)controller[0]--;);

  expander_PrevStates.S1 = expander_CurStates.S1;
  expander_PrevStates.S2 = expander_CurStates.S2;
  expander_PrevStates.S3 = expander_CurStates.S3;
  expander_PrevStates.S4 = expander_CurStates.S4;
  expander_PrevStates.S5 = expander_CurStates.S5;
  expander_PrevStates.S6 = expander_CurStates.S6;
  expander_PrevStates.S7 = expander_CurStates.S7;
  expander_PrevStates.S8 = expander_CurStates.S8;
//  expander_PrevStates = expander_CurStates;

  Btn_Nav_CurStates.btn_nav_Up = !palReadPad(PORT_BTN_NAV_UP, PAD_BTN_NAV_UP);
  Btn_Nav_CurStates.btn_nav_Down = !palReadPad(PORT_BTN_NAV_DOWN, PAD_BTN_NAV_DOWN);
  Btn_Nav_CurStates.btn_nav_Left = !palReadPad(PORT_BTN_NAV_LEFT, PAD_BTN_NAV_LEFT);
  Btn_Nav_CurStates.btn_nav_Right = !palReadPad(PORT_BTN_NAV_RIGHT,PAD_BTN_NAV_RIGHT);
  Btn_Nav_CurStates.btn_nav_Enter = !palReadPad(PORT_BTN_NAV_ENTER, PAD_BTN_NAV_ENTER);
  Btn_Nav_CurStates.btn_nav_Shift = !palReadPad(PORT_BTN_NAV_SHIFT, PAD_BTN_NAV_SHIFT);
  Btn_Nav_CurStates.btn_nav_Back = !palReadPad(PORT_BTN_NAV_BACK, PAD_BTN_NAV_BACK);

#if 1
  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    KeyValuePair_s *cur = &((KeyValuePair_s *)(KvpsDisplay->avp.array))[KvpsDisplay->avp.current];
    IF_BTN_NAV_DOWN(btn_nav_Up)
    KVP_Increment(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Down)
    KVP_Decrement(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Left)
    KVP_Decrement(cur);
    IF_BTN_NAV_DOWN(btn_nav_Right)
    KVP_Increment(cur);
    IF_BTN_NAV_DOWN(btn_nav_Enter) {
      if ((cur->kvptype == KVP_TYPE_AVP)||
          (cur->kvptype == KVP_TYPE_APVP)||
          (cur->kvptype == KVP_TYPE_CUSTOM))
      KvpsDisplay = cur;
      else if (cur->kvptype == KVP_TYPE_FNCTN)
      if (cur->fnctnvp.fnctn != 0) (cur->fnctnvp.fnctn)();
    }
    IF_BTN_NAV_DOWN(btn_nav_Back)
    if (KvpsDisplay->parent) KvpsDisplay = (KeyValuePair_s *)KvpsDisplay->parent;

  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_APVP) {
    KeyValuePair_s *cur = (KeyValuePair_s *)( KvpsDisplay->apvp.array[KvpsDisplay->apvp.current]);
    IF_BTN_NAV_DOWN(btn_nav_Up)
    KVP_Increment(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Down)
    KVP_Decrement(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Left)
    KVP_Decrement(cur);
    IF_BTN_NAV_DOWN(btn_nav_Right)
    KVP_Increment(cur);
    IF_BTN_NAV_DOWN(btn_nav_Enter) {
      if ((cur->kvptype == KVP_TYPE_AVP)||
          (cur->kvptype == KVP_TYPE_APVP)||
          (cur->kvptype == KVP_TYPE_CUSTOM))
      KvpsDisplay = cur;
      else if (cur->kvptype == KVP_TYPE_FNCTN)
      if (cur->fnctnvp.fnctn != 0) (cur->fnctnvp.fnctn)();
    }
    IF_BTN_NAV_DOWN(btn_nav_Back)
    if (KvpsDisplay->parent) KvpsDisplay = (KeyValuePair_s *)KvpsDisplay->parent;

  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_CUSTOM) {
    IF_BTN_NAV_DOWN(btn_nav_Back)
    if (KvpsDisplay->parent) KvpsDisplay = (KeyValuePair_s *)KvpsDisplay->parent;
  }

  Btn_Nav_PrevStates = Btn_Nav_CurStates;

#endif
#endif
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
 * It is desireable that the current state is true during a whole process interval.
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

static void UIPollButtons2(void) {
  /*
   Btn_Nav_CurStates.btn_nav_Up    = (control_rx_buffer[4]&0x01)>0;
   Btn_Nav_CurStates.btn_nav_Down  = (control_rx_buffer[4]&0x08)>0;
   Btn_Nav_CurStates.btn_nav_Left  = (control_rx_buffer[4]&0x04)>0;
   Btn_Nav_CurStates.btn_nav_Right = (control_rx_buffer[4]&0x02)>0;
   Btn_Nav_CurStates.btn_nav_Enter = control_rx_buffer[25];
   Btn_Nav_CurStates.btn_nav_Shift = control_rx_buffer[26];
   Btn_Nav_CurStates.btn_nav_Back  = control_rx_buffer[24];
   */

  Btn_Nav_CurStates.word = Btn_Nav_CurStates.word | Btn_Nav_Or.word;

  Btn_Nav_Or.word = 0;

  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    KeyValuePair_s *cur =
        &((KeyValuePair_s *)(KvpsDisplay->avp.array))[KvpsDisplay->avp.current];
    IF_BTN_NAV_DOWN(btn_nav_Down)
      KVP_Increment(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Up)
      KVP_Decrement(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Left)
      KVP_Decrement(cur);
    IF_BTN_NAV_DOWN(btn_nav_Right)
      KVP_Increment(cur);
    IF_BTN_NAV_DOWN(btn_nav_Enter) {
      if ((cur->kvptype == KVP_TYPE_AVP) || (cur->kvptype == KVP_TYPE_APVP)
          || (cur->kvptype == KVP_TYPE_CUSTOM))
        KvpsDisplay = cur;
      else if (cur->kvptype == KVP_TYPE_FNCTN)
        if (cur->fnctnvp.fnctn != 0)
          (cur->fnctnvp.fnctn)();
    }
    IF_BTN_NAV_DOWN(btn_nav_Back)
      if (KvpsDisplay->parent)
        KvpsDisplay = (KeyValuePair_s *)KvpsDisplay->parent;

  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_APVP) {
    KeyValuePair_s *cur =
        (KeyValuePair_s *)(KvpsDisplay->apvp.array[KvpsDisplay->apvp.current]);
    IF_BTN_NAV_DOWN(btn_nav_Down)
      KVP_Increment(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Up)
      KVP_Decrement(KvpsDisplay);
    IF_BTN_NAV_DOWN(btn_nav_Left)
      KVP_Decrement(cur);
    IF_BTN_NAV_DOWN(btn_nav_Right)
      KVP_Increment(cur);
    IF_BTN_NAV_DOWN(btn_nav_Enter) {
      if ((cur->kvptype == KVP_TYPE_AVP) || (cur->kvptype == KVP_TYPE_APVP)
          || (cur->kvptype == KVP_TYPE_CUSTOM))
        KvpsDisplay = cur;
      else if (cur->kvptype == KVP_TYPE_FNCTN)
        if (cur->fnctnvp.fnctn != 0)
          (cur->fnctnvp.fnctn)();
    }
    IF_BTN_NAV_DOWN(btn_nav_Back)
      if (KvpsDisplay->parent)
        KvpsDisplay = (KeyValuePair_s *)KvpsDisplay->parent;

  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_CUSTOM) {
    IF_BTN_NAV_DOWN(btn_nav_Back)
      if (KvpsDisplay->parent)
        KvpsDisplay = (KeyValuePair_s *)KvpsDisplay->parent;
  }

// test: toggle LED's
  IF_BTN_NAV_DOWN(btn_1)
    led_buffer[LCDHEADER + 0] = led_buffer[LCDHEADER + 0] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_2)
    led_buffer[LCDHEADER + 1] = led_buffer[LCDHEADER + 1] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_3)
    led_buffer[LCDHEADER + 2] = led_buffer[LCDHEADER + 2] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_4)
    led_buffer[LCDHEADER + 3] = led_buffer[LCDHEADER + 3] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_5)
    led_buffer[LCDHEADER + 4] = led_buffer[LCDHEADER + 4] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_6)
    led_buffer[LCDHEADER + 5] = led_buffer[LCDHEADER + 5] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_7)
    led_buffer[LCDHEADER + 6] = led_buffer[LCDHEADER + 6] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_8)
    led_buffer[LCDHEADER + 7] = led_buffer[LCDHEADER + 7] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_9)
    led_buffer[LCDHEADER + 8] = led_buffer[LCDHEADER + 8] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_10)
    led_buffer[LCDHEADER + 9] = led_buffer[LCDHEADER + 9] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_11)
    led_buffer[LCDHEADER + 10] = led_buffer[LCDHEADER + 10] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_12)
    led_buffer[LCDHEADER + 11] = led_buffer[LCDHEADER + 11] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_13)
    led_buffer[LCDHEADER + 12] = led_buffer[LCDHEADER + 12] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_14)
    led_buffer[LCDHEADER + 13] = led_buffer[LCDHEADER + 13] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_15)
    led_buffer[LCDHEADER + 14] = led_buffer[LCDHEADER + 14] ? 0 : 255;
  IF_BTN_NAV_DOWN(btn_16)
    led_buffer[LCDHEADER + 15] = led_buffer[LCDHEADER + 15] ? 0 : 255;

// process encoder // todo: more than just one encoder...
  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    KeyValuePair_s *cur =
        &((KeyValuePair_s *)(KvpsDisplay->avp.array))[KvpsDisplay->avp.current];
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
    KeyValuePair_s *cur =
        (KeyValuePair_s *)(KvpsDisplay->apvp.array[KvpsDisplay->apvp.current]);
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

  Btn_Nav_CurStates.word = Btn_Nav_CurStates.word & Btn_Nav_And.word;
  Btn_Nav_PrevStates = Btn_Nav_CurStates;
  Btn_Nav_And.word = ~0;
}

static void UIUpdateLCD(void) {
  KVP_DisplayInv(0, 0, KvpsDisplay);
  if (KvpsDisplay->kvptype == KVP_TYPE_AVP) {
    int c = KvpsDisplay->avp.current;
    int l = KvpsDisplay->avp.length;
    KeyValuePair_s *k = (KeyValuePair_s *)KvpsDisplay->avp.array;
    if (l < STATUSROW) {
      int i;
      for (i = 0; i < l; i++) {
        if (c == i)
          KVP_DisplayInv(LCD_COL_INDENT, 1 + i, &k[i]);
        else
          KVP_Display(LCD_COL_INDENT, 1 + i, &k[i]);
      }
      for (; i < STATUSROW; i++) {
        LCD_drawStringN(LCD_COL_INDENT, 1 + i, " ", 78);
      }
    }
    else if (c == 0) {
      if (c < l)
        KVP_DisplayInv(LCD_COL_INDENT, 1, &k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 1, " ", LCDWIDTH);
      int line;
      for (line = 2; line < STATUSROW; line++) {
        if (c < l)
          KVP_Display(LCD_COL_INDENT, line, &k[c++]);
        else
          LCD_drawStringN(LCD_COL_INDENT, line, " ", LCDWIDTH);
      }
    }
    else if (c == 1) {
      c--;
      if (c < l)
        KVP_Display(LCD_COL_INDENT, 1, &k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 1, " ", LCDWIDTH);
      if (c < l)
        KVP_DisplayInv(LCD_COL_INDENT, 2, &k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 2, " ", LCDWIDTH);
      int line;
      for (line = 3; line < STATUSROW; line++) {
        if (c < l)
          KVP_Display(LCD_COL_INDENT, line, &k[c++]);
        else
          LCD_drawStringN(LCD_COL_INDENT, line, " ", LCDWIDTH);
      }
    }
    else {
      c--;
      c--;
      if (c < l)
        KVP_Display(LCD_COL_INDENT, 1, &k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 1, " ", LCDWIDTH);
      if (c < l)
        KVP_Display(LCD_COL_INDENT, 2, &k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 2, " ", LCDWIDTH);
      if (c < l)
        KVP_DisplayInv(LCD_COL_INDENT, 3, &k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 3, " ", LCDWIDTH);
      int line;
      for (line = 3; line < STATUSROW; line++) {
        if (c < l)
          KVP_Display(LCD_COL_INDENT, line, &k[c++]);
        else
          LCD_drawStringN(LCD_COL_INDENT, line, " ", LCDWIDTH);
      }
    }
    if (KvpsDisplay->parent) {
      LCD_drawStringInv(0, STATUSROW, "BACK");
      LCD_drawString(24, STATUSROW, "     ");
    }
    else
      LCD_drawString(0, STATUSROW, "          ");
    if ((k[KvpsDisplay->avp.current].kvptype == KVP_TYPE_AVP)
        || (k[KvpsDisplay->avp.current].kvptype == KVP_TYPE_APVP)
        || (k[KvpsDisplay->avp.current].kvptype == KVP_TYPE_CUSTOM))
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else if (k[KvpsDisplay->avp.current].kvptype == KVP_TYPE_FNCTN)
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else
      LCD_drawString(LCD_COL_ENTER, STATUSROW, "     ");
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_APVP) {
    int c = KvpsDisplay->apvp.current;
    int l = KvpsDisplay->apvp.length;
    KeyValuePair_s **k = (KeyValuePair_s **)KvpsDisplay->apvp.array;
    if (l < 7) {
      int i;
      for (i = 0; i < l; i++) {
        if (c == i)
          KVP_DisplayInv(LCD_COL_INDENT, 1 + i, k[i]);
        else
          KVP_Display(LCD_COL_INDENT, 1 + i, k[i]);
      }
      for (; i < STATUSROW; i++) {
        LCD_drawStringN(LCD_COL_INDENT, 1 + i, " ", 78);
      }
    }
    else if (c == 0) {
      if (c < l)
        KVP_DisplayInv(LCD_COL_INDENT, 1, k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 1, " ", LCDWIDTH);
      int line;
      for (line = 2; line < STATUSROW; line++) {
        if (c < l)
          KVP_Display(LCD_COL_INDENT, line, k[c++]);
        else
          LCD_drawStringN(LCD_COL_INDENT, line, " ", LCDWIDTH);
      }
    }
    else if (c == 1) {
      c--;
      if (c < l)
        KVP_Display(LCD_COL_INDENT, 1, k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 1, " ", LCDWIDTH);
      if (c < l)
        KVP_DisplayInv(LCD_COL_INDENT, 2, k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 2, " ", LCDWIDTH);
      int line;
      for (line = 3; line < STATUSROW; line++) {
        if (c < l)
          KVP_Display(LCD_COL_INDENT, line, k[c++]);
        else
          LCD_drawStringN(LCD_COL_INDENT, line, " ", LCDWIDTH);
      }
    }
    else {
      c--;
      c--;
      if (c < l)
        KVP_Display(LCD_COL_INDENT, 1, k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 1, " ", LCDWIDTH);
      if (c < l)
        KVP_Display(LCD_COL_INDENT, 2, k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 2, " ", LCDWIDTH);
      if (c < l)
        KVP_DisplayInv(LCD_COL_INDENT, 3, k[c++]);
      else
        LCD_drawStringN(LCD_COL_INDENT, 3, " ", LCDWIDTH);
      int line;
      for (line = 4; line < STATUSROW; line++) {
        if (c < l)
          KVP_Display(LCD_COL_INDENT, line, k[c++]);
        else
          LCD_drawStringN(LCD_COL_INDENT, line, " ", LCDWIDTH);
      }
    }
    if (KvpsDisplay->parent) {
      LCD_drawStringInv(0, STATUSROW, "BACK");
      LCD_drawString(24, STATUSROW, "     ");
    }
    else
      LCD_drawString(0, STATUSROW, "          ");
    if ((k[KvpsDisplay->apvp.current]->kvptype == KVP_TYPE_AVP)
        || (k[KvpsDisplay->apvp.current]->kvptype == KVP_TYPE_APVP)
        || (k[KvpsDisplay->apvp.current]->kvptype == KVP_TYPE_CUSTOM))
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else if (k[KvpsDisplay->avp.current]->kvptype == KVP_TYPE_FNCTN)
      LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
    else
      LCD_drawString(LCD_COL_ENTER, STATUSROW, "     ");
  }
  else if (KvpsDisplay->kvptype == KVP_TYPE_CUSTOM) {
    (*KvpsDisplay->custom.displayFunction)(KvpsDisplay->custom.userdata);
  }
}
