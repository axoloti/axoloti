#ifndef _CHIBIOS_HAL_
/*
    ChibiOS - Copyright (C) 2006..2018 Giovanni Di Sirio.

    This file is part of ChibiOS.

    ChibiOS is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    ChibiOS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#ifdef __cplusplus
extern "C" {
#endif

#include <stddef.h>
#include <stdint.h>
#include <stdbool.h>

#include "cmparams.h"

#define TRUE 1
#define FALSE 0
#define HAL_LLD_H 
#define HALCONF_H 
#define _CHIBIOS_HAL_CONF_ 
#define _CHIBIOS_HAL_CONF_VER_7_0_ 
#define MCUCONF_H 
#define STM32F4xx_MCUCONF 
#define STM32_I2C_USE_I2C1 TRUE
#define STM32_SPI_USE_SPI1 TRUE
#define STM32_UART_USE_USART1 TRUE
#define STM32_PWM_USE_TIM3 TRUE
#define STM32_PWM_USE_TIM4 TRUE
#define STM32_PWM_USE_TIM5 TRUE
#define STM32_PWM_USE_TIM8 TRUE
#define STM32_SERIAL_USE_USART1 TRUE
#define HAL_USE_TM TRUE
#define HAL_USE_PAL TRUE
#define HAL_USE_ADC FALSE
#define HAL_USE_CAN FALSE
#define HAL_USE_CRY FALSE
#define HAL_USE_DAC FALSE
#define HAL_USE_GPT FALSE
#define HAL_USE_I2C TRUE
#define HAL_USE_I2S FALSE
#define HAL_USE_ICU FALSE
#define HAL_USE_MAC FALSE
#define HAL_USE_MMC_SPI FALSE
#define HAL_USE_PWM TRUE
#define HAL_USE_RTC FALSE
#define HAL_USE_SDC FALSE
#define HAL_USE_SERIAL TRUE
#define HAL_USE_SERIAL_USB FALSE
#define HAL_USE_SIO FALSE
#define HAL_USE_SPI TRUE
#define HAL_USE_TRNG FALSE
#define HAL_USE_UART FALSE
#define HAL_USE_USB FALSE
#define PAL_USE_CALLBACKS FALSE
#define PAL_USE_WAIT FALSE
#define ADC_USE_WAIT TRUE
#define ADC_USE_MUTUAL_EXCLUSION TRUE
#define CAN_USE_SLEEP_MODE TRUE
#define CAN_ENFORCE_USE_CALLBACKS FALSE
#define I2C_USE_MUTUAL_EXCLUSION TRUE
#define MAC_USE_EVENTS TRUE
#define MMC_SECTOR_SIZE 512
#define MMC_NICE_WAITING TRUE
#define MMC_POLLING_INTERVAL 10
#define MMC_POLLING_DELAY 10
#define MMC_USE_SPI_POLLING TRUE
#define SDC_INIT_RETRY 100
#define SDC_MMC_SUPPORT FALSE
#define SDC_NICE_WAITING TRUE
#define STM32_SDC_SDIO_UNALIGNED_SUPPORT FALSE
#define SERIAL_DEFAULT_BITRATE 38400
#define SERIAL_BUFFERS_SIZE 32
#define SPI_USE_WAIT TRUE
#define SPI_USE_MUTUAL_EXCLUSION TRUE
#define USB_USE_WAIT TRUE
#define HALCONF_COMMUNITY_H 
#define HAL_USE_COMMUNITY TRUE
#define HAL_USE_FSMC FALSE
#define HAL_USE_NAND FALSE
#define HAL_USE_ONEWIRE FALSE
#define HAL_USE_EICU FALSE
#define HAL_USE_CRC FALSE
#define HAL_USE_RNG FALSE
#define HAL_USE_USBH FALSE
struct stm32_dma_stream_t;
#define HAL_PAL_H 
#define PAL_MODE_RESET 0U
#define PAL_MODE_UNCONNECTED 1U
#define PAL_MODE_INPUT 2U
#define PAL_MODE_INPUT_PULLUP 3U
#define PAL_MODE_INPUT_PULLDOWN 4U
#define PAL_MODE_INPUT_ANALOG 5U
#define PAL_MODE_OUTPUT_PUSHPULL 6U
#define PAL_MODE_OUTPUT_OPENDRAIN 7U
#define PAL_LOW 0U
#define PAL_HIGH 1U
#define PAL_EVENT_MODE_EDGES_MASK 3U
#define PAL_EVENT_MODE_DISABLED 0U
#define PAL_EVENT_MODE_RISING_EDGE 1U
#define PAL_EVENT_MODE_FALLING_EDGE 2U
#define PAL_EVENT_MODE_BOTH_EDGES 3U
typedef void (*palcallback_t)(void *arg);
#define HAL_PAL_LLD_H 
#define STM32_GPIO_H 
#undef GPIOA
#undef GPIOB
#undef GPIOC
#undef GPIOD
#undef GPIOE
#undef GPIOF
#undef GPIOG
#undef GPIOH
#undef GPIOI
#undef GPIOJ
#undef GPIOK
#define GPIOA ((stm32_gpio_t *)GPIOA_BASE)
#define GPIOB ((stm32_gpio_t *)GPIOB_BASE)
#define GPIOC ((stm32_gpio_t *)GPIOC_BASE)

#undef PAL_MODE_RESET
#undef PAL_MODE_UNCONNECTED
#undef PAL_MODE_INPUT
#undef PAL_MODE_INPUT_PULLUP
#undef PAL_MODE_INPUT_PULLDOWN
#undef PAL_MODE_INPUT_ANALOG
#undef PAL_MODE_OUTPUT_PUSHPULL
#undef PAL_MODE_OUTPUT_OPENDRAIN
#define PAL_STM32_MODE_MASK (3U << 0U)
#define PAL_STM32_MODE_INPUT (0U << 0U)
#define PAL_STM32_MODE_OUTPUT (1U << 0U)
#define PAL_STM32_MODE_ALTERNATE (2U << 0U)
#define PAL_STM32_MODE_ANALOG (3U << 0U)
#define PAL_STM32_OTYPE_MASK (1U << 2U)
#define PAL_STM32_OTYPE_PUSHPULL (0U << 2U)
#define PAL_STM32_OTYPE_OPENDRAIN (1U << 2U)
#define PAL_STM32_OSPEED_MASK (3U << 3U)
#define PAL_STM32_OSPEED_LOWEST (0U << 3U)
#define PAL_STM32_OSPEED_MID1 (1U << 3U)
#define PAL_STM32_OSPEED_MID2 (2U << 3U)
#define PAL_STM32_OSPEED_HIGHEST (3U << 3U)
#define PAL_STM32_PUPDR_MASK (3U << 5U)
#define PAL_STM32_PUPDR_FLOATING (0U << 5U)
#define PAL_STM32_PUPDR_PULLUP (1U << 5U)
#define PAL_STM32_PUPDR_PULLDOWN (2U << 5U)
#define PAL_STM32_ALTERNATE_MASK (15U << 7U)
#define PAL_STM32_ALTERNATE(n) ((n) << 7U)
#define PAL_MODE_ALTERNATE(n) (PAL_STM32_MODE_ALTERNATE | PAL_STM32_ALTERNATE(n))
#define PAL_MODE_RESET PAL_STM32_MODE_INPUT
#define PAL_MODE_UNCONNECTED PAL_MODE_INPUT_PULLUP
#define PAL_MODE_INPUT PAL_STM32_MODE_INPUT
#define PAL_MODE_INPUT_PULLUP (PAL_STM32_MODE_INPUT | PAL_STM32_PUPDR_PULLUP)
#define PAL_MODE_INPUT_PULLDOWN (PAL_STM32_MODE_INPUT | PAL_STM32_PUPDR_PULLDOWN)
#define PAL_MODE_INPUT_ANALOG PAL_STM32_MODE_ANALOG
#define PAL_MODE_OUTPUT_PUSHPULL (PAL_STM32_MODE_OUTPUT | PAL_STM32_OTYPE_PUSHPULL)
#define PAL_MODE_OUTPUT_OPENDRAIN (PAL_STM32_MODE_OUTPUT | PAL_STM32_OTYPE_OPENDRAIN)

struct stm32_gpio_t;
typedef stm32_gpio_t * ioportid_t;

int palReadPad(ioportid_t port, int pad);
void palWritePad(ioportid_t port, int pad, int bit);
void palSetPad(ioportid_t port, int pad);
void palClearPad(ioportid_t port, int pad);
void palTogglePad(ioportid_t port, int pad);
void palSetPadMode(ioportid_t port, int pad, int mode);


#define HAL_I2C_LLD_H 
typedef struct abstract I2CDriver;
typedef struct abstract I2CConfig;
typedef uint32_t i2cflags_t;
typedef uint16_t i2caddr_t;
#define HAL_I2C_H 
#define I2C_NO_ERROR 0x00
#define I2C_BUS_ERROR 0x01
#define I2C_ARBITRATION_LOST 0x02
#define I2C_ACK_FAILURE 0x04
#define I2C_OVERRUN 0x08
#define I2C_PEC_ERROR 0x10
#define I2C_TIMEOUT 0x20
#define I2C_SMB_ALERT 0x40
typedef enum {
  I2C_UNINIT = 0,
  I2C_STOP = 1,
  I2C_READY = 2,
  I2C_ACTIVE_TX = 3,
  I2C_ACTIVE_RX = 4,
  I2C_LOCKED = 5
} i2cstate_t;
#define _i2c_wakeup_isr(i2cp) do { osalSysLockFromISR(); osalThreadResumeI(&(i2cp)->thread, MSG_OK); osalSysUnlockFromISR(); } while(0)
#define _i2c_wakeup_error_isr(i2cp) do { osalSysLockFromISR(); osalThreadResumeI(&(i2cp)->thread, MSG_RESET); osalSysUnlockFromISR(); } while(0)
#define i2cMasterTransmit(i2cp,addr,txbuf,txbytes,rxbuf,rxbytes) (i2cMasterTransmitTimeout(i2cp, addr, txbuf, txbytes, rxbuf, rxbytes, TIME_INFINITE))
#define i2cMasterReceive(i2cp,addr,rxbuf,rxbytes) (i2cMasterReceiveTimeout(i2cp, addr, rxbuf, rxbytes, TIME_INFINITE))
  void i2cInit(void);
  void i2cObjectInit(I2CDriver *i2cp);
  void i2cStart(I2CDriver *i2cp, const I2CConfig *config);
  void i2cStop(I2CDriver *i2cp);
  i2cflags_t i2cGetErrors(I2CDriver *i2cp);
  msg_t i2cMasterTransmitTimeout(I2CDriver *i2cp,
                                 i2caddr_t addr,
                                 const uint8_t *txbuf, size_t txbytes,
                                 uint8_t *rxbuf, size_t rxbytes,
                                 sysinterval_t timeout);
  msg_t i2cMasterReceiveTimeout(I2CDriver *i2cp,
                                i2caddr_t addr,
                                uint8_t *rxbuf, size_t rxbytes,
                                sysinterval_t timeout);
  void i2cAcquireBus(I2CDriver *i2cp);
  void i2cReleaseBus(I2CDriver *i2cp);
#undef _i2c_wakeup_isr
#undef _i2c_wakeup_error_isr
extern I2CDriver I2CD1;
#define HAL_SPI_LLD_H 
#define spi_lld_config_fields uint16_t cr1; uint16_t cr2
#define spi_lld_driver_fields SPI_TypeDef *spi; const stm32_dma_stream_t *dmarx; const stm32_dma_stream_t *dmatx; uint32_t rxdmamode; uint32_t txdmamode
#define HAL_SPI_H 
#define SPI_SELECT_MODE_NONE 0
#define SPI_SELECT_MODE_PAD 1
#define SPI_SELECT_MODE_PORT 2
#define SPI_SELECT_MODE_LINE 3
#define SPI_SELECT_MODE_LLD 4
#define SPI_USE_CIRCULAR FALSE
#define SPI_SELECT_MODE SPI_SELECT_MODE_PAD
typedef enum {
  SPI_UNINIT = 0,
  SPI_STOP = 1,
  SPI_READY = 2,
  SPI_ACTIVE = 3,
  SPI_COMPLETE = 4
} spistate_t;
typedef struct hal_spi_driver SPIDriver;
typedef struct hal_spi_config SPIConfig;
typedef void (*spicallback_t)(SPIDriver *spip);
struct hal_spi_config {
  spicallback_t end_cb;
  ioportid_t ssport;
  uint_fast8_t sspad;
  spi_lld_config_fields;
};
struct hal_spi_driver {
  spistate_t state;
  const SPIConfig *config;
  thread_reference_t thread;
  mutex_t mutex;
  spi_lld_driver_fields;
};
#define spiIsBufferComplete(spip) ((bool)((spip)->state == SPI_COMPLETE))
#define spiSelectI(spip) do { palClearPad((spip)->config->ssport, (spip)->config->sspad); } while (false)
#define spiUnselectI(spip) do { palSetPad((spip)->config->ssport, (spip)->config->sspad); } while (false)
#define spiStartIgnoreI(spip,n) { (spip)->state = SPI_ACTIVE; spi_lld_ignore(spip, n); }
#define spiStartExchangeI(spip,n,txbuf,rxbuf) { (spip)->state = SPI_ACTIVE; spi_lld_exchange(spip, n, txbuf, rxbuf); }
#define spiStartSendI(spip,n,txbuf) { (spip)->state = SPI_ACTIVE; spi_lld_send(spip, n, txbuf); }
#define spiStartReceiveI(spip,n,rxbuf) { (spip)->state = SPI_ACTIVE; spi_lld_receive(spip, n, rxbuf); }
#define spiPolledExchange(spip,frame) spi_lld_polled_exchange(spip, frame)
#define _spi_wakeup_isr(spip) { osalSysLockFromISR(); osalThreadResumeI(&(spip)->thread, MSG_OK); osalSysUnlockFromISR(); }
#define _spi_isr_code(spip) { if ((spip)->config->end_cb) { (spip)->state = SPI_COMPLETE; (spip)->config->end_cb(spip); if ((spip)->state == SPI_COMPLETE) (spip)->state = SPI_READY; } else (spip)->state = SPI_READY; _spi_wakeup_isr(spip); }
#define _spi_isr_half_code(spip) { if ((spip)->config->end_cb) { (spip)->config->end_cb(spip); } }
#define _spi_isr_full_code(spip) { if ((spip)->config->end_cb) { (spip)->state = SPI_COMPLETE; (spip)->config->end_cb(spip); if ((spip)->state == SPI_COMPLETE) (spip)->state = SPI_ACTIVE; } }
  void spiInit(void);
  void spiObjectInit(SPIDriver *spip);
  void spiStart(SPIDriver *spip, const SPIConfig *config);
  void spiStop(SPIDriver *spip);
  void spiSelect(SPIDriver *spip);
  void spiUnselect(SPIDriver *spip);
  void spiStartIgnore(SPIDriver *spip, size_t n);
  void spiStartExchange(SPIDriver *spip, size_t n,
                        const void *txbuf, void *rxbuf);
  void spiStartSend(SPIDriver *spip, size_t n, const void *txbuf);
  void spiStartReceive(SPIDriver *spip, size_t n, void *rxbuf);
  void spiIgnore(SPIDriver *spip, size_t n);
  void spiExchange(SPIDriver *spip, size_t n, const void *txbuf, void *rxbuf);
  void spiSend(SPIDriver *spip, size_t n, const void *txbuf);
  void spiReceive(SPIDriver *spip, size_t n, void *rxbuf);
  void spiAcquireBus(SPIDriver *spip);
  void spiReleaseBus(SPIDriver *spip);
extern SPIDriver SPID1;
#undef _spi_wakeup_isr
#undef _spi_isr_code
#undef _spi_isr_half_code
#undef _spi_isr_full_code
#define STM32_HAS_TIM3 TRUE
#define STM32_HAS_TIM4 TRUE
#define STM32_HAS_TIM5 TRUE
#define STM32_HAS_TIM8 TRUE
#define STM32_TIM_MAX_CHANNELS 4
#define OSAL_IRQ_IS_VALID_PRIORITY(x) (TRUE)
#define HAL_PWM_H 
#define PWM_OUTPUT_MASK 0x0FU
#define PWM_OUTPUT_DISABLED 0x00U
#define PWM_OUTPUT_ACTIVE_HIGH 0x01U
#define PWM_OUTPUT_ACTIVE_LOW 0x02U
typedef enum {
  PWM_UNINIT = 0,
  PWM_STOP = 1,
  PWM_READY = 2
} pwmstate_t;
typedef struct PWMDriver PWMDriver;
typedef void (*pwmcallback_t)(PWMDriver *pwmp);
#define HAL_PWM_LLD_H 
#define STM32_TIM_H 
#define STM32_TIM_CR1_CEN (1U << 0)
#define STM32_TIM_CR1_UDIS (1U << 1)
#define STM32_TIM_CR1_URS (1U << 2)
#define STM32_TIM_CR1_OPM (1U << 3)
#define STM32_TIM_CR1_DIR (1U << 4)
#define STM32_TIM_CR1_CMS_MASK (3U << 5)
#define STM32_TIM_CR1_CMS(n) ((n) << 5)
#define STM32_TIM_CR1_ARPE (1U << 7)
#define STM32_TIM_CR1_CKD_MASK (3U << 8)
#define STM32_TIM_CR1_CKD(n) ((n) << 8)
#define STM32_TIM_CR1_UIFREMAP (1U << 11)
#define STM32_TIM_CR2_CCPC (1U << 0)
#define STM32_TIM_CR2_CCUS (1U << 2)
#define STM32_TIM_CR2_CCDS (1U << 3)
#define STM32_TIM_CR2_MMS_MASK (7U << 4)
#define STM32_TIM_CR2_MMS(n) ((n) << 4)
#define STM32_TIM_CR2_TI1S (1U << 7)
#define STM32_TIM_CR2_OIS1 (1U << 8)
#define STM32_TIM_CR2_OIS1N (1U << 9)
#define STM32_TIM_CR2_OIS2 (1U << 10)
#define STM32_TIM_CR2_OIS2N (1U << 11)
#define STM32_TIM_CR2_OIS3 (1U << 12)
#define STM32_TIM_CR2_OIS3N (1U << 13)
#define STM32_TIM_CR2_OIS4 (1U << 14)
#define STM32_TIM_CR2_OIS5 (1U << 16)
#define STM32_TIM_CR2_OIS6 (1U << 18)
#define STM32_TIM_CR2_MMS2_MASK (15U << 20)
#define STM32_TIM_CR2_MMS2(n) ((n) << 20)
#define STM32_TIM_SMCR_SMS_MASK ((7U << 0) | (1U << 16))
#define STM32_TIM_SMCR_SMS(n) ((((n) & 7) << 0) | (((n) >> 3) << 16))
#define STM32_TIM_SMCR_OCCS (1U << 3)
#define STM32_TIM_SMCR_TS_MASK (7U << 4)
#define STM32_TIM_SMCR_TS(n) ((n) << 4)
#define STM32_TIM_SMCR_MSM (1U << 7)
#define STM32_TIM_SMCR_ETF_MASK (15U << 8)
#define STM32_TIM_SMCR_ETF(n) ((n) << 8)
#define STM32_TIM_SMCR_ETPS_MASK (3U << 12)
#define STM32_TIM_SMCR_ETPS(n) ((n) << 12)
#define STM32_TIM_SMCR_ECE (1U << 14)
#define STM32_TIM_SMCR_ETP (1U << 15)
#define STM32_TIM_DIER_UIE (1U << 0)
#define STM32_TIM_DIER_CC1IE (1U << 1)
#define STM32_TIM_DIER_CC2IE (1U << 2)
#define STM32_TIM_DIER_CC3IE (1U << 3)
#define STM32_TIM_DIER_CC4IE (1U << 4)
#define STM32_TIM_DIER_COMIE (1U << 5)
#define STM32_TIM_DIER_TIE (1U << 6)
#define STM32_TIM_DIER_BIE (1U << 7)
#define STM32_TIM_DIER_UDE (1U << 8)
#define STM32_TIM_DIER_CC1DE (1U << 9)
#define STM32_TIM_DIER_CC2DE (1U << 10)
#define STM32_TIM_DIER_CC3DE (1U << 11)
#define STM32_TIM_DIER_CC4DE (1U << 12)
#define STM32_TIM_DIER_COMDE (1U << 13)
#define STM32_TIM_DIER_TDE (1U << 14)
#define STM32_TIM_DIER_IRQ_MASK (STM32_TIM_DIER_UIE | STM32_TIM_DIER_CC1IE | STM32_TIM_DIER_CC2IE | STM32_TIM_DIER_CC3IE | STM32_TIM_DIER_CC4IE | STM32_TIM_DIER_COMIE | STM32_TIM_DIER_TIE | STM32_TIM_DIER_BIE)
#define STM32_TIM_SR_UIF (1U << 0)
#define STM32_TIM_SR_CC1IF (1U << 1)
#define STM32_TIM_SR_CC2IF (1U << 2)
#define STM32_TIM_SR_CC3IF (1U << 3)
#define STM32_TIM_SR_CC4IF (1U << 4)
#define STM32_TIM_SR_COMIF (1U << 5)
#define STM32_TIM_SR_TIF (1U << 6)
#define STM32_TIM_SR_BIF (1U << 7)
#define STM32_TIM_SR_B2IF (1U << 8)
#define STM32_TIM_SR_CC1OF (1U << 9)
#define STM32_TIM_SR_CC2OF (1U << 10)
#define STM32_TIM_SR_CC3OF (1U << 11)
#define STM32_TIM_SR_CC4OF (1U << 12)
#define STM32_TIM_SR_CC5IF (1U << 16)
#define STM32_TIM_SR_CC6IF (1U << 17)
#define STM32_TIM_EGR_UG (1U << 0)
#define STM32_TIM_EGR_CC1G (1U << 1)
#define STM32_TIM_EGR_CC2G (1U << 2)
#define STM32_TIM_EGR_CC3G (1U << 3)
#define STM32_TIM_EGR_CC4G (1U << 4)
#define STM32_TIM_EGR_COMG (1U << 5)
#define STM32_TIM_EGR_TG (1U << 6)
#define STM32_TIM_EGR_BG (1U << 7)
#define STM32_TIM_EGR_B2G (1U << 8)
#define STM32_TIM_CCMR1_CC1S_MASK (3U << 0)
#define STM32_TIM_CCMR1_CC1S(n) ((n) << 0)
#define STM32_TIM_CCMR1_OC1FE (1U << 2)
#define STM32_TIM_CCMR1_OC1PE (1U << 3)
#define STM32_TIM_CCMR1_OC1M_MASK ((7U << 4) | (1U << 16))
#define STM32_TIM_CCMR1_OC1M(n) ((((n) & 7) << 4) | (((n) >> 3) << 16))
#define STM32_TIM_CCMR1_OC1CE (1U << 7)
#define STM32_TIM_CCMR1_CC2S_MASK (3U << 8)
#define STM32_TIM_CCMR1_CC2S(n) ((n) << 8)
#define STM32_TIM_CCMR1_OC2FE (1U << 10)
#define STM32_TIM_CCMR1_OC2PE (1U << 11)
#define STM32_TIM_CCMR1_OC2M_MASK ((7U << 12) | (1U << 24))
#define STM32_TIM_CCMR1_OC2M(n) ((((n) & 7) << 12) | (((n) >> 3) << 24))
#define STM32_TIM_CCMR1_OC2CE (1U << 15)
#define STM32_TIM_CCMR1_IC1PSC_MASK (3U << 2)
#define STM32_TIM_CCMR1_IC1PSC(n) ((n) << 2)
#define STM32_TIM_CCMR1_IC1F_MASK (15U << 4)
#define STM32_TIM_CCMR1_IC1F(n) ((n) << 4)
#define STM32_TIM_CCMR1_IC2PSC_MASK (3U << 10)
#define STM32_TIM_CCMR1_IC2PSC(n) ((n) << 10)
#define STM32_TIM_CCMR1_IC2F_MASK (15U << 12)
#define STM32_TIM_CCMR1_IC2F(n) ((n) << 12)
#define STM32_TIM_CCMR2_CC3S_MASK (3U << 0)
#define STM32_TIM_CCMR2_CC3S(n) ((n) << 0)
#define STM32_TIM_CCMR2_OC3FE (1U << 2)
#define STM32_TIM_CCMR2_OC3PE (1U << 3)
#define STM32_TIM_CCMR2_OC3M_MASK ((7U << 4) | (1U << 16))
#define STM32_TIM_CCMR2_OC3M(n) ((((n) & 7) << 4) | (((n) >> 3) << 16))
#define STM32_TIM_CCMR2_OC3CE (1U << 7)
#define STM32_TIM_CCMR2_CC4S_MASK (3U << 8)
#define STM32_TIM_CCMR2_CC4S(n) ((n) << 8)
#define STM32_TIM_CCMR2_OC4FE (1U << 10)
#define STM32_TIM_CCMR2_OC4PE (1U << 11)
#define STM32_TIM_CCMR2_OC4M_MASK ((7U << 12) | (1U << 24))
#define STM32_TIM_CCMR2_OC4M(n) ((((n) & 7) << 12) | (((n) >> 3) << 24))
#define STM32_TIM_CCMR2_OC4CE (1U << 15)
#define STM32_TIM_CCMR2_IC3PSC_MASK (3U << 2)
#define STM32_TIM_CCMR2_IC3PSC(n) ((n) << 2)
#define STM32_TIM_CCMR2_IC3F_MASK (15U << 4)
#define STM32_TIM_CCMR2_IC3F(n) ((n) << 4)
#define STM32_TIM_CCMR2_IC4PSC_MASK (3U << 10)
#define STM32_TIM_CCMR2_IC4PSC(n) ((n) << 10)
#define STM32_TIM_CCMR2_IC4F_MASK (15U << 12)
#define STM32_TIM_CCMR2_IC4F(n) ((n) << 12)
#define STM32_TIM_CCER_CC1E (1U << 0)
#define STM32_TIM_CCER_CC1P (1U << 1)
#define STM32_TIM_CCER_CC1NE (1U << 2)
#define STM32_TIM_CCER_CC1NP (1U << 3)
#define STM32_TIM_CCER_CC2E (1U << 4)
#define STM32_TIM_CCER_CC2P (1U << 5)
#define STM32_TIM_CCER_CC2NE (1U << 6)
#define STM32_TIM_CCER_CC2NP (1U << 7)
#define STM32_TIM_CCER_CC3E (1U << 8)
#define STM32_TIM_CCER_CC3P (1U << 9)
#define STM32_TIM_CCER_CC3NE (1U << 10)
#define STM32_TIM_CCER_CC3NP (1U << 11)
#define STM32_TIM_CCER_CC4E (1U << 12)
#define STM32_TIM_CCER_CC4P (1U << 13)
#define STM32_TIM_CCER_CC4NP (1U << 15)
#define STM32_TIM_CCER_CC5E (1U << 16)
#define STM32_TIM_CCER_CC5P (1U << 17)
#define STM32_TIM_CCER_CC6E (1U << 20)
#define STM32_TIM_CCER_CC6P (1U << 21)
#define STM32_TIM_CNT_UIFCPY (1U << 31)
#define STM32_TIM_BDTR_DTG_MASK (255U << 0)
#define STM32_TIM_BDTR_DTG(n) ((n) << 0)
#define STM32_TIM_BDTR_LOCK_MASK (3U << 8)
#define STM32_TIM_BDTR_LOCK(n) ((n) << 8)
#define STM32_TIM_BDTR_OSSI (1U << 10)
#define STM32_TIM_BDTR_OSSR (1U << 11)
#define STM32_TIM_BDTR_BKE (1U << 12)
#define STM32_TIM_BDTR_BKP (1U << 13)
#define STM32_TIM_BDTR_AOE (1U << 14)
#define STM32_TIM_BDTR_MOE (1U << 15)
#define STM32_TIM_BDTR_BKF_MASK (15U << 16)
#define STM32_TIM_BDTR_BKF(n) ((n) << 16)
#define STM32_TIM_BDTR_BK2F_MASK (15U << 20)
#define STM32_TIM_BDTR_BK2F(n) ((n) << 20)
#define STM32_TIM_BDTR_BK2E (1U << 24)
#define STM32_TIM_BDTR_BK2P (1U << 25)
#define STM32_TIM_DCR_DBA_MASK (31U << 0)
#define STM32_TIM_DCR_DBA(n) ((n) << 0)
#define STM32_TIM_DCR_DBL_MASK (31U << 8)
#define STM32_TIM_DCR_DBL(n) ((n) << 8)
#define STM32_TIM16_OR_TI1_RMP_MASK (3U << 6)
#define STM32_TIM16_OR_TI1_RMP(n) ((n) << 6)
#define STM32_TIM_OR_ETR_RMP_MASK (15U << 0)
#define STM32_TIM_OR_ETR_RMP(n) ((n) << 0)
#define STM32_TIM_CCMR3_OC5FE (1U << 2)
#define STM32_TIM_CCMR3_OC5PE (1U << 3)
#define STM32_TIM_CCMR3_OC5M_MASK ((7U << 4) | (1U << 16))
#define STM32_TIM_CCMR3_OC5M(n) ((((n) & 7) << 4) | (((n) >> 2) << 16))
#define STM32_TIM_CCMR3_OC5CE (1U << 7)
#define STM32_TIM_CCMR3_OC6FE (1U << 10)
#define STM32_TIM_CCMR3_OC6PE (1U << 11)
#define STM32_TIM_CCMR3_OC6M_MASK ((7U << 12) | (1U << 24))
#define STM32_TIM_CCMR3_OC6M(n) ((((n) & 7) << 12) | (((n) >> 2) << 24))
#define STM32_TIM_CCMR3_OC6CE (1U << 15)
#define STM32_LPTIM_ISR_CMPM (1U << 0)
#define STM32_LPTIM_ISR_ARRM (1U << 1)
#define STM32_LPTIM_ISR_EXTTRIG (1U << 2)
#define STM32_LPTIM_ISR_CMPOK (1U << 3)
#define STM32_LPTIM_ISR_ARROK (1U << 4)
#define STM32_LPTIM_ISR_UP (1U << 5)
#define STM32_LPTIM_ISR_DOWN (1U << 6)
#define STM32_LPTIM_ICR_CMPMCF (1U << 0)
#define STM32_LPTIM_ICR_ARRMCF (1U << 1)
#define STM32_LPTIM_ICR_EXTTRIGCF (1U << 2)
#define STM32_LPTIM_ICR_CMPOKCF (1U << 3)
#define STM32_LPTIM_ICR_ARROKCF (1U << 4)
#define STM32_LPTIM_ICR_UPCF (1U << 5)
#define STM32_LPTIM_ICR_DOWNCF (1U << 6)
#define STM32_LPTIM_IER_CMPMIE (1U << 0)
#define STM32_LPTIM_IER_ARRMIE (1U << 1)
#define STM32_LPTIM_IER_EXTTRIGIE (1U << 2)
#define STM32_LPTIM_IER_CMPOKIE (1U << 3)
#define STM32_LPTIM_IER_ARROKIE (1U << 4)
#define STM32_LPTIM_IER_UPIE (1U << 5)
#define STM32_LPTIM_IER_DOWNIE (1U << 6)
#define STM32_LPTIM_CFGR_CKSEL (1U << 0)
#define STM32_LPTIM_CFGR_CKPOL_MASK (3U << 1)
#define STM32_LPTIM_CFGR_CKPOL(n) ((n) << 1)
#define STM32_LPTIM_CFGR_CKFLT_MASK (3U << 3)
#define STM32_LPTIM_CFGR_CKFLT(n) ((n) << 3)
#define STM32_LPTIM_CFGR_TRGFLT_MASK (3U << 6)
#define STM32_LPTIM_CFGR_TRGFLT(n) ((n) << 6)
#define STM32_LPTIM_CFGR_PRESC_MASK (7U << 9)
#define STM32_LPTIM_CFGR_PRESC(n) ((n) << 9)
#define STM32_LPTIM_CFGR_TRIGSEL_MASK (7U << 13)
#define STM32_LPTIM_CFGR_TRIGSEL(n) ((n) << 13)
#define STM32_LPTIM_CFGR_TRIGEN_MASK (3U << 17)
#define STM32_LPTIM_CFGR_TRIGEN(n) ((n) << 17)
#define STM32_LPTIM_CFGR_TIMOUT (1U << 19)
#define STM32_LPTIM_CFGR_WAVE (1U << 20)
#define STM32_LPTIM_CFGR_WAVPOL (1U << 21)
#define STM32_LPTIM_CFGR_PRELOAD (1U << 22)
#define STM32_LPTIM_CFGR_COUNTMODE (1U << 23)
#define STM32_LPTIM_CFGR_ENC (1U << 24)
#define STM32_LPTIM_CR_ENABLE (1U << 0)
#define STM32_LPTIM_CR_SNGSTRT (1U << 1)
#define STM32_LPTIM_CR_CNTSTRT (1U << 2)
#define STM32_LPTIM_OR_0 (1U << 0)
#define STM32_LPTIM_OR_1 (1U << 1)
#define STM32_TIM1 ((stm32_tim_t *)TIM1_BASE)
#define STM32_TIM2 ((stm32_tim_t *)TIM2_BASE)
#define STM32_TIM3 ((stm32_tim_t *)TIM3_BASE)
#define STM32_TIM4 ((stm32_tim_t *)TIM4_BASE)
#define STM32_TIM5 ((stm32_tim_t *)TIM5_BASE)
#define STM32_TIM6 ((stm32_tim_t *)TIM6_BASE)
#define STM32_TIM7 ((stm32_tim_t *)TIM7_BASE)
#define STM32_TIM8 ((stm32_tim_t *)TIM8_BASE)
#define STM32_TIM9 ((stm32_tim_t *)TIM9_BASE)
#define STM32_TIM10 ((stm32_tim_t *)TIM10_BASE)
#define STM32_TIM11 ((stm32_tim_t *)TIM11_BASE)
#define STM32_TIM12 ((stm32_tim_t *)TIM12_BASE)
#define STM32_TIM13 ((stm32_tim_t *)TIM13_BASE)
#define STM32_TIM14 ((stm32_tim_t *)TIM14_BASE)
#define STM32_TIM15 ((stm32_tim_t *)TIM15_BASE)
#define STM32_TIM16 ((stm32_tim_t *)TIM16_BASE)
#define STM32_TIM17 ((stm32_tim_t *)TIM17_BASE)
#define STM32_TIM18 ((stm32_tim_t *)TIM18_BASE)
#define STM32_TIM19 ((stm32_tim_t *)TIM19_BASE)
#define STM32_TIM20 ((stm32_tim_t *)TIM20_BASE)
#define STM32_TIM21 ((stm32_tim_t *)TIM21_BASE)
#define STM32_TIM22 ((stm32_tim_t *)TIM22_BASE)
#define STM32_LPTIM1 ((stm32_lptim_t *)LPTIM1_BASE)
#define STM32_LPTIM2 ((stm32_lptim_t *)LPTIM2_BASE)
typedef struct {
  volatile uint32_t CR1;
  volatile uint32_t CR2;
  volatile uint32_t SMCR;
  volatile uint32_t DIER;
  volatile uint32_t SR;
  volatile uint32_t EGR;
  volatile uint32_t CCMR1;
  volatile uint32_t CCMR2;
  volatile uint32_t CCER;
  volatile uint32_t CNT;
  volatile uint32_t PSC;
  volatile uint32_t ARR;
  volatile uint32_t RCR;
  volatile uint32_t CCR[4];
  volatile uint32_t BDTR;
  volatile uint32_t DCR;
  volatile uint32_t DMAR;
  volatile uint32_t OR;
  volatile uint32_t CCMR3;
  volatile uint32_t CCXR[2];
} stm32_tim_t;
typedef struct {
  volatile uint32_t ISR;
  volatile uint32_t ICR;
  volatile uint32_t IER;
  volatile uint32_t CFGR;
  volatile uint32_t CR;
  volatile uint32_t CMP;
  volatile uint32_t ARR;
  volatile uint32_t CNT;
  volatile uint32_t OR;
} stm32_lptim_t;
#define PWM_CHANNELS STM32_TIM_MAX_CHANNELS
#define PWM_COMPLEMENTARY_OUTPUT_MASK 0xF0
#define PWM_COMPLEMENTARY_OUTPUT_DISABLED 0x00
#define PWM_COMPLEMENTARY_OUTPUT_ACTIVE_HIGH 0x10
#define PWM_COMPLEMENTARY_OUTPUT_ACTIVE_LOW 0x20
#define STM32_PWM_USE_ADVANCED FALSE
#define STM32_PWM_USE_TIM1 FALSE
#define STM32_PWM_USE_TIM2 FALSE
#define STM32_PWM_USE_TIM9 FALSE
#define STM32_PWM_USE_TIM15 FALSE
#define STM32_PWM_USE_TIM16 FALSE
#define STM32_PWM_USE_TIM17 FALSE
#define STM32_PWM_TIM1_IRQ_PRIORITY 7
#define STM32_PWM_TIM2_IRQ_PRIORITY 7
#define STM32_PWM_TIM3_IRQ_PRIORITY 7
#define STM32_PWM_TIM4_IRQ_PRIORITY 7
#define STM32_PWM_TIM5_IRQ_PRIORITY 7
#define STM32_PWM_TIM8_IRQ_PRIORITY 7
#define STM32_PWM_TIM9_IRQ_PRIORITY 7
#define STM32_PWM_TIM15_IRQ_PRIORITY 7
#define STM32_PWM_TIM16_IRQ_PRIORITY 7
#define STM32_PWM_TIM17_IRQ_PRIORITY 7
#define STM32_HAS_TIM1 FALSE
#define STM32_HAS_TIM2 FALSE
#define STM32_HAS_TIM9 FALSE
#define STM32_HAS_TIM15 FALSE
#define STM32_HAS_TIM16 FALSE
#define STM32_HAS_TIM17 FALSE
#define STM32_TIM3_IS_USED 
#define STM32_TIM4_IS_USED 
#define STM32_TIM5_IS_USED 
#define STM32_TIM8_IS_USED 
typedef uint32_t pwmmode_t;
typedef uint8_t pwmchannel_t;
typedef uint32_t pwmchnmsk_t;
typedef uint32_t pwmcnt_t;
typedef struct {
  pwmmode_t mode;
  pwmcallback_t callback;
} PWMChannelConfig;
typedef struct {
  uint32_t frequency;
  pwmcnt_t period;
  pwmcallback_t callback;
  PWMChannelConfig channels[PWM_CHANNELS];
  uint32_t cr2;
   uint32_t dier;
} PWMConfig;
struct PWMDriver {
  pwmstate_t state;
  const PWMConfig *config;
  pwmcnt_t period;
  pwmchnmsk_t enabled;
  pwmchannel_t channels;
  uint32_t clock;
  stm32_tim_t *tim;
};
#define pwm_lld_change_period(pwmp,period) ((pwmp)->tim->ARR = ((period) - 1))
extern PWMDriver PWMD3;
extern PWMDriver PWMD4;
extern PWMDriver PWMD5;
extern PWMDriver PWMD8;
  void pwm_lld_init(void);
  void pwm_lld_start(PWMDriver *pwmp);
  void pwm_lld_stop(PWMDriver *pwmp);
  void pwm_lld_enable_channel(PWMDriver *pwmp,
                              pwmchannel_t channel,
                              pwmcnt_t width);
  void pwm_lld_disable_channel(PWMDriver *pwmp, pwmchannel_t channel);
  void pwm_lld_enable_periodic_notification(PWMDriver *pwmp);
  void pwm_lld_disable_periodic_notification(PWMDriver *pwmp);
  void pwm_lld_enable_channel_notification(PWMDriver *pwmp,
                                           pwmchannel_t channel);
  void pwm_lld_disable_channel_notification(PWMDriver *pwmp,
                                            pwmchannel_t channel);
  void pwm_lld_serve_interrupt(PWMDriver *pwmp);
#define PWM_FRACTION_TO_WIDTH(pwmp,denominator,numerator) ((pwmcnt_t)((((pwmcnt_t)(pwmp)->period) * (pwmcnt_t)(numerator)) / (pwmcnt_t)(denominator)))
#define PWM_DEGREES_TO_WIDTH(pwmp,degrees) PWM_FRACTION_TO_WIDTH(pwmp, 36000, degrees)
#define PWM_PERCENTAGE_TO_WIDTH(pwmp,percentage) PWM_FRACTION_TO_WIDTH(pwmp, 10000, percentage)
#define pwmChangePeriodI(pwmp,value) { (pwmp)->period = (value); pwm_lld_change_period(pwmp, value); }
#define pwmEnableChannelI(pwmp,channel,width) do { (pwmp)->enabled |= ((pwmchnmsk_t)1U << (pwmchnmsk_t)(channel)); pwm_lld_enable_channel(pwmp, channel, width); } while (false)
#define pwmDisableChannelI(pwmp,channel) do { (pwmp)->enabled &= ~((pwmchnmsk_t)1U << (pwmchnmsk_t)(channel)); pwm_lld_disable_channel(pwmp, channel); } while (false)
#define pwmIsChannelEnabledI(pwmp,channel) (((pwmp)->enabled & ((pwmchnmsk_t)1U << (pwmchnmsk_t)(channel))) != 0U)
#define pwmEnablePeriodicNotificationI(pwmp) pwm_lld_enable_periodic_notification(pwmp)
#define pwmDisablePeriodicNotificationI(pwmp) pwm_lld_disable_periodic_notification(pwmp)
#define pwmEnableChannelNotificationI(pwmp,channel) pwm_lld_enable_channel_notification(pwmp, channel)
#define pwmDisableChannelNotificationI(pwmp,channel) pwm_lld_disable_channel_notification(pwmp, channel)
  void pwmInit(void);
  void pwmObjectInit(PWMDriver *pwmp);
  void pwmStart(PWMDriver *pwmp, const PWMConfig *config);
  void pwmStop(PWMDriver *pwmp);
  void pwmChangePeriod(PWMDriver *pwmp, pwmcnt_t period);
  void pwmEnableChannel(PWMDriver *pwmp,
                        pwmchannel_t channel,
                        pwmcnt_t width);
  void pwmDisableChannel(PWMDriver *pwmp, pwmchannel_t channel);
  void pwmEnablePeriodicNotification(PWMDriver *pwmp);
  void pwmDisablePeriodicNotification(PWMDriver *pwmp);
  void pwmEnableChannelNotification(PWMDriver *pwmp, pwmchannel_t channel);
  void pwmDisableChannelNotification(PWMDriver *pwmp, pwmchannel_t channel);
extern PWMDriver PWMD3;
extern PWMDriver PWMD4;
extern PWMDriver PWMD5;
extern PWMDriver PWMD8;

#ifdef __cplusplus
} // extern "C"
#endif

#endif
