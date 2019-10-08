#include "chversion.h"

// verify chibios version

#define EXPECTED_CH_VERSION_YEAR         19
#define EXPECTED_CH_VERSION_MONTH        1
#define EXPECTED_CH_VERSION_PATCH        3
#define XSTR(x) STR(x)
#define STR(x) #x

#if ((EXPECTED_CH_VERSION_YEAR!=CH_VERSION_YEAR) \
	|| (EXPECTED_CH_VERSION_MONTH!=CH_VERSION_MONTH) \
	|| (EXPECTED_CH_VERSION_PATCH!=CH_VERSION_PATCH))
#pragma message("Expected chibios version: " \
		XSTR(EXPECTED_CH_VERSION_YEAR) "." \
		XSTR(EXPECTED_CH_VERSION_MONTH) "." \
		XSTR(EXPECTED_CH_VERSION_PATCH) " " \
		"but found chibios version: " \
		XSTR(CH_VERSION_YEAR) "." \
		XSTR(CH_VERSION_MONTH) "." \
		XSTR(CH_VERSION_PATCH))
#error "chibios version mismatch"
#endif
