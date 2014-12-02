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
//#include "usbh_midi_controller.h"

/** @defgroup USBH_MIDI_CORE_Private_Variables
 * @{
 */
//extern USB_OTG_CORE_HANDLE USB_OTG_Core_dev;

//MIDI_Machine_t MIDI_Machine;

USB_Setup_TypeDef MIDI_Setup;

//USBH_MIDIDesc_t			MIDI_Desc  ;

__IO uint8_t start_toggle = 0;

//int State;


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

#define MIDI_MIN_POLL 10

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

  interface = USBH_FindInterface(phost, USB_AUDIO_CLASS,
                                 USB_MIDISTREAMING_SubCLASS, 0xFF);

  if (interface == 0xFF) /* No Valid Interface */
  {
    status = USBH_FAIL;
    USBH_DbgLog("Cannot Find the interface for %s class.",
                phost->pActiveClass->Name);
  }
  else {

    USBH_SelectInterface(phost, interface);
    phost->pActiveClass->pData = (MIDI_HandleTypeDef *)USBH_malloc(
        sizeof(MIDI_HandleTypeDef));
    MIDI_Handle = phost->pActiveClass->pData;
    MIDI_Handle->state = MIDI_ERROR;

    /*Decode Bootclass Protocl: Mouse or Keyboard*/

    /*if (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].bInterfaceProtocol
        == HID_KEYBRD_BOOT_CODE) {
      USBH_UsrLog("KeyBoard device found!");
      HID_Handle->Init = USBH_HID_KeybdInit;
    }
    else if (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].bInterfaceProtocol
        == HID_MOUSE_BOOT_CODE) {
      USBH_UsrLog("Mouse device found!");
      HID_Handle->Init = USBH_HID_MouseInit;
    }
    else {
      USBH_UsrLog("Protocol not supported.");
      return USBH_FAIL;
    }
*/

    MIDI_Handle->state = MIDI_INIT;
//    HID_Handle->ctl_state = HID_REQ_INIT;
    MIDI_Handle->ep_addr =
        phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].bEndpointAddress;
    MIDI_Handle->length =
        phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].wMaxPacketSize;
    MIDI_Handle->poll =
        phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[0].bInterval;

    if (MIDI_Handle->poll < MIDI_MIN_POLL) {
      MIDI_Handle->poll = MIDI_MIN_POLL;
    }

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

        USBH_LL_SetToggle(phost, MIDI_Handle->InPipe, 0);

      }
      else {
        MIDI_Handle->OutEp =
            (phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[num].bEndpointAddress);
        MIDI_Handle->OutPipe =\
 USBH_AllocPipe(phost, MIDI_Handle->OutEp);

        /* Open pipe for OUT endpoint */
        USBH_OpenPipe(phost, MIDI_Handle->OutPipe, MIDI_Handle->OutEp,
                      phost->device.address, phost->device.speed,
                      USB_EP_TYPE_BULK, MIDI_Handle->length);

        USBH_LL_SetToggle(phost, MIDI_Handle->OutPipe, 0);
      }

    }
    status = USBH_OK;
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
static USBH_StatusTypeDef USBH_MIDI_Process(USBH_HandleTypeDef *phost) {
  USBH_StatusTypeDef status = USBH_OK;
  MIDI_HandleTypeDef *MIDI_Handle = phost->pActiveClass->pData;

//uint8_t appliStatus = 0;
//USBH_Status status = USBH_BUSY;

//  if (HCD_IsDeviceConnected(pdev)) {
    //appliStatus = pphost->usr_cb->UserApplication(); // this will call USBH_USR_MIDI_Application()

    switch (MIDI_Handle->state) {

    case MIDI_INIT:
      MIDI_Handle->state = MIDI_GET_DATA;
      break;

    case MIDI_GET_DATA:

      USBH_BulkReceiveData(phost, MIDI_Handle->buff, 64,
                           MIDI_Handle->InPipe);
      start_toggle = 1;
      MIDI_Handle->state = MIDI_POLL;
      //STM_EVAL_LEDOn(LED_Blue);

      break;

    case MIDI_POLL:
      if (USBH_LL_GetURBState(phost, MIDI_Handle->InPipe) == USBH_URB_DONE) {
        if (start_toggle == 1) /* handle data once */
        {
          start_toggle = 0;
          //MIDI_Machine.cb->Decode(MIDI_Machine.buff);
          //MIDI_Decode(MIDI_Machine->buff);
          MIDI_CB(MIDI_Handle->buff[0],MIDI_Handle->buff[1],MIDI_Handle->buff[2],MIDI_Handle->buff[3]);
          MIDI_Handle->buff[1] = 0; // the whole buffer should be cleaned...
        }
        MIDI_Handle->state = MIDI_GET_DATA;
      }
      else if (USBH_LL_GetURBState(phost, MIDI_Handle->InPipe)
          == USBH_URB_STALL) /* IN Endpoint Stalled */
          {

        /* Issue Clear Feature on IN endpoint */
        if (USBH_ClrFeature(phost, MIDI_Handle->ep_addr) == USBH_OK) {
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
