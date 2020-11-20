-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (2, 'M20', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'CEEA', 2, true);

-- Tipo Memoria
INSERT INTO ETI.TIPO_MEMORIA
  (ID,NOMBRE,ACTIVO)
VALUES
  (1, 'Nueva', true);

-- Tipo actividad
INSERT INTO ETI.TIPO_ACTIVIDAD
  (ID,NOMBRE,ACTIVO)
VALUES
  (1, 'Proyecto de investigación', true);


-- Peticion Evaluacion
INSERT INTO ETI.PETICION_EVALUACION
  (ID,SOLICITUD_CONVOCATORIA_REF,CODIGO,TITULO,TIPO_ACTIVIDAD_ID,FUENTE_FINANCIACION,FECHA_INICIO,FECHA_FIN,RESUMEN,VALOR_SOCIAL,OBJETIVOS,DIS_METODOLOGICO,EXTERNO,TIENE_FONDOS_PROPIOS,PERSONA_REF,ACTIVO)
VALUES
  (1, 'Ref solicitud convocatoria', 'Código PeticionEvaluacion1', 'PeticionEvaluacion1', 1, 'Fuente financiación', '2020-07-09', '2021-07-09', 'Resumen', ' valor social', 'Objetivos', 'Diseño metodológico', false, false, 'user-001', true);

-- Tipo Estado Memoria
INSERT INTO ETI.TIPO_ESTADO_MEMORIA
  (ID,NOMBRE,ACTIVO)
VALUES
  (11, 'Completada seguimiento anual', true);


-- ESTADO RETROSPECTIVA
INSERT INTO ETI.ESTADO_RETROSPECTIVA
  (ID, NOMBRE, ACTIVO)
VALUES(1, 'EstadoRetrospectiva01', true);

-- RETROSPECTIVA
INSERT INTO ETI.RETROSPECTIVA
  (ID, ESTADO_RETROSPECTIVA_ID, FECHA_RETROSPECTIVA)
VALUES(4, 1, '2020-07-01');

-- Memoria
INSERT INTO ETI.MEMORIA
  (ID,NUM_REFERENCIA,PETICION_EVALUACION_ID,COMITE_ID,TITULO,PERSONA_REF,TIPO_MEMORIA_ID,ESTADO_ACTUAL_ID,FECHA_ENVIO_SECRETARIA,REQUIERE_RETROSPECTIVA,RETROSPECTIVA_ID,VERSION,ACTIVO)
VALUES
  (1, 'ref-134', 1, 2, 'Memoria1', 'user-8984', 1, 11, '2020-07-09', true, 4, 2, true);

-- Cargo comite
INSERT INTO ETI.CARGO_COMITE
  (ID,NOMBRE,ACTIVO)
VALUES
  (1, 'PRESIDENTE', true);
INSERT INTO ETI.CARGO_COMITE
  (ID,NOMBRE,ACTIVO)
VALUES
  (2, 'VOCAL', true);

-- Dictamen
INSERT INTO ETI.DICTAMEN
  (ID,NOMBRE,TIPO_EVALUACION_ID,ACTIVO)
VALUES
  (1, 'Favorable', NULL, true);

-- Tipo Convocatoria Reunión
INSERT INTO ETI.TIPO_CONVOCATORIA_REUNION
  (ID,NOMBRE,ACTIVO)
VALUES
  (1, 'Ordinaria', true);

-- Convocatoria Reunión
INSERT INTO ETI.CONVOCATORIA_REUNION
  (ID,COMITE_ID,FECHA_EVALUACION,FECHA_LIMITE,LUGAR,ORDEN_DIA,ANIO,NUMERO_ACTA,TIPO_CONVOCATORIA_REUNION_ID,HORA_INICIO,MINUTO_INICIO,FECHA_ENVIO,ACTIVO)
VALUES
  (1, 2, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 1', 2020, 1, 1, 8, 30, '2020-07-13', true);

-- Tipo evaluación
INSERT INTO ETI.TIPO_EVALUACION
  (ID,NOMBRE,ACTIVO)
VALUES
  (3, 'Seguimiento anual', true);

-- EVALUADOR
INSERT INTO eti.evaluador
  (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES
  (1, 'Evaluador1', 2, 1, '2020-07-01', '2021-07-01', 'user-006', true);
INSERT INTO eti.evaluador
  (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES
  (2, 'Evaluador2', 2, 1, '2020-07-01', '2021-07-01', 'user-002', true);

-- EVALUACION
INSERT INTO eti.evaluacion
  (id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima, evaluador1_id, evaluador2_id, fecha_dictamen, version, activo)
VALUES(1, 1, 1, 1, 3, true, 1, 2, '2020-08-01', 2, true);
