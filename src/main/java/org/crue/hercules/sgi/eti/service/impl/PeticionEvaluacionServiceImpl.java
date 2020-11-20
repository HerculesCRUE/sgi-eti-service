package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.specification.PeticionEvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
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
 * Service Implementation para la gestión de {@link PeticionEvaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PeticionEvaluacionServiceImpl implements PeticionEvaluacionService {
  private final PeticionEvaluacionRepository peticionEvaluacionRepository;

  public PeticionEvaluacionServiceImpl(PeticionEvaluacionRepository peticionEvaluacionRepository) {
    this.peticionEvaluacionRepository = peticionEvaluacionRepository;
  }

  /**
   * Guarda la entidad {@link PeticionEvaluacion}.
   *
   * @param peticionEvaluacion la entidad {@link PeticionEvaluacion} a guardar.
   * @return la entidad {@link PeticionEvaluacion} persistida.
   */
  @Transactional
  public PeticionEvaluacion create(PeticionEvaluacion peticionEvaluacion) {
    log.debug("Petición a create PeticionEvaluacion : {} - start", peticionEvaluacion);
    Assert.isNull(peticionEvaluacion.getId(),
        "PeticionEvaluacion id tiene que ser null para crear un nuevo peticionEvaluacion");

    PeticionEvaluacion peticionEvaluacionAnio = peticionEvaluacionRepository
        .findFirstByCodigoContainingOrderByCodigoDesc(String.valueOf(peticionEvaluacion.getFechaInicio().getYear()));

    Long numEvaluacion = 1L;
    if (peticionEvaluacionAnio != null) {
      numEvaluacion = Long.valueOf(peticionEvaluacionAnio.getCodigo().split("/")[1]);
      numEvaluacion++;
    }

    StringBuilder codigoPeticionEvaluacion = new StringBuilder();

    codigoPeticionEvaluacion.append(String.valueOf(peticionEvaluacion.getFechaInicio().getYear())).append("/")
        .append(String.format("%03d", numEvaluacion));

    peticionEvaluacion.setCodigo(codigoPeticionEvaluacion.toString());

    return peticionEvaluacionRepository.save(peticionEvaluacion);
  }

  /**
   * Obtiene todas las entidades {@link PeticionEvaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link PeticionEvaluacion} paginadas y
   *         filtradas.
   */
  public Page<PeticionEvaluacion> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllPeticionEvaluacion(List<QueryCriteria> query,Pageable paging) - start");
    Specification<PeticionEvaluacion> specByQuery = new QuerySpecification<PeticionEvaluacion>(query);
    Specification<PeticionEvaluacion> specActivos = PeticionEvaluacionSpecifications.activos();

    Specification<PeticionEvaluacion> specs = Specification.where(specActivos).and(specByQuery);

    Page<PeticionEvaluacion> returnValue = peticionEvaluacionRepository.findAll(specs, paging);
    log.debug("findAllPeticionEvaluacion(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   * @return la entidad {@link PeticionEvaluacion}.
   * @throws PeticionEvaluacionNotFoundException Si no existe ningún
   *                                             {@link PeticionEvaluacion}e con
   *                                             ese id.
   */
  public PeticionEvaluacion findById(final Long id) throws PeticionEvaluacionNotFoundException {
    log.debug("Petición a get PeticionEvaluacion : {}  - start", id);
    final PeticionEvaluacion PeticionEvaluacion = peticionEvaluacionRepository.findById(id)
        .orElseThrow(() -> new PeticionEvaluacionNotFoundException(id));
    log.debug("Petición a get PeticionEvaluacion : {}  - end", id);
    return PeticionEvaluacion;

  }

  /**
   * Elimina una entidad {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   */
  @Transactional
  public void delete(Long id) throws PeticionEvaluacionNotFoundException {
    log.debug("Petición a delete PeticionEvaluacion : {}  - start", id);
    Assert.notNull(id, "El id de PeticionEvaluacion no puede ser null.");
    if (!peticionEvaluacionRepository.existsById(id)) {
      throw new PeticionEvaluacionNotFoundException(id);
    }
    peticionEvaluacionRepository.deleteById(id);
    log.debug("Petición a delete PeticionEvaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link PeticionEvaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de PeticionEvaluacion: {} - start");
    peticionEvaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de PeticionEvaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link PeticionEvaluacion}.
   * 
   * @param peticionEvaluacionActualizar {@link PeticionEvaluacion} con los datos
   *                                     actualizados.
   * @return El {@link PeticionEvaluacion} actualizado.
   * @throws PeticionEvaluacionNotFoundException Si no existe ningún
   *                                             {@link PeticionEvaluacion} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si el {@link PeticionEvaluacion}
   *                                             no tiene id.
   */

  @Transactional
  public PeticionEvaluacion update(final PeticionEvaluacion peticionEvaluacionActualizar) {
    log.debug("update(PeticionEvaluacion peticionEvaluacionActualizar) - start");

    Assert.notNull(peticionEvaluacionActualizar.getId(),
        "PeticionEvaluacion id no puede ser null para actualizar una petición de evaluación");

    return peticionEvaluacionRepository.findById(peticionEvaluacionActualizar.getId()).map(peticionEvaluacion -> {
      peticionEvaluacion.setCodigo(peticionEvaluacionActualizar.getCodigo());
      peticionEvaluacion.setDisMetodologico(peticionEvaluacionActualizar.getDisMetodologico());
      peticionEvaluacion.setExterno(peticionEvaluacionActualizar.getExterno());
      peticionEvaluacion.setFechaFin(peticionEvaluacionActualizar.getFechaFin());
      peticionEvaluacion.setFechaInicio(peticionEvaluacionActualizar.getFechaInicio());
      peticionEvaluacion.setFuenteFinanciacion(peticionEvaluacionActualizar.getFuenteFinanciacion());
      peticionEvaluacion.setObjetivos(peticionEvaluacionActualizar.getObjetivos());
      peticionEvaluacion.setResumen(peticionEvaluacionActualizar.getResumen());
      peticionEvaluacion.setSolicitudConvocatoriaRef(peticionEvaluacionActualizar.getSolicitudConvocatoriaRef());
      peticionEvaluacion.setTieneFondosPropios(peticionEvaluacionActualizar.getTieneFondosPropios());
      peticionEvaluacion.setTipoActividad(peticionEvaluacionActualizar.getTipoActividad());
      peticionEvaluacion.setTipoInvestigacionTutelada(peticionEvaluacionActualizar.getTipoInvestigacionTutelada());
      peticionEvaluacion.setTitulo(peticionEvaluacionActualizar.getTitulo());
      peticionEvaluacion.setPersonaRef(peticionEvaluacionActualizar.getPersonaRef());
      peticionEvaluacion.setValorSocial(peticionEvaluacionActualizar.getValorSocial());
      peticionEvaluacion.setActivo(peticionEvaluacionActualizar.getActivo());

      PeticionEvaluacion returnValue = peticionEvaluacionRepository.save(peticionEvaluacion);
      log.debug("update(PeticionEvaluacion peticionEvaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new PeticionEvaluacionNotFoundException(peticionEvaluacionActualizar.getId()));
  }

  /**
   * Obtiene todas las entidades {@link PeticionEvaluacion} paginadas y filtadas.
   *
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @param personaRef Referencia de la persona
   * @return el listado de entidades {@link PeticionEvaluacion} paginadas y
   *         filtradas.
   */
  public Page<PeticionEvaluacion> findAllByPersonaRef(List<QueryCriteria> query, Pageable paging, String personaRef) {
    log.debug("findAllPeticionEvaluacion(List<QueryCriteria> query,Pageable paging) - start");
    Specification<PeticionEvaluacion> specByQuery = new QuerySpecification<PeticionEvaluacion>(query);
    Specification<PeticionEvaluacion> specActivos = PeticionEvaluacionSpecifications.activos();
    Specification<PeticionEvaluacion> specByPersonaRef = PeticionEvaluacionSpecifications.byPersonaRef(personaRef);

    Specification<PeticionEvaluacion> specs = Specification.where(specActivos).and(specByQuery).and(specByPersonaRef);

    Page<PeticionEvaluacion> returnValue = peticionEvaluacionRepository.findAll(specs, paging);
    log.debug("findAllPeticionEvaluacion(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

}
