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

#include "ch.h"
#include "hal.h"
#include "chprintf.h"
#include "ui.h"
#include "axoloti_control.h"
#include "axoloti_board.h"
#if 0
#include "sdcard.h"
#include "ff.h"
#endif
#include "crc32.h"
#include "sdram.h"
#include "exceptions.h"
#include "fourcc.h"

#define AXOLOTICONTROL FALSE
#define SERIALDEBUG TRUE

#if SERIALDEBUG
#define DBGPRINTCHAR(x) sdPut(&SD2,x);   //chThdSleepMilliseconds(1);

void dbgPrintHexDigit(uint8_t b) {
  if (b > 9) {
    DBGPRINTCHAR('a' + b - 10);
  }
  else {
    DBGPRINTCHAR('0' + b);
  }
}

#define DBGPRINTHEX(x) \
    DBGPRINTCHAR(' '); \
    DBGPRINTCHAR('0'); \
    DBGPRINTCHAR('x'); \
    dbgPrintHexDigit((x>>28)&0x0f); \
    dbgPrintHexDigit((x>>24)&0x0f); \
    dbgPrintHexDigit((x>>20)&0x0f); \
    dbgPrintHexDigit((x>>16)&0x0f); \
    dbgPrintHexDigit((x>>12)&0x0f); \
    dbgPrintHexDigit((x>>8)&0x0f); \
    dbgPrintHexDigit((x>>4)&0x0f); \
    dbgPrintHexDigit((x)&0x0f); \
    DBGPRINTCHAR('\r'); \
    DBGPRINTCHAR('\n');
#else 
#define DBGPRINTCHAR(x)
#define DBGPRINTHEX(x)
#endif

#define FLASH_BASE_ADDR 0x08000000


void refresh_LCD(void) {
#if AXOLOTICONTROL
  int i;
  for(i=0;i<9;i++) {
    do_axoloti_control();
    chThdSleepMilliseconds(20);
  }
#endif
}

void DispayAbortErr(int err) {
  DBGPRINTCHAR('0' + err);
#if AXOLOTICONTROL
  LCD_drawStringN(0, 5, "error code:", 128);
  LCD_drawNumber3D(0, 6, (int)err);
  refresh_LCD();
#endif
// blink red slowly, green off
  palWritePad(LED1_PORT, LED1_PIN, 0);
  int i = 10;
  while (i--) {
    palWritePad(LED2_PORT, LED2_PIN, 1);
    chThdSleepMilliseconds(1000);
    palWritePad(LED2_PORT, LED2_PIN, 0);
    chThdSleepMilliseconds(1000);
  }
  NVIC_SystemReset();
}

void setErrorFlag(int error){
	(void)error;
	while(1){
	}
}



void prvGetRegistersFromStack( uint32_t *pulFaultStackAddress )
{
/* These are volatile to try and prevent the compiler/linker optimising them
away as the variables never actually get used.  If the debugger won't show the
values of the variables, make them global my moving their declaration outside
of this function. */
volatile uint32_t r0;
volatile uint32_t r1;
volatile uint32_t r2;
volatile uint32_t r3;
volatile uint32_t r12;
volatile uint32_t lr; /* Link register. */
volatile uint32_t pc; /* Program counter. */
volatile uint32_t psr;/* Program status register. */

    r0 = pulFaultStackAddress[ 0 ];
    r1 = pulFaultStackAddress[ 1 ];
    r2 = pulFaultStackAddress[ 2 ];
    r3 = pulFaultStackAddress[ 3 ];

    r12 = pulFaultStackAddress[ 4 ];
    lr = pulFaultStackAddress[ 5 ];
    pc = pulFaultStackAddress[ 6 ];
    psr = pulFaultStackAddress[ 7 ];

    /* When the following line is hit, the variables contain the register values. */
   asm("BKPT 255");
   for( ;; );
}

void HardFault_Handler(void) {
    __asm volatile
    (
        " tst lr, #4                                                \n"
        " ite eq                                                    \n"
        " mrseq r0, msp                                             \n"
        " mrsne r0, psp                                             \n"
        " ldr r1, [r0, #24]                                         \n"
        " ldr r2, handler2_address_const                            \n"
        " bx r2                                                     \n"
        " handler2_address_const: .word prvGetRegistersFromStack    \n"
    );
}
void MemManage_Handler(void) {
	  asm("BKPT 255");
}
void BusFault_Handler(void) {
	  asm("BKPT 255");
}
void UsageFault_Handler(void) {
	  asm("BKPT 255");
}


void watchdog_feed(void) {
#if WATCHDOG_ENABLED
  if ((WWDG->CR & WWDG_CR_T) != WWDG_CR_T)
    WWDG->CR = WWDG_CR_T;
#endif
}

int flash_WaitForLastOperation(void) {
  while (FLASH->SR == FLASH_SR_BSY) {
    WWDG->CR = WWDG_CR_T;
  }
  return FLASH->SR;
}

