# Sistema de Gestión Administrativa de un Gimnasio

## Avance 3 - Entrega final de Programación II

Aplicación web para administrar clientes, entrenadores, planes, suscripciones,
pagos, accesos por QR simulado, agenda de sesiones, disponibilidad y reportes
de un gimnasio.

El Avance 3 integra y mejora los avances anteriores. La persistencia en archivos
`.dat` fue reemplazada por PostgreSQL mediante JDBC explícito y un pool de
conexiones Tomcat JDBC. El proyecto no utiliza JPA ni Hibernate.

Repositorio público:
[github.com/TheLol952/Proyecto_gimnasio_grupo6](https://github.com/TheLol952/Proyecto_gimnasio_grupo6)

> **Configuración académica:** el repositorio incluye intencionalmente un
> archivo `.env` con credenciales exclusivamente demostrativas. No pertenecen
> a servicios reales y no deben reutilizarse en producción. Se versionan para
> que el docente pueda ejecutar la entrega sin crear usuarios, bases de datos
> ni archivos de configuración.

## Funcionalidades

| Historia | Funcionalidad | Estado |
|---|---|---|
| HU-001 | Registrar, consultar y actualizar clientes | Implementada |
| HU-002 | Venta de planes, creación transaccional de suscripción y pago | Implementada |
| HU-003 | Acceso por QR simulado y validación de vigencia | Implementada |
| HU-004 | Agenda de sesiones y prevención de traslapes | Implementada |
| HU-005 | Consulta de franjas disponibles por entrenador y fecha | Implementada |
| HU-006 | Resumen administrativo de clientes, suscripciones e ingresos | Implementada |
| HU-007 | Verificación concurrente de vencimientos y avisos | Implementada |

Una sesión programada no puede comenzar en una fecha pasada. La regla se aplica
en el formulario Vue, en `SesionRequestDTO`, en `SesionService` y mediante un
trigger de PostgreSQL. Esto corrige la observación recibida en el avance anterior.

## Tecnologías

- Java 21.
- Spring Boot 3.3.4.
- Spring Web, Validation y JDBC.
- Tomcat JDBC Pool.
- PostgreSQL 16.
- JUnit 5 y Mockito.
- Vue.js 3, Vue Router, Axios y Vite.
- Maven y npm.

## Arquitectura

```text
frontend Vue
    -> API REST /api
        -> Controller
            -> Service (reglas de negocio)
                -> DAO (interfaces)
                    -> DAOImpl + BaseJdbcDAO
                        -> DataSource / Tomcat JDBC Pool
                            -> PostgreSQL
```

Estructura principal:

```text
backend/src/main/java/com/sv/gimnasio/
├── config/       DataSourceConfig y reloj de negocio
├── controller/   Endpoints REST y ApiExceptionHandler
├── dao/          Contratos DAO y mapper JDBC genérico
├── dao/impl/     Implementaciones con JDBC explícito
├── dto/          Entradas y salidas de la API
├── exception/    Excepciones propias de persistencia
├── model/        Entidades y jerarquías de dominio
├── service/      Reglas de negocio y concurrencia
└── util/         Generación de IDs y normalización reutilizable

backend/src/test/java/com/sv/gimnasio/
├── controller/
├── dao/impl/
├── dto/
├── service/
└── util/

database/
├── schema.sql
└── data.sql

frontend/src/
├── router/
├── services/
└── views/

.env             Configuración y claves exclusivamente demostrativas
compose.yaml      Entorno reproducible de base de datos, backend y frontend
```

## Programación orientada a objetos

- **Encapsulamiento:** atributos privados y acceso controlado por métodos.
- **Herencia:** `Cliente` y `Entrenador` extienden `Persona`.
- **Abstracción:** `Persona` y `Plan` son clases abstractas.
- **Polimorfismo:** `PlanMensual` y `PlanAnual` implementan de forma distinta
  `calcularFechaFin()` y `precioFinal()`.
- **Interfaces:** los servicios dependen de contratos DAO, no de implementaciones.
- **Colecciones:** listas, `TreeMap` para agenda y `ConcurrentHashMap` para avisos.
- **Genéricos:** `JdbcEntityMapper<T>` y `BaseJdbcDAO.leerLista(...)` reutilizan
  el mapeo de cualquier entidad desde un `ResultSet`.

## Ejecución rápida para evaluación

La ruta recomendada requiere únicamente Git y Docker Desktop con Docker
Compose. No es necesario instalar PostgreSQL, Java, Maven, Node.js ni crear
manualmente la base o su usuario.

Desde la raíz del repositorio:

```bash
docker compose up --build
```

La primera ejecución descarga las imágenes, compila las aplicaciones, crea
PostgreSQL 16, aplica `database/schema.sql`, carga `database/data.sql` y levanta
todos los servicios. Cuando aparezcan como iniciados, abrir:

- Interfaz: `http://localhost:5173`
- API: `http://localhost:8081/api`

Para detener la demostración:

```bash
docker compose down
```

Si se necesita reiniciar también los datos demostrativos desde cero, el
siguiente comando elimina exclusivamente el volumen Docker de este proyecto:

```bash
docker compose down -v
```

## Archivo `.env` demostrativo

El archivo `.env` de la raíz es la única fuente versionada de contraseñas y
valores configurables de la demostración. Spring Boot lo carga automáticamente,
Docker Compose lo utiliza para aprovisionar PostgreSQL y los scripts de
inicialización lo leen antes de ejecutar SQL.

Incluye:

- conexión JDBC, nombre de base, usuario y contraseña de PostgreSQL;
- puertos del backend y frontend;
- usuarios y contraseñas demostrativos de la tabla `usuario`.

Estas credenciales son públicas por diseño, solo sirven para evaluación local y
no protegen información real. Para un despliegue real, `.env` debe excluirse de
Git, las claves deben rotarse y administrarse con un gestor de secretos.

## Ejecución local alternativa

Esta alternativa requiere JDK 21, Maven 3.9+, Node.js 18+, npm, PostgreSQL 16 y
el cliente `psql`. La base y el usuario indicados en `.env` deben existir en la
instalación local; para una evaluación sin preparación se debe usar Docker.

Preparar esquema y datos en PowerShell:

```powershell
.\scripts\init-db.ps1
```

O en Bash:

```bash
bash scripts/init-db.sh
```

Los scripts cargan automáticamente `.env`; no hace falta exportar cada variable.
Después, iniciar el backend:

```bash
cd backend
mvn spring-boot:run
```

Y en otra terminal iniciar el frontend:

```bash
cd frontend
npm install
npm run dev
```

Los scripts SQL son re-ejecutables y no eliminan tablas ni datos existentes.

## DataSource y pool de conexiones

`DataSourceConfig` crea un `org.apache.tomcat.jdbc.pool.DataSource`. Los DAO
dependen únicamente de la interfaz `javax.sql.DataSource`.

| Parámetro | Valor | Justificación |
|---|---:|---|
| `validationQuery` | `SELECT 1` | Verificación liviana compatible con PostgreSQL |
| `initialSize` | 3 | Conexiones listas para la demostración |
| `minIdle` | 3 | Evita recrear conexiones ante carga normal |
| `maxIdle` | 8 | Limita conexiones ociosas |
| `maxActive` | 15 | Capacidad suficiente sin saturar PostgreSQL |
| `maxWait` | 10000 ms | Falla controladamente si el pool se agota |
| `testOnBorrow` | `true` | No entrega conexiones inválidas |
| `testOnReturn` | `false` | Evita una validación duplicada al devolver |
| `testWhileIdle` | `true` | Revisa conexiones inactivas |
| `testOnConnect` | `true` | Valida conexiones nuevas |
| `timeBetweenEvictionRunsMillis` | 30000 ms | Revisión periódica del pool |
| `minEvictableIdleTimeMillis` | 60000 ms | Retira conexiones ociosas por más de un minuto |
| `removeAbandoned` | `true` | Detecta conexiones no devueltas |
| `removeAbandonedTimeout` | 60 s | Umbral para considerar una conexión abandonada |
| `validationInterval` | 30000 ms | Evita validaciones redundantes demasiado frecuentes |

Todas las consultas variables usan `PreparedStatement` y los recursos se
cierran con `try-with-resources`. `TransactionAwareDataSourceProxy` permite que
las operaciones JDBC de suscripción y pago participen en la misma transacción
de Spring.

## Cómo ejecutar las pruebas

Backend:

```bash
cd backend
mvn clean test
```

La suite incluye 22 pruebas JUnit/Mockito para servicios, controlador,
validaciones, utilidades, mapeo JDBC genérico, reglas de fecha, traslapes, QR,
disponibilidad, suscripciones y concurrencia.

Build del frontend:

```bash
cd frontend
npm run build
```

## Concurrencia integrada con la base de datos

`NotificacionVencimientoService` utiliza un `ScheduledExecutorService` de un
solo hilo. Cada minuto:

1. Consulta suscripciones mediante `SuscripcionDAO` y el `DataSource`.
2. Actualiza como `VENCIDA` cualquier suscripción cuya fecha final ya pasó.
3. Mantiene avisos únicos para las suscripciones que vencen en los próximos
   tres días.
4. Registra errores mediante SLF4J sin finalizar permanentemente la tarea.
5. Detiene el ejecutor de forma controlada cuando Spring cierra la aplicación.

El `ConcurrentHashMap` evita duplicar avisos y permite lectura segura desde las
peticiones REST. La vigencia para el acceso QR se consulta directamente en
PostgreSQL, por lo que no existe una caché que pueda quedar inconsistente tras
un rollback.

## Manejo de excepciones

- `PersistenciaException` encapsula fallos JDBC y conserva la causa para logs.
- `ApiExceptionHandler` devuelve mensajes seguros sin SQL ni trazas internas.
- Los errores de validación y reglas de negocio responden HTTP 400.
- Los recursos inexistentes responden HTTP 404.
- Una falla temporal de conexión responde HTTP 503.
- Los errores inesperados responden HTTP 500 con un mensaje genérico.

## Ejemplo de mejora antes/después

Antes de la migración, cada DAO cargaba una lista completa desde
`DatFileStorage<T>` y volvía a serializar el archivo `.dat`. Actualmente los DAO
ejecutan SQL parametrizado sobre PostgreSQL y `BaseJdbcDAO` centraliza las
conexiones, el tratamiento de `SQLException` y el recorrido genérico de
`ResultSet` mediante `JdbcEntityMapper<T>`.

También se reemplazaron cinco implementaciones duplicadas de UUID por
`IdGenerator`, y la normalización repetida de teléfono/correo por `TextoUtils`.

## Endpoints principales

| Método | Ruta | Uso |
|---|---|---|
| GET, POST | `/api/clientes` | Listar y registrar clientes |
| GET, PUT | `/api/clientes/{id}` | Consultar y actualizar cliente |
| GET, POST, PUT | `/api/entrenadores` | Gestión de entrenadores |
| GET | `/api/planes` | Planes disponibles |
| POST | `/api/suscripciones` | Venta transaccional de un plan |
| GET | `/api/suscripciones/cliente/{id}` | Historial de suscripciones |
| GET, POST | `/api/accesos` | Control e historial QR |
| GET, POST | `/api/sesiones` | Agenda de sesiones |
| GET | `/api/sesiones/disponibilidad` | Franjas libres por entrenador y fecha |
| GET | `/api/reportes` | Resumen administrativo |
| GET | `/api/reportes/vencimientos-proximos` | Avisos generados concurrentemente |

Para el QR simulado, un cliente `CLI-ABC12345` utiliza `QR-ABC12345`.

## Datos de demostración

`database/data.sql` agrega planes, clientes, entrenadores, suscripciones,
pagos, sesiones y accesos mediante `ON CONFLICT DO NOTHING`.

La tabla `usuario` contiene las cuentas declaradas en `.env` para evidenciar el
modelo y los roles, pero el proyecto no expone todavía un módulo de
autenticación; por ello esas cuentas no se utilizan para ingresar a la interfaz.

## Equipo

| Integrante | Rol Scrum | Rol técnico |
|---|---|---|
| Brayan Enrique Alfaro Guzmán | Product Owner | Análisis de negocio |
| Josué Ernesto Zelaya Carballo | Scrum Master | Coordinación y QA de proceso |
| Kelly Alejandra Rodríguez Alvarado | Developer | Frontend Vue |
| Carlos Javier Alfaro Viera | Developer | Backend y persistencia |
| Rubén Eduardo Estupinian Ávila | QA | Pruebas funcionales |

## Evidencias pendientes para la entrega académica

El código y este README cubren los requisitos técnicos del Avance 3. Antes de
entregar, el equipo debe incorporar al documento académico final las capturas de
ejecución, el diagrama UML actualizado, las conclusiones y la evidencia visual
de las pruebas, tal como solicita la guía del docente.
