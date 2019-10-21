#include "error_codes.h"

const char * errorCodeToString(int error_code) {
    switch(error_code) {
        case error_outOfMemory:
            return "out of memory";
        case error_fileNotFound:
            return "file not found";
        case error_fileOperationFailed:
            return "file operation failed";
        default:
            return 0;
    }
}
