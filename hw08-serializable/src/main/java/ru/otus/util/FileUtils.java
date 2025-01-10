package ru.otus.util;

import ru.otus.dataprocessor.FileProcessException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileUtils {

    private static final String JSON_EXTENSION = ".json";

    private static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;


    public static String checkJsonFileName(String fileName) {
        if (fileName.isBlank()) {
            throw new IllegalArgumentException("FileName is blank");
        }
        if (!fileName.endsWith(JSON_EXTENSION)) {
            throw new IllegalArgumentException("FileName is not json");
        }
        return fileName;
    }

    public static void writeStringToFile(String fileName, String content) {
        checkFileName(fileName);
        checkContent(content);
        try (PrintStream out = new PrintStream(new FileOutputStream(fileName), false, CHARSET_UTF_8)) {
            out.print(content);
        } catch (Exception e) {
            throw new FileProcessException("Can't write to file: %sReason: %s".formatted(fileName, e.getMessage()));
        }
    }

    public static String readResourceFileToString(String fileName) {
        checkFileName(fileName);

        StringBuilder fileContent = new StringBuilder();
        try (InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET_UTF_8))) {
            for (String line; (line = reader.readLine()) != null; ) {
                fileContent.append(line).append(System.lineSeparator());
            }
        } catch (Exception e) {
            throw new FileProcessException("Can't write to file: %s. Reason: %s".formatted(fileName, e.getMessage()));
        }
        return fileContent.toString();
    }

    private static void checkFileName(String fileName) {
        if (Objects.isNull(fileName)) {
            throw new FileProcessException("File name is NULL");
        }
    }

    private static void checkContent(String content) {
        if (Objects.isNull(content)) {
            throw new FileProcessException("Content is NULL");
        }
    }

}
