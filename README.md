# Sistema para la Gestión Administrativa de un Gimnasio

Segundo avance — Programación II.

## Tecnologías

- **Backend:** Java 21 + Spring Boot (Maven)
- **Frontend:** Vue.js 3 (Vite)
- **Comunicación:** REST API (JSON)
- **Base de datos (próximo avance):** PostgreSQL / SQL Server / Oracle (a definir)
- **Persistencia en este avance:** archivos `.dat` (serialización de objetos Java), sin conexión a base de datos.

## Estructura del repositorio

```
gimnasio-app/
├── backend/     Proyecto Spring Boot (Maven)
│   └── src/main/java/com/sv/gimnasio/
│       ├── model/       Entidades (Persona, Cliente, Entrenador, Plan, PlanMensual, PlanAnual, ...)
│       ├── dto/         Objetos de transferencia usados por los controladores REST
│       ├── dao/         Interfaces DAO + implementación (dao/impl) sobre archivos .dat
│       ├── service/     Lógica de negocio, colecciones y el hilo de vencimientos
│       ├── controller/  Controladores REST (@RestController)
│       └── util/        Utilidad genérica de persistencia .dat
└── frontend/    Proyecto Vue 3 + Vite
    └── src/
        ├── views/       Una vista por historia de usuario
        ├── services/    Cliente Axios hacia la REST API
        └── router/      Rutas de la SPA
```

## Cómo ejecutar el backend

Requiere JDK 21 y Maven (o el wrapper `mvnw`, si se agrega).

Instalar Maven (En caso de no tenerlo)


```bash
cd backend
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8081/api/...` y los archivos `.dat`
se generan automáticamente en `backend/data/`.

## Cómo ejecutar el frontend

Requiere Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

La aplicación queda disponible en `http://localhost:5173` y consume el backend
mediante un proxy configurado en `vite.config.js` (`/api` → `http://localhost:8081`).

## Endpoints principales

| Método | Ruta                              | Historia de usuario |
|--------|-----------------------------------|----------------------|
| GET/POST | `/api/clientes`                 | HU-001 |
| GET/POST | `/api/entrenadores`             | Gestión de entrenadores |
| GET    | `/api/planes`                     | Soporte a HU-002 |
| POST   | `/api/suscripciones`              | HU-002 |
| GET    | `/api/suscripciones/cliente/{id}` | HU-002 |
| POST/GET | `/api/accesos`                  | HU-003 |
| POST/GET | `/api/sesiones`                 | HU-004 / HU-005 |
| GET    | `/api/reportes`                   | HU-006 |
| GET    | `/api/reportes/vencimientos-proximos` | HU-007 (hilo de fondo) |

## Conceptos de POO aplicados

- **Encapsulamiento:** atributos privados con getters/setters validados en todas las entidades.
- **Herencia:** `Persona` → `Cliente`, `Entrenador`; `Plan` → `PlanMensual`, `PlanAnual`.
- **Polimorfismo:** `calcularFechaFin()` y `precioFinal()` se comportan distinto según el tipo de plan.
- **Clases abstractas:** `Persona` y `Plan`.
- **Colecciones:** `HashMap` (suscripción activa por cliente), `TreeMap` (agenda por entrenador), `Queue` (avisos de vencimiento), `ArrayList`.
- **Concurrencia:** `NotificacionVencimientoService` ejecuta un hilo en segundo plano que revisa el vencimiento de las suscripciones sin bloquear la API.
- **Arquitectura por capas:** `model/entity`, `dto`, `dao`, `service`, `controller`.
- **Persistencia:** archivos `.dat` mediante `DatFileStorage<T>` (serialización de objetos).

## Equipo

| Integrante | Rol Scrum | Rol técnico |
|---|---|---|
| Brayan Enrique Alfaro Guzmán | Product Owner | Analista / negocio |
| Josué Ernesto Zelaya Carballo | Scrum Master | Coordinador / QA de proceso |
| Kelly Alejandra Rodríguez Alvarado | Developer | Frontend (Vue.js) |
| Carlos Javier Alfaro Viera | Developer | Backend / persistencia (.dat) |
| Rubén Eduardo Estupinian Ávila | QA | Pruebas funcionales |
