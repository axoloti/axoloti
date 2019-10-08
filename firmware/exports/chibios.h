#ifndef EXPORTS_CHIBIOS_H
#define EXPORTS_CHIBIOS_H

//#include "api/chibios.h"

#define EXPORTS_CHIBIOS_RT_SYMBOLS \
    SYM(chThdSleep), \
    SYM(chThdExit), \
    SYM(chThdTerminate), \
    SYM(chThdCreateStatic), \
    SYM(chThdWait), \
    SYM(chEvtSignal), \
    SYM(ch), \
    SYM(chEvtWaitAnyTimeout), \
    SYM(_dbg_check_disable), \
    SYM(_dbg_check_enable)

#endif
