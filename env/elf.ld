
FORCE_GROUP_ALLOCATION

INCLUDE axoloti_api_version.ld

SECTIONS
{
	.text 0 :
	{
		*(.text)
		*(.text.*)
	}

	.rodata 0 :
	{
		*(.rodata*)
		*(.rodata1)
		*(.rodata.*)
	}

	.data 0 :
	{
		*(.data)
		*(.data1)
		*(.data.*)
	}

	.bss 0 (NOLOAD) :
	{
		*(.bss)
		*(.bss.*)
		*(.sbss)
		*(.sbss.*)
		*(COMMON)    /* in BSS? needed? */
	}

	.sdram_bss 0 (NOLOAD) :
	{
		*(.sdram) /* legacy unspecified sdram */
		*(.sdram.*) /* legacy unspecified sdram */
		*(.sdram_bss)
		*(.sdram_bss.*)
	}

	.sdram_data 0 :
	{
		*(.sdram_data)
		*(.sdram_data.*)
	}

	.init 0 :
	{
		KEEP (*(.init));
	}

	.fini 0 :
	{
		KEEP (*(.fini));
	}

	.init_array 0 :
	{
		PROVIDE (__init_array_start = .); 
		KEEP (*(SORT(.init_array.*)))
		KEEP (*(.init_array))
		PROVIDE (__init_array_end = .); 
	}

	.fini_array 0 :
	{
		PROVIDE (__fini_array_start = .); 
		KEEP (*(.fini_array))
		KEEP (*(SORT(.fini_array.*)))
		PROVIDE (__fini_array_end = .); 
	}
}

