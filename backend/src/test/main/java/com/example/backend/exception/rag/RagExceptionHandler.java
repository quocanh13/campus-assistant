package com.example.backend.exception.rag;

import java.util.ArrayList;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.client.response.RagBaseResponse;
import com.example.backend.dto.response.BaseResponse;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
@Order(1)
public class RagExceptionHandler {

    @ExceptionHandler(RagException.class)
    public ResponseEntity<BaseResponse<Void>> handleRagService(
        RagException exception
    ){
        ArrayList<BaseResponse.FieldError> detail = new ArrayList<>();
        ArrayList<RagBaseResponse.FieldError> ragDetail = exception.getDetail();
        if(ragDetail != null){
            for(RagBaseResponse.FieldError error : ragDetail){
                detail.add(new BaseResponse.FieldError(error.getField(), error.getMessage()));
            }
        } else detail = null;
        BaseResponse<Void> responseData = BaseResponse.<Void>builder()
            .code(exception.getCode())
            .message(exception.getMessage())
            .detail(detail).build();
        return ResponseEntity.status(exception.getStatusCode()).body(responseData);
    }
}
