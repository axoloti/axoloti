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



#define _USB_H_
#include "ch.h"

USB_Setup_TypeDef MIDI_Setup;

#define RING_BUFFER_SIZE 5

typedef struct
{
    uint8_t data[4];
} MIDIEvent_t;


// very simple ring buffer
static struct {
    MIDIEvent_t event[RING_BUFFER_SIZE];
    uint8_t read_ptr;
    uint8_t write_ptr;
} send_ring_buffer  __attribute__ ((section (".ccmramend")));;



void usbh_midi_init(void)
{
    send_ring_buffer.read_ptr = send_ring_buffer.write_ptr = 0;
}


// the Send etc, work for everything except Sysex
inline uint8_t calcDS(uint8_t b0) {
    // this works for everything bar SysEx,
    // for sysex you need to use 0x4-0x7 to pack messages
    return (b0 & 0xF0 ) >> 4;
    
}

inline uint8_t calcCIN(uint8_t port, uint8_t b0) {
    uint8_t ds  = calcDS(b0);
    uint8_t cin = ((( port - 1) & 0x0F) << 4)  | ds;
    return cin;
}


void usbh_MidiSend1(uint8_t port, uint8_t b0) {
    USBH_DbgLog("usbh_MidiSend1");
    uint8_t next = (send_ring_buffer.write_ptr+ 1) % RING_BUFFER_SIZE;
    
    if(next == send_ring_buffer.read_ptr) {
        USBH_ErrLog("usbh_MidiSend1 : ring buffer overflow");
    }
    
    send_ring_buffer.event[next].data[0]=calcCIN(port, b0);
    send_ring_buffer.event[next].data[1]=b0;
    send_ring_buffer.event[next].data[2]=0;
    send_ring_buffer.event[next].data[3]=0;
    send_ring_buffer.write_ptr=next;
}

void usbh_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1) {
    USBH_DbgLog("usbh_MidiSend2");
    uint8_t next = (send_ring_buffer.write_ptr+ 1) % RING_BUFFER_SIZE;
    
    if(next == send_ring_buffer.read_ptr) {
        USBH_ErrLog("usbh_MidiSend2 : ring buffer overflow");
    }
    
    send_ring_buffer.event[next].data[0]=calcCIN(port, b0);
    send_ring_buffer.event[next].data[1]=b0;
    send_ring_buffer.event[next].data[2]=b1;
    send_ring_buffer.event[next].data[3]=0;
    send_ring_buffer.write_ptr=next;
}

void usbh_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
    USBH_DbgLog("usbh_MidiSend3");
    uint8_t next = (send_ring_buffer.write_ptr+ 1) % RING_BUFFER_SIZE;
    
    if(next == send_ring_buffer.read_ptr) {
        USBH_ErrLog("usbh_MidiSend3 : ring buffer overflow");
    }
    
    send_ring_buffer.event[next].data[0]=calcCIN(port, b0);
    send_ring_buffer.event[next].data[1]=b0;
    send_ring_buffer.event[next].data[2]=b1;
    send_ring_buffer.event[next].data[3]=b2;
    send_ring_buffer.write_ptr=next;
}

int  usbh_MidiGetOutputBufferPending(void) {
    return 0;
}

/** @defgroup USBH_MIDI_CORE_Private_Variables
 * @{
 */




/****************** MIDI interface ****************************/

