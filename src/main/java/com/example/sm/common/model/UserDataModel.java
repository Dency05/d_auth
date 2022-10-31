package com.example.sm.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Document(collection = "user_imported_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDataModel {
    @Id
    String  id;
    String firstName;
    String middleName;
    String lastName;
    String email;
    String mobileNo;
    String userName;
    String city;
    String state;
    String address;
    Date ImportDate;
    String importedId;
    boolean duplicateEmail=false;
    boolean emptyEmail = false;
    boolean importFromExcel = false;
    @JsonIgnore
    boolean softDelete= false;
}
