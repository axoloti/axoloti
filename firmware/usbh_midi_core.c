/**
 ******************************************************************************
 * @file    usbh_midi_core.c
 * @author  Johannes Taelman (based on work by Xavier Halgand)
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
#include "hal.h"
#include "axoloti_board.h"
#include "exceptions.h"

USB_Setup_TypeDef MIDI_Setup;

#define MIDI_MIN_READ_POLL 1
#define MIDI_MIN_WRITE_POLL 1

#define RING_BUFFER_SIZE 64

typedef struct
{
    uint8_t data[4];
} MIDIEvent_t;


// very simple ring buffer
// Note: this should be moved into main midi structure. (hub support will need this) 
static struct {
    MIDIEvent_t event[RING_BUFFER_SIZE];
    volatile int8_t read_ptr;
    volatile int8_t write_ptr;
} send_ring_buffer;


void usbh_midi_init_buffer(void) {
  // make no bytes available for output, initialise will reset
  send_ring_buffer.read_ptr  = 0;
  send_ring_buffer.write_ptr = RING_BUFFER_SIZE - 1;
}

void usbh_midi_deinit_buffer(void) {
  send_ring_buffer.read_ptr  = 0;
  send_ring_buffer.write_ptr = -1;
}


void usbh_midi_init(void)
{
  usbh_midi_deinit_buffer();
}

void usbh_midi_reset_buffer(void) {
    send_ring_buffer.read_ptr = send_ring_buffer.write_ptr = 0;
}


// CIN for everyting except sysex
inline uint8_t calcCIN(uint8_t b0) {
    return (b0 & 0xF0 ) >> 4;
    
}

// pack header CN | CIN
inline uint8_t calcPH(uint8_t port, uint8_t b0) {
    uint8_t cin  = calcCIN(b0);
    uint8_t ph = ((( port - 1) & 0x0F) << 4)  | cin;
    return ph;
}


void usbh_MidiSend1(uint8_t port, uint8_t b0) {
    if (send_ring_buffer.write_ptr + 1 == 0) return;
    USBH_DbgLog("usbh_MidiSend1");
    uint8_t next = (send_ring_buffer.write_ptr + 1) % RING_BUFFER_SIZE;
    
    if(next == send_ring_buffer.read_ptr) {
        report_usbh_midi_ringbuffer_overflow();
        return;
    }
    
    send_ring_buffer.event[next].data[0]=calcPH(port, b0);
    send_ring_buffer.event[next].data[1]=b0;
    send_ring_buffer.event[next].data[2]=0;
    send_ring_buffer.event[next].data[3]=0;
    send_ring_buffer.write_ptr=next;
}

void usbh_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1) {
    if (send_ring_buffer.write_ptr + 1 == 0) return;
    USBH_DbgLog("usbh_MidiSend2");
    uint8_t next = (send_ring_buffer.write_ptr + 1) % RING_BUFFER_SIZE;
    
    if(next == send_ring_buffer.read_ptr) {
        report_usbh_midi_ringbuffer_overflow();
        return;
    }
    
    send_ring_buffer.event[next].data[0]=calcPH(port, b0);
    send_ring_buffer.event[next].data[1]=b0;
    send_ring_buffer.event[next].data[2]=b1;
    send_ring_buffer.event[next].data[3]=0;
    send_ring_buffer.write_ptr=next;
}

void usbh_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
    if (send_ring_buffer.write_ptr + 1 == 0) return;
    USBH_DbgLog("usbh_MidiSend3");
    uint8_t next = (send_ring_buffer.write_ptr + 1) % RING_BUFFER_SIZE;
    
    if(next == send_ring_buffer.read_ptr) {
        report_usbh_midi_ringbuffer_overflow();
        return;
    }
    
    send_ring_buffer.event[next].data[0]=calcPH(port, b0);
    send_ring_buffer.event[next].data[1]=b0;
    send_ring_buffer.event[next].data[2]=b1;
    send_ring_buffer.event[next].data[3]=b2;
    send_ring_buffer.write_ptr=next;
}

#define CIN_SYSEX_START 0x04
#define CIN_SYSEX_END_1 0x05
#define CIN_SYSEX_END_2 0x06
#define CIN_SYSEX_END_3 0x07

void usbh_MidiSendSysEx(uint8_t port, uint8_t bytes[], uint8_t len) {
    if (send_ring_buffer.write_ptr + 1 == 0) return;
    USBH_DbgLog("usbh_MidiSysEx %i",len);
    uint8_t next = send_ring_buffer.write_ptr;

    uint8_t cn = ((( port - 1) & 0x0F) << 4);
    uint8_t cin = CIN_SYSEX_START;
    uint8_t ph = cin | cn;
    int i = 0;
    for(i = 0; i< (len - 3); i += 3) {
        next = (next + 1) % RING_BUFFER_SIZE;
        // later do this up front... but read_ptr may be changing
        if(next == send_ring_buffer.read_ptr) {
            report_usbh_midi_ringbuffer_overflow();
            return;
        }

        USBH_DbgLog("usbh_MidiSysEx start %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = bytes[i + 2];
    }

    int res = len - i;

    // end the sysex message, also handles special cases 2/3 bytes
    next = (next + 1) % RING_BUFFER_SIZE;
    if(next == send_ring_buffer.read_ptr) {
        report_usbh_midi_ringbuffer_overflow();
        return;
    }

    if (res == 1) {
        cin = CIN_SYSEX_END_1;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 1 %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = 0;
        send_ring_buffer.event[next].data[3] = 0;
    } else if (res == 2) {
        cin = CIN_SYSEX_END_2;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 2 %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = 0;
    } else if (res == 3) {
        cin = CIN_SYSEX_END_3;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 3 %i, %i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = bytes[i + 2];
    }

    send_ring_buffer.write_ptr=next;
}

int  usbh_MidiGetOutputBufferPending(void) {

    if(send_ring_buffer.write_ptr >= send_ring_buffer.read_ptr) {
        return send_ring_buffer.write_ptr - send_ring_buffer.read_ptr;
    }

    return send_ring_buffer.write_ptr + RING_BUFFER_SIZE - send_ring_buffer.read_ptr;
}

int  usbh_MidiGetOutputBufferAvailable(void) {
    return RING_BUFFER_SIZE - usbh_MidiGetOutputBufferPending() - 1;
}

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
    usbh_midi_init_buffer();
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
                    MIDI_Handle->InEp = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bEndpointAddress;
                    MIDI_Handle->InEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].wMaxPacketSize;
                    USBH_UsrLog("USB Host Input size requests : %x", MIDI_Handle->InEpSize );
                    MIDI_Handle->InEpSize = USBH_MIDI_EPS_IN_SIZE; // why bother reducing the size? Some devices will lie about the max ep size...
//                    MIDI_Handle->read_poll = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bInterval;
//                  USBH_UsrLog("USB Host Input interval : %i", MIDI_Handle->read_poll);
//                    if(MIDI_Handle->read_poll<MIDI_MIN_READ_POLL) MIDI_Handle->read_poll = MIDI_MIN_READ_POLL;
                    MIDI_Handle->input_valid = true;
                }
                if(!isValidOutput(MIDI_Handle) && !bInput) {
                    MIDI_Handle->OutEp = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bEndpointAddress;
                    MIDI_Handle->OutEpSize  = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].wMaxPacketSize;
                    USBH_UsrLog("USB Host Output size requests : %x", MIDI_Handle->OutEpSize );
                    if(MIDI_Handle->OutEpSize >USBH_MIDI_EPS_OUT_SIZE) MIDI_Handle->OutEpSize = USBH_MIDI_EPS_OUT_SIZE;
//                    MIDI_Handle->write_poll = phost->device.CfgDesc.Itf_Desc[phost->device.current_interface].Ep_Desc[i].bInterval;
//                  USBH_UsrLog("USB Host Output interval : %i", MIDI_Handle->write_poll);
//                    if(MIDI_Handle->write_poll<MIDI_MIN_WRITE_POLL) MIDI_Handle->write_poll = MIDI_MIN_WRITE_POLL;
                    MIDI_Handle->output_valid = true;
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
                usbh_midi_reset_buffer();
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

    usbh_midi_deinit_buffer();

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
            MIDI_Handle->state_in = MIDI_GET_DATA;
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
                int i;
                
                int n = USBH_LL_GetLastXferSize(phost, MIDI_Handle->InPipe);
                for (i=0; i<n; i+=4) {
                    if (MIDI_Handle->buff_in[0+i]) {
                        MIDI_CB(MIDI_Handle->buff_in[0+i],MIDI_Handle->buff_in[1+i],MIDI_Handle->buff_in[2+i],MIDI_Handle->buff_in[3+i]);
                //  USBH_DbgLog("USB Host Input recv data : %x, %x, %x %x",
                //              MIDI_Handle->buff_in[0+i],MIDI_Handle->buff_in[1+i],MIDI_Handle->buff_in[2+i],MIDI_Handle->buff_in[3+i]);
                    }
                }
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

                int i;

                int n = USBH_LL_GetLastXferSize(phost, MIDI_Handle->InPipe);
                for (i=0; i<n; i+=4) {
                    if (MIDI_Handle->buff_in[0+i]) {
                        MIDI_CB(MIDI_Handle->buff_in[0+i],MIDI_Handle->buff_in[1+i],MIDI_Handle->buff_in[2+i],MIDI_Handle->buff_in[3+i]);
                //  USBH_DbgLog("USB Host Input recv data : %x, %x, %x %x",
                //              MIDI_Handle->buff_in[0+i],MIDI_Handle->buff_in[1+i],MIDI_Handle->buff_in[2+i],MIDI_Handle->buff_in[3+i]);
                    }
                }


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
            if (send_ring_buffer.read_ptr != send_ring_buffer.write_ptr) {
                MIDI_Handle->buff_out_len = 0;
                while (send_ring_buffer.read_ptr != send_ring_buffer.write_ptr
                        && MIDI_Handle->buff_out_len + 4 <= MIDI_Handle->OutEpSize ) {

                        send_ring_buffer.read_ptr =(send_ring_buffer.read_ptr + 1) % RING_BUFFER_SIZE;
                        USBH_DbgLog("USB Host Output sending data @ %i", send_ring_buffer.read_ptr  );

#if 0
                        USBH_DbgLog("USB Host Output sending data : %x, %x, %x %x",
                                    send_ring_buffer.event[send_ring_buffer.read_ptr].data[0],
                                    send_ring_buffer.event[send_ring_buffer.read_ptr].data[1],
                                    send_ring_buffer.event[send_ring_buffer.read_ptr].data[2],
                                    send_ring_buffer.event[send_ring_buffer.read_ptr].data[3]);
#endif

                        MIDI_Handle->buff_out[MIDI_Handle->buff_out_len + 0] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[0];
                        MIDI_Handle->buff_out[MIDI_Handle->buff_out_len + 1] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[1];
                        MIDI_Handle->buff_out[MIDI_Handle->buff_out_len + 2] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[2];
                        MIDI_Handle->buff_out[MIDI_Handle->buff_out_len + 3] =  send_ring_buffer.event[send_ring_buffer.read_ptr].data[3];
                        MIDI_Handle->buff_out_len += 4;
                }

                USBH_BulkSendData(phost, MIDI_Handle->buff_out, MIDI_Handle->buff_out_len, MIDI_Handle->OutPipe, SEND_DATA_DO_PING);
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
        
//        if (( phost->Timer - MIDI_Handle->write_timer) >= MIDI_Handle->write_poll
//              || phost->Timer < MIDI_Handle->write_timer) {
            if (send_ring_buffer.read_ptr != send_ring_buffer.write_ptr) {
                // ready to send more data, and we have data to send
                #if (USBH_USE_OS == 1)
                osMessagePutI ( phost->os_event, USBH_URB_EVENT, 0);
                #endif
            }
//        }
    } else if ((MIDI_Handle->state_out == MIDI_RETRY)||
        (MIDI_Handle->state_in == MIDI_RETRY)) {
      #if (USBH_USE_OS == 1)
      osMessagePutI ( phost->os_event, USBH_URB_EVENT, 0);
      #endif
    }
    return USBH_OK;
}
