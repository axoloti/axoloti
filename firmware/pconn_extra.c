#include "ch.h"
#include "logging.h"


void dbg_dump_threads() {
  thread_t *thd1 = chRegFirstThread();
  thread_t *thd = thd1;
  static const char *states[] = {CH_STATE_NAMES};
#if CH_DBG_FILL_THREADS
  LogTextMessage("Thread Name      Status     Prio  StkUnused");
#else
  LogTextMessage("Thread name Status");
#endif
  while(thd){
    const char *name = chRegGetThreadNameX(thd);
    if (!name) {
      name = "????";
    }
#if CH_DBG_FILL_THREADS
    char *stk = (char *)(thd->wabase);
    int nfree = 0;
    while(*stk == 0x55){
      nfree++;
      stk++;
    }
    LogTextMessage("%-16s %-9s %5d     %6d",name,states[thd->state],thd->prio, nfree);
#else
    LogTextMessage("%-16s %-16s",name,states[thd->state]);
#endif
    thd = chRegNextThread(thd);
    if (thd == thd1) break;
  }
}
