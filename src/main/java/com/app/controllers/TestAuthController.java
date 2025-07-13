package com.app.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/method")
/*// Deniega el acceso al controlador a cualquier usuario
@PreAuthorize("denyAll()")*/
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
    public String helloGet(){
        return "Hello Get";
    }

    // Permite el acceso a /auth/post a un usuario que tenga el permiso de CREATE
    @PostMapping("/post")
    public String helloPost(){return "Hello Post";}

    @PatchMapping("/patch")
    public String helloPatch(){return "Hello Patch";}

}
