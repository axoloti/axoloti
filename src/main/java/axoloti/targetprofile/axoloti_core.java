/**
 * Copyright (C) 2015 Johannes Taelman
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
package axoloti.targetprofile;

import java.nio.ByteBuffer;

/**
 *
 * @author Johannes Taelman
 */
public class axoloti_core {

    public int getPatchAddr() {
        // SRAM1 - must match with ramlink.ld
        return 0x20011000;
    }

    public int getSDRAMAddr() {
        return 0xC0000000;
    }

    public int getSDRAMSize() {
        return 8 * 1024 * 1024;
    }

    public int getOTP0Addr() {
        return 0x1FFF7800;
    }

    public int getOTP0Length() {
        return 32;
    }

    public int getOTP1Addr() {
        return 0x1FFF7820;
    }

    public int getOTP1Length() {
        return 32;
    }

    public int getCPUIDAddr() {
        return 0x1FFF7A10;
    }

    public int getCPUIDLength() {
        return 12;
    }

    ByteBuffer OTP0Data;
    ByteBuffer OTP1Data;
    ByteBuffer CPUIDData;

    public void setOTP0Data(ByteBuffer b) {
        OTP0Data = b;
    }

    public void setOTP1Data(ByteBuffer b) {
        OTP1Data = b;
    }

    public void setCPUIDData(ByteBuffer b) {
        CPUIDData = b;
    }

}
