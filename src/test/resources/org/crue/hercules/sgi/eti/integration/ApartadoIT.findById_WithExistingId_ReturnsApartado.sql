-- FORMULARIO
INSERT INTO ETI.FORMULARIO
(ID, NOMBRE, DESCRIPCION)
VALUES(1, 'M10', 'Descripcion1');

-- BLOQUE
INSERT INTO ETI.BLOQUE
(ID, NOMBRE, FORMULARIO_ID, ORDEN)
VALUES(1, 'Bloque1', 1, 1);

-- APARTADO FORMULARIO
INSERT INTO ETI.APARTADO
(ID, BLOQUE_ID, NOMBRE, PADRE_ID, ORDEN, ESQUEMA)
VALUES(1, 1, 'Apartado01', NULL, 1, '{"nombre":"EsquemaApartado01"}');

INSERT INTO ETI.APARTADO
(ID, BLOQUE_ID, NOMBRE, PADRE_ID, ORDEN, ESQUEMA)
VALUES(2, 1, 'Apartado2', 1, 2, '{"nombre":"EsquemaApartado2"}');