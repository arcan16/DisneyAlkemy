package com.pelisflix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/securedTest")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok().body("{\"message\":\"Acceso exitoso\"}");
    }
    @GetMapping("/unSecuredTest")
    public ResponseEntity<?> unsecuredTest(){
        return ResponseEntity.ok().body("{\"message\":\"Acceso sin seguridad exitoso\"}");
    }

}
