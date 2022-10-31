package com.example.sm.cc.model;

import com.example.sm.cc.enums.MembershipPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection= "chapterName")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterName {
    String id;
    String name;
    String description;
    Set<MembershipPlan> membershipPlans;
    @JsonIgnore
    boolean mainChapter= false;
    @JsonIgnore
    boolean softDelete= false;
}
