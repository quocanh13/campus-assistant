package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.example.backend.client.response.QueryResponse;
import com.example.backend.dto.request.QueryRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.service.QueryService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(path = "/query")
@AllArgsConstructor
public class QueryController {
    private final QueryService queryService;

    @PostMapping()
    public ResponseEntity<BaseResponse<QueryResponse>> query(
        @RequestBody QueryRequest request,
        @RequestParam (defaultValue = "false") Boolean includeRelatedDocuments
    ) {
        QueryResponse queryResponse = queryService.query(request.getQuery(), includeRelatedDocuments);
        BaseResponse<QueryResponse> responseData = BaseResponse.success(
            "Query sucessfully",
            queryResponse
        );
        return ResponseEntity.ok().body(responseData);
    }
    
}
