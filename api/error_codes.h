#ifndef API_ERROR_CODES_H
#define API_ERROR_CODES_H

// return codes for initInstance()
enum {
    error_outOfMemory  = 0x0100,
    error_fileNotFound = 0x0101,
    error_fileOperationFailed = 0x0102
};

#endif
