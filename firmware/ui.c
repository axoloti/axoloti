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
#include "ui_menu_content.h"


// normal content has a indent to allow indicators on first column:
#define LCD_COL_INDENT 3

// the last column to write a character on:
#define LCD_COL_RIGHT 61

#define LCD_COL_ENTER 48

#define STATUSROW 7

#define LCD_VALUE_POSITION 36

Btn_Nav_States_struct Btn_Nav_CurStates;
Btn_Nav_States_struct Btn_Nav_PrevStates;
Btn_Nav_States_struct Btn_Nav_Or;
Btn_Nav_States_struct Btn_Nav_And;

int8_t EncBuffer[2];

extern const ui_node_t RootMenu;

// ------ menu stack ------

typedef struct {
	const ui_node_t *parent;
	int currentpos;
	int maxposition;
} menu_stack_t;

#define menu_stack_size 10

menu_stack_t menu_stack[menu_stack_size] = {
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length},
	{&RootMenu, 0, MainMenu_length}
};

int menu_stack_position = 0;

// ------ Parameter node ------

ui_node_t ParamMenu = {
  node_type_param, "Param", .param = {0}
};

// ------ Object menu stuff ------

// todo: use a stack of ObjMenu's
char objname[MAX_PARAMETER_NAME_LENGTH + 1];

ui_node_t ObjMenu = {
  node_type_object, objname, .obj = {0}
};

// ------ LCD dirty flags ------
// for selective repainting
// in high to low priority order:

const int lcd_dirty_flag_clearscreen = 1<<0;
const int lcd_dirty_flag_header  = 1<<1;
const int lcd_dirty_flag_initial = 1<<2;
const int lcd_dirty_flag_listnav = 1<<3;
uint32_t lcd_dirty_flags;


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

inline void UINode_Increment(const ui_node_t *node) {
	switch (node->node_type) {
	case node_type_integer_value:
		if (*node->intValue.pvalue < node->intValue.maxvalue)
			(*node->intValue.pvalue)++;
		break;
	case node_type_object_list:
	case node_type_param_list:
	case node_type_object:
	case node_type_node_list: {
		if (menu_stack[menu_stack_position].currentpos
				< (menu_stack[menu_stack_position].maxposition - 1))
			menu_stack[menu_stack_position].currentpos++;
		lcd_dirty_flags |= lcd_dirty_flag_listnav;
	}
		break;
	default:
		break;
	}
}

inline void UINode_Decrement(const ui_node_t *node) {
	switch (node->node_type) {
	case node_type_integer_value:
		if (*node->intValue.pvalue > node->intValue.minvalue)
			(*node->intValue.pvalue)--;
		break;
	case node_type_node_list:
	case node_type_object_list:
	case node_type_param_list:
	case node_type_object: {
		if (menu_stack[menu_stack_position].currentpos > 0)
			menu_stack[menu_stack_position].currentpos--;
		lcd_dirty_flags |= lcd_dirty_flag_listnav;
	}
		break;
	default:
		break;
	}
}

#define LCD_COL_EQ 45
#define LCD_COL_EQ_LENGTH 6
#define LCD_COL_VAL 45

