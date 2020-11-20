-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (1, 'M10', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'CEISH', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'CEEA', 1, true);


-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'Nueva', true);
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (2, 'Modificada', true);
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (3, 'Ratificación', true);

-- TIPO MEMORIA COMITE
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (1, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (2, 1, 2);


-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 'valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- ESTADO RETROSPECTIVA
INSERT INTO ETI.ESTADO_RETROSPECTIVA
(ID, NOMBRE, ACTIVO)
VALUES(1, 'EstadoRetrospectiva01', true);


-- RETROSPECTIVA
INSERT INTO ETI.RETROSPECTIVA
(ID, ESTADO_RETROSPECTIVA_ID, FECHA_RETROSPECTIVA)
VALUES(1, 1, '2020-07-01');

 -- TIPO ESTADO MEMORIA 
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (1, 'En elaboración', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (1, 'ref-5588', 1, 2, 'Memoria1', 'userref-55698', 1, 1, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (2, 'ref-3534', 1, 2, 'Memoria2', 'userref-5698', 1, 1, null, false, 1, 1, true, null);
