#ifndef MIDI_CLOCK_H_
#define MIDI_CLOCK_H_

// midi clock
typedef struct {
  int active;
  int32_t period;
  int32_t counter;
  int32_t song_position;
} midi_clock_t;

extern midi_clock_t midi_clock;

#endif
