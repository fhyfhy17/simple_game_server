package com.util;

import lombok.Data;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GenTemplatesTypeUtil {


    public static void generate() throws IOException {
        String templatesTypePath = FileUtil.getTemplatesTypePah();

        System.out.println("templatesTypePath = " + templatesTypePath);

        List<String> fileList = FileUtil.getFiles(templatesTypePath, ".xml");
        convert(fileList);
    }

    public static void convert(List<String> fileList) {
        if(Objects.isNull(fileList)||fileList.isEmpty()){
            System.err.println("没有找到任何文件");
            return;
        }
        for (String path : fileList) {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("文件不存在, path=" + path);
                return;
            }
            convert(file);
        }

    }

    public static void convert(File file) {
        try {
            writeToModel(file);
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    private static class TypeClass {
        private int id;
        private String name;
        private String describe;
    }

    private static void writeToModel(File file) throws JDOMException, IOException {

        Document doc = new SAXBuilder().build(file);
        Element root = doc.getRootElement();
        List<TypeClass> typeList = new ArrayList<>();
        Iterator<Element> it2 = root.getChildren().iterator();
        it2.next();
        while (it2.hasNext()) {
            Element next = it2.next();

            TypeClass t = new TypeClass();
            Attribute id = next.getAttribute("id");
            String idValue = (null == id.getValue()) ? "" : id.getValue().trim();
            t.setId(Integer.parseInt(idValue));

            Attribute name = next.getAttribute("name");
            String nameValue = (null == name.getValue()) ? "" : name.getValue().trim();
            t.setName(nameValue);

            Attribute describe = next.getAttribute("describe");
            String describeValue = (null == describe.getValue()) ? "" : describe.getValue().trim();
            t.setDescribe(describeValue);

            typeList.add(t);

        }
        int first = 0;
        int last = 0;

        if (typeList.size() > 0) {
            first = typeList.get(0).id;
            last = typeList.get(typeList.size() - 1).id;
        }

        String templateFileName = "";
        String xmlName = file.getName().split("_")[0];
        templateFileName = xmlName.substring(0, xmlName.indexOf("."));
        writeToModel(templateFileName, typeList, file.getName(), first, last, typeList.size());
//

    }

    //
    private static void writeToModel(String fileName, List<TypeClass> typeList, String fullPathName, int first, int last, int size) {
        System.out.println("---------------------------");
        String className = fileName;
        StringBuilder buff = new StringBuilder();
        buff.append("package com.template.templates.type;\n\n");


        buff.append("public class ").append(toUpperFirstLetter(className)).append(" {\n");


        for (TypeClass t : typeList) {


            buff.append("\n    public static final int ").append(toUpperFirstLetter(t.getName())).append(" = ")
                    .append(t.getId())
                    .append(";")
                    .append("//")
                    .append(t.getDescribe())
                    .append("\r\n");
        }
        buff.append("\r\n");
        buff.append("\n    public static final int FIRST_ID = ")
                .append(first)
                .append(";")
                .append("//第一个ID\r\n");

        buff.append("\n    public static final int LAST_ID = ")
                .append(last)
                .append(";")
                .append("//最后一个ID\r\n");

        buff.append("\n    public static final int TYPE_SIZE = ")
                .append(size)
                .append(";")
                .append("//ID数量\r\n");

        buff.append("\r\n}");
        System.out.println(buff.toString());

        FileUtil.writeStringToFile(FileUtil.getJavaTemplatesPath()
                        + File.separator
                        + "type"
                        + File.separator
                        + toUpperFirstLetter(className) + ".java"
                , buff.toString(), Charset.forName("utf-8"));

    }


    public static String toUpperFirstLetter(String letter) {
        if (letter.equals("int")) {
            letter = "integer";
        }

        char[] arr = letter.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return String.valueOf(arr);
    }


}
