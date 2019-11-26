package com.template.templates;

import com.annotation.Template;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@Template(path = "Mail.xlsx_mail.xml")
public class MailTemplate extends AbstractTemplate {

    private String name; //
    private String describe; //
    private int title; //
    private int content; //
    private List<List<Integer>> items = new ArrayList<>(); //

}