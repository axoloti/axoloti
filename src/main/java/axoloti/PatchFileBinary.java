package axoloti;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

/**
 *
 * @author jtaelman
 */
public class PatchFileBinary {

    public static byte[] getPatchFileBinary() throws IOException {
        // TODO: fourcc signatures
        // also firmware ignores them...
        
        String buildDir = System.getProperty(Axoloti.HOME_DIR) + "/build";
        IConnection connection = CConnection.GetConnection();
        int nfiles = 3;
        File f[] = new File[nfiles];
        f[0] = new File(buildDir + "/xpatch.sram1.bin");
        f[1] = new File(buildDir + "/xpatch.sram3.bin");
        f[2] = new File(buildDir + "/xpatch.sdram.bin");
        int addr[] = {
            connection.getTargetProfile().getPatchAddr(),
            connection.getTargetProfile().getSRAM3Addr(),
            connection.getTargetProfile().getSDRAMAddr()
        };
        int size_header = 20;
        int size_segmentheader = 12;
        int bbsize = size_header;
        for (int i = 0; i < nfiles; i++) {
            bbsize += size_segmentheader + f[i].length();
        }
        byte ba[] = new byte[bbsize];
        ByteBuffer bb = ByteBuffer.wrap(ba);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.clear();
        // header
        // signature
        bb.put((byte) 'A');
        bb.put((byte) 'x');
        bb.put((byte) 'o');
        bb.put((byte) 'h');
        // length
        bb.putInt(12);
        // arch
        bb.put((byte) 'f');
        bb.put((byte) '4');
        bb.put((byte) '2');
        bb.put((byte) '7');
        // version
        bb.put((byte) 0);
        bb.put((byte) 0);
        bb.put((byte) 0);
        bb.put((byte) 0);
        // nsegments
        bb.putInt(nfiles);
        // segments...
        for (int i = 0; i < nfiles; i++) {
            bb.putInt(0x12345678); 
            bb.putInt((int) f[i].length() + 4);
            bb.putInt(addr[i]);
            bb.put(Files.readAllBytes(f[i].toPath()));
        }
        return bb.array();
    }
}
