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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para MascotaService")
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private ProtectoraRepository protectoraRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdopcionRepository adopcionRepository;

    @InjectMocks
    private MascotaService mascotaService;

    private MascotaDTO mascotaDTO;
    private Mascota mascota;
    private Protectora protectora;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        protectora = new Protectora();
        protectora.setEmail("protectora@test.com");
        protectora.setNombre("Protectora Test");
        protectora.setCiudad(Ciudad.MADRID);

        usuario = new Usuario();
        usuario.setEmail("usuario@test.com");
        usuario.setNombre("Usuario");

        mascotaDTO = new MascotaDTO();
        mascotaDTO.setNombre("max");
        mascotaDTO.setEspecie("perro");
        mascotaDTO.setRaza("labrador");
        mascotaDTO.setEdad(3);
        mascotaDTO.setPeso(25.5);
        mascotaDTO.setDescripcion("  Perro muy juguetón  ");
        mascotaDTO.setImagenUrl("http://example.com/image.jpg");

        mascota = new Mascota();
        mascota.setId(1L);
        mascota.setNombre("Max");
        mascota.setEspecie("Perro");
        mascota.setRaza("Labrador");
        mascota.setEdad(3);
        mascota.setPeso(25.5);
        mascota.setDescripcion("Perro muy juguetón");
        mascota.setProtectora(protectora);
        mascota.setAdoptada(false);
    }

    @Test
    @DisplayName("Debe crear mascota correctamente")
    void debeCrearMascota() throws Exception {
        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        mascotaService.c(mascotaDTO, "protectora@test.com");

        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si protectora no existe")
    void debeLanzarExcepcionProtectoraNoExiste() {
        when(protectoraRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.c(mascotaDTO, "noexiste@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("Protectora no encontrada");
    }

    @Test
    @DisplayName("Debe capitalizar nombre, especie y raza")
    void debeCapitalizarCampos() throws Exception {
        when(protectoraRepository.findByEmail(anyString())).thenReturn(Optional.of(protectora));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota m = invocation.getArgument(0);
            assertThat(m.getNombre()).isEqualTo("Max");
            assertThat(m.getEspecie()).isEqualTo("Perro");
            assertThat(m.getRaza()).isEqualTo("Labrador");
            return m;
        });

        mascotaService.c(mascotaDTO, "protectora@test.com");

        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debe hacer trim de la descripción")
    void debeHacerTrimDescripcion() throws Exception {
        when(protectoraRepository.findByEmail(anyString())).thenReturn(Optional.of(protectora));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota m = invocation.getArgument(0);
            assertThat(m.getDescripcion()).isEqualTo("Perro muy juguetón");
            return m;
        });

        mascotaService.c(mascotaDTO, "protectora@test.com");

        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debe listar todas las mascotas")
    void debeListarTodasMascotas() {
        Mascota mascota2 = new Mascota();
        mascota2.setId(2L);
        List<Mascota> mascotas = Arrays.asList(mascota, mascota2);

        when(mascotaRepository.findAll()).thenReturn(mascotas);

        List<Mascota> resultado = mascotaService.r();

        assertThat(resultado).hasSize(2);
        verify(mascotaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar por id")
    void debeBuscarPorId() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        Optional<Mascota> resultado = mascotaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe actualizar mascota")
    void debeActualizarMascota() throws Exception {
        mascotaDTO.setNombre("nuevo nombre");

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        mascotaService.u(1L, mascotaDTO, "protectora@test.com");

        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si mascota no existe al actualizar")
    void debeLanzarExcepcionMascotaNoExisteActualizar() {
        when(mascotaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.u(1L, mascotaDTO, "protectora@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("Mascota no encontrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción si no tiene permisos para editar")
    void debeLanzarExcepcionSinPermisosEditar() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        assertThatThrownBy(() -> mascotaService.u(1L, mascotaDTO, "otra@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("No tiene permisos para editar esta mascota");
    }

    @Test
    @DisplayName("Debe eliminar mascota como admin")
    void debeEliminarMascotaAdmin() throws Exception {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(adopcionRepository.findByMascotaId(1L)).thenReturn(Arrays.asList());

        mascotaService.d(1L, "cualquier@test.com", true);

        verify(mascotaRepository).delete(mascota);
    }

    @Test
    @DisplayName("Debe eliminar mascota como propietario")
    void debeEliminarMascotaPropietario() throws Exception {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(adopcionRepository.findByMascotaId(1L)).thenReturn(Arrays.asList());

        mascotaService.d(1L, "protectora@test.com", false);

        verify(mascotaRepository).delete(mascota);
    }

    @Test
    @DisplayName("Debe eliminar adopciones antes de eliminar mascota")
    void debeEliminarAdopcionesAntes() throws Exception {
        List<Adopcion> adopciones = Arrays.asList(new Adopcion(), new Adopcion());

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(adopcionRepository.findByMascotaId(1L)).thenReturn(adopciones);

        mascotaService.d(1L, "protectora@test.com", false);

        verify(adopcionRepository).deleteAll(adopciones);
        verify(mascotaRepository).delete(mascota);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no tiene permisos para eliminar")
    void debeLanzarExcepcionSinPermisosEliminar() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        assertThatThrownBy(() -> mascotaService.d(1L, "otra@test.com", false))
                .isInstanceOf(Exception.class)
                .hasMessage("No tiene permisos para eliminar esta mascota");
    }

    @Test
    @DisplayName("Debe adoptar mascota correctamente")
    void debeAdoptarMascota() throws Exception {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(usuarioRepository.findByEmail("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);
        when(adopcionRepository.save(any(Adopcion.class))).thenReturn(new Adopcion());

        mascotaService.adoptar(1L, "usuario@test.com");

        verify(mascotaRepository).save(any(Mascota.class));
        verify(adopcionRepository).save(any(Adopcion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si mascota ya está adoptada")
    void debeLanzarExcepcionMascotaYaAdoptada() {
        mascota.setAdoptada(true);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        assertThatThrownBy(() -> mascotaService.adoptar(1L, "usuario@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("Esta mascota ya ha sido adoptada");
    }

    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe al adoptar")
    void debeLanzarExcepcionUsuarioNoExisteAdoptar() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.adoptar(1L, "noexiste@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    @DisplayName("Debe buscar mascotas por protectora")
    void debeBuscarPorProtectora() {
        List<Mascota> mascotas = Arrays.asList(mascota);

        when(mascotaRepository.findByProtectoraEmail("protectora@test.com")).thenReturn(mascotas);

        List<Mascota> resultado = mascotaService.buscarPorProtectora("protectora@test.com");

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Debe buscar mascotas por usuario")
    void debeBuscarPorUsuario() {
        mascota.setUsuario(usuario);
        mascota.setAdoptada(true);
        List<Mascota> mascotas = Arrays.asList(mascota);

        when(mascotaRepository.findByUsuarioEmailAndAdoptadaTrue("usuario@test.com")).thenReturn(mascotas);

        List<Mascota> resultado = mascotaService.buscarPorUsuario("usuario@test.com");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).isAdoptada()).isTrue();
    }

    @Test
    @DisplayName("Debe contar todas las mascotas")
    void debeContarTodasMascotas() {
        when(mascotaRepository.count()).thenReturn(50L);

        long resultado = mascotaService.contarTodos();

        assertThat(resultado).isEqualTo(50L);
    }

    @Test
    @DisplayName("Debe contar mascotas disponibles")
    void debeContarMascotasDisponibles() {
        when(mascotaRepository.countByAdoptadaFalse()).thenReturn(30L);

        long resultado = mascotaService.contarDisponibles();

        assertThat(resultado).isEqualTo(30L);
    }

    @Test
    @DisplayName("Debe obtener últimas 5 mascotas")
    void debeObtenerUltimas5() {
        List<Mascota> mascotas = Arrays.asList(mascota, new Mascota(), new Mascota());

        when(mascotaRepository.findTop5ByOrderByIdDesc()).thenReturn(mascotas);

        List<Mascota> resultado = mascotaService.obtenerUltimas5();

        assertThat(resultado).hasSize(3);
        verify(mascotaRepository).findTop5ByOrderByIdDesc();
    }
}
