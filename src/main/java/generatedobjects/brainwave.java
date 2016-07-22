/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
package generatedobjects;

import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32;

/**
 *
 * @author Johannes Taelman
 */
public class brainwave extends gentools {

    static void GenerateAll() {
        String catName = "brainwave";
//UNRELEASED                WriteAxoObject(catName, Create_brainwave());
    }

    static AxoObject Create_brainwave() {
        AxoObject o = new AxoObject("read", "script with 2 inputs and 2 outputs, running in a separate thread, you must define \"void init(void){}\" and \"void loop(void)\"");
        o.outlets.add(new OutletFrac32("quality", "signal quality"));
        o.outlets.add(new OutletFrac32("attention", "attention"));
        o.outlets.add(new OutletFrac32("meditation", "meditation"));
        o.outlets.add(new OutletFrac32("low_alpha", "low alpha"));
        o.outlets.add(new OutletFrac32("high_alpha", "high_alpha"));
        o.outlets.add(new OutletFrac32("low_beta", "low beta"));
        o.outlets.add(new OutletFrac32("high_beta", "high_beta"));
        o.outlets.add(new OutletFrac32("low_gamma", "low gamma"));
        o.outlets.add(new OutletFrac32("high_gamma", "high_gamma"));
        o.outlets.add(new OutletFrac32("delta", "delta"));
        o.outlets.add(new OutletFrac32("theta", "theta"));
        o.attributes.add(new AxoAttributeTextEditor("script"));
        o.sLocalData = "int32_t _quality;\n"
                + "int32_t _attention;\n"
                + "int32_t _meditation;\n"
                + "int32_t _low_alpha;\n"
                + "int32_t _high_alpha;\n"
                + "int32_t _low_beta;\n"
                + "int32_t _high_beta;\n"
                + "int32_t _low_gamma;\n"
                + "int32_t _high_gamma;\n"
                + "int32_t _delta;\n"
                + "int32_t _theta;\n"
                + ""
                + "// mostly taken from kitschpatrol Arduino Brain library\n"
                + "\n"
                + "static const int MAX_PACKET_LENGTH = 32;\n"
                + "static const int EEG_POWER_BANDS = 8;\n"
                + "uint8_t packetData[MAX_PACKET_LENGTH];\n"
                + "bool inPacket;\n"
                + "//uint8_t latestByte;\n"
                + "uint8_t lastByte;\n"
                + "uint8_t packetIndex;\n"
                + "uint8_t packetLength;\n"
                + "uint8_t checksum;\n"
                + "uint8_t checksumAccumulator;\n"
                + "uint8_t eegPowerLength;\n"
                + "bool hasPower;\n"
                + "bool freshPacket;\n"
                + "\n"
                + "uint32_t eegPower[EEG_POWER_BANDS];\n"
                + "\n"
                + "\n"
                + "void clearPacket() {\n"
                + "	for (uint8_t i = 0; i < MAX_PACKET_LENGTH; i++) {\n"
                + "		packetData[i] = 0;\n"
                + "	}\n"
                + "}\n"
                + "\n"
                + "void clearEegPower() {\n"
                + "// Zero the power bands.\n"
                + "	for(uint8_t i = 0; i < EEG_POWER_BANDS; i++) {\n"
                + "		eegPower[i] = 0;\n"
                + "	}\n"
                + "}\n"
                + "\n"
                + "bool parsePacket() {\n"
                + "// Loop through the packet, extracting data.\n"
                + "// Based on mindset_communications_protocol.pdf from the Neurosky Mindset SDK.\n"
                + "// Returns true if passing succeeds\n"
                + "	hasPower = false;\n"
                + "	bool parseSuccess = true;\n"
                + "	int rawValue = 0;\n"
                + "	clearEegPower(); // clear the eeg power to make sure we're honest about missing values\n"
                + "	for (uint8_t i = 0; i < packetLength; i++) {\n"
                + "		switch (packetData[i]) {\n"
                + "		case 0x2:\n"
                + "			_quality = (packetData[++i])<<19;\n"
                + "			break;\n"
                + "		case 0x4:\n"
                + "			_attention = (packetData[++i])<<19;\n"
                + "			break;\n"
                + "		case 0x5:\n"
                + "			_meditation = (packetData[++i])<<19;\n"
                + "			break;\n"
                + "		case 0x83:\n"
                + "			// ASIC_EEG_POWER: eight big-endian 3-uint8_t unsigned integer values representing delta, theta, low-alpha high-alpha, low-beta, high-beta, low-gamma, and mid-gamma EEG band power values\n"
                + "			// The next uint8_t sets the length, usually 24 (Eight 24-bit numbers... big endian?)\n"
                + "			// We dont' use this value so let's skip it and just increment i\n"
                + "			i++;\n"
                + "			// Extract the values\n"
                + "			for (int j = 0; j < EEG_POWER_BANDS; j++) {\n"
                + "			eegPower[j] = ((uint32_t)packetData[++i] << 16) | ((uint32_t)packetData[++i] << 8) | (uint32_t)packetData[++i];\n"
                + "			}\n"
                + "			hasPower = true;\n"
                + "			// This seems to happen once during start-up on the force trainer. Strange. Wise to wait a couple of packets before\n"
                + "			// you start reading.\n"
                + "			break;\n"
                + "		case 0x80:\n"
                + "			// We dont' use this value so let's skip it and just increment i\n"
                + "			// uint8_t packetLength = packetData[++i];\n"
                + "			i++;\n"
                + "			rawValue = ((int)packetData[++i] << 8) | packetData[++i];\n"
                + "			break;\n"
                + "		default:\n"
                + "			// Broken packet ?\n"
                + "			/*\n"
                + "			Serial.print(F(\"parsePacket UNMATCHED data 0x\"));\n"
                + "			Serial.print(packetData[i], HEX);\n"
                + "			Serial.print(F(\" in position \"));\n"
                + "			Serial.print(i, DEC);\n"
                + "			printPacket();\n"
                + "			*/\n"
                + "			parseSuccess = false;\n"
                + "			break;\n"
                + "		}\n"
                + "	}\n"
                + "	return parseSuccess;\n"
                + "}\n"
                + "\n"
                + "void setup(void){\n"
                + "	freshPacket = false;\n"
                + "	inPacket = false;\n"
                + "	packetIndex = 0;\n"
                + "	packetLength = 0;\n"
                + "	eegPowerLength = 0;\n"
                + "	hasPower = false;\n"
                + "	checksum = 0;\n"
                + "	checksumAccumulator = 0;\n"
                + "	_quality = 200;\n"
                + "	_attention = 0;\n"
                + "	_meditation = 0;\n"
                + "	clearEegPower();\n"
                + "}\n"
                + "\n"
                + "bool parseChar(uint8_t latestByte){\n"
                + "	 // Build a packet if we know we're and not just listening for sync bytes.\n"
                + "	if (inPacket) {\n"
                + "		// First byte after the sync bytes is the length of the upcoming packet.\n"
                + "		if (packetIndex == 0) {\n"
                + "			packetLength = latestByte;\n"
                + "			// Catch error if packet is too long\n"
                + "			if (packetLength > MAX_PACKET_LENGTH) {\n"
                + "				// Packet exceeded max length\n"
                + "				// Send an error\n"
                + "				//sprintf(latestError, \"ERROR: Packet too long %i\", packetLength);\n"
                + "				LogTextMessage(\"ERROR: Packet too long\");\n"
                + "				inPacket = false;\n"
                + "			}\n"
                + "		}\n"
                + "		else if (packetIndex <= packetLength) {\n"
                + "			// Run of the mill data bytes.\n"
                + "			// Print them here\n"
                + "			// Store the byte in an array for parsing later.\n"
                + "			packetData[packetIndex - 1] = latestByte;\n"
                + "			// Keep building the checksum.\n"
                + "			checksumAccumulator += latestByte;\n"
                + "		}\n"
                + "		else if (packetIndex > packetLength) {\n"
                + "			// We're at the end of the data payload.\n"
                + "			// Check the checksum.\n"
                + "			checksum = latestByte;\n"
                + "			checksumAccumulator = 255 - checksumAccumulator;\n"
                + "			// Do they match?\n"
                + "			if (checksum == checksumAccumulator) {\n"
                + "				bool parseSuccess = parsePacket();\n"
                + "				if (parseSuccess) {\n"
                + "					freshPacket = true;\n"
                + "				}\n"
                + "				else {\n"
                + "					// Parsing failed, send an error.\n"
                + "//					sprintf(latestError, \"ERROR: Could not parse\");\n"
                + "				LogTextMessage(\"ERROR: Could not parse\");\n"
                + "					\n"
                + "					// good place to print the packet if debugging\n"
                + "				}\n"
                + "			}\n"
                + "			else {\n"
                + "				// Checksum mismatch, send an error.\n"
                + "//				sprintf(latestError, \"ERROR: Checksum\");\n"
                + "				LogTextMessage(\"ERROR: Checksum\");\n"
                + "				// good place to print the packet if debugging\n"
                + "			}\n"
                + "			// End of packet\n"
                + "			// Reset, prep for next packet\n"
                + "			inPacket = false;\n"
                + "		}\n"
                + "		packetIndex++;\n"
                + "	}\n"
                + "	// Look for the start of the packet\n"
                + "	if ((latestByte == 170) && (lastByte == 170) && !inPacket) {\n"
                + "		// Start of packet\n"
                + "		inPacket = true;\n"
                + "		packetIndex = 0;\n"
                + "		checksumAccumulator = 0;\n"
                + "	}\n"
                + "	// Keep track of the last byte so we can find the sync byte pairs.\n"
                + "	lastByte = latestByte;\n"
                + "}\n"
                + "\n"
                + "void loop(void) {\n"
                + "    char ch = sdGet(&SD2);\n"
                + "    parseChar(ch);\n"
                + "	const int sh = 6;\n"
                + "	_delta = eegPower[0]<<sh;\n"
                + "	_theta = eegPower[1]<<sh;\n"
                + "	_low_alpha = eegPower[2]<<sh;\n"
                + "	_high_alpha = eegPower[3]<<sh;\n"
                + "	_low_beta = eegPower[4]<<sh;\n"
                + "	_high_beta = eegPower[5]<<sh;\n"
                + "	_low_gamma = eegPower[6]<<sh;\n"
                + "	_high_gamma = eegPower[7]<<sh;\n"
                + "}"
                + "\n"
                + "msg_t ThreadX2(){\n"
                + "  setup();\n"
                + "  while(!chThdShouldTerminate()){\n"
                + "     loop();\n"
                + "     chThdYield();\n"
                + "  }\n"
                + "  chThdExit((msg_t)0);\n"
                + "}\n"
                + "static msg_t ThreadX(void *arg) {\n"
                + "((attr_parent *)arg)->ThreadX2();\n"
                + "}\n";
        o.sLocalData += "WORKING_AREA(waThreadX, 1024);\n"
                + "Thread *Thd;\n";
        o.sInitCode = "_quality = 0;\n"
                + "_attention = 0;\n"
                + "_meditation = 0;\n"
                + "_low_alpha = 0;\n"
                + "_high_alpha = 0;\n"
                + "_low_beta = 0;\n"
                + "_high_beta = 0;\n"
                + "_low_gamma = 0;\n"
                + "_high_gamma = 0;\n"
                + "_delta = 0;\n"
                + "_theta = 0;\n"
                + "  Thd = chThdCreateStatic(waThreadX, sizeof(waThreadX),\n"
                + "                    NORMALPRIO, ThreadX, (void *)this);\n";
        o.sDisposeCode = "chThdTerminate(Thd);\n"
                + "chThdWait(Thd);\n";
        o.sKRateCode
                = "%quality% = this->_quality;\n"
                + "%attention% = this->_attention;\n"
                + "%meditation% = this->_meditation;\n"
                + "%low_alpha% = this->_low_alpha;\n"
                + "%high_alpha% = this->_high_alpha;\n"
                + "%low_beta% = this->_low_beta;\n"
                + "%high_beta% = this->_high_beta;\n"
                + "%low_gamma% = this->_low_gamma;\n"
                + "%high_gamma% = this->_high_gamma;\n"
                + "%delta% = this->_delta;\n"
                + "%theta% = this->_theta;\n";
        return o;
    }
}
