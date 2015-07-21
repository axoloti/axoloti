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

#include "codec.h"

#include "axoloti_defines.h"

#if (BOARD_STM32F4DISCOVERY)
#include "codec_CS43L22.h"
#elif (BOARD_AXOLOTI_V05)
#include "codec_ADAU1961.h"
#elif (BOARD_AXOLOTI_V03)
#include "codec_ADAU1961.h"
#elif (BOARD_AXOLOTI_V01)
#include "codec_WM8731.h"
#endif

int32_t buf[BUFSIZE*2] __attribute__ ((section (".sram2")));
int32_t buf2[BUFSIZE*2] __attribute__ ((section (".sram2")));
int32_t rbuf[BUFSIZE*2] __attribute__ ((section (".sram2")));
int32_t rbuf2[BUFSIZE*2] __attribute__ ((section (".sram2")));

void codec_init(void) {
#if (BOARD_STM32F4DISCOVERY)
  codec_CS43L22_i2s_init_48k();
  codec_CS43L22_hw_init();
  codec_CS43L22_pwrCtl(1);
/*
  while(1){
//    chThdSleepMilliseconds(100);
    codec_CS43L22_sendBeep();
    chThdSleepMilliseconds(100);
  }
*/
#elif ((BOARD_AXOLOTI_V03)||(BOARD_AXOLOTI_V05))
  codec_ADAU1961_i2s_init(SAMPLERATE);
  codec_ADAU1961_hw_init(SAMPLERATE);
#else
#error "BOARD_ not defined"
#endif
}

void codecStop(void) {
#if (BOARD_AXOLOTI_V03)
  codec_ADAU1961_Stop();
#endif
}

void codec_clearbuffer(void) {
  int i;
  for(i=0;i<BUFSIZE*2;i++){
    buf[i]=0;
    buf2[i]=0;
  }
}

#if (BOARD_STM32F4DISCOVERY)
#include "codec_CS43L22.c"
#elif (BOARD_AXOLOTI_V03)
#include "codec_ADAU1961.c"
#elif (BOARD_AXOLOTI_V05)
#include "codec_ADAU1961_SAI.c"
#endif
