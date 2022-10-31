package com.example.sm.auth.decorator;

import com.example.sm.auth.model.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddRequest {
    String id;
    String firstName;
    String middleName;
    String lastName;
    String email;
    String userName;
    String password;
    Address address;
    String confirmPassword;
    String newPassword;
    String mobileNo;
    @JsonFormat(pattern="yyyy-MM-dd")
    Date birthDate;
}
