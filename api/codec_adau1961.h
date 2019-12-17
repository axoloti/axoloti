#ifndef API_CODEC_ADAU1961_H
#define API_CODEC_ADAU1961_H

#ifdef __cplusplus
extern "C" {
#endif

void ADAU1961_WriteRegister(uint16_t RegisterAddr,
                                   uint8_t RegisterValue);

#ifdef __cplusplus
} // extern "C"
#endif

#endif
