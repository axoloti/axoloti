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
 
 MEMORY
{
    flash0  : org = 0x08000000, len = 512k    /* last 512kB reserved for patch storage*/
    flash1  : org = 0x00000000, len = 0
    flash2  : org = 0x00000000, len = 0
    flash3  : org = 0x00000000, len = 0
    flash4  : org = 0x00000000, len = 0
    flash5  : org = 0x00000000, len = 0
    flash6  : org = 0x00000000, len = 0
    flash7  : org = 0x00000000, len = 0
    ram0    : org = 0x20000000, len =  60k    /* first 60kB of SRAM1 for patch */
    ram1    : org = 0x2000F000, len =  52k    /* next 52kB of SRAM1 for firmware */
    ram2    : org = 0x2001C000, len =   8k    /* 1st half of SRAM2 for patch DMA */
    ram3    : org = 0x2001E000, len =   8k    /* 2nd half of SRAM2 for fimware DMA */
    ram4    : org = 0x20020000, len =  64k    /* SRAM3 for patch */
    ram5    : org = 0x10000000, len =  48k    /* CCM SRAM for patch */
    ram6    : org = 0x1000C000, len =  16k    /* CCM SRAM for firmware */
    ram7    : org = 0x40024000, len =   4k    /* BCKP SRAM */
}


/* For each data/text section two region are defined, a virtual region
   and a load region (_LMA suffix).*/

/* Flash region to be used for exception vectors.*/
REGION_ALIAS("VECTORS_FLASH", flash0);
REGION_ALIAS("VECTORS_FLASH_LMA", flash0);

/* Flash region to be used for constructors and destructors.*/
REGION_ALIAS("XTORS_FLASH", flash0);
REGION_ALIAS("XTORS_FLASH_LMA", flash0);

/* Flash region to be used for code text.*/
REGION_ALIAS("TEXT_FLASH", flash0);
REGION_ALIAS("TEXT_FLASH_LMA", flash0);

/* Flash region to be used for read only data.*/
REGION_ALIAS("RODATA_FLASH", flash0);
REGION_ALIAS("RODATA_FLASH_LMA", flash0);

/* Flash region to be used for various.*/
REGION_ALIAS("VARIOUS_FLASH", flash0);
REGION_ALIAS("VARIOUS_FLASH_LMA", flash0);

/* Flash region to be used for RAM(n) initialization data.*/
REGION_ALIAS("RAM_INIT_FLASH_LMA", flash0);

/* RAM region to be used for Main stack. This stack accommodates the processing
   of all exceptions and interrupts.*/
REGION_ALIAS("MAIN_STACK_RAM", ram6);

/* RAM region to be used for the process stack. This is the stack used by
   the main() function.*/
REGION_ALIAS("PROCESS_STACK_RAM", ram6);

/* RAM region to be used for data segment.*/
REGION_ALIAS("DATA_RAM", ram1);
REGION_ALIAS("DATA_RAM_LMA", flash0);

/* RAM region to be used for BSS segment.*/
REGION_ALIAS("BSS_RAM", ram1);

/* RAM region to be used for the default heap.*/
REGION_ALIAS("HEAP_RAM", ram1);

/* Generic rules inclusion.*/
INCLUDE rules.ld

SECTIONS {
    .flashend :
    {
        . = ALIGN(4);
        LONG(0x12345678)
        PROVIDE(_flash_end = .);
    } > flash0
}
