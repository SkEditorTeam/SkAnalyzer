package me.glicz.skanalyzer.bootstrap.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class Utils {
    private static final MessageDigest SHA256_DIGEST;

    static {
        try {
            SHA256_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyFile(Path path, byte[] hash) throws IOException {
        if (!Files.exists(path)) {
            return false;
        }

        return Arrays.equals(hash, SHA256_DIGEST.digest(Files.readAllBytes(path)));
    }

    public static byte[] fromHex(String s) {
        if (s.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }

        byte[] bytes = new byte[s.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            int offset = i * 2;

            int high = hexDigit(s.charAt(offset));
            int low = hexDigit(s.charAt(offset + 1));

            bytes[i] = (byte) ((high << 4) | low);
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

    public static <X extends Throwable> RuntimeException sneakyThrow(Throwable ex) throws X {
        //noinspection unchecked
        throw (X) ex;
    }
}
