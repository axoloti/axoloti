ENTRY(ResetHandler)

MEMORY
{
/*
 originally 
  24kB for program
  64kB for variables
 new:
  56kB for program
  64kB for variables 
 next:
  72kB for program!
*/

      SRAM (rwx) : ORIGIN = 0x20011000, LENGTH = 0x00009000 
      CCMSRAM : ORIGIN = 0x10000000, LENGTH = 0x0000E000 /* CCM - ccmramend */
}

__main_stack_size__     = 0x400;
__process_stack_size__  = 0x400;

SECTIONS {
    . = 0;
    _text = .;
    
      .text : ALIGN(16) SUBALIGN(16)
    {
        *(.text.ResetHandler)
        *(.text.startup.*)
        *(.text)
        *(.text.*)
        *(.rodata)
        *(.rodata.*)
        *(.glue_7t)
        *(.glue_7)
        *(.gcc*)
        *(.startup)
    } > SRAM
    
    constructors : ALIGN(4) SUBALIGN(4)
    {
        PROVIDE(__init_array_start = .);
        KEEP(*(SORT(.init_array.*)))
        KEEP(*(.init_array))
        PROVIDE(__init_array_end = .);
    } > SRAM

    destructors : ALIGN(4) SUBALIGN(4)
    {
        PROVIDE(__fini_array_start = .);
        KEEP(*(.fini_array))
        KEEP(*(SORT(.fini_array.*)))
        PROVIDE(__fini_array_end = .);
    } > SRAM
        
    .rodata : ALIGN(4) SUBALIGN(4) 
    {
        * (.rodata);
        * (.rodata.*);
    } > SRAM

    .startup : ALIGN(16) SUBALIGN(16)
    {
        KEEP(*(vectors))
    } > SRAM


    _etext = .;
    _textdata = _etext;

    .data : ALIGN(4) SUBALIGN(4)
    {
        . = ALIGN(4);
        PROVIDE(_data = .);
        *(.data)
        . = ALIGN(4);
        *(.data.*)
        . = ALIGN(4);
        *(.ramtext)
        . = ALIGN(4);
        PROVIDE(_edata = .);
    } > SRAM
    
    .stacks :
    {
        . = ALIGN(8);
        __main_stack_base__ = .;
        . += __main_stack_size__;
        . = ALIGN(8);
        __main_stack_end__ = .;
        __process_stack_base__ = .;
        __main_thread_stack_base__ = .;
        . += __process_stack_size__;
        . = ALIGN(8);
        __process_stack_end__ = .;
        __main_thread_stack_end__ = .;
    } > CCMSRAM

    .bss :
    {
        . = ALIGN(4);
        PROVIDE(_bss_start = .);
        *(.bss)
        . = ALIGN(4);
        *(.bss.*)
        . = ALIGN(4);
        *(COMMON)
        . = ALIGN(4);
        PROVIDE(_bss_end = .);
    } > SRAM    
 
}

PROVIDE(end = .);
_end            = .;