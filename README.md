# 🌐 Red Social Profesional

> Una plataforma de networking profesional estilo LinkedIn desarrollada con Spring Boot y PostgreSQL

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Descripción

**Red Social Profesional** es una aplicación web completa que permite a los usuarios crear perfiles profesionales, conectarse con otros profesionales, y gestionar su red de contactos laborales. El proyecto está desarrollado como parte de un curso universitario de Ingeniería de Sistemas, implementando las mejores prácticas de desarrollo web moderno.

### ✨ Características Principales

- 🔐 **Sistema de Autenticación Dual**: 
  - Autenticación web tradicional con sesiones y CSRF
  - API REST con autenticación JWT para aplicaciones móviles
- 👤 **Gestión de Perfiles**: Creación y administración de perfiles profesionales completos
- 🔒 **Seguridad Robusta**: 
  - Encriptación BCrypt para contraseñas
  - Tokens JWT con expiración configurable
  - Protección CSRF para formularios web
- 📱 **Interfaz Responsive**: Diseño adaptativo con Bootstrap 5
- 🎨 **UI/UX Profesional**: Interfaz moderna inspirada en LinkedIn
- 🗄️ **Base de Datos Robusta**: PostgreSQL con esquemas optimizados y triggers
- ✉️ **Sistema de Verificación**: Verificación de email integrada
- 🌐 **API RESTful**: Endpoints completos para integración con aplicaciones externas

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación principal
- **Spring Boot 3.5.0** - Framework de aplicación
- **Spring Security 6** - Autenticación y autorización avanzada
- **Spring Data JPA** - Persistencia y manejo de datos
- **JWT (jsonwebtoken)** - Tokens de autenticación stateless
- **BCrypt** - Encriptación segura de contraseñas
- **Maven** - Gestión de dependencias y build

### Frontend
- **Thymeleaf** - Motor de plantillas server-side
- **Bootstrap 5.3** - Framework CSS responsive
- **JavaScript ES6** - Interactividad del cliente
- **HTML5 & CSS3** - Estructura y estilos modernos
- **Bootstrap Icons** - Iconografía profesional

### Base de Datos
- **PostgreSQL 12+** - Base de datos relacional principal
- **Esquema**: `red_profesional` con tablas optimizadas
- **Triggers y Funciones**: Automatización de procesos de BD

## 📋 Prerrequisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

