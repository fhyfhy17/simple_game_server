package com.template.templates;

import com.annotation.Template;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@Template(path = "Language.xlsx_language.xml")
public class LanguageTemplate extends AbstractTemplate {

    private String chinese; //
    private String englist; //

}