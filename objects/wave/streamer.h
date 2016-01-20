
#define SDREADFILEPINGPONGSIZE 256
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
  volatile stream_state pingpong;
  volatile int doSeek;
  volatile uint32_t seekPos;
  char filename[32];
  int offset;
  fbuffer fbuff0;
  fbuffer fbuff1;
  FIL f;
} sdReadFilePingpong;

// pre_size : 32 MB
#define PRE_SIZE (1024*1024*32)

#define __INL __attribute__ ((noinline))

static __INL msg_t ThreadSD(void *arg) {
  volatile FRESULT err;
  UINT bytes_read;
  while (!chThdShouldTerminate()) {
    int busy;
    do {
      busy = 0;
      sdReadFilePingpong *s = (sdReadFilePingpong *)arg;
      if (s->pingpong == OPEN) {
        if (s->offset >=0) {
          err = f_close(&s->f);
          if (err) report_fatfs_error(err,0);
        }
        err = f_open(&s->f, &s->filename[0], FA_READ | FA_OPEN_EXISTING);
        if (err) report_fatfs_error(err,&s->filename[0]);
//          s->f.cltbl = &s->clmt[0];                      /* Enable fast seek feature (cltbl != NULL) */
//          s->clmt[0] = SZ_TBL;                      /* Set table size */
//          err = f_lseek(&s->f, CREATE_LINKMAP);     /* Create CLMT */

        if (!s->doSeek) {
          err = f_read(&s->f, s->fbuff0.i8buff, SDREADFILEPINGPONGSIZE * 4,
                       &bytes_read);
          if (err) report_fatfs_error(err,&s->filename[0]);
          s->offset = 0;
          chSysDisable();
          if (s->pingpong != CLOSING)
            s->pingpong = PLAYA_READB;
          chSysEnable();
        }
        busy = 1;
      }
      else if (s->pingpong == OPENREC) {
        LogTextMessage("rec : opening");
        err = f_open(&s->f, &s->filename[0], FA_WRITE | FA_CREATE_ALWAYS);
        if (err) report_fatfs_error(err,&s->filename[0]);
        err = f_lseek(&s->f, PRE_SIZE);
        if (err) report_fatfs_error(err,&s->filename[0]);
        err = f_lseek(&s->f, 0);
        if (err) report_fatfs_error(err,&s->filename[0]);
        s->offset = 0;
        chSysDisable();
        if (s->pingpong != CLOSING)
          s->pingpong = RECB;
        chSysEnable();
        LogTextMessage("rec : open ok");
        busy = 1;
      }
      if (s->pingpong == CLOSING) {
        s->pingpong = CLOSED;
        err = f_close(&s->f);
        if (err) report_fatfs_error(err,&s->filename[0]);
        s->offset = -1;
        busy = 1;
      }
      else if (s->pingpong == CLOSINGREC) {
        s->pingpong = CLOSED;
        err = f_truncate(&s->f);
        err = f_close(&s->f);
        s->offset = -1;
        LogTextMessage("closerec");
        busy = 1;
      }
      else if (s->doSeek && (s->pingpong != CLOSED)) {
        s->pingpong = SEEKING;
        err = f_lseek(&s->f, s->seekPos);
        if (err) report_fatfs_error(err,&s->filename[0]);
        err = f_read(&s->f, s->fbuff0.i8buff, SDREADFILEPINGPONGSIZE * 4,
                     &bytes_read);
        if (err) report_fatfs_error(err,&s->filename[0]);
        s->offset = 0;
        s->doSeek = 0;
        chSysDisable();
        if (s->pingpong != CLOSING)
          s->pingpong = PLAYA_READB;
        chSysEnable();
        busy = 1;
      }
      if (s->pingpong == PLAYB_READA) {
        if (f_tell(&s->f) + SDREADFILEPINGPONGSIZE * 4 < f_size(&s->f)) {
          err = f_read(&s->f, s->fbuff0.i8buff, SDREADFILEPINGPONGSIZE * 4,
                       &bytes_read);
          if (err) report_fatfs_error(err,&s->filename[0]);
          chSysDisable();
          if (s->pingpong == PLAYB_READA)
            s->pingpong = PLAYB;
          if ((err != FR_OK) || (bytes_read != SDREADFILEPINGPONGSIZE * 4)) {
            s->pingpong = CLOSING;
          }
          chSysEnable();
        }
        else {
          chSysDisable();
          s->pingpong = CLOSING;
          chSysEnable();
        }
        busy = 1;
      }
      else if (s->pingpong == PLAYA_READB) {
        if (f_tell(&s->f) + SDREADFILEPINGPONGSIZE * 4 < f_size(&s->f)) {
          err = f_read(&s->f, s->fbuff1.i8buff, SDREADFILEPINGPONGSIZE * 4,
                       &bytes_read);
          if (err) report_fatfs_error(err,&s->filename[0]);
          chSysDisable();
          if (s->pingpong == PLAYA_READB)
            s->pingpong = PLAYA;
          chSysEnable();
          if ((err != FR_OK) || (bytes_read != SDREADFILEPINGPONGSIZE * 4)) {
            s->pingpong = CLOSING;
          }
        }
        else {
          chSysDisable();
          s->pingpong = CLOSING;
          chSysEnable();
        }
        busy = 1;
      }
      else if (s->pingpong == RECB_WRITEA) {
        err = f_write(&s->f, s->fbuff0.i8buff, SDREADFILEPINGPONGSIZE * 4,
                      &bytes_read);
        chSysDisable();
        if (s->pingpong == RECB_WRITEA)
          s->pingpong = RECB;
        chSysEnable();
        if (err != FR_OK) {
          s->pingpong = CLOSINGREC;
        }
        busy = 1;
      }
      else if (s->pingpong == RECA_WRITEB) {
        err = f_write(&s->f, s->fbuff1.i8buff, SDREADFILEPINGPONGSIZE * 4,
                      &bytes_read);
        chSysDisable();
        if (s->pingpong == RECA_WRITEB)
          s->pingpong = RECA;
        chSysEnable();
        if (err != FR_OK) {
          s->pingpong = CLOSINGREC;
        }
        busy = 1;
      }
      //}
    } while ((busy != 0)&&!chThdShouldTerminate());
    if (!chThdShouldTerminate())
        chEvtWaitAnyTimeout((eventmask_t)1, 10);
  }
  sdReadFilePingpong *s = (sdReadFilePingpong *)arg;
  if (s->pingpong != CLOSED) {
    err = f_close(&s->f);
    if (err) report_fatfs_error(err,&s->filename[0]);
  }
//  LogTextMessage("streamer thread : terminated");


  return (msg_t)0;
}

