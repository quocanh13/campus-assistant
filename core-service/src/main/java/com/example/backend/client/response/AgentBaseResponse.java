package com.example.backend.client.response;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentBaseResponse<T> {
    private String code;
    private String message;
    private ArrayList<RagBaseResponse.FieldError> detail;
    private T data;
}
