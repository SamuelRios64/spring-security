package com.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
// Deniega el acceso al controlador a cualquier usuario
@PreAuthorize("denyAll()")
public class TestAuthController {

   /* @GetMapping("/hello")
    @PreAuthorize("permitAll()")
    public String hello(){
        return "Hello World";
    }

    @GetMapping("/hello-secured")
    @PreAuthorize("hasAuthority('READ')")
    public String helloSecured(){
        return "Hello World Secured ";
    }*/

    // Permite el acceso a /auth/get a un usuario que tenga el permiso de READ
    @GetMapping("/get")
    @PreAuthorize("hasAuthority('READ')")
    public String helloGet(){
        return "Hello Get";
    }

    // Permite el acceso a /auth/post a un usuario que tenga el permiso de CREATE
    @PostMapping("/post")
    @PreAuthorize("hasAuthority('CREATE')")
    public String hellGet(){return "Hello Post";}

}
