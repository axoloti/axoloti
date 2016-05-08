/**
  ******************************************************************************
  * @file    USBH_conf.h
  * @author  MCD Application Team
  * @version V3.1.0
  * @date    19-June-2014
  * @brief   General low level driver configuration
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2014 STMicroelectronics</center></h2>
  *
  * Licensed under MCD-ST Liberty SW License Agreement V2, (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/software_license_agreement_liberty_v2
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
  */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __USBH_CONF__H__
#define __USBH_CONF__H__

#define STM32F40_41xxx

#include "stm32f4xx.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "ch.h"
#include "chprintf.h"


/* Includes ------------------------------------------------------------------*/

/** @addtogroup USBH_OTG_DRIVER
  * @{
  */

/** @defgroup USBH_CONF
  * @brief usb otg low level driver configuration file
  * @{
  */

/** @defgroup USBH_CONF_Exported_Defines
  * @{
  */

#define USBH_MAX_NUM_ENDPOINTS                6
#define USBH_MAX_NUM_INTERFACES               6
#define USBH_MAX_NUM_CONFIGURATION            1
#define USBH_KEEP_CFG_DESCRIPTOR              1
#define USBH_MAX_NUM_SUPPORTED_CLASS          3
#define USBH_MAX_SIZE_CONFIGURATION           0x200
#define USBH_MAX_DATA_BUFFER                  0x200
#define USBH_DEBUG_LEVEL                      2
#define USBH_USE_OS                           1

/** @defgroup USBH_Exported_Macros
  * @{
  */

 /* Memory management macros */
#define USBH_malloc               fakemalloc
#define USBH_free                 fakefree
#define USBH_memset               memset
#define USBH_memcpy               memcpy

extern void* fakemalloc(size_t size);
extern void fakefree(void * p);

#define osThreadId Thread *

#define osThreadDef(name, fn, prio, instances, stacksz) \
  static WORKING_AREA(wa##name, 640); \
  Thread *name = chThdCreateStatic(wa##name, sizeof(wa##name), HIGHPRIO, fn, phost); \
  phost->os_event = name;
#define osThreadCreate(x,y) x
#define osThread(x) x

#if 0
#define osMessageQId InputQueue *
//#define osMessagePut(q,val,time) chSysLockFromIsr(); chIQPutI (q,val); chSysUnlockFromIsr();
#define osMessagePut(q,val,time) chIQPutI (q,val);
#define osMessageGet(q,to) \
   (osEvent)chIQGetTimeout(q, TIME_INFINITE)

#else
#define osMessageQId Thread *
#define osMessagePutI(q,val,time) chEvtSignalI (q,1<<val);
#define osMessagePut(q,val,time) chEvtSignal (q,1<<val);
#define osMessageGet(q,to) chEvtWaitOneTimeout(0xFF, MS2ST(to))
#endif

// osThreadId
#define osMessageQDef(name, queue_sz, type) \
  static type buf[queue_sz]; \
  INPUTQUEUE_DECL(name, &buf, sizeof(buf), NULL, NULL)
#define osMessageCreate(queue_def, thread_id)  &queue_def
#define osMessageQ(x) x
#define osWaitForever TIME_INFINITE
#define osEventMessage 1
typedef uint8_t osEvent;

//#define DEBUG_ON_GPIO

 /* DEBUG macros */


#if (USBH_DEBUG_LEVEL > 0)
extern void LogTextMessage(const char* format, ...);
#define  USBH_UsrLog(...)   LogTextMessage(__VA_ARGS__);
#else
#define USBH_UsrLog(...)
#endif


#if (USBH_DEBUG_LEVEL > 1)

#define  USBH_ErrLog(...)   LogTextMessage(__VA_ARGS__);
#else
#define USBH_ErrLog(...)
#endif


#if (USBH_DEBUG_LEVEL > 2)
#define  USBH_DbgLog(...)   LogTextMessage(__VA_ARGS__);
#else
#define USBH_DbgLog(...)
#endif

/**
  * @}
  */

/**
  * @}
  */


/** @defgroup USBH_CONF_Exported_Types
  * @{
  */
/**
  * @}
  */


/** @defgroup USBH_CONF_Exported_Macros
  * @{
  */
/**
  * @}
  */

/** @defgroup USBH_CONF_Exported_Variables
  * @{
  */
/**
  * @}
  */

/** @defgroup USBH_CONF_Exported_FunctionsPrototype
  * @{
  */
/**
  * @}
  */


/* The following functions are moved to the header to allow inlining
 *
 */

#include "usbh_def.h"
//#include "stm32f427xx.h"
//#include "stm32f4xx.h"
//#include "stm32f4xx_hal_hcd.h"
//#include "stm32f4xx_ll_usb.h"

/**
 * @brief  USBH_LL_GetURBState
 *         Get a URB state from the low level driver.
 * @param  phost: Host handle
 * @param  pipe: Pipe index
 *         This parameter can be a value from 1 to 15
 * @retval URB state
 *          This parameter can be one of the these values:
 *            @arg URB_IDLE
 *            @arg URB_DONE
 *            @arg URB_NOTREADY
 *            @arg URB_NYET
 *            @arg URB_ERROR
 *            @arg URB_STALL
 */

static USBH_URBStateTypeDef USBH_LL_GetURBState(USBH_HandleTypeDef *phost,
                                        uint8_t pipe) {
  return (USBH_URBStateTypeDef)HAL_HCD_HC_GetURBState(phost->pData, pipe);
}


/**
 * @brief  USBH_LL_SubmitURB
 *         Submit a new URB to the low level driver.
 * @param  phost: Host handle
 * @param  pipe: Pipe index
 *         This parameter can be a value from 1 to 15
 * @param  direction : Channel number
 *          This parameter can be one of the these values:
 *           0 : Output
 *           1 : Input
 * @param  ep_type : Endpoint Type
 *          This parameter can be one of the these values:
 *            @arg EP_TYPE_CTRL: Control type
 *            @arg EP_TYPE_ISOC: Isochrounous type
 *            @arg EP_TYPE_BULK: Bulk type
 *            @arg EP_TYPE_INTR: Interrupt type
 * @param  token : Endpoint Type
 *          This parameter can be one of the these values:
 *            @arg 0: PID_SETUP
 *            @arg 1: PID_DATA
 * @param  pbuff : pointer to URB data
 * @param  length : Length of URB data
 * @param  do_ping : activate do ping protocol (for high speed only)
 *          This parameter can be one of the these values:
 *           0 : do ping inactive
 *           1 : do ping active
 * @retval Status
 */

static USBH_StatusTypeDef USBH_LL_SubmitURB(USBH_HandleTypeDef *phost, uint8_t pipe,
                                        uint8_t direction, uint8_t ep_type,
                                        uint8_t token, uint8_t* pbuff,
                                        uint16_t length, uint8_t do_ping) {
     HAL_HCD_HC_SubmitRequest(phost->pData, pipe, direction, ep_type, token, pbuff,
                              length, do_ping);
     return USBH_OK;
}

#endif //__USBH_CONF__H__


/**
  * @}
  */

/**
  * @}
  */
/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/

