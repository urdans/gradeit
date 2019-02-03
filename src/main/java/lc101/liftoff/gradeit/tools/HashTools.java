package lc101.liftoff.gradeit.tools;

import java.security.SecureRandom;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

public class HashTools {
    /*info
     * The output is 16+40=56 chars long.
     * If a better hash needs to be used, like SHA-256 or SHA-512, the password fields in tables: student, teacher and
     * registrar, must be redefined to allow for longer strings' storage
     */
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


    public static String hashAndSaltPassword(String password) {
//        String saltStr = randomAlphaNumeric(16);
//
//        String saltedHash = sha1Hex(saltStr + password);
//        return saltStr + "." + saltedHash;
        return hashAndSaltPassword(randomAlphaNumeric(16), password);
    }

    public static String hashAndSaltPassword(String salt, String password) {
        String saltedHash = sha1Hex(salt + password);
        return salt + "." + saltedHash;
    }

    public static String extractSalt(String saltedPassword) {
        int p = saltedPassword.indexOf(".");
        if (p <= 0) return "";
        String salt = saltedPassword.substring(0, p);
        return salt;
    }

    public static String hash(String aStr) {
        return sha1Hex(aStr);
    }
}
