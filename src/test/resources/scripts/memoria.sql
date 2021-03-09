-- DEPENDENCIAS: MEMORIA
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_convocatoria_reunion.sql",
    "classpath:scripts/tipo_evaluacion.sql",
    "classpath:scripts/cargo_comite.sql",
    "classpath:scripts/tipo_actividad.sql",
    "classpath:scripts/tipo_memoria.sql",
    "classpath:scripts/estado_retrospectiva.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/tipo_estado_memoria.sql"
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'CEEA', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);

 -- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (1, 'Dictamen1', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (2, 'Dictamen2', true);

-- CONVOCATORIA REUNION
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 01', 2020, 1, 1, 8, 30, '2020-07-13', true);
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(2, 1, '2020-07-02 00:00:00.000', '2020-08-02', 'Lugar 2', 'Orden del día convocatoria reunión 2', 2020, 2, 2, 9, 30, '2020-07-13', true);
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(3, 1, '2020-07-03 00:00:00.000', '2020-08-03', 'Lugar 3', 'Orden del día convocatoria reunión 3', 2020, 3, 3, 10, 30, '2020-07-13', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(2, 'PeticionEvaluacion2', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(3, 'PeticionEvaluacion3', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(4, 'PeticionEvaluacion4', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(5, 'PeticionEvaluacion5', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(6, 'PeticionEvaluacion6', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(7, 'PeticionEvaluacion7', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(8, 'PeticionEvaluacion8', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- RETROSPECTIVA
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(1, 1, '2020-07-01');  
INSERT INTO eti.retrospectiva (id, estado_retrospectiva_id, fecha_retrospectiva) VALUES(2, 3, '2020-07-01');  

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (1, 'ref-001', 1, 1, 'Memoria001', 'userref-001', 1, 1, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (2, 'ref-002', 1, 1, 'Memoria002', 'userref-002', 1, 12, '2020-08-01', false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (3, 'ref-003', 1, 1, 'Memoria003', 'userref-003', 1, 1, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (4, 'ref-004', 1, 1, 'Memoria004', 'userref-004', 1, 17, '2020-08-01', false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (5, 'ref-005', 1, 1, 'Memoria005', 'userref-005', 1, 1, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (6, 'ref-006', 1, 1, 'Memoria006', 'userref-006', 1, 12, '2020-08-01', false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (7, 'ref-007', 1, 1, 'Memoria007', 'userref-007', 1, 1, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (8, 'ref-008', 1, 1, 'Memoria008', 'userref-008', 1, 1, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (9, 'ref-009', 1, 1, 'Memoria009', 'userref-009', 1, 2, null, true, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (10, 'ref-010', 1, 1, 'Memoria010', 'userref-010', 1, 3, '2020-08-01', false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (11, 'ref-011', 1, 1, 'Memoria011', 'userref-011', 1, 3, '2020-08-01', false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (12, 'ref-012', 1, 1, 'Memoria012', 'userref-012', 1, 3, '2020-08-01', false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (13, 'ref-013', 1, 1, 'Memoria013', 'userref-013', 1, 1, '2020-08-01', false, 2, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (14, 'ref-014', 1, 1, 'Memoria014', 'userref-014', 1, 3, '2020-09-01', false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (15, 'ref-015', 1, 1, 'Memoria015', 'userref-015', 1, 9, null, false, 1, 1, true, null);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (16, 'ref-016', 1, 1, 'Memoria016', 'userref-016', 1, 14, null, false, 1, 1, true, null);

-- DOCUMENTACIÓN MEMORIA
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (1, 1, 1, 'doc-001', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (2, 1, 2, 'doc-002', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (3, 1, 3, 'doc-003', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (4, 1, 4, 'doc-004', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (5, 1, 5, 'doc-005', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (6, 1, 6, 'doc-006', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (7, 1, 7, 'doc-007', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (8, 2, 3, 'doc-008', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (9, 3, 2, 'doc-009', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (10, 15, 1, 'doc-010', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (11, 16, 2, 'doc-011', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (12, 1, 3, 'doc-012', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador2', 1, 1, '2020-07-01', '2021-07-01', 'user-002', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima, evaluador1_id, evaluador2_id, activo, version) VALUES(1, 9, 1, 1, 1, true, 1, 2, true, 1);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima, evaluador1_id, evaluador2_id, version, activo) VALUES(2, 2, 2, 2, 1, true, 1, 2, 1, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima, evaluador1_id, evaluador2_id, version, activo) VALUES(3, 2, 2, 2, 1, true, 1, 2, 2, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima, evaluador1_id, evaluador2_id, version, activo) VALUES(4, 2, 2, 2, 1, true, 1, 2, 3, true);
