package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoInvestigacionTutelada}.
 */
public interface TipoInvestigacionTuteladaService {

  /**
   * Obtener todas las entidades {@link TipoInvestigacionTutelada} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoInvestigacionTutelada} paginadas y/o
   *         filtradas.
   */
  Page<TipoInvestigacionTutelada> findAll(List<QueryCriteria> query, Pageable pageable);

}