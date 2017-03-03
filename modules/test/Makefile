# an overly simplified makefile, 
# we need to make this so developers just need to specify src and module, then the rest is hidden
# this implies using substution rules etc


MODULE = test
BUILDDIR = ${BDIR}/${MODULE}
MODSRC = ../modules/${MODULE}/test.cpp
MODOBJ = ${BUILDIR}/test.o 
MODINC = -I../modules/${MODULE}


MODLIB = lib${MODULE}.a

$(info building ${MODULE})

AWK=awk

${BDIR}/${MODLIB}: ../modules/${MODULE}/test.cpp
	@rm -rf {BUILDDIR}
	@mkdir -p ${BUILDDIR}
	@$(CPP) $(CCFLAGS) -H $(IINCDIR) ${MODINC} -Winvalid-pch -MD -MP --include ${BDIR}/xpatch.h -c ${MODSRC} -o ${BUILDDIR}/test.o
	@$(AR) rcs ${BDIR}/${MODLIB} ${BUILDDIR}/test.o
	@$(NM) -g ${BDIR}/${MODLIB} > ${BUILDDIR}/defines.sym
	@$(AWK) -v nspace="${MODULE}" -f ../firmware/createSym.awk < ${BUILDDIR}/defines.sym > ${BUILDDIR}/redefines.sym
	@$(CP) --redefine-syms ${BUILDDIR}/redefines.sym ${BDIR}/${MODLIB} ${BDIR}/${MODLIB}
