package com.petlink.adopcion_mascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private double peso;
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String imagenUrl;

    private boolean adoptada = false;

    @ManyToOne
    @JoinColumn(name = "usuario_email", referencedColumnName = "email")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "protectora_email", referencedColumnName = "email")
    private Protectora protectora;

    @OneToOne(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Adopcion adopcion;

    public Mascota(String nombre, String especie, String raza, int edad, double peso, String descripcion, Protectora protectora) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.descripcion = descripcion;
        this.protectora = protectora;
        this.adoptada = false;
        this.usuario = null;
    }
}