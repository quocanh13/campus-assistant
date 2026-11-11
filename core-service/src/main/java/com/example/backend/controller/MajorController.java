package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.backend.dto.request.NewMajorRequest;
import com.example.backend.dto.request.PatchMajorRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.entity.Major;
import com.example.backend.service.MajorService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller 
@RequestMapping(path = "/majors")
@AllArgsConstructor 
public class MajorController {
    private final MajorService majorService;
    
    @PostMapping
    public ResponseEntity<BaseResponse<Major>> newMajor(@RequestBody @Valid NewMajorRequest request) {
        Major newMajor = majorService.newMajor(request);
        BaseResponse<Major> response = BaseResponse.success(null, newMajor);
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/{code}")
    public ResponseEntity<BaseResponse<Major>> getMajor(@PathVariable  String code) {
        Major newMajor = majorService.getMajor(code);
        BaseResponse<Major> response = BaseResponse.success(null, newMajor);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{code}")
    public ResponseEntity<BaseResponse<Major>> patchMajor(
        @PathVariable String code,
        @RequestBody @Valid PatchMajorRequest request
    ) {
        Major major = majorService.patchMajor(code, request);
        BaseResponse<Major> response = BaseResponse.success("Update major successfully", major);
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping
    public ResponseEntity<BaseResponse<List<Major>>> getAllMajors() {
        List<Major> majors = majorService.getAllMajors();
        BaseResponse<List<Major>> response = BaseResponse.success(null, majors);
        return ResponseEntity.ok().body(response);
    }

    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    

    @DeleteMapping("/{code}")
    public ResponseEntity<BaseResponse<Void>> deleteMajor(@PathVariable String code) {
        majorService.deleteMajor(code);;
        BaseResponse<Void> response = BaseResponse.success(null, null);
        return ResponseEntity.ok().body(response);
    }
}
