/**
 * Copyright (C) 2017 Johannes Taelman
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

#ifndef MIDI_ROUTING_H
#define MIDI_ROUTING_H

#define MIDI_VPORTS 16


typedef struct {
	char *name;
	int nports;
	int32_t bmvports[/*nports*/]; // nports long, bitmap: for every vport, 1: output, 0: ignore
} midi_output_routing_t;

/* -------
 * MIDI input port mapping
 *
 * all patch midi input and output to use 16 virtual input and output ports
 *
 * map each real input port to up to 4 "virtual" input port
 * map each "virtual" output port to up to 4 "virtual" output ports
 *
 * one fixed virtual input port # specifically for clock
 * one fixed virtual input port # specifically for regular keybd playing
 * a virtual port can serve for specific midi controllers,
 *   for example a launchpad, and be handled specifically without
 *   injecting note-events in a synth patch
 *
 * perhaps a few dedicated virtual ports ID's could serve for midi-thru routing
 *
 * how to trap midi/break feedback loops?
 */

typedef struct {
	char *name;
	int nports;
	int32_t bmvports[/*nports*/]; // bitmap: for every vport, 1: output, 0: ignore
} midi_input_remap_t;

#endif
