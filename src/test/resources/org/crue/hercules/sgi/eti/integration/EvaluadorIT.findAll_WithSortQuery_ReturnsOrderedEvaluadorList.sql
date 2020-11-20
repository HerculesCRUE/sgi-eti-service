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

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador001', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador002', 2, 2, '2020-07-01', '2021-07-01', 'user-002', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (3, 'Evaluador003', 3, 3, '2020-07-01', '2021-07-01', 'user-003', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (4, 'Evaluador004', 4, 4, '2020-07-01', '2021-07-01', 'user-004', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (5, 'Evaluador005', 5, 5, '2020-07-01', '2021-07-01', 'user-005', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (6, 'Evaluador006', 6, 6, '2020-07-01', '2021-07-01', 'user-006', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (7, 'Evaluador007', 7, 7, '2020-07-01', '2021-07-01', 'user-007', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (8, 'Evaluador008', 8, 8, '2020-07-01', '2021-07-01', 'user-008', true);