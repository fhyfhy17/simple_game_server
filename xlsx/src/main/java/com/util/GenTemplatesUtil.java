package com.util;

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

public class GenTemplatesUtil {


    public static void genarate() throws IOException {
        String templatesPath = FileUtil.getTemplatesPah();
        //TODO 先删除文件夹下所有文件，防止有错误的文件留在里面
        System.out.println("templatesPath = " + templatesPath);


        List<String> fileList = FileUtil.getFiles(templatesPath, ".xml");
        convert(fileList);

        GenTemplatesTypeUtil.generate();

    }

    public static void convert(List<String> fileList) {
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
//		if (file.isFile())
        try {
            writeToModel(file);
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
//		else {
//			File[] fileArr = file.listFiles();
//			for (File f : fileArr) {
//				if (!f.exists())
//					continue;
//				writeToModel(f);
//			}
//		}
    }

    private static void writeToModel(File file) throws JDOMException, IOException {

        Document doc = new SAXBuilder().build(file);
        Element root = doc.getRootElement();
        Iterator<Element> it = root.getChildren().<Element>iterator();
        List<Attribute> attrList = new ArrayList<>();
        if (it.hasNext()) {
            Element next = it.next();

            next.getAttributes().forEach(x ->
            {
                Attribute attr = (Attribute) x;
                attrList.add(attr);
            });
        }
        String templateFileName = "";
        String xmlName = file.getName().split("_")[1];
        templateFileName = xmlName.substring(0, xmlName.indexOf("."));
        writeToModel(templateFileName, attrList, file.getName());
//
        writeToModelCache(templateFileName);
    }

    //
    private static void writeToModel(String fileName, List<Attribute> attrList, String fullPathName) {
        System.out.println("---------------------------");
        String className = fileName + "Template";
        StringBuilder buff = new StringBuilder();
        buff.append("package com.template.templates;\r\n\r\n")
                .append("import com.annotation.Template;\r\n")
                .append("import lombok.Data;\r\n")
                .append("\r\n")
                .append("import java.util.*;\r\n")
                .append("\r\n")
                .append("import org.springframework.stereotype.Component;\r\n")
                .append("\r\n");


        buff.append("@Data\r\n");
        buff.append("@Component\r\n");
        buff.append("@Template(path = \"").append(fullPathName).append("\")\r\n");


        buff.append("public class ").append(toUpperFirstLetter(className)).append(" extends AbstractTemplate {\r\n");


        for (Attribute attr : attrList) {
            String key = attr.getName();
            String value = attr.getValue();
            if ("id".equals(key)) {
                continue;
            }
            Pair<String, String> type = getType(value);

            buff.append("\r\n    private ").append(type.t1).append(" ")
                    .append(key)
                    .append("".equals(type.t2) ? "" : " = " + type.t2)
                    .append("; //");
//                    .append()
        }
        buff.append("\r\n");

//        for (Attribute attr : attrList) {
//            String key = attr.getName();
//            String value = attr.getValue();
//            if ("id".equals(key)) {
//                continue;
//            }
//            buff.append("\n\tpublic ").append(getType(value)).append(" get").append(toUpperFirstLetter(key)).append("() {").append("\n\t\treturn this.").append(key).append(";").append("\n\t}\n");
//            buff.append("\n\tpublic void set").append(toUpperFirstLetter(key)).append("(").append(getType(value)).append(" ").append(key).append(") {").append("\n\t\tthis.").append(key)
//                    .append(" = ").append(key).append(";").append("\n\t}\n");
//        }


        buff.append("\r\n}");
        System.out.println(buff.toString());

        FileUtil.writeStringToFile(FileUtil.getJavaTemplatesPath()
                        + File.separator + toUpperFirstLetter(className) + ".java"
                , buff.toString(), Charset.forName("utf-8"));

    }

    public static Pair<String, String> getType(String value) {
        if (value.contains("[][]")) {
            value = value.substring(0, value.indexOf("[][]"));
            value = "List<List<" + toUpperFirstLetter(value) + ">>";
            Pair<String, String> pair = new Pair<>(value, "new ArrayList<>()");
            return pair;
        } else if (value.contains("[]")) {
            value = value.substring(0, value.indexOf("[]"));
            value = "List<" + toUpperFirstLetter(value) + ">";
            Pair<String, String> pair = new Pair<>(value, "new ArrayList<>()");
            return pair;
        } else {
            value = singleToUpperFirstLetter(value);
            Pair<String, String> pair = new Pair<>(value, "");
            return pair;
        }
    }

    public static String singleToUpperFirstLetter(String letter) {
        if (letter.equals("string")) {
            return "String";
        }
        if (letter.equals("bool")) {
            return "boolean";
        }
        return letter;
    }

    public static String toUpperFirstLetter(String letter) {
        if (letter.equals("int")) {
            letter = "integer";
        }

        char[] arr = letter.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return String.valueOf(arr);
    }
    
    private static void writeToModelCache(String fileName) {
     
        System.out.println("---------------------------");
        String className = toUpperFirstLetter(fileName) + "TemplateCache";
        String fullFilePath = FileUtil.getJavaTemplatesPath()
                + File.separator + toUpperFirstLetter(className) + ".java";
        if(FileUtil.exists(fullFilePath)){
            System.out.println(fullFilePath+" 已存在");
            return;
        }
        
        String rawClassName = toUpperFirstLetter(fileName) + "Template";
        StringBuilder buff = new StringBuilder();
        buff.append("package com.template.templates;\r\n\r\n")
                .append("import java.util.HashMap;\r\n")
                .append("\r\n");
        
        
        buff.append("public class ").append(className).append(" {\r\n\r\n");

        buff.append("   public static HashMap<Integer,"+rawClassName+"> cache=new HashMap<>();\r\n\r\n");
        
        buff.append("   public static HashMap<Integer,"+rawClassName+"> getMap(){\r\n");
        
        buff.append("       return cache;\r\n");
        
        buff.append("   }\r\n\r\n");

        buff.append("   public static void setMap(HashMap<Integer," + rawClassName + "> map){\r\n");

        buff.append("       " + className + ".cache = map;\r\n");

        buff.append("   }\r\n\r\n");
        
        buff.append("   public static "+rawClassName+" get(int id){\r\n");
        
        buff.append("       return cache.get(id);\r\n");
    
        buff.append("   }\r\n\r\n");
        
        buff.append("   public static void after(){\r\n\r\n");
        
        buff.append("   }\r\n");
    
        //public static HashMap<Integer,LanguageTemplate> cache=new HashMap<>();
        //
        //public static HashMap<Integer,LanguageTemplate> getMap(){
        //    return cache;
        //}
        //
        //public static LanguageTemplate get(int id){
        //    return cache.get(id);
        //}
        //
        //public static void after(){
        //
        //}
        
        
        
        buff.append("\r\n}");
        System.out.println(buff.toString());
        
        FileUtil.writeStringToFile(FileUtil.getJavaTemplatesPath()
                        + File.separator + toUpperFirstLetter(className) + ".java"
                , buff.toString(), Charset.forName("utf-8"));
        
    }

}
