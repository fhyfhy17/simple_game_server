package com.template;

import com.annotation.Template;
import com.template.templates.AbstractTemplate;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TemplateManager {
    @Autowired
    private TemplateLoader loader;
    private Map<Class<? extends AbstractTemplate>, Map<Integer, AbstractTemplate>> templates = new HashMap<>();

    /**
     * 加载模板数据的过程.
     */
    @PostConstruct
    public void load() {
        Map<String, Object> templates = SpringUtils.getBeansWithAnnotation(Template.class);
        for (Map.Entry<String, Object> t : templates.entrySet()) {
            String className = t.getKey();
            Object o = t.getValue();

            if (!(o instanceof AbstractTemplate)) {
                log.error("非模板类 = {}", className);
                continue;
            }
            Template templateAnno = o.getClass().getAnnotation(Template.class);

            String path = System.getProperty("user.dir")
                    + File.separator
                    + "bin"
                    + File.separator
                    + "templates"
                    + File.separator
                    + templateAnno.path();

            Class<? extends AbstractTemplate> subclass = o.getClass().asSubclass(AbstractTemplate.class);
            this.templates.put(subclass,
                    loader.loadTemplate(new File(path), subclass)
                            .stream()
                            .collect(Collectors.toMap(AbstractTemplate::getId, Function.identity())));
        }
    }

    public Map<Class<? extends AbstractTemplate>, Map<Integer, AbstractTemplate>> getTemplates() {
        return this.templates;
    }

    public <T extends AbstractTemplate> Map<Integer, T> getTemplateMap(Class<? extends T> clazz) {
        return (Map<Integer, T>) this.templates.get(clazz);
    }

    public <T extends AbstractTemplate> T getTemplate(Class<? extends T> clazz, Integer id) {
        return (T) this.templates.get(clazz).get(id);
    }

    public <T extends AbstractTemplate> T getTemplateSingle(Class<? extends T> clazz) {
        return (T) this.templates.get(clazz).entrySet().iterator().next().getValue();
    }

    public <T extends AbstractTemplate> List<T> getTemplateList(Class<? extends T> clazz) {
        List<T> list = new ArrayList<>();
        for (AbstractTemplate value : this.templates.get(clazz).values()) {
            list.add((T) value);
        }
        return list;
    }

}
