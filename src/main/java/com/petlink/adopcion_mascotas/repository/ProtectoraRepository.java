package com.petlink.adopcion_mascotas.repository;

import com.petlink.adopcion_mascotas.enums.Ciudad;
import com.petlink.adopcion_mascotas.model.Protectora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProtectoraRepository extends JpaRepository<Protectora, String> {
    Optional<Protectora> findByEmail(String email);

    List<Protectora> findByAprobado(Boolean aprobado);

    List<Protectora> findByCiudad(Ciudad ciudad);

    long countByAprobado(Boolean aprobado);

    List<Protectora> findTop5ByOrderByEmailDesc();

    @Query("SELECT p FROM Protectora p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.direccion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "CAST(p.ciudad AS string) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Protectora> buscarPorMultiplesCampos(@Param("busqueda") String busqueda);
}