/**
 ******************************************************************************
 * @file    usbh_midi_core.c
 * @author	Johannes Taelman (based on work by Xavier Halgand)
 * @version
 * @date
 * @brief   Very basic driver for USB Host MIDI class.
 *
 * @verbatim
 *
 *          ===================================================================
 *                                MIDI Class  Description
 *          ===================================================================
 *
 *
 *  @endverbatim
 *
 ******************************************************************************
 *
 *
 ******************************************************************************
 */

/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

/* Includes ------------------------------------------------------------------*/
#include "usbh_midi_core.h"

/** @defgroup USBH_MIDI_CORE_Private_Variables
 * @{
 */
//extern USB_OTG_CORE_HANDLE USB_OTG_Core_dev;


USB_Setup_TypeDef MIDI_Setup;


#define _USB_H_
#include "ch.h"



/****************** MIDI interface ****************************/

static USBH_StatusTypeDef USBH_MIDI_InterfaceInit  (USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_InterfaceDeInit  (USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_ClassRequest(USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_Process(USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_SOFProcess(USBH_HandleTypeDef *phost);
//static void  USBH_MIDI_ParseHIDDesc (HID_DescTypeDef *desc, uint8_t *buf);

USBH_ClassTypeDef  MIDI_Class =
{
  "MID",
  USB_AUDIO_CLASS,
  USBH_MIDI_InterfaceInit,
  USBH_MIDI_InterfaceDeInit,
  USBH_MIDI_ClassRequest,
  USBH_MIDI_Process,
  USBH_MIDI_SOFProcess,
  NULL,
};

#define MIDI_MIN_POLL 2

/*-----------------------------------------------------------------------------------------*/
/**
 * @brief  USBH_MIDI_InterfaceInit
 *         The function init the MIDI class.
 * @param  pdev: Selected device
 * @param  hdev: Selected device property
 * @retval  USBH_Status :Response for USB MIDI driver intialization
 */
static USBH_StatusTypeDef USBH_MIDI_InterfaceInit(USBH_HandleTypeDef *phost) {

  USBH_StatusTypeDef status = USBH_FAIL;
  MIDI_HandleTypeDef *MIDI_Handle;

  uint8_t max_ep;
  uint8_t num = 0;
  uint8_t interface;

  for(interface=0; interface<phost->device.CfgDesc.bNumInterfaces && interface < USBH_MAX_NUM_INTERFACES; interface++) {

  if( (phost->device.CfgDesc.Itf_Desc[interface].bInterfaceClass == USB_AUDIO_CLASS) &&
      (phost->device.CfgDesc.Itf_Desc[interface].bInterfaceSubClass == USB_MIDISTREAMING_SubCLASS) ) {

    USBH_SelectInterface(phost, interface);
    phost->pActiveClass->pData = (MIDI_HandleTypeDef *)USBH_malloc(
        sizeof(MIDI_HandleTypeDef));
    MIDI_Handle = phost->pActiveClass->pData;

    MIDI_Handle->state = MIDI_INIT;
    MIDI_Handle->ep_addr =
        phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].bEndpointAddress;
    MIDI_Handle->poll =
        phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].bInterval;

    if (MIDI_Handle->poll < MIDI_MIN_POLL) {
      MIDI_Handle->poll = MIDI_MIN_POLL;
    }

    if(phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].bEndpointAddress & 0x80)
    {
      MIDI_Handle->InEp = (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].bEndpointAddress);
      MIDI_Handle->InEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].wMaxPacketSize;
    }
    else
    {
      MIDI_Handle->OutEp = (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].bEndpointAddress);
      MIDI_Handle->OutEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].wMaxPacketSize;
    }

    if(phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[1].bEndpointAddress & 0x80)
    {
      MIDI_Handle->InEp = (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[1].bEndpointAddress);
      MIDI_Handle->InEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[1].wMaxPacketSize;
    }
    else
    {
      MIDI_Handle->OutEp = (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[1].bEndpointAddress);
      MIDI_Handle->OutEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[1].wMaxPacketSize;
    }

