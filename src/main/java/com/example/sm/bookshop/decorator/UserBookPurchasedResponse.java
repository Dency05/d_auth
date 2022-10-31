package com.example.sm.bookshop.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookPurchasedResponse {
String month;
List<StudentData> studentData;
int count;
}
