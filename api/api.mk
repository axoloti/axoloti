
API_INCDIR = \
	$(axoloti_api_) \
	$(axoloti_api_)/stm32f4xx \
	$(axoloti_api_)/fatfs \
	$(axoloti_api_)/CMSIS/Core/Include \
	$(axoloti_api_)/CMSIS/DSP/Include

API_LIBS = "$(axoloti_api)/CMSIS/DSP/Lib/GCC/libarm_cortexM4lf_math.a"

API_INCLUDE = $(axoloti_api_)/xpatch.h

ALLINCDIR += $(API_INCDIR)
ALLINC += $(API_INCLUDE)
ALLLIBS += $(API_LIBS)
