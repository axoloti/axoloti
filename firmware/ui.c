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
#include "axoloti_math.h"
#include "patch.h"
#include "axoloti_control.h"
#include "glcdfont.h"
#include <string.h>
#include "spilink.h"
#include "menu_content/main_menu.h"

#pragma GCC optimize ("Og")

static thread_t * thd_ui2 = 0;

extern const ui_node_t RootMenu;

// ------ menu stack ------

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

// ------ Parameter node ------

ui_node_t ParamMenu = {
  &nodeFunctionTable_param, "Param", .param = {0}
};

// ------ Object node ------

// todo: use a stack of ObjMenu's
char objname[MAX_PARAMETER_NAME_LENGTH + 1];

ui_node_t ObjMenu = {
  &nodeFunctionTable_object, objname, .obj = {0}
};

#if 0 // obsolete, use static initializers instead
void SetKVP_APVP(ui_node_t *kvp, ui_node_t *parent,
                 const char *keyName, int length, ui_node_t **array) {
  kvp->node_type = KVP_TYPE_APVP;
  kvp->name = keyName;
  kvp->apvp.length = length;
  kvp->apvp.array = (void *)array;
}

void SetKVP_AVP(ui_node_t *kvp, const ui_node_t *parent,
                const char *keyName, int length, const ui_node_t *array) {
  kvp->node_type = node_type_node_list;
  kvp->name = keyName;
  kvp->nodeList.length = length;
  kvp->nodeList.array = array;
}

void SetKVP_IVP(ui_node_t *kvp, ui_node_t *parent,
                const char *keyName, int *value, int min, int max) {
  kvp->node_type = node_type_integer_value;
  kvp->name = keyName;
  kvp->intValue.pvalue = value;
  kvp->intValue.minvalue = min;
  kvp->intValue.maxvalue = max;
}

void SetKVP_IPVP(ui_node_t *kvp, ui_node_t *parent,
                 const char *keyName, ParameterExchange_t *PEx, int min,
                 int max) {
  PEx->signals = 0x0F;
  kvp->node_type = KVP_TYPE_IPVP;
  kvp->name = keyName;
  kvp->ipvp.PEx = PEx;
  kvp->ipvp.minvalue = min;
  kvp->ipvp.maxvalue = max;
}

void SetKVP_FNCTN(ui_node_t *kvp, ui_node_t *parent,
                  const char *keyName, VoidFunction fnctn) {
  kvp->node_type = node_type_function;
  kvp->name = keyName;
  kvp->fnctn.fnctn = fnctn;
}

void SetKVP_CUSTOM(ui_node_t *node, ui_node_t *parent,
                  const char *keyName, DisplayFunction dispfnctn, ButtonFunction btnfnctn, void* userdata) {
  node->node_type = node_type_custom;
  node->name = keyName;
  node->custom.displayFunction = dispfnctn;
  node->custom.buttonFunction = btnfnctn;
  node->custom.userdata = userdata;
}
#endif

uint32_t list_nav_down(const ui_node_t *node, int maxposition) {
	if (menu_stack[menu_stack_position].currentpos
			< (maxposition - 1))
		menu_stack[menu_stack_position].currentpos++;
	return lcd_dirty_flag_listnav;
}

uint32_t list_nav_up(const ui_node_t *node) {
	if (menu_stack[menu_stack_position].currentpos > 0)
		menu_stack[menu_stack_position].currentpos--;
	return lcd_dirty_flag_listnav;
}

void DisplayHeading(void) {
	int h = 21;
	int i = menu_stack_position;
	while(i>0) {
		const char *name = menu_stack[i].parent->name;
		int l = strlen(name);
		if (l>h) {
			l=h;
			LCD_drawStringInvN(0, 0, "......................", l);
			break;
		}
		h -= l;
		LCD_drawStringInvN(h*3, 0, name, l);
		h--;
		if ((h>=0)&&(i>0)) {
			LCD_drawCharInv(h*3, 0, '>');
		} else break;
		i--;
	}
}

static uint32_t nav_Back(void) {
	if (menu_stack_position > 0)
		menu_stack_position--;
	return ~0;
}

