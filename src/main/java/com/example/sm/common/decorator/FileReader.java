package com.example.sm.common.decorator;

import com.google.common.io.Resources;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.charset.StandardCharsets;
@Slf4j
@Data
public class FileReader {

    public static String loadFile(String fileName){
        try {
            URL url = Resources.getResource(fileName);
            return Resources.toString(url, StandardCharsets.UTF_8);
        }
        catch (Exception e){
           log.error(e.getMessage());
        }
        return "";
    }
}
