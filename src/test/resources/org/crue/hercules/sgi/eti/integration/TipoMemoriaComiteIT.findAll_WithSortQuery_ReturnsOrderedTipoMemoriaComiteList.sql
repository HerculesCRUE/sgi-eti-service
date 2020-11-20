-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion) VALUES (1, 'M10', 'Descripcion');

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

-- TIPO MEMORIA COMITE
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (1, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (2, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (3, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (4, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (5, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (6, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (7, 1, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (8, 1, 1);