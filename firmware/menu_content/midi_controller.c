
#include "../qgfx.h"
#include "../ui.h"
#include "../midi.h"
#include "../axoloti_control.h"
#include "../glcdfont.h"

// ------ midi controller/keyboard ---

typedef void (*fnDraw)(const gfxq *gfx, void *userdata);
typedef uint32_t (*fnInputHandler)(input_event evt, void *userdata);

typedef struct {
	fnInputHandler evtHandler;
	fnDraw drawInit;
	fnDraw drawUpdate;
	void *userdata;
} qhandler;

#define NQUADS 12

typedef struct {
	int bend;
	int shift;
	int channel;
	int velo;
	int port;
	int rvelo;
	int cc[128];
	int page;
	int8_t note[16];
	qhandler quads[NQUADS];
} midiccdata_t;

extern midiccdata_t midiccdata;

static uint32_t fhandle_evt_midicc(const struct ui_node * node, input_event evt) {
	switch (evt.fields.quadrant) {
		case quadrant_topleft : {
			qhandler *q = &midiccdata.quads[midiccdata.page+0];
			uint32_t r = q->evtHandler(evt, q->userdata);
			return r?lcd_dirty_flag_usr1:0;
		}
		break;
		case quadrant_topright : {
			qhandler *q = &midiccdata.quads[midiccdata.page+1];
			uint32_t r = q->evtHandler(evt, q->userdata);
			return r?lcd_dirty_flag_usr2:0;
		}
		break;
		case quadrant_bottomleft : {
			qhandler *q = &midiccdata.quads[midiccdata.page+2];
			uint32_t r = q->evtHandler(evt, q->userdata);
			return r?lcd_dirty_flag_usr3:0;
		}
		break;
		case quadrant_bottomright : {
			qhandler *q = &midiccdata.quads[midiccdata.page+3];
			uint32_t r = q->evtHandler(evt, q->userdata);
			return r?lcd_dirty_flag_usr4:0;
		}
		break;
		default: break;
	}
	if (evt.fields.button>=btn_1) {
		int i = evt.fields.button-btn_1;
		if (evt.fields.value) {
			int n = i+60+12*(midiccdata.shift);
			if (midiccdata.note[i]!=n) {
				if (midiccdata.note[i]!=-1) {
					midi_message_t m = {.bytes={0,
							MIDI_NOTE_OFF + midiccdata.channel,
							midiccdata.note[i],
							100}};
					midi_input_buffer_put(&midi_input_buffer, m);
				}
				midi_message_t m = {.bytes={0,
						MIDI_NOTE_ON + midiccdata.channel,
						n,
						midiccdata.velo}};
				midi_input_buffer_put(&midi_input_buffer, m);
				midiccdata.note[i]=n;
				return lcd_dirty_flag_usr5;
			}
		} else {
			if (!(evt.fields.modifiers & EVT_MODIFIER_SHIFT) && (midiccdata.note[i] != -1)) {
				midi_message_t m = {.bytes={0,
						MIDI_NOTE_OFF + midiccdata.channel,
						midiccdata.note[i],
						midiccdata.velo}};
				midi_input_buffer_put(&midi_input_buffer, m);
				midiccdata.note[i]=-1;
				return lcd_dirty_flag_usr5;
			}
		}
	} else if (evt.fields.value && evt.fields.quadrant == quadrant_main) {
		if (evt.fields.button == btn_up) {
			midiccdata.page -= 2;
			if (midiccdata.page < 0) midiccdata.page = 0;
			return ~0;
		} else if (evt.fields.button == btn_down) {
			midiccdata.page += 2;
			if (midiccdata.page > NQUADS-4) midiccdata.page = NQUADS-4;
			return ~0;
		}
	}
	return 0;
}
static uint32_t handleInputBend(input_event evt, void *userdata) {
	if (!evt.fields.value) return 0;
	if (evt.fields.button==btn_encoder) {
		midiccdata.bend += evt.fields.value;
		if (midiccdata.bend>63) midiccdata.bend=63;
		if (midiccdata.bend<-64) midiccdata.bend=-64;
		midi_message_t m = {.bytes={0,
				MIDI_PITCH_BEND + midiccdata.channel,
				0,
				64+midiccdata.bend}};
		midi_input_buffer_put(&midi_input_buffer, m);
		return 1;
	} else if (evt.fields.button == btn_up) {
		if (evt.fields.modifiers & EVT_MODIFIER_SHIFT)
			midiccdata.bend=63;
		else
			midiccdata.bend++;
		midi_message_t m = {.bytes={0,
				MIDI_PITCH_BEND + midiccdata.channel,
				0,
				64+midiccdata.bend}};
		midi_input_buffer_put(&midi_input_buffer, m);
		return 1;
	} else if (evt.fields.button == btn_down) {
		if (evt.fields.modifiers & EVT_MODIFIER_SHIFT)
			midiccdata.bend=-64;
		else
			midiccdata.bend--;
		midi_message_t m = {.bytes={0,
				MIDI_PITCH_BEND + midiccdata.channel,
				0,
				64+midiccdata.bend}};
		midi_input_buffer_put(&midi_input_buffer, m);
		return 1;
	}
	return 0;
}
static uint32_t handleInputChannel(input_event evt, void *userdata) {
	if (!evt.fields.value) return 0;
	if (evt.fields.button==btn_up) {
		midiccdata.channel = (midiccdata.channel+1)&0xF;
		return 1;
	} else if (evt.fields.button==btn_down) {
		midiccdata.channel = (midiccdata.channel-1)&0xF;
		return 1;
	}
	return 0;
}
static uint32_t handleInputOct(input_event evt, void *userdata) {
	if (!evt.fields.value) return 0;
	if (evt.fields.button == btn_up) {
		midiccdata.shift++;
		if (midiccdata.shift > 3) midiccdata.shift = 3;
		return 1;
	} else if (evt.fields.button == btn_down) {
		midiccdata.shift--;
		if (midiccdata.shift < -3) midiccdata.shift = -3;
		return 1;
	}
	return 0;
}
static uint32_t handleInputVelocity(input_event evt, void *userdata) {
	if (!evt.fields.value) return 0;
	if (evt.fields.button==btn_encoder) {
		midiccdata.velo += evt.fields.value;
		if (midiccdata.velo>127) midiccdata.velo=127;
		if (midiccdata.velo<1) midiccdata.velo=1;
		return 1;
	} else if (evt.fields.button==btn_up) {
		midiccdata.velo = (midiccdata.velo+1)&0x7F;
		return 1;
	} else if (evt.fields.button==btn_down) {
		midiccdata.velo = (midiccdata.velo-1)&0x7F;
		return 1;
	}
	return 0;
}
static uint32_t handleInputRVelocity(input_event evt, void *userdata) {
	if (!evt.fields.value) return 0;
	if (evt.fields.button==btn_encoder) {
		midiccdata.rvelo += evt.fields.value;
		if (midiccdata.rvelo>127) midiccdata.rvelo=127;
		if (midiccdata.rvelo<1) midiccdata.rvelo=1;
		return 1;
	} else if (evt.fields.button==btn_up) {
		midiccdata.rvelo = (midiccdata.rvelo+1)&0x7F;
		return 1;
	} else if (evt.fields.button==btn_down) {
		midiccdata.rvelo = (midiccdata.rvelo-1)&0x7F;
		return 1;
	}
	return 0;
}
static uint32_t handleInputCC(input_event evt, void *userdata) {
	int i = (int)userdata;
	if (!evt.fields.value) return 0;
	if (evt.fields.button==btn_encoder) {
		midiccdata.cc[i] = __USAT(midiccdata.cc[i]+evt.fields.value,7);
		midi_message_t m = {.bytes={0,
				MIDI_CONTROL_CHANGE + midiccdata.channel,
				i,
				midiccdata.cc[i]}};
		midi_input_buffer_put(&midi_input_buffer, m);
		return 1;
	} else if (evt.fields.button==btn_up) {
		midiccdata.cc[i] = __USAT(midiccdata.cc[i]+1,7);
		midi_message_t m = {.bytes={0,
				MIDI_CONTROL_CHANGE + midiccdata.channel,
				i,
				midiccdata.cc[i]}};
		midi_input_buffer_put(&midi_input_buffer, m);
		return 1;
	} else if (evt.fields.button==btn_down) {
		midiccdata.cc[i] = __USAT(midiccdata.cc[i]-1,7);
		midi_message_t m = {.bytes={0,
				MIDI_CONTROL_CHANGE + midiccdata.channel,
				i,
				midiccdata.cc[i]}};
		midi_input_buffer_put(&midi_input_buffer, m);
		return 1;
	}
	return 0;
}

