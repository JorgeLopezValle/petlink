package com.petlink.adopcion_mascotas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDTO {

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    @Size(min = 2, max = 30, message = "La especie debe tener entre 2 y 30 caracteres")
    private String especie;

    @NotBlank(message = "La raza es obligatoria")
    @Size(min = 2, max = 50, message = "La raza debe tener entre 2 y 50 caracteres")
    private String raza;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 50, message = "La edad no puede ser mayor a 50 años")
    private Integer edad;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
    @DecimalMax(value = "200", message = "El peso no puede ser mayor a 200 kg")
    private Double peso;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    private String imagenUrl;
}