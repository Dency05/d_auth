package com.example.sm.common.model;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTUser {

    private enum ClaimFiledNames{
        ID,ROLE
    }

    String id;
    List<String> role;

    public JWTUser(String id) {
        this.id = id;
    }

    public Map<String, Object> toClaim(){
        Map<String, Object> claim   = new HashMap<>();
        claim.put(ClaimFiledNames.ID.toString(),id);
        claim.put(ClaimFiledNames.ROLE.toString(),role);
        return claim;
    }

    public boolean hasRole(String role){

        return this.role.contains(role);
    }

    public static JWTUser fromClaim(Claims claim){
        JWTUser jwtUser = new JWTUser();
        jwtUser.setId((String) claim.get(ClaimFiledNames.ID.toString()));
        jwtUser.setRole((List<String>) claim.get(ClaimFiledNames.ROLE.toString()));
        return jwtUser;
    }
}
