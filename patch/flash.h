#include "stm32f4xx.h"

int FLASH_WaitForLastOperation(void)
{
  while(FLASH->SR == FLASH_SR_BSY)
    ;
  return FLASH->SR;
}

int Erase_sector(int sector ){
// unlock sequence
    FLASH->KEYR = 0x45670123;
    FLASH->KEYR = 0xCDEF89AB;
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
