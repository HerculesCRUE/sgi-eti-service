-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- EQUIPO TRABAJO
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (1, 1, 'user-1');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (2, 1, 'user-2');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (3, 1, 'user-3');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (4, 1, 'user-4');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (5, 1, 'user-5');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (6, 1, 'user-6');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (7, 1, 'user-7');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (8, 1, 'user-8');