#if 0
    /* Check fo available number of endpoints */
    /* Find the number of EPs in the Interface Descriptor */
    /* Choose the lower number in order not to overrun the buffer allocated */
    max_ep =
        ((phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].bNumEndpoints
            <= USBH_MAX_NUM_ENDPOINTS) ? phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].bNumEndpoints :
                                         USBH_MAX_NUM_ENDPOINTS);

    /* Decode endpoint IN and OUT address from interface descriptor */
    for (; num < max_ep; num++) {
      if (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[num].bEndpointAddress
          & 0x80) {
        MIDI_Handle->InEp =
            (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[num].bEndpointAddress);
        MIDI_Handle->InPipe =\
 USBH_AllocPipe(phost, MIDI_Handle->InEp);

        /* Open pipe for IN endpoint */
        USBH_OpenPipe(phost, MIDI_Handle->InPipe, MIDI_Handle->InEp,
                      phost->device.address, phost->device.speed,
                      USB_EP_TYPE_BULK, MIDI_Handle->length);

      }
#if 0
      else {
        MIDI_Handle->OutEp =
            (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[num].bEndpointAddress);
        MIDI_Handle->OutPipe =\
 USBH_AllocPipe(phost, MIDI_Handle->OutEp);

        /* Open pipe for OUT endpoint */
        USBH_OpenPipe(phost, MIDI_Handle->OutPipe, MIDI_Handle->OutEp,
                      phost->device.address, phost->device.speed,
                      USB_EP_TYPE_BULK, MIDI_Handle->length);
      }
#endif
    }
#endif

    MIDI_Handle->OutPipe = USBH_AllocPipe(phost, MIDI_Handle->OutEp);
    MIDI_Handle->InPipe = USBH_AllocPipe(phost, MIDI_Handle->InEp);
    /* Open the new channels */
    USBH_OpenPipe  (phost,
                    MIDI_Handle->OutPipe,
                    MIDI_Handle->OutEp,
                    phost->device.address,
                    phost->device.speed,
                    USB_EP_TYPE_BULK,
                    MIDI_Handle->OutEpSize);

    USBH_OpenPipe  (phost,
                    MIDI_Handle->InPipe,
                    MIDI_Handle->InEp,
                    phost->device.address,
                    phost->device.speed,
                    USB_EP_TYPE_BULK,
                    MIDI_Handle->InEpSize);


    USBH_LL_SetToggle  (phost, MIDI_Handle->InPipe,0);
    USBH_LL_SetToggle  (phost, MIDI_Handle->OutPipe,0);


    status = USBH_OK;
  }
  }
  return status;
}
#if 0 // original code
if((phost->device_prop.Itf_Desc[0].bInterfaceClass == USB_AUDIO_CLASS) &&
    (pphost->device_prop.Itf_Desc[0].bInterfaceSubClass == USB_MIDISTREAMING_SubCLASS))
{
  if(phost->device_prop.Ep_Desc[0][0].bEndpointAddress & 0x80)
  {
    MIDI_Machine.MIDIBulkInEp = (phost->device_prop.Ep_Desc[0][0].bEndpointAddress);
    MIDI_Machine.MIDIBulkInEpSize = phost->device_prop.Ep_Desc[0][0].wMaxPacketSize;
  }
  else
  {
    MIDI_Machine.MIDIBulkOutEp = (phost->device_prop.Ep_Desc[0][0].bEndpointAddress);
    MIDI_Machine.MIDIBulkOutEpSize = phost->device_prop.Ep_Desc[0] [0].wMaxPacketSize;
  }

  if(pphost->device_prop.Ep_Desc[0][1].bEndpointAddress & 0x80)

  {
    MIDI_Machine.MIDIBulkInEp = (pphost->device_prop.Ep_Desc[0][1].bEndpointAddress);
    MIDI_Machine.MIDIBulkInEpSize = pphost->device_prop.Ep_Desc[0][1].wMaxPacketSize;
  }
  else
  {
    MIDI_Machine.MIDIBulkOutEp = (pphost->device_prop.Ep_Desc[0][1].bEndpointAddress);
    MIDI_Machine.MIDIBulkOutEpSize = pphost->device_prop.Ep_Desc[0][1].wMaxPacketSize;
  }

  MIDI_Machine.hc_num_out = USBH_Alloc_Channel(pdev,
      MIDI_Machine.MIDIBulkOutEp);
  MIDI_Machine.hc_num_in = USBH_Alloc_Channel(pdev,
      MIDI_Machine.MIDIBulkInEp);

  /* Open the new channels */
  USBH_Open_Channel (pdev,
      MIDI_Machine.hc_num_out,
      pphost->device_prop.address,
      pphost->device_prop.speed,
      EP_TYPE_BULK,
      MIDI_Machine.MIDIBulkOutEpSize);

  USBH_Open_Channel (pdev,
      MIDI_Machine.hc_num_in,
      pphost->device_prop.address,
      pphost->device_prop.speed,
      EP_TYPE_BULK,
      MIDI_Machine.MIDIBulkInEpSize);

  MIDI_Machine.state = MIDI_GET_DATA;
  start_toggle =0;
  status = USBH_OK;

}

