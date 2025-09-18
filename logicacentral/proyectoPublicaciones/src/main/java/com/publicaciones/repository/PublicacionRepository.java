/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.publicaciones.repository;

/**
 *
 * @author Bossa
 */
import com.publicaciones.model.EstadoPublicacion;
import com.publicaciones.model.Publicacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findByEstado(EstadoPublicacion estado);
    
    List<Publicacion> findByUsuarioIdAndEstado(Long usuarioId, EstadoPublicacion estado);

    
    List<Publicacion> findByUsuarioId(Long usuarioId);
}