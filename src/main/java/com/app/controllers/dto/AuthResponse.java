package com.app.controllers.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "message", "jwt", "status"}) // orden a la respuesta
public record AuthResponse (String username, String message, String jwt, boolean status){

}
