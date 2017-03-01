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
#include "axoloti_control.h"
#include "usbcfg.h"

void TransmitLCDoverUSB(void) {

	  // TODO: update protocol
	  // there is no longer a header in the buffer, so a header needs to be prefixed, for client to recognise msg types
//  static int r = 0;
//  r++;
//  if (r == (LCDROWS + 1))
//    r = 0;
//
//  if (r < LCDROWS) {
//    chSequentialStreamWrite(
//        (BaseSequentialStream *)&BDU1,
//        (const unsigned char*)&lcd_buffer[r * (LCDHEADER + LCDWIDTH)],
//        LCDHEADER + LCDWIDTH);
//  }
//  else {
//    chSequentialStreamWrite((BaseSequentialStream *)&BDU1,
//                            (const unsigned char*)&led_buffer[0],
//                            LCDHEADER + LEDSIZE);
//  }

}
