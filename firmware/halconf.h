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
 * @file    templates/halconf.h
 * @brief   HAL configuration header.
 * @details HAL configuration file, this file allows to enable or disable the
 *          various device drivers from your application. You may also use
 *          this file in order to override the device drivers default settings.
 *
 * @addtogroup HAL_CONF
 * @{
 */

#ifndef HALCONF_H
#define HALCONF_H

#define _CHIBIOS_HAL_CONF_
#define _CHIBIOS_HAL_CONF_VER_7_0_

#include "mcuconf.h"

/**
 * @brief   Enables the TM subsystem.
 */
#if !defined(HAL_USE_TM) || defined(__DOXYGEN__)
#define HAL_USE_TM                          TRUE
#endif

/**
 * @brief   Enables the PAL subsystem.
 */
#if !defined(HAL_USE_PAL) || defined(__DOXYGEN__)
#define HAL_USE_PAL                         TRUE
#endif

/**
 * @brief   Enables the ADC subsystem.
 */
#if !defined(HAL_USE_ADC) || defined(__DOXYGEN__)
#define HAL_USE_ADC                         TRUE
#endif

/**
 * @brief   Enables the CAN subsystem.
 */
#if !defined(HAL_USE_CAN) || defined(__DOXYGEN__)
#define HAL_USE_CAN                         FALSE
#endif

/**
 * @brief   Enables the cryptographic subsystem.
 */
#if !defined(HAL_USE_CRY) || defined(__DOXYGEN__)
#define HAL_USE_CRY                         FALSE
#endif

/**
 * @brief   Enables the DAC subsystem.
 */
#if !defined(HAL_USE_DAC) || defined(__DOXYGEN__)
#define HAL_USE_DAC                         FALSE
#endif

/**
 * @brief   Enables the GPT subsystem.
 */
#if !defined(HAL_USE_GPT) || defined(__DOXYGEN__)
#define HAL_USE_GPT                         FALSE
#endif

/**
 * @brief   Enables the I2C subsystem.
 */
#if !defined(HAL_USE_I2C) || defined(__DOXYGEN__)
#define HAL_USE_I2C                         TRUE
#endif

/**
 * @brief   Enables the I2S subsystem.
 */
#if !defined(HAL_USE_I2S) || defined(__DOXYGEN__)
#define HAL_USE_I2S                         FALSE
#endif

/**
 * @brief   Enables the ICU subsystem.
 */
#if !defined(HAL_USE_ICU) || defined(__DOXYGEN__)
#define HAL_USE_ICU                         FALSE
#endif

/**
 * @brief   Enables the MAC subsystem.
 */
#if !defined(HAL_USE_MAC) || defined(__DOXYGEN__)
#define HAL_USE_MAC                         FALSE
#endif

/**
 * @brief   Enables the MMC_SPI subsystem.
 */
#if !defined(HAL_USE_MMC_SPI) || defined(__DOXYGEN__)
#define HAL_USE_MMC_SPI                     FALSE
#endif

/**
 * @brief   Enables the PWM subsystem.
 */
#if !defined(HAL_USE_PWM) || defined(__DOXYGEN__)
#define HAL_USE_PWM                         TRUE
#endif

/**
 * @brief   Enables the RTC subsystem.
 */
#if !defined(HAL_USE_RTC) || defined(__DOXYGEN__)
#define HAL_USE_RTC                         FALSE
#endif

/**
 * @brief   Enables the SDC subsystem.
 */
#if !defined(HAL_USE_SDC) || defined(__DOXYGEN__)
#define HAL_USE_SDC                         TRUE
#endif

/**
 * @brief   Enables the SERIAL subsystem.
 */
#if !defined(HAL_USE_SERIAL) || defined(__DOXYGEN__)
#define HAL_USE_SERIAL                      TRUE
#endif

/**
 * @brief   Enables the SERIAL over USB subsystem.
 */
#if !defined(HAL_USE_SERIAL_USB) || defined(__DOXYGEN__)
#define HAL_USE_SERIAL_USB                  FALSE
#endif

/**
 * @brief   Enables the SIO subsystem.
 */
#if !defined(HAL_USE_SIO) || defined(__DOXYGEN__)
#define HAL_USE_SIO                         FALSE
#endif

/**
 * @brief   Enables the SPI subsystem.
 */
#if !defined(HAL_USE_SPI) || defined(__DOXYGEN__)
#define HAL_USE_SPI                         TRUE
#endif

/**
 * @brief   Enables the TRNG subsystem.
 */
#if !defined(HAL_USE_TRNG) || defined(__DOXYGEN__)
#define HAL_USE_TRNG                        FALSE
#endif

/**
 * @brief   Enables the UART subsystem.
 */
#if !defined(HAL_USE_UART) || defined(__DOXYGEN__)
#define HAL_USE_UART                        FALSE
#endif

/**
 * @brief   Enables the USB subsystem.
 */
#if !defined(HAL_USE_USB) || defined(__DOXYGEN__)
#define HAL_USE_USB                         TRUE
#endif

/*===========================================================================*/
/* PAL driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Enables synchronous APIs.
 * @note    Disabling this option saves both code and data space.
 */
#if !defined(PAL_USE_CALLBACKS) || defined(__DOXYGEN__)
#define PAL_USE_CALLBACKS                   FALSE
#endif

/**
 * @brief   Enables synchronous APIs.
 * @note    Disabling this option saves both code and data space.
 */
#if !defined(PAL_USE_WAIT) || defined(__DOXYGEN__)
#define PAL_USE_WAIT                        FALSE
#endif

/*===========================================================================*/
/* ADC driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Enables synchronous APIs.
 * @note    Disabling this option saves both code and data space.
 */
#if !defined(ADC_USE_WAIT) || defined(__DOXYGEN__)
#define ADC_USE_WAIT                        TRUE
#endif

/**
 * @brief   Enables the @p adcAcquireBus() and @p adcReleaseBus() APIs.
 * @note    Disabling this option saves both code and data space.
 */
#if !defined(ADC_USE_MUTUAL_EXCLUSION) || defined(__DOXYGEN__)
#define ADC_USE_MUTUAL_EXCLUSION            TRUE
#endif

/*===========================================================================*/
/* CAN driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Sleep mode related APIs inclusion switch.
 */
#if !defined(CAN_USE_SLEEP_MODE) || defined(__DOXYGEN__)
#define CAN_USE_SLEEP_MODE                  TRUE
#endif

/**
 * @brief   Enforces the driver to use direct callbacks rather than OSAL events.
 */
#if !defined(CAN_ENFORCE_USE_CALLBACKS) || defined(__DOXYGEN__)
#define CAN_ENFORCE_USE_CALLBACKS           FALSE
#endif

/*===========================================================================*/
/* I2C driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Enables the mutual exclusion APIs on the I2C bus.
 */
#if !defined(I2C_USE_MUTUAL_EXCLUSION) || defined(__DOXYGEN__)
#define I2C_USE_MUTUAL_EXCLUSION            TRUE
#endif

/*===========================================================================*/
/* MAC driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Enables an event sources for incoming packets.
 */
#if !defined(MAC_USE_EVENTS) || defined(__DOXYGEN__)
#define MAC_USE_EVENTS                      TRUE
#endif

/*===========================================================================*/
/* MMC_SPI driver related settings.                                          */
/*===========================================================================*/

/**
 * @brief   Block size for MMC transfers.
 */
#if !defined(MMC_SECTOR_SIZE) || defined(__DOXYGEN__)
#define MMC_SECTOR_SIZE                     512
#endif

/**
 * @brief   Delays insertions.
 * @details If enabled this options inserts delays into the MMC waiting
 *          routines releasing some extra CPU time for the threads with
 *          lower priority, this may slow down the driver a bit however.
 *          This option is recommended also if the SPI driver does not
 *          use a DMA channel and heavily loads the CPU.
 */
#if !defined(MMC_NICE_WAITING) || defined(__DOXYGEN__)
#define MMC_NICE_WAITING                    TRUE
#endif

/**
 * @brief   Number of positive insertion queries before generating the
 *          insertion event.
 */
#if !defined(MMC_POLLING_INTERVAL) || defined(__DOXYGEN__)
#define MMC_POLLING_INTERVAL                10
#endif

/**
 * @brief   Interval, in milliseconds, between insertion queries.
 */
#if !defined(MMC_POLLING_DELAY) || defined(__DOXYGEN__)
#define MMC_POLLING_DELAY                   10
#endif

/**
 * @brief   Uses the SPI polled API for small data transfers.
 * @details Polled transfers usually improve performance because it
 *          saves two context switches and interrupt servicing. Note
 *          that this option has no effect on large transfers which
 *          are always performed using DMAs/IRQs.
 */
#if !defined(MMC_USE_SPI_POLLING) || defined(__DOXYGEN__)
#define MMC_USE_SPI_POLLING                 TRUE
#endif

/*===========================================================================*/
/* SDC driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Number of initialization attempts before rejecting the card.
 * @note    Attempts are performed at 10mS intervals.
 */
#if !defined(SDC_INIT_RETRY) || defined(__DOXYGEN__)
#define SDC_INIT_RETRY                      100
#endif

/**
 * @brief   Include support for MMC cards.
 * @note    MMC support is not yet implemented so this option must be kept
 *          at @p FALSE.
 */
#if !defined(SDC_MMC_SUPPORT) || defined(__DOXYGEN__)
#define SDC_MMC_SUPPORT                     FALSE
#endif

/**
 * @brief   Delays insertions.
 * @details If enabled this options inserts delays into the MMC waiting
 *          routines releasing some extra CPU time for the threads with
 *          lower priority, this may slow down the driver a bit however.
 */
#if !defined(SDC_NICE_WAITING) || defined(__DOXYGEN__)
#define SDC_NICE_WAITING                    TRUE
#endif

#define STM32_SDC_SDIO_UNALIGNED_SUPPORT FALSE

/*===========================================================================*/
/* SERIAL driver related settings.                                           */
/*===========================================================================*/

/**
 * @brief   Default bit rate.
 * @details Configuration parameter, this is the baud rate selected for the
 *          default configuration.
 */
#if !defined(SERIAL_DEFAULT_BITRATE) || defined(__DOXYGEN__)
#define SERIAL_DEFAULT_BITRATE              38400
#endif

/**
 * @brief   Serial buffers size.
 * @details Configuration parameter, you can change the depth of the queue
 *          buffers depending on the requirements of your application.
 * @note    The default is 16 bytes for both the transmission and receive
 *          buffers.
 */
#if !defined(SERIAL_BUFFERS_SIZE) || defined(__DOXYGEN__)
#define SERIAL_BUFFERS_SIZE                 32
#endif

/*===========================================================================*/
/* SPI driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Enables synchronous APIs.
 * @note    Disabling this option saves both code and data space.
 */
#if !defined(SPI_USE_WAIT) || defined(__DOXYGEN__)
#define SPI_USE_WAIT                        TRUE
#endif

/**
 * @brief   Enables the @p spiAcquireBus() and @p spiReleaseBus() APIs.
 * @note    Disabling this option saves both code and data space.
 */
#if !defined(SPI_USE_MUTUAL_EXCLUSION) || defined(__DOXYGEN__)
#define SPI_USE_MUTUAL_EXCLUSION            TRUE
#endif


/*===========================================================================*/
/* USB driver related settings.                                              */
/*===========================================================================*/

/**
 * @brief   Enables synchronous APIs.
 * @note    Disabling this option saves both code and data space.
 */
#if !defined(USB_USE_WAIT) || defined(__DOXYGEN__)
#define USB_USE_WAIT                        TRUE
#endif


/*===========================================================================*/
/* Community drivers' includes                                               */
/*===========================================================================*/

#include "halconf_community.h"

#endif /* HALCONF_H */

/** @} */
