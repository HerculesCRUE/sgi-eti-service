-- DEPENDENCIAS: CONFLICTO_INTERES
/*
  scripts = { 
    "classpath:scripts/formulario.sql", 
    "classpath:scripts/cargo_comite.sql",
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'Comite2', 2, true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador2', 2, 2, '2020-07-01', '2021-07-01', 'user-002', true);

--CONFLICTO INTERÉS
INSERT INTO eti.conflicto_interes (id, evaluador_id, persona_conflicto_ref)
VALUES(1, 1, 'user-001');
INSERT INTO eti.conflicto_interes (id, evaluador_id, persona_conflicto_ref)
VALUES(2, 2, 'user-002');