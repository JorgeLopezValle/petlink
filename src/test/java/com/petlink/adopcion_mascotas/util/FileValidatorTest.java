package com.petlink.adopcion_mascotas.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para FileValidator")
class FileValidatorTest {

    @Test
    @DisplayName("Debe validar imagen PNG por magic bytes")
    void debeValidarImagenPNG() throws IOException {
        byte[] pngSignature = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x00};
        InputStream is = new ByteArrayInputStream(pngSignature);

        assertThat(FileValidator.isValidImage(is)).isTrue();
    }

    @Test
    @DisplayName("Debe validar imagen JPEG por magic bytes")
    void debeValidarImagenJPEG() throws IOException {
        byte[] jpegSignature = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00, 0x00, 0x00};
        InputStream is = new ByteArrayInputStream(jpegSignature);

        assertThat(FileValidator.isValidImage(is)).isTrue();
    }

    @Test
    @DisplayName("Debe validar imagen WebP por magic bytes")
    void debeValidarImagenWebP() throws IOException {
        byte[] webpSignature = new byte[]{0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50};
        InputStream is = new ByteArrayInputStream(webpSignature);

        assertThat(FileValidator.isValidImage(is)).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar archivo que no es imagen")
    void debeRechazarArchivoNoImagen() throws IOException {
        byte[] invalidBytes = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
        InputStream is = new ByteArrayInputStream(invalidBytes);

        assertThat(FileValidator.isValidImage(is)).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar archivo con pocos bytes")
    void debeRechazarArchivoPequeño() throws IOException {
        byte[] tooSmall = new byte[]{0x00, 0x01};
        InputStream is = new ByteArrayInputStream(tooSmall);

        assertThat(FileValidator.isValidImage(is)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"image.jpg", "photo.jpeg", "picture.png", "graphic.webp"})
    @DisplayName("Debe aceptar extensiones de imagen válidas")
    void debeAceptarExtencionesValidas(String filename) {
        assertThat(FileValidator.hasValidImageExtension(filename)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"document.pdf", "file.txt", "script.js", "style.css", "video.mp4"})
    @DisplayName("Debe rechazar extensiones no válidas")
    void debeRechazarExtencionesInvalidas(String filename) {
        assertThat(FileValidator.hasValidImageExtension(filename)).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar filename null")
    void debeRechazarFilenameNull() {
        assertThat(FileValidator.hasValidImageExtension(null)).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar filename sin extensión")
    void debeRechazarFilenameSinExtension() {
        assertThat(FileValidator.hasValidImageExtension("imagensinextension")).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"image.JPG", "photo.JPEG", "picture.PNG", "graphic.WEBP"})
    @DisplayName("Debe aceptar extensiones en mayúsculas")
    void debeAceptarExtensionesMayusculas(String filename) {
        assertThat(FileValidator.hasValidImageExtension(filename)).isTrue();
    }
}
