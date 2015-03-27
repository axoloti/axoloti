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
#include "hal.h"

extern void codec_ADAU1961_i2s_init(uint16_t sampleRate);
extern void codec_ADAU1961_SAI_init(uint16_t sampleRate);
extern void codec_ADAU1961_hw_init(uint16_t samplerate);
extern void codec_ADAU1961_hw_reset(void);
extern void codec_ADAU1961_Stop(void);

extern int codec_interrupt_timestamp;

#define ADAU1961_REG_R0_CLKC     0x4000
#define ADAU1961_REG_R1_PLLC     0x4002
#define ADAU1961_REG_R2_DMICJ    0x4008
#define ADAU1961_REG_R3_RES      0x4009
#define ADAU1961_REG_R4_RMIXL0   0x400A
#define ADAU1961_REG_R5_RMIXL1   0x400B
#define ADAU1961_REG_R6_RMIXR0   0x400C
#define ADAU1961_REG_R7_RMIXR1   0x400D
#define ADAU1961_REG_R8_LDIVOL   0x400E
#define ADAU1961_REG_R9_RDIVOL   0x400F
#define ADAU1961_REG_R10_MICBIAS 0x4010
#define ADAU1961_REG_R11_ALC0    0x4011
#define ADAU1961_REG_R12_ALC1    0x4012
#define ADAU1961_REG_R13_ALC2    0x4013
#define ADAU1961_REG_R14_ALC3    0x4014
#define ADAU1961_REG_R15_SERP0     0x4015
#define ADAU1961_REG_R16_SERP1     0x4016
#define ADAU1961_REG_R17_CON0    0x4017
#define ADAU1961_REG_R18_CON1    0x4018
#define ADAU1961_REG_R19_ADCC    0x4019
#define ADAU1961_REG_R20_LDVOL   0x401A
#define ADAU1961_REG_R21_RDVOL   0x401B
#define ADAU1961_REG_R22_PMIXL0  0x401C
#define ADAU1961_REG_R23_PMIXL1  0x401D
#define ADAU1961_REG_R24_PMIXR0  0x401E
#define ADAU1961_REG_R25_PMIXR1  0x401F
#define ADAU1961_REG_R26_PLRML   0x4020
#define ADAU1961_REG_R27_PLRMR   0x4021
#define ADAU1961_REG_R28_PLRMM   0x4022
#define ADAU1961_REG_R29_PHPLVOL 0x4023
#define ADAU1961_REG_R30_PHPRVOL 0x4024
#define ADAU1961_REG_R31_PLLVOL  0x4025
#define ADAU1961_REG_R32_PLRVOL  0x4026
#define ADAU1961_REG_R33_PMONO   0x4027
#define ADAU1961_REG_R34_POPCLICK  0x4028
#define ADAU1961_REG_R35_PWRMGMT 0x4029
#define ADAU1961_REG_R36_DACC0   0x402A
#define ADAU1961_REG_R37_DACC1   0x402B
#define ADAU1961_REG_R38_DACC2   0x402C
#define ADAU1961_REG_R39_SERPP   0x402D
#define ADAU1961_REG_R40_CPORTP0 0x402F
#define ADAU1961_REG_R41_CPORTP1 0x4030
#define ADAU1961_REG_R42_JACKDETP 0x4031
#define ADAU1961_REG_R67_DJITTER 0x4036

