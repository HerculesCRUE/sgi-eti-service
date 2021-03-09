-- DEPENDENCIAS: TIPO MEMORIA COMITE
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_memoria.sql"
  }
*/
-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 1, true);

-- TIPO MEMORIA COMITE
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (2, 2, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (3, 2, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (4, 2, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (5, 2, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (6, 2, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (7, 2, 1);
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (8, 2, 1);
