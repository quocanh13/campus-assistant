package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.backend.dto.request.NewUnitRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.entity.Unit;
import com.example.backend.service.UnitService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Controller()
@RequestMapping(path = "/units")
@AllArgsConstructor
public class UnitController {
    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<BaseResponse<Unit>> newUnit(@RequestBody @Valid NewUnitRequest request) {
        Unit newUnit = unitService.newUnit(request);
        BaseResponse<Unit> response = BaseResponse.success(null, newUnit);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Unit>>> getAllUnits() {
        List<Unit> units = unitService.getAllUnits();
        BaseResponse<List<Unit>> response = BaseResponse.success(null, units);
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/{code}")
    public ResponseEntity<BaseResponse<Unit>> getUnit(@PathVariable String code){
        Unit unit = unitService.getUnit(code);
        BaseResponse<Unit> response = BaseResponse.success(null, unit);
        return ResponseEntity.ok().body(response);
    }
    

    @DeleteMapping("/{code}")
    public ResponseEntity<BaseResponse<Void>> deleteUnit(@PathVariable String code) {
        unitService.deleteUnit(code);
        BaseResponse<Void> response = BaseResponse.success(null, null);
        return ResponseEntity.ok().body(response);
    }
}
