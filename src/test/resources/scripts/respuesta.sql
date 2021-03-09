-- DEPENDENCIAS: RESPUESTA
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_actividad.sql", 
    "classpath:scripts/tipo_memoria.sql",
    "classpath:scripts/tipo_estado_memoria.sql",
    "classpath:scripts/estado_retrospectiva.sql",
    "classpath:scripts/bloque.sql",
    "classpath:scripts/apartado.sql",
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- RETROSPECTIVA
insert into eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) values(1, 1, '2020-07-01');        

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (1, 'ref-5588', 1, 1, 'Memoria1', 'userref-55698', 1, 1, null, false, 1, 1, true);

-- RESPUESTA FORMULARIO
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (2, 1, 1, '{"valor":"Valor2"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (3, 1, 1, '{"valor":"Valor3"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (4, 1, 1, '{"valor":"Valor4"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (5, 1, 1, '{"valor":"Valor5"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (6, 1, 1, '{"valor":"Valor6"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (7, 1, 1, '{"valor":"Valor7"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (8, 1, 1, '{"valor":"Valor8"}');
