/**
  ******************************************************************************
  * @file    usbh_hid_wacom.c
  * @author  Johannes Taelman
  * @brief   This file is the application layer for USB Host HID Wacom tablets
  ******************************************************************************


/* Includes ------------------------------------------------------------------*/
#include "usbh_hid_wacom.h"
#include "usbh_hid_parser.h"


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
  * @brief    This file includes HID Layer Handlers for USB Host HID class.
  * @{
  */ 

/** @defgroup USBH_HID_WACOM_Private_TypesDefinitions
  * @{
  */ 
/**
  * @}
  */ 


/** @defgroup USBH_HID_WACOM_Private_Defines
  * @{
  */ 
/**
  * @}
  */ 


/** @defgroup USBH_HID_WACOM_Private_Macros
  * @{
  */ 
/**
  * @}
  */ 

/** @defgroup USBH_HID_WACOM_Private_FunctionPrototypes
  * @{
  */ 
static USBH_StatusTypeDef USBH_HID_WacomDecode(USBH_HandleTypeDef *phost);

/**
  * @}
  */ 


/** @defgroup USBH_HID_WACOM_Private_Variables
  * @{
  */
HID_WACOM_Info_TypeDef    wacom_info;
uint8_t                  wacom_report_data[64];

/* Structures defining how to access items in a HID wacom report */
#if 0
/* Access button 1 state. */
static const HID_Report_ItemTypedef prop_b1={
  (uint8_t *)wacom_report_data+0, /*data*/
  1,     /*size*/
  0,     /*shift*/
  0,     /*count (only for array items)*/
  0,     /*signed?*/
  0,     /*min value read can return*/
  1,     /*max value read can return*/
  0,     /*min value device can report*/
  1,     /*max value device can report*/
  1      /*resolution*/
};

/* Access button 2 state. */
static const HID_Report_ItemTypedef prop_b2={
  (uint8_t *)wacom_report_data+0, /*data*/
  1,     /*size*/
  1,     /*shift*/
  0,     /*count (only for array items)*/  
  0,     /*signed?*/
  0,     /*min value read can return*/
  1,     /*max value read can return*/
  0,     /*min value device can report*/
  1,     /*max value device can report*/
  1      /*resolution*/
};

/* Access button 3 state. */   
static const HID_Report_ItemTypedef prop_b3={
  (uint8_t *)wacom_report_data+0, /*data*/
  1,     /*size*/
  2,     /*shift*/
  0,     /*count (only for array items)*/
  0,     /*signed?*/
  0,     /*min value read can return*/
  1,     /*max value read can return*/
  0,     /*min vale device can report*/
  1,     /*max value device can report*/
  1      /*resolution*/
};

/* Access x coordinate change. */
// 0123456789ABCDEFFEDCBA9876543210
//         0xAB                     // +6:14:8
//         0xAB00                   // +6:14:0
// 0x26                             // +8:14:10
//                 0xDC             // +10:14:8


static const HID_Report_ItemTypedef prop_x={
  (uint8_t *)wacom_report_data+6, /*data*/
  16,     /*size*/
  0,     /*shift*/
  0,     /*count (only for array items)*/
  0,     /*signed?*/
  0,     /*min value read can return*/
  0xFFFF,/*max value read can return*/
  0,     /*min vale device can report*/
  0xFFFF,/*max value device can report*/
  1      /*resolution*/
};

/* Access y coordinate change. */
static const HID_Report_ItemTypedef prop_y={
  (uint8_t *)wacom_report_data+8, /*data*/
  16,     /*size*/
  0,     /*shift*/
  0,     /*count (only for array items)*/  
  0,     /*signed?*/
  0,     /*min value read can return*/
  0xFFFF,/*max value read can return*/
  0,     /*min vale device can report*/
  0xFFFF,/*max value device can report*/
  1      /*resolution*/
};


/* Access y coordinate change. */
static const HID_Report_ItemTypedef prop_press={
  (uint8_t *)wacom_report_data+10, /*data*/
  16,     /*size*/
  0,     /*shift*/
  0,     /*count (only for array items)*/  
  0,     /*signed?*/
  0,     /*min value read can return*/
  0xFFFF,/*max value read can return*/
  0,     /*min vale device can report*/
  0xFFFF,/*max value device can report*/
  1      /*resolution*/
};

#endif

/**
  * @}
  */ 


/** @defgroup USBH_HID_WACOM_Private_Functions
  * @{
  */ 

/**
  * @brief  USBH_HID_WacomInit
  *         The function init the HID wacom.
  * @param  phost: Host handle
  * @retval USBH Status
  */
USBH_StatusTypeDef USBH_HID_WacomInit(USBH_HandleTypeDef *phost)
{
  HID_HandleTypeDef *HID_Handle =  (HID_HandleTypeDef *) phost->pActiveClass->pData;

  wacom_info.x=0;
  wacom_info.y=0;
  wacom_info.buttons[0]=0;
  wacom_info.buttons[1]=0;
  wacom_info.buttons[2]=0;
  
  wacom_report_data[0]=0;
  
  if(HID_Handle->length > sizeof(wacom_report_data))
  {
    HID_Handle->length = sizeof(wacom_report_data);
  }
  HID_Handle->pData = (uint8_t *)wacom_report_data;
  fifo_init(&HID_Handle->fifo, phost->device.Data, 2 * sizeof(wacom_report_data));

#if 1
  if(USBH_HID_GetReport (phost,
                         0x03,
                          0x05,//0x07
                          HID_Handle->pData,
							HID_Handle->length) == USBH_OK)
  {

    fifo_write(&HID_Handle->fifo, HID_Handle->pData, HID_Handle->length);
    USBH_HID_EventCallback(phost);
    HID_Handle->state = HID_SYNC;
  }
#endif

  return USBH_NOT_SUPPORTED;
}

/**
  * @brief  USBH_HID_GetWacomInfo
  *         The function return wacom information.
  * @param  phost: Host handle
  * @retval wacom information
  */
HID_WACOM_Info_TypeDef *USBH_HID_GetWacomInfo(USBH_HandleTypeDef *phost)
{
 if(USBH_HID_WacomDecode(phost)== USBH_OK)
 {
  return &wacom_info;
 }
 else
 {
  return NULL; 
 }
}

/**
  * @brief  USBH_HID_WacomDecode
  *         The function decode wacom data.
  * @param  phost: Host handle
  * @retval USBH Status
  */
static USBH_StatusTypeDef USBH_HID_WacomDecode(USBH_HandleTypeDef *phost)
{
  HID_HandleTypeDef *HID_Handle = (HID_HandleTypeDef *) phost->pActiveClass->pData;
  
  if(HID_Handle->length == 0)
  {
    return USBH_FAIL;
  }
  /*Fill report */
  if(fifo_read(&HID_Handle->fifo, &wacom_report_data, HID_Handle->length) ==  HID_Handle->length)
  {
	 if (wacom_report_data[0] == 0x10){
		wacom_info.x = (wacom_report_data[2]<<8) + wacom_report_data[3];
		wacom_info.y = (wacom_report_data[4]<<8) + wacom_report_data[5];
		wacom_info.press = wacom_report_data[6];
		wacom_info.state = wacom_report_data[1];
	  } else if (wacom_report_data[0] == 0x02){
		  if (wacom_report_data[2] == 0x02){
			  wacom_info.x = wacom_report_data[4];
			  wacom_info.y = wacom_report_data[5];
			  wacom_info.press = wacom_report_data[3];
			  wacom_info.state = wacom_report_data[6];
		  }
	  }

    return USBH_OK;  
  }
  return   USBH_FAIL;
}

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


/**
  * @}
  */
/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
