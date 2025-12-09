package com.petlink.adopcion_mascotas.model;

import com.petlink.adopcion_mascotas.enums.Ciudad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Protectora {

    @Id
    private String email;

    private String nombre;
    private String direccion;

    @Enumerated(EnumType.STRING)
    private Ciudad ciudad;

    private String password;
    private String telefono;

    @Column(nullable = false)
    private Boolean aprobado = false;

    @OneToMany(mappedBy = "protectora", cascade = CascadeType.ALL)
    private List<Mascota> mascotas = new ArrayList<>();

    public Protectora(String email, String nombre, String direccion, Ciudad ciudad, String password, String telefono) {
        this.email = email;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.password = password;
        this.telefono = telefono;
        this.aprobado = false;
        this.mascotas = new ArrayList<>();
    }
}