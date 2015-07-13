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
 * @file    bulk_usb.c
 * @brief   Bulk USB Driver code.
 *
 * @addtogroup BULK_USB
 * @{
 */

#include "ch.h"
#include "hal.h"
#include "bulk_usb.h"

#if 1 // HAL_USE_BULK_USB || defined(__DOXYGEN__)

/*===========================================================================*/
/* Driver local definitions.                                                 */
/*===========================================================================*/

/*===========================================================================*/
/* Driver exported variables.                                                */
/*===========================================================================*/

/*===========================================================================*/
/* Driver local variables and types.                                         */
/*===========================================================================*/

/*===========================================================================*/
/* Driver local functions.                                                   */
/*===========================================================================*/

/*
 * Interface implementation.
 */

static size_t write(void *ip, const uint8_t *bp, size_t n) {

  return chOQWriteTimeout(&((BulkUSBDriver *)ip)->oqueue, bp,
                          n, TIME_INFINITE);
}

static size_t read(void *ip, uint8_t *bp, size_t n) {

  return chIQReadTimeout(&((BulkUSBDriver *)ip)->iqueue, bp,
                         n, TIME_INFINITE);
}

static msg_t put(void *ip, uint8_t b) {

  return chOQPutTimeout(&((BulkUSBDriver *)ip)->oqueue, b, TIME_INFINITE);
}

static msg_t get(void *ip) {

  return chIQGetTimeout(&((BulkUSBDriver *)ip)->iqueue, TIME_INFINITE);
}

static msg_t putt(void *ip, uint8_t b, systime_t timeout) {

  return chOQPutTimeout(&((BulkUSBDriver *)ip)->oqueue, b, timeout);
}

static msg_t gett(void *ip, systime_t timeout) {

  return chIQGetTimeout(&((BulkUSBDriver *)ip)->iqueue, timeout);
}

static size_t writet(void *ip, const uint8_t *bp, size_t n, systime_t time) {

  return chOQWriteTimeout(&((BulkUSBDriver *)ip)->oqueue, bp, n, time);
}

static size_t readt(void *ip, uint8_t *bp, size_t n, systime_t time) {

  return chIQReadTimeout(&((BulkUSBDriver *)ip)->iqueue, bp, n, time);
}

static const struct BulkUSBDriverVMT vmt = {
  write, read, put, get,
  putt, gett, writet, readt
};

/**
 * @brief   Notification of data removed from the input queue.
 */
static void inotify(GenericQueue *qp) {
  size_t n, maxsize;
  BulkUSBDriver *bdup = chQGetLink(qp);

  /* If the USB driver is not in the appropriate state then transactions
     must not be started.*/
  if ((usbGetDriverStateI(bdup->config->usbp) != USB_ACTIVE) ||
      (bdup->state != BDU_READY))
    return;

  /* If there is in the queue enough space to hold at least one packet and
     a transaction is not yet started then a new transaction is started for
     the available space.*/
  maxsize = bdup->config->usbp->epc[bdup->config->bulk_out]->out_maxsize;
  if (!usbGetReceiveStatusI(bdup->config->usbp, bdup->config->bulk_out) &&
      ((n = chIQGetEmptyI(&bdup->iqueue)) >= maxsize)) {
    chSysUnlock();

    n = (n / maxsize) * maxsize;
    usbPrepareQueuedReceive(bdup->config->usbp,
                            bdup->config->bulk_out,
                            &bdup->iqueue, n);

    chSysLock();
    usbStartReceiveI(bdup->config->usbp, bdup->config->bulk_out);
  }
}

/**
 * @brief   Notification of data inserted into the output queue.
 */
static void onotify(GenericQueue *qp) {
  size_t n;
  BulkUSBDriver *bdup = chQGetLink(qp);

  /* If the USB driver is not in the appropriate state then transactions
     must not be started.*/
  if ((usbGetDriverStateI(bdup->config->usbp) != USB_ACTIVE) ||
      (bdup->state != BDU_READY))
    return;

  /* If there is not an ongoing transaction and the output queue contains
     data then a new transaction is started.*/
  if (!usbGetTransmitStatusI(bdup->config->usbp, bdup->config->bulk_in) &&
      ((n = chOQGetFullI(&bdup->oqueue)) > 0)) {
    chSysUnlock();

    usbPrepareQueuedTransmit(bdup->config->usbp,
                             bdup->config->bulk_in,
                             &bdup->oqueue, n);

    chSysLock();
    usbStartTransmitI(bdup->config->usbp, bdup->config->bulk_in);
  }
}

/*===========================================================================*/
/* Driver exported functions.                                                */
/*===========================================================================*/

/**
 * @brief   Bulk USB Driver initialization.
 * @note    This function is implicitly invoked by @p halInit(), there is
 *          no need to explicitly initialize the driver.
 *
 * @init
 */
void bduInit(void) {
}

/**
 * @brief   Initializes a generic full duplex driver object.
 * @details The HW dependent part of the initialization has to be performed
 *          outside, usually in the hardware initialization code.
 *
 * @param[out] bdup     pointer to a @p BulkUSBDriver structure
 *
 * @init
 */
void bduObjectInit(BulkUSBDriver *bdup) {

  bdup->vmt = &vmt;
  chEvtInit(&bdup->event);
  bdup->state = BDU_STOP;
  chIQInit(&bdup->iqueue, bdup->ib, BULK_USB_BUFFERS_SIZE, inotify, bdup);
  chOQInit(&bdup->oqueue, bdup->ob, BULK_USB_BUFFERS_SIZE, onotify, bdup);
}

/**
 * @brief   Configures and starts the driver.
 *
 * @param[in] bdup      pointer to a @p BulkUSBDriver object
 * @param[in] config    the Bulk USB driver configuration
 *
 * @api
 */
void bduStart(BulkUSBDriver *bdup, const BulkUSBConfig *config) {
  USBDriver *usbp = config->usbp;

  chDbgCheck(bdup != NULL, "bduStart");

  chSysLock();
  chDbgAssert((bdup->state == BDU_STOP) || (bdup->state == BDU_READY),
              "bduStart(), #1",
              "invalid state");
  usbp->in_params[config->bulk_in - 1]   = bdup;
  usbp->out_params[config->bulk_out - 1] = bdup;
  bdup->config = config;
  bdup->state = BDU_READY;
  chSysUnlock();
}

/**
 * @brief   Stops the driver.
 * @details Any thread waiting on the driver's queues will be awakened with
 *          the message @p Q_RESET.
 *
 * @param[in] bdup      pointer to a @p BulkUSBDriver object
 *
 * @api
 */
void bduStop(BulkUSBDriver *bdup) {
  USBDriver *usbp = bdup->config->usbp;

  chDbgCheck(bdup != NULL, "sdStop");

  chSysLock();

  chDbgAssert((bdup->state == BDU_STOP) || (bdup->state == BDU_READY),
              "bduStop(), #1",
              "invalid state");

  /* Driver in stopped state.*/
  usbp->in_params[bdup->config->bulk_in - 1]   = NULL;
  usbp->out_params[bdup->config->bulk_out - 1] = NULL;
  bdup->state = BDU_STOP;

  /* Queues reset in order to signal the driver stop to the application.*/
  chnAddFlagsI(bdup, CHN_DISCONNECTED);
  chIQResetI(&bdup->iqueue);
  chOQResetI(&bdup->oqueue);
  chSchRescheduleS();

  chSysUnlock();
}

/**
 * @brief   USB device configured handler.
 *
 * @param[in] bdup      pointer to a @p BulkUSBDriver object
 *
 * @iclass
 */
void bduConfigureHookI(BulkUSBDriver *bdup) {
  USBDriver *usbp = bdup->config->usbp;

  chIQResetI(&bdup->iqueue);
  chOQResetI(&bdup->oqueue);
  chnAddFlagsI(bdup, CHN_CONNECTED);

  /* Starts the first OUT transaction immediately.*/
  usbPrepareQueuedReceive(usbp, bdup->config->bulk_out, &bdup->iqueue,
                          usbp->epc[bdup->config->bulk_out]->out_maxsize);
  usbStartReceiveI(usbp, bdup->config->bulk_out);
}

/**
 * @brief   Default requests hook.
 * @details Applications wanting to use the Bulk USB driver can use
 *          this function as requests hook in the USB configuration.
 *          The following requests are emulated:
 *          - CDC_GET_LINE_CODING.
 *          - CDC_SET_LINE_CODING.
 *          - CDC_SET_CONTROL_LINE_STATE.
 *          .
 *
 * @param[in] usbp      pointer to the @p USBDriver object
 * @return              The hook status.
 * @retval TRUE         Message handled internally.
 * @retval FALSE        Message not handled.
 */
bool_t bduRequestsHook(USBDriver *usbp) {

  (void)usbp;
  return FALSE;
}

/**
 * @brief   Default data transmitted callback.
 * @details The application must use this function as callback for the IN
 *          data endpoint.
 *
 * @param[in] usbp      pointer to the @p USBDriver object
 * @param[in] ep        endpoint number
 */
void bduDataTransmitted(USBDriver *usbp, usbep_t ep) {
  size_t n;
  BulkUSBDriver *bdup = usbp->in_params[ep - 1];

  if (bdup == NULL)
    return;

  chSysLockFromIsr();
  chnAddFlagsI(bdup, CHN_OUTPUT_EMPTY);

  if ((n = chOQGetFullI(&bdup->oqueue)) > 0) {
    /* The endpoint cannot be busy, we are in the context of the callback,
       so it is safe to transmit without a check.*/
    chSysUnlockFromIsr();

    usbPrepareQueuedTransmit(usbp, ep, &bdup->oqueue, n);

    chSysLockFromIsr();
    usbStartTransmitI(usbp, ep);
  }
  else if ((usbp->epc[ep]->in_state->txsize > 0) &&
           !(usbp->epc[ep]->in_state->txsize &
             (usbp->epc[ep]->in_maxsize - 1))) {
    /* Transmit zero sized packet in case the last one has maximum allowed
       size. Otherwise the recipient may expect more data coming soon and
       not return buffered data to app. See section 5.8.3 Bulk Transfer
       Packet Size Constraints of the USB Specification document.*/
    chSysUnlockFromIsr();

    usbPrepareQueuedTransmit(usbp, ep, &bdup->oqueue, 0);

    chSysLockFromIsr();
    usbStartTransmitI(usbp, ep);
  }

  chSysUnlockFromIsr();
}

/**
 * @brief   Default data received callback.
 * @details The application must use this function as callback for the OUT
 *          data endpoint.
 *
 * @param[in] usbp      pointer to the @p USBDriver object
 * @param[in] ep        endpoint number
 */
void bduDataReceived(USBDriver *usbp, usbep_t ep) {
  size_t n, maxsize;
  BulkUSBDriver *bdup = usbp->out_params[ep - 1];

  if (bdup == NULL)
    return;

  chSysLockFromIsr();
  chnAddFlagsI(bdup, CHN_INPUT_AVAILABLE);

  /* Writes to the input queue can only happen when there is enough space
     to hold at least one packet.*/
  maxsize = usbp->epc[ep]->out_maxsize;
  if ((n = chIQGetEmptyI(&bdup->iqueue)) >= maxsize) {
    /* The endpoint cannot be busy, we are in the context of the callback,
       so a packet is in the buffer for sure.*/
    chSysUnlockFromIsr();

    n = (n / maxsize) * maxsize;
    usbPrepareQueuedReceive(usbp, ep, &bdup->iqueue, n);

    chSysLockFromIsr();
    usbStartReceiveI(usbp, ep);
  }

  chSysUnlockFromIsr();
}

#endif /* HAL_USE_BULK_USB */

/** @} */
