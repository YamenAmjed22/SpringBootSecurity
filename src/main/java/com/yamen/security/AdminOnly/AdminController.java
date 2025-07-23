package com.yamen.security.AdminOnly;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// TODO: i need to make this end point jsu for ADMIN
@RestController
@RequestMapping("api/v1/demoController")
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello my friend , this is secured end point ");
    }
}