__INL void sdOpenStream(sdReadFilePingpong * s, const char *fn) {
  strcpy(&s->filename[0], fn);
  s->pingpong = OPEN;
  chEvtSignal(s->pThreadSD, (eventmask_t)1);
}

__INL void sdOpenStreamRec(sdReadFilePingpong * s, const char *fn) {
  strcpy(&s->filename[0], fn);
  s->pingpong = OPENREC;
  chEvtSignal(s->pThreadSD, (eventmask_t)1);
}

#define BUFSIZE 16
__INL int16_t *sdReadStream(sdReadFilePingpong *s) {
  int16_t *p = 0;
  if ((s->pingpong == PLAYB_READA) || (s->pingpong == PLAYB)) {
    p = &s->fbuff1.i16buff[s->offset];
    s->offset += BUFSIZE;
    if (s->offset == SDREADFILEPINGPONGSIZE * 2) {
      s->pingpong = PLAYA_READB;
      s->offset = 0;
      chEvtSignal(s->pThreadSD, (eventmask_t)1);
    }
  }
  else if ((s->pingpong == PLAYA_READB) || (s->pingpong == PLAYA)) {
    p = &s->fbuff0.i16buff[s->offset];
    s->offset += BUFSIZE;
    if (s->offset == SDREADFILEPINGPONGSIZE * 2) {
      s->pingpong = PLAYB_READA;
      s->offset = 0;
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

int16_t * sdWriteStream(sdReadFilePingpong *s) {
  int16_t *p = 0;
  if ((s->pingpong == RECB_WRITEA) || (s->pingpong == RECB)) {
    p = &s->fbuff1.i16buff[s->offset];
    if (s->offset == 0) {
      if (s->pingpong != CLOSINGREC)
        s->pingpong = RECB_WRITEA;
      chEvtSignal(s->pThreadSD, (eventmask_t)1);
    }
    s->offset += BUFSIZE;
    if (s->offset == SDREADFILEPINGPONGSIZE * 2) {
      if (s->pingpong != CLOSINGREC)
        s->pingpong = RECA;
      s->offset = 0;
    }
  }
  else if ((s->pingpong == RECA_WRITEB) || (s->pingpong == RECA)) {
    p = &s->fbuff0.i16buff[s->offset];
    if (s->offset == 0) {
      if (s->pingpong != CLOSINGREC)
        s->pingpong = RECA_WRITEB;
      chEvtSignal(s->pThreadSD, (eventmask_t)1);
    }
    s->offset += BUFSIZE;
    if (s->offset == SDREADFILEPINGPONGSIZE * 2) {
      if (s->pingpong != CLOSINGREC)
        s->pingpong = RECB;
      s->offset = 0;
    }
  }
  else {
    p = 0;
  }
  return p;
}

void sdSeekStream(sdReadFilePingpong *s, uint32_t pos) {
  if (s->pingpong == CLOSED)
    return;
  s->seekPos = pos;
  s->doSeek = 1;
  chEvtSignal(s->pThreadSD, (eventmask_t)1);
}

void sdCloseStream(sdReadFilePingpong *s) {
  if (s->pingpong == CLOSED)
    return;
  s->pingpong = CLOSING;
  chEvtSignal(s->pThreadSD, (eventmask_t)1);
}

void sdCloseStreamRec(sdReadFilePingpong *s) {
  if (s->pingpong == CLOSED)
    return;
  s->pingpong = CLOSINGREC;
  chEvtSignal(s->pThreadSD, (eventmask_t)1);
}

void sdStopStreamer(sdReadFilePingpong *s) {
  if (s->pThreadSD) {
    chThdTerminate(s->pThreadSD);
    chThdWait(s->pThreadSD);
  }
  s->pThreadSD = 0;
}
