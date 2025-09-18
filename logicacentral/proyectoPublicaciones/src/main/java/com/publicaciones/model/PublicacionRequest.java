package com.publicaciones.model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Bossa
 */

public class PublicacionRequest {
    private String descripcion;
    private String imagenBase64;
    private String videoUrl;
    private Long usuarioId;
    
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenBase64() { return imagenBase64; }
    
    public void setImagenBase64(String imagenBase64) { this.imagenBase64 = imagenBase64; }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            this.videoUrl = "0";
        } else {
            this.videoUrl = videoUrl;
        }
    }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
}