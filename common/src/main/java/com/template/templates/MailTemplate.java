package com.template.templates;

import com.annotation.Template;
import lombok.Data;

import java.util.*;

@Data
@Template(path = "Mail.xlsx_mail.xml")
public class MailTemplate extends AbstractTemplate {

    private String name; //
    private String describe; //
    private int title; //
    private int content; //
    private List<List<Integer>> items = new ArrayList<>(); //

}