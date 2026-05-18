# hogar-seguro-auth

> Servicio de autenticación (Auth) para la plataforma Hogar Seguro.

**Propósito**: Provee endpoints para registro, login, validación de JWT y manejo de reset de contraseña.

**Contenido rápido**
- Build: `./mvnw clean package`
- Ejecutar: `./mvnw spring-boot:run` o `java -jar target/auth-service-0.0.1-SNAPSHOT.jar`
- Puerto por defecto: `8081` (variable `AUTH_PORT`)

**Requisitos**
- Java 21
- Maven (o usar el wrapper `./mvnw` incluido)

**Cómo ejecutar localmente**
1. Construir:

```bash
./mvnw clean package -DskipTests
```

2. Ejecutar con Spring Boot:

```bash
./mvnw spring-boot:run
# o
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

3. Ejecutar tests:

```bash
./mvnw test
```

**Configuración (variables de entorno / overrides)**
Las propiedades principales se encuentran en `src/main/resources/application.yml`. Variables útiles:

- `DB_URL` (por defecto `jdbc:h2:mem:testdb`)
- `DB_USERNAME` (por defecto `sa`)
- `DB_PASSWORD`
- `DB_DRIVER` (por defecto `org.h2.Driver`)
- `HIBERNATE_DIALECT`
- `AUTH_PORT` (por defecto `8081`)
- `JWT_SECRET` (usar un secreto de al menos 32 caracteres en producción)
- `JWT_EXPIRATION_MS` (milisegundos)
- `EMAIL_PROVIDER` (por ejemplo `brevo` o `dummy`)
- `BREVO_API_KEY`, `BREVO_API_URL`, `BREVO_SENDER_NAME`, `BREVO_SENDER_EMAIL`
- `APP_RESET_PASSWORD_URL` (URL pública para el link de reset)

Ejemplo (bash) antes de ejecutar:
- En caso de no usar email_provider dummy. Usar un proveedor de email real (`EMAIL_PROVIDER=brevo`) y configurar `BREVO_API_KEY`.

```bash
export AUTH_PORT=8081
export DB_URL=jdbc:postgresql://localhost:5432/authdb
export DB_USERNAME=auth
export DB_PASSWORD=secret
export JWT_SECRET="clave_secreta"
export EMAIL_PROVIDER=dummy
```

**Endpoints principales**

- GET `/health` — Health check (devuelve status UP)

- POST `/auth/register` — Registrar usuario
  - Request JSON:

```json
{
  "email": "vonneumann@hotmail.com",
  "password": "MiPassword123!"
}
```
  - Response: `201 Created` con `RegisterResponseDTO` envuelto en `StandardResponse`.

- POST `/auth/login` — Iniciar sesión
  - Request JSON:

```json
{
  "email": "vonneumann@hotmail.com",
  "password": "MiPassword123!"
}
```
  - Response: `200 OK` con `accessToken` y `refreshToken` (`LoginResponseDTO`).

- GET `/auth/validate` — Validar token
  - Header: `Authorization: Bearer <accessToken>`
  - Response: `200 OK` con payload JWT (si es válido).

- POST `/auth/password-reset` — Solicitar reset de contraseña (envía email)
  - Request JSON:

```json
{
  "email": "vonneumann@hotmail.com"
}
```

- PUT `/auth/password-update` — Actualizar contraseña con token
  - Request JSON:

```json
{
  "token": "123e4567-e89b-12d3-a456-426614174000",
  "newPassword": "NuevoPassword123!"
}
```
**H2 Console**
- Si usa la DB en memoria por defecto, la consola H2 está habilitada en `/h2-console`.

**Swagger / OpenAPI**
- Springdoc está incluido; la UI suele estar disponible en `/swagger-ui/index.html` o `/swagger-ui.html`.
