package com.example.sm.bookshop.decorator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookShopFilter {
    String search;
    String id ;
    Set<String> ids;
    int month;
    int year;

    @JsonIgnore
    boolean softDelete = false;

    public String getSearch(){
        if(search !=null){
            return search.trim();
        }
        return search;
    }

}