void DisplayHeading(void) {
	int h = 21;
	int i = menu_stack_position;
	while(i>0) {
		const char *name = menu_stack[i].parent->name;
		int l = strlen(name);
		if (l>h) {
			l=h;
			LCD_drawStringInvN(0, 0, "----------------------", l);
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

void UINode_DrawInv(int x, int y, const ui_node_t *node) {
	LCD_drawStringInvN(x, y, node->name, 14); // todo: fix 14
	switch (node->node_type) {
	case node_type_integer_value:
		LCD_drawCharInv(LCD_COL_EQ, y, '=');
		LCD_drawNumber5DInv(LCD_COL_VAL, y, *node->intValue.pvalue);
		break;
	case node_type_node_list:
		LCD_drawStringInvN(LCD_COL_EQ, y, "     *", LCD_COL_EQ_LENGTH);
		break;
	case node_type_short_value:
		LCD_drawCharInv(LCD_COL_EQ, y, '=');
		LCD_drawNumber5DInv(LCD_COL_VAL, y, *node->shortValue.pvalue);
		break;
	case node_type_custom:
		LCD_drawStringInvN(LCD_COL_EQ, y, "     @", LCD_COL_EQ_LENGTH);
		break;
	case node_type_param_list:
		LCD_drawStringInvN(LCD_COL_EQ, y, "     $", LCD_COL_EQ_LENGTH);
		break;
	case node_type_object_list:
		LCD_drawStringInvN(LCD_COL_EQ, y, "     0", LCD_COL_EQ_LENGTH);
		break;
	default:
		break;
	}
}

void UINode_Draw(int x, int y, const ui_node_t *node) {
	LCD_drawStringN(x, y, node->name, 14); // todo: fix 14?
	switch (node->node_type) {
	case node_type_integer_value:
		LCD_drawChar(LCD_COL_EQ, y, '=');
		LCD_drawNumber5D(LCD_COL_VAL, y, *node->intValue.pvalue);
		break;
	case node_type_node_list:
		LCD_drawStringN(LCD_COL_EQ, y, "     *", LCD_COL_EQ_LENGTH);
		break;
	case node_type_short_value:
		LCD_drawChar(LCD_COL_EQ, y, '=');
		LCD_drawNumber5D(LCD_COL_VAL, y, *node->shortValue.pvalue);
		break;
	case node_type_custom:
		LCD_drawStringN(LCD_COL_EQ, y, "     @", LCD_COL_EQ_LENGTH);
		break;
	case node_type_param_list:
		LCD_drawStringN(LCD_COL_EQ, y, "     $", LCD_COL_EQ_LENGTH);
		break;
	case node_type_object_list:
		LCD_drawStringN(LCD_COL_EQ, y, "     0", LCD_COL_EQ_LENGTH);
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

static void nav_Back(void) {
	if (menu_stack_position > 0)
		menu_stack_position--;
	lcd_dirty_flags = ~0;
}

void ui_enter_node(const ui_node_t *node) {
	switch (node->node_type) {
	case node_type_action_function:
		if (node->fnctn.fnctn != 0)
			(node->fnctn.fnctn)();
		break;
	default: {
		if (menu_stack_position < menu_stack_size - 1) {
			LCD_clear();
			menu_stack_t *m = &menu_stack[menu_stack_position + 1];
			m->parent = node;
			m->currentpos = 0;
			switch (node->node_type) {
			case node_type_node_list:
				m->maxposition = node->nodeList.length;
				break;
			case node_type_object_list: {
				// refuse to descent when empty
				int n = node->objList.nobjs;
				if (!n) return;
				m->maxposition = n;
			}
				break;
			case node_type_param_list: {
				// refuse to descent when empty
				int n = node->paramList.nparams;
				if (!n) return;
				m->maxposition = n;
			}
				break;
			case node_type_object:
				m->maxposition = node->obj.obj->nparams
						+ node->obj.obj->ndisplays;
				break;
			default:
				m->maxposition = 0;
			}
			menu_stack_position++;
			lcd_dirty_flags = ~0;
		}
	}
		break;
	}
}

void ProcessEncoderParameter(Parameter_t *p, int8_t *v) {
// todo: add other parameter types
// todo: clamp to parameter minimum, maximum
// todo: use ticks
	if (!*v) return;
	switch (p->type) {
	case param_type_bin_1bit_momentary:
	case param_type_bin_1bit_toggle:
		if (*v>0) {
			ParameterChange(p,1,0xFFFFFFFF);
		} else {
			ParameterChange(p,0,0xFFFFFFFF);
		}
		break;
	case param_type_int:
		ParameterChange(p,p->d.intt.value + *v,0xFFFFFFFF);
		break;
	case param_type_frac_sq27:
		ParameterChange(p,__SSAT(p->d.frac.value + (*v<<20),28),0xFFFFFFFF);
		break;
	case param_type_frac_uq27:
		ParameterChange(p,__USAT(p->d.frac.value + (*v<<20),27),0xFFFFFFFF);
		break;
	default:
		break;
	}
	*v = 0;
}

void ProcessStepButtonsParameter(Parameter_t *p) {
	switch (p->type) {
	case param_type_int: {
		int x = -1;
		if (BTN_NAV_DOWN(btn_1)) x = 0;
		if (BTN_NAV_DOWN(btn_2)) x = 1;
		if (BTN_NAV_DOWN(btn_3)) x = 2;
		if (BTN_NAV_DOWN(btn_4)) x = 3;
		if (BTN_NAV_DOWN(btn_5)) x = 4;
		if (BTN_NAV_DOWN(btn_6)) x = 5;
		if (BTN_NAV_DOWN(btn_7)) x = 6;
		if (BTN_NAV_DOWN(btn_8)) x = 7;
		if (BTN_NAV_DOWN(btn_9)) x = 8;
		if (BTN_NAV_DOWN(btn_10)) x = 9;
		if (BTN_NAV_DOWN(btn_11)) x = 10;
		if (BTN_NAV_DOWN(btn_12)) x = 11;
		if (BTN_NAV_DOWN(btn_13)) x = 12;
		if (BTN_NAV_DOWN(btn_14)) x = 13;
		if (BTN_NAV_DOWN(btn_15)) x = 14;
		if (BTN_NAV_DOWN(btn_16)) x = 15;
		if (x>=0) {
			if ((x < p->d.intt.maximum) && (x >= p->d.intt.minimum)) {
				ParameterChange(p, x, 0xFFFFFFFF);
			}
		}

	}
		break;
	case param_type_bin_16bits: {
		int x = 0;
		if (BTN_NAV_DOWN(btn_1)) x |= 1<<0;
		if (BTN_NAV_DOWN(btn_2)) x |= 1<<1;
		if (BTN_NAV_DOWN(btn_3)) x |= 1<<2;
		if (BTN_NAV_DOWN(btn_4)) x |= 1<<3;
		if (BTN_NAV_DOWN(btn_5)) x |= 1<<4;
		if (BTN_NAV_DOWN(btn_6)) x |= 1<<5;
		if (BTN_NAV_DOWN(btn_7)) x |= 1<<6;
		if (BTN_NAV_DOWN(btn_8)) x |= 1<<7;
		if (BTN_NAV_DOWN(btn_9)) x |= 1<<8;
		if (BTN_NAV_DOWN(btn_10)) x |= 1<<9;
		if (BTN_NAV_DOWN(btn_11)) x |= 1<<10;
		if (BTN_NAV_DOWN(btn_12)) x |= 1<<11;
		if (BTN_NAV_DOWN(btn_13)) x |= 1<<12;
		if (BTN_NAV_DOWN(btn_14)) x |= 1<<13;
		if (BTN_NAV_DOWN(btn_15)) x |= 1<<14;
		if (BTN_NAV_DOWN(btn_16)) x |= 1<<15;
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

static void UIPollButtons(void) {
	Btn_Nav_CurStates.word = Btn_Nav_CurStates.word | Btn_Nav_Or.word;
	Btn_Nav_Or.word = 0;
	const ui_node_t * head_node = menu_stack[menu_stack_position].parent;

	// list navigation
	switch (head_node->node_type) {
	case node_type_node_list:
	case node_type_param_list:
	case node_type_object_list:
	case node_type_object: {
		if (BTN_NAV_DOWN(btn_nav_Down))
			UINode_Increment(head_node);
		if (BTN_NAV_DOWN(btn_nav_Up))
			UINode_Decrement(head_node);
		if (EncBuffer[0] > 0) {
			UINode_Increment(head_node);
			EncBuffer[0] = 0;
		}
		if (EncBuffer[0] < 0) {
			UINode_Decrement(head_node);
			EncBuffer[0] = 0;
		}
	}
		break;
	default:
		break;
	}

	if (head_node->node_type == node_type_node_list) {
		ui_node_t *cur =
				&((ui_node_t *) (head_node->nodeList.array))[menu_stack[menu_stack_position].currentpos];
		if (BTN_NAV_DOWN(btn_nav_Left))
			UINode_Decrement(cur);
		if (BTN_NAV_DOWN(btn_nav_Right))
			UINode_Increment(cur);
		if (BTN_NAV_DOWN(btn_nav_Enter))
			ui_enter_node(cur);
		if ((cur->node_type == node_type_integer_value)) {
			if (EncBuffer[1] > 0) {
				UINode_Increment(cur);
				EncBuffer[1]--;
			}
			if (EncBuffer[1] < 0) {
				UINode_Decrement(cur);
				EncBuffer[1]++;
			}
		}
	} else if (head_node->node_type == node_type_custom) {
		if (head_node->custom.buttonFunction != 0)
			(head_node->custom.buttonFunction)(head_node->custom.userdata);
	} else if (head_node->node_type == node_type_param_list) {
		int n = head_node->paramList.nparams;
		if (n > 0) {
			Parameter_t *p =
					&head_node->paramList.params[menu_stack[menu_stack_position].currentpos];
			if (BTN_NAV_DOWN(btn_nav_Enter)) {
				ParamMenu.param.param = p;
				ParamMenu.param.param_name =
						&head_node->paramList.param_names[menu_stack[menu_stack_position].currentpos];
				ui_enter_node(&ParamMenu);
			}
			ProcessEncoderParameter(p, &EncBuffer[1]);
			ProcessStepButtonsParameter(p);
		}
	} else if (head_node->node_type == node_type_object_list) {
		if (head_node->objList.objs[menu_stack[menu_stack_position].currentpos].nparams
				> 0) {
			Parameter_t *p = head_node->objList.objs[menu_stack[menu_stack_position].currentpos].params;
			ProcessEncoderParameter(p, &EncBuffer[1]);
			ProcessStepButtonsParameter(p);
		}
		if (BTN_NAV_DOWN(btn_nav_Enter) && (menu_stack[menu_stack_position].maxposition>0)) {
			// todo: use a stack of ObjMenu's
			ObjMenu.obj.obj =
					&(head_node->objList.objs)[menu_stack[menu_stack_position].currentpos];
			// copy object name, need a null terminated string now
			int i;
			for (i = 0; i < MAX_PARAMETER_NAME_LENGTH; i++) {
				((char *) ObjMenu.name)[i] = ObjMenu.obj.obj->name[i];
			}
			((char *) ObjMenu.name)[i] = 0;
			ui_enter_node(&ObjMenu);
		}
	} else if (head_node->node_type == node_type_object) {
		int nparams = head_node->obj.obj->nparams;
		if (nparams > 0) {
			int pos = menu_stack[menu_stack_position].currentpos;
			if (pos < nparams) {
				Parameter_t *p = &(head_node->obj.obj->params)[pos];
				if (BTN_NAV_DOWN(btn_nav_Enter)) {
					Parameter_name_t *pn =
							&(head_node->obj.obj->param_names)[menu_stack[menu_stack_position].currentpos];
					ParamMenu.param.param = p;
					ParamMenu.param.param_name = pn;
					ui_enter_node(&ParamMenu);
				}
				ProcessEncoderParameter(p, &EncBuffer[1]);
				ProcessStepButtonsParameter(p);
			}
		}
	} else if (head_node->node_type == node_type_param) {
		ProcessEncoderParameter(head_node->param.param, &EncBuffer[1]);
		ProcessStepButtonsParameter(head_node->param.param);
	}

	if (BTN_NAV_DOWN(btn_nav_Back))
		nav_Back();

	// for repaint diagnosis:
	if (BTN_NAV_DOWN(btn_nav_Shift))
		LCD_grey();

	Btn_Nav_CurStates.word = Btn_Nav_CurStates.word & ~Btn_Nav_And.word;
	Btn_Nav_PrevStates = Btn_Nav_CurStates;
	Btn_Nav_And.word = 0;
}


static void drawParamValue(int line, int x, Parameter_t *param) {
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

static void drawParamValueInv(int line, int x, Parameter_t *param) {
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

static void drawDispValue(int line, int x, Display_meta_t *disp) {
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

static void drawDispValueInv(int line, int x, Display_meta_t *disp) {
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

static void UIUpdateLCD(void) {

	const ui_node_t * head_node = menu_stack[menu_stack_position].parent;
	const int current_menu_position = menu_stack[menu_stack_position].currentpos;
	const int current_menu_length = menu_stack[menu_stack_position].maxposition;

	if (lcd_dirty_flags & lcd_dirty_flag_clearscreen) {
		LCD_clear();
		LED_clear(LED_STEPS);
		LED_clear(LED_RING_LEFT);
		LED_clear(LED_RING_RIGHT);
		lcd_dirty_flags &= ~lcd_dirty_flag_clearscreen;
		return;
	}
	if (lcd_dirty_flags & lcd_dirty_flag_header) {
		DisplayHeading();
		lcd_dirty_flags &= ~lcd_dirty_flag_header;
		return;
	}
	if (lcd_dirty_flags & lcd_dirty_flag_listnav) {
		// update list navigation indicators
		switch (head_node->node_type) {
		case node_type_custom:
			break;
		default:
			ShowListPositionOnEncoderLEDRing(LED_RING_LEFT, current_menu_position,
					current_menu_length);
			LCD_drawChar(0, 3, current_menu_position > 0 ? CHAR_ARROW_UP : 0);
			LCD_drawChar(0, 5,
					current_menu_position < (current_menu_length - 1) ?
							CHAR_ARROW_DOWN : 0);
			LED_clear(LED_STEPS);
		}
		if (menu_stack_position > 0)
			LCD_drawStringInv(0, STATUSROW, "BACK");
		else
			LCD_drawString(0, STATUSROW, "    ");
		lcd_dirty_flags &= ~lcd_dirty_flag_listnav;
		return;
	}
	// todo: get even lazier by painting list element names only when scrolled

	switch (head_node->node_type) {
	case node_type_node_list: {
		int l = head_node->nodeList.length;
		ui_node_t *k = (ui_node_t *) head_node->nodeList.array;
		int offset = 0;
		if (current_menu_position > 3)
			offset = current_menu_position - 3;
		if ((l - current_menu_position) < 3)
			offset = l - 6;
		if (l < STATUSROW)
			offset = 0;
		LCD_drawChar(61, 3, '+');
		LCD_drawChar(61, 5, '-');
		int line;
		for (line = 0; line < (STATUSROW - 1); line++) {
			if (offset + line < l) {
				if (offset + line == current_menu_position)
					UINode_DrawInv(LCD_COL_INDENT, line + 1, &k[offset + line]);
				else
					UINode_Draw(LCD_COL_INDENT, line + 1, &k[offset + line]);
			} else
				// blank
				LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
		}
		ui_node_t * sel = &k[menu_stack[menu_stack_position].currentpos];

		if ((sel->node_type == node_type_node_list)
				|| (sel->node_type == node_type_custom)
				|| (sel->node_type == node_type_param_list)
				|| (sel->node_type == node_type_action_function
						&& sel->fnctn.fnctn != 0))
			LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
		else
			LCD_drawString(LCD_COL_ENTER, STATUSROW, "     ");
	}
		break;
	case node_type_param_list: {
		Parameter_t *params = head_node->paramList.params;
		Parameter_name_t *param_names = head_node->paramList.param_names;
		int l = head_node->paramList.nparams;
		int offset = 0;
		if (current_menu_position > 3)
			offset = current_menu_position - 3;
		if ((l - current_menu_position) < 3)
			offset = l - 6;
		if (l < STATUSROW)
			offset = 0;
		if (l>0) ShowParameterOnButtonArrayLEDs(LED_STEPS,&params[current_menu_position]);
		LCD_drawChar(LCD_COL_RIGHT, 3, '-');
		LCD_drawChar(LCD_COL_RIGHT, 5, '+');
		int line;
		for (line = 0; line < (STATUSROW - 1); line++) {
			if (offset + line < l) {
				if (offset + line == current_menu_position) {
					LCD_drawStringInvN(LCD_COL_INDENT, line + 1,
							param_names[offset + line].name,
							MAX_PARAMETER_NAME_LENGTH);
					LCD_drawStringInvN(
							LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH,
							line + 1, "", 3);
					drawParamValueInv(line + 1, LCD_VALUE_POSITION,
							&params[offset + line]);
				} else {
					LCD_drawStringN(LCD_COL_INDENT, line + 1,
							param_names[offset + line].name,
							MAX_PARAMETER_NAME_LENGTH);
					LCD_drawStringN(
							LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH,
							line + 1, "", 3);
					drawParamValue(line + 1, LCD_VALUE_POSITION,
							&params[offset + line]);
				}
			} else
				// blank
				LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
		}
		LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
	}
		break;
	case node_type_param: {
		LCD_clear();
		Parameter_t *p = head_node->param.param;
		Parameter_name_t *pn = head_node->param.param_name;
		int line = 1;
		LCD_drawStringN(LCD_COL_INDENT, line, pn->name,
				MAX_PARAMETER_NAME_LENGTH);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "type", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->type);
		line++;
		switch (p->type) {
		case param_type_frac_sq27:
		case param_type_frac_uq27:
			LCD_drawStringN(LCD_COL_INDENT, line, "value", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.value);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "modvalue", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.modvalue);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "finalvalue", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.finalvalue);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "offset", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.offset);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "multiplier", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.multiplier);
			ShowParameterOnEncoderLEDRing(LED_RING_RIGHT, p);
			break;
		case param_type_bin_1bit_momentary:
		case param_type_bin_1bit_toggle:
		case param_type_bin_16bits:
		case param_type_bin_32bits:
			ShowParameterOnButtonArrayLEDs(LED_STEPS,p);
			LCD_drawStringN(LCD_COL_INDENT, line, "value", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.value);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "modvalue", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.modvalue);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "finalvalue", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.finalvalue);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "nbits", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.nbits);
			line++;
			break;
		case param_type_int:
			LCD_drawStringN(LCD_COL_INDENT, line, "value", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.value);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "modvalue", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.modvalue);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "finalvalue", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.finalvalue);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "minimum", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.minimum);
			line++;
			LCD_drawStringN(LCD_COL_INDENT, line, "maximum", 10);
			LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.maximum);
			ShowParameterOnEncoderLEDRing(LED_RING_RIGHT, p);
			break;
		default:
			LCD_drawStringN(LCD_COL_INDENT, line, "undefined", 10);
		}
	}
		break;
	case node_type_object_list: {
		ui_object_t *ui_objects;
		ui_objects = head_node->objList.objs;
		int l = head_node->objList.nobjs;
		int offset = 0;
		if (current_menu_position > 3)
			offset = current_menu_position - 3;
		if ((l - current_menu_position) < 3)
			offset = l - 6;
		if (l < STATUSROW)
			offset = 0;
		if (ui_objects[current_menu_position].nparams>0) ShowParameterOnButtonArrayLEDs(LED_STEPS,&ui_objects[current_menu_position].params[0]);
		LCD_drawChar(LCD_COL_RIGHT, 3, '-');
		LCD_drawChar(LCD_COL_RIGHT, 5, '+');
		int line;
		for (line = 0; line < (STATUSROW - 1); line++) {
			if (offset + line < l) {
				ui_object_t *obj = &ui_objects[offset + line];
				if (offset + line == current_menu_position) {
					LCD_drawStringInvN(LCD_COL_INDENT, line + 1, obj->name,
							MAX_PARAMETER_NAME_LENGTH);
					LCD_drawStringInvN(
							LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH,
							line + 1, "", 3);
					if (obj->nparams) {
						drawParamValueInv(line + 1, LCD_VALUE_POSITION,
								obj->params);
						ShowParameterOnEncoderLEDRing(LED_RING_RIGHT,
								obj->params);
					} else if (obj->ndisplays) {
						drawDispValueInv(line + 1, LCD_VALUE_POSITION,
								obj->displays);
						LED_clear(LED_RING_RIGHT);
					} else {
						LED_clear(LED_RING_RIGHT);
						LCD_drawStringN(LCD_VALUE_POSITION, line + 1, "", 8);
					}
				} else {
					LCD_drawStringN(LCD_COL_INDENT, line + 1, obj->name,
							MAX_PARAMETER_NAME_LENGTH);
					LCD_drawStringN(
							LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH,
							line + 1, "", 3);
					if (obj->nparams) {
						drawParamValue(line + 1, LCD_VALUE_POSITION,
								obj->params);
					} else if (obj->ndisplays) {
						drawDispValue(line + 1, LCD_VALUE_POSITION,
								obj->displays);
					} else {
						LCD_drawStringN(LCD_VALUE_POSITION, line + 1, "", 8);
					}
				}
			} else
				// blank
				LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
		}
		LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
	}
		break;
	case node_type_object: {
		ui_object_t *ui_object;
		ui_object = head_node->obj.obj;
		int l = ui_object->nparams + ui_object->ndisplays;
		int offset = 0;
		if (current_menu_position > 3)
			offset = current_menu_position - 3;
		if ((l - current_menu_position) < 3)
			offset = l - 6;
		if (l < STATUSROW)
			offset = 0;
		LCD_drawChar(LCD_COL_RIGHT, 3, '-');
		LCD_drawChar(LCD_COL_RIGHT, 5, '+');
		int line;
		for (line = 0; line < (STATUSROW - 1); line++) {
			if (offset < l) {
				if (offset == current_menu_position)
					if (offset < ui_object->nparams) {
						LCD_drawStringInvN(LCD_COL_INDENT, line + 1,
								ui_object->param_names[offset].name,
								MAX_PARAMETER_NAME_LENGTH);
						LCD_drawStringInvN(
								LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH,
								line + 1, "", 3);
						drawParamValueInv(line + 1, LCD_VALUE_POSITION,
								&ui_object->params[offset]);
					} else {
						LCD_drawStringInvN(LCD_COL_INDENT, line + 1,
								ui_object->displays[offset - ui_object->nparams].name,
								MAX_PARAMETER_NAME_LENGTH);
						LCD_drawStringN(
								LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH,
								line + 1, "", 3);
						drawDispValueInv(line + 1, LCD_VALUE_POSITION,
								&ui_object->displays[offset - ui_object->nparams]);
					}
				else if (offset < ui_object->nparams) {
					LCD_drawStringN(LCD_COL_INDENT, line + 1,
							ui_object->param_names[offset].name,
							MAX_PARAMETER_NAME_LENGTH);
					drawParamValue(line + 1, LCD_VALUE_POSITION,
							&ui_object->params[offset]);
				} else {
					LCD_drawStringN(LCD_COL_INDENT, line + 1,
							ui_object->displays[offset - ui_object->nparams].name,
							MAX_PARAMETER_NAME_LENGTH);
					drawDispValue(line + 1, LCD_VALUE_POSITION,
							&ui_object->displays[offset - ui_object->nparams]);
				}
			} else
				// blank line
				LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
			offset++;
		}
		if (current_menu_position < ui_object->nparams) {
			Parameter_t *p = &ui_object->params[current_menu_position];
			ShowParameterOnEncoderLEDRing(LED_RING_RIGHT, p);
			ShowParameterOnButtonArrayLEDs(LED_STEPS, p);
		}
		LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
	}
		break;
	case node_type_short_value: {
		static int xpos = 0;
		if (lcd_dirty_flags & lcd_dirty_flag_initial) {
			lcd_dirty_flags &= ~lcd_dirty_flag_initial;
			xpos = 0;
		} else {
			uint8_t *p = &lcd_buffer[xpos + LCDWIDTH];
			int16_t *pvalue = head_node->shortValue.pvalue;
			const int ylim = 6*8-1;
			if (pvalue) {
				LCD_drawNumber5D(20, 7, *pvalue);
				int32_t y = ylim - (((*pvalue - head_node->shortValue.minvalue)*3*head_node->shortValue.scale)>>16);
				int v = LCDROWS-2;
				if (xpos & 1) {
					uint32_t uy = y;
					while(v--) {
						if (uy<8)
							*p=1<<uy;
						else
							*p=0;
						uy=uy-8;
						p += LCDWIDTH;
					}
				} else {
					// with zero-line stipples, and clamped y
					if (y<0) y=0;
					if (y>ylim) y = ylim;
					uint32_t uy = y;
					uint32_t y0 = ylim - (((0 - head_node->shortValue.minvalue)*3*head_node->shortValue.scale)>>16);
					while(v--) {
						int v;
						if (uy<8)
							v=1<<uy;
						else
							v=0;
						if (y0<8)
							v |= 1<<y0;
						*p=v;
						uy=uy-8;
						y0=y0-8;
						p += LCDWIDTH;
					}
				}
				xpos = (xpos + 1)&(LCDWIDTH-1);
			}
		}
	}
		break;
	case node_type_custom: {
		if (head_node->custom.displayFunction != 0) {
			if (lcd_dirty_flags & lcd_dirty_flag_initial) {
				(head_node->custom.displayFunction)(head_node->custom.userdata, 1);
				lcd_dirty_flags &= ~lcd_dirty_flag_initial;
			} else {
				(head_node->custom.displayFunction)(head_node->custom.userdata, 0);
			}
		}
	}
		break;
		// following are leaf nodes:
	case node_type_integer_value:
	case node_type_action_function:
		break;
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

static WORKING_AREA(waThreadUI2, 512);
static THD_FUNCTION(ThreadUI2, arg) {
	(void) (arg);
	chRegSetThreadName("ui2");
	while (1) {
		// todo: make better
		// the motivation to differentiate between
		// input handling and screen painting is that
		// screen painting can be much slower
		// than knob tweaking
		UIPollButtons();
		UIUpdateLCD();
		chThdSleepMilliseconds(15);
	}
}

void ui_init(void) {
	Btn_Nav_Or.word = 0;
	Btn_Nav_And.word = ~0;
	lcd_dirty_flags = ~0;

	chThdCreateStatic(waThreadUI2, sizeof(waThreadUI2), NORMALPRIO, ThreadUI2,
			NULL);
	axoloti_control_init();

	int i;
	for (i = 0; i < 2; i++) {
		EncBuffer[i] = 0;
	}
}

void ui_go_home(void) {
	menu_stack_position = 0;
	lcd_dirty_flags = ~0;
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
