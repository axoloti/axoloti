-include $(deps)

IINCDIR = $(patsubst %,-I"%",$(ALLINCDIR))
LLIBDIR = $(patsubst %,-L%,$(DLIBDIR) $(ULIBDIR))

Makefile.uptodate: Makefile
	@>Makefile.uptodate
	@make -f $(VPATH)/Makefile clean

%.o : %.c Makefile.uptodate
	$(info compiling ${<})
	@"$(CC)" $(CPPFLAGS) $(CFLAGS) -c "$<" -o "$@"

%.o : %.cc Makefile.uptodate
	$(info compiling ${<})
	@"$(CXX)" $(CPPFLAGS) $(CXXFLAGS) -c "$<" -o "$@"

%.dbg.elf : $(objs)
	$(info linking $(@:.o=))
	@"$(LD)" $(LDFLAGS) -T"${LDSCRIPT}" $+ -Wl,-Map="$(@:.elf=.map)",--cref -o "$@"

%.elf : %.dbg.elf
	@"$(STRP)" -g --strip-unneeded -o "$@" "$<"
	@"$(SZ)" -A "$@"

%.lst : %.elf
	@"$(OD)" -hpxdSsrt "$<" > "$@"

%.read : %.elf
	@"$(TRGT)readelf" -atSln "$<" > "$@"

clean:
	$(info CLEAN)
	@rm -f *.o *.elf *.read *.lst *.map *.d
