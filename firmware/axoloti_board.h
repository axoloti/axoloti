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
#ifndef __AXOBOARD_H
#define __AXOBOARD_H

#include "axoloti_defines.h"

#define ADC_CHANNELS 16
extern unsigned short adcvalues[ADC_CHANNELS];

extern Mutex Mutex_DMAStream_1_7;

void axoloti_board_init(void);
void adc_init(void);
void adc_configpads(void);
void adc_convert(void);


#if (BOARD_AXOLOTI_V05)
#define LED1_PORT GPIOG
#define LED1_PIN 6
#define LED2_PORT GPIOC
#define LED2_PIN 6
// SW1 is also BOOT0
#define SW1_PORT GPIOB
#define SW1_PIN 5
#define SW2_PORT GPIOA
#define SW2_PIN 10
#define OCFLAG_PORT GPIOG
#define OCFLAG_PIN 13
#define SDCSW_PORT GPIOD
#define SDCSW_PIN 13

#elif (BOARD_AXOLOTI_V03)
#define LED1_PORT GPIOA
#define LED1_PIN 8
#define SW2_PORT GPIOB
#define SW2_PIN 2
#elif (BOARD_STM32F4DISCOVERY)
// led 1 : green
#define LED1_PORT GPIOD
#define LED1_PIN 12
// led 2 : red
#define LED2_PORT GPIOD
#define LED2_PIN 14
#define SW2_PORT GPIOA
#define SW2_PIN 0
#endif


#ifdef BOARD_AXOLOTI_V05
#define SDMIDI SD6
#else
#define SDMIDI SD1
#endif


#endif
