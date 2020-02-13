/*
 * midi_gpio.c
 *
 *      Author: jtaelman
 */

#include "ch.h"
#include "hal.h"
#include "axoloti_board.h"
#include "midi.h"
#include "midi_routing.h"
#include "midi_decoder.h"
#include "midi_encoder.h"
#include "midi_gpio.h"
#include "patch.h"
#include "logging.h"


midi_routing_t midi_gpio_inputmap = {
    .name = "GPIO",
    .nports = 0,
    .bmvports = {
        0b0000000000000000
    }
};

midi_routing_t midi_gpio_outputmap = {
    .name = "GPIO",
    .nports = 0,
    .bmvports = {
        0b0000000000000000
    }
};

static void dispatch_midi_input(midi_message_t midi_msg) {
  int portmap = midi_gpio_inputmap.bmvports[0];
  midi_input_dispatch(portmap, midi_msg);
}

static midi_decoder_state_t gpio_midi_decoder = {
    .midi_rcv_cb = dispatch_midi_input
};

// Midi OUT

static void midi_gpio_send(midi_message_t midimsg) {
  // TODO: running status
  // TODO: skip other messages when sysex is in progress
  int l = midi_encoder_get_length(midimsg);
  sdWrite(&SD2, &midimsg.bytes.b0, l);
}

int midi_gpio_GetOutputBufferPengpiog(void) {
// todo: check references!
  return 0;  //chOQGetFullI(&SDMIDI.oqueue);
}

// Midi UART...
static const SerialConfig sdMidiCfg = {
    31250, // baud
    0, 0, 0
};

static THD_WORKING_AREA(waThreadMidiIn, 256);
static THD_FUNCTION( ThreadMidiIn, arg) {
  (void) arg;
  chRegSetThreadName("midi_gpio_in");
  while (1) {
    int msg;
    msg = sdGet(&SD2);
    if (msg == MSG_RESET) {
      break;
    }
    midi_decoder_process(&gpio_midi_decoder, (char) msg);
  }
}

midi_output_buffer_t midi_gpio_output;

static THD_WORKING_AREA(waThreadMidiOut, 256);
static THD_FUNCTION( ThreadMidiOut, arg) {
  (void) arg;
  chRegSetThreadName("midi_gpio_out");
  while (1) {
    eventmask_t evt = chEvtWaitOne(3);
    if (evt == 2)
      break;
    midi_message_t m;
    msg_t r;
    r = midi_output_buffer_get(&midi_gpio_output, &m);
    while (r == MSG_OK) {
      midi_gpio_send(m);
      r = midi_output_buffer_get(&midi_gpio_output, &m);
    }
  }
}

static thread_t *thd_midi_gpio_reader;
static thread_t *thd_midi_gpio_writer;

static void notify(void *obj) {
  chEvtSignal(thd_midi_gpio_writer, 1);
}

static int midi_gpio_active = 0;

static void midi_gpio_init(void) {
  /*
   * Activates the serial driver 2 using the driver default configuration.
   * PA2 (TX) and PA3 (RX) are routed to USART2.
   */
  if (!midi_gpio_active) {
    load_midi_routing(&midi_gpio_inputmap, in);
    load_midi_routing(&midi_gpio_outputmap, out);

    palSetPadMode(GPIOA, 3, PAL_MODE_ALTERNATE(7) | PAL_MODE_INPUT); // RX
    palSetPadMode(GPIOA, 2, PAL_MODE_OUTPUT_PUSHPULL); // TX
    palSetPadMode(GPIOA, 2, PAL_MODE_ALTERNATE(7)); // TX

    sdStart(&SD2, &sdMidiCfg);
    thd_midi_gpio_reader = chThdCreateStatic(waThreadMidiIn,
        sizeof(waThreadMidiIn), NORMALPRIO, ThreadMidiIn, NULL);
    thd_midi_gpio_writer = chThdCreateStatic(waThreadMidiOut,
        sizeof(waThreadMidiOut), NORMALPRIO, ThreadMidiOut, NULL);
    midi_output_buffer_objinit(&midi_gpio_output, notify);
    midi_gpio_inputmap.nports = 1;
    midi_gpio_outputmap.nports = 1;
  }
  midi_gpio_active = 1;
}

static void midi_gpio_deinit(void) {
  palSetPadMode(GPIOA, 2, PAL_MODE_INPUT_ANALOG);
  palSetPadMode(GPIOA, 3, PAL_MODE_INPUT_ANALOG);
  midi_gpio_outputmap.bmvports[0] = 0;
  midi_gpio_inputmap.nports = 0;
  midi_gpio_outputmap.nports = 0;

  if (midi_gpio_active) {
    sdStop(&SD2);
    chEvtSignal(thd_midi_gpio_writer, 2);
    chThdWait(thd_midi_gpio_writer);
    chThdWait(thd_midi_gpio_reader);
    LogTextMessage("midi_gpio stopped");
    midi_gpio_active = 0;
  }
}

void midi_gpio_enable(int enable) {
  if (enable) {
    midi_gpio_init();
  } else {
    midi_gpio_deinit();
  }
}
