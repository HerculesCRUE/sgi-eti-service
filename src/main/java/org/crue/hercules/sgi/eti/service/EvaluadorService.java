package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Evaluador}.
 */
public interface EvaluadorService {
  /**
   * Guardar {@link Evaluador}.
   *
   * @param evaluador la entidad {@link Evaluador} a guardar.
   * @return la entidad {@link Evaluador} persistida.
   */
  Evaluador create(Evaluador evaluador);

  /**
   * Actualizar {@link Evaluador}.
   *
   * @param evaluador la entidad {@link Evaluador} a actualizar.
   * @return la entidad {@link Evaluador} persistida.
   */
  Evaluador update(Evaluador evaluador);

  /**
   * Obtener todas las entidades {@link Evaluador} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Evaluador} paginadas y/o filtradas.
   */
  Page<Evaluador> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Devuelve los evaluadores activos del comité indicado que no entre en
   * conflicto de intereses con ningún miembro del equipo investigador de la
   * memoria.
   * 
   * @param idComite  Identificador del {@link Comite}
   * @param idMemoria Identificador de la {@link Memoria}
   * @param pageable  la información de paginación.
   * @return lista de evaluadores sin conflictos de intereses
   */
  Page<Evaluador> findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria, Pageable pageable);

  /**
   * Obtiene {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   * @return la entidad {@link Evaluador}.
   */
  Evaluador findById(Long id);

  /**
   * Elimina el {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   */
  void delete(Long id) throws EvaluadorNotFoundException;

}