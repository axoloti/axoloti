include path.mk
include arm-none-eabi.mk

CPPVER_REF = 7-2018-q2
CPPVER = $(shell "$(CXX)" --version)

ifeq "$(CPPVER)" ""
$(info error: compiler $(CXX) not found in path=$(PATH), not installed?)
$(info Please download and install GNU Arm Embedded Toolchain 7-2018-q2-update)
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
$(info from https://armkeil.blob.core.windows.net/developer/Files/downloads/gnu-rm/7-2018q2/gcc-arm-none-eabi-7-2018-q2-update-mac.tar.bz2)
$(info and extract to /Applications/)
endif
endif
$(error Compiler not found)
endif

ifeq "$(findstring $(CPPVER_REF), $(CPPVER))" ""
$(info axoloti_runtime = ${axoloti_runtime})
$(info axoloti_home = ${axoloti_home})
$(info axoloti_api = ${axoloti_api})
$(info axoloti_env = ${axoloti_env})
$(info CPPCP = ${CPPC})
$(info PATH=${PATH})
$(error Unexpected compiler version: $(CPPVER), expected $(CPPVER_REF))
endif

MAKE_VER_REF = GNU Make 4.2
MAKE_VER = $(shell make --version)

ifeq "$(MAKE_VER)" ""
$(info "make" not found in path=$(PATH), not installed?)
endif
ifeq "$(findstring $(MAKE_VER_REF), $(MAKE_VER))" ""
$(info PATH=${PATH})
$(info Unexpected Make version, expected ${MAKE_VER_REF}, but found: ${MAKE_VER})
endif

# test for spaces
space :=
space +=
ifneq (,$(findstring $(space),$(axoloti_api)))
$(info There is space character in the path of axoloti_api=$(axoloti_api))
$(info Please fix!)
endif
ifneq (,$(findstring $(space),$(axoloti_home)))
$(info There is space character in the path of axoloti_home=$(axoloti_home))
$(info Please fix!)
endif
ifneq (,$(findstring $(space),$(axoloti_env)))
$(info There is space character in the path of axoloti_env=$(axoloti_env))
$(info Please fix!)
endif

.SILENT:

all: hello

hello:
	@