uint32_t ui_enter_node(const ui_node_t *nodex) {
	if (menu_stack_position < menu_stack_size - 1) {
		LCD_clear();
		menu_stack_t *m = &menu_stack[menu_stack_position + 1];
		m->parent = nodex;
		m->currentpos = 0;
		menu_stack_position++;
		return ~0;
	} else
		return 0;
}

void drawParamValue(int line, int x, Parameter_t *param) {
   // TODO: other parameter types
   switch (param->type) {
   case param_type_bin_1bit_momentary:
   case param_type_bin_1bit_toggle:
	   if (param->d.intt.value) {
		   LCD_drawStringN(x, line, "      on", 8);
	   } else {
		   LCD_drawStringN(x, line, "     off", 8);
	   }
	   break;
   case param_type_bin_16bits:
	   LCD_drawBitField2(x, line, param->d.intt.value, 16);
   	   break;
   case param_type_bin_32bits:
	   LCD_drawBitField(x, line, param->d.intt.value, 32);
	   break;
   case param_type_int:
	   LCD_drawNumber7D(x,line, param->d.intt.value);
	   break;
   case param_type_frac_sq27:
   case param_type_frac_uq27:
   	   LCD_drawNumberQ27x64(x, line, param->d.frac.value);
   	   break;
   default:
   	   LCD_drawNumberHex32(x, line, param->d.frac.value);
   	   break;
   }
}

void drawParamValueInv(int line, int x, Parameter_t *param) {
   // TODO: other parameter types
   switch (param->type) {
   case param_type_bin_1bit_momentary:
   case param_type_bin_1bit_toggle:
	   if (param->d.intt.value) {
		   LCD_drawStringInvN(x, line, "      on", 8);
	   } else {
		   LCD_drawStringInvN(x, line, "     off", 8);
	   }
	   break;
   case param_type_bin_16bits:
	   LCD_drawBitField2Inv(x, line, param->d.intt.value, 16);
   	   break;
   case param_type_bin_32bits:
	   LCD_drawBitFieldInv(x, line, param->d.intt.value, 32);
	   break;
   case param_type_int:
	   LCD_drawNumber7DInv(x,line, param->d.intt.value);
	   break;
   case param_type_frac_sq27:
   case param_type_frac_uq27:
   	   LCD_drawNumberQ27x64Inv(x, line, param->d.frac.value);
   	   break;
   default:
   	   LCD_drawNumberHex32Inv(x, line, param->d.frac.value);
   	   break;
   }
}

void drawDispValue(int line, int x, Display_meta_t *disp) {
   // TODO: other display types
   switch (disp->display_type) {
   case display_meta_type_int32:
	   LCD_drawNumber7D(x,line,*disp->displaydata);
	   break;
   case display_meta_type_ibar16:
	   LCD_drawHBar(x, line, *disp->displaydata, 16);
   	   break;
   case display_meta_type_ibar32:
	   LCD_drawHBar(x, line, *disp->displaydata, 32);
   	   break;
   case display_meta_type_chart_sq27:
   case display_meta_type_dial_sq27:
	   // TODO: create signed bar
	   LCD_drawHBar(x, line, __SSAT(*disp->displaydata >>21,5)+16, 32);
   	   break;
   case display_meta_type_chart_uq27:
   case display_meta_type_dial_uq27:
	   LCD_drawHBar(x, line, __USAT(*disp->displaydata >>21,5), 32);
	   break;
   default:
	   LCD_drawNumberHex32(x, line, *disp->displaydata);
   }
}

void drawDispValueInv(int line, int x, Display_meta_t *disp) {
   // TODO: other display types
   switch (disp->display_type) {
   case display_meta_type_int32:
	   LCD_drawNumber7DInv(x,line,*disp->displaydata);
	   break;
   case display_meta_type_ibar16:
	   LCD_drawHBarInv(x, line, 1 + *disp->displaydata, 17);
   	   break;
   case display_meta_type_ibar32:
	   LCD_drawHBarInv(x, line, *disp->displaydata, 32);
   	   break;
   case display_meta_type_chart_sq27:
   case display_meta_type_dial_sq27:
	   LCD_drawHBarInv(x, line, __SSAT(*disp->displaydata >>21,5)+16, 32);
   	   break;
   case display_meta_type_chart_uq27:
   case display_meta_type_dial_uq27:
	   LCD_drawHBarInv(x, line, __USAT(*disp->displaydata >>21,5), 32);
	   break;
   default:
	   LCD_drawNumberHex32Inv(x, line, *disp->displaydata);
   }
}


