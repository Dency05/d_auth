package com.example.sm.common.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserImportResponse {

    List<String> excelHeaders;
    Map<String,String>  mappingHeaders;
    String id;
}


