-- DEPENDENCIAS: COMITÉ
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
  }
*/

-- COMITÉ
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (3, 'Comite3', 3, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (4, 'Comite4', 4, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (5, 'Comite5', 5, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (6, 'Comite6', 6, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (7, 'Comite7', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (8, 'Comite8', 2, true);

-- TIPO MEMORIA COMITE
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (1, 2, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (2, 2, 2);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (3, 2, 3);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 'valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- RETROSPECTIVA
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(1, 1, '2020-07-01');

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (1, 'ref-5588', 1, 2, 'Memoria1', 'userref-55698', 2, 1, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (2, 'ref-3534', 1, 2, 'Memoria2', 'userref-5698', 2, 1, null, false, 1, 1, true, null);