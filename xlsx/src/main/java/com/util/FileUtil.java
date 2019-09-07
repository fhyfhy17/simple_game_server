package com.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileUtil {

  
    public static List<String> getFiles(String filePath, String filter) {
    
        //Predicate<String> fileFilter = fileName->fileName.endsWith(filter)&&!fileName.startsWith("~");
        //List<String> filelist = new ArrayList<>();
        //try
        //{
        //    Files.walkFileTree(Paths.get(filePath),new SimpleFileVisitor<Path>(){
        //        @Override
        //        public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException
        //        {
        //            if(fileFilter.test(file.getFileName().toString())){
        //                filelist.add(file.toFile().getAbsolutePath());
        //
        //            }
        //            return super.visitFile(file,attrs);
        //        }
        //    });
        //}
        //catch(IOException e)
        //{
        //    e.printStackTrace();
        //}
    
    
        File root = new File(filePath);

        FileFilter fileFilter =pathName -> pathName.getName().endsWith(filter)&&!pathName.getName().startsWith("~");


        File[] files = root.listFiles(fileFilter);
        if(Objects.isNull(files)){
            return null;
        }
        List<String> filelist = new ArrayList<>();
        Arrays.stream(files).forEach(file->{
            if (file.isDirectory()) {
                getFiles(file.getAbsolutePath(), filter);
                System.out.println("显示" + filePath + "下所有子目录及其文件" + file.getAbsolutePath());
            } else {
                filelist.add(file.getAbsolutePath());
                System.out.println("显示" + filePath + "下所有子目录" + file.getAbsolutePath());
            }
        });

        return filelist;
    }

    public static String getShootMainPath() {
        String binPath = getBinPath();
        String main = binPath.substring(0, binPath.lastIndexOf(File.separator));
        return main;
    }

    public static String getJavaTemplatesPath() {
        return getShootMainPath()
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
}
