/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
package axoloti.patch.object.attribute;

import axoloti.object.attribute.AxoAttributeWavefile;
import axoloti.patch.object.AxoObjectInstance;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
@Deprecated
public class AttributeInstanceWavefile extends AttributeInstance<AxoAttributeWavefile> {

    @Attribute
    private String waveFilename;

    AttributeInstanceWavefile(AxoAttributeWavefile attribute, AxoObjectInstance axoObj1) {
        super(attribute, axoObj1);
    }

    @Override
    public String CValue() {
        String s = " {";
        File fp = new File(getParent().getParent().getFileNamePath());
        File f = new File(fp.getParent() + "/" + waveFilename);
        System.out.println("waveFilename : " + fp.getParent() + "/" + waveFilename + "\n");

        try {
            AudioFileFormat af = AudioSystem.getAudioFileFormat(f);
            AudioInputStream as = AudioSystem.getAudioInputStream(f);
            byte[] bytes = new byte[af.getByteLength()];
            int read = as.read(bytes, 0, af.getByteLength());
            ByteBuffer BB = ByteBuffer.wrap(bytes);
            BB.order(ByteOrder.LITTLE_ENDIAN);
            ShortBuffer SB = BB.asShortBuffer();
            for (int i = 0; i < af.getFrameLength(); i++) {
                s = s + SB.get(i * af.getFormat().getChannels()) + ", ";
                if ((i & 0x7) == 0) {
                    s += "\n        ";
                }
            }
            s += " 0}";
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(AttributeInstanceWavefile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AttributeInstanceWavefile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return s;
    }

    public String getWaveFilename() {
        return waveFilename;
    }

    public void setWaveFilename(String waveFilename) {
        this.waveFilename = waveFilename;
    }

    @Override
    public void copyValueFrom(AttributeInstance a) {
        if (a instanceof AttributeInstanceWavefile) {
            AttributeInstanceWavefile a1 = (AttributeInstanceWavefile) a;
            setWaveFilename(a1.getWaveFilename());
        }
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
