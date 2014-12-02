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

#include "stm32f4xx_v2.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ch.h"
#include "chprintf.h"
//#include "hal.h"
extern BaseSequentialStream SD2;

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

#define USBH_MAX_NUM_ENDPOINTS                2
#define USBH_MAX_NUM_INTERFACES               2
#define USBH_MAX_NUM_CONFIGURATION            1
#define USBH_KEEP_CFG_DESCRIPTOR              1
#define USBH_MAX_NUM_SUPPORTED_CLASS          1
#define USBH_MAX_SIZE_CONFIGURATION           0x200
#define USBH_MAX_DATA_BUFFER                  0x200
#define USBH_DEBUG_LEVEL                      3
#define USBH_USE_OS                           0

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

#define osThreadId (Thread *)
#define osMessageQId (InputQueue *)

extern void ToggleGreen(void);
extern void ToggleOrange(void);
extern void ToggleRed(void);
extern void ToggleBlue(void);


 /* DEBUG macros */


#if (USBH_DEBUG_LEVEL > 0)
#define  USBH_UsrLog(...)   chprintf(&SD2,__VA_ARGS__);\
                            chprintf(&SD2,"\n");
#else
#define USBH_UsrLog(...)
#endif


#if (USBH_DEBUG_LEVEL > 1)

#define  USBH_ErrLog(...)   chprintf(&SD2,"ERROR: ") ;\
                            chprintf(&SD2,__VA_ARGS__);\
                            chprintf(&SD2,"\n");
#else
#define USBH_ErrLog(...)
#endif


#if (USBH_DEBUG_LEVEL > 2)
#define  USBH_DbgLog(...)   chprintf(&SD2,"DEBUG : ") ;\
                            chprintf(&SD2,__VA_ARGS__);\
                            chprintf(&SD2,"\n");
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


#endif //__USBH_CONF__H__


/**
  * @}
  */

/**
  * @}
  */
/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/

