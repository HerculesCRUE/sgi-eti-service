-- TIPO EVALUACION
INSERT INTO eti.tipo_evaluacion (id, nombre, activo) VALUES (1, 'TipoEvaluacion1', true);

-- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (1, 'Favorable', 1, true);
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (2, 'Favorable pendiente de revisión mínima', 1, true);
