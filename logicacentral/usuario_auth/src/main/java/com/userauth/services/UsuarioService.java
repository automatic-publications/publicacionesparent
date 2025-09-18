
package com.userauth.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.userauth.entity.Usuarios;
import com.userauth.repository.UsuariosRepo;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuariosRepo usuarioRepo;

    public Usuarios agregar(Usuarios user){
        return usuarioRepo.save(user);
    }
    
    public Usuarios actualizar(Usuarios user){
        return usuarioRepo.save(user);
    }
    
    public List<Usuarios> mostrar(){
        return usuarioRepo.findAll();
    }
    
    public void eliminar(Usuarios user){
        usuarioRepo.delete(user);
    }
    public boolean existeEmail(String email) {
        return usuarioRepo.existsByEmail(email);
    }
    
public Usuarios findByEmail(String email) {
    return usuarioRepo.findByEmail(email);
}

public Optional<Usuarios> buscarPorId(Integer id) {
        return usuarioRepo.findById(id);
    }
    
}