else
{
  phost->
  usr_cb->DeviceNotSupported();
}

return status;

}
#endif

/*-----------------------------------------------------------------------------------------*/
/**
 * @brief  USBH_MIDI_InterfaceDeInit
 *         The function DeInit the Host Channels used for the MIDI class.
 * @param  pdev: Selected device
 * @param  hdev: Selected device property
 * @retval None
 */
USBH_StatusTypeDef USBH_MIDI_InterfaceDeInit  (USBH_HandleTypeDef *phost){
#if 0
  if (MIDI_Machine.hc_num_out) {
    USB_OTG_HC_Halt(pdev, MIDI_Machine.hc_num_out);
    USBH_Free_Channel(pdev, MIDI_Machine.hc_num_out);
    MIDI_Machine.hc_num_out = 0; /* Reset the Channel as Free */
  }

  if (MIDI_Machine.hc_num_in) {
    USB_OTG_HC_Halt(pdev, MIDI_Machine.hc_num_in);
    USBH_Free_Channel(pdev, MIDI_Machine.hc_num_in);
    MIDI_Machine.hc_num_in = 0; /* Reset the Channel as Free */
  }
  start_toggle = 0;
#else
  return USBH_OK;
#endif
}
/*-----------------------------------------------------------------------------------------*/
/**
 * @brief  USBH_MIDI_ClassRequest
 *         The function is responsible for handling MIDI Class requests
 *         for MIDI class.
 * @param  pdev: Selected device
 * @param  hdev: Selected device property
 * @retval  USBH_Status :Response for USB Set Protocol request
 */
static USBH_StatusTypeDef USBH_MIDI_ClassRequest(USBH_HandleTypeDef *phost) {
  USBH_StatusTypeDef status = USBH_OK;

  return status;
}

/*-----------------------------------------------------------------------------------------*/
/**
 * @brief  USBH_MIDI_Handle
 *         The function is for managing state machine for MIDI data transfers
 * @param  pdev: Selected device
 * @param  hdev: Selected device property
 * @retval USBH_Status
 */

volatile USBH_URBStateTypeDef URB_state_in;
volatile USBH_URBStateTypeDef URB_state_out;
volatile int state_in;
volatile int state_out;

