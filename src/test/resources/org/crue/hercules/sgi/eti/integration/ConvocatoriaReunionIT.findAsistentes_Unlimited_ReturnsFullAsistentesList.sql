-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (1, 'M10', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);

-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (1, 'Ordinaria', true);

-- CONVOCATORIA REUNION
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, ANIO, NUMERO_ACTA, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(1, 1, '2020-07-01 00:00:00', '2020-08-01', 'Lugar 01', 'Orden del día convocatoria reunión 01', 2020, 1, 1, 8, 30, '2020-07-13', true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);

-- ASISTENTES
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (1,  1, 1,  'Motivo1', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (2,  1, 1,  'Motivo2', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (3,  1, 1,  'Motivo3', true);