package com.doghotel.reservation.global.aws.util;

import java.time.LocalDate;

public class CommonUtils {
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    public static String buildFileName(String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(LocalDate.now());

        return fileName + "_" + now + fileExtension;
    }

}
