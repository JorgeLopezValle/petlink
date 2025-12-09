package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.dto.UsuarioDTO;
import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.model.Adopcion;
import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.model.Usuario;
import com.petlink.adopcion_mascotas.repository.AdopcionRepository;
import com.petlink.adopcion_mascotas.repository.MascotaRepository;
import com.petlink.adopcion_mascotas.repository.UsuarioRepository;
import com.petlink.adopcion_mascotas.util.Capitalizar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private AdopcionRepository adopcionRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void c(UsuarioDTO usuarioDTO) {
        String email = usuarioDTO.getEmail();
        String nombre = Capitalizar.capitalizar(usuarioDTO.getNombre());
        String apellido = Capitalizar.capitalizar(usuarioDTO.getApellido());
        String password = passwordEncoder.encode(usuarioDTO.getPassword());
        String telefono = usuarioDTO.getTelefono();
        Rol rol = usuarioDTO.getRol() != null ? usuarioDTO.getRol() : Rol.USUARIO;

        Usuario usuario = new Usuario(email, nombre, apellido, password, telefono, rol);
        usuarioRepository.save(usuario);
    }

    public List<Usuario> r() {
        return usuarioRepository.findAll();
    }

    public Usuario validarLogin(String email, String password) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        return usuario;
    }

    public void d(String email) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        List<Adopcion> adopcionesUsuario = adopcionRepository.findByUsuario(usuario);
        adopcionRepository.deleteAll(adopcionesUsuario);

        List<Mascota> mascotasAdoptadas = mascotaRepository.findByUsuarioEmailAndAdoptadaTrue(email);
        mascotaRepository.deleteAll(mascotasAdoptadas);

        usuarioRepository.delete(usuario);
    }

    public Usuario findByEmail(String email) throws Exception {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
    }

    public void u(String emailOriginal, UsuarioDTO usuarioDTO) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailOriginal);

        if (usuarioOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        String passwordActual = usuario.getPassword();

        if (!emailOriginal.equals(usuarioDTO.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
                throw new Exception("El email ya está en uso");
            }

            List<Mascota> mascotasAdoptadas = mascotaRepository.findByUsuarioEmailAndAdoptadaTrue(emailOriginal);
            List<Adopcion> adopcionesUsuario = adopcionRepository.findByUsuario(usuario);

            usuario = new Usuario();
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setPassword(passwordActual);
            usuario.setNombre(Capitalizar.capitalizar(usuarioDTO.getNombre()));
            usuario.setApellido(Capitalizar.capitalizar(usuarioDTO.getApellido()));
            usuario.setTelefono(usuarioDTO.getTelefono());
            usuario.setRol(usuarioDTO.getRol() != null ? usuarioDTO.getRol() : Rol.USUARIO);

            usuarioRepository.save(usuario);

            for (Mascota mascota : mascotasAdoptadas) {
                mascota.setUsuario(usuario);
                mascotaRepository.save(mascota);
            }

            for (Adopcion adopcion : adopcionesUsuario) {
                adopcion.setUsuario(usuario);
                adopcionRepository.save(adopcion);
            }

            usuarioRepository.deleteById(emailOriginal);

            if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
                usuarioRepository.save(usuario);
            }

            return;
        }

        usuario.setNombre(Capitalizar.capitalizar(usuarioDTO.getNombre()));
        usuario.setApellido(Capitalizar.capitalizar(usuarioDTO.getApellido()));
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setRol(usuarioDTO.getRol() != null ? usuarioDTO.getRol() : Rol.USUARIO);

        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        usuarioRepository.save(usuario);
    }

    public long contarTodos() {
        return usuarioRepository.count();
    }

    public List<Usuario> obtenerUltimos5() {
        return usuarioRepository.findTop5ByOrderByEmailDesc();
    }
}