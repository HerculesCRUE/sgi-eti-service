-- TIPO EVALUACION
INSERT INTO eti.tipo_evaluacion (id, nombre, activo) VALUES (1, 'TipoEvaluacion1', true);
INSERT INTO eti.tipo_evaluacion (id, nombre, activo) VALUES (2, 'TipoEvaluacion1', true);

-- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (1, 'Favorable', 2, true);
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (2, 'Favorable pendiente de revisión mínima', 2, true);
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (3, 'Pendiente de correcciones', 2, true);
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (4, 'No procede evaluar', 2, true);
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (5, 'Favorable', 1, true);
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (6, 'Desfavorable', 1, true);