void flash_Erase_sector1(int sector) {
  // assume VDD>2.7V
  FLASH->CR &= ~FLASH_CR_PSIZE;
  FLASH->CR |= FLASH_CR_PSIZE_1;
  FLASH->CR &= ~FLASH_CR_SNB;
  FLASH->CR |= FLASH_CR_SER | (sector << 3);
  FLASH->CR |= FLASH_CR_STRT;
  flash_WaitForLastOperation();

  FLASH->CR &= (~FLASH_CR_SER);
  FLASH->CR &= ~FLASH_CR_SER;
  flash_WaitForLastOperation();
}

int flash_Erase_sector(int sector) {
  // interrupts would cause flash execution, stall
  // and cause watchdog trigger
  chSysLock();
  flash_Erase_sector1(sector);
  chSysUnlock();
  return 0;
}

int flash_ProgramWord(uint32_t Address, uint32_t Data) {
  int status;

  flash_WaitForLastOperation();

  /* if the previous operation is completed, proceed to program the new data */
  FLASH->CR &= ~FLASH_CR_PSIZE;
  FLASH->CR |= FLASH_CR_PSIZE_1;
  FLASH->CR |= FLASH_CR_PG;

  *(__IO uint32_t*)Address = Data;

  /* Wait for last operation to be completed */
  status = flash_WaitForLastOperation();

  /* if the program operation is completed, disable the PG Bit */
  FLASH->CR &= (~FLASH_CR_PG);

  watchdog_feed();

  /* Return the Program Status */
  return status;
}

void flash_unlock(void) {
  // unlock sequence
  FLASH->KEYR = 0x45670123;
  FLASH->KEYR = 0xCDEF89AB;
}


int main(void) {
  watchdog_feed();
  halInit();

  // float usb inputs, hope the host notices detach...
  palSetPadMode(GPIOA, 11, PAL_MODE_INPUT); palSetPadMode(GPIOA, 12, PAL_MODE_INPUT);
  // setup LEDs
  palSetPadMode(LED1_PORT,LED1_PIN,PAL_MODE_OUTPUT_PUSHPULL);
  palSetPad(LED1_PORT,LED1_PIN);
#ifdef LED2_PIN
  palSetPadMode(LED2_PORT,LED2_PIN,PAL_MODE_OUTPUT_PUSHPULL);
#endif

#if (CH_DBG_SYSTEM_STATE_CHECK == TRUE)
  // avoid trapping into _dbg_check_enable
  ch.dbg.isr_cnt = 0;
  ch.dbg.lock_cnt = 0;
#endif
  chSysInit();
  watchdog_feed();

#ifdef SERIALDEBUG
// SD2 for serial debug output
  palSetPadMode(GPIOA, 3, PAL_MODE_ALTERNATE(7) | PAL_MODE_INPUT); // RX
  palSetPadMode(GPIOA, 2, PAL_MODE_OUTPUT_PUSHPULL); // TX
  palSetPadMode(GPIOA, 2, PAL_MODE_ALTERNATE(7)); // TX
// 115200 baud
  static const SerialConfig sd2Cfg = {115200, 0, 0, 0};
  sdStart(&SD2, &sd2Cfg);
#endif

  DBGPRINTCHAR('a');

  watchdog_feed();

  uint32_t *sdram32 = (uint32_t *)SDRAM_BANK_ADDR;
  uint8_t *sdram8 = (uint8_t *)SDRAM_BANK_ADDR;

  if ((sdram8[0] != 'f') || (sdram8[1] != 'l') || (sdram8[2] != 'a')
      || (sdram8[3] != 's') || (sdram8[4] != 'c') || (sdram8[5] != 'o')
      || (sdram8[6] != 'p') || (sdram8[7] != 'y')) {
	  if (CoreDebug->DHCSR&CoreDebug_DHCSR_C_DEBUGEN_Msk) {
		  // debugger connected, software breakpoint
		  asm("BKPT 255");
	  }
    DispayAbortErr(1);
  }
  DBGPRINTCHAR('b');

  uint32_t flength = sdram32[2];
  uint32_t fcrc = sdram32[3];

  if (flength > 1 * 1024 * 1024) {
	  if (CoreDebug->DHCSR&CoreDebug_DHCSR_C_DEBUGEN_Msk) {
		  // debugger connected, software breakpoint
		  asm("BKPT 255");
	  }
    DispayAbortErr(2);
  }

  DBGPRINTCHAR('c');

  DBGPRINTHEX(flength);

  uint32_t ccrc = CalcCRC32((uint8_t *)(SDRAM_BANK_ADDR + 0x010), flength);

  DBGPRINTCHAR('d');

  DBGPRINTHEX(ccrc);
  DBGPRINTHEX(fcrc);

  if (ccrc != fcrc) {
	  if (CoreDebug->DHCSR&CoreDebug_DHCSR_C_DEBUGEN_Msk) {
		  // debugger connected, software breakpoint
		  asm("BKPT 255");
	  }
    DispayAbortErr(3);
  }

  DBGPRINTCHAR('e');

  // unlock sequence
  FLASH->KEYR = 0x45670123;
  FLASH->KEYR = 0xCDEF89AB;

  uint32_t i;

  for (i = 0; i < 12; i++) {
    flash_Erase_sector(i);
//    LCD_drawStringN(0, 3, "Erased sector", 128);
//    LCD_drawNumber3D(80, 3, i);
//    refresh_LCD();
    palWritePad(LED2_PORT,LED2_PIN,1);
    chThdSleepMilliseconds(100);
    palWritePad(LED2_PORT,LED2_PIN,0);
    DBGPRINTCHAR('f');
    DBGPRINTHEX(i);
  }

  DBGPRINTCHAR('g');

  DBGPRINTHEX(flength);

  ccrc = CalcCRC32((uint8_t *)(SDRAM_BANK_ADDR + 0x010), flength);

  DBGPRINTCHAR('h');

  DBGPRINTHEX(ccrc);
  DBGPRINTHEX(fcrc);

  if (ccrc != fcrc) {
	  if (CoreDebug->DHCSR&CoreDebug_DHCSR_C_DEBUGEN_Msk) {
		  // debugger connected, software breakpoint
		  asm("BKPT 255");
	  }
    DispayAbortErr(4);
  }

  DBGPRINTCHAR('i');

  int destptr = FLASH_BASE_ADDR; // flash base adress
  uint32_t *srcptr = (uint32_t *)(SDRAM_BANK_ADDR + 0x010); // sdram base adress + header offset

  for (i = 0; i < (flength + 3) / 4; i++) {
    uint32_t d = *srcptr;
    flash_ProgramWord(destptr, d);
    if ((FLASH->SR != 0) && (FLASH->SR != 1)) {
      DBGPRINTCHAR('z');
      DBGPRINTHEX(FLASH->SR);
    }
//    DBGPRINTHEX(f);
    if ((i & 0xFFF) == 0) {
      palWritePad(LED2_PORT,LED2_PIN,1);
      chThdSleepMilliseconds(100); palWritePad(LED2_PORT,LED2_PIN,0);
      DBGPRINTCHAR('j');
      DBGPRINTHEX(destptr);
      DBGPRINTHEX(*(srcptr));
    }
    destptr += 4;
    srcptr++;
  }

  DBGPRINTCHAR('k');

  ccrc = CalcCRC32((uint8_t *)(FLASH_BASE_ADDR), flength);

  DBGPRINTCHAR('l');

  DBGPRINTHEX(ccrc);
  DBGPRINTHEX(fcrc);

  if (ccrc != fcrc) {
	  if (CoreDebug->DHCSR&CoreDebug_DHCSR_C_DEBUGEN_Msk) {
		  // debugger connected, software breakpoint
		  asm("BKPT 255");
	  }
    DispayAbortErr(5);
  }

  DBGPRINTCHAR('\r');
  DBGPRINTCHAR('\n');
  DBGPRINTCHAR('S');
  DBGPRINTCHAR('U');
  DBGPRINTCHAR('C');
  DBGPRINTCHAR('C');
  DBGPRINTCHAR('E');
  DBGPRINTCHAR('S');
  DBGPRINTCHAR('S');
  DBGPRINTCHAR('\r');
  DBGPRINTCHAR('\n');

  chThdSleepMilliseconds(1000);
  NVIC_SystemReset();
  return 0;
}

