package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.dto.MascotaDTO;
import com.petlink.adopcion_mascotas.enums.Ciudad;
import com.petlink.adopcion_mascotas.model.Adopcion;
import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.model.Usuario;
import com.petlink.adopcion_mascotas.repository.AdopcionRepository;
import com.petlink.adopcion_mascotas.repository.MascotaRepository;
import com.petlink.adopcion_mascotas.repository.ProtectoraRepository;
import com.petlink.adopcion_mascotas.repository.UsuarioRepository;
import com.petlink.adopcion_mascotas.util.Capitalizar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ProtectoraRepository protectoraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdopcionRepository adopcionRepository;

    public void c(MascotaDTO mascotaDTO, String emailProtectora) throws Exception {
        Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(emailProtectora);

        if (protectoraOpt.isEmpty()) {
            throw new Exception("Protectora no encontrada");
        }

        Protectora protectora = protectoraOpt.get();

        String nombre = Capitalizar.capitalizar(mascotaDTO.getNombre());
        String especie = Capitalizar.capitalizar(mascotaDTO.getEspecie());
        String raza = Capitalizar.capitalizar(mascotaDTO.getRaza());
        String descripcion = mascotaDTO.getDescripcion().trim();

        Mascota mascota = new Mascota(
                nombre,
                especie,
                raza,
                mascotaDTO.getEdad(),
                mascotaDTO.getPeso(),
                descripcion,
                protectora);

        if (mascotaDTO.getImagenUrl() != null && !mascotaDTO.getImagenUrl().isEmpty()) {
            mascota.setImagenUrl(mascotaDTO.getImagenUrl());
        }

        mascotaRepository.save(mascota);
    }

    public List<Mascota> r() {
        return mascotaRepository.findAll();
    }

    public List<Mascota> buscarPorRaza(String raza) {
        return mascotaRepository.buscarPorMultiplesCampos(raza);
    }

    public List<Protectora> buscarPorCiudad(Ciudad ciudad) {
        return protectoraRepository.findByCiudad(ciudad);
    }

    public List<Mascota> buscarPorProtectora(String emailProtectora) {
        return mascotaRepository.findByProtectoraEmail(emailProtectora);
    }

    public Optional<Mascota> buscarPorId(Long id) {
        return mascotaRepository.findById(id);
    }

    public void u(Long id, MascotaDTO mascotaDTO, String emailProtectora) throws Exception {
        Optional<Mascota> mascotaOpt = mascotaRepository.findById(id);

        if (mascotaOpt.isEmpty()) {
            throw new Exception("Mascota no encontrada");
        }

        Mascota mascota = mascotaOpt.get();

        if (!mascota.getProtectora().getEmail().equals(emailProtectora)) {
            throw new Exception("No tiene permisos para editar esta mascota");
        }

        String nombre = Capitalizar.capitalizar(mascotaDTO.getNombre());
        String especie = Capitalizar.capitalizar(mascotaDTO.getEspecie());
        String raza = Capitalizar.capitalizar(mascotaDTO.getRaza());
        String descripcion = mascotaDTO.getDescripcion().trim();

        mascota.setNombre(nombre);
        mascota.setEspecie(especie);
        mascota.setRaza(raza);
        mascota.setEdad(mascotaDTO.getEdad());
        mascota.setPeso(mascotaDTO.getPeso());
        mascota.setDescripcion(descripcion);

        if (mascotaDTO.getImagenUrl() != null && !mascotaDTO.getImagenUrl().isEmpty()) {
            mascota.setImagenUrl(mascotaDTO.getImagenUrl());
        }

        mascotaRepository.save(mascota);
    }

    @Transactional
    public void d(Long id, String emailProtectora, boolean esAdmin) throws Exception {
        Optional<Mascota> mascotaOpt = mascotaRepository.findById(id);

        if (mascotaOpt.isEmpty()) {
            throw new Exception("Mascota no encontrada");
        }

        Mascota mascota = mascotaOpt.get();

        if (!esAdmin && !mascota.getProtectora().getEmail().equals(emailProtectora)) {
            throw new Exception("No tiene permisos para eliminar esta mascota");
        }

        List<Adopcion> adopciones = adopcionRepository.findByMascotaId(id);
        if (!adopciones.isEmpty()) {
            adopcionRepository.deleteAll(adopciones);
        }

        mascotaRepository.delete(mascota);
    }

    public void adoptar(Long id, String emailUsuario) throws Exception {
        Optional<Mascota> mascotaOpt = mascotaRepository.findById(id);

        if (mascotaOpt.isEmpty()) {
            throw new Exception("Mascota no encontrada");
        }

        Mascota mascota = mascotaOpt.get();

        if (mascota.isAdoptada()) {
            throw new Exception("Esta mascota ya ha sido adoptada");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailUsuario);

        if (usuarioOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        mascota.setAdoptada(true);
        mascota.setUsuario(usuario);
        mascotaRepository.save(mascota);

        Adopcion adopcion = new Adopcion(mascota, usuario);
        adopcionRepository.save(adopcion);
    }

    public List<Mascota> buscarPorUsuario(String emailUsuario) {
        return mascotaRepository.findByUsuarioEmailAndAdoptadaTrue(emailUsuario);
    }

    public long contarTodos() {
        return mascotaRepository.count();
    }

    public long contarDisponibles() {
        return mascotaRepository.countByAdoptadaFalse();
    }

    public List<Mascota> obtenerUltimas5() {
        return mascotaRepository.findTop5ByOrderByIdDesc();
    }

    public List<Mascota> obtenerPorUsuarioEmail(String email) {
        return mascotaRepository.findByUsuarioEmailAndAdoptadaTrue(email);
    }

    public List<Mascota> obtenerPorProtectoraEmail(String email) {
        return mascotaRepository.findByProtectoraEmail(email);
    }
}