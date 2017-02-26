/**
  ******************************************************************************
  * @file    usbh_hid_wacom.h
  * @author  MCD Application Team
  * @version V3.2.0
  * @date    04-November-2014
  * @brief   This file contains all the prototypes for the usbh_hid_wacom.c
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

/* Define to prevent recursive  ----------------------------------------------*/
#ifndef __USBH_HID_WACOM_H
#define __USBH_HID_WACOM_H

#ifdef __cplusplus
 extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "usbh_hid.h"

/** @addtogroup USBH_LIB
  * @{
  */

/** @addtogroup USBH_CLASS
  * @{
  */

/** @addtogroup USBH_HID_CLASS
  * @{
  */

/** @defgroup USBH_HID_WACOM
  * @brief This file is the Header file for usbh_hid_wacom.c
  * @{
  */ 


/** @defgroup USBH_HID_WACOM_Exported_Types
  * @{
  */ 

typedef struct _HID_WACOM_Info
{
  uint16_t              x;
  uint16_t              y;
  uint16_t              press;
  uint16_t              state;
  uint8_t              buttons[4];
}
HID_WACOM_Info_TypeDef;

/**
  * @}
  */ 

/** @defgroup USBH_HID_WACOM_Exported_Defines
  * @{
  */ 
/**
  * @}
  */ 

/** @defgroup USBH_HID_WACOM_Exported_Macros
  * @{
  */ 
/**
  * @}
  */ 

/** @defgroup USBH_HID_WACOM_Exported_Variables
  * @{
  */ 
/**
  * @}
  */ 

/** @defgroup USBH_HID_WACOM_Exported_FunctionsPrototype
  * @{
  */ 
USBH_StatusTypeDef USBH_HID_WacomInit(USBH_HandleTypeDef *phost);
HID_WACOM_Info_TypeDef *USBH_HID_GetWacomInfo(USBH_HandleTypeDef *phost);

/**
  * @}
  */ 

#ifdef __cplusplus
}
#endif

#endif /* __USBH_HID_WACOM_H */

/**
  * @}
  */ 

/**
  * @}
  */ 

/**
  * @}
  */ 

/**
  * @}
  */ 
/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
