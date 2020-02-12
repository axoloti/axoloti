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
 * @endverbatim
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
#include "midi_usbh.h"

//#define STM32_REGISTRY_H
//#define HAL_USB_LLD_H

#define _USB_H_
#include "ch.h"
//#include "hal.h"
#include "axoloti_board.h"

// USB_Setup_TypeDef MIDI_Setup;

#define MIDI_MIN_READ_POLL 1
#define MIDI_MIN_WRITE_POLL 1


/** @defgroup USBH_MIDI_CORE_Private_Variables
 * @{
 */




/****************** MIDI interface ****************************/

USBH_ClassTypeDef  MIDI_Class = {
  "MID",
  USB_AUDIO_CLASS,
  USBH_MIDI_InterfaceInit,
  USBH_MIDI_InterfaceDeInit,
  USBH_MIDI_ClassRequest,
  USBH_MIDI_Process,
  USBH_MIDI_SOFProcess,
  NULL,
};


bool isValidInput(MIDI_HandleTypeDef* pH)
{
    return pH!= NULL && pH->input_valid;
}

bool isValidOutput(MIDI_HandleTypeDef* pH)
{
    return pH!= NULL && pH->output_valid;
}

static char * getCSEPDesc(USBH_HandleTypeDef *phost, int intf, int ep) {
    uint16_t ptr = USB_LEN_CFG_DESC;
    int8_t if_ix = 0;
    int8_t ep_ix = 0;
    int8_t if_num = -1;
    int8_t ep_num = -1;

    char *buf = phost->device.CfgDesc_Raw;
    USBH_DescHeader_t *pdesc = (USBH_DescHeader_t *)buf;
    int cfg_desc_wTotalLength = LE16 (buf + 2);
    while (ptr < cfg_desc_wTotalLength)  {
        pdesc = USBH_GetNextDesc((uint8_t *)pdesc, &ptr);
        if (pdesc->bDescriptorType == USB_DESC_TYPE_INTERFACE)
        {
            ep_ix = 0;
            if_num = if_ix;
            if_ix++;
            //USBH_UsrLog("INTF %02x", if_ix);
        } else if (pdesc->bDescriptorType == USB_DESC_TYPE_ENDPOINT) {
            ep_num = ep_ix;
            ep_ix++;
            //USBH_UsrLog("ENDPOINT %02x", ep_num);
        } else if (pdesc->bDescriptorType == 0x25 /* CS_ENDPOINT */) {
            if ((ep_num == ep) && (if_num == intf)) {
                return (char *)pdesc;
            }
        }
    }
    return 0;
}

/*-----------------------------------------------------------------------------------------*/
/**
 * @brief  USBH_MIDI_InterfaceInit
 *         The function init the MIDI class.
 * @param  pdev: Selected device
 * @param  hdev: Selected device property
 * @retval  USBH_Status :Response for USB MIDI driver intialization
 */
