package com.publicaciones.DTO;

/**
 *
 * @author Bossa
 */
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;

    // Constructor vacío
    public UsuarioResponse() {
    }

    // Constructor con todos los campos
    public UsuarioResponse(Long id, String nombre, String apellido, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
