package com.example.sm.common.decorator;

import com.example.sm.common.model.EmailModel;
import com.example.sm.common.model.TemplateModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.util.Set;
@Component
public class  NotificationParser {
    // Seth the notification with given value with mustache templating
    /*public EmailModel parseEmailNotification(TemplateModel templateModel, Object data, String email, String templateName, Set<String> bcc, List<EmailAttachment> attachmentList){
        EmailModel model = parseEmailNotification(templateModel, data, email, templateName,bcc);
        model.setAttachmentList(attachmentList);
        return model;
    }*/
    public EmailModel parseEmailNotification(TemplateModel templateModel, Object data, String email, String templateName, Set<String> bcc, Set<String> cc){
        EmailModel model = parseEmailNotification(templateModel, data, email, templateName,bcc);
        model.setCc(cc);
        return model;
    }
    public EmailModel parseEmailNotification(TemplateModel templateModel, Object data, String email, String templateName, Set<String> bcc){
        EmailModel model = parseEmailNotification(templateModel, data, email, templateName);
        if(templateModel.isAllowBcc()){
            model.setBcc(bcc);
        }
        return model;
    }

    public EmailModel parseEmailNotification(TemplateModel templateModel,Object data,String email){
        return parseEmailNotification(templateModel,data,email,null);
    }

    public EmailModel parseEmailNotification(TemplateModel templateModel, Object data, String email, String templateName){
        templateModel.setTitle(new TemplateParser<>().compileTemplate(templateModel.getTitle(),data));
        templateModel.setMessage(new TemplateParser<>().compileTemplate(templateModel.getMessage(),data));
        EmailModel model = new EmailModel();
        if (!StringUtils.isEmpty(templateModel.getTemplateName())){
            model.setTemplateName(templateModel.getTemplateName());
        }else{
            model.setTemplateName(templateName);
        }
        model.setTo(email);
        model.setSubject(templateModel.getTitle());
        model.setMessage(templateModel.getMessage());
        return model;
    }
}