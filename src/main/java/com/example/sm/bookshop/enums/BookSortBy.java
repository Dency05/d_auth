package com.example.sm.bookshop.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum BookSortBy {
    BOOK_NAME("bookName"),
    STUDENT_NAME("StudentName"),
    PRICE("price");
    @JsonIgnore
    private String value;
    BookSortBy(String value) {
        this.value = value;
    }
}
