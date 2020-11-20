-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, persona_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen',  ' valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- EQUIPO TRABAJO
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (100, 1, 'user-1');

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (2, 'M20', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);

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
 VALUES (200, 'ref-5588', 1, 2, 'Memoria001', 'userref-55698', 1, 1, null, false, 1, 1, true);

-- FORMACIÓN ESPECÍFICA 
INSERT INTO eti.formacion_especifica (id, nombre, activo) VALUES (300, 'FormacionEspecifica001', true);

-- TIPO TAREA
INSERT INTO eti.tipo_tarea (id, nombre, activo) VALUES (1, 'Eutanasia', true);

-- TAREA
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (2, 100, 200, 'Tarea2', 'Formacion2', 300, 'Organismo2', 2020, 1);