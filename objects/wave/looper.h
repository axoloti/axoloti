#include "ff.h"

const int SDREADFILEPINGPONGSIZE = 1024;
typedef struct {
  union {
    int32_t i32buff[SDREADFILEPINGPONGSIZE];
    int16_t i16buff[SDREADFILEPINGPONGSIZE * 2];
    int8_t i8buff[SDREADFILEPINGPONGSIZE * 4];
  };
} fbuffer;

typedef enum {
  CLOSED,
  OPEN,
  OPENED,
  PLAYB_READA,
  PLAYB,
  PLAYA_READB,
  PLAYA,
  SEEKING,
  CLOSING,
  OPENREC,
  OPENEDREC,
  RECB_WRITEA,
  RECB,
  RECA_WRITEB,
  RECA,
  CLOSINGREC
} stream_state;

#define SZ_TBL 64

typedef struct {
  Thread *pThreadSD;
  DWORD clmt[SZ_TBL];
  volatile stream_state playpong;
  volatile stream_state recpong;
  char filename[32];
  int roffset;
  int woffset;
  fbuffer rbuff0;
  fbuffer rbuff1;
  fbuffer wbuff0;
  fbuffer wbuff1;
//  int rpos;
  int wpos;
  int delay;
  FIL f;
} sdFilePingpongRW;

// pre_size : 128 MB
#define PRE_SIZE (1024*1024*128)

void clearbuffer(fbuffer *p) {
  int i;
  for (i = 0; i < SDREADFILEPINGPONGSIZE; i++) {
    p->i32buff[i] = 0;
  }
}

void stream_open(sdFilePingpongRW *s) {
  volatile FRESULT err;
  int i;
  UINT bytes_written;
  LogTextMessage("rec : opening");
  err = f_open(&s->f, &s->filename[0], FA_READ | FA_WRITE | FA_CREATE_ALWAYS);
  if (err != 0)
    LogTextMessage("rec : open fail");
  err = f_lseek(&s->f, PRE_SIZE);
  if (err != 0)
    LogTextMessage("rec : seek1 fail");
  err = f_lseek(&s->f, 0);
  if (err != 0)
    LogTextMessage("rec : seek2 fail");

  s->f.cltbl = s->clmt;
  s->clmt[0] = SZ_TBL;
  err = f_lseek(&s->f, CREATE_LINKMAP);

  clearbuffer(&s->rbuff0);
  clearbuffer(&s->rbuff1);
  clearbuffer(&s->wbuff0);
  clearbuffer(&s->wbuff1);

  f_lseek(&s->f, 0);
/*
  for(i=0;i<PRE_SIZE;i+=sizeof(s->wbuff0.i8buff)){
    f_write(&s->f,&s->wbuff0.i8buff,sizeof(s->wbuff0.i8buff),&bytes_written);
  }
*/
  s->roffset = 0;
  s->woffset = 0;
  s->recpong = RECB;
  s->playpong = PLAYA_READB;
}

void stream_close(sdFilePingpongRW *s) {
  volatile FRESULT err;

  chThdTerminate(s->pThreadSD);
  chThdWait(s->pThreadSD);

  LogTextMessage("rec : closing");
  err = f_close(&s->f);
  if (err != 0)
    LogTextMessage("rec : close fail");
}

#define __INL __attribute__ ((noinline))

