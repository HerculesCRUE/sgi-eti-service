-- DEPENDENCIAS: ACTA
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_convocatoria_reunion.sql",
    "classpath:scripts/tipo_estado_acta.sql",
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);

-- CONVOCATORIA REUNION
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(100, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 01', 'Orden del día convocatoria reunión 01', 2020, 1, 1, 8, 30, '2020-07-13', true);

-- ACTA
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (2, 100, 10, 15, 12, 0, 'Resumen124', 124, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (3, 100, 10, 15, 12, 0, 'Resumen125', 125, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (4, 100, 10, 15, 12, 0, 'Resumen126', 126, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (5, 100, 10, 15, 12, 0, 'Resumen127', 127, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (6, 100, 10, 15, 12, 0, 'Resumen128', 128, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (7, 100, 10, 15, 12, 0, 'Resumen129', 129, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (8, 100, 10, 15, 12, 0, 'Resumen120', 120, 1, true, true);
