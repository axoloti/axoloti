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
 * @file    bulk_usb.h
 * @brief   Bulk USB Driver macros and structures.
 *
 * @addtogroup BULK_USB
 * @{
 */

#ifndef _BULK_USB_H_
#define _BULK_USB_H_

#if 1 //HAL_USE_BULK_USB || defined(__DOXYGEN__)

/*===========================================================================*/
/* Driver constants.                                                         */
/*===========================================================================*/


/*===========================================================================*/
/* Driver pre-compile time settings.                                         */
/*===========================================================================*/

/**
 * @name    BULK_USB configuration options
 * @{
 */
/**
 * @brief   Bulk USB buffers size.
 * @details Configuration parameter, the buffer size must be a multiple of
 *          the USB data endpoint maximum packet size.
 * @note    The default is 256 bytes for both the transmission and receive
 *          buffers.
 */
#if !defined(BULK_USB_BUFFERS_SIZE) || defined(__DOXYGEN__)
#define BULK_USB_BUFFERS_SIZE     256
#endif
/** @} */

/*===========================================================================*/
/* Derived constants and error checks.                                       */
/*===========================================================================*/

#if !HAL_USE_USB || !CH_USE_QUEUES || !CH_USE_EVENTS
#error "Bulk USB Driver requires HAL_USE_USB, CH_USE_QUEUES, "
       "CH_USE_EVENTS"
#endif

/*===========================================================================*/
/* Driver data structures and types.                                         */
/*===========================================================================*/

/**
 * @brief Driver state machine possible states.
 */
typedef enum {
  BDU_UNINIT = 0,                   /**< Not initialized.                   */
  BDU_STOP = 1,                     /**< Stopped.                           */
  BDU_READY = 2                     /**< Ready.                             */
} bdustate_t;

/**
 * @brief   Structure representing a bulk USB driver.
 */
typedef struct BulkUSBDriver BulkUSBDriver;

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
} BulkUSBConfig;

/**
 * @brief   @p BulkUSBDriver specific data.
 */
#define _bulk_usb_driver_data                                             \
  _base_asynchronous_channel_data                                           \
  /* Driver state.*/                                                        \
  bdustate_t                state;                                          \
  /* Input queue.*/                                                         \
  InputQueue                iqueue;                                         \
  /* Output queue.*/                                                        \
  OutputQueue               oqueue;                                         \
  /* Input buffer.*/                                                        \
  uint8_t                   ib[BULK_USB_BUFFERS_SIZE];                    \
  /* Output buffer.*/                                                       \
  uint8_t                   ob[BULK_USB_BUFFERS_SIZE];                    \
  /* End of the mandatory fields.*/                                         \
  /* Current configuration data.*/                                          \
  const BulkUSBConfig     *config;

/**
 * @brief   @p BulkUSBDriver specific methods.
 */
#define _bulk_usb_driver_methods                                          \
  _base_asynchronous_channel_methods

/**
 * @extends BaseAsynchronousChannelVMT
 *
 * @brief   @p BulkUSBDriver virtual methods table.
 */
struct BulkUSBDriverVMT {
  _bulk_usb_driver_methods
};

/**
 * @extends BaseAsynchronousChannel
 *
 * @brief   Full duplex serial driver class.
 * @details This class extends @p BaseAsynchronousChannel by adding physical
 *          I/O queues.
 */
struct BulkUSBDriver {
  /** @brief Virtual Methods Table.*/
  const struct BulkUSBDriverVMT *vmt;
  _bulk_usb_driver_data
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
  void bduInit(void);
  void bduObjectInit(BulkUSBDriver *sdp);
  void bduStart(BulkUSBDriver *bdup, const BulkUSBConfig *config);
  void bduStop(BulkUSBDriver *bdup);
  void bduConfigureHookI(BulkUSBDriver *bdup);
  bool_t bduRequestsHook(USBDriver *usbp);
  void bduDataTransmitted(USBDriver *usbp, usbep_t ep);
  void bduDataReceived(USBDriver *usbp, usbep_t ep);
#ifdef __cplusplus
}
#endif

#endif /* HAL_USE_BULK_USB */

#endif /* _BULK_USB_H_ */

/** @} */
