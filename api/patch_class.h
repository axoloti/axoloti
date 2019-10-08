#ifndef API_PATCH_CLASS_H
#define API_PATCH_CLASS_H

/*
 * patch ELF's must implement all of this
 */

class PatchInstance;

extern "C" {
  int getInstanceSize();
  int initInstance(PatchInstance *instance /*,... args */);
};

typedef enum {
  ax_prop_displayvector = 0x00,
  ax_prop_displayvector_size = 0x01,
  ax_prop_nparams = 0x02,
  ax_prop_param = 0x03,
  ax_prop_paramName = 0x04,
  ax_prop_presetData = 0x05,
  ax_prop_applyPreset = 0x100
} ax_property_id_t;

class PatchInstance {
public:
  // audio_in and audio_out are
  // non-interleaved audio samples:
  // BUFSIZE samples left channel, BUFSIZE samples right channel
  // audio_out should not be assumed to be zeroes, clear if needed.
  virtual void tick(int32_t * audio_in, int32_t * audio_out) = 0;

  virtual void midiInHandler(int32_t m) = 0;

  // calling getProperty with unimplemented id's should return 0
  virtual void* getProperty(ax_property_id_t id, int index);
  // calling setProperty with unimplemented id's should return -1
  virtual int setProperty(ax_property_id_t id, int index, void * value);

  virtual void dispose() = 0;

  virtual int reserved1() = 0;
  virtual int reserved2() = 0;
};

#endif
