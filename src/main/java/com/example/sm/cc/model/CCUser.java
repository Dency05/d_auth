package com.example.sm.cc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "CC_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCUser {
    @Id
    String id;
    String email;
    String firstName;
    String lastName;
    String middleName;
    Set<String> planIds;

    @JsonIgnore
    boolean softDelete=false;
}