static void paintInitialBend(const gfxq *gfx, void *userdata) {
	gfx->drawStringN(3,0,"Bend",7);
	gfx->drawChar(0, 0, CHAR_ARROW_UP);
	gfx->drawChar(0, 2, CHAR_ARROW_DOWN);
}
static void paintInitialChannel(const gfxq *gfx, void *userdata) {
	gfx->drawStringN(3,0,"Channel",7);
	gfx->drawChar(0, 0, CHAR_ARROW_UP);
	gfx->drawChar(0, 2, CHAR_ARROW_DOWN);
}
static void paintInitialOct(const gfxq *gfx, void *userdata) {
	gfx->drawStringN(3,0,"Oct",7);
	gfx->drawChar(0, 0, CHAR_ARROW_UP);
	gfx->drawChar(0, 2, CHAR_ARROW_DOWN);
}
static void paintInitialVelocity(const gfxq *gfx, void *userdata) {
	gfx->drawStringN(3,0,"Velocity",8);
	gfx->drawChar(0, 0, CHAR_ARROW_UP);
	gfx->drawChar(0, 2, CHAR_ARROW_DOWN);
}
static void paintInitialRVelocity(const gfxq *gfx, void *userdata) {
	gfx->drawStringN(3,0,"RelVelo",8);
	gfx->drawChar(0, 0, CHAR_ARROW_UP);
	gfx->drawChar(0, 2, CHAR_ARROW_DOWN);
}
static void paintInitialCC(const gfxq *gfx, void *userdata) {
	int i = (int)userdata;
	char c[6] = {'C','C','0','0','0',0};
	int j = i/100;
	c[2] = '0'+j; i-=j;
	j = i/10;
	c[3] = '0'+j; i-=j;
	c[4] = '0'+i;
	gfx->drawStringN(3,0,c,6);
	gfx->drawChar(0, 0, CHAR_ARROW_UP);
	gfx->drawChar(0, 2, CHAR_ARROW_DOWN);
}
static void paintUpdateBend(const gfxq *gfx, void *userdata) {
   gfx->setEncoderOne((8+(midiccdata.bend>>3)) & 0xF);
   gfx->drawNumber3D(4, 1, midiccdata.bend);
}
static void paintUpdateChannel(const gfxq *gfx, void *userdata) {
   gfx->drawNumber3D(4, 1, midiccdata.channel+1);
}
static void paintUpdateOct(const gfxq *gfx, void *userdata) {
   gfx->drawNumber3D(4, 1, midiccdata.shift);
}
static void paintUpdateVelocity(const gfxq *gfx, void *userdata) {
   gfx->drawNumber3D(4, 1, midiccdata.velo);
   gfx->setEncoderOne((midiccdata.velo>>3) & 0xF);
}
static void paintUpdateRVelocity(const gfxq *gfx, void *userdata) {
   gfx->drawNumber3D(4, 1, midiccdata.rvelo);
   gfx->setEncoderOne((midiccdata.rvelo>>3) & 0xF);
}
static void paintUpdateCC(const gfxq *gfx, void *userdata) {
   int i = (int)userdata;
   gfx->drawNumber3D(4, 1, midiccdata.cc[i]);
   gfx->setEncoderOne((midiccdata.cc[i]>>3) & 0xF);
}

