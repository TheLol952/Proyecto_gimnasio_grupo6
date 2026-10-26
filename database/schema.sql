-- =====================================================================
-- Sistema de Gestion Administrativa de un Gimnasio - Esquema PostgreSQL
-- Ejecutar conectado a la base "gimnasio":
--   psql -U gimnasio_app -d gimnasio -f database/schema.sql
-- Es re-ejecutable: usa IF NOT EXISTS y no borra datos.
-- Los IDs son VARCHAR porque el codigo los genera como texto
-- (CLI-XXXXXXXX, ENT-..., PLAN-..., SUS-..., PAG-..., SES-..., ACC-...).
-- =====================================================================

-- Necesaria para la restriccion anti-traslape de sesiones (ver tabla sesion).
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- ---------------------------------------------------------------------
-- cliente  (entidad Cliente, hereda de Persona)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente      VARCHAR(20)  PRIMARY KEY,
    nombre          VARCHAR(80)  NOT NULL,
    apellido        VARCHAR(80)  NOT NULL,
    telefono        VARCHAR(15),
    correo          VARCHAR(120),
    fecha_registro  DATE         NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT ck_cliente_nombre   CHECK (btrim(nombre)   <> ''),
    CONSTRAINT ck_cliente_apellido CHECK (btrim(apellido) <> ''),
    -- Mismos formatos que valida Persona.java, para que la BD nunca guarde
    -- algo que el modelo rechazaria al leerlo.
    CONSTRAINT ck_cliente_telefono CHECK (telefono IS NULL OR telefono ~ '^[0-9+ -]{7,15}$'),
    CONSTRAINT ck_cliente_correo   CHECK (correo   IS NULL OR correo   ~ '^[A-Za-z0-9_.+-]+@[A-Za-z0-9_-]+\.[A-Za-z]{2,}$')
);

-- ---------------------------------------------------------------------
-- entrenador  (entidad Entrenador, hereda de Persona)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS entrenador (
    id_entrenador   VARCHAR(20)  PRIMARY KEY,
    nombre          VARCHAR(80)  NOT NULL,
    apellido        VARCHAR(80)  NOT NULL,
    telefono        VARCHAR(15),
    correo          VARCHAR(120),
    especialidad    VARCHAR(80)  NOT NULL,
    turno           VARCHAR(30)  NOT NULL,
    CONSTRAINT ck_entrenador_nombre   CHECK (btrim(nombre)   <> ''),
    CONSTRAINT ck_entrenador_apellido CHECK (btrim(apellido) <> ''),
    CONSTRAINT ck_entrenador_telefono CHECK (telefono IS NULL OR telefono ~ '^[0-9+ -]{7,15}$'),
    CONSTRAINT ck_entrenador_correo   CHECK (correo   IS NULL OR correo   ~ '^[A-Za-z0-9_.+-]+@[A-Za-z0-9_-]+\.[A-Za-z]{2,}$')
);

-- ---------------------------------------------------------------------
-- plan  (Plan abstracto -> PlanMensual / PlanAnual)
-- Una sola tabla con columna discriminadora "tipo".
-- descuento solo aplica a ANUAL (en MENSUAL queda en 0).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS plan (
    id_plan      VARCHAR(20)   PRIMARY KEY,
    nombre       VARCHAR(60)   NOT NULL,
    tipo         VARCHAR(10)   NOT NULL,
    precio_base  NUMERIC(10,2) NOT NULL,
    descuento    NUMERIC(4,3)  NOT NULL DEFAULT 0,
    CONSTRAINT ck_plan_tipo      CHECK (tipo IN ('MENSUAL', 'ANUAL')),
    CONSTRAINT ck_plan_precio    CHECK (precio_base >= 0),
    CONSTRAINT ck_plan_descuento CHECK (descuento >= 0 AND descuento <= 1)
);

-- ---------------------------------------------------------------------
-- suscripcion  (cliente compra un plan)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS suscripcion (
    id_suscripcion  VARCHAR(20) PRIMARY KEY,
    id_cliente      VARCHAR(20) NOT NULL REFERENCES cliente (id_cliente),
    id_plan         VARCHAR(20) NOT NULL REFERENCES plan (id_plan),
    fecha_inicio    DATE        NOT NULL,
    fecha_fin       DATE        NOT NULL,
    estado          VARCHAR(10) NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT ck_suscripcion_estado  CHECK (estado IN ('ACTIVA', 'VENCIDA')),
    CONSTRAINT ck_suscripcion_fechas  CHECK (fecha_fin >= fecha_inicio)
);
CREATE INDEX IF NOT EXISTS ix_suscripcion_cliente       ON suscripcion (id_cliente);
-- Ayuda al hilo de vencimientos (HU-007), que filtra por estado y fecha_fin.
CREATE INDEX IF NOT EXISTS ix_suscripcion_estado_fin    ON suscripcion (estado, fecha_fin);

