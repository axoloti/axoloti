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
 * @file    stm32f429i_discovery_sdram.c
 * @author  MCD Application Team
 * @version V1.0.1
 * @date    28-October-2013
 * @brief   This file provides a set of functions needed to drive the
 * IS42S16400J SDRAM memory mounted on STM32F429I-DISCO Kit.
 ******************************************************************************
 * @attention
 *
 * <h2><center>&copy; COPYRIGHT 2013 STMicroelectronics</center></h2>
 *
 * Licensed under MCD-ST Liberty SW License Agreement V2, (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.st.com/software_license_agreement_liberty_v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************
 */

#include "sdram.h"
#include "stm32f4xx_fmc.h"
#include "ch.h"
#include "hal.h"
#include "axoloti_board.h"
#include "sysmon.h"
/**
 * @brief  Configures the FMC and GPIOs to interface with the SDRAM memory.
 *         This function must be called before any read/write operation
 *         on the SDRAM.
 * @param  None
 * @retval None
 */
void SDRAM_Init(void) {
  FMC_SDRAMInitTypeDef FMC_SDRAMInitStructure;
  FMC_SDRAMTimingInitTypeDef FMC_SDRAMTimingInitStructure;

  /* Enable FMC clock */
  rccEnableAHB3(RCC_AHB3ENR_FMCEN, FALSE);

  /* FMC Configuration ---------------------------------------------------------*/
  /* FMC SDRAM Bank configuration */
  /* Timing configuration for 84 Mhz of SD clock frequency (168Mhz/2) */
  /* TMRD: 2 Clock cycles */
  FMC_SDRAMTimingInitStructure.FMC_LoadToActiveDelay = 2;
  /* TXSR: min=70ns (6x11.90ns) */
  FMC_SDRAMTimingInitStructure.FMC_ExitSelfRefreshDelay = 7;
  /* TRAS: min=42ns (4x11.90ns) max=120k (ns) */
  FMC_SDRAMTimingInitStructure.FMC_SelfRefreshTime = 4;
  /* TRC:  min=63 (6x11.90ns) */
  FMC_SDRAMTimingInitStructure.FMC_RowCycleDelay = 7;
  /* TWR:  2 Clock cycles */
  FMC_SDRAMTimingInitStructure.FMC_WriteRecoveryTime = 2;
  /* TRP:  15ns => 2x11.90ns */
  FMC_SDRAMTimingInitStructure.FMC_RPDelay = 2;
  /* TRCD: 15ns => 2x11.90ns */
  FMC_SDRAMTimingInitStructure.FMC_RCDDelay = 2;

  /* FMC SDRAM control configuration */
  FMC_SDRAMInitStructure.FMC_Bank = FMC_Bank1_SDRAM;
  /* Row addressing: [7:0] */
  FMC_SDRAMInitStructure.FMC_ColumnBitsNumber = FMC_ColumnBits_Number_8b;
  /* Column addressing: [11:0] */
  FMC_SDRAMInitStructure.FMC_RowBitsNumber = FMC_RowBits_Number_12b;
  FMC_SDRAMInitStructure.FMC_SDMemoryDataWidth = SDRAM_MEMORY_WIDTH;
  FMC_SDRAMInitStructure.FMC_InternalBankNumber = FMC_InternalBank_Number_4;
  FMC_SDRAMInitStructure.FMC_CASLatency = SDRAM_CAS_LATENCY;
  FMC_SDRAMInitStructure.FMC_WriteProtection = FMC_Write_Protection_Disable;
  FMC_SDRAMInitStructure.FMC_SDClockPeriod = SDCLOCK_PERIOD;
  FMC_SDRAMInitStructure.FMC_ReadBurst = SDRAM_READBURST;
  FMC_SDRAMInitStructure.FMC_ReadPipeDelay = FMC_ReadPipe_Delay_1;
  FMC_SDRAMInitStructure.FMC_SDRAMTimingStruct = &FMC_SDRAMTimingInitStructure;

  /* FMC SDRAM bank initialization */
  FMC_SDRAMInit(&FMC_SDRAMInitStructure);

  /* FMC SDRAM device initialization sequence */
  SDRAM_InitSequence();

}

void configSDRAM(void) {
  SDRAM_Init();

#if 0
  int qsource[16];
  int qdest[16];

  int i;
  for (i = 0; i < 16; i++) {
    qsource[i] = i;
  }
//  a small test...
  SDRAM_WriteBuffer(&qsource[0], 0, 16);
  for (i = 0; i < 16; i++) {
    qdest[i] = 0;
  }
  SDRAM_ReadBuffer(&qdest[0], 0, 16);
#endif
}

void memTest(void) {
  int memSize = 8 * 1024 * 1024; // 8MB
  void *base;
  base = (void *)0xC0000000;
  int i;
  // 4MB test
  const uint32_t a = 22695477;
  const uint32_t c = 1;
  //write
  volatile int iter = 0;
  volatile int niter = 16;
  volatile int niter2 = 16;
  // linear write with linear congruential generator values
  // 362 ms execution cycle at 8MB : 22MB/s read+write+compute
  for (iter = 0; iter < niter; iter++) {
    uint32_t x = iter;
    // write
    for (i = 0; i < memSize / 4; i++) {
      x = (a * x) + c;
      //
      ((volatile uint32_t *)base)[i] = x;
    }
    // read/verify
    x = iter;
    for (i = 0; i < memSize / 4; i++) {
      x = (a * x) + c;
      if (((volatile uint32_t *)base)[i] != x) {
        setErrorFlag(ERROR_SDRAM);
        while (1) {
          chThdSleepMilliseconds(100);
        }
      }
    }
  }
  // scattered byte write at linear congruential generator addresses
  // 300 ms execution time for one iteration: 3.3M scattered read+write per second
  // equals 68
  for (iter = 0; iter < niter2; iter++) {
    uint32_t x = iter;
    // write
    for (i = 0; i < 1024 * 1024; i++) {
      x = (a * x) + c;
      ((volatile uint8_t *)base)[x & (memSize - 1)] = (uint8_t)i;
    }
    // read/verify
    x = iter;
    for (i = 0; i < 1024 * 1024; i++) {
      x = (a * x) + c;
      if (((volatile uint8_t *)base)[x & (memSize - 1)] != (uint8_t)i) {
        setErrorFlag(ERROR_SDRAM);
        while (1) {
          chThdSleepMilliseconds(100);
        }
      }
    }
  }
}

/**
 * @brief  Executes the SDRAM memory initialization sequence.
 * @param  None.
 * @retval None.
 */
void SDRAM_InitSequence(void) {
  FMC_SDRAMCommandTypeDef FMC_SDRAMCommandStructure;
  uint32_t tmpr = 0;

  /* Step 3 --------------------------------------------------------------------*/
  /* Configure a clock configuration enable command */
  FMC_SDRAMCommandStructure.FMC_CommandMode = FMC_Command_Mode_CLK_Enabled;
  FMC_SDRAMCommandStructure.FMC_CommandTarget = FMC_Command_Target_bank1;
  FMC_SDRAMCommandStructure.FMC_AutoRefreshNumber = 1;
  FMC_SDRAMCommandStructure.FMC_ModeRegisterDefinition = 0;
  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }
  /* Send the command */
  FMC_SDRAMCmdConfig(&FMC_SDRAMCommandStructure);

  //In the ST example, this is 100ms, but the 429 RM says 100us is typical, and
  //the ISSI datasheet confirms this. 1ms seems plenty, and is much shorter than
  //refresh interval, meaning we won't risk losing contents if the SDRAM is in self-refresh
  //mode
  /* Step 4 --------------------------------------------------------------------*/
  /* Insert 1 ms delay */
  chThdSleepMilliseconds(1);

  /* Step 5 --------------------------------------------------------------------*/
  /* Configure a PALL (precharge all) command */
  FMC_SDRAMCommandStructure.FMC_CommandMode = FMC_Command_Mode_PALL;
  FMC_SDRAMCommandStructure.FMC_CommandTarget = FMC_Command_Target_bank1;
  FMC_SDRAMCommandStructure.FMC_AutoRefreshNumber = 1;
  FMC_SDRAMCommandStructure.FMC_ModeRegisterDefinition = 0;
  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }
  /* Send the command */
  FMC_SDRAMCmdConfig(&FMC_SDRAMCommandStructure);

  /* Step 6 --------------------------------------------------------------------*/
  /* Configure a Auto-Refresh command */
  FMC_SDRAMCommandStructure.FMC_CommandMode = FMC_Command_Mode_AutoRefresh;
  FMC_SDRAMCommandStructure.FMC_CommandTarget = FMC_Command_Target_bank1;
  FMC_SDRAMCommandStructure.FMC_AutoRefreshNumber = 4;
  FMC_SDRAMCommandStructure.FMC_ModeRegisterDefinition = 0;
  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }
  /* Send the  first command */
  FMC_SDRAMCmdConfig(&FMC_SDRAMCommandStructure);

  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }
  /* Send the second command */
  FMC_SDRAMCmdConfig(&FMC_SDRAMCommandStructure);

  /* Step 7 --------------------------------------------------------------------*/
  /* Program the external memory mode register */
  tmpr = (uint32_t)SDRAM_MODEREG_BURST_LENGTH_2 |
  SDRAM_MODEREG_BURST_TYPE_SEQUENTIAL |
  SDRAM_MODEREG_CAS_LATENCY_2 |
  SDRAM_MODEREG_OPERATING_MODE_STANDARD |
  SDRAM_MODEREG_WRITEBURST_MODE_SINGLE;

  /* Configure a load Mode register command*/
  FMC_SDRAMCommandStructure.FMC_CommandMode = FMC_Command_Mode_LoadMode;
  FMC_SDRAMCommandStructure.FMC_CommandTarget = FMC_Command_Target_bank1;
  FMC_SDRAMCommandStructure.FMC_AutoRefreshNumber = 1;
  FMC_SDRAMCommandStructure.FMC_ModeRegisterDefinition = tmpr;
  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }
  /* Send the command */
  FMC_SDRAMCmdConfig(&FMC_SDRAMCommandStructure);

  /* Step 8 --------------------------------------------------------------------*/

  /* Set the refresh rate counter */
  /* (7.81 us x Freq) - 20 */
  /* Set the device refresh counter */
  FMC_SetRefreshCount(683);
  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }

  FMC_SDRAMWriteProtectionConfig(FMC_Bank1_SDRAM, DISABLE);
}

