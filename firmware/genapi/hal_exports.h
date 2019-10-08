#define TRUE 1
#define FALSE 0

#define HAL_LLD_H

#include "halconf.h"

// DMA streams

struct stm32_dma_stream_t;

// PAL

//#define HAL_PAL_LLD_H
#include "os/hal/include/hal_pal.h"

// I2C

#define HAL_I2C_LLD_H
typedef struct abstract I2CDriver;
typedef struct abstract I2CConfig;
typedef uint32_t i2cflags_t;
typedef uint16_t i2caddr_t;
#include "os/hal/include/hal_i2c.h"
#undef _i2c_wakeup_isr
#undef _i2c_wakeup_error_isr
extern I2CDriver I2CD1;

// SPI

#define HAL_SPI_LLD_H
#define spi_lld_config_fields                                               \
  /* SPI CR1 register initialization data.*/                                \
  uint16_t                  cr1;                                            \
  /* SPI CR2 register initialization data.*/                                \
  uint16_t                  cr2

/**
 * @brief   Low level fields of the SPI driver structure.
 */
#define spi_lld_driver_fields                                               \
  /* Pointer to the SPIx registers block.*/                                 \
  SPI_TypeDef               *spi;                                           \
  /* Receive DMA stream.*/                                                  \
  const stm32_dma_stream_t  *dmarx;                                         \
  /* Transmit DMA stream.*/                                                 \
  const stm32_dma_stream_t  *dmatx;                                         \
  /* RX DMA mode bit mask.*/                                                \
  uint32_t                  rxdmamode;                                      \
  /* TX DMA mode bit mask.*/                                                \
  uint32_t                  txdmamode

#include "os/hal/include/hal_spi.h"
extern SPIDriver SPID1;
#undef _spi_wakeup_isr
#undef _spi_isr_code
#undef _spi_isr_half_code
#undef _spi_isr_full_code

// PWM
#define STM32_HAS_TIM3 TRUE
#define STM32_HAS_TIM4 TRUE
#define STM32_HAS_TIM5 TRUE
#define STM32_HAS_TIM8 TRUE
#define STM32_TIM_MAX_CHANNELS 4
#define OSAL_IRQ_IS_VALID_PRIORITY(x) (TRUE)
#include "os/hal/include/hal_pwm.h"
extern PWMDriver PWMD3;
extern PWMDriver PWMD4;
extern PWMDriver PWMD5;
extern PWMDriver PWMD8;
