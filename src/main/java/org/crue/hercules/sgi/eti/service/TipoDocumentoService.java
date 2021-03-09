package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoDocumento}.
 */
public interface TipoDocumentoService {
  /**
   * Guardar {@link TipoDocumento}.
   *
   * @param tipoDocumento la entidad {@link TipoDocumento} a guardar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  TipoDocumento create(TipoDocumento tipoDocumento);

  /**
   * Actualizar {@link TipoDocumento}.
   *
   * @param tipoDocumento la entidad {@link TipoDocumento} a actualizar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  TipoDocumento update(TipoDocumento tipoDocumento);

  /**
   * Obtener todas las entidades {@link TipoDocumento} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  Page<TipoDocumento> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoDocumento} por id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento}.
   */
  TipoDocumento findById(Long id);

  /**
   * Elimina el {@link TipoDocumento} por id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   */
  void delete(Long id) throws TipoDocumentoNotFoundException;

  /**
   * Elimina todos los {@link TipoDocumento}.
   */
  void deleteAll();

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} inicial de una
   * memoria.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return lista paginada de tipos documentos.
   */
  Page<TipoDocumento> findTipoDocumentacionInicial(String query, Pageable paging);

}