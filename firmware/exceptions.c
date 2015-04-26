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
#include "exceptions.h"

__attribute__ ((naked))
void report_exception(void) {

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

typedef enum {
  fault = 0,
  watchdog_soft,
  watchdog_hard,
  brownout,
} faulttype;

typedef struct {
  uint32_t magicnumber;
  faulttype type;
  uint32_t r0;
  uint32_t r1;
  uint32_t r2;
  uint32_t r3;
  uint32_t r12;
  uint32_t lr;
  uint32_t pc;
  uint32_t psr;
  uint32_t ipsr;
  uint32_t cfsr;
  uint32_t hfsr;
  uint32_t mmfar;
  uint32_t bfar;
  uint32_t i;
} exceptiondump_t;

#define exceptiondump ((exceptiondump_t *)BKPSRAM_BASE)

void exception_init(void) {
  RCC->AHB1ENR |= RCC_AHB1ENR_BKPSRAMEN;
  RCC->APB1ENR |= RCC_APB1ENR_WWDGEN;
  chThdSleepMilliseconds(1);

  // disable watchdog when debugger active?
  DBGMCU->APB1FZ |= DBGMCU_APB1_FZ_DBG_WWDG_STOP;
  WWDG->CFR = WWDG_CFR_W | WWDG_CFR_WDGTB0 | WWDG_CFR_WDGTB1 | WWDG_CFR_EWI;
  if (!exception_check()) {
    if (RCC->CSR & RCC_CSR_WWDGRSTF) {
      // no exception found, but watchdog caused a reset?
      exceptiondump->magicnumber = ERROR_MAGIC_NUMBER;
      exceptiondump->type = watchdog_hard;
    }
    else if (RCC->CSR & RCC_CSR_BORRSTF) {
      exceptiondump->magicnumber = ERROR_MAGIC_NUMBER;
      exceptiondump->type = brownout;
    } else {
      exceptiondump->type = -1;
      exceptiondump->lr = 0;
      exceptiondump->pc = 0;
      exceptiondump->psr = 0;
      exceptiondump->ipsr = 0;
    }
    // clear reset flags
    RCC->CSR |= RCC_CSR_RMVF;
  }
  WWDG->SR = 0;
  WWDG->CR = 0x7F;
}

void watchdog_enable(void) {
  WWDG->CR = WWDG_CR_T | WWDG_CR_WDGA;
  WWDG->SR = 0;
  nvicEnableVector(WWDG_IRQn, CORTEX_PRIORITY_MASK(0));
}

void watchdog_feed(void) {
  if ((WWDG->CR & WWDG_CR_T) != WWDG_CR_T)
    WWDG->CR = WWDG_CR_T;
}

int exception_check(void) {
  if (exceptiondump->magicnumber == ERROR_MAGIC_NUMBER)
    return 1; // exception happened
  else
    return 0; // all fine
}

void exception_clear(void) {
  exceptiondump->magicnumber = 0;
}

void exception_checkandreport(void) {
  if (exception_check()) {
    bool report_registers = 0;
    if (exceptiondump->type == fault) {
      TransmitTextMessage("exception report:");
      report_registers = 1;
    }
    else if (exceptiondump->type == watchdog_soft) {
      TransmitTextMessage("exception: soft watchdog");
      report_registers = 1;
    }
    else if (exceptiondump->type == watchdog_hard) {
      TransmitTextMessage("exception: hard watchdog");

      TransmitTextMessageHeader();
      chprintf((BaseSequentialStream *)&SDU1, "i=0x%x%c", exceptiondump->i);
      chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);

    }
    else {
      TransmitTextMessage("unknown exception?");
    }

    if (report_registers) {
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

      TransmitTextMessageHeader();
      chprintf((BaseSequentialStream *)&SDU1, "cfsr=0x%x%c", exceptiondump->cfsr);
      chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);

      if (exceptiondump->cfsr & (1<<15)) {
        // BFARVALID
        TransmitTextMessageHeader();
        chprintf((BaseSequentialStream *)&SDU1, "bfar=0x%x%c", exceptiondump->bfar);
        chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);
      }

      if (exceptiondump->cfsr & (1<<7)) {
        // MMARVALID
        TransmitTextMessageHeader();
        chprintf((BaseSequentialStream *)&SDU1, "mmfar=0x%x%c", exceptiondump->mmfar);
        chSequentialStreamPut((BaseSequentialStream * )&SDU1, 0);
      }
    }
    exception_clear();
  }
}

void dbg_set_i(int i) {
  exceptiondump->i = i;
}

void terminator(void) {
#ifdef INFINITE_LOOP_ON_FAULTS
  for (;;)
  ;
#else
  // float usb inputs, hope the host notices detach...
  palSetPadMode(GPIOA, 11, PAL_MODE_INPUT);
  palSetPadMode(GPIOA, 12, PAL_MODE_INPUT);
  volatile int i = 20;
  while (i--) {
    volatile int j = 1 << 12;
    palTogglePad(LED1_PORT, LED1_PIN);
    while (j--) {
      volatile int k = 1 << 8;
      while (k--) {
      }
      watchdog_feed();
    }
  }

  NVIC_SystemReset();
#endif
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

  exceptiondump->magicnumber = ERROR_MAGIC_NUMBER;
  if (WWDG->SR & WWDG_SR_EWIF)
    exceptiondump->type = watchdog_soft;
  else
    exceptiondump->type = fault;
  exceptiondump->r0 = r0;
  exceptiondump->r1 = r1;
  exceptiondump->r2 = r2;
  exceptiondump->r3 = r3;
  exceptiondump->r12 = r12;
  exceptiondump->lr = lr;
  exceptiondump->pc = pc;
  exceptiondump->psr = psr;
  exceptiondump->ipsr = __get_IPSR();
  exceptiondump->cfsr = SCB->CFSR;
  exceptiondump->hfsr = SCB->HFSR;
  exceptiondump->mmfar = SCB->MMFAR;
  exceptiondump->bfar = SCB->BFAR;

  WWDG->CR = WWDG_CR_T;

  palClearPad(LED1_PORT, LED1_PIN);

  codec_clearbuffer();

  terminator();
}

void HardFaultVector(void) __attribute__((alias("report_exception")));
void MemManageVector(void) __attribute__((alias("report_exception")));
void BusFaultVector(void) __attribute__((alias("report_exception")));
void UsageFaultVector(void) __attribute__((alias("report_exception")));

__attribute__ ((naked))
CH_IRQ_HANDLER(WWDG_IRQHandler){
__asm volatile
(
    " tst lr, #4                                                \n"
    " ite eq                                                    \n"
    " mrseq r0, msp                                             \n"
    " mrsne r0, psp                                             \n"
    " ldr r1, [r0, #24]                                         \n"
    " ldr r2, handler2_address_const                            \n"
    " bx r2                                                     \n"
);
}
