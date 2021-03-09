-- DEPENDENCIAS: EVALUADOR
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_actividad.sql", 
    "classpath:scripts/tipo_memoria.sql",
    "classpath:scripts/estado_retrospectiva.sql",
    "classpath:scripts/tipo_convocatoria_reunion.sql",
    "classpath:scripts/tipo_evaluacion.sql",
    "classpath:scripts/formacion_especifica.sql",
    "classpath:scripts/tipo_tarea.sql"
    "classpath:scripts/tipo_estado_memoria.sql",
    }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador2', 1, 1, '2020-07-01', '2021-07-01', 'user-002', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (3, 'Evaluador3', 1, 1, '2020-07-01', '2021-07-01', 'user-003', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (4, 'Evaluador4', 1, 1, '2020-07-01', '2021-07-01', 'user-004', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (5, 'Evaluador5', 1, 1, '2020-07-01', '2021-07-01', 'user-005', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (6, 'Evaluador6', 1, 1, '2020-07-01', '2021-07-01', 'user-006', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (7, 'Evaluador7', 1, 1, '2020-07-01', '2021-07-01', 'user-007', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (8, 'Evaluador8', 1, 1, '2020-07-01', '2021-07-01', 'user-008', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- EQUIPO TRABAJO
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (100, 1, 'user-001');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (200, 1, 'user-002');

-- RETROSPECTIVA
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(1, 1, '2020-07-01');  
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(2, 1, '2020-07-01');      

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (100, 'ref-5588', 1, 1, 'Memoria001', 'userref-55697', 1, 11, null, false, 2, 2, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (200, 'ref-5588', 1, 1, 'Memoria002', 'userref-55698', 1, 1, null, false, 1, 2, true);

-- Dictamen
INSERT INTO eti.dictamen (id,nombre,tipo_evaluacion_id,activo) VALUES (1, 'Favorable', NULL, true);

-- Convocatoria Reunión
INSERT INTO eti.convocatoria_reunion 
(id,comite_id,fecha_evaluacion,fecha_limite,lugar,orden_dia,anio,numero_acta,tipo_convocatoria_reunion_id,hora_inicio,minuto_inicio,fecha_envio,activo)
VALUES (1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 1', 2020, 1, 1, 8, 30, '2020-07-13', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima, evaluador1_id, evaluador2_id, fecha_dictamen, version, activo) VALUES(1, 100, 1, 1, 3, true, 6, 7, '2020-08-01', 2, true);

-- TAREA
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (1, 100, 100, 'Tarea1', 'Formacion1', 1, 'Organismo1', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (2, 200, 200, 'Tarea2', 'Formacion2', 1, 'Organismo2', 2020, 1);