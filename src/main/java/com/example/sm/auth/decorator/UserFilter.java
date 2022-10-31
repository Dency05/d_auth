package com.example.sm.auth.decorator;

import com.example.sm.common.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserFilter {
    String search;
    Role role;

    Set<String> ids;


    @JsonIgnore
    boolean softDelete = false;

    public String getSearch(){
        if(search !=null){
            return search.trim();
        }
        return search;
    }
    public Role getRole(){
        return role;
    }
}
