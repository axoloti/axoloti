#include "ff.h"
#include "axoloti_mi.h"
#include "mutable_instruments/elements/resources.h"

extern "C" {
    void LogTextMessage(const char* format, ...);
}

extern "C" void loadElementsData(void) {
    const int SAMPLE_SZ = 256026 / 2;
    const int NOISE_SZ = 81926 / 2;
    static int16_t smp_sample_data[SAMPLE_SZ] __attribute__ ((section (".sdram")));
    static int16_t smp_noise_data[NOISE_SZ] __attribute__ ((section (".sdram")));

    FIL FileObject;
    FRESULT err;
    UINT bytes_read;
    const char* fn;
    int i;

    fn = "/shared/elements/smp_sample_data.raw";
    err = f_open(&FileObject, fn, FA_READ | FA_OPEN_EXISTING);
    if (err != FR_OK) {
        LogTextMessage("Open failed: %s", fn);
        // clear from file end to array end
        for (i = 0; i < SAMPLE_SZ; i++) {
            smp_sample_data[i] = 0;
        }
        return;
    }
    err = f_read(&FileObject, smp_sample_data, sizeof(smp_sample_data), &bytes_read);
    if (err != FR_OK) {LogTextMessage("Read failed %s\n", fn); return;}
    err = f_close(&FileObject);
    if (err != FR_OK) {LogTextMessage("Close failed %s\n", fn); return;}

    i = bytes_read / 2; // 16 bit per sample
    for (; i < SAMPLE_SZ; i++) {
        smp_sample_data[i] = 0;
    }
    LogTextMessage("Bytes Read %s, %d\n", fn, bytes_read);

    elements::sample_table[0] = smp_sample_data;


    fn = "/shared/elements/smp_noise_data.raw";
    err = f_open(&FileObject, fn, FA_READ | FA_OPEN_EXISTING);
    if (err != FR_OK) {
        LogTextMessage("Open failed: %s", fn);
        // clear from file end to array end
        for (i = 0; i < NOISE_SZ; i++) {
            smp_noise_data[i] = 0;
        }
        return;
    }
    err = f_read(&FileObject, smp_noise_data, sizeof(smp_noise_data), &bytes_read);
    if (err != FR_OK) {LogTextMessage("Read failed %s\n", fn); return;}
    err = f_close(&FileObject);
    if (err != FR_OK) {LogTextMessage("Close failed %s\n", fn); return;}

    i = bytes_read / 2; // 16 bit per sample
// clear from file end to array end
    for (; i < NOISE_SZ; i++) {
        smp_noise_data[i] = 0;
    }

    LogTextMessage("Bytes Read %s, %d\n", fn, bytes_read);

    elements::sample_table[1] = smp_noise_data;

}