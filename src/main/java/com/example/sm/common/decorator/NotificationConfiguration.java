package com.example.sm.common.decorator;

import com.example.sm.common.model.TemplateModel;
import com.google.common.io.Resources;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.net.URL;
import java.nio.charset.StandardCharsets;
@Data
@AllArgsConstructor
public class NotificationConfiguration {


    TemplateModel resultTemplate;

   // NotificationSetting notificationSetting;
    public NotificationConfiguration(){

        //notificationSetting = new NotificationSetting("dd/MM/yyyy","HH:mm","EEEE, MMMM dd, yyyy hh:mm a (z)","THEME_1","CST");
        resultTemplate = new TemplateModel("Result ",loadHtmlTemplateOrReturnNull("result"),false);
    }

    private String loadHtmlTemplateOrReturnNull(String name){
        try{
            URL url = Resources.getResource("templates/"+name+".html");
            return Resources.toString(url, StandardCharsets.UTF_8);
        }catch (Exception e){
            return "";
        }
    }
}