extern void Reset_Handler(void);

void patch_init(int32_t fwID) {
	(void)fwID;
	Reset_Handler();
}

typedef void (*fptr_patch_init_t)(int32_t fwID);

// stripped function signatures, won't get there anyway...
typedef void (*fptr_patch_dispose_t)(void);
typedef void (*fptr_patch_dsp_process_t)(void);
typedef void (*fptr_patch_midi_in_handler_t)(void);
typedef void (*fptr_patch_applyPreset_t)(void);

#define fourcc_patch_meta FOURCC('P','T','C','H')
typedef struct {
	chunk_header_t header;
	int32_t patchID;
	char patchname[64];
} chunk_patch_meta_t;

#define fourcc_patch_functions FOURCC('P','F','U','N')
typedef struct {
	chunk_header_t header;
	fptr_patch_init_t fptr_patch_init;
	fptr_patch_dispose_t fptr_patch_dispose;
	fptr_patch_dsp_process_t fptr_dsp_process;
	fptr_patch_midi_in_handler_t fptr_MidiInHandler;
	fptr_patch_applyPreset_t fptr_applyPreset;
} chunk_patch_functions_t;

#define fourcc_patch_root FOURCC('A','X','P','T')
typedef struct {
	chunk_header_t header;
	chunk_patch_meta_t patch_meta;
	chunk_patch_functions_t patch_functions;
} chunk_patch_root_t;

chunk_patch_root_t patch_root_chunk = {
		header : CHUNK_HEADER(patch_root),
		patch_meta : {
			header : CHUNK_HEADER(patch_meta),
			patchID : 0,
			patchname : {'f','l','a','s','h','e','r'}
		},
		patch_functions : {
			header : CHUNK_HEADER(patch_functions),
					fptr_patch_init: patch_init,
					fptr_patch_dispose: 0,
					fptr_dsp_process: 0,
					fptr_MidiInHandler: 0,
					fptr_applyPreset: 0,
		},
};