void ProcessEncoderParameter(Parameter_t *p, int8_t v) {
// todo: add other parameter types
// todo: clamp to parameter minimum, maximum
// todo: use ticks
	switch (p->type) {
	case param_type_bin_1bit_momentary:
	case param_type_bin_1bit_toggle:
		if (v>0) {
			ParameterChange(p,1,0xFFFFFFFF);
		} else {
			ParameterChange(p,0,0xFFFFFFFF);
		}
		break;
	case param_type_int:
		ParameterChange(p,p->d.intt.value + v,0xFFFFFFFF);
		break;
	case param_type_frac_sq27:
		ParameterChange(p,__SSAT(p->d.frac.value + (v<<20),28),0xFFFFFFFF);
		break;
	case param_type_frac_uq27:
		ParameterChange(p,__USAT(p->d.frac.value + (v<<20),27),0xFFFFFFFF);
		break;
	default:
		break;
	}
}

void ProcessStepButtonsParameter(Parameter_t *p) {
#if 0 // todo switch to event handler
	switch (p->type) {
	case param_type_int: {
		int x = -1;
		if (BTN_NAV_DOWN(sw1)) x = 0;
		if (BTN_NAV_DOWN(sw2)) x = 1;
		if (BTN_NAV_DOWN(sw3)) x = 2;
		if (BTN_NAV_DOWN(sw4)) x = 3;
		if (BTN_NAV_DOWN(sw5)) x = 4;
		if (BTN_NAV_DOWN(sw6)) x = 5;
		if (BTN_NAV_DOWN(sw7)) x = 6;
		if (BTN_NAV_DOWN(sw8)) x = 7;
		if (BTN_NAV_DOWN(sw9)) x = 8;
		if (BTN_NAV_DOWN(sw10)) x = 9;
		if (BTN_NAV_DOWN(sw11)) x = 10;
		if (BTN_NAV_DOWN(sw12)) x = 11;
		if (BTN_NAV_DOWN(sw13)) x = 12;
		if (BTN_NAV_DOWN(sw14)) x = 13;
		if (BTN_NAV_DOWN(sw15)) x = 14;
		if (BTN_NAV_DOWN(sw16)) x = 15;
		if (x>=0) {
			if ((x < p->d.intt.maximum) && (x >= p->d.intt.minimum)) {
				ParameterChange(p, x, 0xFFFFFFFF);
			}
		}

	}
		break;
	case param_type_bin_16bits: {
		int x = 0;
		if (BTN_NAV_DOWN(sw1)) x |= 1<<0;
		if (BTN_NAV_DOWN(sw2)) x |= 1<<1;
		if (BTN_NAV_DOWN(sw3)) x |= 1<<2;
		if (BTN_NAV_DOWN(sw4)) x |= 1<<3;
		if (BTN_NAV_DOWN(sw5)) x |= 1<<4;
		if (BTN_NAV_DOWN(sw6)) x |= 1<<5;
		if (BTN_NAV_DOWN(sw7)) x |= 1<<6;
		if (BTN_NAV_DOWN(sw8)) x |= 1<<7;
		if (BTN_NAV_DOWN(sw9)) x |= 1<<8;
		if (BTN_NAV_DOWN(sw10)) x |= 1<<9;
		if (BTN_NAV_DOWN(sw11)) x |= 1<<10;
		if (BTN_NAV_DOWN(sw12)) x |= 1<<11;
		if (BTN_NAV_DOWN(sw13)) x |= 1<<12;
		if (BTN_NAV_DOWN(sw14)) x |= 1<<13;
		if (BTN_NAV_DOWN(sw15)) x |= 1<<14;
		if (BTN_NAV_DOWN(sw16)) x |= 1<<15;
		if (x) {
			// todo: add binary parameter API toggle function
			int v = p->d.bin.value ^ x;
			p->d.bin.value = v;
			p->d.bin.finalvalue = v;
			p->signals = ~0;
		}
	}
		break;
	}
#endif
}

