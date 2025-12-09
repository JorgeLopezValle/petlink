package com.petlink.adopcion_mascotas.dto;

import com.petlink.adopcion_mascotas.enums.Ciudad;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtectoraDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @Size(min = 2, max = 100, message = "La dirección debe tener entre 10 y 100 caracteres")
    private String direccion;

    @NotNull(message = "Debe seleccionar una ciudad")
    private Ciudad ciudad;

    private String password;

    private String confirmarPassword;

    @Pattern(regexp = "^\\+\\d{1,3}\\s\\d{6,15}$", message = "El formato del teléfono debe ser: +prefijo número (ejemplo: +34 612345678)")
    private String telefono;

    @AssertTrue(message = "Las contraseñas no coinciden")
    public boolean isPasswordsMatch() {
        if (password == null || confirmarPassword == null) {
            return true;
        }
        if (password.isEmpty() && confirmarPassword.isEmpty()) {
            return true;
        }
        return password.equals(confirmarPassword);
    }

    @AssertTrue(message = "La contraseña debe tener al menos 8 caracteres")
    public boolean isPasswordLengthValid() {
        if (password == null || password.isEmpty()) {
            return true;
        }
        return password.length() >= 8 && password.length() <= 100;
    }

    @AssertTrue(message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial")
    public boolean isPasswordFormatValid() {
        if (password == null || password.isEmpty()) {
            return true;
        }
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$");
    }
}