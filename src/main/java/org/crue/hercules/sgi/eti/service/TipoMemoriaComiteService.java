package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaComiteNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoMemoriaComite}.
 */
public interface TipoMemoriaComiteService {
  /**
   * Guardar {@link TipoMemoriaComite}.
   *
   * @param tipoMemoriaComite la entidad {@link TipoMemoriaComite} a guardar.
   * @return la entidad {@link TipoMemoriaComite} persistida.
   */
  TipoMemoriaComite create(TipoMemoriaComite tipoMemoriaComite);

  /**
   * Actualizar {@link TipoMemoriaComite}.
   *
   * @param tipoMemoriaComite la entidad {@link TipoMemoriaComite} a actualizar.
   * @return la entidad {@link TipoMemoriaComite} persistida.
   */
  TipoMemoriaComite update(TipoMemoriaComite tipoMemoriaComite);

  /**
   * Obtener todas las entidades {@link TipoMemoriaComite} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoMemoriaComite} paginadas y/o
   *         filtradas.
   */
  Page<TipoMemoriaComite> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link TipoMemoriaComite} por id.
   *
   * @param id el id de la entidad {@link TipoMemoriaComite}.
   * @return la entidad {@link TipoMemoriaComite}.
   */
  TipoMemoriaComite findById(Long id);

  /**
   * Elimina el {@link TipoMemoriaComite} por id.
   *
   * @param id el id de la entidad {@link TipoMemoriaComite}.
   */
  void deleteById(Long id) throws TipoMemoriaComiteNotFoundException;

  /**
   * Elimina todos los {@link TipoMemoriaComite}.
   */
  void deleteAll();

  /**
   * Devuelve la lista paginada de los tipos memoria de un comité
   * 
   * @param id     Identificador de {@link Comite}
   * @param paging Datos de la paginación
   * @return lista paginada de los tipos memoria
   */
  Page<TipoMemoria> findByComite(Long id, Pageable paging);

}