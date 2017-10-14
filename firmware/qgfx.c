#include "axoloti_control.h"
#include "qgfx.h"

static void fdrawStringNQ1(int x, int line, const char *string, int n) {
	LCD_drawStringN(x, line+1,string,n);
}
static void fdrawStringNQ2(int x, int line, const char *string, int n) {
	LCD_drawStringN(63-x-3*n,line+1,string,n);
}
static void fdrawStringNQ3(int x, int line, const char *string, int n) {
	LCD_drawStringN(x,line+4,string,n);
}
static void fdrawStringNQ4(int x, int line, const char *string, int n) {
	LCD_drawStringN(63-x-3*n,line+4,string,n);
}

static void fdrawStringInvNQ1(int x, int line, const char *string, int n) {
	LCD_drawStringInvN(x, line+1,string,n);
}
static void fdrawStringInvNQ2(int x, int line, const char *string, int n) {
	LCD_drawStringInvN(63-x-3*n,line+1,string,n);
}
static void fdrawStringInvNQ3(int x, int line, const char *string, int n) {
	LCD_drawStringInvN(x,line+4,string,n);
}
static void fdrawStringInvNQ4(int x, int line, const char *string, int n) {
	LCD_drawStringInvN(63-x-3*n,line+4,string,n);
}


static void fdrawCharQ1(int x, int line, const char ch) {
	LCD_drawChar(x, line+1, ch);
}
static void fdrawCharQ2(int x, int line, const char ch) {
	LCD_drawChar(64-x-3, line+1, ch);
}
static void fdrawCharQ3(int x, int line, const char ch) {
	LCD_drawChar(x, line+4, ch);
}
static void fdrawCharQ4(int x, int line, const char ch) {
	LCD_drawChar(64-x-3, line+4, ch);
}
static void fdrawNumberQ1(int x, int line, int i) {
	LCD_drawNumber3D(x, line+1, i);
}
static void fdrawNumberQ2(int x, int line, int i) {
	LCD_drawNumber3D(64-x-12, line+1, i);
}
static void fdrawNumberQ3(int x, int line, int i) {
	LCD_drawNumber3D(x, line+4, i);
}
static void fdrawNumberQ4(int x, int line, int i) {
	LCD_drawNumber3D(64-x-12, line+4, i);
}
static void fsetEncoderQ1(int x) {
	LED_setOne(LED_RING_TOPLEFT, x);
}
static void fsetEncoderQ2(int x) {
	LED_setOne(LED_RING_TOPRIGHT, x);
}
static void fsetEncoderQ3(int x) {
	LED_setOne(LED_RING_BOTTOMLEFT, x);
}
static void fsetEncoderQ4(int x) {
	LED_setOne(LED_RING_BOTTOMRIGHT, x);
}

const gfxq gfx_Q[QGFX_QUADRANTS] = {
	{fdrawStringNQ1, fdrawStringInvNQ1, fdrawCharQ1, fdrawNumberQ1, fsetEncoderQ1},
	{fdrawStringNQ2, fdrawStringInvNQ2, fdrawCharQ2, fdrawNumberQ2, fsetEncoderQ2},
	{fdrawStringNQ3, fdrawStringInvNQ3, fdrawCharQ3, fdrawNumberQ3, fsetEncoderQ3},
	{fdrawStringNQ4, fdrawStringInvNQ4, fdrawCharQ4, fdrawNumberQ4, fsetEncoderQ4}
};
