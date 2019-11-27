package com.hot;

import com.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class Hot {

    private static Instrumentation instrumentation;
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        Hot.instrumentation = instrumentation;
    }

    public static void reload() {
        if(Objects.isNull(instrumentation)){
            log.error("热更新 未加载 agent");
        }
        log.info("热更新 开始");
        String path = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "hot";
        List<File> list = FileUtil.getFiles(path, ".class");
        List<ClassDefinition> defines = new LinkedList<>();
        String rootPath = new File(path).getPath();

        for (File file : list) {
            if (file.getName().indexOf('$') == -1) {
                String filePath = file.getPath();
                String className = filePath.substring(rootPath.length() + 1, filePath.lastIndexOf('.'));
                className = className.replace(File.separatorChar, '.');
                log.info("热更新 代理生成了一个类 {}", className);

                Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    log.error("热更新 找不到类: {}", className, e);
                    return;
                }
                try {
                    byte[] fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    ClassDefinition classDefinition = new ClassDefinition(clazz, fileBytes);
                    defines.add(classDefinition);
                } catch (IOException e) {
                    log.error("热更新 报错", e);
                }
            }
        }
        ClassDefinition[] arr = new ClassDefinition[defines.size()];
        defines.toArray(arr);
        try {
            instrumentation.redefineClasses(arr);
        } catch (Exception e) {
            log.error("热更新失败", e);
            return;
        }
        log.info("热更类结束");
    }
}