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


/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef SDRAM_H
#define SDRAM_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

#define SDRAM_BANK_ADDR     ((uint32_t)0xC0000000)

  /* Includes ------------------------------------------------------------------*/

extern void configSDRAM(void);

  void SDRAM_Init(void);
  void SDRAM_InitSequence(void);
  void SDRAM_WriteBuffer(uint32_t* pBuffer, uint32_t uwWriteAddress,
                         uint32_t uwBufferSize);
  void SDRAM_ReadBuffer(uint32_t* pBuffer, uint32_t uwReadAddress,
                        uint32_t uwBufferSize);

  void configSDRAM(void);

#ifdef __cplusplus
}
#endif

#endif