-- ---------------------------------------------------------------------
-- pago  (cada venta de suscripcion genera un pago)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pago (
    id_pago         VARCHAR(20)   PRIMARY KEY,
    id_suscripcion  VARCHAR(20)   NOT NULL REFERENCES suscripcion (id_suscripcion),
    fecha           TIMESTAMP     NOT NULL,
    monto           NUMERIC(10,2) NOT NULL,
    metodo_pago     VARCHAR(30)   NOT NULL,
    CONSTRAINT ck_pago_monto CHECK (monto >= 0)
);
CREATE INDEX IF NOT EXISTS ix_pago_suscripcion ON pago (id_suscripcion);

-- ---------------------------------------------------------------------
-- sesion  (cita cliente - entrenador)
-- La restriccion EXCLUDE impide, a nivel de BD, dos sesiones no canceladas
-- del mismo entrenador con horarios traslapados. Complementa la validacion
-- de SesionService y cubre el caso de dos peticiones simultaneas.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sesion (
    id_sesion      VARCHAR(20) PRIMARY KEY,
    id_cliente     VARCHAR(20) NOT NULL REFERENCES cliente (id_cliente),
    id_entrenador  VARCHAR(20) NOT NULL REFERENCES entrenador (id_entrenador),
    hora_inicio    TIMESTAMP   NOT NULL,
    hora_fin       TIMESTAMP   NOT NULL,
    estado         VARCHAR(12) NOT NULL DEFAULT 'PROGRAMADA',
    CONSTRAINT ck_sesion_estado CHECK (estado IN ('PROGRAMADA', 'CANCELADA', 'REALIZADA')),
    CONSTRAINT ck_sesion_horas  CHECK (hora_fin > hora_inicio),
    CONSTRAINT ex_sesion_sin_traslape EXCLUDE USING gist (
        id_entrenador WITH =,
        tsrange(hora_inicio, hora_fin) WITH &&
    ) WHERE (estado <> 'CANCELADA')
);
CREATE INDEX IF NOT EXISTS ix_sesion_cliente ON sesion (id_cliente);

-- Impide registrar o reprogramar una sesion PROGRAMADA con una fecha pasada,
-- incluso cuando la escritura se realiza fuera de la API. Se usa un trigger
-- porque una comparacion con la hora actual no es un invariante permanente
-- apropiado para una restriccion CHECK.
CREATE OR REPLACE FUNCTION validar_sesion_programada_futura()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado = 'PROGRAMADA'
            AND NEW.hora_inicio <= (CURRENT_TIMESTAMP AT TIME ZONE 'America/El_Salvador') THEN
        RAISE EXCEPTION 'La hora de inicio de una sesion programada debe ser futura'
            USING ERRCODE = '22007';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sesion_programada_futura ON sesion;
CREATE TRIGGER trg_sesion_programada_futura
    BEFORE INSERT OR UPDATE OF hora_inicio, estado ON sesion
    FOR EACH ROW
    EXECUTE FUNCTION validar_sesion_programada_futura();

-- ---------------------------------------------------------------------
-- acceso  (intentos de ingreso por QR simulado, HU-003)
-- id_cliente NO tiene FOREIGN KEY a proposito: AccesoService tambien
-- registra como DENEGADO los intentos con un id que no existe, y ese
-- historial no debe perderse ni fallar.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS acceso (
    id_acceso   VARCHAR(20)  PRIMARY KEY,
    id_cliente  VARCHAR(50)  NOT NULL,
    fecha_hora  TIMESTAMP    NOT NULL,
    codigo_qr   VARCHAR(100),
    estado      VARCHAR(10)  NOT NULL,
    CONSTRAINT ck_acceso_estado CHECK (estado IN ('PERMITIDO', 'DENEGADO'))
);
CREATE INDEX IF NOT EXISTS ix_acceso_cliente ON acceso (id_cliente);

-- ---------------------------------------------------------------------
-- usuario  (cuentas por rol; enum Rol del modelo)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario  VARCHAR(20)  PRIMARY KEY,
    usuario     VARCHAR(50)  NOT NULL UNIQUE,
    contrasena  VARCHAR(100) NOT NULL,
    rol         VARCHAR(15)  NOT NULL,
    estado      VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT ck_usuario_rol    CHECK (rol IN ('ADMINISTRADOR', 'RECEPCIONISTA', 'ENTRENADOR', 'CLIENTE')),
    CONSTRAINT ck_usuario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);
