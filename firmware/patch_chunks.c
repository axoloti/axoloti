#include "patch_chunk_reader.h"
#include "patch_chunks.h"

#pragma GCC optimize ("Og")

static void readchunk_patch_meta(chunk_patch_meta_t *chunk) {
	patchMeta.patchID = chunk->patchID;
	// patch name currently unused
}

static void readchunk_patch_preset(chunk_patch_preset_t *chunk) {
	patchMeta.npresets = chunk->npresets;
	patchMeta.npreset_entries = chunk->npreset_entries;
	patchMeta.pPresets = chunk->pPresets;
}

static void readchunk_patch_display_meta(chunk_patch_display_meta_t *chunk) {
	patchMeta.ndisplay_metas = chunk->ndisplay_metas;
	patchMeta.pDisplay_metas = chunk->pDisplay_metas;
}

static void readchunk_patch_parameter(chunk_patch_parameter_t *chunk) {
	patchMeta.params = chunk->pParams;
	patchMeta.nparams = chunk->nparams;
	patchMeta.param_names = chunk->pParam_names;
}

static void readchunk_patch_ui_objects(chunk_patch_ui_objects_t *chunk) {
	patchMeta.objects = chunk->pObjects;
	patchMeta.nobjects = chunk->nobjects;
}

static void readchunk_patch_initpreset(chunk_patch_initpreset_t *chunk) {
	patchMeta.initpreset_size = chunk->initpreset_size;
	patchMeta.pInitpreset = chunk->pInitpreset;
}

static void readchunk_patch_display(chunk_patch_display_t *chunk) {
	patchMeta.ndisplayVector_size = chunk->ndisplayVector;
	patchMeta.pDisplayVector = chunk->pDisplayVector;
}

static void readchunk_patch_functions(chunk_patch_functions_t *chunk) {
	patchMeta.fptr_patch_init = chunk->fptr_patch_init;
	patchMeta.fptr_patch_dispose = chunk->fptr_patch_dispose;
	patchMeta.fptr_dsp_process = chunk->fptr_dsp_process;
	patchMeta.fptr_applyPreset = chunk->fptr_applyPreset;
	patchMeta.fptr_MidiInHandler = chunk->fptr_MidiInHandler;
}

#define CASE_CHUNK(name) \
case fourcc_##name: readchunk_##name((chunk_##name##_t *)current); break;

void readchunk_patch_root(chunk_header_t *chunk) {
	chunk_header_t *end = (chunk_header_t *)(((char *)(chunk+1))+chunk->size);
	chunk_header_t *current = chunk+1;
	while (current != end) {
		switch (current->fourcc) {
		CASE_CHUNK(patch_meta)
		CASE_CHUNK(patch_preset)
		CASE_CHUNK(patch_display_meta)
		CASE_CHUNK(patch_parameter)
		CASE_CHUNK(patch_ui_objects)
		CASE_CHUNK(patch_initpreset)
		CASE_CHUNK(patch_display)
		CASE_CHUNK(patch_functions)
		default:
			break;
		}
		current = (chunk_header_t *)(((char *)(current+1))+current->size);
	}
	return;
}
