#include "../chibios/ext/fatfs/src/ff.h"
#include "../chibios/os/various/chprintf.c"

#define SDREADFILEPINGPONGSIZE 2048
typedef struct {
  union {
    int32_t i32buff[SDREADFILEPINGPONGSIZE];
    int16_t i16buff[SDREADFILEPINGPONGSIZE * 2];
    int8_t i8buff[SDREADFILEPINGPONGSIZE * 4];
  };
} fbuffer;

#define SZ_TBL 64

typedef struct {
  FIL f;
  Thread *pThreadSD;
  DWORD clmt[SZ_TBL];
  char filename[32];
  fbuffer fbuff0;
  fbuffer fbuff1;
} sdReadFilePingpong;

sdReadFilePingpong sdStreams[2] __attribute__ ((section (".sram")));
#define PRE_SIZE (1024*1024*128)

void reportFError(FRESULT err){
      TransmitTextMessageHeader();
      chprintf((BaseSequentialStream *)&SDU1, "fatfs err %i\r\n%c",err,0);
}

void BenchmarkBS(int bufsize, int nstreams){
  int i;
  int nbuf = 128;

  // create filenames
  for(i=0;i<nstreams;i++){
      sdReadFilePingpong *s = (sdReadFilePingpong*)(0x20000000 | (int)&sdStreams[i]);
    s->filename[0]='x';
    s->filename[1]='0'+i/10;
    s->filename[2]='0'+i%10;
    s->filename[3]='.';
    s->filename[4]='b';
    s->filename[5]='i';
    s->filename[6]='n';
    s->filename[7]=0;
  }
  volatile FRESULT err;

  int t0 = chTimeNow();
  // open for write
  for(i=0;i<nstreams;i++){
      sdReadFilePingpong *s = (sdReadFilePingpong*)(0x20000000 | (int)&sdStreams[i]);
    err = f_open(&s->f, &s->filename[0], FA_WRITE | FA_CREATE_ALWAYS);
    if (err) reportFError(err); else TransmitTextMessage("Open OK...");
    err = f_lseek(&s->f, PRE_SIZE);
    if (err) reportFError(err); else TransmitTextMessage("lseek1 OK...");
    err = f_lseek(&s->f, 0);
    if (err) reportFError(err); else TransmitTextMessage("lseek2 OK...");

    s->f.cltbl = &s->clmt[0]; /* Enable fast seek feature (cltbl != NULL) */
    s->clmt[0] = SZ_TBL; /* Set table size */
    err = f_lseek(&s->f, CREATE_LINKMAP); /* Create CLMT */
    if (err) {
      TransmitTextMessageHeader();
      chprintf((BaseSequentialStream *)&SDU1, "lseek err %i\r\n%c",err,0);
    }else TransmitTextMessage("lseek3 OK...");
  }
  // write
  int t1 = chTimeNow();
  unsigned int bytes_read;
  int j;
  for(j=0;j<nbuf;j++){
    for(i=0;i<nstreams;i++){
      sdReadFilePingpong *s = (sdReadFilePingpong*)(0x20000000 | (int)&sdStreams[i]);
      err = f_write(&s->f, s->fbuff0.i8buff, bufsize, &bytes_read);
      if (err) reportFError(err);
    }
  }
  int t2 = chTimeNow();
  // close
  for(i=0;i<nstreams;i++){
    sdReadFilePingpong *s = (sdReadFilePingpong*)(0x20000000 | (int)&sdStreams[i]);
    err = f_close(&s->f);
    if (err) reportFError(err); 
  }
  // open for read
  int t3 = chTimeNow();
  for(i=0;i<nstreams;i++){
    sdReadFilePingpong *s = (sdReadFilePingpong*)(0x20000000 | (int)&sdStreams[i]);
    err = f_open(&s->f, &s->filename[0], FA_READ | FA_OPEN_EXISTING);
    s->f.cltbl = &s->clmt[0]; /* Enable fast seek feature (cltbl != NULL) */
    s->clmt[0] = SZ_TBL; /* Set table size */
    err = f_lseek(&s->f, CREATE_LINKMAP); /* Create CLMT */
    if (err) {
        TransmitTextMessageHeader();
        chprintf((BaseSequentialStream *)&SDU1, "lseek err %i\r\n%c",err,0);
    }
  }
  // read
  int t4 = chTimeNow();
  for(j=0;j<nbuf;j++){
    for(i=0;i<nstreams;i++){
      sdReadFilePingpong *s = (sdReadFilePingpong*)(0x20000000 | (int)&sdStreams[i]);
      err = f_read(&s->f, s->fbuff0.i8buff, bufsize, &bytes_read);
      if (err) reportFError(err); 
    }
  }
  int t5 = chTimeNow();
  // close
  for(i=0;i<nstreams;i++){
      sdReadFilePingpong *s = (sdReadFilePingpong*)(0x20000000 | (int)&sdStreams[i]);
    err = f_close(&s->f);
  }
  TransmitTextMessageHeader();
  chprintf((BaseSequentialStream *)&SDU1, "nstreams = %i, ",nstreams);
  chprintf((BaseSequentialStream *)&SDU1, "NBUFFERS = %i, ",nbuf);
  chprintf((BaseSequentialStream *)&SDU1, "BUFSIZE = %i\r\n",bufsize);
  chprintf((BaseSequentialStream *)&SDU1, "open : %i ms\r\n",t1-t0);
//  chprintf((BaseSequentialStream *)&SDU1, "write : %i ms\r\n",t2-t1);
  chprintf((BaseSequentialStream *)&SDU1, "write BW : %i kB/s\r\n",(nstreams*nbuf*bufsize)/(t2-t1));
  chprintf((BaseSequentialStream *)&SDU1, "close : %i ms\r\n",t3-t2);
  chprintf((BaseSequentialStream *)&SDU1, "open : %i ms\r\n",t4-t3);
//  chprintf((BaseSequentialStream *)&SDU1, "read : %i ms\r\n",t5-t4,0);
  chprintf((BaseSequentialStream *)&SDU1, "read BW : %i kB/s\r\n%c",(nstreams*nbuf*bufsize)/(t5-t4),0);
}

static msg_t ThreadBenchmarkSD(void *arg) {
  TransmitTextMessage("Starting sdcard benchmark. Wait...");
  BenchmarkBS(SDREADFILEPINGPONGSIZE * 4, 1);
  BenchmarkBS(SDREADFILEPINGPONGSIZE * 4, 2);
  BenchmarkBS(SDREADFILEPINGPONGSIZE * 2, 1);
  BenchmarkBS(SDREADFILEPINGPONGSIZE * 2, 2);
  BenchmarkBS(SDREADFILEPINGPONGSIZE * 1, 1);
  BenchmarkBS(SDREADFILEPINGPONGSIZE * 1, 2);
  BenchmarkBS(SDREADFILEPINGPONGSIZE / 2, 1);
  BenchmarkBS(SDREADFILEPINGPONGSIZE / 2, 2);
  TransmitTextMessage("SDCard benchmark finished.");
}

static WORKING_AREA(waThreadSD0, 1024) __attribute__ ((section (".data")));

void sdbenchmark(void){
  sdStreams[0].pThreadSD = chThdCreateStatic(waThreadSD0, sizeof(waThreadSD0), NORMALPRIO, ThreadBenchmarkSD, NULL);
}
