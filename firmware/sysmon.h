/**
 * Copyright (C) 2015 Johannes Taelman
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

#ifndef _SYSMON_H
#define _SYSMON_H

void sysmon_init(void);
void sysmon_blink_pattern(uint32_t pattern);

// just green
#define BLINK_OK 0b01010101010101010101010101010101
// green/red/green/red alternating : boot
#define BLINK_BOOT 0b10011001100110011001100110011001
// green+red
#define BLINK_OVERLOAD 0b11111111111111111111111111111111
// green + red slow blink
#define BLINK_ERROR 0b11110101111101011111010111110101

typedef enum
{
    ERROR_USBH_OVERCURRENT = 0,
    ERROR_OVERVOLT_50,
    ERROR_OVERVOLT_33,
    ERROR_UNDERVOLT_50,
    ERROR_UNDERVOLT_33,
    ERROR_SDRAM,
    ERROR_SDCARD,
    ERROR_CODEC_I2C
} error_flag_t ;

void setErrorFlag(error_flag_t error);
bool getErrorFlag(error_flag_t error);
void errorFlagClearAll(void);
void sysmon_disable_blinker(void);
void sysmon_enable_blinker(void);
uint16_t sysmon_getVoltage50(void);
uint16_t sysmon_getVoltage10(void);

#endif