static __INL msg_t ThreadSD(void *arg) {
  volatile FRESULT err;
  UINT bytes_read;
  while (!chThdShouldTerminate()) {
    int busy;
    do {
      busy = 0;
      sdFilePingpongRW *s = (sdFilePingpongRW *)arg;
      if (s->playpong == PLAYB_READA) {
        err = f_lseek(&s->f, (s->wpos - s->delay) & (PRE_SIZE - 1023));
        err = f_read(&s->f, s->rbuff0.i8buff, SDREADFILEPINGPONGSIZE * 4,
                     &bytes_read);
//        s->rpos = (s->rpos + SDREADFILEPINGPONGSIZE * 4) & (PRE_SIZE - 3);
        chSysDisable();
        s->playpong = PLAYB;
        chSysEnable();
        busy = 1;
      }
      else if (s->playpong == PLAYA_READB) {
        err = f_lseek(&s->f, (s->wpos - s->delay) & (PRE_SIZE - 1023));
        err = f_read(&s->f, s->rbuff1.i8buff, SDREADFILEPINGPONGSIZE * 4,
                     &bytes_read);
//        s->rpos = (s->rpos + SDREADFILEPINGPONGSIZE * 4) & (PRE_SIZE - 3);
        chSysDisable();
        s->playpong = PLAYA;
        chSysEnable();
        busy = 1;
      }
      else if (s->recpong == RECB_WRITEA) {
        err = f_lseek(&s->f, s->wpos);
        err = f_write(&s->f, s->wbuff0.i8buff, SDREADFILEPINGPONGSIZE * 4,
                      &bytes_read);
        s->wpos = (s->wpos + SDREADFILEPINGPONGSIZE * 4) & (PRE_SIZE - 3);
        chSysDisable();
        s->recpong = RECB;
        chSysEnable();
        busy = 1;
      }
      else if (s->recpong == RECA_WRITEB) {
        err = f_lseek(&s->f, s->wpos);
        err = f_write(&s->f, s->wbuff1.i8buff, SDREADFILEPINGPONGSIZE * 4,
                      &bytes_read);
        s->wpos = (s->wpos + SDREADFILEPINGPONGSIZE * 4) & (PRE_SIZE - 3);
        chSysDisable();
        s->recpong = RECA;
        chSysEnable();
        busy = 1;
      }
    } while ((busy != 0) && !chThdShouldTerminate());
    if (!chThdShouldTerminate())
      chEvtWaitAnyTimeout((eventmask_t)1, 10);
  }
  return (msg_t)0;
}

//#define BUFSIZE 16
__INL int16_t *sdReadStream(sdFilePingpongRW *s) {
  int16_t *p = 0;
  if ((s->playpong == PLAYB_READA) || (s->playpong == PLAYB)) {
    p = &s->rbuff1.i16buff[s->roffset];
    s->roffset += BUFSIZE;
    if (s->roffset == SDREADFILEPINGPONGSIZE * 2) {
      s->playpong = PLAYA_READB;
      s->roffset = 0;
      chEvtSignal(s->pThreadSD, (eventmask_t)1);
    }
  }
  else if ((s->playpong == PLAYA_READB) || (s->playpong == PLAYA)) {
    p = &s->rbuff0.i16buff[s->roffset];
    s->roffset += BUFSIZE;
    if (s->roffset == SDREADFILEPINGPONGSIZE * 2) {
      s->playpong = PLAYB_READA;
      s->roffset = 0;
      chEvtSignal(s->pThreadSD, (eventmask_t)1);
    }
  }
  else {
    p = 0;
  }
  return p;
}

/*
 * RECB_WRITEA
 * RECB
 * RECB
 * RECB
 * RECA_WRITEB
 * RECA
 * RECA
 * RECA
 */

int16_t * sdWriteStream(sdFilePingpongRW *s) {
  int16_t *p = 0;
  if ((s->recpong == RECB_WRITEA) || (s->recpong == RECB)) {
    p = &s->wbuff1.i16buff[s->woffset];
    if (s->woffset == 0) {
      s->recpong = RECB_WRITEA;
      chEvtSignal(s->pThreadSD, (eventmask_t)1);
    }
    s->woffset += BUFSIZE;
    if (s->woffset == SDREADFILEPINGPONGSIZE * 2) {
      s->recpong = RECA;
      s->woffset = 0;
    }
  }
  else if ((s->recpong == RECA_WRITEB) || (s->recpong == RECA)) {
    p = &s->wbuff0.i16buff[s->woffset];
    if (s->woffset == 0) {
      s->recpong = RECA_WRITEB;
      chEvtSignal(s->pThreadSD, (eventmask_t)1);
    }
    s->woffset += BUFSIZE;
    if (s->woffset == SDREADFILEPINGPONGSIZE * 2) {
      s->recpong = RECB;
      s->woffset = 0;
    }
  }
  else {
    p = 0;
  }
  return p;
}