void ShowListPositionOnEncoderLEDRing(led_array_t *led_array, int pos, int length) {
	if (length < 2) {
		LED_clear(led_array);
		return;
	}
	if (!pos) {
		LED_setOne(led_array, 0);
		return;
	}
	if (pos == length-1) {
		LED_setOne(led_array, 15);
		return;
	}
	LED_setOne(led_array, 1 + (pos*14)/(length - 1));
}

void ShowParameterOnEncoderLEDRing(led_array_t *led_array, Parameter_t *p) {
// todo: add other parameter types
	switch (p->type) {
	case param_type_bin_1bit_momentary:
	case param_type_bin_1bit_toggle:
		if (p->d.intt.value) {
			LED_setOne(led_array, 15);
		} else {
			LED_setOne(led_array, 0);
		}
		break;
	case param_type_frac_uq27:
		LED_setOne(led_array, __USAT(p->d.frac.value >> 23, 4));
		break;
	case param_type_frac_sq27:
		LED_setOne(led_array, __SSAT(p->d.frac.value >> 24, 4)+8);
		break;
	case param_type_int:
		// hmm maybe we need to differentiate between signed and unsigned?
		if (p->d.intt.value >= 0 && p->d.intt.value < 16) {
			LED_setOne(led_array, p->d.intt.value);
		} else {
			LED_clear(led_array);
		}
		break;
	default:
		LED_clear(led_array);
	}
}

void ShowParameterOnButtonArrayLEDs(led_array_t *led_array, Parameter_t *p) {
	switch (p->type) {
	case param_type_int: {
		LED_clear(LED_STEPS);
		int v = p->d.intt.value;
		if (v >= 0 && v < 16)
			LED_addOne(led_array, v, 1);
	}
		break;
	case param_type_bin_16bits: {
		LED_clear(LED_STEPS);
		int v = p->d.bin.value;
		int i = 16;
		int j = 0;
		while (i--) {
			if (v & 1)
				LED_addOne(led_array, j, 1);
			v >>= 1;
			j++;
		}
	}
		break;
	default:
		LED_clear(led_array);
	}
}

void update_list_nav(int current_menu_length) {
	const int current_menu_position = menu_stack[menu_stack_position].currentpos;
	// update list navigation indicators
	ShowListPositionOnEncoderLEDRing(LED_RING_TOPLEFT, current_menu_position,
			current_menu_length);
	LCD_drawChar(0, STATUSROW, current_menu_position > 0 ? CHAR_ARROW_UP : 0);
	LCD_drawChar(12, STATUSROW,
			current_menu_position < (current_menu_length - 1) ?
					CHAR_ARROW_DOWN : 0);
	LED_clear(LED_STEPS);
	return;
}

bool evtIsEnter(input_event evt) {
	return ((evt.fields.button == btn_E) && (evt.fields.quadrant == quadrant_main) && (evt.fields.value));
}

bool evtIsUp(input_event evt) {
	if (evt.fields.button == btn_up)
		return ((evt.fields.quadrant == quadrant_main) && (evt.fields.value));
	else if ((evt.fields.button == btn_encoder) && (evt.fields.quadrant == quadrant_topleft))
		return (evt.fields.value<0);
	return 0;
}

bool evtIsDown(input_event evt) {
	if (evt.fields.button == btn_down)
		return ((evt.fields.quadrant == quadrant_main) && (evt.fields.value));
	else if ((evt.fields.button == btn_encoder) && (evt.fields.quadrant == quadrant_topleft))
		return (evt.fields.value>0);
	return 0;
}

