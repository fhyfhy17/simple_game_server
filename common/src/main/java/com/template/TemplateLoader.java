package com.template;

import com.template.templates.AbstractTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TemplateLoader {

    <T extends AbstractTemplate> List<T> loadTemplate(File file, Class<T> clazz) {

        List<T> ts = new ArrayList<>();

        try {
            Document doc = new SAXBuilder().build(file);
            Element root = doc.getRootElement();
            Iterator<Element> it = root.getChildren().iterator();
            int count = 0;
            while (it.hasNext()) {
                Element next = it.next();
                count++;
                if (count == 1) {
                    continue;
                }
                T t = clazz.newInstance();
                for (Object elem : next.getAttributes()) {
                    Attribute attr = (Attribute) elem;
                    String value = (null == attr.getValue()) ? "" : attr.getValue().trim();
                    setProperties(t, attr.getName(), value);
                }
                if (ts.stream().anyMatch(x -> x.getId() == t.getId())) {
                    log.error("文件= {} 发现重复ID= {} ", file.getName(), t.getId());
                    continue;
                }
                ts.add(t);
            }

        } catch (Exception e) {
            log.error("加载 XML 资源文件 {} 报错", file.getName(), e);
        }

        if (ts.isEmpty()) {
            log.error("警告：XML 资源文件加载为空：{}", file.getName());
        }

        return ts;
    }

    // FIXME  烦，写得一坨便便，有时间再细改吧，List这里写得头疼，还有判空问题什么的
    private static <T> void setProperties(T object, String fieldName, String fieldValue) {
        PropertyDescriptor prop;
        try {
            prop = new PropertyDescriptor(fieldName, object.getClass());
        } catch (Exception ex) {
            return;
        }

        Class<?> fieldClass = prop.getPropertyType();
        String field = fieldClass.getSimpleName();
        try {

            Method m = prop.getWriteMethod();
            boolean emptyVal = "".equals(fieldValue);
            Object val = null;
            switch (field) {

                case "List":
                    Field f = null;
                    try {
                        f = object.getClass().getDeclaredField(fieldName.trim());
                    } catch (Exception e) {
                        log.error("", e);
                    }

                    Type genericReturnType = f.getGenericType();

                    int wrapCount = 1;

                    Class<?> typeClass = null;
                    if (genericReturnType instanceof ParameterizedType) {
                        ParameterizedType g2 = (ParameterizedType) genericReturnType;
                        Type t = g2.getActualTypeArguments()[0];
                        while(!(t instanceof Class)){
                            ParameterizedType g3 = (ParameterizedType) t;
                            t = g3.getActualTypeArguments()[0];
                            wrapCount++;
                        }
                        typeClass = (Class<?>)t;
                    }
                    if (typeClass == String.class) {
                        if (wrapCount == 0) {
                            String[] vs = fieldValue.split(",");
                            List<String> list = new ArrayList<>();
                            Collections.addAll(list, vs);
                            val = list;
                        } else {
                            List<List<String>> list = new ArrayList<>();
                            String[] vs = fieldValue.split(";");
                            for (String v : vs) {
                                String[] temp = v.split(",");
                                List<String> a = new ArrayList<>();
                                Collections.addAll(a, temp);
                                list.add(a);
                            }
                            val = list;
                        }
                    } else if (typeClass == Integer.class) {
                        if (wrapCount == 0) {
                            String[] vs = fieldValue.split(",");

                            if (vs.length > 0) {
                                val = Arrays.stream(vs).filter(v -> !(null == v) && !"".equals(v.trim())).map(Integer::parseInt).collect(Collectors.toList());
                            }
                        } else {
                            List<List<Integer>> list = new ArrayList<>();
                            String[] vs = fieldValue.split(";");
                            for (String v : vs) {
                                List<Integer> c = new ArrayList<>();
                                String[] temp = v.split(",");
                                for (String a : temp) {
                                    if (a != null && !"".equals(a.trim())) {
                                        c.add(Integer.parseInt(a));
                                    }
                                }
                                list.add(c);
                            }
                            val = list;
                        }

                    } else if (typeClass == Double.class) {
                        if (wrapCount == 0) {
                            String[] vs = fieldValue.split(",");
                            if (vs.length > 0) {
                                val = Arrays.stream(vs).filter(v -> !(null == v) && !"".equals(v.trim())).map(TemplateLoader::formatDouble).collect(Collectors.toList());
                            }
                        } else {
                            List<List<Double>> list = new ArrayList<>();
                            String[] vs = fieldValue.split(";");
                            for (String v : vs) {
                                List<Double> c = new ArrayList<>();
                                String[] temp = v.split(",");
                                for (String a : temp) {
                                    if (a != null && !"".equals(a.trim())) {
                                        c.add(formatDouble(a));
                                    }
                                }
                                list.add(c);
                            }
                            val = list;
                        }

                    }
                    break;
                case "String":
                    val = fieldValue;
                    break;
                case "int":
                    val = emptyVal ? 0 : Integer.parseInt(fieldValue);
                    break;
                case "long":
                    val = emptyVal ? 0 : Long.parseLong(fieldValue);
                    break;
                case "float":
                    val = emptyVal ? 0 : Float.parseFloat(fieldValue);
                    break;
                case "double":
                    val = emptyVal ? 0 : Double.parseDouble(fieldValue);
                    break;
                case "byte":
                    val = emptyVal ? 0 : Byte.parseByte(fieldValue);
                    break;
                case "short":
                    val = emptyVal ? 0 : Short.parseShort(fieldValue);
                    break;
                case "Date":
                    val = emptyVal ? new Date() : new Date(fieldValue);
                    break;
                case "boolean":
                    switch (fieldValue) {
                        case "1":
                            val = true;
                            break;
                        case "0":
                            val = false;
                            break;
                        default:
                            val = !emptyVal && Boolean.parseBoolean(fieldValue);
                            break;
                    }
                    break;

                default:
                    return;
            }
            m.invoke(object, val);

        } catch (Exception e) {
            log.error(
                    "Config namespace error {}: class={} colName={} type={} namespace={}",
                    e.getMessage(), object.getClass().getSimpleName(), fieldName, field, fieldValue);
        }
    }
    
    private static void parse(Class<?> clazz,int layers,String value){
        if(StringUtils.isEmpty(value)){
            return;
        }
        
        for(int i=0;i<layers;i++)
        {
        
        }
    
    }

    private static double formatDouble(String strVal) {
        if (strVal.equals("")) {
            return 0;
        }
        DecimalFormat df = new DecimalFormat("#.000000");// six
        String temp = df.format(Double.parseDouble(strVal));
        return Double.parseDouble(temp);
    }

    private static float formatFloat(String strVal) {
        if (strVal.equals("")) {
            return 0;
        }
        DecimalFormat df = new DecimalFormat("#.000000");// six
        String temp = df.format(Float.parseFloat(strVal));
        return Float.parseFloat(temp);
    }
}
