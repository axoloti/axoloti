#include "ff.h"
#include "axoloti_mi.h"
#include "mutable_instruments/elements/resources.h"

extern "C" {
    void LogTextMessage(const char* format, ...);
}

extern "C" bool loadElementsData(int idx, const char* fn, int16_t sample_array[], int size) {

    FIL FileObject;
    FRESULT err;
    UINT bytes_read;
    int i;
    if (idx>1) {LogTextMessage("Elements sample array index must be < 2"); return false;}

    err = f_open(&FileObject, fn, FA_READ | FA_OPEN_EXISTING);
    if (err != FR_OK) {
        LogTextMessage("Open failed: %s", fn);
        // clear from file end to array end
        for (i = 0; i < size; i++) {
            sample_array[i] = 0;
        }
        return false;
    }
    err = f_read(&FileObject, sample_array, size * 2, &bytes_read);
    if (err != FR_OK) {LogTextMessage("Read failed %s\n", fn); return false;}
    err = f_close(&FileObject);
    if (err != FR_OK) {LogTextMessage("Close failed %s\n", fn); return false;}

    LogTextMessage("Bytes Read %s, %d\n", fn, bytes_read);
    i = bytes_read / 2; // 16 bit per sample
    for (; i < size; i++) {
        sample_array[i] = 0;
    }

    elements::sample_table[idx] = sample_array;
    return true;
}