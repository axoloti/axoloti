/**
 * Copyright (C) 2013 - 2017 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */

#include "ui.h"
#include "main_menu.h"
#include "loadpatch_menu.h"
#include "adc_menu.h"
#include "food_menu.h"
#include "midi_clock.h"
#include "midi_controller.h"
#include "midi_monitor.h"
#include "sdcard_menu.h"
#include "test_menu.h"
#include "processes_menu.h"

#include "patch.h"

// ------ Main menu stuff ------

ui_node_t MainMenu[MainMenu_length] = {
  { &nodeFunctionTable_loadpatch, "Load patch", .nodeList = {}},
  { &nodeFunctionTable_object_list, "Objects", .objList = {0,0}}, // at MAIN_MENU_INDEX_PATCH
  { &nodeFunctionTable_param_list, "Params", .paramList = {0,0}}, // at MAIN_MENU_INDEX_PARAMS
  { &nodeFunctionTable_node_list, "SDCard", .nodeList = {SdcMenu, SdcMenu_length}},
  { &nodeFunctionTable_node_list, "ADCs", .nodeList = {ADCMenu, ADCMenu_length}},
  { &nodeFunctionTable_integer_value, "dsp%", .intValue = {&dspLoadPct, 0, 100}},
  { &nodeFunctionTable_midiclock, "MIDI clock", .nodeList = {}},
  { &nodeFunctionTable_midicc, "MIDI controller", .nodeList = {}},
  { &nodeFunctionTable_midimon, "MIDI monitor", .nodeList = {}},
  { &nodeFunctionTable_processes, "Processes", .nodeList = {}},
  { &nodeFunctionTable_test, "Test", .nodeList = {}},
  { &nodeFunctionTable_node_list, "Food", .nodeList = {FoodMenu, FoodMenu_length}}
};

// ------ Root menu stuff ------

const ui_node_t RootMenu = {
  &nodeFunctionTable_node_list, "--- AXOLOTI ---", .nodeList = {MainMenu, MainMenu_length}
};


void ui_deinit_patch(void) {
	ui_go_home();
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.objs = 0;
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.nobjs = 0;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.params = 0;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.param_names = 0;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.nparams = 0;
}

void ui_init_patch(void) {
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.objs = patchMeta.objects;
	MainMenu[MAIN_MENU_INDEX_PATCH].objList.nobjs = patchMeta.nobjects;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.params = patchMeta.params;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.param_names = patchMeta.param_names;
	MainMenu[MAIN_MENU_INDEX_PARAMS].paramList.nparams = patchMeta.nparams;
}
