/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.userauth.services;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.userauth.entity.Usuarios;
import com.userauth.repository.UsuariosRepo;

/**
 *
 * @author Bossa
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuariosRepo usuariosRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuarios estudiante = usuariosRepo.findByEmail(email);
        if (estudiante == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
        }
        return new User(estudiante.getEmail(), estudiante.getClave(), new ArrayList<>());
    }
}

