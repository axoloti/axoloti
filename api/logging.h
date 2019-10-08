#ifndef API_LOGGING_H
#define API_LOGGING_H

#include <stdarg.h>

#ifdef __cplusplus
extern "C" {
#endif

extern void log_vprintf(const char * format, va_list arg );
void report_fatfs_error(int errno, const char *fn);

static void LogTextMessage(const char * format, ...) {
  va_list args;
  va_start (args, format);
  log_vprintf (format, args);
  va_end (args);

  log_vprintf("\n", args);
}

static void log_printf(const char * format, ...) {
  va_list args;
  va_start (args, format);
  log_vprintf (format, args);
  va_end (args);
}

#ifdef __cplusplus
} // extern "C"
#endif

#endif
