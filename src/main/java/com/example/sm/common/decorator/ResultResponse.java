package com.example.sm.common.decorator;

import com.example.sm.auth.decorator.Result;
import com.example.sm.auth.decorator.Resultupdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Component
public class ResultResponse <T>{
    T data;
    Response status;
    Resultupdate result;
}
