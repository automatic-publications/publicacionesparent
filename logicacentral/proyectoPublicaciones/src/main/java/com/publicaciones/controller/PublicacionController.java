package com.publicaciones.controller;

/**
 *
 * @author Bossa
 */
import com.publicaciones.model.PublicacionRequest;
import com.publicaciones.model.EstadoPublicacion;
import com.publicaciones.model.EstadoRequest;
import com.publicaciones.model.Publicacion;
import com.publicaciones.service.PublicacionService;
import com.publicaciones.service.S3Service;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

//IMPORTACIONES SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import com.publicaciones.DTO.UsuarioClient;
import com.publicaciones.DTO.UsuarioResponse;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/publicaciones")
@Tag(name = "Publicaciones", description = "Endpoints para publicaciones")
public class PublicacionController {

    private final PublicacionService service;
    private final S3Service s3Service;
    
    @Autowired
    private UsuarioClient usuarioClient;

    
    
    public PublicacionController(PublicacionService service, S3Service s3Service) {
        this.service = service;
        this.s3Service = s3Service;
    }
    
    //Obtener pblicaciones
    @Operation(
        summary = "Listar publicaciones",
        description = "Devuelve todas las publicaciones disponibles"
    )
    @ApiResponse(responseCode = "200", description = "Lista de publicaciones obtenida exitosamente")
    @GetMapping
    public List<Publicacion> listar() {
        return service.listar();
    }
    
     @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Publicacion>> listarPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }
    
    @GetMapping("/usuario-real")
