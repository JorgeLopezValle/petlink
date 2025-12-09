package com.petlink.adopcion_mascotas.repository;

import com.petlink.adopcion_mascotas.model.Adopcion;
import com.petlink.adopcion_mascotas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdopcionRepository extends JpaRepository<Adopcion, Long> {
    List<Adopcion> findByUsuario(Usuario usuario);
    List<Adopcion> findByMascotaId(Long mascotaId);
}