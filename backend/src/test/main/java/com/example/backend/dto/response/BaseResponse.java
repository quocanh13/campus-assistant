package com.example.backend.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class BaseResponse<T> {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FieldError{
        private String field;
        private String message;
    }

    private String code;
    private String message;
    private List<FieldError> detail;
    private T data;

    public static <T> BaseResponse<T> success(String message, T data){
        return BaseResponse.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static BaseResponse<Void> serverError(){
        return BaseResponse.<Void>builder()
                .code("SERVER_ERROR")
                .message("Server error")
                .data(null).build();
    }
}


