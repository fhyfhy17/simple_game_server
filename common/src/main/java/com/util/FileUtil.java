package com.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> getFiles(String filePath, String filter) {
        File root = new File(filePath);
        File[] files = root.listFiles(pathname -> {
            String name = pathname.getName();
            return name.endsWith(filter);
        });
        List<String> filelist = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                getFiles(file.getAbsolutePath(), filter);
                System.out.println("显示" + filePath + "下所有子目录及其文件" + file.getAbsolutePath());
            } else {
                filelist.add(file.getAbsolutePath());
                System.out.println("显示" + filePath + "下所有子目录" + file.getAbsolutePath());
            }
        }
        return filelist;
    }
}
