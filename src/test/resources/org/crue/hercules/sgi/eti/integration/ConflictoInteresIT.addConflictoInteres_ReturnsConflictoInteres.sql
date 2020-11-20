-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (2, 'M20', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);
-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (2, 'CargoComite2', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador1', 2, 2, '2020-07-01', '2021-07-01', 'user-001', true);