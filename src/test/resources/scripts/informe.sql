-- DEPENDENCIAS: INFORME
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_actividad.sql",
    "classpath:scripts/tipo_memoria.sql", 
    "classpath:scripts/tipo_estado_memoria.sql",
    "classpath:scripts/estado_retrospectiva.sql",
    "classpath:scripts/retrospectiva.sql",
    "classpath:scripts/tipo_evaluacion.sql",
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);     

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (1, 'ref-5588', 1, 2, 'Memoria001', 'userref-55698', 1, 1, null, false, 3, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (2, 'ref-3534', 1, 2, 'Memoria002', 'userref-5698', 1, 1, null, false, 3, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (3, 'ref-657', 1, 2, 'Memoria003', 'userref-757', 1, 1, null, false, 3, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (4, 'ref-4698', 1, 2, 'Memoria4', 'userref-654', 1, 1, null, false, 3, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (5, 'ref-4657', 1, 2, 'Memoria5', 'userref-777', 1, 1, null, false, 3, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (6, 'ref-6658', 1, 2, 'Memoria6', 'userref-55465', 1, 1, null, false, 3, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (7, 'ref-3635', 1, 2, 'Memoria7', 'userref-4444', 1, 1, null, false, 3, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (8, 'ref-777', 1, 2, 'Memoria8', 'userref-5555', 1, 1, null, false, 3, 1, true);

-- INFORME
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (1, 1, 'DocumentoFormulario1', 1, 1);
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (2, 2, 'DocumentoFormulario2', 2, 1);
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (3, 3, 'DocumentoFormulario3', 3, 1);
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (4, 4, 'DocumentoFormulario4', 4, 1);
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (5, 5, 'DocumentoFormulario5', 5, 1);
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (6, 6, 'DocumentoFormulario6', 6, 1);
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (7, 7, 'DocumentoFormulario7', 7, 1);
INSERT INTO eti.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (8, 8, 'DocumentoFormulario8', 8, 1);
