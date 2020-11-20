package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.InformeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Informe}.
 */
public interface InformeService {
  /**
   * Guardar {@link Informe}.
   *
   * @param informe la entidad {@link Informe} a guardar.
   * @return la entidad {@link Informe} persistida.
   */
  Informe create(Informe informe);

  /**
   * Actualizar {@link Informe}.
   *
   * @param informe la entidad {@link Informe} a actualizar.
   * @return la entidad {@link Informe} persistida.
   */
  Informe update(Informe informe);

  /**
   * Obtener todas las entidades {@link Informe} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Informe} paginadas y/o filtradas.
   */
  Page<Informe> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Informe} por id.
   *
   * @param id el id de la entidad {@link Informe}.
   * @return la entidad {@link Informe}.
   */
  Informe findById(Long id);

  /**
   * Elimina el {@link Informe} por id.
   *
   * @param id el id de la entidad {@link Informe}.
   */
  void delete(Long id) throws InformeNotFoundException;

  /**
   * Elimina todos los {@link Informe}.
   */
  void deleteAll();

  /**
   * Elimina el informe relacionado a una memoria
   * 
   * @param idMemoria identificador de la {@link Memoria}
   */
  void deleteInformeMemoria(Long idMemoria);

  /**
   * Devuelve un listado paginado de {@link Informe} filtrado por la
   * {@link Memoria}
   * 
   * @param id       identificador de la {@link Memoria}
   * @param pageable paginación
   * @return el listado paginado de {@link Informe}
   */
  Page<Informe> findByMemoria(Long id, Pageable pageable);

}