static input_event btn_event = {.word = 0};
static input_event enc_event[4] = {
		{.fields={.quadrant=quadrant_topleft, .modifiers = 0, .value = 0, .button = btn_encoder}},
		{.fields={.quadrant=quadrant_topright, .modifiers = 0, .value = 0, .button = btn_encoder}},
		{.fields={.quadrant=quadrant_bottomleft, .modifiers = 0, .value = 0, .button = btn_encoder}},
		{.fields={.quadrant=quadrant_bottomright, .modifiers = 0, .value = 0, .button = btn_encoder}}
};

void queueInputEventI(input_event evt) {
	if ((evt.fields.button == btn_encoder) &&
			(evt.fields.quadrant > 0) &&
			(evt.fields.quadrant < 4)
			) {
		input_event *e = &enc_event[evt.fields.quadrant-1];
		e->fields.value += evt.fields.value;
		e->fields.modifiers += evt.fields.modifiers;
	} else if (evt.fields.value == 0) {
		// button release event takes priority
		btn_event.word = evt.word;
	} else if (evt.word) {
		btn_event.word = evt.word;
	} else {
		// drop event
	}
}

input_event getEvent(void) {
	if (btn_event.word) {
		input_event e;
		e.word = btn_event.word;
		btn_event.word = 0;
		return e;
	}
	int i;
	for (i=0;i<4;i++){
		if (enc_event[i].fields.value) {
			input_event e;
			e.word = enc_event[i].word;
			enc_event[i].fields.value = 0;
			return e;
		}
	}
	input_event e = {.word=0};
	return e;
}

static void processUIEvent(input_event evt) {
	const ui_node_t * head_node = menu_stack[menu_stack_position].parent;
	if (head_node->functions && head_node->functions->handle_evt) {
		eventmask_t resp_evt;
		resp_evt = head_node->functions->handle_evt(head_node, evt);
		if (resp_evt) {
			chEvtSignal(thd_ui2, resp_evt);
		}
	}
	// ALWAYS handle back
	if ((evt.fields.button == btn_X) && (evt.fields.quadrant == quadrant_main) && (evt.fields.value))
		chEvtSignal(thd_ui2, nav_Back());
}

void pollProcessUIEvent(void) {
	input_event e = getEvent();
	while (e.word) {
		processUIEvent(e);
		e = getEvent();
	}
}

static THD_WORKING_AREA(waThreadUI2, 1380);
static THD_FUNCTION(ThreadUI2, arg) {
	(void) (arg);
	chRegSetThreadName("ui2");
	while (1) {
		eventmask_t evt = chEvtWaitOneTimeout(0xFFFFFFFF, TIME_MS2I(16)); // ~ 60Hz refresh rate
		const ui_node_t * head_node = menu_stack[menu_stack_position].parent;
		if (evt == lcd_dirty_flag_clearscreen) {
			LCD_clear();
			LED_clear(LED_RING_TOPLEFT);
			LED_clear(LED_RING_TOPRIGHT);
			LED_clear(LED_RING_BOTTOMLEFT);
			LED_clear(LED_RING_BOTTOMRIGHT);
			LED_clear(LED_STEPS);
			LED_clear(LED_LVL);
		} else if (evt == lcd_dirty_flag_header) {
			DisplayHeading();
		} else if (head_node->functions && head_node->functions->paint_screen_update) {
			head_node->functions->paint_screen_update(head_node, evt);
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
		// sleep a bit to prevent LCD repaint flood
		// hmm we don't get a steady idle refresh rate this way...
		chThdSleepMilliseconds(8);
	}
}

void ui_init(void) {
	thd_ui2 = chThdCreateStatic(waThreadUI2, sizeof(waThreadUI2), NORMALPRIO, ThreadUI2,
			NULL);
	chEvtSignal(thd_ui2, ~0);
}

void ui_go_home(void) {
	menu_stack_position = 0;
	if (thd_ui2)
		chEvtSignal(thd_ui2, ~0);
}

int getValuFromInputEvent(input_event evt) {
        switch (evt.fields.button) {
        case btn_encoder:
                return evt.fields.value;
        case btn_up:
                return 1;
        case btn_down:
                return -1;
        default:
                return 0;
        }
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
