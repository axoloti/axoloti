
const q31_t armRecipTableQ31[64] = {
  0x7F03F03F, 0x7D137420, 0x7B31E739, 0x795E9F94, 0x7798FD29, 0x75E06928,
  0x7434554D, 0x72943B4B, 0x70FF9C40, 0x6F760031, 0x6DF6F593, 0x6C8210E3,
  0x6B16EC3A, 0x69B526F6, 0x685C655F, 0x670C505D, 0x65C4952D, 0x6484E519,
  0x634CF53E, 0x621C7E4F, 0x60F33C61, 0x5FD0EEB3, 0x5EB55785, 0x5DA03BEB,
  0x5C9163A1, 0x5B8898E6, 0x5A85A85A, 0x598860DF, 0x58909373, 0x579E1318,
  0x56B0B4B8, 0x55C84F0B, 0x54E4BA80, 0x5405D124, 0x532B6E8F, 0x52556FD0,
  0x5183B35A, 0x50B618F3, 0x4FEC81A2, 0x4F26CFA2, 0x4E64E64E, 0x4DA6AA1D,
  0x4CEC008B, 0x4C34D010, 0x4B810016, 0x4AD078EF, 0x4A2323C4, 0x4978EA96,
  0x48D1B827, 0x482D77FE, 0x478C1657, 0x46ED801D, 0x4651A2E5, 0x45B86CE2,
  0x4521CCE1, 0x448DB244, 0x43FC0CFA, 0x436CCD78, 0x42DFE4B4, 0x42554426,
  0x41CCDDB6, 0x4146A3C6, 0x40C28923, 0x40408102
};


void f_filter_biquad_A2(data_filter_biquad_A *v,const int32_t *sourcebuf,int32_t *destbuf,uint32_t filter_W0,uint32_t q_inv) {
// reference http://www.musicdsp.org/files/Audio-EQ-Cookbook.txt
// LPF
  int32_t *destbufcopy = destbuf;
  if (filter_W0>(INT32_MAX/4)) filter_W0 = INT32_MAX/4;
//    filter_W0 = filter_W0<<2;

    int32_t sinW0;// = arm_sin_q31(filter_W0);
    int32_t cosW0;// = arm_cos_q31(filter_W0);
    int a = filter_W0;
    int b = filter_W0 + (1<<30);

    SINE2TINTERP(a,sinW0)
    SINE2TINTERP(b,cosW0)

//    int32_t sinW0 = fsini(filter_W0);
//    int32_t cosW0 = fsini(filter_W0+(INT32_MAX>>2));
    int32_t alpha = ___SMMUL(sinW0,q_inv);
//    int32_t alpha = sinW0>>8;
    int32_t filter_x_n1 = v->filter_x_n1;
    int32_t filter_x_n2 = v->filter_x_n2;
    int32_t filter_y_n1 = v->filter_y_n1;
    int32_t filter_y_n2 = v->filter_y_n2;
#if 1
    float filter_a0 = (HALFQ31 + alpha);
    float filter_a0_inv = ((INT32_MAX>>2) / filter_a0);
    float filter_a0_inv_x2 = ((INT32_MAX>>1) / filter_a0);
    //float filter_a0_inv_x4 = ((INT32_MAX) / filter_a0);
    int32_t a0_inv_q31 = (int32_t)(INT32_MAX*filter_a0_inv);
    int32_t a0_inv_q31_x2 = (int32_t)(INT32_MAX*filter_a0_inv_x2);
    //int32_t a0_inv_q31_x4 = (int32_t)(INT32_MAX*filter_a0_inv_x4);
#else
//    int32_t a0_inv_q31_x4;
//    arm_recip_q31((HALFQ31 + alpha)>>26, (q31_t *)&a0_inv_q31_x4, (q31_t *)&armRecipTableQ31[0]);
//    int32_t a0_inv_q31_x4_v2 =
//    a0_inv_q31_x4_v2 = a0_inv_q31_x4_v2 * 2;
    //a0_inv_q31_x4 = a0_inv_q31_x4;
    int32_t a0_inv_q31_x2 = (((int64_t)1)<<61)/(((int64_t)(HALFQ31 + alpha)));
#endif
    int32_t filter_a1 = ___SMMUL(-(-cosW0),a0_inv_q31_x2); // negated
    int32_t filter_a2 = ___SMMUL(-(HALFQ31 - alpha),a0_inv_q31_x2); // negated
//    int32_t filter_b0 = ___SMMUL(HALFQ31 - (cosW0>>1),a0_inv_q31_x4);
    int32_t filter_b0 = ___SMMUL(INT32_MAX - cosW0,a0_inv_q31_x2)<<1;
    int32_t filter_b1 = (filter_b0>>1);
    int i;
    for(i=0;i<BUFSIZE;i++) {
      int32_t filterinput = *(sourcebuf++);
      int64_t accu = (int64_t)filter_b0*filterinput;
      accu += (int64_t)filter_b0*(int64_t)filter_x_n2;
      accu += (int64_t)filter_b1*(int64_t)filter_x_n1;
      accu += (int64_t)filter_a1*(int64_t)filter_y_n1;
      accu += (int64_t)filter_a2*(int64_t)filter_y_n2;
      //accu +=
      int32_t filteroutput;
      filteroutput = (accu>>29);//__SSAT(accu,28)<<4;
      filter_x_n2 = filter_x_n1;
      filter_x_n1 = filterinput;
      filter_y_n2 = filter_y_n1;
      filter_y_n1 = filteroutput;
      *(destbuf++) = filteroutput;
    }
    v->filter_x_n1 = filter_x_n1;
    v->filter_x_n2 = filter_x_n2;
    v->filter_y_n1 = filter_y_n1;
    v->filter_y_n2 = filter_y_n2;
/*
    destbufcopy[0] = a0_inv_q31_x2>>4;
    destbufcopy[1] = a0_inv_q31_x2_v2>>4;
    destbufcopy[2] = a0_inv_q31_x2>>6;
    destbufcopy[3] = a0_inv_q31_x2_v2>>6;
    destbufcopy[4] = a0_inv_q31_x2>>8;
    destbufcopy[5] = a0_inv_q31_x2_v2>>8;
    destbufcopy[6] = a0_inv_q31_x2>>10;
    destbufcopy[7] = a0_inv_q31_x2_v2>>10;
    */
}
