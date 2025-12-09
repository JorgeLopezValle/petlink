package com.petlink.adopcion_mascotas.repository;

import com.petlink.adopcion_mascotas.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByRazaContainingIgnoreCase(String raza);

    @Query("SELECT m FROM Mascota m WHERE " +
           "LOWER(m.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(m.raza) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(m.especie) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Mascota> buscarPorMultiplesCampos(@Param("busqueda") String busqueda);

    List<Mascota> findByProtectoraEmail(String email);
    List<Mascota> findByUsuarioEmailAndAdoptadaTrue(String email);
    long countByAdoptadaFalse();
    List<Mascota> findTop5ByOrderByIdDesc();
}