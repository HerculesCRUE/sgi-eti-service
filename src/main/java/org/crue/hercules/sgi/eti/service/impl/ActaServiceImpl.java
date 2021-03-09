package org.crue.hercules.sgi.eti.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.TipoEstadoActaRepository;
import org.crue.hercules.sgi.eti.service.ActaService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Acta}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService {

  /** Acta Repository. */
  private final ActaRepository actaRepository;

  /** Estado Acta Repository. */
  private final EstadoActaRepository estadoActaRepository;

  /** Evaluacion Repository. */
  private final EvaluacionRepository evaluacionRepository;

  /** Tipo Estado Acta Repository. */
  private final TipoEstadoActaRepository tipoEstadoActaRepository;

  /** Retrospectiva repository. */
  private final RetrospectivaRepository retrospectivaRepository;

  /** Memoria Service. */
  private final MemoriaService memoriaService;

  /**
   * Instancia un nuevo ActaServiceImpl.
   * 
   * @param actaRepository           { @link ActaRepository}
   * @param estadoActaRepository     { @link EstadoActaRepository}
   * @param tipoEstadoActaRepository { @link TipoEstadoActaRepository}
   * @param evaluacionRepository     {@link EvaluacionRepository}
   * @param retrospectivaRepository  {@link RetrospectivaRepository}
   * @param memoriaService           {@link MemoriaService}
   * 
   */
  public ActaServiceImpl(ActaRepository actaRepository, EstadoActaRepository estadoActaRepository,
      TipoEstadoActaRepository tipoEstadoActaRepository, EvaluacionRepository evaluacionRepository,
      RetrospectivaRepository retrospectivaRepository, MemoriaService memoriaService) {
    this.actaRepository = actaRepository;
    this.estadoActaRepository = estadoActaRepository;
    this.tipoEstadoActaRepository = tipoEstadoActaRepository;
    this.evaluacionRepository = evaluacionRepository;
    this.retrospectivaRepository = retrospectivaRepository;
    this.memoriaService = memoriaService;
  }

  /**
   * Guarda la entidad {@link Acta}.
   * 
   * Se insertará de forma automática un registro para el estado inicial del tipo
   * "En elaboración" para el acta
   *
   * @param acta la entidad {@link Acta} a guardar.
   * @return la entidad {@link Acta} persistida.
   */
  @Transactional
  public Acta create(Acta acta) {
    log.debug("Acta create (Acta acta) - start");

    Assert.isNull(acta.getId(), "Acta id tiene que ser null para crear un nuevo acta");

    Optional<TipoEstadoActa> tipoEstadoActa = tipoEstadoActaRepository.findById(1L);
    Assert.isTrue(tipoEstadoActa.isPresent(), "No se puede establecer el TipoEstadoActa inicial (1: 'En elaboración')");

    acta.setEstadoActual(tipoEstadoActa.get());
    Acta returnValue = actaRepository.save(acta);

    EstadoActa estadoActa = estadoActaRepository
        .save(new EstadoActa(null, returnValue, tipoEstadoActa.get(), LocalDateTime.now()));
    Assert.notNull(estadoActa, "No se ha podido crear el EstadoActa inicial");

    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ActaWithNumEvaluaciones}.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de {@link ActaWithNumEvaluaciones} paginadas y filtradas.
   */
  public Page<ActaWithNumEvaluaciones> findAllActaWithNumEvaluaciones(String query, Pageable paging) {
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - start");

    Page<ActaWithNumEvaluaciones> returnValue = actaRepository.findAllActaWithNumEvaluaciones(query, paging);
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return la entidad {@link Acta}.
   * @throws ActaNotFoundException Si no existe ningún {@link Acta} con ese id.
   */
  public Acta findById(final Long id) throws TareaNotFoundException {
    log.debug("Acta findById (Acta acta)  - start", id);
    final Acta acta = actaRepository.findById(id).orElseThrow(() -> new ActaNotFoundException(id));
    log.debug("Acta findById (Acta acta)  - end", id);
    return acta;
  }

  /**
   * Comprueba la existencia del {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return true si existe y false en caso contrario.
   */
  public boolean existsById(final Long id) throws TareaNotFoundException {
    log.debug("Acta existsById (Acta acta)  - start", id);
    final boolean existe = actaRepository.existsById(id);
    log.debug("Acta existsById (Acta acta)  - end", id);
    return existe;
  }

  /**
   * Elimina una entidad {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @throws ActaNotFoundException Si no existe ningún {@link Acta} con ese id.
   */
  @Transactional
  public void delete(Long id) throws TareaNotFoundException {
    log.debug("Petición a delete Acta : {}  - start", id);
    Assert.notNull(id, "El id de Acta no puede ser null.");
    if (!actaRepository.existsById(id)) {
      throw new ActaNotFoundException(id);
    }
    actaRepository.deleteById(id);
    log.debug("Petición a delete Acta : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link Acta}.
   * 
   * @param actaActualizar {@link Acta} con los datos actualizados.
   * @return El {@link Acta} actualizado.
   * @throws ActaNotFoundException    Si no existe ningún {@link Acta} con ese id.
   * @throws IllegalArgumentException Si el {@link Acta} no tiene id.
   */
  @Transactional
  public Acta update(final Acta actaActualizar) {
    log.debug("update(Acta actaActualizar) - start");

    Assert.notNull(actaActualizar.getId(), "Acta id no puede ser null para actualizar un acta");

    return actaRepository.findById(actaActualizar.getId()).map(acta -> {
      acta.setConvocatoriaReunion(actaActualizar.getConvocatoriaReunion());
      acta.setHoraInicio(actaActualizar.getHoraInicio());
      acta.setMinutoInicio(actaActualizar.getMinutoInicio());
      acta.setHoraFin(actaActualizar.getHoraFin());
      acta.setMinutoFin(actaActualizar.getMinutoFin());
      acta.setResumen(actaActualizar.getResumen());
      acta.setNumero(actaActualizar.getNumero());
      acta.setEstadoActual(actaActualizar.getEstadoActual());
      acta.setInactiva(actaActualizar.getInactiva());
      acta.setActivo(actaActualizar.getActivo());

      Acta returnValue = actaRepository.save(acta);
      log.debug("update(Acta actaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ActaNotFoundException(actaActualizar.getId()));
  }

  /**
   * Finaliza el {@link Acta} con el id recibido como parámetro (actualización de
   * su estado a finalizado).
   * 
   * Se actualiza el estado de sus evaluaciones:
   * 
   * - En caso de ser de tipo memoria se actualiza el estado de la memoria
   * dependiendo del dictamen asociado a la evaluación.
   * 
   * - En caso de ser una retrospectiva se actualiza siempre a estado "Fin
   * evaluación".
   * 
   * @param id identificador del {@link Acta} a finalizar.
   */
  @Override
  @Transactional
  public void finishActa(Long id) {

    log.debug("finishActa(Long id) - start");

    Assert.notNull(id, "El id de acta recibido no puede ser null.");

    Acta acta = actaRepository.findById(id).orElseThrow(() -> new ActaNotFoundException(id));

    List<Evaluacion> listEvaluacionesMemoria = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(2L, Boolean.FALSE,
            acta.getConvocatoriaReunion().getId());

    listEvaluacionesMemoria.forEach(evaluacion -> {

      switch (evaluacion.getDictamen().getId().intValue()) {
        case 1: {
          // Dictamen "Favorable"-
          // Se actualiza memoria a estado 9: "Fin evaluación"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), 9L);
          break;
        }
        case 2: {
          // Dictamen "Favorable pendiente de revisión mínima"-
          // Se actualiza memoria a estado 6: "Favorable Pendiente de Modificaciones
          // Mínimas"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), 6L);
          break;
        }
        case 3: {
          // Dictamen "Pendiente de correcciones"
          // Se actualiza memoria a estado 7: "Pendiente de correcciones"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), 7L);
          break;
        }
        case 4: {
          // Dictamen "No procede evaluar"
          // Se actualiza memoria a estado 8: "No procede evaluar"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), 8L);

          break;
        }
      }

    });

    List<Evaluacion> listEvaluacionesRetrospectiva = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(1L, Boolean.FALSE,
            acta.getConvocatoriaReunion().getId());

    listEvaluacionesRetrospectiva.forEach(evaluacion -> {

      // Se actualiza el estado de la retrospectiva a 5: "Fin evaluación"

      EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
      estadoRetrospectiva.setId(5L);

      evaluacion.getMemoria().getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva);

      retrospectivaRepository.save(evaluacion.getMemoria().getRetrospectiva());

    });

    // Se crea el nuevo estado acta 2:"Finalizado"
    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(2L);

    EstadoActa estadoActa = new EstadoActa(null, acta, tipoEstadoActa, LocalDateTime.now());
    estadoActaRepository.save(estadoActa);

    // Actualización del estado actual de acta
    acta.setEstadoActual(tipoEstadoActa);
    actaRepository.save(acta);

    log.debug("finishActa(Long id) - end");
  }

}
