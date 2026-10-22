-- =====================================================================
-- Datos de demostracion. Re-ejecutable (ON CONFLICT DO NOTHING).
--   psql -U gimnasio_app -d gimnasio -f database/data.sql
-- Las fechas de suscripciones son relativas a la fecha de ejecucion para
-- que la demo del hilo de vencimientos (HU-007) siempre tenga casos.
-- Las contrasenas de usuario son SOLO de demostracion.
-- =====================================================================

INSERT INTO plan (id_plan, nombre, tipo, precio_base, descuento) VALUES
    ('PLAN-MEN-01', 'Plan Mensual', 'MENSUAL', 25.00, 0.000),
    ('PLAN-ANU-01', 'Plan Anual',   'ANUAL',   25.00, 0.150)
ON CONFLICT (id_plan) DO NOTHING;

INSERT INTO cliente (id_cliente, nombre, apellido, telefono, correo, fecha_registro) VALUES
    ('CLI-DEMO0001', 'Josue',  'Zelaya',   '66802552', 'jzelaya@example.com',  CURRENT_DATE - 60),
    ('CLI-DEMO0002', 'Maria',  'Lopez',    '70112233', 'mlopez@example.com',   CURRENT_DATE - 45),
    ('CLI-DEMO0003', 'Carlos', 'Mendoza',  '71223344', 'cmendoza@example.com', CURRENT_DATE - 20),
    ('CLI-DEMO0004', 'Ana',    'Ramirez',  '72334455', NULL,                   CURRENT_DATE - 5)
ON CONFLICT (id_cliente) DO NOTHING;

INSERT INTO entrenador (id_entrenador, nombre, apellido, telefono, correo, especialidad, turno) VALUES
    ('ENT-DEMO0001', 'Pepe',  'Hernandez', '12345678', 'pepe@example.com',  'Fuerza',     'MATUTINO'),
    ('ENT-DEMO0002', 'Laura', 'Campos',    '73445566', 'laura@example.com', 'Cardio',     'VESPERTINO')
ON CONFLICT (id_entrenador) DO NOTHING;

-- SUS-DEMO0001: activa y vigente.
-- SUS-DEMO0002: activa que vence en 2 dias -> el hilo la deja en la cola de avisos.
-- SUS-DEMO0003: marcada ACTIVA pero ya vencida -> el hilo debe pasarla a VENCIDA.
-- SUS-DEMO0004: ya VENCIDA.
INSERT INTO suscripcion (id_suscripcion, id_cliente, id_plan, fecha_inicio, fecha_fin, estado) VALUES
    ('SUS-DEMO0001', 'CLI-DEMO0001', 'PLAN-ANU-01', CURRENT_DATE - 30,  CURRENT_DATE + 335, 'ACTIVA'),
    ('SUS-DEMO0002', 'CLI-DEMO0002', 'PLAN-MEN-01', CURRENT_DATE - 28,  CURRENT_DATE + 2,   'ACTIVA'),
    ('SUS-DEMO0003', 'CLI-DEMO0003', 'PLAN-MEN-01', CURRENT_DATE - 36,  CURRENT_DATE - 6,   'ACTIVA'),
    ('SUS-DEMO0004', 'CLI-DEMO0004', 'PLAN-MEN-01', CURRENT_DATE - 90,  CURRENT_DATE - 60,  'VENCIDA')
ON CONFLICT (id_suscripcion) DO NOTHING;

INSERT INTO pago (id_pago, id_suscripcion, fecha, monto, metodo_pago) VALUES
    ('PAG-DEMO0001', 'SUS-DEMO0001', CURRENT_TIMESTAMP - INTERVAL '30 days', 255.00, 'TARJETA'),
    ('PAG-DEMO0002', 'SUS-DEMO0002', CURRENT_TIMESTAMP - INTERVAL '28 days',  25.00, 'EFECTIVO'),
    ('PAG-DEMO0003', 'SUS-DEMO0003', CURRENT_TIMESTAMP - INTERVAL '36 days',  25.00, 'EFECTIVO'),
    ('PAG-DEMO0004', 'SUS-DEMO0004', CURRENT_TIMESTAMP - INTERVAL '90 days',  25.00, 'EFECTIVO')
ON CONFLICT (id_pago) DO NOTHING;

INSERT INTO sesion (id_sesion, id_cliente, id_entrenador, hora_inicio, hora_fin, estado) VALUES
    ('SES-DEMO0001', 'CLI-DEMO0001', 'ENT-DEMO0001', date_trunc('day', now()) + INTERVAL '1 day 9 hours',  date_trunc('day', now()) + INTERVAL '1 day 10 hours', 'PROGRAMADA'),
    ('SES-DEMO0002', 'CLI-DEMO0002', 'ENT-DEMO0002', date_trunc('day', now()) + INTERVAL '1 day 17 hours', date_trunc('day', now()) + INTERVAL '1 day 18 hours', 'PROGRAMADA')
ON CONFLICT (id_sesion) DO NOTHING;

INSERT INTO acceso (id_acceso, id_cliente, fecha_hora, codigo_qr, estado) VALUES
    ('ACC-DEMO0001', 'CLI-DEMO0001', CURRENT_TIMESTAMP - INTERVAL '2 hours',  'QR-DEMO-0001', 'PERMITIDO'),
    ('ACC-DEMO0002', 'CLI-DEMO0004', CURRENT_TIMESTAMP - INTERVAL '1 hour',   'QR-DEMO-0004', 'DENEGADO')
ON CONFLICT (id_acceso) DO NOTHING;

INSERT INTO usuario (id_usuario, usuario, contrasena, rol, estado) VALUES
    ('USR-DEMO0001', 'admin',        'admin123',  'ADMINISTRADOR', 'ACTIVO'),
    ('USR-DEMO0002', 'recepcion',    'recep123',  'RECEPCIONISTA', 'ACTIVO'),
    ('USR-DEMO0003', 'entrenador1',  'entre123',  'ENTRENADOR',    'ACTIVO')
ON CONFLICT (id_usuario) DO NOTHING;
