#ifndef QGFX_H
#define QGFX_H

/*
 * drawing functions specific for LCD quadrants
 * (topleft, topright, bottomleft, bottomright)
 * draft!
 */

typedef void (*fdrawStringN)(int x, int line, const char *string, int n);
typedef void (*fdrawChar)(int x, int line, const char ch);
typedef void (*fdrawNumber)(int x, int line, int i);
typedef void (*fsetEncoder)(int x);

typedef struct {
	fdrawStringN drawStringN;
	fdrawStringN drawStringInvN;
	fdrawChar drawChar;
	fdrawNumber drawNumber3D;
	fsetEncoder setEncoderOne;
} gfxq;

#define QGFX_QUADRANTS 4

extern const gfxq gfx_Q[QGFX_QUADRANTS];

#endif
