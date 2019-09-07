package com.util;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class XlsToxmlUtil {

    public static void write(String path) throws IOException {
        List<String> filelist = FileUtil.getFiles(path, ".xls");
        for (String fileName : filelist) {
            // 这里是生成工作簿
            Workbook wb = null;
            InputStream instream = new FileInputStream(fileName);
            try {
                wb = Workbook.getWorkbook(instream);
                // 获取第一张Sheet表
                Sheet[] sheets = wb.getSheets();
                for (Sheet sheet : sheets) {
                    if (!isLetterDigit(sheet.getName())) {
                        continue;
                    }
                    System.out.println("正在生成" + fileName + "_" + sheet.getName());
                    // 创建根节点;
                    Element root = new Element("root");
                    // 将根节点添加到文档中；
                    Document Doc = new Document(root);
                    // 获取Sheet表中所包含的总列数
                    int columns = sheet.getColumns();
                    // 获取Sheet表中所包含的总行数
                    int rows = sheet.getRows();
                    // 获取指定单元格的对象引用

                    for (int j = 0; j < rows; j++) {
                        if (j <= 1)
                            continue;
                        if (sheet.getCell(0, j) == null || sheet.getCell(0, j).getContents().trim().equals("")) {
                            continue;
                        }

                        Element elements = new Element("tr");
                        for (int i = 0; i < columns; i++) {
                            String column1 = sheet.getCell(i, 0).getContents().trim();
                            if (column1.equals("")) {
                                continue;
                            }
                            Cell cell = sheet.getCell(i, j);
                            elements.setAttribute(column1, cell.getContents().trim());
                            root.addContent(elements.detach());
                        }
                    }

                    Format format = Format.getPrettyFormat();
                    XMLOutputter XMLOut = new XMLOutputter(format);
                    int index = fileName.lastIndexOf("\\");
                    XMLOut.output(Doc, new FileOutputStream(fileName.substring(0, index) + "\\xml\\" + fileName.substring(index) + "_" + sheet.getName() + ".xml"));
                }

            } catch (BiffException e) {
                e.printStackTrace();
            }
        }
    }

    public static void read() throws JDOMException, IOException {
        File file = new File("C:/books.xml");
        SAXBuilder s = new SAXBuilder();
        Document d = s.build(file);
        Element root = d.getRootElement();
        Iterator<Element> it = root.getChildren().<Element>iterator();
        while (it.hasNext()) {
            Element e = it.next();
            List l = e.getAttributes();
            for (int i = 0; i < l.size(); i++) {
                Attribute a = (Attribute) l.get(i);
                System.out.println(a.getName() + "_" + a.getValue());
            }

        }
    }

    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z_-]+$";// 加入横线，下划线
        // \u4e00-\u9fa5 这是汉字
        return str.matches(regex);
    }

    public static void generate(String path) {
        try {
            write(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}