USBH_StatusTypeDef USBH_MIDI_InterfaceInit(USBH_HandleTypeDef *phost) {

    USBH_StatusTypeDef status = USBH_FAIL;
    MIDI_HandleTypeDef *MIDI_Handle;

    uint8_t interface;
    // usbh_midi_init();
//    sysmon_disable_blinker();

    // this is limited to one midi interface, and also currently only 1 input and 1 output endpoint on that interface
    
    for(interface=0; interface<phost->device.CfgDesc.bNumInterfaces && interface < USBH_MAX_NUM_INTERFACES; interface++) {
        if( (phost->device.CfgDesc.Itf_Desc[interface].bInterfaceClass == USB_AUDIO_CLASS) &&
            (phost->device.CfgDesc.Itf_Desc[interface].bInterfaceSubClass == USB_MIDISTREAMING_SubCLASS) ) {

            // bizarre, why select the interface... all it does is put it in current interface and then log it,
            // but we may not even actually use it !?
            USBH_SelectInterface(phost, interface);
            phost->pActiveClass->pData = (MIDI_HandleTypeDef *)USBH_malloc(sizeof(MIDI_HandleTypeDef));
            MIDI_Handle = phost->pActiveClass->pData;
            MIDI_Handle->state_in = MIDI_INIT;
            MIDI_Handle->state_out = MIDI_INIT;


            uint8_t num_ep = ((phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].bNumEndpoints <= USBH_MAX_NUM_ENDPOINTS)
                              ? phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].bNumEndpoints
                              : USBH_MAX_NUM_ENDPOINTS);

            // eventually we should be looking for multiple input and output EP, for output we then just write to the one indicated
            // by the CID, for READ we may have to consider allowing the user to select which ports they are interested in (for efficiency?)
            // but for the moment just pick the first input and the first output
            MIDI_Handle->input_valid = false;
            MIDI_Handle->output_valid = false;

            uint8_t i=0;
            for (; i< num_ep && (!isValidInput(MIDI_Handle) || !isValidOutput(MIDI_Handle)) ; i++) {
                bool bInput = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bEndpointAddress & 0x80;
                if(!isValidInput(MIDI_Handle) && bInput) {
                	USBH_EpDescTypeDef *epDesc = &phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i];
                    MIDI_Handle->InEp = epDesc->bEndpointAddress;
                    MIDI_Handle->InEpSize  = epDesc->wMaxPacketSize;
                	USBH_UsrLog("USB Host Input size requests : %x", MIDI_Handle->InEpSize );
                    MIDI_Handle->InEpSize = USBH_MIDI_EPS_IN_SIZE; // why bother reducing the size? Some devices will lie about the max ep size...
//                    MIDI_Handle->read_poll = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bInterval;
//                	USBH_UsrLog("USB Host Input interval : %i", MIDI_Handle->read_poll);
//                    if(MIDI_Handle->read_poll<MIDI_MIN_READ_POLL) MIDI_Handle->read_poll = MIDI_MIN_READ_POLL;
                    MIDI_Handle->input_valid = true;
                    // TODO: remove USBHMIDIC[0] references
                    char *b = getCSEPDesc(phost,interface,i);
                    if (b) {
                        //USBH_UsrLog("CS_ENDPOINT %08x %02x %02x %02x %02x", b, b[0], b[1], b[2], b[3]);
                        USBHMIDIC[0].in_mapping->nports = b[3];
                    } else {
                        USBHMIDIC[0].in_mapping->nports = 1;
                    }
                }
                if(!isValidOutput(MIDI_Handle) && !bInput) {
                	USBH_EpDescTypeDef *epDesc = &phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i];
                    MIDI_Handle->OutEp = epDesc->bEndpointAddress;
                    MIDI_Handle->OutEpSize = epDesc->wMaxPacketSize;
                	USBH_UsrLog("USB Host Output size requests : %x", MIDI_Handle->OutEpSize );
                    if(MIDI_Handle->OutEpSize >USBH_MIDI_EPS_OUT_SIZE) MIDI_Handle->OutEpSize = USBH_MIDI_EPS_OUT_SIZE;
//                    MIDI_Handle->write_poll = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bInterval;
//                	USBH_UsrLog("USB Host Output interval : %i", MIDI_Handle->write_poll);
//                    if(MIDI_Handle->write_poll<MIDI_MIN_WRITE_POLL) MIDI_Handle->write_poll = MIDI_MIN_WRITE_POLL;
                    MIDI_Handle->output_valid = true;
                    char *b = getCSEPDesc(phost,interface,i);
                    if (b) {
                        //USBH_UsrLog("CS_ENDPOINT %08x %02x %02x %02x %02x", b,  b[0], b[1], b[2], b[3]);
                        USBHMIDIC[0].out_mapping->nports = b[3];
                    } else {
                        USBHMIDIC[0].out_mapping->nports = 1;
                    }
                }
            } // each endpoint, or until ive found both input and output endpoint

            if (isValidOutput(MIDI_Handle)) {
                USBH_UsrLog("USB Host Output connected to %x : %x", interface, MIDI_Handle->OutEp );

                MIDI_Handle->OutPipe = USBH_AllocPipe(phost, MIDI_Handle->OutEp);
                USBH_OpenPipe  (phost,
                                MIDI_Handle->OutPipe,
                                MIDI_Handle->OutEp,
                                phost->device.address,
                                phost->device.speed,
                                USB_EP_TYPE_BULK,
                                MIDI_Handle->OutEpSize);
                USBH_LL_SetToggle  (phost, MIDI_Handle->OutPipe,0);

                // ring buffer ready to use
                // usbh_midi_reset_buffer();
            }
            
            if (isValidInput(MIDI_Handle)) {
                USBH_UsrLog("USB Host Input connected to %x : %x", interface, MIDI_Handle->InEp );

                MIDI_Handle->InPipe = USBH_AllocPipe(phost, MIDI_Handle->InEp);
                USBH_OpenPipe  (phost,
                                MIDI_Handle->InPipe,
                                MIDI_Handle->InEp,
                                phost->device.address,
                                phost->device.speed,
                                USB_EP_TYPE_BULK,
                                MIDI_Handle->InEpSize);
                USBH_LL_SetToggle  (phost, MIDI_Handle->InPipe,0);
            }
            status = USBH_OK;


            return status;
        } // if, a midi interface
  
    }// for each interface
    
  return status;
}


