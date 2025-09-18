/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.userauth.entity;

/**
 *
 * @author Bossa
 */
public class LoginRequest {
    private String email;
    private String clave;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public LoginRequest() {
    }

    public LoginRequest(String email, String clave) {
        this.email = email;
        this.clave = clave;
    }

   
}

