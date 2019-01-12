package dai.android.ashelper.compiler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Utility {

    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(data.getBytes());
            StringBuffer buffer = new StringBuffer();
            byte[] bits = md.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0) a += 256;
                if (a < 16) buffer.append("0");
                buffer.append(Integer.toHexString(a));
            }
            return buffer.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
