package com.example.sm.common.model;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
public class ImportedData {
    List<String> headers;
    Map<String,List<Object>> data;
    Date importDate;
}