static USBH_StatusTypeDef USBH_MIDI_InterfaceInit  (USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_InterfaceDeInit  (USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_ClassRequest(USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_Process(USBH_HandleTypeDef *phost);
static USBH_StatusTypeDef USBH_MIDI_SOFProcess(USBH_HandleTypeDef *phost);


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

#define MIDI_MIN_POLL 2

inline bool isValidInput(MIDI_HandleTypeDef* pH)
{
    return pH->input_valid;
}

inline bool isValidOutput(MIDI_HandleTypeDef* pH)
{
    return pH->output_valid;
}

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

    uint8_t interface;
    send_ring_buffer.read_ptr = send_ring_buffer.write_ptr = 0;

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
                
                uint8_t iv = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bInterval;
                // select longest poll time
                MIDI_Handle->poll = iv > MIDI_Handle->poll ? iv : MIDI_Handle->poll;


                if(!isValidInput(MIDI_Handle) && phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bEndpointAddress & 0x80) {
                    MIDI_Handle->InEp = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bEndpointAddress;
                    MIDI_Handle->InEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].wMaxPacketSize;
                    if(MIDI_Handle->InEpSize >USBH_MIDI_EPS_IN_SIZE) MIDI_Handle->InEpSize = USBH_MIDI_EPS_IN_SIZE;
                    MIDI_Handle->input_valid = true;
                }
                if(!isValidOutput(MIDI_Handle) && phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bEndpointAddress)
                {
                    MIDI_Handle->OutEp = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bEndpointAddress;
                    MIDI_Handle->OutEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].wMaxPacketSize;
                    if(MIDI_Handle->OutEpSize >USBH_MIDI_EPS_IN_SIZE) MIDI_Handle->OutEpSize = USBH_MIDI_EPS_OUT_SIZE;
                    MIDI_Handle->output_valid = true;
                }
                
            } // each endpoint, or until ive found both input and output endpoint

            if (MIDI_Handle->poll < MIDI_MIN_POLL) {
                MIDI_Handle->poll = MIDI_MIN_POLL;
            }


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
//    if (isValidOutput(MIDI_Handle) {
//        USB_OTG_HC_Halt(phost, MIDI_Handle->OutPipe);
//        USBH_Free_Channel(phost, MIDI_Handle->OutPipe);
//        MIDI_Handle->OutPipe = 0;
//        MIDI_Handle->output_valid = false;
//    }
//    if (isValidInput(MIDI_Handle) {
//        USB_OTG_HC_Halt(phost, MIDI_Handle->InPipe);
//        USBH_Free_Channel(phost, MIDI_Handle->InPipe);
//        MIDI_Handle->InPipe = 0;
//        MIDI_Handle->input_valid = false;
//    }
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
static USBH_StatusTypeDef USBH_MIDI_ClassRequest(__attribute__((__unused__))  USBH_HandleTypeDef *phost) {
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
//volatile int state_in;
//volatile int state_out;

// PING = 1 for HS, apparently ,but 0 or 1 makes no difference
#define SEND_DATA_DO_PING 0

static USBH_StatusTypeDef USBH_MIDI_Process(USBH_HandleTypeDef *phost) {
    
    USBH_StatusTypeDef status = USBH_OK;
    MIDI_HandleTypeDef *MIDI_Handle = phost->pActiveClass->pData;
    URB_state_in    = USBH_LL_GetURBState(phost, MIDI_Handle->InPipe);
    URB_state_out   = USBH_LL_GetURBState(phost, MIDI_Handle->OutPipe);
//    state_in        = HAL_HCD_GetState(phost->pData, MIDI_Handle->InPipe);
//    state_out       = HAL_HCD_GetState(phost->pData, MIDI_Handle->OutPipe);


//  if (HCD_IsDeviceConnected(pdev)) {
    //appliStatus = pphost->usr_cb->UserApplication(); // this will call USBH_USR_MIDI_Application()

    switch (MIDI_Handle->state_in) {
        case MIDI_INIT:
            MIDI_Handle->state_in = MIDI_GET_DATA;
            break;

        case MIDI_GET_DATA:
            if (URB_state_in == USBH_URB_STALL) {
                USBH_ClrFeature(phost, MIDI_Handle->InEp);
            } else if (URB_state_in == USBH_URB_ERROR) {
                USBH_ClrFeature(phost, MIDI_Handle->InEp);
            }

            USBH_BulkReceiveData(phost, MIDI_Handle->buff_in, MIDI_Handle->InEpSize, MIDI_Handle->InPipe);
            MIDI_Handle->state_in = MIDI_POLL;
            //STM_EVAL_LEDOn(LED_Blue);
            break;

        case MIDI_POLL:
            if (URB_state_in == USBH_URB_DONE) {
                int i;
                
                int n = USBH_LL_GetLastXferSize(phost, MIDI_Handle->InPipe);
                for (i=0; i<n; i+=4) {
                    MIDI_CB(MIDI_Handle->buff_in[0+i],MIDI_Handle->buff_in[1+i],MIDI_Handle->buff_in[2+i],MIDI_Handle->buff_in[3+i]);
                }
                    
                MIDI_Handle->buff_in[1] = 0;
                // MIDI_Handle->state_in = MIDI_IDLE;
                MIDI_Handle->timer = 0;
                
            } else if (URB_state_in == USBH_URB_STALL) {
                if (USBH_ClrFeature(phost, MIDI_Handle->InEp) == USBH_OK) {
                    MIDI_Handle->state_in = MIDI_GET_DATA;
                }
            } // if STALL
            break;

        default:
            break;
    } // case

    switch (MIDI_Handle->state_out)
    {
        case MIDI_INIT:
            MIDI_Handle->state_out = MIDI_SEND_DATA;
            break;
            
        case MIDI_SEND_DATA:
            
            if (URB_state_out == USBH_URB_STALL) {
                USBH_ClrFeature(phost, MIDI_Handle->OutEp);
            } else if (URB_state_out == USBH_URB_ERROR) {
                USBH_ClrFeature(phost, MIDI_Handle->OutEp);
            }
            
            if (send_ring_buffer.read_ptr != send_ring_buffer.write_ptr)
            {
                send_ring_buffer.read_ptr =(send_ring_buffer.read_ptr + 1) % RING_BUFFER_SIZE;
                USBH_DbgLog("USB Host Output sending data @ %i", send_ring_buffer.read_ptr  );

                USBH_DbgLog("USB Host Output sending data : %x, %x, %x %x",
                            send_ring_buffer.event[send_ring_buffer.read_ptr].data[0],
                            send_ring_buffer.event[send_ring_buffer.read_ptr].data[1],
                            send_ring_buffer.event[send_ring_buffer.read_ptr].data[2],
                            send_ring_buffer.event[send_ring_buffer.read_ptr].data[3]);
                
                MIDI_Handle->buff_out[0] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[0];
                MIDI_Handle->buff_out[1] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[1];
                MIDI_Handle->buff_out[2] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[2];
                MIDI_Handle->buff_out[3] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[3];
                
                // later, use MIDI_Handle->OutEpSize
                USBH_BulkSendData(phost, MIDI_Handle->buff_out, 4, MIDI_Handle->OutPipe, SEND_DATA_DO_PING);
                
                // now poll for completion
                MIDI_Handle->state_out = MIDI_POLL;
            }
            
            break;
            
        case MIDI_POLL:
            if(URB_state_out == USBH_URB_DONE) {
                USBH_DbgLog("USB Host Output  URB DONE");
                MIDI_Handle->state_out = MIDI_SEND_DATA;
            }
            else if(URB_state_out == USBH_URB_NOTREADY) {
                USBH_DbgLog("USB Host Output  NOT READY");
                // send again
                USBH_BulkSendData(phost, MIDI_Handle->buff_out, 4, MIDI_Handle->OutPipe, SEND_DATA_DO_PING);
                MIDI_Handle->state_out = MIDI_POLL;
            } else if (URB_state_out == USBH_URB_IDLE) {
                // wait
                USBH_DbgLog("USB Host Output  IDLE");
                MIDI_Handle->state_out = MIDI_SEND_DATA;
            } else if (URB_state_out == USBH_URB_ERROR) {
                // giveup
                USBH_ErrLog("USB Host Output  Error sending data");
                MIDI_Handle->state_out = MIDI_SEND_DATA;
            } else if (URB_state_out == USBH_URB_STALL) {
                // stalled, reset ep
                if (USBH_ClrFeature(phost, MIDI_Handle->OutEp) == USBH_OK) {
                    MIDI_Handle->state_out = MIDI_SEND_DATA;
                    USBH_DbgLog("USB Host Output STALLED and cleared");
                }
                else {
                    USBH_ErrLog("USB Host Output  write pipe stalled unable to clear");
                    MIDI_Handle->state_out = MIDI_SEND_DATA;
                }
            } else {
                // giveup
                USBH_ErrLog("USB Host Output  unknown state sending data %x ", URB_state_out);
                MIDI_Handle->state_out = MIDI_SEND_DATA;
            }
            break;
            
        default:
            break;
    } // case
    
    return status;
}
/*-----------------------------------------------------------------------------------------*/

static USBH_StatusTypeDef USBH_MIDI_SOFProcess(USBH_HandleTypeDef *phost) {
    MIDI_HandleTypeDef *MIDI_Handle =  (MIDI_HandleTypeDef *) phost->pActiveClass->pData;

    // this needs tidying up, we should sync the two state machines.... so send data and get data
    // are called periodically.... but long term we probably want to use a queue,
    if(MIDI_Handle->state_in == MIDI_POLL ) {
        if(( phost->Timer - MIDI_Handle->timer) >= MIDI_Handle->poll) {
            MIDI_Handle->state_in = MIDI_GET_DATA;
            #if (USBH_USE_OS == 1)
            osMessagePutI ( phost->os_event, USBH_URB_EVENT, 0);
            #endif
        }
    }
    return USBH_OK;
}
