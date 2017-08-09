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

#ifndef UI_MENU_CONTENT_H
#define UI_MENU_CONTENT_H

#define MAIN_MENU_INDEX_PATCH 1
#define MAIN_MENU_INDEX_PARAMS 2

#define MainMenu_length 12
extern ui_node_t MainMenu[MainMenu_length];

void ui_deinit_patch(void);
void ui_init_patch(void);

#endif
