#include "hal.h"
#include "dbg_stream.h"
#include "semihosting_stream.h"
#include "null_stream.h"

BaseSequentialStream * dbg_stream;

void dbg_stream_init(void) {
	if (CoreDebug->DHCSR & CoreDebug_DHCSR_C_DEBUGEN_Msk) {
		// Debugger is connected
		semihosting_stream_init();
		dbg_stream = &SHS;
	} else {
		dbg_stream = &null_stream;
	}
}
