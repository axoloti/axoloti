null      :=
SPACE     := $(null) $(null)

ifeq ($(OS),Windows_NT)
	include $(subst $(SPACE),\ ,${axoloti_env}/platform_win/path.mk)
else
	UNAME_S = $(shell /bin/uname -s 2>/dev/null)
	ifeq ($(UNAME_S),)
		UNAME_S = $(shell /usr/bin/uname -s 2>/dev/null)
	endif
	ifeq ($(UNAME_S),Linux)
		include $(subst $(SPACE),\ ,${axoloti_env}/platform_linux/path.mk)
	endif
	ifeq ($(UNAME_S),Darwin)
		include $(subst $(SPACE),\ ,${axoloti_env}/platform_osx/path.mk)
	endif
endif

#$(info UNAME_S $(UNAME_S))
