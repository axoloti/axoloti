#include "sdcard_menu.h"
#include "ff.h"
#include "patch.h"

// ------ SDCard menu stuff ------

int load_patch_index = 0;

void EnterMenuPatchLoad(void) {
	LoadPatchIndexed(load_patch_index);
	// TODO: show success/fail somehow
}

#define PatchLoadMenu_length 2
const ui_node_t PatchLoadMenu[PatchLoadMenu_length] = {
  { &nodeFunctionTable_integer_value, "Bank Index", .intValue = {.pvalue=&load_patch_index,.minvalue=0,.maxvalue=1<<15}},
  { &nodeFunctionTable_action_function, "Load", .fnctn = {&EnterMenuPatchLoad}}
};

static const ui_node_t SdcFormatFailed1 =
  { &nodeFunctionTable_action_function, "Format failed", .fnctn = {0}};
static const ui_node_t SdcFormatFailed =
  { &nodeFunctionTable_node_list, "Format", .nodeList = {&SdcFormatFailed1, 1}};

const ui_node_t SdcFormatOK1 =
  { &nodeFunctionTable_action_function, "Format OK", .fnctn = {0}};
static const ui_node_t SdcFormatOK =
  { &nodeFunctionTable_node_list, "Format", .nodeList = {&SdcFormatOK1, 1}};

// todo: test if formatting works
void EnterMenuFormat(void) {
	FRESULT err;
	BYTE work[FF_MAX_SS];
	err = f_mkfs(
		0, // path 
		FM_ANY, // opt
		0, // au
		work, sizeof work);
	if (err != FR_OK)
		ui_enter_node(&SdcFormatFailed);
	else
		ui_enter_node(&SdcFormatOK);
}

// TODO: Write - compose/modify a patchname...

const ui_node_t SdcMenu[SdcMenu_length] = {
  { &nodeFunctionTable_node_list, "Load patch", .nodeList = {&PatchLoadMenu, PatchLoadMenu_length}},
  { &nodeFunctionTable_action_function, "Format", .fnctn = {&EnterMenuFormat}},
  { &nodeFunctionTable_action_function, "Write", .fnctn = {&WritePatch}}
};
