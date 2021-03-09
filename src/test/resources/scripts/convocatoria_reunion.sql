-- DEPENDENCIAS: CONVOCATORIA_REUNION
/*
  scripts = { 
    "classpath:scripts/tipo_memoria.sql", 
    "classpath:scripts/tipo_actividad.sql",
    "classpath:scripts/formulario.sql", 
    "classpath:scripts/tipo_estado_memoria.sql",
    "classpath:scripts/estado_retrospectiva.sql", 
    "classpath:scripts/tipo_evaluacion.sql",
    "classpath:scripts/tipo_convocatoria_reunion.sql", 
    "classpath:scripts/cargo_comite.sql",
    "classpath:scripts/tipo_estado_acta.sql"
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (3, 'Comite3', 3, true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo1', 'Referencia solicitud convocatoria', 1, 'Fuente financiación', '2020-08-01', '2020-08-01', 'Resumen',  'valor social', 'Objetivos1', 'DiseñoMetodologico1', false, false, 'user-001', true);

-- RETROSPECTIVA
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(1, 1, '2020-08-01');

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, cod_organo_competente, activo)
 VALUES (1, 'numRef-001', 1, 1, 'Memoria001', 'user-001', 1, 1, '2020-08-01', false, 1, 3, 'CodOrganoCompetente', true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (2, 'ref-3534', 1, 1, 'Memoria2', 'userref-5698', 1, 1, null, false, 1, 1, true);

-- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (1, 'Dictamen1', null, true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (2, 'Dictamen2', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo) 
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador2', 1, 1, '2020-07-01', '2021-07-01', 'user-002', true);

-- CONVOCATORIA REUNION
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(2, 1, '2020-07-02 00:00:00.000', '2020-08-02', 'Lugar 2', 'Orden del día convocatoria reunión 2', 2020, 2, 2, 9, 30, '2020-07-13', true);
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(3, 2, '2020-07-03 00:00:00.000', '2020-08-03', 'Lugar 03', 'Orden del día convocatoria reunión 03', 2020, 3, 1, 10, 30, '2020-07-13', true);
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(4, 2, '2020-07-04 00:00:00.000', '2020-08-04', 'Lugar 4', 'Orden del día convocatoria reunión 4', 2020, 4, 2, 11, 30, '2020-07-13', true);
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(5, 3, '2020-07-05 00:00:00.000', '2020-08-05', 'Lugar 05', 'Orden del día convocatoria reunión 05', 2020, 5, 1, 12, 30, '2020-07-13', true);

-- ASISTENTES
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia) 
VALUES (1,  1, 2,  'Motivo1', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (2,  1, 2,  'Motivo2', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (3,  1, 2,  'Motivo3', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(1, 1, 1, 2, 1, false, 1, 2, '2020-08-01', 2, true); --borrar
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(2, 1, 1, 3, 1, false, 1, 2, '2020-08-01', 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(3, 1, 1, 2, 1, false, 1, 2, '2020-08-01', 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(4, 1, 1, 3, 1, false, 1, 2, '2020-08-01', 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima,  evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(5, 1, 1, 2, 1, false, 1, 2, '2020-08-01', 2, true);

-- ###############

-- ACTA
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (1, 2, 10, 15, 12, 0, 'Resumen123', 123, 1, true, true); 

