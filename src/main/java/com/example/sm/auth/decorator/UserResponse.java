package com.example.sm.auth.decorator;

import com.example.sm.common.enums.Role;
import com.example.sm.auth.model.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    String id;
    String firstName;
    String middleName;
    String lastName;
    int age;
    String email;
    String userName;
    String password;
    Address address;
    Role role;
    String fullName;
    String mobileNo;
    @JsonFormat(pattern="yyyy-MM-dd")
    Date birthDate;
    @JsonIgnore
    String token;

    List<Result> results;
    Set<String> userId;
    @JsonIgnore
    boolean softDelete =false;
}
