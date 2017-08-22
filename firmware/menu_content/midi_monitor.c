#include "../ui.h"
#include "../midi.h"
#include "../axoloti_control.h"

static uint32_t fhandle_evt_midimon(const struct ui_node * node, input_event evt) {
   return 0;
}

static const char midiStatusNames[7][4] = {
	{'N','O','f','f'},
	{'N','O','n',0},
	{'K','P','r','s'},
	{'C','C','\0',0},
	{'P','g','m',0},
	{'C','P','r','s'},
	{'B','e','n','d'}
};

static const char midiRTNames[16][4] = {
	{'S','E','x','['}, // F0
	{'M','T','C',0},   // F1
	{'S','P','P',0},   // F2
	{'S','S','e','l'}, // F3
	{'?','F','4','?'}, // F4
	{'?','F','5','?'}, // F5
	{'T','u','n','e'}, // F6
	{'S','E','X',']'}, // F7
	{'C','l','k',0},   // F8
	{'M','e','a','s'}, // F9
	{'S','t','r','t'}, // FA
	{'C','o','n','t'}, // FB
	{'S','t','o','p'}, // FC
	{'?','F','D','?'}, // FD
	{'S','e','n','s'}, // FE
	{'R','s','t',0}    // FF
};

static void fpaint_screen_update_midimon(const struct ui_node * node,
		uint32_t flags) {
	if (flags == lcd_dirty_flag_initial) {
		LCD_drawStringInvN(2, 1, "P", 1);
		LCD_drawStringInvN(8, 1, "Type", 4);
		LCD_drawStringInvN(24, 1, "Ch", 3);
		LCD_drawStringInvN(35, 1, "B1", 4);
		LCD_drawStringInvN(49, 1, "B2", 4);
		return;
	}
	int i = midi_input_buffer.read_index;
	int j;
	for (j = 2; j < 8; j++) {
		midi_message_t m =
				midi_input_buffer.buf[i & (MIDI_RING_BUFFER_SIZE - 1)];
		i--;
		LCD_drawChar(3, j, m.fields.port + '1');
		if (!m.fields.b0) {
			LCD_drawStringN(0, j, "", 21);
			continue;
		} else if ((m.fields.b0 & 0xF0) < 0xF0) {
			LCD_drawStringN(8, j, midiStatusNames[(m.fields.b0 >> 4) - 8], 4);
		} else {
			LCD_drawStringN(8, j, midiRTNames[m.fields.b0 - 0xF0], 4);
		}
		LCD_drawNumber3D(21, j, (m.fields.b0 & 0xF) + 1);
		LCD_drawNumber3D(35, j, m.fields.b1);
		LCD_drawNumber3D(49, j, m.fields.b2);
	}
}

const nodeFunctionTable nodeFunctionTable_midimon = {
		fhandle_evt_midimon,
		fpaint_screen_update_midimon,
		0, 0,
};

