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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class axoloti_core {

    enum cputype_e {

        STM32F40xxx,
        STM32F42xxx
    };

    cputype_e cputype;

    public ByteBuffer CreateOTPInfo() {
        return CreateOTPInfo(1, 1, 0, 8);
    }

    public ByteBuffer CreateOTPInfo(
            int boardtype,
            int boardmajorversion,
            int boardminorversion,
            int sdramsize
    ) {
        try {
            ByteBuffer bb = ByteBuffer.allocate(32);
            String header = "Axoloti Core";
            bb.rewind();
            bb.put(header.getBytes("UTF8"));
            while (bb.position() < 16) {
                bb.put((byte) 0);
            }
            bb.putInt(boardtype);
            bb.putInt(boardmajorversion);
            bb.putInt(boardminorversion);
            bb.putInt(sdramsize);

            return bb;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(axoloti_core.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int getPatchAddr() {
        // SRAM1 - must match with patch.ld
        return 0x20000000;
    }

    public int getSRAM3Addr() {
        return 0x20020000;
    }

    public int getSDRAMAddr() {
        return 0xC0000000;
    }

    public int getSDRAMSize() {
        return 8 * 1024 * 1024;
    }

    public int getOTPAddr() {
        return 0x1FFF7800;
    }

    public int getOTPLength() {
        return 32;
    }

    public int getCPUSerialAddr() {
        return 0x1FFF7A10;
    }

    public int getCPUIDCodeAddr() {
        return 0xE0042000;
    }

    public void setCPUIDCode(int i) {
        //System.out.println(String.format("idcode = %8X", i));
        if (i == 0 || (i & 0x0FFF) == 0x0419) {
            cputype = cputype_e.STM32F42xxx;
        } else {
            cputype = cputype_e.STM32F40xxx;
        }
    }

    cputype_e getCPUType() {
        return cputype;
    }

    public boolean hasSDRAM() {
        if (cputype == cputype_e.STM32F42xxx) {
            return true;
        } else {
            return false;
        }
    }

    public int getCPUSerialLength() {
        return 12;
    }

    public int getBKPSRAMAddr() {
        return 0x40024000;
    }

    public int getBKPSRAMLength() {
        return 0x1000;
    }

    ByteBuffer OTP0Data;
    ByteBuffer OTP1Data;
    ByteBuffer CPUIDData;
    ByteBuffer BKPSRAMData;

    public void setOTP0Data(ByteBuffer b) {
        OTP0Data = b;
    }

    public void setOTP1Data(ByteBuffer b) {
        OTP1Data = b;
    }

    public void setCPUSerial(ByteBuffer b) {
        if (b != null) {
            CPUIDData = b;
            String s = "";
            b.rewind();
            while (b.remaining() > 0) {
                s = s + String.format("%08X", b.getInt());
            }
        } else {
            Logger.getLogger(axoloti_core.class.getName()).log(Level.SEVERE, "invalid CPU serial number, invalid protocol?, update firmware", new Object());
        }
    }

    public ByteBuffer getCPUSerial() {
        return CPUIDData;
    }

    public String getCPUSerialString() {
        String s = "";
        if (CPUIDData != null) {
            CPUIDData.rewind();
            while (CPUIDData.remaining() > 0) {
                s = s + String.format("%08X", CPUIDData.getInt());
            }
            return s;
        } else
            return "not connected";
    }

    public ByteBuffer getBKPSRAMData() {
        return BKPSRAMData;
    }

    public void setBKPSRAMData(ByteBuffer BKPSRAMData) {
        this.BKPSRAMData = BKPSRAMData;
    }

}
