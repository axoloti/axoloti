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

#include "ch.h"
#include "hal.h"
#include "chprintf.h"
#include "shell.h"
#include "string.h"
#include "ui.h"
#include "axoloti_control.h"
#include "axoloti_board.h"
#include "sdcard.h"
#include "ff.h"

extern int _vectors;

// dummy ui hooks...

Btn_Nav_States_struct Btn_Nav_CurStates;
Btn_Nav_States_struct Btn_Nav_PrevStates;
Btn_Nav_States_struct Btn_Nav_Or;
Btn_Nav_States_struct Btn_Nav_And;
int8_t EncBuffer[4];


void refresh_LCD(void){
  int i;
  for(i=0;i<9;i++){
    do_axoloti_control();
    chThdSleepMilliseconds(20);
  }
}

void DispayAbortErr(int err){
  LCD_drawStringN(0,5,"error code:",128);
  LCD_drawNumber3D(0,6,(int)err);
  refresh_LCD();
  while(1){
    palWritePad(GPIOA,8,1);
    chThdSleepMilliseconds(1000);
    palWritePad(GPIOA,8,0);
    chThdSleepMilliseconds(1000);
  }
}


int FLASH_WaitForLastOperation(void)
{
  while(FLASH->SR == FLASH_SR_BSY)
    ;
  return FLASH->SR;
}

int Erase_sector(int sector ){
// assume VDD>2.7V
    FLASH->CR &=  ~FLASH_CR_PSIZE;
    FLASH->CR |= FLASH_CR_PSIZE_1;
    FLASH->CR &= ~FLASH_CR_SNB;
    FLASH->CR |= FLASH_CR_SER | (sector<<3);
    FLASH->CR |= FLASH_CR_STRT;
    FLASH_WaitForLastOperation();

    FLASH->CR &= (~FLASH_CR_SER);
    FLASH->CR &= ~FLASH_CR_SER;
    FLASH_WaitForLastOperation();

    return 0;
}

int FLASH_ProgramWord(uint32_t Address, uint32_t Data)
{
  int status;

  FLASH_WaitForLastOperation();

    /* if the previous operation is completed, proceed to program the new data */
  FLASH->CR &=  ~FLASH_CR_PSIZE;
  FLASH->CR |= FLASH_CR_PSIZE_1;
    FLASH->CR |= FLASH_CR_PG;

    *(__IO uint32_t*)Address = Data;

    /* Wait for last operation to be completed */
    status = FLASH_WaitForLastOperation();

    /* if the program operation is completed, disable the PG Bit */
    FLASH->CR &= (~FLASH_CR_PG);

  /* Return the Program Status */
  return status;
}


#define BUFSIZE 4096
uint8_t BUF[BUFSIZE];

int FLASH_ProgramBuf(int offset){
  int i;
  for(i=0;i<BUFSIZE;){
    FLASH_ProgramWord(offset,*((uint32_t *)&BUF[i]));
    i+=4;
    offset +=4;
  }
  return 0;
}

int main(void) {
  // copy vector table
  memcpy((char *)0x20000000, (const char *)&_vectors, 0x200);
  // remap SRAM1 to 0x00000000
  SYSCFG->MEMRMP |= 0x03;

  halInit();
  chSysInit();

  axoloti_board_init();

  axoloti_control_init();
  LCD_drawStringN(0,0,"--- Axoloti ---",128);
  LCD_drawStringN(0,1,"Flashing firmware",128);
  LCD_drawStringN(0,2,"  from SDCard",128);
  refresh_LCD();

  int i=0;
  sdcardInit();

  if (!fs_ready) {
    LCD_drawStringN(0,3,"No file system found.",128);
    LCD_drawStringN(0,4,"Aborting!",128);
    refresh_LCD();
    while(1){
      palWritePad(GPIOA,8,1);
      chThdSleepMilliseconds(1000);
      palWritePad(GPIOA,8,0);
      chThdSleepMilliseconds(500);
    }
  }

  FIL f;
  FRESULT err;
  err = f_open(&f, "firmware.bin", FA_READ | FA_OPEN_EXISTING);
  if (err != FR_OK)
    DispayAbortErr((int)err);

  UINT bytes_read = 0;
  int offset = 0x08000000; // flash base adress
  err = f_read(&f,BUF, BUFSIZE, &bytes_read);
  if (err != FR_OK)
    DispayAbortErr((int)err);

  // unlock sequence
  FLASH->KEYR = 0x45670123;
  FLASH->KEYR = 0xCDEF89AB;


  for(i=0;i<12;i++){
    Erase_sector(i);
    LCD_drawStringN(0,3,"Erased sector",128);
    LCD_drawNumber3D(80,3,i);
    refresh_LCD();
    palWritePad(GPIOA,8,1);
    chThdSleepMilliseconds(100);
    palWritePad(GPIOA,8,0);
  }

  FLASH_ProgramBuf(offset);

  while(bytes_read){
    err = f_read(&f,BUF, BUFSIZE, &bytes_read);
    if (err != FR_OK)
      DispayAbortErr((int)err);
    offset+=BUFSIZE;
    FLASH_ProgramBuf(offset);
    LCD_drawStringN(0,3,"Written block",128);
    LCD_drawNumber3D(80,3,(offset&0xFFFFFF)/BUFSIZE);
    refresh_LCD();
    palWritePad(GPIOA,8,1);
    chThdSleepMilliseconds(100);
    palWritePad(GPIOA,8,0);
  }

  LCD_drawStringN(0,4,"Flashing done.",128);
  refresh_LCD();

  palSetPadMode(GPIOA,8,PAL_MODE_OUTPUT_PUSHPULL);
  for(i=0;i<10;i++)  {
    palWritePad(GPIOA,8,1);
    chThdSleepMilliseconds(1000);
    palWritePad(GPIOA,8,0);
    chThdSleepMilliseconds(500);
    LCD_drawNumber3D(0,7,i);
    i++;
    refresh_LCD();
  }

  NVIC_SystemReset();
  return 0;
}

