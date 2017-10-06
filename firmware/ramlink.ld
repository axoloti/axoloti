ENTRY(xpatch_init)

MEMORY
{

      SRAM (rwx)  : ORIGIN = 0x00000000, LENGTH = 64k  /* first 64kB of SRAM1 */
      SRAM2 (rwx) : ORIGIN = 0x2001C000, LENGTH =  8k  /* first 8kB in SRAM2 */
	  SRAM3 (rwx) : ORIGIN = 0x20020000, LENGTH = 64k  /* whole of SRAM3 */
      CCMSRAM (rw) : ORIGIN = 0x10000000, LENGTH = 0x0000C000 /* CCM - ccmramend */
      SDRAM : org = 0xC0000000, len = 0x800000
}

SECTIONS {

	header : {
		LONG(0x32787861) /* signature "axx2"  */
		LONG(patch_root_chunk)
	} > SRAM

    .text : ALIGN(16) SUBALIGN(16)
    {
        * (.boot);
        * (.text);
        etext = .;
    } > SRAM
    
    constructors : ALIGN(4) SUBALIGN(4)
    {
        PROVIDE(__ctor_array_start = .);
        KEEP(*(SORT(.init_array.*)))
        KEEP(*(.init_array))
        PROVIDE(__ctor_array_end = .);
    } > SRAM

    destructors : ALIGN(4) SUBALIGN(4)
    {
        PROVIDE(__dtor_array_start = .);
        KEEP(*(.fini_array))
        KEEP(*(SORT(.fini_array.*)))
        PROVIDE(__dtor_array_end = .);
    } > SRAM

    .rodata : ALIGN(4) SUBALIGN(4) 
    {
        * (.rodata);
        * (.rodata.*);
    } > SRAM3

    .data : ALIGN(4) SUBALIGN(4)
    {
        sdata = .;
        * (.data);
        edata = .;
    } > SRAM3

    .sram2 (NOLOAD): ALIGN(4) SUBALIGN(4) 
    {
        * (.sram2);
        * (.sram2.*);
    } > SRAM2
   
    .bss (NOLOAD): ALIGN(4) SUBALIGN(4)
    {
        PROVIDE(_pbss_start = .);        
        sbss = .;
        * (.bss);
        ebss = .;
        PROVIDE(_pbss_end = .);        
    } > CCMSRAM
    
    .sdram.data : ALIGN(4) SUBALIGN(4)
    {
        KEEP(*(.sdramdata))
        KEEP(*(.sdramdata.*))
    } > SDRAM

    .sdram (NOLOAD) : ALIGN(4) SUBALIGN(4)
    {
        KEEP(*(.sdram))
        KEEP(*(.sdram.*))
        PROVIDE(_sdram_dyn_start = .);
        PROVIDE(_sdram_dyn_end = ORIGIN(SDRAM) + LENGTH(SDRAM));
    } > SDRAM


 /*   
    .ARM.exidx : 
    {
       __exidx_start = .;
       *(.ARM.exidx* .gnu.linkonce.armexidx.*)
       __exidx_end = .;
    } >CCMSRAM
    _etext = .;
*/
}
