#include <sys/types.h>
#include <unistd.h>
#include "hal.h"
#include "semihosting_stream.h"
#include <unistd.h>

// avoid undefined reference when linking with nosys
__attribute__((weak))
void initialise_monitor_handles(void){
}

void semihosting_stream_init(void) {
	initialise_monitor_handles();
}

#define BUFFERED_WRITE_SZ 32
static char buff[BUFFERED_WRITE_SZ];
static int pos = 0;

static msg_t _put(void *ip, uint8_t b) {
	(void)ip;
	buff[pos++] = b;
	if ((pos == BUFFERED_WRITE_SZ) || (b == 0) || (b == '\n')) {
		write(STDOUT_FILENO, buff, pos);
		pos = 0;
	}
	return 0;
}

static size_t _writes(void *ip, const uint8_t *bp, size_t n) {
	(void)ip;
	if (n==1) {
		_put(ip, *bp);
		return 1;
	}
	if (pos) {
		write(STDOUT_FILENO, buff, pos);
		pos = 0;
	}
	write(STDOUT_FILENO,bp,n);
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

BaseSequentialStream SHS = {
		&vmt
};
