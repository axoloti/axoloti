package axoloti.connection;

import axoloti.job.GlobalJobProcessor;
import axoloti.job.IJobContext;
import axoloti.target.fs.SDFileInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import javax.imageio.IIOException;

/**
 *
 * @author jtaelman
 */
public class ConnectionTest {

    private static byte[] genRandomBytes(int seed, int length) {
        byte out[] = new byte[length];
        for (int i = 0; i < length; i++) {
            seed = (seed * 1103515245 + 12345);
            out[i] = (byte) seed;
        }
        return out;
    }

    /**
     * @param a
     * @param b
     * @return 0 if data in a and b is identical
     */
    private static int compareByteArrays(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return 2;
        }
        for (int i = 0; i < a.length; i++) {
            byte ba = a[i];
            byte bb = a[i];
//          System.out.println(String.format("0x%08X: %02X", addr + i, bRead));
            if (ba != bb) {
                return 1;
            }
        }
        return 0;
    }

    private static byte[] ByteBufferToArray(ByteBuffer bb) {
        byte res[] = new byte[bb.limit()];
        bb.get(res);
        return res;
    }

    private static void doMemWriteReadVerify(IConnection c, int addr, int size, int seed) throws IOException {
        byte[] data = genRandomBytes(seed, size);
        c.write(addr, data);
        ByteBuffer bb = c.read(addr, data.length);
        int result = compareByteArrays(data, ByteBufferToArray(bb));
        if (result != 0) {
            throw new IOException(String.format("memory test failed at addr 0x%08X", addr));
        }
        System.out.println("doWriteReadVerify complete");
    }

    public static void doMemoryTest(IConnection c, IJobContext ctx) throws IOException {
        int sizes[] = {4, 8, 16, 32, 64, 128, 252, 256};
        int addrs[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        int iters = sizes.length * 2;
        ctx.setNote("Memory read/write test");
        ctx.setMaximum(iters);
        for (int i = 0; i < iters; i++) {
            ctx.setProgress(i);
            int addr = addrs[i % sizes.length];
            int size = sizes[i % sizes.length];

            doMemWriteReadVerify(c, addr, size, addr + size);
            doMemWriteReadVerify(c, addr, size, addr + size ^ 0xFFFFFFFF);

            final int sdram_base = 0xC0000000;
            doMemWriteReadVerify(c, addr | sdram_base, size, addr + size);
            doMemWriteReadVerify(c, addr | sdram_base, size, addr + size ^ 0xFFFFFFFF);
        }
    }

    public static void doFileReadWriteTest(IConnection c, IJobContext ctx) throws IOException {
        int sizes[] = {4, 16, 64, 128, 512, 2048, 8192, 32768, 65536,
            1234, 2345, 3456, 4567, 12345, 23456, 123456, 1234567};
        int iters = sizes.length;
        ctx.setNote("File read/write test");
        ctx.setProgress(0);
        IJobContext ctxs[] = ctx.createSubContexts(iters);
        for (int i = 0; i < iters; i++) {
            IJobContext ctx1 = ctxs[i];
            ctx1.setNote("File read/write test " + i);
            ctx1.setProgress(0);
            int size = sizes[i];
            byte[] testData = genRandomBytes(i, size);
            String filename = String.format("/test%03d.dat", i);

            Calendar cal = Calendar.getInstance();
            cal.set(2005, 1, i, 12, 34, 56);
            c.upload(filename, new ByteArrayInputStream(testData), cal, size, ctx1);
            ByteBuffer readData = c.download(filename, ctx1);
            int result = compareByteArrays(testData, ByteBufferToArray(readData));
            if (result != 0) {
                throw new IOException(String.format("filesystem test readback failed..."));
            }
            SDFileInfo fi = c.getFileInfo(filename);
            if (fi.getSize() != size) {
                throw new IOException("filesystem getFileInfo size wrong: "
                        + fi.getSize() + " vs " + size);
            }
            long t1 = fi.getTimestamp().getTimeInMillis();
            long t2 = cal.getTimeInMillis();
            if (Math.abs(t1 - t2) > 2000) {
                throw new IOException("filesystem getFileInfo date wrong: "
                        + fi.getSize() + " vs " + size);
            }
            c.deleteFile(filename);
            SDFileInfo fi2 = c.getFileInfo(filename);
            if (fi2 != null) {
                throw new IIOException("file " + filename + " should have been deleted, but still exists?");
            }
            System.out.println("doFilesystemTest ok for size " + size);
        }
    }

    public static void doFileDirTest(IConnection c, IJobContext ctx) throws IOException {
        int iters = 30;
        ctx.setNote("Dir create/delete test");
        ctx.setMaximum(iters);
        for (int i = 0; i < iters; i++) {
            ctx.setProgress(i);
            SDFileInfo info1 = c.getFileInfo("/notexist");
            if (info1 != null) {
                throw new IOException("file /notexist is not expected to exist");
            }
            final String sTestDir = "/testdir";
            c.createDirectory(sTestDir, Calendar.getInstance());
            SDFileInfo infoDir = c.getFileInfo(sTestDir);
            if (infoDir == null) {
                throw new IOException("directory /testdir is expected to exist");
            }

            c.deleteFile("testdir");

            // TODO: implement: this should throw an exeption if dir does not exist
            // rather than logging 
//          c.deleteFile("testdir3");
        }
    }

    public static void doAllTests(IConnection c) throws IOException {

        GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
            try {
                ctx.setNote("doAllTests");
                IJobContext[] ctxs = ctx.createSubContexts(new int[]{1, 1, 1});
                doMemoryTest(c, ctxs[0]);
                doFileReadWriteTest(c, ctxs[1]);
                doFileDirTest(c, ctxs[2]);
                ctx.setReady();

            } catch (IOException ex) {
                ctx.reportException(ex);
            }
        });
    }

}
