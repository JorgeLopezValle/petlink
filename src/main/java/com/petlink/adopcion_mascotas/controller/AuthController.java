package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.model.Usuario;
import com.petlink.adopcion_mascotas.service.IntentosLoginService;
import com.petlink.adopcion_mascotas.service.ProtectoraService;
import com.petlink.adopcion_mascotas.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProtectoraService protectoraService;

    @Autowired
    private IntentosLoginService intentosLoginService;

    @GetMapping("/login")
    public String login(ModelMap m) {
        m.put("view", "auth/login");
        return "_t/frame";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam String email, @RequestParam String password, ModelMap m,
            HttpServletRequest request) {
        String ipAddress = getClientIP(request);

        if (intentosLoginService.estaBloqueado(ipAddress)) {
            long minutesRemaining = intentosLoginService.getMinutosBloqueoRestantes(ipAddress);
            m.put("error", true);
            m.put("errorMessage","Demasiados intentos de login fallidos. Inténtalo de nuevo en " + minutesRemaining + " minutos.");
            m.put("view", "auth/login");
            return "_t/frame";
        }

        if (email == null || email.isBlank()) {
            m.put("error", true);
            m.put("errorMessage", "El email es obligatorio");
            m.put("view", "auth/login");
            return "_t/frame";
        }

        if (password == null || password.isBlank()) {
            m.put("error", true);
            m.put("errorMessage", "La contraseña es obligatoria");
            m.put("view", "auth/login");
            return "_t/frame";
        }

        try {
            Usuario usuario = usuarioService.validarLogin(email, password);

            intentosLoginService.loginExitoso(ipAddress);

            HttpSession session = request.getSession(true);
            request.changeSessionId();

            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("emailUsuario", usuario.getEmail());
            session.setAttribute("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
            session.setAttribute("rol", usuario.getRol());
            session.setAttribute("tipoEntidad", "USUARIO");

            return "redirect:/";
        } catch (Exception e) {
            try {
                Protectora protectora = protectoraService.validarLogin(email, password);

                intentosLoginService.loginExitoso(ipAddress);

                HttpSession session = request.getSession(true);
                request.changeSessionId();

                session.setAttribute("protectoraLogueada", protectora);
                session.setAttribute("emailProtectora", protectora.getEmail());
                session.setAttribute("nombreProtectora", protectora.getNombre());
                session.setAttribute("tipoEntidad", "PROTECTORA");

                return "redirect:/";
            } catch (Exception ex) {
                intentosLoginService.loginFallido(ipAddress);

                int remainingAttempts = intentosLoginService.getIntentosRestantes(ipAddress);

                m.put("error", true);
                if (remainingAttempts > 0) {
                    m.put("errorMessage", "Credenciales incorrectas. Te quedan " + remainingAttempts + " intentos.");
                } else {
                    m.put("errorMessage", "Credenciales incorrectas. Cuenta bloqueada temporalmente por seguridad.");
                }
                m.put("view", "auth/login");
                return "_t/frame";
            }
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    @GetMapping("/registro-tipo")
    public String registroTipo(ModelMap m) {
        m.put("view", "auth/registro-tipo");
        return "_t/frame";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
