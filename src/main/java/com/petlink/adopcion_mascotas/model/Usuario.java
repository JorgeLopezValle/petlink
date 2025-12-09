package com.petlink.adopcion_mascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.petlink.adopcion_mascotas.enums.Rol;

@Entity
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    private String email;

    private String nombre;
    private String apellido;
    private String password;
    private String telefono;
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Mascota> mascotas;

    public Usuario(String email, String nombre, String apellido, String password, String telefono, Rol rol) {
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
        this.telefono = telefono;
        this.rol = rol;
    }
}