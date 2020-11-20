-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria
  (id, nombre, activo)
VALUES
  (1, 'TipoMemoria1', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad
  (id, nombre, activo)
VALUES
  (1, 'TipoActividad1', true);

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (1, 'M10', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (2, 'M20', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (3, 'M30', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (3, 'Comite3', 3, true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo1', 'Referencia solicitud convocatoria', 1, 'Fuente financiación', '2020-08-01', '2020-08-01', 'Resumen',  'valor social', 'Objetivos1', 'DiseñoMetodologico1', false, false, 'user-001', true);

-- TIPO ESTADO MEMORIA 
INSERT INTO eti.tipo_estado_memoria
  (id, nombre, activo)
VALUES
  (1, 'En elaboración', true);

-- ESTADO RETROSPECTIVA
INSERT INTO ETI.ESTADO_RETROSPECTIVA
  (ID, NOMBRE, ACTIVO)
VALUES(1, 'Pendiente', true);

-- RETROSPECTIVA
INSERT INTO ETI.RETROSPECTIVA
  (ID, ESTADO_RETROSPECTIVA_ID, FECHA_RETROSPECTIVA)
VALUES(1, 1, '2020-08-01');

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, cod_organo_competente, activo)
 VALUES (1, 'numRef-001', 1, 1, 'Memoria001', 'user-001', 1, 1, '2020-08-01', false, 1, 3, 'CodOrganoCompetente', true);

-- TIPO EVALUACION
INSERT INTO eti.tipo_evaluacion
  (id, nombre, activo)
VALUES
  (1, 'TipoEvaluacion1', true);

-- DICTAMEN
INSERT INTO eti.dictamen
  (id, nombre, tipo_evaluacion_id, activo)
VALUES
  (1, 'Dictamen1', null, true);


-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion
  (id, nombre, activo)
VALUES
  (1, 'Ordinaria', true);
INSERT INTO eti.tipo_convocatoria_reunion
  (id, nombre, activo)
VALUES
  (2, 'Extraordinaria', true);
INSERT INTO eti.tipo_convocatoria_reunion
  (id, nombre, activo)
VALUES
  (3, 'Seguimiento', true);

-- CONVOCATORIA REUNION
INSERT INTO ETI.CONVOCATORIA_REUNION
  (ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, ANIO, NUMERO_ACTA, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 01', 'Orden del día convocatoria reunión 01', 2020, 1, 1, 8, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
  (ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, ANIO, NUMERO_ACTA, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(2, 1, '2020-07-02 00:00:00.000', '2020-08-02', 'Lugar 02', 'Orden del día convocatoria reunión 2', 2020, 2, 2, 9, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
  (ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, ANIO, NUMERO_ACTA, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(3, 2, '2020-07-03 00:00:00.000', '2020-08-03', 'Lugar 03', 'Orden del día convocatoria reunión 3', 2020, 1, 1, 10, 30, '2020-07-13', true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador2', 1, 1, '2020-07-01', '2021-07-01', 'user-002', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(1, 1, 1, 1, 1, false, 1, 2, '2020-08-01', 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(2, 1, 1, 2, 1, false, 1, 2, '2020-08-01', 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(3, 1, 1, 1, 1, false, 1, 2, '2020-08-01', 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(4, 1, 1, 2, 1, false, 1, 2, '2020-08-01', 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(5, 1, 1, 1, 1, false, 1, 2, '2020-08-01', 2, true);
