package com.example.sm.bookshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "student_log")
public class StudentLog {
    Set<String> studentId;
    @Id
    String id;
}
