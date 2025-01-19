package me.glicz.skanalyzer.bootstrap.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Utils {
    private static final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyFile(Path path, byte[] hash) {
        if (!Files.exists(path)) {
            return false;
        }

        try {
            return Arrays.equals(hash, digest.digest(Files.readAllBytes(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Based on PaperMC/Paperclip Util#fromHex
    public static byte[] fromHex(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }

        final byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            final char left = hex.charAt(i * 2);
            final char right = hex.charAt(i * 2 + 1);

            bytes[i] = (byte) ((hexDigit(left) << 4) | (hexDigit(right) & 0xF));
        }

        return bytes;
    }

    private static int hexDigit(char c) {
        int digit = Character.digit(c, 16);

        if (digit < 0) {
            throw new IllegalArgumentException();
        }

        return digit;
    }
}
