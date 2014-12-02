arm-none-eabi-objcopy -I binary -O elf32-little -B arm --rename-section .data=.samples,alloc,load,readonly,contents 808bd.raw 808bd.o
arm-none-eabi-objcopy -I binary -O elf32-little -B arm --rename-section .data=.samples,alloc,load,readonly,contents 808hatclose.raw 808hatclose.o
arm-none-eabi-objcopy -I binary -O elf32-little -B arm --rename-section .data=.samples,alloc,load,readonly,contents 808hatopen.raw 808hatopen.o
arm-none-eabi-objcopy -I binary -O elf32-little -B arm --rename-section .data=.samples,alloc,load,readonly,contents 808hitom.raw 808hitom.o
arm-none-eabi-objcopy -I binary -O elf32-little -B arm --rename-section .data=.samples,alloc,load,readonly,contents 808lotom.raw 808lotom.o
arm-none-eabi-objcopy -I binary -O elf32-little -B arm --rename-section .data=.samples,alloc,load,readonly,contents 808midtom.raw 808midtom.o
arm-none-eabi-objcopy -I binary -O elf32-little -B arm --rename-section .data=.samples,alloc,load,readonly,contents 808snare.raw 808snare.o
