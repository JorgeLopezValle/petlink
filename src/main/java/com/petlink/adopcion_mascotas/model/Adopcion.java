package com.petlink.adopcion_mascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Adopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaAdopcion;

    @OneToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Adopcion(Mascota mascota, Usuario usuario) {
        this.mascota = mascota;
        this.usuario = usuario;
        this.fechaAdopcion = LocalDate.now();
    }
}