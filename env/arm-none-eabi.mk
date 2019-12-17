##############################################################################
# Compiler settings
#

TRGT = $(GCCBINPATH)arm-none-eabi-
CC   = $(TRGT)gcc
CXX  = $(TRGT)g++
# Enable loading with g++ only if you need C++ runtime support.
# NOTE: You can use C++ even without C++ support if you are careful. C++
#       runtime support makes code size explode.
LD   = $(TRGT)gcc
#LD   = $(TRGT)g++
CP   = $(TRGT)objcopy
AS   = $(TRGT)gcc -x assembler-with-cpp
AR   = $(TRGT)ar
OD   = $(TRGT)objdump
SZ   = $(TRGT)size
STRP = $(TRGT)strip
HEX  = $(CP) -O ihex
BIN  = $(CP) -O binary

# or try clang???
# CPPC = clang -v -target arm-none-eabi

#
# Compiler settings
##############################################################################
