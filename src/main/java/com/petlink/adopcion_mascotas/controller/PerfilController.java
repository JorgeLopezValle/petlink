package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.enums.Ciudad;
import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.model.Usuario;
import com.petlink.adopcion_mascotas.service.ProtectoraService;
import com.petlink.adopcion_mascotas.service.UsuarioService;
import com.petlink.adopcion_mascotas.helper.PhoneNumberHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProtectoraService protectoraService;

    @GetMapping
    public String perfil(HttpSession session, ModelMap m) {
        String tipoEntidad = (String) session.getAttribute("tipoEntidad");

        if (tipoEntidad == null) {
            return "redirect:/auth/login";
        }

        if ("USUARIO".equals(tipoEntidad)) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            m.put("usuario", usuario);
            m.put("tipoEntidad", "USUARIO");
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
        } else if ("PROTECTORA".equals(tipoEntidad)) {
            Protectora protectora = (Protectora) session.getAttribute("protectoraLogueada");
            m.put("protectora", protectora);
            m.put("tipoEntidad", "PROTECTORA");
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
        }

        m.put("view", "perfil/perfil");
        return "_t/frame";
    }

    @PostMapping("/actualizar-usuario")
    public String actualizarUsuario(
            @RequestParam String emailOriginal,
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmarPassword,
            HttpSession session,
            ModelMap m) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            Usuario usuarioDB = usuarioService.findByEmail(emailOriginal);

            if (nombre == null || nombre.isBlank()) {
                m.put("error", true);
                m.put("errorMessage", "El nombre es obligatorio");
                m.put("usuario", usuarioDB);
                m.put("tipoEntidad", "USUARIO");
                m.put("prefijos", PhoneNumberHelper.getAllCountries());
                m.put("view", "perfil/perfil");
                return "_t/frame";
            }

            if (apellido == null || apellido.isBlank()) {
                m.put("error", true);
                m.put("errorMessage", "El apellido es obligatorio");
                m.put("usuario", usuarioDB);
                m.put("tipoEntidad", "USUARIO");
                m.put("prefijos", PhoneNumberHelper.getAllCountries());
                m.put("view", "perfil/perfil");
                return "_t/frame";
            }

            if (telefono == null || telefono.isBlank()) {
                m.put("error", true);
                m.put("errorMessage", "El teléfono es obligatorio");
                m.put("usuario", usuarioDB);
                m.put("tipoEntidad", "USUARIO");
                m.put("prefijos", PhoneNumberHelper.getAllCountries());
                m.put("view", "perfil/perfil");
                return "_t/frame";
            }

            if (password != null && !password.isEmpty()) {
                if (!password.equals(confirmarPassword)) {
                    m.put("error", true);
                    m.put("errorMessage", "Las contraseñas no coinciden");
                    m.put("usuario", usuarioDB);
                    m.put("tipoEntidad", "USUARIO");
                    m.put("prefijos", PhoneNumberHelper.getAllCountries());
                    m.put("view", "perfil/perfil");
                    return "_t/frame";
                }
            }

            usuarioDB.setEmail(email);
            usuarioDB.setNombre(nombre);
            usuarioDB.setApellido(apellido);
            usuarioDB.setTelefono(telefono);

            usuarioService.u(emailOriginal, convertirUsuarioADTO(usuarioDB, password != null && !password.isEmpty() ? password : null));

            Usuario usuarioActualizado = usuarioService.findByEmail(email);
            session.setAttribute("usuarioLogueado", usuarioActualizado);
            session.setAttribute("nombreCompleto", usuarioActualizado.getNombre() + " " + usuarioActualizado.getApellido());
            session.setAttribute("emailUsuario", usuarioActualizado.getEmail());

            m.put("success", true);
            m.put("successMessage", "Perfil actualizado correctamente");
            m.put("usuario", usuarioActualizado);
            m.put("tipoEntidad", "USUARIO");
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "perfil/perfil");
            return "_t/frame";

        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Error al actualizar el perfil: " + e.getMessage());
            try {
                Usuario usuarioDB = usuarioService.findByEmail(emailOriginal);
                m.put("usuario", usuarioDB);
            } catch (Exception ex) {
                m.put("usuario", usuario);
            }
            m.put("tipoEntidad", "USUARIO");
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "perfil/perfil");
            return "_t/frame";
        }
    }

    @PostMapping("/actualizar-protectora")
    public String actualizarProtectora(
            @RequestParam String emailOriginal,
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam String direccion,
            @RequestParam Ciudad ciudad,
            @RequestParam String telefono,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmarPassword,
            HttpSession session,
            ModelMap m) {

        Protectora protectora = (Protectora) session.getAttribute("protectoraLogueada");

        if (protectora == null) {
            return "redirect:/auth/login";
        }

        try {
            Protectora protectoraDB = protectoraService.findByEmail(emailOriginal);

            if (nombre == null || nombre.isBlank()) {
                m.put("error", true);
                m.put("errorMessage", "El nombre es obligatorio");
                m.put("protectora", protectoraDB);
                m.put("tipoEntidad", "PROTECTORA");
                m.put("prefijos", PhoneNumberHelper.getAllCountries());
                m.put("view", "perfil/perfil");
                return "_t/frame";
            }

            if (direccion == null || direccion.isBlank()) {
                m.put("error", true);
                m.put("errorMessage", "La dirección es obligatoria");
                m.put("protectora", protectoraDB);
                m.put("tipoEntidad", "PROTECTORA");
                m.put("prefijos", PhoneNumberHelper.getAllCountries());
                m.put("view", "perfil/perfil");
                return "_t/frame";
            }

            if (telefono == null || telefono.isBlank()) {
                m.put("error", true);
                m.put("errorMessage", "El teléfono es obligatorio");
                m.put("protectora", protectoraDB);
                m.put("tipoEntidad", "PROTECTORA");
                m.put("prefijos", PhoneNumberHelper.getAllCountries());
                m.put("view", "perfil/perfil");
                return "_t/frame";
            }

            if (password != null && !password.isEmpty()) {
                if (!password.equals(confirmarPassword)) {
                    m.put("error", true);
                    m.put("errorMessage", "Las contraseñas no coinciden");
                    m.put("protectora", protectoraDB);
                    m.put("tipoEntidad", "PROTECTORA");
                    m.put("prefijos", PhoneNumberHelper.getAllCountries());
                    m.put("view", "perfil/perfil");
                    return "_t/frame";
                }
            }

            protectoraDB.setEmail(email);
            protectoraDB.setNombre(nombre);
            protectoraDB.setDireccion(direccion);
            protectoraDB.setCiudad(ciudad);
            protectoraDB.setTelefono(telefono);

            protectoraService.u(emailOriginal, convertirProtectoraADTO(protectoraDB, password != null && !password.isEmpty() ? password : null));

            Protectora protectoraActualizada = protectoraService.findByEmail(email);
            session.setAttribute("protectoraLogueada", protectoraActualizada);
            session.setAttribute("nombreProtectora", protectoraActualizada.getNombre());
            session.setAttribute("emailProtectora", protectoraActualizada.getEmail());

            m.put("success", true);
            m.put("successMessage", "Perfil actualizado correctamente");
            m.put("protectora", protectoraActualizada);
            m.put("tipoEntidad", "PROTECTORA");
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "perfil/perfil");
            return "_t/frame";

        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Error al actualizar el perfil: " + e.getMessage());
            try {
                Protectora protectoraDB = protectoraService.findByEmail(emailOriginal);
                m.put("protectora", protectoraDB);
            } catch (Exception ex) {
                m.put("protectora", protectora);
            }
            m.put("tipoEntidad", "PROTECTORA");
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "perfil/perfil");
            return "_t/frame";
        }
    }

    private com.petlink.adopcion_mascotas.dto.UsuarioDTO convertirUsuarioADTO(Usuario usuario, String password) {
        com.petlink.adopcion_mascotas.dto.UsuarioDTO dto = new com.petlink.adopcion_mascotas.dto.UsuarioDTO();
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setTelefono(usuario.getTelefono());
        dto.setRol(usuario.getRol());
        if (password != null && !password.isEmpty()) {
            dto.setPassword(password);
        }
        return dto;
    }

    private com.petlink.adopcion_mascotas.dto.ProtectoraDTO convertirProtectoraADTO(Protectora protectora, String password) {
        com.petlink.adopcion_mascotas.dto.ProtectoraDTO dto = new com.petlink.adopcion_mascotas.dto.ProtectoraDTO();
        dto.setEmail(protectora.getEmail());
        dto.setNombre(protectora.getNombre());
        dto.setDireccion(protectora.getDireccion());
        dto.setCiudad(protectora.getCiudad());
        dto.setTelefono(protectora.getTelefono());
        if (password != null && !password.isEmpty()) {
            dto.setPassword(password);
        }
        return dto;
    }
}
