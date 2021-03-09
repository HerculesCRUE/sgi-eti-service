-- DEPENDENCIAS: ASISTENTES
/*
  scripts = { 
    "classpath:scripts/formulario.sql", 
    "classpath:scripts/tipo_convocatoria_reunion.sql",
    "classpath:scripts/cargo_comite.sql",
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);

-- CONVOCATORIA REUNION
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 01', 'Orden del día convocatoria reunión 01', 2020, 1, 1, 8, 30, '2020-07-13', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);

-- ASISTENTES
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (2,  1, 1,  'Motivo2', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (3,  1, 1,  'Motivo3', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (4,  1, 1,  'Motivo4', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (5,  1, 1,  'Motivo5', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (6,  1, 1,  'Motivo6', true);