-- TIPO EVALUACION 
INSERT INTO eti.tipo_evaluacion (id, nombre, activo) VALUES (3, 'Seguimiento anual', true);

-- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (5, 'Favorable', 3, true);
INSERT INTO eti.dictamen (id, nombre, tipo_evaluacion_id, activo) VALUES (6, 'Solicitud de modificaciones', 3, true);
