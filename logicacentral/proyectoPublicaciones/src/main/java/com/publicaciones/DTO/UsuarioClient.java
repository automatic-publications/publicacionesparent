package com.publicaciones.DTO;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8081/usuarios";

    public UsuarioClient() {
        this.restTemplate = new RestTemplate();
    }

    // Obtener usuario por ID con token
    public UsuarioResponse obtenerUsuarioPorId(Long id, String token) {
        String url = baseUrl + "/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // envía JWT
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UsuarioResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UsuarioResponse.class
        );

        return response.getBody();
    }

    // Obtener usuario actual desde token
    public UsuarioResponse obtenerUsuarioActual(String token) {
        String url = baseUrl + "/actual";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UsuarioResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UsuarioResponse.class
        );

        return response.getBody();
    }
}