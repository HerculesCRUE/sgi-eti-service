-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (1, 'M10', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (2, 'M20', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (3, 'M30', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (4, 'M40', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (5, 'M50', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (6, 'M60', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (7, 'M70', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (8, 'M80', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (3, 'Comite3', 3, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (4, 'Comite3', 4, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (5, 'Comite3', 5, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (6, 'Comite3', 6, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (7, 'Comite3', 7, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (8, 'Comite3', 8, true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (2, 'CargoComite2', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (3, 'CargoComite3', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (4, 'CargoComite4', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (5, 'CargoComite5', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (6, 'CargoComite6', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (7, 'CargoComite7', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (8, 'CargoComite8', true);


-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- EQUIPO TRABAJO
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (100, 1, 'user-001');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (200, 1, 'user-002');

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

 -- TIPO ESTADO MEMORIA 
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (1, 'En elaboración', true);

-- ESTADO RETROSPECTIVA
INSERT INTO ETI.ESTADO_RETROSPECTIVA
(ID, NOMBRE, ACTIVO)
VALUES(1, 'EstadoRetrospectiva01', true);

-- RETROSPECTIVA
INSERT INTO ETI.RETROSPECTIVA
(ID, ESTADO_RETROSPECTIVA_ID, FECHA_RETROSPECTIVA)
VALUES(1, 1, '2020-07-01');        

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (100, 'ref-5588', 1, 2, 'Memoria001', 'userref-55697', 1, 1, null, false, 1, 1, true);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (200, 'ref-5588', 1, 2, 'Memoria002', 'userref-55698', 1, 1, null, false, 1, 1, true);

-- FORMACIÓN ESPECÍFICA 
INSERT INTO eti.formacion_especifica (id, nombre, activo) VALUES (300, 'FormacionEspecifica001', true);

-- TIPO TAREA
INSERT INTO eti.tipo_tarea (id, nombre, activo) VALUES (1, 'Eutanasia', true);

-- TAREA
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (1, 100, 100, 'Tarea1', 'Formacion1', 300, 'Organismo1', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (2, 200, 200, 'Tarea2', 'Formacion2', 300, 'Organismo2', 2020, 1);


-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador001', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador002', 2, 2, '2020-07-01', '2021-07-01', 'user-002', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (3, 'Evaluador003', 1, 3, '2020-07-01', '2021-07-01', 'user-003', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (4, 'Evaluador004', 2, 4, '2020-07-01', '2021-07-01', 'user-004', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (5, 'Evaluador005', 1, 5, '2020-07-01', '2021-07-01', 'user-005', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (6, 'Evaluador006', 2, 6, '2020-07-01', '2021-07-01', 'user-006', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (7, 'Evaluador007', 1, 7, '2020-07-01', '2021-07-01', 'user-007', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (8, 'Evaluador008', 2, 8, '2020-07-01', '2021-07-01', 'user-008', true);

