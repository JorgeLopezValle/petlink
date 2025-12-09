package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.model.Usuario;
import com.petlink.adopcion_mascotas.service.MascotaService;
import com.petlink.adopcion_mascotas.service.ProtectoraService;
import com.petlink.adopcion_mascotas.service.UsuarioService;
import com.petlink.adopcion_mascotas.service.AdopcionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProtectoraService protectoraService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private AdopcionService adopcionService;

    @GetMapping
    public String dashboard(HttpSession session, ModelMap m) {
        String tipoEntidad = (String) session.getAttribute("tipoEntidad");

        if (tipoEntidad == null) {
            return "redirect:/auth/login";
        }

        if ("USUARIO".equals(tipoEntidad)) {
            Rol rol = (Rol) session.getAttribute("rol");

            if (rol == Rol.ADMIN) {
                return dashboardAdmin(m);
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    private String dashboardAdmin(ModelMap m) {
        long totalUsuarios = usuarioService.contarTodos();
        long totalProtectoras = protectoraService.contarTodos();
        long totalMascotas = mascotaService.contarTodos();
        long totalAdopciones = adopcionService.contarTodos();
        long totalMascotasDisponibles = mascotaService.contarDisponibles();
        long totalProtectorasPendientes = protectoraService.contarPendientes();

        List<Usuario> ultimosUsuarios = usuarioService.obtenerUltimos5();
        List<Protectora> ultimasProtectoras = protectoraService.obtenerUltimas5();
        List<Mascota> ultimasMascotas = mascotaService.obtenerUltimas5();

        m.put("totalUsuarios", totalUsuarios);
        m.put("totalProtectoras", totalProtectoras);
        m.put("totalMascotas", totalMascotas);
        m.put("totalAdopciones", totalAdopciones);
        m.put("totalMascotasDisponibles", totalMascotasDisponibles);
        m.put("totalProtectorasPendientes", totalProtectorasPendientes);
        m.put("ultimosUsuarios", ultimosUsuarios);
        m.put("ultimasProtectoras", ultimasProtectoras);
        m.put("ultimasMascotas", ultimasMascotas);
        m.put("view", "dashboard/dashboard");

        return "_t/frame";
    }
}
