# 🌐 Red Social Profesional

> Una plataforma de networking profesional estilo LinkedIn desarrollada con Spring Boot y PostgreSQL

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Descripción

**Red Social Profesional** es una aplicación web que permite a los usuarios crear perfiles profesionales, conectarse con otros profesionales, y gestionar su red de contactos laborales. El proyecto está desarrollado como parte de un curso universitario de Ingeniería de Sistemas.

### ✨ Características Principales

- 🔐 **Sistema de Autenticación**: Registro e inicio de sesión con JWT
- 👤 **Perfiles de Usuario**: Creación y gestión de perfiles profesionales
- 🔒 **Seguridad**: Autenticación basada en tokens y encriptación de contraseñas
- 📱 **Interfaz Responsive**: Diseño moderno con Bootstrap 5
- 🎨 **UI/UX Profesional**: Interfaz inspirada en LinkedIn
- 🗄️ **Base de Datos Robusta**: PostgreSQL con esquemas bien estructurados

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.5.0** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **JWT (jsonwebtoken)** - Tokens de autenticación
- **BCrypt** - Encriptación de contraseñas
- **Maven** - Gestión de dependencias

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5** - Framework CSS
- **JavaScript ES6** - Interactividad del frontend
- **HTML5 & CSS3** - Estructura y estilos

### Base de Datos
- **PostgreSQL** - Base de datos relacional
- **Esquema**: `red_profesional`

## 📋 Prerrequisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

### Obligatorios
- ☕ **Java 17** o superior
- 🐘 **PostgreSQL 12** o superior
- 📦 **Maven 3.6** o superior (opcional, se incluye wrapper)

### Verificación de Instalación

```bash
# Verificar Java
java -version
# Debería mostrar: java version "17.x.x"

# Verificar PostgreSQL
psql --version
# Debería mostrar: psql (PostgreSQL) 12.x o superior

# Verificar Maven (opcional)
mvn -version
```

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/red-social-profesional.git
cd red-social-profesional
```

### 2. Configurar Base de Datos

```sql
-- Conectar a PostgreSQL como superusuario
psql -U postgres

-- Crear base de datos
CREATE DATABASE red_profesional;

-- Crear esquema
\c red_profesional
CREATE SCHEMA red_profesional;

-- Ejecutar script de base de datos
\i bd.sql
```

### 3. Configurar Variables de Entorno (Opcional)

Puedes personalizar la configuración creando un archivo `application-local.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/red_profesional
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# JWT
app.jwtSecret=tu_clave_secreta_muy_larga_y_segura
```

### 4. Compilar el Proyecto

```bash
# Con Maven wrapper (recomendado)
./mvnw clean compile

# O con Maven instalado
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
./mvnw clean package

# Ejecutar JAR
java -jar target/red-0.0.1-SNAPSHOT.jar
```

### 🌐 Acceder a la Aplicación

Una vez iniciada la aplicación, abre tu navegador y visita:

- **URL Principal**: http://localhost:8080
- **Página de Login**: http://localhost:8080/login
- **Página de Registro**: http://localhost:8080/registro

## 📁 Estructura del Proyecto

```
red/
├── src/
│   ├── main/
│   │   ├── java/com/redsocial/red/
│   │   │   ├── config/          # Configuraciones de Spring
│   │   │   ├── controller/      # Controladores REST y Web
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # Entidades JPA
│   │   │   ├── repository/     # Repositorios de datos
│   │   │   ├── security/       # Configuración de seguridad
│   │   │   └── service/        # Lógica de negocio
│   │   └── resources/
│   │       ├── static/         # Archivos estáticos (CSS, JS)
│   │       ├── templates/      # Plantillas Thymeleaf
│   │       └── application.properties
│   └── test/                   # Tests unitarios
├── bd.sql                      # Script de base de datos
├── pom.xml                     # Configuración Maven
└── README.md                   # Este archivo
```

## 🔧 API Endpoints Principales

### Autenticación
- `POST /api/auth/registro` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/verify/{token}` - Verificar email

### Páginas Web
- `GET /` - Página principal
- `GET /login` - Página de login
- `GET /registro` - Página de registro
- `GET /dashboard` - Dashboard (requiere autenticación)

## 🔧 Configuración Adicional

### Variables de Entorno Importantes

```bash
# Java (OBLIGATORIO)
JAVA_HOME=C:\Program Files\Java\jdk-17
PATH=%JAVA_HOME%\bin;%PATH%

# Maven (Opcional)
MAVEN_HOME=C:\apache-maven-3.9.9
PATH=%MAVEN_HOME%\bin;%PATH%
```

### Configuración de Logging

El proyecto incluye logging detallado. Para modificar los niveles:

```properties
# En application.properties
logging.level.com.redsocial.red=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 🐛 Solución de Problemas

### Error: "invalid target release: 17"
**Solución**: Verificar que JAVA_HOME apunte a Java 17
```bash
echo $JAVA_HOME  # Linux/Mac
echo %JAVA_HOME% # Windows
```

### Error: "Credenciales inválidas" después del registro
**Solución**: Ya está corregido. Los nuevos usuarios pueden hacer login inmediatamente.

### Error de conexión a PostgreSQL
**Solución**: Verificar que PostgreSQL esté ejecutándose y la configuración sea correcta.

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

### Reglas de Contribución
- Seguir las convenciones de código existentes
- Incluir tests para nuevas funcionalidades
- Actualizar la documentación según sea necesario

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Autores

- **Camila** - *Desarrollo Principal* - [GitHub](https://github.com/tu-usuario)

## 🙏 Agradecimientos

- Profesores y compañeros de la carrera de Ingeniería de Sistemas
- Comunidad de Spring Boot
- [Spring.io](https://spring.io/) por la excelente documentación
- [Bootstrap](https://getbootstrap.com/) por el framework CSS

## 📚 Recursos Adicionales

- [Documentación de Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Guía de Spring Security](https://spring.io/guides/gs/securing-web/)
- [Tutorial de PostgreSQL](https://www.postgresql.org/docs/)
- [Documentación de JWT](https://jwt.io/introduction/)

---

⭐ **¡No olvides dar una estrella al proyecto si te fue útil!** ⭐ 