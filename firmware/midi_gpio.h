/*
 * midi_gpio.h
 *
 *      Author: jtaelman
 */

#ifndef MIDI_GPIO_H_
#define MIDI_GPIO_H_


#include <stdint.h>
#include "midi.h"
#include "midi_buffer.h"

void midi_gpio_enable(int enable);

extern midi_output_buffer_t midi_gpio_output;

// report the number of bytes pending for transmission
int midi_gpio_GetOutputBufferPending(void);

extern midi_routing_t midi_gpio_inputmap;
extern midi_routing_t midi_gpio_outputmap;


#endif /* MIDI_GPIO_H_ */