static USBH_StatusTypeDef USBH_MIDI_Process(USBH_HandleTypeDef *phost) {
  USBH_StatusTypeDef status = USBH_OK;
  MIDI_HandleTypeDef *MIDI_Handle = phost->pActiveClass->pData;
  URB_state_in = USBH_LL_GetURBState(phost, MIDI_Handle->InPipe);
  URB_state_out = USBH_LL_GetURBState(phost, MIDI_Handle->OutPipe);
  state_in = HAL_HCD_GetState(phost->pData, MIDI_Handle->InPipe);
  state_out = HAL_HCD_GetState(phost->pData, MIDI_Handle->InPipe);


//  if (HCD_IsDeviceConnected(pdev)) {
    //appliStatus = pphost->usr_cb->UserApplication(); // this will call USBH_USR_MIDI_Application()

    switch (MIDI_Handle->state) {

    case MIDI_INIT:
      MIDI_Handle->state = MIDI_GET_DATA;
      //break;

    case MIDI_GET_DATA:
      if (URB_state_in == USBH_URB_STALL){
        USBH_ClrFeature(phost, MIDI_Handle->InEp);
      } else       if (URB_state_in == USBH_URB_ERROR){
        USBH_ClrFeature(phost, MIDI_Handle->InEp);
      }

      USBH_BulkReceiveData(phost, MIDI_Handle->buff, USBH_MIDI_MPS_SIZE,
                           MIDI_Handle->InPipe);
      MIDI_Handle->state = MIDI_POLL;
      //STM_EVAL_LEDOn(LED_Blue);

      break;

    case MIDI_POLL:
      if (USBH_LL_GetURBState(phost, MIDI_Handle->InPipe) == USBH_URB_DONE) {
        int i;
        int n = USBH_LL_GetLastXferSize(phost, MIDI_Handle->InPipe);
        for (i=0;i<n;i+=4){
          MIDI_CB(MIDI_Handle->buff[0+i],MIDI_Handle->buff[1+i],MIDI_Handle->buff[2+i],MIDI_Handle->buff[3+i]);
        }
        MIDI_Handle->buff[1] = 0; // the whole buffer should be cleaned...
        MIDI_Handle->state = MIDI_IDLE;
//        chThdSleepMilliseconds(1);
//        USBH_BulkReceiveData(phost, MIDI_Handle->buff, USBH_MIDI_MPS_SIZE,
//                             MIDI_Handle->InPipe);
        MIDI_Handle->timer = 0;
      }
      else if (USBH_LL_GetURBState(phost, MIDI_Handle->InPipe)
          == USBH_URB_STALL) /* IN Endpoint Stalled */
          {

        /* Issue Clear Feature on IN endpoint */
        if (USBH_ClrFeature(phost, MIDI_Handle->InEp) == USBH_OK) {
          /* Change state to issue next IN token */
          MIDI_Handle->state = MIDI_GET_DATA;
      }

    }
    break;

    default:
    break;
  }
  return status;

}
/*-----------------------------------------------------------------------------------------*/

static USBH_StatusTypeDef USBH_MIDI_SOFProcess(USBH_HandleTypeDef *phost){
  MIDI_HandleTypeDef *MIDI_Handle = phost->pActiveClass->pData;
/*
  URB_state_in = USBH_LL_GetURBState(phost, MIDI_Handle->InPipe);
  if (URB_state_in == USBH_URB_STALL){
    USBH_ClrFeature(phost, MIDI_Handle->InEp);
  } else       if (URB_state_in == USBH_URB_ERROR){
    USBH_ClrFeature(phost, MIDI_Handle->InEp);
  }
*/
  if ((MIDI_Handle->state == MIDI_IDLE)||(MIDI_Handle->state == MIDI_POLL)) {
    MIDI_Handle->timer++;
    if (MIDI_Handle->timer > MIDI_Handle->poll) {
      if ((USBH_LL_GetURBState(phost, MIDI_Handle->InPipe) == USBH_URB_IDLE) ||
          (USBH_LL_GetURBState(phost, MIDI_Handle->InPipe) == USBH_URB_DONE)) {
        USBH_BulkReceiveData(phost, MIDI_Handle->buff, USBH_MIDI_MPS_SIZE,
                             MIDI_Handle->InPipe);
        MIDI_Handle->state = MIDI_POLL;
      }
    }
    //MIDI_Handle->state = MIDI_GET_DATA;
  }
//  USBH_BulkReceiveData(phost, MIDI_Handle->buff, USBH_MIDI_MPS_SIZE,
//                       MIDI_Handle->InPipe);

  return USBH_OK;
}

/* Receive data from MIDI device *//*
uint8_t MIDI_RcvData(uint8_t *outBuf) {
URB_STATE urb_status;
urb_status = HCD_GetURB_State(&USB_OTG_Core_dev, MIDI_Machine.hc_num_in);

if (urb_status <= URB_DONE) {
  USBH_BulkReceiveData(&USB_OTG_Core_dev, MIDI_Machine.buff, 64,
                       MIDI_Machine.hc_num_in);
  if (MIDI_Machine.buff[0] == 0 && MIDI_Machine.buff[1] == 0
      && MIDI_Machine.buff[2] == 0 && MIDI_Machine.buff[3] == 0)
    return 0;
  outBuf[0] = MIDI_Machine.buff[1];
  outBuf[1] = MIDI_Machine.buff[2];
  outBuf[2] = MIDI_Machine.buff[3];
  return MIDI_lookupMsgSize(MIDI_Machine.buff[1]);
}
else
  return 0;

}*/
/*-----------------------------------------------------------------------------------------*/

/*****************************END OF FILE****/
