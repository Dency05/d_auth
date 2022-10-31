package com.example.sm.common.decorator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeFilter {
    String search;
    String Id;

    @JsonIgnore
    boolean softDelete = false;

    public  String getSearch(){
        if(search !=null){
            return search.trim();
        }
        return search;
    }

}
