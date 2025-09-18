
package com.userauth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.userauth.entity.Usuarios;

@Repository
public interface UsuariosRepo extends JpaRepository<Usuarios, Integer> {
    Usuarios findByEmail(String email);

    public boolean existsByEmail(String email);

    public Optional<Usuarios> findById(Integer id);
}
