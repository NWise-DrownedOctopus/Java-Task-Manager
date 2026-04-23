package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple password hashing helper using SHA-256.
 */
public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    /**
     * Hashes a plain-text password using SHA-256 and returns a hex-encoded string.
     */
    public String hash(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] bytes = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Returns true if the plain-text password, when hashed, equals the given hash.
     */
    public boolean matches(String plainPassword, String expectedHash) {
        if (plainPassword == null || expectedHash == null) {
            return false;
        }
        String actualHash = hash(plainPassword);
        return constantTimeEquals(actualHash, expectedHash);
    }

    /**
     * Converts bytes to a lowercase hex string.
     */
    private String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            hexChars[i * 2] = HEX_DIGITS[value >>> 4];
            hexChars[i * 2 + 1] = HEX_DIGITS[value & 0x0f];
        }
        return new String(hexChars);
    }

    /**
     * Compares two strings in a way that is harder to exploit for timing attacks.
     */
    private boolean constantTimeEquals(String a, String b) {
        int length = Math.max(a.length(), b.length());
        int result = a.length() ^ b.length();
        for (int i = 0; i < length; i++) {
            char ca = i < a.length() ? a.charAt(i) : 0;
            char cb = i < b.length() ? b.charAt(i) : 0;
            result |= (ca ^ cb);
        }
        return result == 0;
    }
}
