package com.example.sm.common.advice;


import com.example.sm.common.decorator.DataResponse;
import com.example.sm.common.decorator.Response;
import com.example.sm.common.exception.AlreadyExistException;
import com.example.sm.common.exception.EmptyException;
import com.example.sm.common.exception.InvaildRequestException;
import com.example.sm.common.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
@ControllerAdvice
public class GeneralExceptionHandler {

    @Autowired
    Response response;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<DataResponse<Object>> getError(HttpServletRequest req, NotFoundException ex) {
        return new ResponseEntity<>(new DataResponse<>(null, Response.getNotFoundResponse(ex.getMessage())), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Object>> getError(HttpServletRequest req, Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(new DataResponse<>(null, Response.getInternalServerErrorResponse()), HttpStatus.OK);
    }
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<DataResponse<Object>> getError(HttpServletRequest req, AlreadyExistException ex) {
        return new ResponseEntity<>(new DataResponse<>(null, Response.getAlreadyExists(ex.getMessage())), HttpStatus.OK);
    }
    @ExceptionHandler(EmptyException.class)
    public ResponseEntity<DataResponse<Object>> getError(HttpServletRequest req, EmptyException ex) {
        return new ResponseEntity<>(new DataResponse<>(null, Response.getEmptyResponse(ex.getMessage())), HttpStatus.OK);
    }
    @ExceptionHandler(InvaildRequestException.class)
    public ResponseEntity<DataResponse<Object>> getError(HttpServletRequest req, InvaildRequestException ex) {
        return new ResponseEntity<>(new DataResponse<>(null, Response.getInvaildResponse(ex.getMessage())), HttpStatus.OK);
    }

}