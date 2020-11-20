package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.TipoMemoriaComiteRepository;
import org.crue.hercules.sgi.eti.service.TipoMemoriaComiteService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoMemoriaComite}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoMemoriaComiteServiceImpl implements TipoMemoriaComiteService {

  /** Tipo memoria comite repository. */
  private final TipoMemoriaComiteRepository tipoMemoriaComiteRepository;

  /** Comite repository. */
  private final ComiteRepository comiteRepository;

  public TipoMemoriaComiteServiceImpl(TipoMemoriaComiteRepository tipoMemoriaComiteRepository,
      ComiteRepository comiteRepository) {
    this.tipoMemoriaComiteRepository = tipoMemoriaComiteRepository;
    this.comiteRepository = comiteRepository;
  }

  /**
   * Guarda la entidad {@link TipoMemoriaComite}.
   *
   * @param tipoMemoriaComite la entidad {@link TipoMemoriaComite} a guardar.
   * @return la entidad {@link TipoMemoriaComite} persistida.
   */
  @Transactional
  public TipoMemoriaComite create(TipoMemoriaComite tipoMemoriaComite) {
    log.debug("Petición a create TipoMemoriaComite : {} - start", tipoMemoriaComite);
    Assert.isNull(tipoMemoriaComite.getId(),
        "TipoMemoriaComite id tiene que ser null para crear un nuevo TipoMemoriaComite");

    return tipoMemoriaComiteRepository.save(tipoMemoriaComite);
  }

  /**
   * Obtiene todas las entidades {@link TipoMemoriaComite} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoMemoriaComite} paginadas y
   *         filtradas.
   */
  public Page<TipoMemoriaComite> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllTipoMemoriaComite(List<QueryCriteria> query,Pageable paging) - start");
    Specification<TipoMemoriaComite> spec = new QuerySpecification<>(query);

    Page<TipoMemoriaComite> returnValue = tipoMemoriaComiteRepository.findAll(spec, paging);
    log.debug("findAllTipoMemoriaComite(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Optiene una entidad {@link TipoMemoriaComite} por id.
   *
   * @param id el id de la entidad {@link TipoMemoriaComite}.
   * @return la entidad {@link TipoMemoriaComite}.
   * @throws TipoMemoriaComiteNotFoundException excepción.
   */
  public TipoMemoriaComite findById(final Long id) throws TipoMemoriaComiteNotFoundException {
    log.debug("Petición a get TipoMemoriaComite : {}  - start", id);
    final TipoMemoriaComite tipoMemoriaComite = tipoMemoriaComiteRepository.findById(id)
        .orElseThrow(() -> new TipoMemoriaComiteNotFoundException(id));
    log.debug("Petición a get TipoMemoriaComite : {}  - end", id);
    return tipoMemoriaComite;

  }

  /**
   * Elimina una entidad {@link TipoMemoriaComite} por id.
   *
   * @param id el id de la entidad {@link TipoMemoriaComite}.
   */
  @Transactional
  public void deleteById(Long id) throws TipoMemoriaComiteNotFoundException {
    log.debug("Petición a delete TipoMemoriaComite : {}  - start", id);
    Assert.notNull(id, "El id de TipoMemoriaComite no puede ser null.");
    if (!tipoMemoriaComiteRepository.existsById(id)) {
      throw new TipoMemoriaComiteNotFoundException(id);
    }
    tipoMemoriaComiteRepository.deleteById(id);
    log.debug("Petición a delete TipoMemoriaComite : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoMemoriaComite}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoMemoriaComite: {} - start");
    tipoMemoriaComiteRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoMemoriaComite: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoMemoriaComite}.
   * 
   * @param tipoMemoriaComiteActualizar {@link TipoMemoriaComite} con los datos
   *                                    actualizados.
   * @return El {@link TipoMemoriaComite} actualizado.
   * @throws TipoMemoriaComiteNotFoundException Si no existe ningún
   *                                            {@link TipoMemoriaComite} con ese
   *                                            id.
   * @throws IllegalArgumentException           Si el {@link TipoMemoriaComite} no
   *                                            tiene id.
   */

  @Transactional
  public TipoMemoriaComite update(final TipoMemoriaComite tipoMemoriaComiteActualizar) {
    log.debug("update(TipoMemoriaComite tipoMemoriaComiteActualizar) - start");

    Assert.notNull(tipoMemoriaComiteActualizar.getId(),
        "TipoMemoriaComite id no puede ser null para actualizar un TipoMemoriaComite");

    return tipoMemoriaComiteRepository.findById(tipoMemoriaComiteActualizar.getId()).map(tipoMemoriaComite -> {
      tipoMemoriaComite.setComite(tipoMemoriaComiteActualizar.getComite());
      tipoMemoriaComite.setTipoMemoria(tipoMemoriaComiteActualizar.getTipoMemoria());

      TipoMemoriaComite returnValue = tipoMemoriaComiteRepository.save(tipoMemoriaComite);
      log.debug("update(TipoMemoriaComite tipoMemoriaComiteActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoMemoriaComiteNotFoundException(tipoMemoriaComiteActualizar.getId()));
  }

  @Override
  public Page<TipoMemoria> findByComite(Long id, Pageable paging) {
    log.debug("findByComite(Long id, Pageable paging) - start");

    Assert.notNull(id, "El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");

    return comiteRepository.findByIdAndActivoTrue(id).map(comite -> {
      Page<TipoMemoriaComite> tiposMemoriaComite = tipoMemoriaComiteRepository.findByComiteIdAndComiteActivoTrue(id,
          paging);

      List<TipoMemoria> listTipoMemoria = tiposMemoriaComite.getContent().stream()
          .map(tipoMemoria -> tipoMemoria.getTipoMemoria()).collect(Collectors.toList());

      log.debug("findByComite(Long id, Pageable paging) - end");
      return new PageImpl<TipoMemoria>(listTipoMemoria, paging, listTipoMemoria.size());
    }).orElseThrow(() -> new ComiteNotFoundException(id));

  }

}
