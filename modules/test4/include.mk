#$(info include.mk)

$(info   MODULE_SRC_DIR=$(MODULE_SRC_DIR))
$(info   MODULE=$(MODULE))

MODULE_INC=$(call sq,$(MODULE_SRC_DIR)/$(MODULE).h)
$(info   MODULE_INC $(MODULE_INC))

ALLINC+= $(MODULE_INC)
MODULE_BDIR = $(call sq,${axoloti_home}/build/$(MODULE))
# TODO: ensure MODULE_BDIR exists...

MODULEDEP := $(MODULE).elf
MODULEDEPS += $(MODULEDEP)

MODULEFILEDEPS += \
	$(call sq,$(MODULE_BDIR)/test4.elf) \
	$(call sq,/lib/test4.elf)

#$(info MODULEDEP $(MODULEDEP))

# TODO: needs cleanup
# VPATH does not work with spaces
VP2=$(subst $(SPACE),\ ,$(MODULE_SRC_DIR))
$(info VP2=$(VP2))

$(MODULE).elf : #$(MODULE_SRC_DIR)/*
	make -f "$(MODULE_SRC_DIR)/Makefile" -C "$(call qs,$(MODULE_BDIR))" VPATH=$(VP2) all
