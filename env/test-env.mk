include path.mk
include arm-none-eabi.mk

CPPVER_REF = 7-2018-q2
CPPVER = $(shell "$(CPPC)" --version)

ifeq "$(CPPVER)" ""
$(info gcc not found at $(CPPC), not installed?)
$(info download and install GNU Arm Embedded Toolchain 7-2018-q2-update)
ifeq ($(OS),Windows_NT)
	$(info https://developer.arm.com/-/media/Files/downloads/gnu-rm/7-2018q2/gcc-arm-none-eabi-7-2018-q2-update-win32-sha2.exe)
else
	UNAME_S = $(shell /bin/uname -s 2>/dev/null)
	ifeq ($(UNAME_S),)
		UNAME_S = $(shell /usr/bin/uname -s 2>/dev/null)
	endif
	ifeq ($(UNAME_S),Linux)
		$(info via your package manager or from https://developer.arm.com/tools-and-software/open-source-software/developer-tools/gnu-toolchain/gnu-rm/downloads)
	endif
	ifeq ($(UNAME_S),Darwin)
		$(info // TODO: osx gcc-arm-none-eabi-7-2018-q2 installation instructions)
	endif
endif

$(error gcc not found)
endif

ifeq "$(findstring $(CPPVER_REF), $(CPPVER))" ""
$(info axoloti_runtime = ${axoloti_runtime})
$(info axoloti_home = ${axoloti_home})
$(info axoloti_api = ${axoloti_api})
$(info axoloti_env = ${axoloti_env})
$(info CPPCP = ${CPPC})
$(error unexpected compiler version: $(CPPVER), expected $(CPPVER_REF))
endif

all: hello

hello:
