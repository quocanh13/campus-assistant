package com.example.backend.exception.rag;

import java.util.ArrayList;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import com.example.backend.client.response.RagBaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RagException extends RuntimeException {
    @NonNull
    private final HttpStatusCode statusCode;
    private final String code;
    private final String message;
    private final ArrayList<RagBaseResponse.FieldError> detail;
    public RagException(RagBaseResponse<Void> responseData, @NonNull HttpStatusCode statusCode){
        this.statusCode = statusCode;
        code = responseData.getCode();
        message = responseData.getMessage();
        detail = responseData.getDetail();
    }
}
