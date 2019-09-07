package com.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ProtostuffUtil {


    public static <U> byte[] serializeObject(U obj, Class<U> clazz) {
        if (obj == null) {
            return null;
        }
        Schema schema = RuntimeSchema.getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 4);
        byte[] protostuff = null;
        protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        return protostuff;
    }


    public static <U> U deserializeObject(
            byte[] bytes, Class<U> clazz) {
        if (bytes == null) {
            return null;
        }
        Schema<U> schema = RuntimeSchema.getSchema(clazz);


        Field unsafeField = null;
        try {
            unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        unsafeField.setAccessible(true);

        Unsafe u = null;
        try {
            u = (Unsafe) unsafeField.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        U obj = null;
        try {
            obj = (U) u.allocateInstance(clazz);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


//        U obj = null;
////        try {
////            obj = clazz.newInstance();
////        } catch (InstantiationException | IllegalAccessException e) {
////            e.printStackTrace();
////        }
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    public static List<byte[]> serializeObjectList(List list, Class clazz) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        List<byte[]> bytes = new ArrayList<byte[]>();
        Schema schema = RuntimeSchema.getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 40);
        byte[] protostuff = null;
        for (Object p : list) {
            try {
                protostuff = ProtostuffIOUtil.toByteArray(p, schema, buffer);
                bytes.add(protostuff);
            } finally {
                buffer.clear();
            }
        }
        return bytes;
    }

    public static <U> List<U> deserializeObjectList(
            List<byte[]> bytesList, Class<U> clazz) {
        if (bytesList == null || bytesList.size() <= 0) {
            return null;
        }
        Schema<U> schema = RuntimeSchema.getSchema(clazz);
        List<U> list = new ArrayList<>();
        for (byte[] bs : bytesList) {
            U obj = null;
            try {
                obj = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            ProtostuffIOUtil.mergeFrom(bs, obj, schema);
            list.add(obj);
        }
        return list;
    }
}
 