midiccdata_t midiccdata = {
		.bend = 0,
		.shift = 0,
		.channel = 0,
		.velo = 100,
		.port = 0,
		.rvelo = 0,
		.page = 0,
		{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
		{{handleInputBend,paintInitialBend,paintUpdateBend,0},
		 {handleInputChannel,paintInitialChannel,paintUpdateChannel,0},
		 {handleInputOct,paintInitialOct,paintUpdateOct,0},
		 {handleInputVelocity,paintInitialVelocity,paintUpdateVelocity,0},
		 {handleInputRVelocity,paintInitialRVelocity,paintUpdateRVelocity,0},
		 {handleInputCC,paintInitialCC,paintUpdateCC,(void *)0},
		 {handleInputCC,paintInitialCC,paintUpdateCC,(void *)1},
		 {handleInputCC,paintInitialCC,paintUpdateCC,(void *)2},
		 {handleInputCC,paintInitialCC,paintUpdateCC,(void *)3},
		 {handleInputCC,paintInitialCC,paintUpdateCC,(void *)4},
		 {handleInputCC,paintInitialCC,paintUpdateCC,(void *)5},
		 {handleInputCC,paintInitialCC,paintUpdateCC,(void *)6},
		}
};

static void fpaint_screen_update_midicc(const struct ui_node * node, uint32_t flag) {
   switch (flag) {
   	case 0: return;
   	case lcd_dirty_flag_initial: {
   		qhandler *q = &midiccdata.quads[midiccdata.page];
   		q->drawInit(&gfx_Q[0], q->userdata); q++;
   		q->drawInit(&gfx_Q[1], q->userdata); q++;
   		q->drawInit(&gfx_Q[2], q->userdata); q++;
   		q->drawInit(&gfx_Q[3], q->userdata);
		LCD_drawStringN(20,7,"...KEYS...",10);
   	} break;
   	case lcd_dirty_flag_usr0: {
		LCD_drawStringN(20,7,"...KEYS...",10);
		if (midiccdata.page>0) LCD_drawChar(0,7,CHAR_ARROW_UP);
		else LCD_drawChar(0,7,' ');
		if (midiccdata.page<(NQUADS-4)) LCD_drawChar(10,7,CHAR_ARROW_DOWN);
		else LCD_drawChar(12,7,' ');
   	} break;
   	case lcd_dirty_flag_usr1: {
   		qhandler *q = &midiccdata.quads[midiccdata.page + 0];
   		q->drawUpdate(&gfx_Q[0],q->userdata);
   	} break;
   	case lcd_dirty_flag_usr2: {
   		qhandler *q = &midiccdata.quads[midiccdata.page + 1];
   		q->drawUpdate(&gfx_Q[1],q->userdata);
   	} break;
   	case lcd_dirty_flag_usr3: {
   		qhandler *q = &midiccdata.quads[midiccdata.page + 2];
   		q->drawUpdate(&gfx_Q[2],q->userdata);
   	} break;
   	case lcd_dirty_flag_usr4: {
   		qhandler *q = &midiccdata.quads[midiccdata.page + 3];
   		q->drawUpdate(&gfx_Q[3],q->userdata);
   	} break;
   	case lcd_dirty_flag_usr5: {
	   // note on indicators
	   int i;
	   int o = 0;
	   for(i=15;i>=0;i--) {
	   	o <<= 1;
	   	o |= (midiccdata.note[i]==-1)?0:1;
	   }
	   LED_set(LED_STEPS, o);
   	} break;
   	default:
   	break;
   }
}

const nodeFunctionTable nodeFunctionTable_midicc = {
		fhandle_evt_midicc,
		fpaint_screen_update_midicc,
		0,
		0,
};
