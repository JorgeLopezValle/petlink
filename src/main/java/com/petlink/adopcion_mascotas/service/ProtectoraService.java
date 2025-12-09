package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.dto.ProtectoraDTO;
import com.petlink.adopcion_mascotas.enums.Ciudad;
import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.repository.MascotaRepository;
import com.petlink.adopcion_mascotas.repository.ProtectoraRepository;
import com.petlink.adopcion_mascotas.util.Capitalizar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProtectoraService {

    @Autowired
    private ProtectoraRepository protectoraRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void c(ProtectoraDTO protectoraDTO) {
        String email = protectoraDTO.getEmail();
        String nombre = Capitalizar.capitalizar(protectoraDTO.getNombre());
        String direccion = protectoraDTO.getDireccion();
        Ciudad ciudad = protectoraDTO.getCiudad();
        String password = passwordEncoder.encode(protectoraDTO.getPassword());
        String telefono = protectoraDTO.getTelefono();

        Protectora protectora = new Protectora(email, nombre, direccion, ciudad, password, telefono);
        protectora.setAprobado(false);
        protectoraRepository.save(protectora);
    }

    public List<Protectora> r() {
        return protectoraRepository.findAll();
    }

    public Protectora validarLogin(String email, String password) throws Exception {
        Protectora protectora = protectoraRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Protectora no encontrada"));

        if (!passwordEncoder.matches(password, protectora.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        if (!protectora.getAprobado()) {
            throw new Exception("Su cuenta está pendiente de aprobación por un administrador");
        }

        return protectora;
    }

    public List<Protectora> obtenerPendientesAprobacion() {
        return protectoraRepository.findByAprobado(false);
    }

    public void aprobarProtectora(String email) throws Exception {
        Protectora protectora = protectoraRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Protectora no encontrada"));

        protectora.setAprobado(true);
        protectoraRepository.save(protectora);
    }

    public void rechazarProtectora(String email) throws Exception {
        Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(email);

        if (protectoraOpt.isEmpty()) {
            throw new Exception("Protectora no encontrada");
        }

        protectoraRepository.delete(protectoraOpt.get());
    }

    public void d(String email) throws Exception {
        Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(email);

        if (protectoraOpt.isEmpty()) {
            throw new Exception("Protectora no encontrada");
        }

        protectoraRepository.delete(protectoraOpt.get());
    }

    public Protectora findByEmail(String email) throws Exception {
        return protectoraRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Protectora no encontrada"));
    }

    public void u(String emailOriginal, ProtectoraDTO protectoraDTO) throws Exception {
        Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(emailOriginal);

        if (protectoraOpt.isEmpty()) {
            throw new Exception("Protectora no encontrada");
        }

        Protectora protectora = protectoraOpt.get();
        String passwordActual = protectora.getPassword();
        Boolean aprobadoActual = protectora.getAprobado();

        if (!emailOriginal.equals(protectoraDTO.getEmail())) {
            if (protectoraRepository.findByEmail(protectoraDTO.getEmail()).isPresent()) {
                throw new Exception("El email ya está en uso");
            }

            List<Mascota> mascotas = mascotaRepository.findByProtectoraEmail(emailOriginal);

            protectora = new Protectora();
            protectora.setEmail(protectoraDTO.getEmail());
            protectora.setPassword(passwordActual);
            protectora.setAprobado(aprobadoActual);
            protectora.setNombre(Capitalizar.capitalizar(protectoraDTO.getNombre()));
            protectora.setDireccion(protectoraDTO.getDireccion());
            protectora.setCiudad(protectoraDTO.getCiudad());
            protectora.setTelefono(protectoraDTO.getTelefono());

            protectoraRepository.save(protectora);

            for (Mascota mascota : mascotas) {
                mascota.setProtectora(protectora);
                mascotaRepository.save(mascota);
            }

            protectoraRepository.deleteById(emailOriginal);

            if (protectoraDTO.getPassword() != null && !protectoraDTO.getPassword().isEmpty()) {
                protectora.setPassword(passwordEncoder.encode(protectoraDTO.getPassword()));
                protectoraRepository.save(protectora);
            }

            return;
        }

        protectora.setNombre(Capitalizar.capitalizar(protectoraDTO.getNombre()));
        protectora.setDireccion(protectoraDTO.getDireccion());
        protectora.setCiudad(protectoraDTO.getCiudad());
        protectora.setTelefono(protectoraDTO.getTelefono());

        if (protectoraDTO.getPassword() != null && !protectoraDTO.getPassword().isEmpty()) {
            protectora.setPassword(passwordEncoder.encode(protectoraDTO.getPassword()));
        }

        protectoraRepository.save(protectora);
    }

    public long contarTodos() {
        return protectoraRepository.count();
    }

    public long contarPendientes() {
        return protectoraRepository.countByAprobado(false);
    }

    public List<Protectora> obtenerUltimas5() {
        return protectoraRepository.findTop5ByOrderByEmailDesc();
    }
}