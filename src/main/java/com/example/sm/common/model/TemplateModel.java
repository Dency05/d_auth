package com.example.sm.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class TemplateModel {

    String title;
    String message;
    boolean allowBcc;
    String templateName;
    public TemplateModel(String title, String message, boolean allowBcc) {
        this.title = title;
        this.message = message;
        this.allowBcc = allowBcc;
    }

    public TemplateModel(String title, String message, boolean allowBcc, String templateName) {
        this.title = title;
        this.message = message;
        this.allowBcc = allowBcc;
        this.templateName = templateName;
    }

}
