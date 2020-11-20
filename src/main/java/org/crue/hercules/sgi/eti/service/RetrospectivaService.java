package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.RetrospectivaNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Retrospectiva}.
 */
public interface RetrospectivaService {
  /**
   * Guardar {@link Retrospectiva}.
   *
   * @param retrospectiva la entidad {@link Retrospectiva} a guardar.
   * @return la entidad {@link Retrospectiva} persistida.
   */
  Retrospectiva create(Retrospectiva retrospectiva);

  /**
   * Actualizar {@link Retrospectiva}.
   *
   * @param retrospectiva la entidad {@link Retrospectiva} a actualizar.
   * @return la entidad {@link Retrospectiva} persistida.
   */
  Retrospectiva update(Retrospectiva retrospectiva);

  /**
   * Obtener todas las entidades {@link Retrospectiva} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Retrospectiva} paginadas y/o filtradas.
   */
  Page<Retrospectiva> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Retrospectiva} por id.
   *
   * @param id el id de la entidad {@link Retrospectiva}.
   * @return la entidad {@link Retrospectiva}.
   */
  Retrospectiva findById(Long id);

  /**
   * Elimina el {@link Retrospectiva} por id.
   *
   * @param id el id de la entidad {@link Retrospectiva}.
   */
  void delete(Long id) throws RetrospectivaNotFoundException;

  /**
   * Elimina todos los {@link Retrospectiva}.
   */
  void deleteAll();

}