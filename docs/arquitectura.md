# Arquitectura y UML actualizado - Avance 3

Este archivo sirve como fuente versionable para los diagramas que deben
incorporarse al documento académico final.

## Componentes

```mermaid
flowchart LR
    UI[Vue 3 / Axios] -->|HTTP JSON| C[REST Controllers]
    C --> S[Services]
    S --> D[DAO interfaces]
    D --> I[JDBC DAO implementations]
    I --> B[BaseJdbcDAO]
    B --> P[Tomcat JDBC Pool]
    P --> DB[(PostgreSQL 16)]
    N[ScheduledExecutorService] --> S
    N --> D
```

Los controladores solo traducen HTTP; los servicios contienen reglas de
negocio; los DAO abstraen la persistencia; `DataSourceConfig` administra el
pool; y PostgreSQL conserva la integridad referencial y los traslapes.

## Modelo de dominio

```mermaid
classDiagram
    class Persona {
        <<abstract>>
        -String idPersona
        -String nombre
        -String apellido
        -String telefono
        -String correo
        +mostrarInformacion()* String
    }
    class Cliente {
        -LocalDate fechaRegistro
    }
    class Entrenador {
        -String especialidad
        -String turno
    }
    Persona <|-- Cliente
    Persona <|-- Entrenador

    class Plan {
        <<abstract>>
        -String idPlan
        -String nombre
        -double precioBase
        +calcularFechaFin(LocalDate)* LocalDate
        +precioFinal()* double
    }
    class PlanMensual
    class PlanAnual {
        -double descuento
    }
    Plan <|-- PlanMensual
    Plan <|-- PlanAnual

    class Suscripcion {
        -String idSuscripcion
        -LocalDate fechaInicio
        -LocalDate fechaFin
        -String estado
    }
    class Pago {
        -String idPago
        -LocalDateTime fecha
        -double monto
    }
    class Sesion {
        -String idSesion
        -LocalDateTime horaInicio
        -LocalDateTime horaFin
        -String estado
    }
    class Acceso {
        -String idAcceso
        -LocalDateTime fechaHora
        -String codigoQR
        -String estado
    }

    Cliente "1" --> "0..*" Suscripcion
    Plan "1" --> "0..*" Suscripcion
    Suscripcion "1" --> "1" Pago
    Cliente "1" --> "0..*" Sesion
    Entrenador "1" --> "0..*" Sesion
    Cliente "1" --> "0..*" Acceso
```

## Flujo transaccional de una venta

```mermaid
sequenceDiagram
    participant UI as Vue
    participant C as SuscripcionController
    participant S as SuscripcionService
    participant SD as SuscripcionDAO
    participant PD as PagoDAO
    participant DB as PostgreSQL

    UI->>C: POST /api/suscripciones
    C->>S: venderPlan(dto)
    Note over S,DB: @Transactional
    S->>SD: guardar(suscripcion)
    SD->>DB: INSERT suscripcion
    S->>PD: guardar(pago)
    PD->>DB: INSERT pago
    alt ambas operaciones finalizan
        DB-->>S: COMMIT
        S-->>UI: 201 Created
    else ocurre una excepción
        DB-->>S: ROLLBACK
        S-->>UI: respuesta segura de error
    end
```

## Decisiones de cohesión y acoplamiento

- Los servicios dependen de interfaces DAO.
- Los DAO dependen de `javax.sql.DataSource`, no del pool concreto.
- `JdbcEntityMapper<T>` separa el mapeo de filas del recorrido del `ResultSet`.
- `IdGenerator` y `TextoUtils` eliminan lógica duplicada entre servicios.
- `Clock` se inyecta para separar la hora del sistema de las reglas y pruebas.
