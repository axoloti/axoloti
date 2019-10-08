include path.mk
include arm-none-eabi.mk

CPPVER_REF = 7-2018-q2
CPPVER = $(shell "$(CPPC)" --version)
ifeq "$(findstring $(CPPVER_REF), $(CPPVER))" ""
$(info axoloti_runtime = ${axoloti_runtime})
$(info axoloti_home = ${axoloti_home})
$(info axoloti_api = ${axoloti_api})
$(info axoloti_env = ${axoloti_env})
$(error unexpected compiler version: $(CPPVER), expected $(CPPVER_REF))
endif

all: hello

hello:
