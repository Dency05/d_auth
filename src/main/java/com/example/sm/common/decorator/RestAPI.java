package com.example.sm.common.decorator;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Document(collection = "rest_apis")
@Data
@NoArgsConstructor
public class RestAPI {
    @Id
    String id;
    String name;
    String description;
    List<String> roles;
}
