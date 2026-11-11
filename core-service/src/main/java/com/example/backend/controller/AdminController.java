package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateAdminRequest;
import com.example.backend.dto.request.PatchAdminRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.admin.AdminResponse;
import com.example.backend.service.AdminService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admins")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<BaseResponse<AdminResponse>> createAdmin(
            @Valid @RequestBody CreateAdminRequest request) {
        AdminResponse admin = adminService.createAdmin(request);
        BaseResponse<AdminResponse> response = BaseResponse.success(
                "Create admin successfully", admin);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<AdminResponse>>> getAdmins(
            @RequestParam(defaultValue = "0") int index,
            @RequestParam(defaultValue = "10") int limit) {
        List<AdminResponse> admins = adminService.getAdmins(index, limit);
        BaseResponse<List<AdminResponse>> response = BaseResponse.success(
                "Get admins successfully", admins);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AdminResponse>> getAdmin(
            @PathVariable Integer id) {
        AdminResponse admin = adminService.getAdmin(id);
        BaseResponse<AdminResponse> response = BaseResponse.success(
                "Get admin successfully", admin);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<AdminResponse>> patchAdmin(
            @PathVariable Integer id,
            @Valid @RequestBody PatchAdminRequest request) {
        AdminResponse admin = adminService.patchAdmin(id, request);
        BaseResponse<AdminResponse> response = BaseResponse.success(
                "Update admin successfully", admin);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAdmin(
            @PathVariable Integer id) {
        adminService.deleteAdmin(id);
        BaseResponse<Void> response = BaseResponse.success(
                "Delete admin successfully", null);
        return ResponseEntity.ok().body(response);
    }
}
