# PetLink - Plataforma de Adopción de Mascotas

Plataforma web para facilitar la adopción de mascotas, conectando protectoras y personas interesadas en adoptar.

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Contribuir](#contribuir)

---

## Tecnologías

### Backend
- **Java 25**
- **Spring Boot 3.5.8**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Mail
  - Spring Validation
- **MySQL** (base de datos en producción)
- **H2** (base de datos para tests)
- **Maven**
- **Lombok**

### Frontend
- **Thymeleaf** (motor de plantillas)
- **Tailwind CSS 3.4** (estilos)
- **JavaScript** (vanilla)

### Almacenamiento
- **MinIO**

### Utilidades
- **libphonenumber**

---

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

### Java 25 o superior
- Descargar desde: https://www.oracle.com/es/java/technologies/downloads/

---

## Instalación y Ejecución

### Paso 1: Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/petlink.git
cd petlink
```

### Paso 2: Configurar variables de entorno
Copia el archivo de ejemplo y configura tus credenciales:
```bash
cp .env.example .env
```

### Paso 3: Ejecutar el proyecto

**Opción A - Solo Maven (recomendado):**
```bash
# Mac/Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```
Maven instalará Node/npm automáticamente y compilará Tailwind CSS.

**Opción B - Usando Visual Studio Code (desarrollo):**

1. Abre el proyecto en VS Code
2. Ve a la pestaña "Ejecutar y depurar" (Ctrl+Shift+D)
3. Selecciona "Spring Boot App" en el desplegable
4. Haz clic en el botón de play

### Paso 4: Acceder a la aplicación

Abre tu navegador en: **http://localhost:8080**

---

## Estructura del Proyecto

```
petlink/
├── src/
│   ├── main/
│   │   ├── java/com/petlink/adopcion_mascotas/
│   │   │   ├── config/              # Configuración (Security, MinIO, etc.)
│   │   │   ├── controller/          # Controladores MVC
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── enums/               # Tipos enumerados
│   │   │   ├── helper/              # Helpers
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── repository/          # Repositorios Spring Data
│   │   │   ├── service/             # Lógica de negocio
│   │   │   └── util/                # Clases de utilidad
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── assets/          # Imágenes y logos
│   │       │   ├── css/
│   │       │   │   ├── styles.css   # Fuente Tailwind
│   │       │   │   └── main.css     # CSS compilado (no editar)
│   │       │   └── js/              # JavaScript
│   │       ├── templates/           # Plantillas Thymeleaf
│   │       │   ├── _t/              # Fragmentos compartidos
│   │       │   ├── auth/            # Autenticación
│   │       │   ├── contacto/        # Página de contacto
│   │       │   ├── dashboard/       # Panel de control
│   │       │   ├── home/            # Página principal
│   │       │   ├── mascota/         # Gestión de mascotas
│   │       │   ├── perfil/          # Perfil de usuario
│   │       │   ├── protectora/      # Gestión de protectoras
│   │       │   └── usuario/         # Gestión de usuarios
│   │       └── application.properties
│   └── test/                        # Tests
├── .env.example                     # Variables de entorno de ejemplo
├── Dockerfile                       # Configuración Docker
├── Jenkinsfile                      # Pipeline CI/CD
├── package.json                     # Configuración npm
├── pom.xml                          # Configuración Maven
├── tailwind.config.js               # Configuración Tailwind
└── mvnw / mvnw.cmd                  # Maven Wrapper
```
---

## Contribuir

### Workflow de Git

1. **Crea una rama para tu feature:**
```bash
git checkout -b feature/nombre-feature
```

2. **Haz tus cambios y commitea:**
```bash
git add .
git commit -m "feat: descripcion del cambio"
```

3. **Push a tu rama:**
```bash
git push origin feature/nombre-feature
```

4. **Crea un Pull Request** en GitHub

### Conventional Commits

- `feat:` - Nueva funcionalidad
- `fix:` - Corrección de bugs
- `docs:` - Cambios en documentación
- `style:` - Formateo de código
- `refactor:` - Refactorización
- `test:` - Tests
- `chore:` - Tareas de mantenimiento

---

## Documentación Adicional

- [Memoria Proyecto](https://drive.google.com/file/d/10s4MJWA2Vpw7svJ2eHdFCjun46jpxbI4/view?usp=sharing)
- [Presentación](https://drive.google.com/file/d/1EbO-Lzb2ZEurcQSfaip7C6-kvihieI-d/view?usp=sharing)

---

## Licencia

Este proyecto es parte de un Trabajo de Fin de Grado (TFG).

---

**Proyecto TFG hecho en conjunto por Jorge López del Valle, Rubén Rodríguez Orallo e Iker Ibarra Martín para ayudar a las mascotas a encontrar un hogar**
