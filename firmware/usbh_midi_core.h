/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * (based on work by Xavier Halgand)
 */

/* Define to prevent recursive  ----------------------------------------------*/
#ifndef __USBH_MIDI_CORE_H
#define __USBH_MIDI_CORE_H

/* Includes ------------------------------------------------------------------*/
#include "usbh_core.h"
//#include "usbh_stdreq.h"
//#include "usb_bsp.h"
#include "usbh_ioreq.h"
#include "usbh_def.h"
//#include "usbh_hcs.h"
//#include "usbh_usr.h"
//#include "midi_interface.h"


// external midi interface
void usbh_midi_init(void);
void usbh_midi_reset_buffer(void);
void usbh_MidiSend1(uint8_t port, uint8_t b0);
void usbh_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1);
void usbh_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2);
void usbh_MidiSendSysEx(uint8_t port, uint8_t bytes[], uint8_t len);

int  usbh_MidiGetOutputBufferPending(void);
int  usbh_MidiGetOutputBufferAvailable(void);

//#define MIDI_MIN_POLL          10
#define USBH_MIDI_EPS_IN_SIZE  64
#define USBH_MIDI_EPS_OUT_SIZE 64
#define USB_AUDIO_CLASS 0x01
#define USB_MIDISTREAMING_SubCLASS 0x03

extern USBH_ClassTypeDef  MIDI_Class;
#define USBH_MIDI_CLASS    &MIDI_Class


/******************************************************************************/
/* States for MIDI State Machine */
typedef enum {
  MIDI_INIT = 0,
  MIDI_IDLE,
  MIDI_SEND_DATA,
  MIDI_BUSY,
  MIDI_GET_DATA,
  MIDI_POLL,
  MIDI_RETRY,
  MIDI_ERROR
} MIDI_State_t;

/******************************************************************************/
typedef struct MIDI_cb {
  void (*Init)(void);
  void (*Decode)(uint8_t *data);
} MIDI_cb_t;

/**************************************************************************/

typedef struct _MIDIDescriptor {
  uint8_t bLength;
  uint8_t bDescriptorType;
  uint16_t bcdHID; /* indicates what endpoint this descriptor is describing */
  uint8_t bCountryCode; /* specifies the transfer type. */
  uint8_t bNumDescriptors; /* specifies the transfer type. */
  uint8_t bReportDescriptorType; /* Maximum Packet Size this endpoint is capable of sending or receiving */
  uint16_t wItemLength; /* is used to specify the polling interval of certain transfers. */
} USBH_MIDIDesc_t;

/******************************************************************************/
/** \brief MIDI Class Driver Event Packet.
 *
 *  Type define for a USB MIDI event packet, used to encapsulate sent and received MIDI messages from a USB MIDI interface.
 *
 *  \note Regardless of CPU architecture, these values should be stored as little endian.
 */
typedef struct {
  uint8_t Event; /**< MIDI event type, constructed with the \ref MIDI_EVENT() macro. */

  uint8_t Data1; /**< First byte of data in the MIDI event. */
  uint8_t Data2; /**< Second byte of data in the MIDI event. */
  uint8_t Data3; /**< Third byte of data in the MIDI event. */
} MIDI_EventPacket_t;

/******************************************************************************/
/* Structure for MIDI process */
typedef struct _MIDI_Process {
  uint8_t OutPipe;
  uint8_t InPipe;
  uint8_t OutEp;
  uint8_t InEp;
  uint16_t OutEpSize;
  uint16_t InEpSize;
  MIDI_State_t state_in;
  MIDI_State_t state_out;
  bool input_valid;
  bool output_valid;

  uint8_t buff_in[USBH_MIDI_EPS_IN_SIZE];
  uint8_t buff_out[USBH_MIDI_EPS_OUT_SIZE];
  uint8_t buff_out_len;
//  uint16_t             length;
//  uint8_t              ep_addr;
//  uint16_t             read_poll;
//  uint32_t             read_timer;
//  uint16_t             write_poll;
//  uint32_t             write_timer;
//  uint8_t              DataReady;
//  USBH_MIDIDesc_t      HID_Desc;
  USBH_StatusTypeDef  ( * Init)(USBH_HandleTypeDef *phost);
} MIDI_HandleTypeDef;

/******************************************************************************/

typedef void (*USBH_Class_cb_TypeDef)(uint8_t, uint8_t, uint8_t);

extern USBH_Class_cb_TypeDef MIDI_cb;

extern void MIDI_CB(uint8_t a,uint8_t b,uint8_t c,uint8_t d);

//uint8_t MIDI_RcvData(uint8_t *outBuf);

typedef USBH_HandleTypeDef USB_OTG_CORE_HANDLE;

USBH_StatusTypeDef USBH_MIDI_InterfaceInit  (USBH_HandleTypeDef *phost);
USBH_StatusTypeDef USBH_MIDI_InterfaceDeInit  (USBH_HandleTypeDef *phost);
USBH_StatusTypeDef USBH_MIDI_ClassRequest(USBH_HandleTypeDef *phost);
USBH_StatusTypeDef USBH_MIDI_Process(USBH_HandleTypeDef *phost);
USBH_StatusTypeDef USBH_MIDI_SOFProcess(USBH_HandleTypeDef *phost);


bool isValidInput(MIDI_HandleTypeDef* pH);
bool isValidOutput(MIDI_HandleTypeDef* pH);


#endif /* __USBH_MIDI_CORE_H */

/************************ ****************** *****END OF FILE****/

