package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.config.MinioProperties;
import com.petlink.adopcion_mascotas.util.FileValidator;
import io.minio.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String publicUrl;

    public MinioService(MinioProperties minioProperties) {
        this.publicUrl = minioProperties.getPublicUrl();
        this.bucketName = minioProperties.getBucketName();
        this.minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    public String uploadMultipleImages(MultipartFile[] files, String emailProtectora, String nombreMascota)
            throws Exception {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Debe subir al menos una imagen");
        }

        String sanitizedEmail = emailProtectora.replaceAll("[^a-zA-Z0-9@.-]", "_");
        String sanitizedNombre = nombreMascota.replaceAll("[^a-zA-Z0-9]", "_");

        String folder = sanitizedEmail + "/" + sanitizedNombre;

        StringBuilder urls = new StringBuilder();
        long timestamp = System.currentTimeMillis();
        int imageCounter = 0;

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Todos los archivos deben ser imágenes (Content-Type inválido)");
            }

            String originalFilename = file.getOriginalFilename();
            if (!FileValidator.hasValidImageExtension(originalFilename)) {
                throw new IllegalArgumentException("Extensión de archivo no permitida: " + originalFilename);
            }

            byte[] fileBytes = file.getBytes();
            try (InputStream magicBytesStream = new ByteArrayInputStream(fileBytes)) {
                if (!FileValidator.isValidImage(magicBytesStream)) {
                    throw new IllegalArgumentException(
                            "El archivo no es una imagen válida (magic bytes inválidos): " + originalFilename);
                }
            }

            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                    : ".jpg";

            String fileName = folder + "/" + timestamp + "_" + imageCounter + extension;

            try (InputStream uploadStream = new ByteArrayInputStream(fileBytes)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(uploadStream, fileBytes.length, -1)
                        .contentType(contentType)
                        .build());
            }

            if (urls.length() > 0) {
                urls.append(",");
            }
            urls.append(getImageUrl(fileName));

            imageCounter++;
        }

        if (urls.length() == 0) {
            throw new IllegalArgumentException("No se pudo subir ninguna imagen válida");
        }

        return urls.toString();
    }

    public String getImageUrl(String fileName) {
        return String.format("%s/%s/%s", publicUrl, bucketName, fileName);
    }

    public void deleteImage(String imageUrl) throws Exception {
        String fileName = imageUrl.replace(publicUrl + "/" + bucketName + "/", "");

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            throw new Exception("Error al eliminar la imagen: " + e.getMessage());
        }
    }
}
