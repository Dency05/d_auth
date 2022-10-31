package com.example.sm.common.advice;

import com.example.sm.common.decorator.DataResponse;
import com.example.sm.common.decorator.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.stream.Collectors;
@ControllerAdvice
public class ControllerErrorConfig extends ResponseEntityExceptionHandler {

    @Autowired
    Response response;
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        String error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList()).get(0);
        dataResponse.setStatus(response.getInvalidRequestResponse(error));
        return new ResponseEntity<>(dataResponse, headers, status);
    }
}

