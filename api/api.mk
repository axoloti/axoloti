
API_INCDIR = \
	$(axoloti_api) \
	$(axoloti_api)/stm32f4xx \
	$(axoloti_api)/fatfs \
	$(axoloti_api)/CMSIS/Core/Include \
	$(axoloti_api)/CMSIS/DSP/Include

API_LIBS = "$(axoloti_api)/CMSIS/DSP/Lib/GCC/libarm_cortexM4lf_math.a"

API_INCLUDE = $(axoloti_api)/xpatch.h

ALLINCDIR += $(API_INCDIR)
ALLINC += $(API_INCLUDE)
ALLLIBS += $(API_LIBS)
