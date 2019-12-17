#ifndef EXPORTS_HAL_EXPORTS_H
#define EXPORTS_HAL_EXPORTS_H

#include "ch.h"
#include "hal.h"


static int x_palReadPad(ioportid_t port, int pad) {
  return palReadPad(port,pad);
}

static void x_palWritePad(ioportid_t port, int pad, int bit) {
  palWritePad(port,pad,bit);
}

static void x_palSetPad(ioportid_t port, int pad) {
  palSetPad(port,pad);
}

static void x_palClearPad(ioportid_t port, int pad) {
  palClearPad(port,pad);
}

static void x_palTogglePad(ioportid_t port, int pad) {
  palTogglePad(port, pad);
}

static void x_palSetPadMode(ioportid_t port, int pad, int mode) {
  palSetPadMode(port,pad,mode);
}

#define EXPORTS_PAL_SYMBOLS \
  SYM2("palReadPad",x_palReadPad), \
  SYM2("palWritePad",x_palWritePad), \
  SYM2("palSetPad",x_palSetPad), \
  SYM2("palClearPad",x_palClearPad), \
  SYM2("palTogglePad",x_palTogglePad), \
  SYM2("palSetPadMode",x_palSetPadMode)

#define EXPORTS_I2C_SYMBOLS \
  SYM(i2cStart), \
  SYM(i2cStop), \
  SYM(i2cGetErrors), \
  SYM(i2cMasterTransmitTimeout), \
  SYM(i2cMasterReceiveTimeout), \
  SYM(i2cAcquireBus), \
  SYM(i2cReleaseBus), \
  SYM(I2CD1)

#define EXPORTS_SPI_SYMBOLS \
  SYM(spiStart), \
  SYM(spiStop), \
  SYM(spiSelect), \
  SYM(spiUnselect), \
  SYM(spiStartIgnore), \
  SYM(spiStartExchange), \
  SYM(spiStartSend), \
  SYM(spiStartReceive), \
  SYM(spiIgnore), \
  SYM(spiExchange), \
  SYM(spiSend), \
  SYM(spiReceive), \
  SYM(spiAcquireBus), \
  SYM(spiReleaseBus), \
  SYM(SPID1)

#define EXPORTS_PWM_SYMBOLS \
  SYM(pwmStart), \
  SYM(pwmStop), \
  SYM(pwmEnableChannel), \
  SYM(pwmChangePeriod), \
  SYM(PWMD3), \
  SYM(PWMD4), \
  SYM(PWMD5), \
  SYM(PWMD8)

#define EXPORTS_HAL_SYMBOLS \
  EXPORTS_PAL_SYMBOLS, \
  EXPORTS_I2C_SYMBOLS, \
  EXPORTS_SPI_SYMBOLS, \
  EXPORTS_PWM_SYMBOLS

#endif
