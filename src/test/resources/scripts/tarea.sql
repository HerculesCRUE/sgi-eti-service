-- DEPENDENCIAS: TAREA
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_actividad.sql",
    "classpath:scripts/equipo_trabajo.sql",
    "classpath:scripts/tipo_memoria.sql",
    "classpath:scripts/estado_retrospectiva.sql",
    "classpath:scripts/retrospectiva.sql",
    "classpath:scripts/formacion_especifica.sql",
    "classpath:scripts/tipo_tarea.sql",
    "classpath:scripts/tipo_estado_memoria.sql"
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo)
 VALUES (1, 'ref-5588', 1, 2, 'Memoria001', 'userref-55698', 1, 1, null, false, 3, 1, true);

-- TAREA
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (1, 1, 1, 'Tarea1', 'Formacion1', 1, 'Organismo1', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (2, 1, 1, 'Tarea2', 'Formacion2', 1, 'Organismo2', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (3, 1, 1, 'Tarea3', 'Formacion3', 1, 'Organismo3', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (4, 1, 1, 'Tarea4', 'Formacion4', 1, 'Organismo4', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (5, 1, 1, 'Tarea5', 'Formacion5', 1, 'Organismo5', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (6, 1, 1, 'Tarea6', 'Formacion6', 1, 'Organismo6', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (7, 1, 1, 'Tarea7', 'Formacion7', 1, 'Organismo7', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (8, 1, 1, 'Tarea8', 'Formacion8', 1, 'Organismo8', 2020, 1);