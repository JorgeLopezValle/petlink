package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.dto.UsuarioDTO;
import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.service.UsuarioService;
import com.petlink.adopcion_mascotas.helper.PhoneNumberHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("r")
    public String r(ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        m.put("usuarios", usuarioService.r());
        m.put("view", "usuario/r");
        return "_t/frame";
    }

    @GetMapping("c")
    public String c(ModelMap m) {
        m.put("usuarioDTO", new UsuarioDTO());
        m.put("prefijos", PhoneNumberHelper.getAllCountries());
        m.put("view", "usuario/c");
        return "_t/frame";
    }

    @PostMapping("c")
    public String cPost(@Valid UsuarioDTO usuarioDTO, BindingResult bindingResult, ModelMap m) {
        if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isEmpty()) {
            bindingResult.rejectValue("password", "error.password", "La contraseña es obligatoria");
        }

        if (bindingResult.hasErrors()) {
            m.put("usuarioDTO", usuarioDTO);
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "usuario/c");
            return "_t/frame";
        }

        try {
            usuarioService.c(usuarioDTO);
            m.put("success", true);
            m.put("successMessage", "Usuario registrado correctamente. Ya puedes iniciar sesión.");
            m.put("view", "auth/login");

        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Error al registrar el usuario. Por favor, verifica los datos e inténtalo de nuevo.");
            m.put("usuarioDTO", usuarioDTO);
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "usuario/c");
        }
        return "_t/frame";
    }

    @GetMapping("u")
    public String u(@RequestParam String email, ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        try {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            var usuario = usuarioService.findByEmail(email);

            usuarioDTO.setEmail(usuario.getEmail());
            usuarioDTO.setNombre(usuario.getNombre());
            usuarioDTO.setApellido(usuario.getApellido());
            usuarioDTO.setTelefono(usuario.getTelefono());
            usuarioDTO.setRol(usuario.getRol());

            m.put("usuarioDTO", usuarioDTO);
            m.put("emailOriginal", email);
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "usuario/u");
            return "_t/frame";
        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Usuario no encontrado");
            return "redirect:/usuario/r";
        }
    }

    @PostMapping("u")
    public String uPost(@RequestParam String emailOriginal, @Valid UsuarioDTO usuarioDTO,
                        BindingResult bindingResult, ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            m.put("usuarioDTO", usuarioDTO);
            m.put("emailOriginal", emailOriginal);
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "usuario/u");
            return "_t/frame";
        }

        try {
            usuarioService.u(emailOriginal, usuarioDTO);
            m.put("success", true);
            m.put("successMessage", "Usuario actualizado correctamente");
            m.put("usuarios", usuarioService.r());
            m.put("view", "usuario/r");
            return "_t/frame";
        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", e.getMessage());
            m.put("usuarioDTO", usuarioDTO);
            m.put("emailOriginal", emailOriginal);
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "usuario/u");
            return "_t/frame";
        }
    }

    @PostMapping("d")
    public String dPost(@RequestParam String email, ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        try {
            usuarioService.d(email);
            m.put("success", true);
            m.put("successMessage", "Usuario eliminado correctamente");
        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Error al eliminar el usuario: " + e.getMessage());
        }

        m.put("usuarios", usuarioService.r());
        m.put("view", "usuario/r");
        return "_t/frame";
    }

}