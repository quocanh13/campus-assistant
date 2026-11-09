package com.example.backend.client.response;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RagBaseResponse<T> {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FieldError{
        private String field;
        private String message;
    }

    private String code;
    private String message;
    private ArrayList<FieldError> detail;
    private T data;
}
