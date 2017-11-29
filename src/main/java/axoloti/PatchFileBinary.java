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
        int nsegments = 3;
        File f[] = new File[nsegments];
        int offsets[] = new int[nsegments];
        f[0] = new File(buildDir + "/xpatch.sram1.bin");
        f[1] = new File(buildDir + "/xpatch.sram3.bin");
        f[2] = new File(buildDir + "/xpatch.sdram.bin");
        int addr[] = {
            connection.getTargetProfile().getPatchAddr(),
            connection.getTargetProfile().getSRAM3Addr(),
            connection.getTargetProfile().getSDRAMAddr()
        };
        int offset = 0;
        int size_segmentheader = 5 * 4;
        int size_header = (5 * 4) + nsegments * size_segmentheader;
        offset += size_header;
        for (int i = 0; i < nsegments; i++) {
            offsets[i] = offset;
            offset += f[i].length();
        }
        int bbsize = offset;
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
        bb.putInt(size_header - 8);
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
        // segments...
        bb.putInt(nsegments);
        for (int i = 0; i < nsegments; i++) {
            bb.putInt(0x12345678); // fourCC
            bb.putInt(4 * 3); // chunk size
            bb.putInt(offsets[i]); // source offset
            bb.putInt(addr[i]); // target address
            bb.putInt((int) f[i].length()); // segment size
        }
        for (int i = 0; i < nsegments; i++) {
            bb.put(Files.readAllBytes(f[i].toPath()));
        }
        return bb.array();
    }
}
