package com.petlink.adopcion_mascotas.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FileValidator {

    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPEG_SIGNATURE = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] WEBP_SIGNATURE = new byte[]{0x52, 0x49, 0x46, 0x46};
    private static final byte[] WEBP_SIGNATURE_PART2 = new byte[]{0x57, 0x45, 0x42, 0x50};

   
    public static boolean isValidImage(InputStream inputStream) throws IOException {
        byte[] header = new byte[12];
        int bytesRead = inputStream.read(header);

        if (bytesRead < 4) {
            return false;
        }

        if (bytesRead >= 8 && startsWith(header, PNG_SIGNATURE)) {
            return true;
        }

        if (bytesRead >= 3 && startsWith(header, JPEG_SIGNATURE)) {
            return true;
        }

        if (bytesRead >= 12 && startsWith(header, WEBP_SIGNATURE) &&
            Arrays.equals(Arrays.copyOfRange(header, 8, 12), WEBP_SIGNATURE_PART2)) {
            return true;
        }

        return false;
    }

    private static boolean startsWith(byte[] source, byte[] match) {
        if (source.length < match.length) {
            return false;
        }
        for (int i = 0; i < match.length; i++) {
            if (source[i] != match[i]) {
                return false;
            }
        }
        return true;
    }

    
    public static boolean hasValidImageExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return extension.equals(".jpg") || extension.equals(".jpeg") ||
               extension.equals(".png") || extension.equals(".webp");
    }
}
