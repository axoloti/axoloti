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

/*
 * Adapted from codec.h
 * Created on: Jun 7, 2012
 * Author: Kumar Abhishek
 */

#ifndef __CODEC_CS43L22_H
#define __CODEC_CS43L22_H

#include "ch.h"
#include "hal.h"

#define CODEC_I2C I2CD1
#define CODEC_I2S_ENABLE rccEnableSPI3(FALSE)
#define CODEC_I2S_DISABLE rccDisableSPI3(FALSE)
#define CODEC_I2S SPI3

#define CS43L22_ADDR (0x94 >> 1)

extern void codec_CS43L22_hw_init(void);

extern void codec_CS43L22_hw_reset(void);

extern void codec_CS43L22_writeReg(uint8_t addr, uint8_t data);

extern uint8_t codec_CS43L22_readReg(uint8_t addr);

extern void codec_CS43L22_volCtl(uint8_t vol);

extern void codec_CS43L22_pwrCtl(uint8_t pwr);

extern void codec_CS43L22_muteCtl(uint8_t mute);

extern void codec_CS43L22_sendBeep(void);

extern void codec_CS43L22_i2s_init_48k(void);

#endif /* CODEC_H_ */
