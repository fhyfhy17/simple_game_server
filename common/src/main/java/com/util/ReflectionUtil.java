package com.util;

import com.annotation.SeqClassName;
import com.entry.BaseEntry;
import com.entry.Serialize;
import com.google.common.collect.Lists;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;

public class ReflectionUtil {

    public static List<Class<?>> scan(Class<?> clazz, Class<? extends Annotation> annotation, String... paths) {
        List<String> list = new ArrayList<>(Arrays.asList(paths));
        return scan(clazz, annotation, list);
    }

    public static List<Class<?>> scan(Class<?> clazz, Class<? extends Annotation> annotation, List<String> paths) {
        if (paths.size() < 1) {
            return Collections.emptyList();
        }

        Reflections reflections = new Reflections(paths);
        Set<Class<?>> classesSet = reflections.getTypesAnnotatedWith(annotation);
        return new ArrayList<>(classesSet);
//                classesSet.stream().map(x ->
//        {
//
//            try {
//                return x.newInstance();
//            } catch (InstantiationException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//
//        }).collect(Collectors.toList());

    }

    public static List<String> getSeqClassNames() {
        List<String> list = Lists.newArrayList();
        Reflections reflections = new Reflections("com.entry");
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(SeqClassName.class);
        for (Class<?> aClass : set) {
            SeqClassName annotation = aClass.getAnnotation(SeqClassName.class);
            list.add(annotation.name());
        }
        return list;
    }


    public static Set<Class<? extends BaseEntry>> getEntryClasses() {
        Reflections reflections = new Reflections("com.entry");
        return reflections.getSubTypesOf(BaseEntry.class);
    }

    public static Set<Class<? extends Serialize>> getSerializeClasses() {
        Reflections reflections = new Reflections("com.entry");
        Set<Class<? extends Serialize>> subTypesOf = reflections.getSubTypesOf(Serialize.class);
        subTypesOf.removeIf(next -> next.equals(BaseEntry.class));
        return subTypesOf;
    }

    public static void main(String[] args) {
//        getSeqClassNames();
        System.out.println(PropertiesUtil.getIntValue("a.properties", "a"));
        System.out.println(PropertiesUtil.getIntValue("a.properties", "b"));
    }

}
