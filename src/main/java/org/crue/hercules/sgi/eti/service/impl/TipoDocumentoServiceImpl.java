package org.crue.hercules.sgi.eti.service.impl;

import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoDocumentoSpecifications;
import org.crue.hercules.sgi.eti.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoDocumentoServiceImpl implements TipoDocumentoService {
  private final TipoDocumentoRepository tipoDocumentoRepository;

  public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository) {
    this.tipoDocumentoRepository = tipoDocumentoRepository;
  }

  /**
   * Guarda la entidad {@link TipoDocumento}.
   *
   * @param tipoDocumento la entidad {@link TipoDocumento} a guardar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Transactional
  public TipoDocumento create(TipoDocumento tipoDocumento) {
    log.debug("Petición a create TipoDocumento : {} - start", tipoDocumento);
    Assert.notNull(tipoDocumento.getId(), "TipoDocumento id no puede ser null para crear un nuevo tipoDocumento");

    return tipoDocumentoRepository.save(tipoDocumento);
  }

  /**
   * Obtiene todas las entidades {@link TipoDocumento} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoDocumento} paginadas y filtradas.
   */
  public Page<TipoDocumento> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllTipoDocumento(List<QueryCriteria> query,Pageable paging) - start");
    Specification<TipoDocumento> specByQuery = new QuerySpecification<TipoDocumento>(query);
    Specification<TipoDocumento> specActivos = TipoDocumentoSpecifications.activos();

    Specification<TipoDocumento> specs = Specification.where(specActivos).and(specByQuery);

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(specs, paging);
    log.debug("findAllTipoDocumento(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoDocumento} por id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento}.
   * @throws TipoDocumentoNotFoundException Si no existe ningún
   *                                        {@link TipoDocumento}e con ese id.
   */
  public TipoDocumento findById(final Long id) throws TipoDocumentoNotFoundException {
    log.debug("Petición a get TipoDocumento : {}  - start", id);
    final TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id)
        .orElseThrow(() -> new TipoDocumentoNotFoundException(id));
    log.debug("Petición a get TipoDocumento : {}  - end", id);
    return tipoDocumento;

  }

  /**
   * Elimina una entidad {@link TipoDocumento} por id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   */
  @Transactional
  public void delete(Long id) throws TipoDocumentoNotFoundException {
    log.debug("Petición a delete TipoDocumento : {}  - start", id);
    Assert.notNull(id, "El id de TipoDocumento no puede ser null.");
    if (!tipoDocumentoRepository.existsById(id)) {
      throw new TipoDocumentoNotFoundException(id);
    }
    tipoDocumentoRepository.deleteById(id);
    log.debug("Petición a delete TipoDocumento : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoDocumento}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoDocumento: {} - start");
    tipoDocumentoRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoDocumento: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoDocumento}.
   * 
   * @param tipoDocumentoActualizar {@link TipoDocumento} con los datos
   *                                actualizados.
   * @return El {@link TipoDocumento} actualizado.
   * @throws TipoDocumentoNotFoundException Si no existe ningún
   *                                        {@link TipoDocumento} con ese id.
   * @throws IllegalArgumentException       Si el {@link TipoDocumento} no tiene
   *                                        id.
   */

  @Transactional
  public TipoDocumento update(final TipoDocumento tipoDocumentoActualizar) {
    log.debug("update(TipoDocumento TipoDocumentoActualizar) - start");

    Assert.notNull(tipoDocumentoActualizar.getId(),
        "TipoDocumento id no puede ser null para actualizar un tipo Documento");

    return tipoDocumentoRepository.findById(tipoDocumentoActualizar.getId()).map(tipoDocumento -> {
      tipoDocumento.setNombre(tipoDocumentoActualizar.getNombre());
      tipoDocumento.setFormulario(tipoDocumentoActualizar.getFormulario());
      tipoDocumento.setActivo(tipoDocumentoActualizar.getActivo());

      TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);
      log.debug("update(TipoDocumento tipoDocumentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoDocumentoNotFoundException(tipoDocumentoActualizar.getId()));
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} inicial de una
   * memoria.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @Override
  public Page<TipoDocumento> findTipoDocumentacionInicial(List<QueryCriteria> query, Pageable paging) {
    log.debug("findTipoDocumentacionInicial(List<QueryCriteria> query,Pageable paging) - start");
    Specification<TipoDocumento> specByQuery = new QuerySpecification<TipoDocumento>(query);
    Specification<TipoDocumento> specActivos = TipoDocumentoSpecifications.activos();

    Specification<TipoDocumento> specTipoDocumentoIdNotIn = TipoDocumentoSpecifications
        .byIdNotIn(Arrays.asList(1L, 2L, 3L));

    Specification<TipoDocumento> specs = Specification.where(specActivos).and(specByQuery)
        .and(specTipoDocumentoIdNotIn);

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(specs, paging);
    log.debug("findTipoDocumentacionInicial(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

}
