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

#include "ch.h"
#include "hal.h"
#include "codec.h"
#include "chprintf.h"
#include "pconnection.h"
#include "axoloti_board.h"

__attribute__ ((naked))
void unhandled_exception(void) {

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

#define ERROR_MAGIC_NUMBER 0xE1212012

typedef struct {
  uint32_t magicnumber;
  uint32_t r0;
  uint32_t r1;
  uint32_t r2;
  uint32_t r3;
  uint32_t r12;
  uint32_t lr;
  uint32_t pc;
  uint32_t psr;
} exceptiondump_t;

volatile exceptiondump_t *exceptiondump = BKPSRAM_BASE;

void exception_init(void){
  RCC->AHB1ENR |= RCC_AHB1ENR_BKPSRAMEN;
}

int exception_check(void) {
  if (*(volatile uint32_t *)BKPSRAM_BASE == ERROR_MAGIC_NUMBER)
    return 1; // exception happened
  else
    return 0; // all fine
}

void exception_clear(void) {
  *(volatile uint32_t *)BKPSRAM_BASE = 0;
}

void exception_checkandreport(void) {
  if (exception_check()) {
    TransmitTextMessage("exception report:");

    TransmitTextMessageHeader();
    chprintf((BaseSequentialStream *)&SDU1, "pc=0x%x%c", exceptiondump->pc);
    chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);

    TransmitTextMessageHeader();
    chprintf((BaseSequentialStream *)&SDU1, "psr=0x%x%c", exceptiondump->psr);
    chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);

    TransmitTextMessageHeader();
    chprintf((BaseSequentialStream *)&SDU1, "lr=0x%x%c", exceptiondump->lr);
    chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);

    TransmitTextMessageHeader();
    chprintf((BaseSequentialStream *)&SDU1, "r12=0x%x%c", exceptiondump->r12);
    chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);

    exception_clear();
  }
}

void prvGetRegistersFromStack(uint32_t *pulFaultStackAddress) {
  volatile uint32_t r0;
  volatile uint32_t r1;
  volatile uint32_t r2;
  volatile uint32_t r3;
  volatile uint32_t r12;
  volatile uint32_t lr; /* Link register. */
  volatile uint32_t pc; /* Program counter. */
  volatile uint32_t psr;/* Program status register. */

  r0 = pulFaultStackAddress[0];
  r1 = pulFaultStackAddress[1];
  r2 = pulFaultStackAddress[2];
  r3 = pulFaultStackAddress[3];

  r12 = pulFaultStackAddress[4];
  lr = pulFaultStackAddress[5];
  pc = pulFaultStackAddress[6];
  psr = pulFaultStackAddress[7];

  RCC->AHB1ENR |= RCC_AHB1ENR_BKPSRAMEN;

  volatile uint32_t *bkp;
  bkp = (volatile uint32_t *)BKPSRAM_BASE;
  *bkp++ = ERROR_MAGIC_NUMBER; // "error"
  exceptiondump->r0 = r0;
  exceptiondump->r1 = r1;
  exceptiondump->r2 = r2;
  exceptiondump->r3 = r3;
  exceptiondump->r12 = r12;
  exceptiondump->lr = lr;
  exceptiondump->pc = pc;
  exceptiondump->psr = psr;

  codec_clearbuffer();

#ifdef INFINITE_LOOP_ON_FAULTS
  for (;;)
  ;
#else
  // float usb inputs, hope the host notices detach...
  palSetPadMode(GPIOA, 11, PAL_MODE_INPUT);
  palSetPadMode(GPIOA, 12, PAL_MODE_INPUT);
  volatile int i = 20;
  while (i--) {
    volatile int j = 1 << 22;
    palTogglePad(LED1_PORT, LED1_PIN);
    while (j--) {
    }
  }

  NVIC_SystemReset();
#endif
}

void HardFaultVector(void) __attribute__((alias("unhandled_exception")));
void MemManageVector(void) __attribute__((alias("unhandled_exception")));
void BusFaultVector(void) __attribute__((alias("unhandled_exception")));
void UsageFaultVector(void) __attribute__((alias("unhandled_exception")));