/*-----------------------------------------------------------------------------------------*/
/**
 * @brief  USBH_MIDI_InterfaceDeInit
 *         The function DeInit the Host Channels used for the MIDI class.
 * @param  pdev: Selected device
 * @param  hdev: Selected device property
 * @retval None
 */
USBH_StatusTypeDef USBH_MIDI_InterfaceDeInit  (__attribute__((__unused__))  USBH_HandleTypeDef *phost) {
   	USBH_UsrLog("USB Host : device disconnected");
    MIDI_HandleTypeDef *MIDI_Handle = phost->pActiveClass->pData;
    if (isValidOutput(MIDI_Handle)) {
        USBH_ClosePipe(phost, MIDI_Handle->OutPipe);
        USBH_FreePipe(phost, MIDI_Handle->OutPipe);
        MIDI_Handle->OutPipe = 0;
        MIDI_Handle->output_valid = false;
    }
    if (isValidInput(MIDI_Handle)) {
        USBH_ClosePipe(phost, MIDI_Handle->InPipe);
        USBH_FreePipe(phost, MIDI_Handle->InPipe);
        MIDI_Handle->InPipe = 0;
        MIDI_Handle->input_valid = false;
    }
    
    if(phost->pActiveClass->pData)
    {
        USBH_free (phost->pActiveClass->pData);
        phost->pActiveClass->pData = NULL;
    }

    usbmidi_disconnect(&USBHMIDIC[0].config);
    return USBH_OK;
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
USBH_StatusTypeDef USBH_MIDI_ClassRequest(__attribute__((__unused__))  USBH_HandleTypeDef *phost) {
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


USBH_StatusTypeDef USBH_MIDI_ProcessInput(USBH_HandleTypeDef *phost) {
    USBH_StatusTypeDef status = USBH_OK;
    MIDI_HandleTypeDef *MIDI_Handle = phost->pActiveClass->pData;

    if(!isValidInput(MIDI_Handle)) {
        USBH_UsrLog("USBH_MIDI_ProcessInput : invalid input");
        return status; // ?
    }

    USBH_URBStateTypeDef URB_state_in = USBH_LL_GetURBState(phost, MIDI_Handle->InPipe);

    switch (MIDI_Handle->state_in) {
        case MIDI_INIT:

            if ( USBH_Get_StringDesc(phost,
                                       phost->device.DevDesc.iProduct,
									   MIDI_Handle->name,
                                       0xff) == USBH_OK)
              {
            	USBHMIDIC[0].in_mapping->name = MIDI_Handle->name;
            	USBHMIDIC[0].out_mapping->name = MIDI_Handle->name;
            	load_midi_routing(USBHMIDIC[0].in_mapping, in);
            	load_midi_routing(USBHMIDIC[0].out_mapping, out);
                MIDI_Handle->state_in = MIDI_GET_DATA;
              }
            break;

        case MIDI_GET_DATA:
            if (URB_state_in == USBH_URB_STALL) {
                USBH_ErrLog("USB Host Input,GD  URB STALL");
                USBH_ClrFeature(phost, MIDI_Handle->InEp);
            } else if (URB_state_in == USBH_URB_ERROR) {
                USBH_ErrLog("USB Host Input,GD  URB ERROR");
                USBH_ClrFeature(phost, MIDI_Handle->InEp);
            }

            USBH_BulkReceiveData(phost, MIDI_Handle->buff_in, MIDI_Handle->InEpSize, MIDI_Handle->InPipe);
            MIDI_Handle->state_in = MIDI_POLL;
            //STM_EVAL_LEDOn(LED_Blue);
            break; 

        case MIDI_RETRY:
        case MIDI_POLL:
            if (URB_state_in == USBH_URB_DONE) {
                // USBH_DbgLog("USB Host Input  URB DONE");
                int n = USBH_LL_GetLastXferSize(phost, MIDI_Handle->InPipe);
                USBHMIDIC[0].config.cb_report(&USBHMIDIC[0].config, (uint32_t *)MIDI_Handle->buff_in, n/4);
                MIDI_Handle->state_in = MIDI_POLL;
                URB_state_in = USBH_LL_GetURBState(phost, MIDI_Handle->InPipe);
                if (URB_state_in != USBH_URB_DONE) {
                  USBH_ErrLog("URB0 Not done %d",URB_state_in);
                }
                USBH_BulkReceiveData(phost, MIDI_Handle->buff_in, MIDI_Handle->InEpSize, MIDI_Handle->InPipe);
                if (URB_state_in != USBH_URB_DONE) {
                  USBH_ErrLog("URB1 Not done %d",URB_state_in);
                }
            } else if (URB_state_in == USBH_URB_STALL) {
                USBH_ErrLog("USB Host Input,POLL  URB STALL 2");
                if (USBH_ClrFeature(phost, MIDI_Handle->InEp) == USBH_OK) {
                    MIDI_Handle->state_in = MIDI_GET_DATA;
                }
            } else {
              if (URB_state_in == USBH_URB_IDLE) {
#if 1
                //USBH_ErrLog("URB state? %d",URB_state_in);
//                palTogglePad(LED2_PORT,LED2_PIN);
//                if (USBH_ClrFeature(phost, MIDI_Handle->InEp) == USBH_OK) {
//                    //MIDI_Handle->state_in = MIDI_GET_DATA;
//                }

                int n = USBH_LL_GetLastXferSize(phost, MIDI_Handle->InPipe);
                USBHMIDIC[0].config.cb_report(&USBHMIDIC[0].config, (uint32_t *)MIDI_Handle->buff_in, n/4);
                USBH_BulkReceiveData(phost, MIDI_Handle->buff_in, MIDI_Handle->InEpSize, MIDI_Handle->InPipe);
                MIDI_Handle->state_in = MIDI_RETRY; // hanging notes when using MIDI_POLL...
#endif
              } else {
                USBH_DbgLog("URB Host Input state? %d",URB_state_in);
              }
            }
            break;

        default:
                USBH_DbgLog("USB Host Input  Invalid State");
            break;
    } // case
    return status;
}

// PING = 1 for HS, apparently ,but 0 or 1 makes no difference
#define SEND_DATA_DO_PING 0

USBH_StatusTypeDef USBH_MIDI_ProcessOutput(USBH_HandleTypeDef *phost) {
    USBH_StatusTypeDef status = USBH_OK;
    MIDI_HandleTypeDef *MIDI_Handle = phost->pActiveClass->pData;

    if(!isValidOutput(MIDI_Handle)) {
        USBH_UsrLog("USBH_MIDI_ProcessOutput : invalid output");
        return status; // ?
    }

    USBH_URBStateTypeDef URB_state_out = USBH_LL_GetURBState(phost, MIDI_Handle->OutPipe);

    switch (MIDI_Handle->state_out) {
        case MIDI_INIT:
            MIDI_Handle->state_out = MIDI_SEND_DATA;
            break;


        case MIDI_POLL:
            if(URB_state_out == USBH_URB_DONE) {
                MIDI_Handle->buff_out_len = 0;
                MIDI_Handle->state_out = MIDI_SEND_DATA;
                USBH_DbgLog("USB Host Output URB DONE");
            }
            else if(URB_state_out == USBH_URB_NOTREADY) {
                USBH_ErrLog("USB Host Output NOT READY");
                // send again
                USBH_BulkSendData(phost, MIDI_Handle->buff_out, MIDI_Handle->buff_out_len, MIDI_Handle->OutPipe, SEND_DATA_DO_PING);
                MIDI_Handle->state_out = MIDI_POLL;
                break;
            } else if (URB_state_out == USBH_URB_IDLE) {
//                MIDI_Handle->state_out = MIDI_SEND_DATA;
//                osMessagePutI ( phost->os_event, USBH_URB_EVENT, 0);

//              USBH_BulkSendData(phost, MIDI_Handle->buff_out, MIDI_Handle->buff_out_len, MIDI_Handle->OutPipe, SEND_DATA_DO_PING);
//              MIDI_Handle->state_out = MIDI_POLL;
 //             osMessagePutI ( phost->os_event, USBH_URB_EVENT, 0);
/*
              if (USBH_ClrFeature(phost, MIDI_Handle->OutEp) == USBH_OK) {
                USBH_ErrLog("USB Idle1");
              }
              else {
                USBH_ErrLog("USB Idle2");
              }*/

              MIDI_Handle->state_out = MIDI_RETRY;
              break;
                // wait
                //USBH_DbgLog("USB Host Output IDLE %i " ,MIDI_Handle->buff_out_len);
                ; // NOP
            } else if (URB_state_out == USBH_URB_ERROR) {
                // giveup
                USBH_ErrLog("USB Host Output Error sending data");
                MIDI_Handle->state_out = MIDI_SEND_DATA;
                MIDI_Handle->buff_out_len = 0;
                break;
            } else if (URB_state_out == USBH_URB_STALL) {
                // stalled, reset ep
                if (USBH_ClrFeature(phost, MIDI_Handle->OutEp) == USBH_OK) {
                    MIDI_Handle->state_out = MIDI_SEND_DATA;
                    USBH_ErrLog("USB Host Output STALLED and cleared");
                }
                else {
                    USBH_ErrLog("USB Host Output  write pipe stalled unable to clear");
                    MIDI_Handle->state_out = MIDI_SEND_DATA;
                    MIDI_Handle->buff_out_len = 0;
                }
                break;
            } else {
                // giveup
                USBH_ErrLog("USB Host Output  unknown state sending data %x ", URB_state_out);
                MIDI_Handle->state_out = MIDI_SEND_DATA;
                MIDI_Handle->buff_out_len = 0;
                break;
            }
            
        case MIDI_SEND_DATA:
            if (URB_state_out == USBH_URB_STALL) {
                USBH_ErrLog("USB Host Output(SD) STALL");
                USBH_ClrFeature(phost, MIDI_Handle->OutEp);
            } else if (URB_state_out == USBH_URB_ERROR) {
                USBH_ErrLog("USB Host Output(SD) ERROR");
                USBH_ClrFeature(phost, MIDI_Handle->OutEp);
            }
            static midi_message_t outbuf[4];
			midi_message_t *m = &outbuf[0];
			int s;
			midi_output_buffer_t *b = &USBHMIDIC[0].out_buffer;
			for(s=0;s<4;s++) {
					msg_t r = midi_output_buffer_get(b, m);
					if (r!=0) break;
					m++;
			}
			// buffer made, transmit
			if (s>0) {
				USBH_BulkSendData(phost, (uint8_t *)outbuf, s*4, MIDI_Handle->OutPipe, SEND_DATA_DO_PING);
				USBH_DbgLog("USB Host Output sent bytes : %i", MIDI_Handle->buff_out_len);

				// now poll for completion
				MIDI_Handle->state_out = MIDI_POLL;
			}
            break;

        case MIDI_RETRY:
          USBH_BulkSendData(phost, MIDI_Handle->buff_out, MIDI_Handle->buff_out_len, MIDI_Handle->OutPipe, SEND_DATA_DO_PING);
          USBH_DbgLog("USB Host Output sent bytes : %i", MIDI_Handle->buff_out_len);

          // now poll for completion
          MIDI_Handle->state_out = MIDI_POLL;
          break;


            
        default:
            break;
    } // case

    return status;
}

USBH_StatusTypeDef USBH_MIDI_Process(USBH_HandleTypeDef *phost) {
    USBH_StatusTypeDef status=USBH_OK;

    status =  USBH_MIDI_ProcessInput(phost);
    if (status!=USBH_OK) return status;

    status =  USBH_MIDI_ProcessOutput(phost);
    return status;
}
/*-----------------------------------------------------------------------------------------*/

USBH_StatusTypeDef USBH_MIDI_SOFProcess(USBH_HandleTypeDef *phost) {
    MIDI_HandleTypeDef *MIDI_Handle =  (MIDI_HandleTypeDef *) phost->pActiveClass->pData;

    if (!isValidInput(MIDI_Handle) && !isValidOutput(MIDI_Handle)) {
        USBH_UsrLog("USBH_MIDI_SOFProcess : invalid input/output");
        return USBH_OK; //?
    }
//    palTogglePad(LED1_PORT,LED1_PIN);

    if (MIDI_Handle->state_out == MIDI_SEND_DATA) {

#if 0 // TODO: Fix USBH midi output
//        if (( phost->Timer - MIDI_Handle->write_timer) >= MIDI_Handle->write_poll
//              || phost->Timer < MIDI_Handle->write_timer) {
            if (send_ring_buffer.read_ptr != send_ring_buffer.write_ptr) {
                // ready to send more data, and we have data to send
                #if (USBH_USE_OS == 1)
                osMessagePutI ( phost->os_event, USBH_URB_EVENT, 0);
                #endif
            }
//        }
#endif
    } else if ((MIDI_Handle->state_out == MIDI_RETRY)||
        (MIDI_Handle->state_in == MIDI_RETRY)) {
      #if (USBH_USE_OS == 1)
      osMessagePutI ( phost->os_event, USBH_URB_EVENT, 0);
      #endif
    }
    return USBH_OK;
}
