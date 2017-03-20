/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
 * Note: Use of ram1, ram2 and ram3 is mutually exclusive with use of ram0.
 * This memory is partitioned with gaps for the patch
 */
MEMORY
{
    flash : org = 0x08000000, len = 896k    /* last 128kB reserved for patch storage*/
    ram0  : org = 0x20000000, len = 192k    /* SRAM1 + SRAM2 + SRAM3 */
    ram1  : org = 0x20010000, len =  48k    /* first 64kB of SRAM1 for patch, next 48kB for firmware */
    ram2  : org = 0x2001E000, len =   8k    /* 2nd half of SRAM2 for fimware DMA */
    ram3  : org = 0x20024000, len =   0k    /* SRAM3 for patch */
    ram4  : org = 0x1000C000, len =  10k    /* CCM SRAM end*/
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
REGION_ALIAS("DATA_RAM", ram1);

/* RAM region to be used for BSS segment.*/
REGION_ALIAS("BSS_RAM", ram1);

/* RAM region to be used for the default heap.*/
REGION_ALIAS("HEAP_RAM", ram1);

INCLUDE rules.ld

SECTIONS {
    .flashend :
    {
        . = ALIGN(4);
        LONG(0x12345678)
        PROVIDE(_flash_end = .);
    } > flash
}
