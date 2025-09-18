package com.userauth.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.userauth.entity.Usuarios;
import com.userauth.entity.JwtResponse;
import com.userauth.entity.LoginRequest;
import com.userauth.services.UsuarioService;
import com.userauth.configuration.JwtTokenProvider;
import com.userauth.entity.RefreshTokenRequest;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuarios> mostrar() {
        return usuarioService.mostrar();
    }

    @PostMapping
    public Usuarios agregar(@RequestBody Usuarios user) {
        return usuarioService.agregar(user);
    }

    @PostMapping("/registro")
    public Usuarios registrar(@RequestBody Usuarios user) {
        if (usuarioService.existeEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        user.setClave(passwordEncoder.encode(user.getClave()));
        return usuarioService.agregar(user);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getClave())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtTokenProvider.generateToken(authentication);

    // Buscar el usuario en BD
    Usuarios usuario = usuarioService.findByEmail(loginRequest.getEmail());

    // Construir respuesta con todos los datos
    JwtResponse response = new JwtResponse(
        jwt,
        usuario.getId(),
        usuario.getNombre(),
        usuario.getApellido(),
        usuario.getEmail(),
        usuario.getRol()
    );

    return ResponseEntity.ok(response);
}
    
 @GetMapping("/actual")
public ResponseEntity<Usuarios> obtenerUsuarioActual(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Usuarios usuario = usuarioService.findByEmail(email);

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

/*@GetMapping("/{id}")
public ResponseEntity<Usuarios> obtenerUsuarioPorId(@PathVariable Integer id) {
    Optional<Usuarios> usuario = usuarioService.buscarPorId(id);

    return usuario.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
}*/

@PostMapping("/refresh")
public ResponseEntity<?> refreshTokens(@RequestBody RefreshTokenRequest refreshTokenRequest) {
    String refreshToken = refreshTokenRequest.getRefreshToken();

    if (jwtTokenProvider.validateToken(refreshToken)) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(authentication);

        // Buscar usuario en BD con el authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuarios usuario = usuarioService.findByEmail(userDetails.getUsername());

        JwtResponse response = new JwtResponse(
            newAccessToken,
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail(),
            usuario.getRol()
        );

        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}


@GetMapping("/{id}")
    public ResponseEntity<Usuarios> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<Usuarios> usuarioOptional = usuarioService.buscarPorId(id);

        return usuarioOptional.map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

  
    

@PutMapping("/update/{id}")
public ResponseEntity<Usuarios> editarUsuario(@PathVariable Integer id, @RequestBody Usuarios user) {
    Optional<Usuarios> usuarioOptional = usuarioService.buscarPorId(id);

    if (usuarioOptional.isPresent()) {
        Usuarios usuarioExistente = usuarioOptional.get();

        // Actualizar los campos del estudiante existente con los valores proporcionados
        usuarioExistente.setNombre(user.getNombre());
        usuarioExistente.setApellido(user.getApellido());
        usuarioExistente.setEmail(user.getEmail());

        // Guardar el estudiante actualizado usando el método del servicio
        Usuarios estudianteActualizado = usuarioService.actualizar(usuarioExistente);
        return ResponseEntity.ok(estudianteActualizado);
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

    @DeleteMapping("/delete/{id}")
public ResponseEntity<Void> eliminarEstudiante(@PathVariable Integer id) {
    Optional<Usuarios> usuarioOptional = usuarioService.buscarPorId(id);

    if (usuarioOptional.isPresent()) {
        Usuarios usuarioExistente = usuarioOptional.get();

        // Eliminar el estudiante usando el método del servicio
        usuarioService.eliminar(usuarioExistente);
        return ResponseEntity.noContent().build();
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}


}
