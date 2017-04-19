#ifndef PATCH_CHUNKS_H
#define PATCH_CHUNKS_H

#include "fourcc.h"
#include "parameters.h"
#include "patch.h"

#define fourcc_patch_meta FOURCC('P','T','C','H')
typedef struct {
	chunk_header_t header;
	int32_t patchID;
	char patchname[64];
} chunk_patch_meta_t;

#define fourcc_patch_preset FOURCC('P','R','S','T')
typedef struct {
	chunk_header_t header;
	int npresets;
	int npreset_entries;
	void *pPresets;
} chunk_patch_preset_t;

#define fourcc_patch_display_meta FOURCC('D','I','S','M')
typedef struct {
	chunk_header_t header;
	int32_t ndisplay_metas;
	Display_meta_t *pDisplay_metas;
} chunk_patch_display_meta_t;

#define fourcc_patch_parameter FOURCC('P','A','R','M')
typedef struct {
	chunk_header_t header;
	uint32_t nparams;
	Parameter_t *pParams;
	Parameter_name_t *pParam_names;
} chunk_patch_parameter_t;

#define fourcc_patch_ui_objects FOURCC('U','I','O','B')
typedef struct {
	chunk_header_t header;
	int nobjects;
	ui_object_t *pObjects;
} chunk_patch_ui_objects_t;

#define fourcc_patch_initpreset FOURCC('P','R','I','N')
typedef struct {
	chunk_header_t header;
	uint32_t initpreset_size;
	void *pInitpreset;
} chunk_patch_initpreset_t;

#define fourcc_patch_display FOURCC('D','I','S','P')
typedef struct {
	chunk_header_t header;
	int32_t ndisplayVector;
	int32_t *pDisplayVector;
} chunk_patch_display_t;

#define fourcc_patch_functions FOURCC('P','F','U','N')
typedef struct {
	chunk_header_t header;
	fptr_patch_init_t fptr_patch_init;
	fptr_patch_dispose_t fptr_patch_dispose;
	fptr_patch_dsp_process_t fptr_dsp_process;
	fptr_patch_midi_in_handler_t fptr_MidiInHandler;
	fptr_patch_applyPreset_t fptr_applyPreset;
} chunk_patch_functions_t;

#endif