public ResponseEntity<UsuarioResponse> obtenerUsuarioReal(
        @RequestHeader("Authorization") String authorizationHeader) {

    String token = authorizationHeader.replace("Bearer ", "");
    UsuarioResponse usuario = usuarioClient.obtenerUsuarioActual(token);
    return ResponseEntity.ok(usuario);
}



     @Operation(summary = "Obtener publicación por ID")
    @ApiResponse(responseCode = "200", description = "Publicación encontrada")
    @ApiResponse(responseCode = "404", description = "Publicación no encontrada")
    //Obtener pblicaciones por id
    @GetMapping("/{id}")
    public ResponseEntity<Publicacion> obtenerPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    

    /* @PostMapping
    public Publicacion crear(@RequestBody Publicacion publicacion) {
        return service.guardar(publicacion);
    } */
    
 
    // CREAR PUBLICACION, JSON SE DEBE MANDAR EN BASE 64 LA IMAGEN
    @Operation(
    summary = "Crear publicación del usuario real",
    description = "Crea una nueva publicación del usuario logueado usando descripción y una imagen en Base64 opcional"
    )
    @ApiResponse(responseCode = "200", description = "Publicación creada exitosamente")
    @PostMapping("/usuario-real/crear-publicacion")
    public ResponseEntity<Publicacion> crearPublicacionUsuarioReal(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PublicacionRequest request) throws IOException {

        // 1️⃣ Extraer token
        String token = authorizationHeader.replace("Bearer ", "");

        // 2️⃣ Obtener usuario real desde microservicio de autenticación
        UsuarioResponse usuario = usuarioClient.obtenerUsuarioActual(token);

        // 3️⃣ Crear la publicación usando el ID real y estado PENDIENTE
        Publicacion publicacion = new Publicacion();
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setEstado(EstadoPublicacion.PENDIENTE);
        publicacion.setImagenUrl("0"); // temporal
        publicacion.setVideoUrl(request.getVideoUrl());
        publicacion.setUsuarioId(usuario.getId());
        publicacion = service.guardar(publicacion);

        // 4️⃣ Subir imagen si viene en Base64
        if (request.getImagenBase64() != null && !request.getImagenBase64().trim().isEmpty()) {
            String base64 = request.getImagenBase64();
            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }
            
            byte[] imagenBytes = java.util.Base64.getDecoder().decode(request.getImagenBase64());
            String urlS3 = s3Service.subirArchivo("publicaciones/" + publicacion.getId() + ".png", imagenBytes);
            publicacion.setImagenUrl(urlS3);
            publicacion = service.guardar(publicacion);
        }

        return ResponseEntity.ok(publicacion);
    }
    
    
    // EDITAR UNA PUBLICACIÓN EN GENERAL
    @Operation(summary = "Actualizar publicación")
    @ApiResponse(responseCode = "200", description = "Publicación actualizada correctamente")
    @ApiResponse(responseCode = "404", description = "Publicación no encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<Publicacion> actualizar(@PathVariable("id") Long id, @RequestBody Publicacion publicacion) {
        return service.buscarPorId(id)
                .map(p -> {
                    p.setDescripcion(publicacion.getDescripcion());
                    p.setImagenUrl(publicacion.getImagenUrl());
                    p.setVideoUrl(publicacion.getVideoUrl()); 
                    p.setEstado(publicacion.getEstado());
                    return ResponseEntity.ok(service.guardar(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    
    
    // EDITAR EL ESTADO DE UNA PUBLCIACIÓN
    @Operation(summary = "Cambiar estado de publicación")
    @ApiResponse(responseCode = "200", description = "Estado actualizado o publicación eliminada")
    @PostMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable("id") Long id,
            @RequestBody EstadoRequest request) {
        try {
            EstadoPublicacion estado = EstadoPublicacion.valueOf(request.getEstado().toUpperCase());

            return service.buscarPorId(id)
                    .map(p -> {
                        if (estado == EstadoPublicacion.DESAPROBADO) {
                            // Eliminar archivo de S3 si existe
                            if (p.getImagenUrl() != null && !p.getImagenUrl().isEmpty()) {
                                String key = p.getImagenUrl().substring(p.getImagenUrl().lastIndexOf("/") + 1);
                                s3Service.eliminarArchivo("publicaciones/" + key);
                            }
                            // Eliminar la publicación de la BD
                            service.eliminar(p.getId());
                            return ResponseEntity.ok("Se ha borrado con éxito");
                        } else {
                            // Si no es DESAPROBADO, simplemente actualizamos
                            p.setEstado(estado);
                            return ResponseEntity.ok(service.guardar(p));
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //OBTENER UNA PUBLICACION POR EL ESTADO
    @Operation(summary = "Listar publicaciones por estado")
    @GetMapping("/estado")
    public ResponseEntity<List<Publicacion>> listarPorEstado(
            @RequestParam("estado") String estadoStr,
            @RequestParam(value = "limit", required = false) Integer limit) {

        try {
            EstadoPublicacion estado = EstadoPublicacion.valueOf(estadoStr.toUpperCase());
            List<Publicacion> publicaciones = service.buscarPorEstado(estado);

            
            if (limit != null && limit > 0 && limit < publicaciones.size()) {
                publicaciones = publicaciones.subList(0, limit);
            }

            return ResponseEntity.ok(publicaciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Listar publicaciones por estado y usuario")
    @GetMapping("/usuario/{usuarioId}/estado")
    public ResponseEntity<List<Publicacion>> listarPorUsuarioYEstado(
            @PathVariable("usuarioId") Long usuarioId,
            @RequestParam("estado") String estadoStr,
            @RequestParam(value = "limit", required = false) Integer limit) {

        try {
            EstadoPublicacion estado = EstadoPublicacion.valueOf(estadoStr.toUpperCase());
            List<Publicacion> publicaciones = service.buscarPorUsuarioYEstado(usuarioId, estado);

            if (limit != null && limit > 0 && limit < publicaciones.size()) {
                publicaciones = publicaciones.subList(0, limit);
            }

            return ResponseEntity.ok(publicaciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Listar publicaciones del usuario real por estado")
    @GetMapping("/usuario-real/estado")
    public ResponseEntity<List<Publicacion>> listarPublicacionesUsuarioRealPorEstado(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("estado") String estadoStr,
            @RequestParam(value = "limit", required = false) Integer limit) {

        // 1️⃣ Extraer token
        String token = authorizationHeader.replace("Bearer ", "");

        // 2️⃣ Obtener usuario real desde microservicio de autenticación
        UsuarioResponse usuario = usuarioClient.obtenerUsuarioActual(token);

        try {
            // 3️⃣ Convertir estado a enum
            EstadoPublicacion estado = EstadoPublicacion.valueOf(estadoStr.toUpperCase());

            // 4️⃣ Listar publicaciones del usuario por estado
            List<Publicacion> publicaciones = service.buscarPorUsuarioYEstado(usuario.getId(), estado);

            // 5️⃣ Aplicar límite si se pasa como parámetro
            if (limit != null && limit > 0 && limit < publicaciones.size()) {
                publicaciones = publicaciones.subList(0, limit);
            }

            return ResponseEntity.ok(publicaciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Listar publicaciones del usuario real")
    @GetMapping("/usuario-real/publicaciones")
    public ResponseEntity<List<Publicacion>> listarPublicacionesUsuarioReal(
            @RequestHeader("Authorization") String authorizationHeader) {
        // 1️⃣ Extraer token
        String token = authorizationHeader.replace("Bearer ", "");

        // 2️⃣ Obtener usuario real desde microservicio de autenticación
        UsuarioResponse usuario = usuarioClient.obtenerUsuarioActual(token);

        // 3️⃣ Listar publicaciones usando el ID real del usuario
        List<Publicacion> publicaciones = service.listarPorUsuario(usuario.getId());

        // 4️⃣ Devolver la lista
        return ResponseEntity.ok(publicaciones);
    }

     
    @Operation(summary = "Eliminar publicación")
    @ApiResponse(responseCode = "204", description = "Publicación eliminada correctamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
       }
}