/**
 * @brief  Writes a Entire-word buffer to the SDRAM memory.
 * @param  pBuffer: pointer to buffer.
 * @param  uwWriteAddress: SDRAM memory internal address from which the data will be
 *         written.
 * @param  uwBufferSize: number of words to write.
 * @retval None.
 */
void SDRAM_WriteBuffer(uint32_t* pBuffer, uint32_t uwWriteAddress,
                       uint32_t uwBufferSize) {
  __IO uint32_t
  write_pointer = (uint32_t)uwWriteAddress;

  /* Disable write protection */
  FMC_SDRAMWriteProtectionConfig(FMC_Bank1_SDRAM, DISABLE);

  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }

  /* While there is data to write */
  for (; uwBufferSize != 0; uwBufferSize--) {
    /* Transfer data to the memory */
    *(uint32_t *)(SDRAM_BANK_ADDR + write_pointer) = *pBuffer++;

    /* Increment the address*/
    write_pointer += 4;
  }

}

/**
 * @brief  Reads data buffer from the SDRAM memory.
 * @param  pBuffer: pointer to buffer.
 * @param  ReadAddress: SDRAM memory internal address from which the data will be
 *         read.
 * @param  uwBufferSize: number of words to write.
 * @retval None.
 */
void SDRAM_ReadBuffer(uint32_t* pBuffer, uint32_t uwReadAddress,
                      uint32_t uwBufferSize) {
  __IO uint32_t
  write_pointer = (uint32_t)uwReadAddress;

  /* Wait until the SDRAM controller is ready */
  while (FMC_GetFlagStatus(FMC_Bank1_SDRAM, FMC_FLAG_Busy) != RESET) {
  }

  /* Read data */
  for (; uwBufferSize != 0x00; uwBufferSize--) {
    *pBuffer++ = *(__IO uint32_t *)(SDRAM_BANK_ADDR + write_pointer );

    /* Increment the address*/
    write_pointer += 4;
  }
}

