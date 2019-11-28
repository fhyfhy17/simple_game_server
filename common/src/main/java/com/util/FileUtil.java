package com.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class FileUtil {

    public static List<String> getFileNames(String filePath, String filter) {
        Predicate<String> fileFilter = fileName -> fileName.endsWith(filter) && !fileName.startsWith("~");
        List<String> fileList = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(filePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (fileFilter.test(file.getFileName().toString())) {
                        fileList.add(file.toFile().getAbsolutePath());
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            log.error("", e);
        }

        return fileList;
    }

    public static List<File> getFiles(String filePath, String filter) {
        Predicate<String> fileFilter = fileName -> fileName.endsWith(filter) && !fileName.startsWith("~");
        List<File> fileList = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(filePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (fileFilter.test(file.getFileName().toString())) {
                        fileList.add(file.toFile());
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            log.error("", e);
        }

        return fileList;
    }
}
