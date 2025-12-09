package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.dto.ProtectoraDTO;
import com.petlink.adopcion_mascotas.enums.Ciudad;
import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.service.ProtectoraService;
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
@RequestMapping("/protectora")
public class ProtectoraController {

    @Autowired
    private ProtectoraService protectoraService;

    @GetMapping("r")
    public String r(ModelMap m) {
        m.put("protectoras", protectoraService.r());
        m.put("ciudades", Ciudad.values());
        m.put("view", "protectora/r");
        return "_t/frame";
    }

    @GetMapping("c")
    public String c(ModelMap m) {
        m.put("protectoraDTO", new ProtectoraDTO());
        m.put("ciudades", Ciudad.values());
        m.put("prefijos", PhoneNumberHelper.getAllCountries());
        m.put("view", "protectora/c");
        return "_t/frame";
    }

    @PostMapping("c")
    public String cPost(@Valid ProtectoraDTO protectoraDTO, BindingResult bindingResult, ModelMap m) {
        if (protectoraDTO.getPassword() == null || protectoraDTO.getPassword().isEmpty()) {
            bindingResult.rejectValue("password", "error.password", "La contraseña es obligatoria");
        }

        if (bindingResult.hasErrors()) {
            m.put("protectoraDTO", protectoraDTO);
            m.put("ciudades", Ciudad.values());
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "protectora/c");
            return "_t/frame";
        }

        try {
            protectoraService.c(protectoraDTO);
            m.put("success", true);
            m.put("successMessage",
                    "Protectora registrada correctamente. Tu cuenta está pendiente de aprobación por un administrador.");
            m.put("view", "auth/login");
            return "_t/frame";
        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Error al registrar la protectora. Por favor, verifica los datos e inténtalo de nuevo.");
            m.put("protectoraDTO", protectoraDTO);
            m.put("ciudades", Ciudad.values());
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "protectora/c");
            return "_t/frame";
        }
    }

    @GetMapping("pendientes")
    public String pendientes(ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        m.put("protectoras", protectoraService.obtenerPendientesAprobacion());
        m.put("view", "protectora/pendientes");
        return "_t/frame";
    }

    @PostMapping("aprobar")
    public String aprobar(@RequestParam String email, ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        try {
            protectoraService.aprobarProtectora(email);
            m.put("protectoras", protectoraService.obtenerPendientesAprobacion());
            m.put("success", true);
            m.put("successMessage", "Protectora aprobada correctamente");
        } catch (Exception e) {
            m.put("protectoras", protectoraService.obtenerPendientesAprobacion());
            m.put("error", true);
            m.put("errorMessage", "Error al aprobar la protectora. Por favor, inténtalo de nuevo.");
        }
        m.put("view", "protectora/pendientes");
        return "_t/frame";
    }

    @PostMapping("rechazar")
    public String rechazar(@RequestParam String email, ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        try {
            protectoraService.rechazarProtectora(email);
            m.put("protectoras", protectoraService.obtenerPendientesAprobacion());
            m.put("success", true);
            m.put("successMessage", "Protectora rechazada y eliminada correctamente");
        } catch (Exception e) {
            m.put("protectoras", protectoraService.obtenerPendientesAprobacion());
            m.put("error", true);
            m.put("errorMessage", "Error al rechazar la protectora. Por favor, inténtalo de nuevo.");
        }
        m.put("view", "protectora/pendientes");
        return "_t/frame";
    }

    @GetMapping("u")
    public String u(@RequestParam String email, ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        try {
            ProtectoraDTO protectoraDTO = new ProtectoraDTO();
            var protectora = protectoraService.findByEmail(email);

            protectoraDTO.setEmail(protectora.getEmail());
            protectoraDTO.setNombre(protectora.getNombre());
            protectoraDTO.setDireccion(protectora.getDireccion());
            protectoraDTO.setCiudad(protectora.getCiudad());
            protectoraDTO.setTelefono(protectora.getTelefono());

            m.put("protectoraDTO", protectoraDTO);
            m.put("emailOriginal", email);
            m.put("ciudades", Ciudad.values());
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "protectora/u");
            return "_t/frame";
        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Protectora no encontrada");
            return "redirect:/protectora/tabla";
        }
    }

    @PostMapping("u")
    public String uPost(@RequestParam String emailOriginal, @Valid ProtectoraDTO protectoraDTO,
                        BindingResult bindingResult, ModelMap m, HttpSession session) {
        Rol rol = (Rol) session.getAttribute("rol");
        if (rol == null || rol != Rol.ADMIN) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            m.put("protectoraDTO", protectoraDTO);
            m.put("emailOriginal", emailOriginal);
            m.put("ciudades", Ciudad.values());
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "protectora/u");
            return "_t/frame";
        }

        try {
            protectoraService.u(emailOriginal, protectoraDTO);
            m.put("success", true);
            m.put("successMessage", "Protectora actualizada correctamente");
            m.put("protectoras", protectoraService.r());
            m.put("view", "protectora/tabla");
            return "_t/frame";
        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", e.getMessage());
            m.put("protectoraDTO", protectoraDTO);
            m.put("emailOriginal", emailOriginal);
            m.put("ciudades", Ciudad.values());
            m.put("prefijos", PhoneNumberHelper.getAllCountries());
            m.put("view", "protectora/u");
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
            protectoraService.d(email);
            m.put("success", true);
            m.put("successMessage", "Protectora eliminada correctamente");
        } catch (Exception e) {
            m.put("error", true);
            m.put("errorMessage", "Error al eliminar la protectora: " + e.getMessage());
        }

        m.put("protectoras", protectoraService.r());
        m.put("view", "protectora/tabla");
        return "_t/frame";
    }
}