-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (1, 'M10', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (2, 'M20', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (3, 'M30', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (4, 'Seguimiento Anual', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (5, 'Seguimiento Final', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (6, 'Retrospectiva', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (7, 'Retrospectiva1', 'Descripcion');
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (8, 'Retrospectiva2', 'Descripcion');

-- BLOQUE
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (1, 'Bloque001', 1, 8);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (2, 'Bloque002', 2, 7);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (3, 'Bloque003', 3, 6);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (4, 'Bloque4', 4, 5);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (5, 'Bloque5', 5, 4);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (6, 'Bloque6', 6, 3);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (7, 'Bloque7', 7, 2);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (8, 'Bloque8', 8, 1);