# TODO: needs cleanup
# VPATH does not work with spaces

SPACE :=
SPACE +=

axoloti_api ?= ../../../api
axoloti_env ?= ../../../env
axoloti_runtime ?= ../../..
M ?= ./


#$(info axoloti_env = $(axoloti_env))
INCL = $(subst $(SPACE),\ ,${axoloti_env}/module.mk)
$(info INCL=$(INCL))
include $(INCL)

CCFLAGS += -ggdb3 \
	-mword-relocations \
	-mlong-calls \
	-nostdlib \
	-fno-common \
	-fno-exceptions \
	-fno-rtti \
	-fomit-frame-pointer \
	-fno-math-errno \
	-fno-threadsafe-statics \
	-fno-use-cxa-atexit \
	-Og

LDFLAGS1 = $(LDFLAGS) \
	-Wl,-Ur \
	-Wl,--gc-sections
#	-Wl,--undefined=tml_load_memory
#	-Wl,--require-defined=tml_load_memory

IINCDIR   = $(call qsb,$(patsubst %,-I%,$(ALLINCDIR)))
LLIBDIR   = $(call qsb,$(patsubst %,-L%,$(DLIBDIR) $(ULIBDIR)))
LDSCRIPT_ = $(call qsb,$(LDSCRIPT))

objs = test4.o
#deps := $(objs:.o=.d)


all: test4.dbg.elf test4.elf test4.read test4.lst \
	test4.dbg.elf test4.dbg.read test4.dbg.lst

#-include $(deps)

clean: test4.clean
	$(info CLEAN)

#VPATH = ..

.SECONDARY:

#.dir:
#	@$(MKDIR_P) ${M}

test4.o:
	$(info compiling ${<})
	@"$(CPPC)" $(CCFLAGS) $(DEFS) $(IINCDIR) -MD -MP -c "$(VPATH)/test4.cpp" -o "$@"

test4.dbg.elf : ${objs}
	$(info linking $(@:.o=))
	"$(LD)" $(LDFLAGS1) -Wl,--require-defined=test4_ctest4p -T${LDSCRIPT_} "$<" -Wl,-Map="$(@:.elf=.map)",--cref -o "$@"
	@#$(LD) $(LDFLAGS1) "$<" ${ALLLIBS} -T${LDSCRIPT_} -Wl,-Map="$(@:.elf=.map)",--cref -o "$@"

%.elf : %.dbg.elf
	@"$(STRP)" -g --strip-unneeded -o "$@" "$<"
	@"$(SZ)" -A "$@"

%.lst : %.elf
	@"$(OD)" -hpxdSsrt "$<" > "$@"

%.read : %.elf
	@"$(TRGT)readelf" -atSln "$<" > "$@"

%.clean :
	@rm -f $*.o
	@rm -f $*.elf
	@rm -f $*.read
	@rm -f $*.lst
	@rm -f $*.dbg.elf
	@rm -f $*.dbg.read
	@rm -f $*.dbg.lst
	@rm -f $*.dbg.map
	@rm -f $*.d
	@rm -f $*.map
