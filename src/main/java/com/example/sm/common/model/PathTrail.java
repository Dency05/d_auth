package com.example.sm.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PathTrail {

    @JsonIgnore
    String createdBy;
    @JsonIgnore
    Date created;
    @JsonIgnore
    String updatedBy;
    @JsonIgnore
    Date updated;

}