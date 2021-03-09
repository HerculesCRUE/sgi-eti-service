-- DEPENDENCIAS: EVALUACIÓN
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/bloque.sql",
    "classpath:scripts/apartado.sql",
    "classpath:scripts/tipo_actividad.sql",
    "classpath:scripts/tipo_memoria.sql",
    "classpath:scripts/tipo_estado_memoria.sql",
    "classpath:scripts/estado_retrospectiva.sql",
    "classpath:scripts/tipo_convocatoria_reunion.sql",
    "classpath:scripts/tipo_evaluacion.sql",
    "classpath:scripts/cargo_comite.sql",
    "classpath:scripts/tipo_comentario.sql",
  }
*/
-- COMITÉ
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'CEISH', 1, true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES (1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 'valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- RETROSPECTIVA
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(1, 1, '2020-07-01');  
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(2, 4, '2020-07-01');  

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (1, 'ref-5588', 1, 1, 'Memoria1', 'userref-55698', 1, 1, null, false, 1, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (2, 'ref-3534', 1, 1, 'Memoria2', 'userref-5698', 1, 1, null, false, 1, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (3, 'ref-3534', 1, 1, 'Memoria3', 'userref-5698', 1, 1, null, false, 2, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (4, 'ref-5588', 1, 1, 'Memoria4', 'userref-55698', 1, 1, null, false, 2, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (5, 'ref-5588', 1, 1, 'Memoria5', 'userref-55698', 1, 1, null, false, 2, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (6, 'ref-5588', 1, 1, 'Memoria6', 'userref-55698', 1, 13, null, false, 1, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)	
 VALUES (7, 'ref-5588', 1, 1, 'Memoria7', 'userref-55698', 1, 1, null, false, 2, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (8, 'ref-5588', 1, 1, 'Memoria8', 'userref-55698', 1, 1, null, false, 2, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (9, 'ref-4657', 1, 1, 'Memoria9', 'userref-777', 1, 1, null, false, 2, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (10, 'ref-4657', 1, 1, 'Memoria10', 'userref-777', 1, 19, null, true, 1, 1, true);

-- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (1, 'Dictamen1', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (2, 'Dictamen2', true);

-- CONVOCATORIA REUNION
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 1', 2020, 1, 1, 8, 30, '2020-07-13', true);
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(2, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 2', 'Orden del día convocatoria reunión 2', 2020, 1, 1, 8, 30, '2020-07-13', true);
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(3, 1, '2040-10-01 00:00:00.000', '2020-12-01', 'Lugar 01', 'Orden del día convocatoria reunión 0', 2020, 1, 1, 8, 30, '2020-10-13', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador2', 1, 1, '2020-07-01', '2021-07-01', 'user-002', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo)
 VALUES(2, 1, 1, 1, 1, 1, 2, '2020-07-10', 3, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo)
 VALUES(3, 4, 1, 2, 1, 1, 2, '2020-07-13', 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(4, 5, 1, 2, 1, 1, 2, '2020-07-13', 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(5, 1, 1, 1, 1, 1, 2, '2020-07-10', 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(6, 1, 1, 1, 1, 1, 2, '2020-07-13', 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(7, 6, 1, 2, 1, 1, 2, '2020-07-13', 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(8, 7, 1, 2, 1, 1, 2, '2020-07-13', 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, version, es_rev_minima, activo) 
 VALUES(9, 8, 3, 1, null, 1, 2, 2, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo)  
 VALUES(10, 9, 3, 1, 2, 1, 2, '2020-08-01', 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(11, 10, 1, 3, 1, 1, 2, '2020-07-13', 1, true, true);

-- COMENTARIO
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (3, 1, 1, 3, 2, 'Comentario3');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (4, 1, 1, 6, 1, 'Comentario4');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (5, 1, 1, 6, 1, 'Comentario5');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (6, 6, 1, 7, 1, 'Comentario6');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (7, 7, 1, 8, 1, 'Comentario7');


