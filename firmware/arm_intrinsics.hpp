#ifndef ARM_INTRINSICS_H_
#define ARM_INTRINSICS_H_

namespace arm {

// Signed Most Significant Word Multiply
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smmul (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smmul %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

// Signed Most Significant Word Multiply Accumulate
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smmla (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smmla %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3) );
  return(result);
}

// Signed Most Significant Word Multiply Subtract
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smmls (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smmls %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3) );
  return(result);
}

/// Signed Dual Multiply Add and Signed Dual Multiply Subtract.

// Signed Dual Multiply Add
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smuad (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smuad %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

// Signed Dual Multiply Add reverse
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smuadx (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smuadx %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

// Signed Dual Multiply Subtract
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smusd (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smusd %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

// Signed Dual Multiply Subtract reverse
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smusdx (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smusdx %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

/// SMUL Signed Multiply
/// Signed Multiply halfword by halfword

/// Signed Multiply top halfword by top halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smultt (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smultt %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

/// Signed Multiply bottom halfword by bottom halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smulbb (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smulbb %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

/// Signed Multiply top halfword by bottom halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smultb (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smultb %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

/// Signed Multiply bottom halfword by top halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smulbt (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smulbt %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

/// word by halfword

// Signed Multiply word by bottom halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smulwb (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smulwb %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

// Signed Multiply word by top halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smulwt (int32_t op1, int32_t op2)
{
  int32_t result;
  __ASM volatile ("smulwt %0, %1, %2" : "=r" (result) : "r" (op1), "r" (op2));
  return(result);
}

/// SMLA : Signed Multiply Accumulate
/// halfword by halfword

// Signed Multiply Accumulate bottom halfword by bottom halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smlabb (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smlabb %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3));
  return(result);
}

// Signed Multiply Accumulate bottom halfword by top halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smlabt (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smlabt %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3));
  return(result);
}

// Signed Multiply Accumulate top halfword by top halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smlatt (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smlatt %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3));
  return(result);
}

// Signed Multiply Accumulate top halfword by bottom halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smlatb (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smlatb %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3));
  return(result);
}

/// word by halfword

// Signed Multiply Accumulate top word by top halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smlawt (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smlawt %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3));
  return(result);
}

// Signed Multiply Accumulate top word by bottom halfword
__attribute__( ( always_inline ) ) __STATIC_INLINE int32_t smlawb (int32_t op1, int32_t op2, int32_t op3)
{
  int32_t result;
  __ASM volatile ("smlawb %0, %1, %2, %3" : "=r" (result) : "r" (op1), "r" (op2), "r" (op3));
  return(result);
}

/// floating point special ops

// floating point square root
__attribute__ ( ( always_inline ) ) __STATIC_INLINE float vsqrtf (float op1) {
  float result;
  __ASM volatile ("vsqrt.f32 %0, %1" : "=w" (result) : "w" (op1) );
  return(result);
}

__attribute__ ( ( always_inline ) ) __STATIC_INLINE float q_to_float(int32_t op1, int q) {
  float fop1 = *(float*)(&op1);
  __ASM volatile ("VCVT.F32.S32 %0, %0, %1" : "+w" (fop1) : "i" (q));
  return(fop1);
}

__attribute__ ( ( always_inline ) ) __STATIC_INLINE int32_t float_to_q(float op1, int q) {
  __ASM volatile ("VCVT.S32.F32 %0, %0, %1" : "+w" (op1) : "i" (q));
  return(*(int32_t*)(&op1));
}

}

#endif
