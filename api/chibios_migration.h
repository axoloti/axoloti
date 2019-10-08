// Chibios 2.6.x to 18.x migration wrappers

#ifndef CHIBIOS_MIGRATION_H
#define CHIBIOS_MIGRATION_H

#define bool_t bool
#define Thread thread_t
#define PAL_STM32_PUDR_PULLUP PAL_MODE_INPUT_PULLUP
#define WORKING_AREA THD_WORKING_AREA
#define chSysLockFromIsr chSysLockFromISR
#define chSysUnlockFromIsr 	chSysUnlockFromISR
#define chTimeNow 	chVTGetSystemTime
#define chTimeElapsedSince 	chVTTimeElapsedSinceX
#define chTimeIsWithin 	chVTIsTimeWithinX
#define chThdSelf 	chThdGetSelfX
#define chThdGetPriority 	chThdGetPriorityX
#define chThdGetTicks 	chThdGetTicksX
#define chThdTerminated 	chThdTerminatedX
#define chThdShouldTerminate 	chThdShouldTerminateX

#define VirtualTimer virtual_timer_t
#define EventSource event_source_t
#define chEvtInit chEvtObjectInit
#define GenericQueue io_queue_t
#define chQGetLink qGetLink
#define chIQInit iqObjectInit
#define chOQInit oqObjectInit
#define CH_USE_REGISTRY CH_CFG_USE_REGISTRY
#define chRegGetThreadName chRegGetThreadNameX
#define THD_STATE_NAMES CH_STATE_NAMES
#define hal_lld_get_counter_value port_rt_get_counter_value

#define MS2ST TIME_MS2I
#endif
