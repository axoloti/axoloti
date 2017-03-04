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

/**
 *  Adapted from:
 ******************************************************************************
 * @file    stm32f429i_discovery_sdram.h
 * @author  MCD Application Team
 * @version V1.0.0
 * @date    20-September-2013
 * @brief   This file contains all the functions prototypes for the
 *          stm324x9i_disco_sdram.c driver.
 ******************************************************************************
 * @attention
 *
 * <h2><center>&copy; COPYRIGHT 2013 STMicroelectronics</center></h2>
 *
 * Licensed under MCD-ST Liberty SW License Agreement V2, (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *        http://www.st.com/software_license_agreement_liberty_v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************
 */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __STM32429I_DISCO_SDRAM_H
#define __STM32429I_DISCO_SDRAM_H

#ifdef __cplusplus
extern "C" {
#endif

#define STM32F427xx

  /* Includes ------------------------------------------------------------------*/
#include "stm32f4xx.h"

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
