# GestionInventarios

Sistema de gestión de inventarios desarrollado con Spring Boot y PostgreSQL para la administración completa de productos, clientes, proveedores, ventas y usuarios.

## Características

- **Gestión de Productos**: CRUD completo para el control de inventario
- **Gestión de Clientes**: Administración de información de clientes con validación de documentos únicos
- **Gestión de Proveedores**: Control de proveedores y sus productos
- **Gestión de Ventas**: Sistema completo de ventas con detalles y estados
- **Gestión de Usuarios**: Sistema de autenticación y autorización con roles
- **Seguridad**: Integración con Spring Security para control de accesos

## Tecnologías Utilizadas

- **Java 21**: Versión del lenguaje
- **Spring Boot 3.5.6**: Framework principal
- **Spring Data JPA**: Para persistencia de datos
- **Spring Security**: Para seguridad y autenticación
- **PostgreSQL**: Base de datos relacional
- **Lombok**: Para reducir código boilerplate
- **ModelMapper 3.2.0**: Para mapeo entre entidades y DTOs
- **Thymeleaf**: Motor de plantillas
- **Maven**: Gestión de dependencias

## Estructura del Proyecto

```
src/main/java/com/Arquitecturaservicios/gestioninventarios/
├── Config/           # Configuraciones (Security, Exception Handling, etc.)
├── Controller/       # Controladores REST
├── Entities/         # Entidades JPA
├── Repository/       # Repositorios JPA
├── Service/          # Lógica de negocio
└── dto/              # Data Transfer Objects
```

## Configuración

1. Copiar el archivo `.env.example` y renombrarlo a `.env`
2. Configurar las variables de entorno según tu entorno
3. Asegurarse de tener PostgreSQL corriendo en el puerto especificado

## Instalación y Ejecución

```bash
# Clonar el repositorio
git clone <repository-url>

# Navegar al proyecto
cd Gestion-inventarios

# Compilar el proyecto
./mvnw clean install

# Ejecutar la aplicación
./mvnw spring-boot:run
```

## Variables de Entorno

Las siguientes variables de entorno deben configurarse en el archivo `.env`:

- `DB_URL`: URL de conexión a PostgreSQL
- `DB_USERNAME`: Usuario de la base de datos
- `DB_PASSWORD`: Contraseña de la base de datos

## API Endpoints

La aplicación expone endpoints REST para la gestión de:
- `/api/clientes` - Gestión de clientes
- `/api/productos` - Gestión de productos
- `/api/proveedores` - Gestión de proveedores
- `/api/ventas` - Gestión de ventas
- `/api/usuarios` - Gestión de usuarios
- `/api/roles` - Gestión de roles

## Autor

Desarrollado como sistema de gestión de inventarios empresarial.
