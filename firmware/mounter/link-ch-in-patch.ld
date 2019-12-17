
ENTRY(patchInit)


SECTIONS
{
  .text : ALIGN(1024)
  {
    *(.vectors)
    *(.text)
    *(.text.*)
    end = 0;
  }

  .rodata :
  {
    *(.rodata)
    *(.rodata.*)
  }

  .data :
  {
    *(.data)
    *(.data.*)
  }

  .bss :
  {
    *(.bss)
    *(.bss.*)

    /* exceptions stack.*/
    . = ALIGN(8);
    __main_stack_base__ = .;
    . += __main_stack_size__;
    . = ALIGN(8);
    __main_stack_end__ = .;

    /* process stack.*/
    . = ALIGN(8);
    __process_stack_base__ = .;
    __main_thread_stack_base__ = .;
    . += __process_stack_size__;
    . = ALIGN(8);
    __process_stack_end__ = .;
    __main_thread_stack_end__ = .;
  }
}
