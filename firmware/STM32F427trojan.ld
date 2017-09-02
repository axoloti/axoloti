/**
 * Copyright (C) 2013 - 2017 Johannes Taelman
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

/*
 * The "trojan" linker pretends to be a regular patch binary but escapes into
 * an independent RTOS environment.
 * 
 */

/*
 * Note: Use of ram1, ram2 and ram3 is mutually exclusive with use of ram0.
 */
MEMORY
{
/* make SRAM1 pretending to be flash memory */
    flash : org = 0x00000000, LENGTH = 0x0000B000
    ram0  : org = 0x20000000, len = 192k    /* SRAM1 + SRAM2 + SRAM3 */
    ram1  : org = 0x20000000, len = 0 
    ram2  : org = 0x2001E000, len = 8k      /* 2nd half of SRAM2 */
    ram3  : org = 0x20020000, len = 64k     /* SRAM3 */
    ram4  : org = 0x10000000, LENGTH = 0x0000C000 /* CCM - ccmramend */
    ram5  : org = 0x40024000, len = 4k      /* BCKP SRAM */
    ram6  : org = 0x00000000, len = 0
    ram7  : org = 0x00000000, len = 0
}

/* RAM region to be used for Main stack. This stack accommodates the processing
   of all exceptions and interrupts*/
REGION_ALIAS("MAIN_STACK_RAM", ram4);

/* RAM region to be used for the process stack. This is the stack used by
   the main() function.*/
REGION_ALIAS("PROCESS_STACK_RAM", ram4);

/* RAM region to be used for data segment.*/
REGION_ALIAS("DATA_RAM", ram3);

/* RAM region to be used for BSS segment.*/
REGION_ALIAS("BSS_RAM", ram3);

/* RAM region to be used for the default heap.*/
REGION_ALIAS("HEAP_RAM", ram3);


SECTIONS
{
    .entry_section : 
    {
		LONG(0x32787861) /* signature "axx2"  */
		LONG(patch_root_chunk)
        *(.entry_section)
        *(.entry_section.*)
        LONG(0)
        LONG(0x12345678)
        . = 0x0200;
    } > flash
}

INCLUDE rules.ld

