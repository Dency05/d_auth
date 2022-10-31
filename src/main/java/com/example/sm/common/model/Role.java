package com.example.sm.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
    @Document(collection = "role")
    @Getter
    @Setter
    @NoArgsConstructor
    public class Role extends PathTrail {
        @Id
        String id;
        String specificRole;   //CHAPTER_ADMIN
        String roleDescription;
        int priorityLevel;
        @JsonIgnore
        boolean softDelete;
        boolean selectable;

    }

