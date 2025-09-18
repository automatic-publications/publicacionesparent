/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.publicaciones.service;

import com.publicaciones.model.EstadoPublicacion;
import com.publicaciones.model.Publicacion;
import com.publicaciones.repository.PublicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Bossa
 */
@Service
public class PublicacionService {

    private final PublicacionRepository repository;

    public PublicacionService(PublicacionRepository repository) {
        this.repository = repository;
    }

    public List<Publicacion> listar() {
        return repository.findAll();
    }
    
     public List<Publicacion> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public Optional<Publicacion> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Publicacion guardar(Publicacion publicacion) {
        return repository.save(publicacion);
    }
    
    public List<Publicacion> buscarPorEstado(EstadoPublicacion estado) {
        return repository.findByEstado(estado);
    }
    
    public List<Publicacion> buscarPorUsuarioYEstado(Long usuarioId, EstadoPublicacion estado) {
    return repository.findByUsuarioIdAndEstado(usuarioId, estado);
        }



    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}