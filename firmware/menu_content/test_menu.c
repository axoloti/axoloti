#include "test_menu.h"
#include "axoloti_control.h"

// ------ Test menu stuff ------

static uint8_t val0;
static uint8_t val1;
static uint8_t val2;
static uint8_t val3;

static input_event last_evt;

static const char* evt_btn_to_string(int btn) {
	switch(btn) {
	case btn_up : return "up";
	case btn_down : return "down";
	case btn_encoder : return "enc";
	case btn_F : return "F";
	case btn_S : return "S";
	case btn_X : return "X";
	case btn_E : return "E";
	case btn_1 : return "1";
	case btn_2 : return "2";
	case btn_3 : return "3";
	case btn_4 : return "4";
	case btn_5 : return "5";
	case btn_6 : return "6";
	case btn_7 : return "7";
	case btn_8 : return "8";
	case btn_9 : return "9";
	case btn_10 : return "10";
	case btn_11 : return "11";
	case btn_12 : return "12";
	case btn_13 : return "13";
	case btn_14 : return "14";
	case btn_15 : return "15";
	case btn_16 : return "16";
	}
	return "???";
}


static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (evt.fields.button==btn_encoder) {
		switch (evt.fields.quadrant) {
		case quadrant_topleft :
			val0 += evt.fields.value;
			return lcd_dirty_flag_usr0;
		case quadrant_topright :
			val1 += evt.fields.value;
			return lcd_dirty_flag_usr1;
		case quadrant_bottomleft :
			val2 += evt.fields.value;
			return lcd_dirty_flag_usr2;
		case quadrant_bottomright :
			val3 += evt.fields.value;
			return lcd_dirty_flag_usr3;
		default: break;
		}
	}
	last_evt = evt;
	return lcd_dirty_flag_usr4;
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	switch(flags){
	case lcd_dirty_flag_initial:
		LCD_drawStringN(4, 6, "last evt:", 10);
		break;
	case lcd_dirty_flag_usr0:
		LED_setOne(LED_RING_TOPLEFT, val0 % 16);
		LCD_drawNumber3D(10, 2, val0);
		LCD_drawChar(10, 3, val0);
		break;
	case lcd_dirty_flag_usr1:
		LED_setOne(LED_RING_TOPRIGHT, val1 % 16);
		LCD_drawNumber3D(44, 2, val1);
		LCD_drawChar(40, 3, val1);
		break;
	case lcd_dirty_flag_usr2:
		LED_setOne(LED_RING_BOTTOMLEFT, val2 % 16);
		LCD_drawNumber3D(10, 4, val2);
		LCD_drawChar(10, 5, val2);
		break;
	case lcd_dirty_flag_usr3:
		LED_setOne(LED_RING_BOTTOMRIGHT, val3 % 16);
		LCD_drawNumber3D(44, 4, val3);
		LCD_drawChar(40, 5, val3);
		break;
	case lcd_dirty_flag_usr4:
		LCD_drawStringN(32, 6, evt_btn_to_string(last_evt.fields.button), 5);
		LCD_drawNumber3D(0, 7, last_evt.fields.quadrant);
		LCD_drawNumber3D(14, 7, last_evt.fields.value);
		LCD_drawNumber3D(28, 7, last_evt.fields.modifiers);
		break;
	}
}

nodeFunctionTable nodeFunctionTable_test = {
		fhandle_evt,
		fpaint_screen_update,
		0,
		0,
};
