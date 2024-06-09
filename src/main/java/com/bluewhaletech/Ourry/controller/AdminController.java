package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;
import com.bluewhaletech.Ourry.service.AdminServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AdminController {
    private final AdminServiceImpl adminService;

    @Autowired
    public AdminController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/admin/createAdmin")
    public ResponseEntity<Object> createAdmin(HttpServletRequest request, @RequestBody MemberRegistrationDTO dto) {
        String fcmToken = request.getHeader("FirebaseCloudMessaging");
        adminService.createAdmin(dto, fcmToken);
        return ResponseEntity.ok().build();
    }
}