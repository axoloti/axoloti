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

#include "sdram.h"
#include "ch.h"
#include "hal.h"
#include "axoloti_board.h"
#include "sysmon.h"

#include "hal_fsmc_sdram.h"


/* SDRAM bank base address.*/
#define SDRAM_BANK_ADDR     ((uint32_t)0xC0000000)

/*
 *  FMC SDRAM Mode definition register defines
 */
#define FMC_SDCMR_MRD_BURST_LENGTH_1             ((uint16_t)0x0000)
#define FMC_SDCMR_MRD_BURST_LENGTH_2             ((uint16_t)0x0001)
#define FMC_SDCMR_MRD_BURST_LENGTH_4             ((uint16_t)0x0002)
#define FMC_SDCMR_MRD_BURST_LENGTH_8             ((uint16_t)0x0004)
#define FMC_SDCMR_MRD_BURST_TYPE_SEQUENTIAL      ((uint16_t)0x0000)
#define FMC_SDCMR_MRD_BURST_TYPE_INTERLEAVED     ((uint16_t)0x0008)
#define FMC_SDCMR_MRD_CAS_LATENCY_2              ((uint16_t)0x0020)
#define FMC_SDCMR_MRD_CAS_LATENCY_3              ((uint16_t)0x0030)
#define FMC_SDCMR_MRD_OPERATING_MODE_STANDARD    ((uint16_t)0x0000)
#define FMC_SDCMR_MRD_WRITEBURST_MODE_PROGRAMMED ((uint16_t)0x0000)
#define FMC_SDCMR_MRD_WRITEBURST_MODE_SINGLE     ((uint16_t)0x0200)

/*
 * FMC_ReadPipe_Delay
 */
#define FMC_ReadPipe_Delay_0               ((uint32_t)0x00000000)
#define FMC_ReadPipe_Delay_1               ((uint32_t)0x00002000)
#define FMC_ReadPipe_Delay_2               ((uint32_t)0x00004000)
#define FMC_ReadPipe_Delay_Mask            ((uint32_t)0x00006000)

/*
 * FMC_Read_Burst
 */
#define FMC_Read_Burst_Disable             ((uint32_t)0x00000000)
#define FMC_Read_Burst_Enable              ((uint32_t)0x00001000)
#define FMC_Read_Burst_Mask                ((uint32_t)0x00001000)

/*
 * FMC_SDClock_Period
 */
#define FMC_SDClock_Disable                ((uint32_t)0x00000000)
#define FMC_SDClock_Period_2               ((uint32_t)0x00000800)
#define FMC_SDClock_Period_3               ((uint32_t)0x00000C00)
#define FMC_SDClock_Period_Mask            ((uint32_t)0x00000C00)

/*
 * FMC_ColumnBits_Number
 */
#define FMC_ColumnBits_Number_8b           ((uint32_t)0x00000000)
#define FMC_ColumnBits_Number_9b           ((uint32_t)0x00000001)
#define FMC_ColumnBits_Number_10b          ((uint32_t)0x00000002)
#define FMC_ColumnBits_Number_11b          ((uint32_t)0x00000003)

/*
 * FMC_RowBits_Number
 */
#define FMC_RowBits_Number_11b             ((uint32_t)0x00000000)
#define FMC_RowBits_Number_12b             ((uint32_t)0x00000004)
#define FMC_RowBits_Number_13b             ((uint32_t)0x00000008)

/*
 * FMC_SDMemory_Data_Width
 */
#define FMC_SDMemory_Width_8b                ((uint32_t)0x00000000)
#define FMC_SDMemory_Width_16b               ((uint32_t)0x00000010)
#define FMC_SDMemory_Width_32b               ((uint32_t)0x00000020)

/*
 * FMC_InternalBank_Number
 */
#define FMC_InternalBank_Number_2          ((uint32_t)0x00000000)
#define FMC_InternalBank_Number_4          ((uint32_t)0x00000040)

/*
 * FMC_CAS_Latency
 */
#define FMC_CAS_Latency_1                  ((uint32_t)0x00000080)
#define FMC_CAS_Latency_2                  ((uint32_t)0x00000100)
#define FMC_CAS_Latency_3                  ((uint32_t)0x00000180)

/*
 * FMC_Write_Protection
 */
#define FMC_Write_Protection_Disable       ((uint32_t)0x00000000)
#define FMC_Write_Protection_Enable        ((uint32_t)0x00000200)


/*
 * SDRAM driver configuration structure.
 */
static const SDRAMConfig sdram_cfg = {
  .sdcr = (uint32_t)(FMC_ColumnBits_Number_8b |
                     FMC_RowBits_Number_12b |
                     FMC_SDMemory_Width_16b |
                     FMC_InternalBank_Number_4 |
                     FMC_CAS_Latency_2 |
                     FMC_Write_Protection_Disable |
                     FMC_SDClock_Period_2 |
                     FMC_Read_Burst_Enable |
                     FMC_ReadPipe_Delay_1),

  .sdtr = (uint32_t)((2   - 1) |  // FMC_LoadToActiveDelay = 2 (TMRD: 2 Clock cycles)
                     (7 <<  4) |  // FMC_ExitSelfRefreshDelay = 7 (TXSR: min=70ns (7x11.11ns))
                     (4 <<  8) |  // FMC_SelfRefreshTime = 4 (TRAS: min=42ns (4x11.11ns) max=120k (ns))
                     (7 << 12) |  // FMC_RowCycleDelay = 7 (TRC:  min=70 (7x11.11ns))
                     (2 << 16) |  // FMC_WriteRecoveryTime = 2 (TWR:  min=1+ 7ns (1+1x11.11ns))
                     (2 << 20) |  // FMC_RPDelay = 2 (TRP:  20ns => 2x11.11ns)
                     (2 << 24)),  // FMC_RCDDelay = 2 (TRCD: 20ns => 2x11.11ns)

  .sdcmr = (uint32_t)(((4 - 1) << 5) |
                      ((FMC_SDCMR_MRD_BURST_LENGTH_2 |
                        FMC_SDCMR_MRD_BURST_TYPE_SEQUENTIAL |
                        FMC_SDCMR_MRD_CAS_LATENCY_2 |
                        FMC_SDCMR_MRD_OPERATING_MODE_STANDARD |
                        FMC_SDCMR_MRD_WRITEBURST_MODE_SINGLE) << 9)),

  /* if (STM32_SYSCLK == 180000000) ->
     64ms / 4096 = 15.625us
     15.625us * 90MHz = 1406 - 20 = 1386 */
  //.sdrtr = (1386 << 1),
  .sdrtr = (uint32_t)(683 << 1),
};


/**
 * @brief  Configures the FMC and GPIOs to interface with the SDRAM memory.
 *         This function must be called before any read/write operation
 *         on the SDRAM.
 * @param  None
 * @retval None
 */
void SDRAM_Init(void) {

	  /*
	   * Initialise FSMC for SDRAM.
	   */
	  fsmcSdramInit();
	  fsmcSdramStart(&SDRAMD, &sdram_cfg);
}

void configSDRAM(void) {
  SDRAM_Init();
//  memTest();
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

