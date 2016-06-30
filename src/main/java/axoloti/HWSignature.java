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
package axoloti;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author jtaelman
 */
public class HWSignature {

    public static final String PRIVATE_KEY_FILE = "private_key.der";
    public static final String PUBLIC_KEY_FILE = "/resources/public_key.der";
    public static final int length = 256;

    static PrivateKey ReadPrivateKey(String privateKeyPath) throws Exception {
        File f = new File(privateKeyPath);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
    }

    static PublicKey ReadPublicKey(String publicKeyResourceName) throws Exception {
        InputStream fis = ClassLoader.class.getResourceAsStream(publicKeyResourceName);
        byte[] keyBytes = convertSteamToByteArray(fis,1024);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
    }

    private static byte[] convertSteamToByteArray(InputStream stream, long size) throws IOException {
        byte[] buffer = new byte[(int) size];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int line = 0;
        // read bytes from stream, and store them in buffer
        while ((line = stream.read(buffer)) != -1) {
            // Writes bytes from byte array (buffer) into output stream.
            os.write(buffer, 0, line);
        }
        stream.close();
        os.flush();
        os.close();
        return os.toByteArray();
    }

    static public void printByteArray(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            if ((i % 32) == 0) {
                System.out.println();
            }
            System.out.print(String.format("%02X ", (int) b[i] & 0xFF));
        }
        System.out.println();
    }

    public static byte[] Sign(ByteBuffer cpuserial, ByteBuffer otpinfo) throws Exception {
        if (cpuserial.limit() != 12) {
            throw new Exception("cpuserial has wrong length");
        }
        if (otpinfo.limit() != 32) {
            throw new Exception("otpinfo has wrong length");
        }
        byte[] sdata = new byte[12 + 32];
        for (int i = 0; i < 12; i++) {
            sdata[i] = cpuserial.get(i);
        }
        for (int i = 0; i < 32; i++) {
            sdata[i + 12] = otpinfo.get(i);
        }
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(ReadPrivateKey(PRIVATE_KEY_FILE));
        sig.update(sdata);
        byte[] signature = sig.sign();
        return signature;
    }

    public static boolean Verify(ByteBuffer cpuserial, ByteBuffer otpinfo, byte[] signature) throws Exception {
        if (cpuserial.limit() != 12) {
            throw new Exception("cpuserial has wrong length");
        }
        if (otpinfo.limit() != 32) {
            throw new Exception("otpinfo has wrong length");
        }
        byte[] sdata = new byte[12 + 32];
        for (int i = 0; i < 12; i++) {
            sdata[i] = cpuserial.get(i);
        }
        for (int i = 0; i < 32; i++) {
            sdata[i + 12] = otpinfo.get(i);
        }
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(ReadPublicKey(PUBLIC_KEY_FILE));
        sig.update(sdata);
        return sig.verify(signature);
    }

}
