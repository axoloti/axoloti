/*
    ChibiOS/RT - Copyright (C) 2006,2007,2008,2009,2010,
                 2011,2012,2013 Giovanni Di Sirio.

    This file is part of ChibiOS/RT.

    ChibiOS/RT is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    ChibiOS/RT is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * @file    midi_usb.h
 * @brief   MIDI USB Driver macros and structures.
 *
 * @addtogroup MIDI_USB
 * @{
 */

#ifndef _MIDI_USB_H_
#define _MIDI_USB_H_

#include "midi.h"

#if 1 //HAL_USE_MIDI_USB || defined(__DOXYGEN__)

/*===========================================================================*/
/* Driver constants.                                                         */
/*===========================================================================*/


/*===========================================================================*/
/* Driver pre-compile time settings.                                         */
/*===========================================================================*/

/**
 * @name    MIDI_USB configuration options
 * @{
 */
/**
 * @brief   Midi USB buffers size.
 * @details Configuration parameter, the buffer size must be a multiple of
 *          the USB data endpoint maximum packet size.
 * @note    The default is 64 bytes for both the transmission and receive
 *          buffers.
 */
#if !defined(MIDI_USB_BUFFERS_SIZE) || defined(__DOXYGEN__)
#define MIDI_USB_BUFFERS_SIZE     64
#endif
/** @} */

/*===========================================================================*/
/* Derived constants and error checks.                                       */
/*===========================================================================*/

#if !HAL_USE_USB || !CH_USE_QUEUES || !CH_USE_EVENTS
#error "Midi USB Driver requires HAL_USE_USB, CH_USE_QUEUES, "
       "CH_USE_EVENTS"
#endif

/*===========================================================================*/
/* Driver data structures and types.                                         */
/*===========================================================================*/

/**
 * @brief Driver state machine possible states.
 */
typedef enum {
  MDU_UNINIT = 0,                   /**< Not initialized.                   */
  MDU_STOP = 1,                     /**< Stopped.                           */
  MDU_READY = 2                     /**< Ready.                             */
} mdustate_t;

/**
 * @brief   Structure representing a bulk USB driver.
 */
typedef struct MidiUSBDriver MidiUSBDriver;

/**
 * @brief   Bulk USB Driver configuration structure.
 * @details An instance of this structure must be passed to @p bduStart()
 *          in order to configure and start the driver operations.
 */
typedef struct {
  /**
   * @brief   USB driver to use.
   */
  USBDriver                 *usbp;
  /**
   * @brief   Bulk IN endpoint used for outgoing data transfer.
   */
  usbep_t                   bulk_in;
  /**
   * @brief   Bulk OUT endpoint used for incoming data transfer.
   */
  usbep_t                   bulk_out;
} MidiUSBConfig;

/**
 * @brief   @p MidiUSBDriver specific data.
 */
#define _midi_usb_driver_data                                             \
  _base_asynchronous_channel_data                                           \
  /* Driver state.*/                                                        \
  mdustate_t                state;                                          \
  /* Input queue.*/                                                         \
  InputQueue                iqueue;                                         \
  /* Output queue.*/                                                        \
  OutputQueue               oqueue;                                         \
  /* Input buffer.*/                                                        \
  uint8_t                   ib[MIDI_USB_BUFFERS_SIZE];                    \
  /* Output buffer.*/                                                       \
  uint8_t                   ob[MIDI_USB_BUFFERS_SIZE];                    \
  /* End of the mandatory fields.*/                                         \
  /* Current configuration data.*/                                          \
  const MidiUSBConfig     *config;

/**
 * @brief   @p BulkUSBDriver specific methods.
 */
#define _midi_usb_driver_methods                                          \
  _base_asynchronous_channel_methods

/**
 * @extends BaseAsynchronousChannelVMT
 *
 * @brief   @p BulkUSBDriver virtual methods table.
 */
struct MidiUSBDriverVMT {
  _midi_usb_driver_methods
};

/**
 * @extends BaseAsynchronousChannel
 *
 * @brief   Full duplex serial driver class.
 * @details This class extends @p BaseAsynchronousChannel by adding physical
 *          I/O queues.
 */
struct MidiUSBDriver {
  /** @brief Virtual Methods Table.*/
  const struct MidiUSBDriverVMT *vmt;
  _midi_usb_driver_data
};

/*===========================================================================*/
/* Driver macros.                                                            */
/*===========================================================================*/

/*===========================================================================*/
/* External declarations.                                                    */
/*===========================================================================*/

#ifdef __cplusplus
extern "C" {
#endif
  void mduInit(void);
  void mduObjectInit(MidiUSBDriver *sdp);
  void mduStart(MidiUSBDriver *mdup, const MidiUSBConfig *config);
  void mduStop(MidiUSBDriver *mdup);
  void mduConfigureHookI(MidiUSBDriver *bdup);
  bool_t mduRequestsHook(USBDriver *usbp);
  void mduDataTransmitted(USBDriver *usbp, usbep_t ep);
  void mduDataReceived(USBDriver *usbp, usbep_t ep);

  void midi_usb_MidiSend1(uint8_t port, uint8_t b0);
  void midi_usb_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1);
  void midi_usb_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2);
#ifdef __cplusplus
}
#endif

#endif /* HAL_USE_MIDI_USB */

#endif /* _MIDI_USB_H_ */

/** @} */
