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

#ifndef __EXCEPTIONS_H
#define __EXCEPTIONS_H

void exception_init(void);
int exception_check(void);
void exception_clear(void);
void exception_checkandreport(void);
void watchdog_enable(void);
void exception_check_DFU(void);
void exception_initiate_dfu(void);
void watchdog_feed(void);

void report_fatfs_error(int errno, const char *fn);
void report_patchLoadFail(const char *fn);
void report_patchLoadSDRamOverflow(const char *fn, int amount);
void report_usbh_midi_ringbuffer_overflow(void);
#endif
