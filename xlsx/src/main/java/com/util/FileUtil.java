package com.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class FileUtil {

    public static List<String> getFiles(String filePath, String filter) {

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
            log.info("",e);
        }

        return fileList;
    }

    public static String getSgsMainPath() {
        String binPath = getBinPath();
        String main = binPath.substring(0, binPath.lastIndexOf(File.separator));
        return main;
    }

    public static String getJavaTemplatesPath() {
        return getSgsMainPath()
                + File.separator
                + "common"
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "java"
                + File.separator
                + "com"
                + File.separator
                + "template"
                + File.separator
                + "templates";

    }

    public static String getBinPath() {
        return System.getProperty("user.dir");
    }

    public static String getTemplatesPah() {
        return getBinPath()
                + File.separator
                + "templates";
    }

    public static String getTemplatesTypePah() {
        return getTemplatesPah()
                + File.separator
                + "type";
    }

    public static void writeStringToFile(String path, String content, Charset charset) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        } else {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
        }

        try (
                Writer writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(file), charset))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void deleteAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("要删除的文件夹不存在");
        }
        if (!file.isDirectory()) {
            throw new RuntimeException("要删除的不是文件夹");
        }
        deleteFile(file);
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {//判断是否为文件，是，则删除
            System.out.println(file.getAbsoluteFile());//打印路径
            file.delete();
        } else {//不为文件，则为文件夹
            String[] childFilePath = file.list();//获取文件夹下所有文件相对路径
            for (String path : childFilePath) {
                File childFile = new File(file.getAbsoluteFile() + "/" + path);
                deleteFile(childFile);//递归，对每个都进行判断
            }
            file.delete();
        }
    }

    public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }
}
