#ifndef INPUT_EVT_H
#define INPUT_EVT_H

#include "stdbool.h"

enum {
	quadrant_main = 0,
	quadrant_topleft = 1,
	quadrant_topright = 2,
	quadrant_bottomleft = 3,
	quadrant_bottomright = 4,
	quadrant_bottom = 5,
};

enum {
	btn_up = 1,
	btn_down = 2,
	btn_encoder = 3,
	btn_F,
	btn_S,
	btn_X,
	btn_E,
	btn_1,
	btn_2,
	btn_3,
	btn_4,
	btn_5,
	btn_6,
	btn_7,
	btn_8,
	btn_9,
	btn_10,
	btn_11,
	btn_12,
	btn_13,
	btn_14,
	btn_15,
	btn_16,
};

#define EVT_MODIFIER_SHIFT 1

typedef union {
  struct {
	  uint8_t quadrant;
	  uint8_t modifiers;
	  /* for button up/down events, value is 1 for down, 0 for up */
	  /* for encoder events, value is increment */
	  int8_t value;
	  uint8_t button;
  } fields;
  int32_t word;
} input_event;

extern bool evtIsEnter(input_event evt);
extern bool evtIsUp(input_event evt);
extern bool evtIsDown(input_event evt);

int getValuFromInputEvent(input_event evt);

extern void queueInputEventI(input_event evt);

#endif
