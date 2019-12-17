#ifndef API_LEDS_BUTTONS_H
#define API_LEDS_BUTTONS_H

#ifdef __cplusplus
extern "C" {
#endif

bool readButton(int index);
void writeLed(int index, bool value);
void enableUserLeds(bool enable);

#ifdef __cplusplus
} // extern "C"
#endif

#endif
