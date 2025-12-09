package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.dto.UsuarioDTO;
import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.model.Adopcion;
import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.model.Usuario;
import com.petlink.adopcion_mascotas.repository.AdopcionRepository;
import com.petlink.adopcion_mascotas.repository.MascotaRepository;
import com.petlink.adopcion_mascotas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private AdopcionRepository adopcionRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setEmail("test@test.com");
        usuarioDTO.setNombre("jorge");
        usuarioDTO.setApellido("lopez");
        usuarioDTO.setPassword("password123");
        usuarioDTO.setTelefono("+34600000000");
        usuarioDTO.setRol(Rol.USUARIO);

        usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setNombre("Jorge");
        usuario.setApellido("Lopez");
        usuario.setPassword(passwordEncoder.encode("password123"));
        usuario.setTelefono("+34600000000");
        usuario.setRol(Rol.USUARIO);
    }

    @Test
    @DisplayName("Debe crear usuario correctamente")
    void debeCrearUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.c(usuarioDTO);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe capitalizar nombre y apellido al crear")
    void debeCapitalizarNombreApellido() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            assertThat(u.getNombre()).isEqualTo("Jorge");
            assertThat(u.getApellido()).isEqualTo("Lopez");
            return u;
        });

        usuarioService.c(usuarioDTO);

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe encriptar contraseña al crear usuario")
    void debeEncriptarPassword() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            assertThat(u.getPassword()).isNotEqualTo("password123");
            assertThat(u.getPassword()).startsWith("$2a$");
            return u;
        });

        usuarioService.c(usuarioDTO);

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe asignar rol USUARIO por defecto si no se especifica")
    void debeAsignarRolPorDefecto() {
        usuarioDTO.setRol(null);

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            assertThat(u.getRol()).isEqualTo(Rol.USUARIO);
            return u;
        });

        usuarioService.c(usuarioDTO);

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void debeListarTodosUsuarios() {
        Usuario usuario2 = new Usuario();
        usuario2.setEmail("otro@test.com");
        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.r();

        assertThat(resultado).hasSize(2);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe validar login con credenciales correctas")
    void debeValidarLoginCorrecto() throws Exception {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.validarLogin("test@test.com", "password123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe en login")
    void debeLanzarExcepcionUsuarioNoExiste() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.validarLogin("noexiste@test.com", "password"))
                .isInstanceOf(Exception.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción si contraseña es incorrecta")
    void debeLanzarExcepcionPasswordIncorrecta() {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.validarLogin("test@test.com", "passwordIncorrecta"))
                .isInstanceOf(Exception.class)
                .hasMessage("Contraseña incorrecta");
    }

    @Test
    @DisplayName("Debe eliminar usuario con sus relaciones")
    void debeEliminarUsuarioConRelaciones() throws Exception {
        List<Adopcion> adopciones = Arrays.asList(new Adopcion(), new Adopcion());
        List<Mascota> mascotas = Arrays.asList(new Mascota(), new Mascota());

        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        when(adopcionRepository.findByUsuario(usuario)).thenReturn(adopciones);
        when(mascotaRepository.findByUsuarioEmailAndAdoptadaTrue("test@test.com")).thenReturn(mascotas);

        usuarioService.d("test@test.com");

        verify(adopcionRepository).deleteAll(adopciones);
        verify(mascotaRepository).deleteAll(mascotas);
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar usuario que no existe")
    void debeLanzarExcepcionAlEliminarUsuarioNoExiste() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.d("noexiste@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    @DisplayName("Debe encontrar usuario por email")
    void debeEncontrarUsuarioPorEmail() throws Exception {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.findByEmail("test@test.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Debe contar todos los usuarios")
    void debeContarTodosUsuarios() {
        when(usuarioRepository.count()).thenReturn(10L);

        long resultado = usuarioService.contarTodos();

        assertThat(resultado).isEqualTo(10L);
    }

    @Test
    @DisplayName("Debe actualizar usuario sin cambiar email")
    void debeActualizarUsuarioSinCambiarEmail() throws Exception {
        usuarioDTO.setNombre("nuevonombre");

        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.u("test@test.com", usuarioDTO);

        verify(usuarioRepository).save(any(Usuario.class));
        verify(usuarioRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Debe actualizar usuario cambiando email")
    void debeActualizarUsuarioCambiandoEmail() throws Exception {
        usuarioDTO.setEmail("nuevoemail@test.com");

        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("nuevoemail@test.com")).thenReturn(Optional.empty());
        when(mascotaRepository.findByUsuarioEmailAndAdoptadaTrue("test@test.com")).thenReturn(Arrays.asList());
        when(adopcionRepository.findByUsuario(any())).thenReturn(Arrays.asList());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.u("test@test.com", usuarioDTO);

        verify(usuarioRepository, atLeastOnce()).save(any(Usuario.class));
        verify(usuarioRepository).deleteById("test@test.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción si nuevo email ya existe")
    void debeLanzarExcepcionEmailYaExiste() {
        usuarioDTO.setEmail("nuevoemail@test.com");

        Usuario otroUsuario = new Usuario();
        otroUsuario.setEmail("nuevoemail@test.com");

        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("nuevoemail@test.com")).thenReturn(Optional.of(otroUsuario));

        assertThatThrownBy(() -> usuarioService.u("test@test.com", usuarioDTO))
                .isInstanceOf(Exception.class)
                .hasMessage("El email ya está en uso");
    }
}
