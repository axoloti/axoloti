#include "hal.h"
#include "null_stream.h"

static msg_t _put(void *ip, uint8_t b) {
	(void)ip;
	return 0;
}

static size_t _writes(void *ip, const uint8_t *bp, size_t n) {
	(void)ip;
	return n;
}

static size_t _reads(void *ip, uint8_t *bp, size_t n) {
  (void)ip;
  return 0;
}

static msg_t _get(void *ip) {
  (void)ip;
  return MSG_RESET;
}

static const struct BaseSequentialStreamVMT vmt = {(size_t)0, _writes, _reads, _put, _get};

BaseSequentialStream null_stream = {
		&vmt
};