### Obligatorios
- ☕ **Java 17** o superior ([Descargar](https://adoptium.net/))
- 🐘 **PostgreSQL 12** o superior ([Descargar](https://www.postgresql.org/download/))
- 📦 **Maven 3.6** o superior (opcional, se incluye wrapper)

### Verificación de Instalación

```bash
# Verificar Java
java -version
# Debería mostrar: openjdk version "17.x.x"

# Verificar PostgreSQL
psql --version
# Debería mostrar: psql (PostgreSQL) 12.x o superior

# Verificar Maven (opcional)
mvn -version
```

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Hir0Exe/red-social-profesional.git
cd red-social-profesional
```

### 2. Configurar Base de Datos

```sql
-- Conectar a PostgreSQL como superusuario
psql -U postgres

-- Crear base de datos
CREATE DATABASE red_profesional;

-- Crear esquema y ejecutar scripts
\c red_profesional
\i bd.sql
```

### 3. Configurar Variables de Entorno (Opcional)

Crear archivo `application-local.properties` para configuración personalizada:

```properties
# Configuración de Base de Datos
spring.datasource.url=jdbc:postgresql://localhost:5432/red_profesional
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configuración JWT
app.jwtSecret=tu_clave_secreta_muy_larga_y_segura_de_al_menos_32_caracteres
app.jwtExpirationMs=86400000

# Configuración de Email (para verificación)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_app_password
```

### 4. Compilar el Proyecto

```bash
# Con Maven wrapper (recomendado)
./mvnw clean compile

# O con Maven instalado globalmente
mvn clean compile
```

## ▶️ Cómo Ejecutar la Aplicación

### Opción 1: Con Maven Wrapper (Recomendado)

```bash
# Windows
.\mvnw spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Opción 2: Con Maven Instalado

```bash
mvn spring-boot:run
```

### Opción 3: Ejecutar JAR

```bash
# Compilar JAR
./mvnw clean package -DskipTests

# Ejecutar JAR
java -jar target/red-0.0.1-SNAPSHOT.jar
```

### 🌐 Acceder a la Aplicación

Una vez iniciada la aplicación, abre tu navegador y visita:

- **🏠 Página Principal**: http://localhost:8080
- **🔑 Inicio de Sesión**: http://localhost:8080/login
- **👥 Registro**: http://localhost:8080/registro
- **📊 Dashboard**: http://localhost:8080/dashboard (requiere autenticación)

## 📁 Estructura del Proyecto

```
red/
├── src/
│   ├── main/
│   │   ├── java/com/redsocial/red/
│   │   │   ├── config/          # Configuraciones de Spring Security y CORS
│   │   │   ├── controller/      # Controladores REST y Web
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # Entidades JPA con relaciones
│   │   │   ├── repository/     # Repositorios JPA con consultas custom
│   │   │   ├── security/       # JWT, UserDetails y filtros de seguridad
│   │   │   └── service/        # Lógica de negocio y servicios
│   │   └── resources/
│   │       ├── static/         # CSS, JavaScript e imágenes
│   │       ├── templates/      # Plantillas Thymeleaf
│   │       │   └── fragments/  # Fragmentos reutilizables
│   │       └── application.properties
│   └── test/                   # Tests unitarios e integración
├── bd.sql                      # Script completo de base de datos
├── pom.xml                     # Configuración Maven
└── README.md                   # Documentación del proyecto
```

## 🔧 API Endpoints Principales

### 🔐 Autenticación
```
POST /api/auth/registro         # Registrar nuevo usuario
POST /api/auth/login           # Autenticación con JWT
GET  /api/auth/verify          # Verificar email con token
GET  /api/auth/check-email     # Verificar disponibilidad de email
```

### 🌐 Páginas Web
```
GET  /                         # Página principal con hero section
GET  /login                    # Formulario de inicio de sesión
GET  /registro                 # Formulario de registro
GET  /dashboard               # Panel de usuario autenticado
GET  /logout                  # Cerrar sesión
```

### 🔍 Diagnóstico (Desarrollo)
```
GET  /api/diagnostic/users     # Listar todos los usuarios
GET  /api/diagnostic/user/{email} # Obtener usuario específico
```

## ⚙️ Configuración Avanzada

### Variables de Entorno del Sistema

```bash
# Java (REQUERIDO)
JAVA_HOME=C:\Program Files\Java\jdk-17
PATH=%JAVA_HOME%\bin;%PATH%

# PostgreSQL (Opcional)
PGPASSWORD=tu_password_postgres
```

### Configuración de Logging

```properties
# Niveles de logging personalizados
logging.level.com.redsocial.red=INFO
logging.level.org.springframework.security=INFO
logging.level.org.springframework.web=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### Perfiles de Spring

```bash
# Ejecutar en modo desarrollo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Ejecutar en modo producción
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## 🎯 Características Implementadas

### ✅ Sistema de Autenticación
- [x] Registro de usuarios con validación
- [x] Login con email y contraseña
- [x] Autenticación JWT para APIs
- [x] Autenticación de sesión para web
- [x] Verificación de email (configurada)
- [x] Encriptación BCrypt

### ✅ Seguridad
- [x] Protección CSRF para formularios web
- [x] CORS configurado para desarrollo
- [x] Filtros de seguridad separados para API y Web
- [x] Manejo seguro de tokens JWT
- [x] Validación de entrada en DTOs

### ✅ Interfaz de Usuario
- [x] Diseño responsive con Bootstrap 5
- [x] Páginas de login y registro funcionales
- [x] Dashboard para usuarios autenticados
- [x] Navegación dinámica según estado de autenticación
- [x] Mensajes de error y éxito

### ✅ Base de Datos
- [x] Esquema completo con relaciones
- [x] Entidades JPA configuradas
- [x] Repositorios con consultas personalizadas
- [x] Triggers para automatización
- [x] Índices para optimización

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

### 📋 Reglas de Contribución
- Seguir las convenciones de código existentes
- Incluir tests para nuevas funcionalidades
- Actualizar la documentación según sea necesario
- Respetar la arquitectura establecida

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Autores

- **Camila** - *Desarrollo Principal* - [GitHub](https://github.com/Hir0Exe)

---

## 🙏 Agradecimientos

- Profesores y compañeros de la carrera de Ingeniería de Sistemas
- Comunidad de Spring Boot y Spring Security
- [Spring.io](https://spring.io/) por la excelente documentación
- [Bootstrap Team](https://getbootstrap.com/) por el framework CSS
- Comunidad de desarrolladores de código abierto

## 📚 Recursos de Aprendizaje

- [Documentación Oficial de Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT.io - JSON Web Tokens](https://jwt.io/introduction/)
- [Bootstrap 5 Documentation](https://getbootstrap.com/docs/5.3/getting-started/introduction/)

---

⭐ **¡No olvides dar una estrella al proyecto si te fue útil!** ⭐

**Estado del Proyecto**: 🟢 **Activo y Funcional** - Todas las funcionalidades principales implementadas y probadas. 