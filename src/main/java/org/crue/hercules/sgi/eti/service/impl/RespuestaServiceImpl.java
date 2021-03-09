package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.RespuestaNotFoundException;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.service.RespuestaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Respuesta}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RespuestaServiceImpl implements RespuestaService {
  private final RespuestaRepository respuestaRepository;

  public RespuestaServiceImpl(RespuestaRepository respuestaRepository) {
    this.respuestaRepository = respuestaRepository;
  }

  /**
   * Guarda la entidad {@link Respuesta}.
   *
   * @param respuesta la entidad {@link Respuesta} a guardar.
   * @return la entidad {@link Respuesta} persistida.
   */
  @Transactional
  public Respuesta create(Respuesta respuesta) {
    log.debug("Petición a create Respuesta : {} - start", respuesta);
    Assert.isNull(respuesta.getId(), "Respuesta id tiene que ser null para crear un nuevo Respuesta");

    return respuestaRepository.save(respuesta);
  }

  /**
   * Obtiene todas las entidades {@link Respuesta} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Respuesta} paginadas y filtradas.
   */
  public Page<Respuesta> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Respuesta> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Respuesta> returnValue = respuestaRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Respuesta} por id.
   *
   * @param id el id de la entidad {@link Respuesta}.
   * @return la entidad {@link Respuesta}.
   * @throws RespuestaNotFoundException Si no existe ningún {@link Respuesta} con
   *                                    ese id.
   */
  public Respuesta findById(final Long id) throws RespuestaNotFoundException {
    log.debug("Petición a get Respuesta : {}  - start", id);
    final Respuesta respuesta = respuestaRepository.findById(id).orElseThrow(() -> new RespuestaNotFoundException(id));
    log.debug("Petición a get Respuesta : {}  - end", id);
    return respuesta;

  }

  /**
   * Elimina una entidad {@link Respuesta} por id.
   *
   * @param id el id de la entidad {@link Respuesta}.
   */
  @Transactional
  public void delete(Long id) throws RespuestaNotFoundException {
    log.debug("Petición a delete Respuesta : {}  - start", id);
    Assert.notNull(id, "El id de Respuesta no puede ser null.");
    if (!respuestaRepository.existsById(id)) {
      throw new RespuestaNotFoundException(id);
    }
    respuestaRepository.deleteById(id);
    log.debug("Petición a delete Respuesta : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Respuesta}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Respuesta: {} - start");
    respuestaRepository.deleteAll();
    log.debug("Petición a deleteAll de Respuesta: {} - end");

  }

  /**
   * Actualiza los datos del {@link Respuesta}.
   * 
   * @param respuestaActualizar {@link Respuesta} con los datos actualizados.
   * @return El {@link Respuesta} actualizado.
   * @throws RespuestaNotFoundException Si no existe ningún {@link Respuesta} con
   *                                    ese id.
   * @throws IllegalArgumentException   Si el {@link Respuesta} no tiene id.
   */

  @Transactional
  public Respuesta update(final Respuesta respuestaActualizar) {
    log.debug("update(Respuesta RespuestaActualizar) - start");

    Assert.notNull(respuestaActualizar.getId(), "Respuesta id no puede ser null para actualizar un Respuesta");

    return respuestaRepository.findById(respuestaActualizar.getId()).map(respuesta -> {
      respuesta.setMemoria(respuestaActualizar.getMemoria());
      respuesta.setApartado(respuestaActualizar.getApartado());
      respuesta.setValor(respuestaActualizar.getValor());

      Respuesta returnValue = respuestaRepository.save(respuesta);
      log.debug("update(Respuesta RespuestaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new RespuestaNotFoundException(respuestaActualizar.getId()));
  }

  @Override
  public Respuesta findByMemoriaIdAndApartadoId(Long id, Long idApartado) {
    return respuestaRepository.findByMemoriaIdAndApartadoId(id, idApartado);
  }

}
