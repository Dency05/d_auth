package com.example.sm.cc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection= "cc_Admin_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCAdminConfiguration {

    Set<String> membershipPlan = getMembershipData();
    private Set<String> getMembershipData() {
        Set<String> membership = new HashSet<>();
        membership.add("lifeTime");
        membership.add("12_year");
        membership.add("6_year");
        membership.add("4_year");
        membership.add("2_months");
        return membership;
    }

    Set<String> Chapter = getChapterData();

    private Set<String> getChapterData() {
        Set<String> chapterName= new HashSet<>();
        chapterName.add("New Jersey");
        chapterName.add("Georgia");
        chapterName.add("Michigan");
        chapterName.add("Illinois");
        chapterName.add("North Carolina");
        return chapterName;
    }
}
