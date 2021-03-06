package org.crue.hercules.sgi.eti.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.crue.hercules.sgi.eti.converter.EvaluacionConverter;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.specification.EvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Evaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EvaluacionServiceImpl implements EvaluacionService {

  /** Estado Memoria repository */
  private final EstadoMemoriaRepository estadoMemoriaRepository;

  /** Evaluación repository */
  private final EvaluacionRepository evaluacionRepository;

  /** Retrospectiva repository */
  private final RetrospectivaRepository retrospectivaRepository;

  /** Memoria service */
  private final MemoriaService memoriaService;

  /** Convocatoria reunión repository */
  private final ConvocatoriaReunionRepository convocatoriaReunionRepository;

  /** Comentario repository */
  private final ComentarioRepository comentarioRepository;

  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;

  /** Evaluacion converter */
  private final EvaluacionConverter evaluacionConverter;

  /**
   * Instancia un nuevo {@link EvaluacionServiceImpl}
   * 
   * @param evaluacionRepository          repository para {@link Evaluacion}
   * @param memoriaRepository             repository para {@link Memoria}
   * @param estadoMemoriaRepository       repository para {@link EstadoMemoria}
   * @param retrospectivaRepository       repository para {@link Retrospectiva}
   * @param memoriaService                service para {@link Memoria}
   * @param comentarioRepository          repository para {@link Comentario}
   * @param convocatoriaReunionRepository repository para
   *                                      {@link ConvocatoriaReunion}
   * @param evaluacionConverter           converter para {@link Evaluacion}
   */
  public EvaluacionServiceImpl(EvaluacionRepository evaluacionRepository,
      EstadoMemoriaRepository estadoMemoriaRepository, RetrospectivaRepository retrospectivaRepository,
      MemoriaService memoriaService, ComentarioRepository comentarioRepository,
      ConvocatoriaReunionRepository convocatoriaReunionRepository, MemoriaRepository memoriaRepository,
      EvaluacionConverter evaluacionConverter) {

    this.evaluacionRepository = evaluacionRepository;
    this.estadoMemoriaRepository = estadoMemoriaRepository;
    this.retrospectivaRepository = retrospectivaRepository;
    this.memoriaService = memoriaService;
    this.convocatoriaReunionRepository = convocatoriaReunionRepository;
    this.comentarioRepository = comentarioRepository;
    this.memoriaRepository = memoriaRepository;
    this.evaluacionConverter = evaluacionConverter;
  }

  /**
   * Guarda la entidad {@link Evaluacion}.
   * 
   * Cuando se generan las evaluaciones al asignar memorias a una
   * {@link ConvocatoriaReunion} el tipo de la evaluación vendrá dado por el tipo
   * de la {@link ConvocatoriaReunion} y el estado de la {@link Memoria} o de la
   * {@link Retrospectiva}. Además será necesario actualizar el estado de la
   * {@link Memoria} o de la {@link Retrospectiva} al estado 'En evaluacion'
   * dependiendo del tipo de evaluación.
   *
   * @param evaluacion la entidad {@link Evaluacion} a guardar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  @Transactional
  public Evaluacion create(Evaluacion evaluacion) {
    log.debug("Petición a create Evaluacion : {} - start", evaluacion);
    Assert.isNull(evaluacion.getId(), "Evaluacion id tiene que ser null para crear una nueva evaluacion");
    Assert.notNull(evaluacion.getConvocatoriaReunion().getId(), "La convocatoria de reunión no puede ser nula");

    if (!convocatoriaReunionRepository.existsById(evaluacion.getConvocatoriaReunion().getId())) {
      throw new ConvocatoriaReunionNotFoundException(evaluacion.getConvocatoriaReunion().getId());
    }

    if (!memoriaRepository.existsById(evaluacion.getMemoria().getId())) {
      throw new MemoriaNotFoundException(evaluacion.getMemoria().getId());
    }

    // Si la evaluación es creada mediante la asignación de memorias en
    // ConvocatoriaReunión
    evaluacion.setConvocatoriaReunion(
        convocatoriaReunionRepository.findById(evaluacion.getConvocatoriaReunion().getId()).get());

    Evaluacion evaluacionCompleta = rellenarEvaluacionConEstadosMemoria(evaluacion);

    if (evaluacionCompleta.getTipoEvaluacion().getId() == Constantes.TIPO_EVALUACION_RETROSPECTIVA) {
      retrospectivaRepository.save(evaluacionCompleta.getMemoria().getRetrospectiva());
    } else {
      estadoMemoriaRepository.save(new EstadoMemoria(null, evaluacionCompleta.getMemoria(),
          evaluacionCompleta.getMemoria().getEstadoActual(), LocalDateTime.now()));
    }

    memoriaService.update(evaluacionCompleta.getMemoria());

    return evaluacionRepository.save(evaluacionCompleta);
  }

  public Evaluacion rellenarEvaluacionConEstadosMemoria(Evaluacion evaluacion) {
    /** Se setean campos de evaluación */

    evaluacion.setActivo(true);
    evaluacion.setEsRevMinima(false);
    evaluacion.setFechaDictamen(evaluacion.getConvocatoriaReunion().getFechaEvaluacion().toLocalDate());
    evaluacion.setTipoEvaluacion(new TipoEvaluacion());

    // Convocatoria Seguimiento
    if (evaluacion.getConvocatoriaReunion().getTipoConvocatoriaReunion()
        .getId() == Constantes.TIPO_CONVOCATORIA_REUNION_SEGUIMIENTO) {
      // mismo tipo seguimiento que la memoria Anual(3) Final(4)
      evaluacion.getTipoEvaluacion()
          .setId((evaluacion.getMemoria().getEstadoActual()
              .getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL)
                  ? Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL
                  : Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL);
      // se actualiza estado de la memoria a 'En evaluación'
      evaluacion.getMemoria().getEstadoActual()
          .setId((evaluacion.getMemoria().getEstadoActual()
              .getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL)
                  ? Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL
                  : Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL);

      // Convocatoria Ordinaria o Extraordinaria
    } else {
      // memoria 'en secretaría' y retrospectiva 'en secretaría'
      if (evaluacion.getMemoria().getEstadoActual().getId() > Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
          && evaluacion.getMemoria().getRequiereRetrospectiva() && evaluacion.getMemoria().getRetrospectiva()
              .getEstadoRetrospectiva().getId() == Constantes.ESTADO_RETROSPECTIVA_EN_SECRETARIA) {
        // tipo retrospectiva
        evaluacion.getTipoEvaluacion().setId(Constantes.TIPO_EVALUACION_RETROSPECTIVA);
        // se actualiza el estado retrospectiva a 'En evaluación'
        evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva()
            .setId(Constantes.ESTADO_RETROSPECTIVA_EN_EVALUACION);

      } else {
        // tipo 'memoria'
        evaluacion.getTipoEvaluacion().setId(Constantes.TIPO_EVALUACION_MEMORIA);
        // se actualiza estado de la memoria a 'En evaluación'
        evaluacion.getMemoria().getEstadoActual().setId(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION);
      }
    }

    if (evaluacion.getTipoEvaluacion().getId().equals(2L)) {
      evaluacion.setVersion(evaluacion.getMemoria().getVersion() + 1);
      evaluacion.getMemoria().setVersion(evaluacion.getVersion());
    } else {
      evaluacionRepository.findFirstByMemoriaIdAndActivoTrueOrderByVersionDesc(evaluacion.getMemoria().getId())
          .map(evaluacionAnterior -> {

            if (evaluacionAnterior != null) {
              evaluacion.setVersion(evaluacionAnterior.getVersion() + 1);
            } else {
              evaluacion.setVersion(1);
            }

            return evaluacionAnterior;
          });
    }
    return evaluacion;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */
  public Page<Evaluacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Evaluacion> specs = EvaluacionSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene la lista de evaluaciones activas de una convocatoria reunion que no
   * estan en revisión mínima.
   * 
   * @param idConvocatoriaReunion Id de {@link ConvocatoriaReunion}.
   * @param query                 información del filtro.
   * @param paging                la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<EvaluacionWithIsEliminable> findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion,
      String query, Pageable paging) {
    log.debug(
        "findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion, String query, Pageable pageable) - start");
    Specification<Evaluacion> specs = EvaluacionSpecifications.byConvocatoriaReunionId(idConvocatoriaReunion)
        .and(EvaluacionSpecifications.byEsRevMinima(false)).and(EvaluacionSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, paging);

    return new PageImpl<EvaluacionWithIsEliminable>(
        evaluacionConverter.evaluacionesToEvaluacionesWithIsEliminable(returnValue.getContent()), paging,
        returnValue.getTotalElements());
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  public Page<Evaluacion> findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) {
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - start");
    Page<Evaluacion> returnValue = evaluacionRepository
        .findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(id, pageable);
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una determinada
   * {@link Memoria}.
   *
   * @param idMemoria    Id de {@link Memoria}.
   * @param idEvaluacion Id de {@link Evaluacion}
   * @param pageable     la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Pageable pageable) {
    log.debug("findEvaluacionesAnterioresByMemoria(Long id, Pageable pageable) - start");
    Assert.notNull(idMemoria, "El id de la memoria no puede ser nulo para mostrar sus evaluaciones");
    Assert.notNull(idEvaluacion, "El id de la evaluación no puede ser nulo para recuperar las evaluaciones anteriores");
    Page<EvaluacionWithNumComentario> returnValue = evaluacionRepository.findEvaluacionesAnterioresByMemoria(idMemoria,
        idEvaluacion, pageable);
    log.debug("findEvaluacionesAnterioresByMemoria(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<Evaluacion> findByEvaluadorPersonaRef(String personaRef, String query, Pageable pageable) {
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - start");
    Assert.notNull(personaRef, "El userRefId de la evaluación no puede ser nulo para mostrar sus evaluaciones");
    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluador(personaRef, query, pageable);
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<Evaluacion> findByEvaluador(String personaRef, String query, Pageable pageable) {
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - start");
    Assert.notNull(personaRef, "El personaRef de la evaluación no puede ser nulo para mostrar sus evaluaciones");
    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluador(personaRef, query, pageable);
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene la última versión de las memorias en estado "En evaluación" o "En
   * secretaria revisión mínima", y evaluaciones de tipo retrospectiva asociadas a
   * memoria con el campo estado de retrospectiva en "En evaluación".
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */
  @Override
  public Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable paging) {
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query,Pageable paging) - start");

    Page<Evaluacion> returnValue = evaluacionRepository.findAllByMemoriaAndRetrospectivaEnEvaluacion(query, paging);
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador
   * 
   * @param personaRef Persona ref del {@link Evaluador}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query,
      Pageable pageable) {
    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - start");
    Assert.notNull(personaRef,
        "El personaRef de la evaluación no puede ser nulo para mostrar sus evaluaciones en seguimiento");
    Page<Evaluacion> evaluaciones = evaluacionRepository.findEvaluacionesEnSeguimientosByEvaluador(personaRef, query,
        pageable);
    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return evaluaciones;
  }

  /**
   * Obtiene una entidad {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la entidad {@link Evaluacion}.
   * @throws EvaluacionNotFoundException Si no existe ningún {@link Evaluacion} *
   *                                     con ese id.
   */
  public Evaluacion findById(final Long id) throws EvaluacionNotFoundException {
    log.debug("Petición a get Evaluacion : {}  - start", id);
    final Evaluacion Evaluacion = evaluacionRepository.findById(id)
        .orElseThrow(() -> new EvaluacionNotFoundException(id));
    log.debug("Petición a get Evaluacion : {}  - end", id);
    return Evaluacion;

  }

  /**
   * Elimina una entidad {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   */
  @Transactional
  public void delete(Long id) throws EvaluacionNotFoundException {
    log.debug("Petición a delete Evaluacion : {}  - start", id);
    Assert.notNull(id, "El id de Evaluacion no puede ser null.");
    if (!evaluacionRepository.existsById(id)) {
      throw new EvaluacionNotFoundException(id);
    }
    evaluacionRepository.deleteById(id);
    log.debug("Petición a delete Evaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Evaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Evaluacion: {} - start");
    evaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de Evaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link Evaluacion}.
   * 
   * @param evaluacionActualizar {@link Evaluacion} con los datos actualizados.
   * @return El {@link Evaluacion} actualizado.
   * @throws EvaluacionNotFoundException Si no existe ningún {@link Evaluacion}
   *                                     con ese id.
   * @throws IllegalArgumentException    Si el {@link Evaluacion} no tiene id.
   */

  @Transactional
  public Evaluacion update(final Evaluacion evaluacionActualizar) {
    log.debug("update(Evaluacion evaluacionActualizar) - start");

    Assert.notNull(evaluacionActualizar.getId(), "Evaluacion id no puede ser null para actualizar una evaluacion");

    // Si la Evaluación es de Revisión Mínima
    // se actualiza la fechaDictamen con la fecha actual
    if (evaluacionActualizar.getEsRevMinima()) {
      evaluacionActualizar.setFechaDictamen(LocalDate.now());
    }

    // Si el dictamen es "Favorable" y la Evaluación es de Revisión Mínima
    if (evaluacionActualizar.getDictamen() != null
        && evaluacionActualizar.getDictamen().getNombre().toUpperCase().equals("FAVORABLE")
        && evaluacionActualizar.getEsRevMinima()) {

      // Si el estado de la memoria es "En evaluación" o
      // "En secretaria revisión mínima"
      // Se cambia el estado de la memoria a "Fin evaluación"
      if (evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(4L)
          || evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(5L)) {
        memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(), 9L);
      }
      // Si el estado de la memoria es "En evaluación seguimiento anual" o "En
      // evaluación seguimiento final" o "En secretaría seguimiento final
      // aclaraciones"
      // Se cambia el estado de la memoria a "Fin evaluación seguimiento final"
      if (evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(13L)
          || evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(19L)
          || evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(18L)) {
        memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(), 20L);
      }
    }

    // Si el dictamen es "Favorable pendiente de revisión mínima" y
    // la Evaluación es de Revisión Mínima, se cambia el estado de la
    // memoria a "Favorable Pendiente de Modificaciones Mínimas".
    if (evaluacionActualizar.getDictamen() != null && evaluacionActualizar.getDictamen().getId().equals(2L)
        && evaluacionActualizar.getEsRevMinima()) {
      memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(), 6L);
    }

    return evaluacionRepository.findById(evaluacionActualizar.getId()).map(evaluacion -> {
      evaluacion.setDictamen(evaluacionActualizar.getDictamen());
      evaluacion.setEsRevMinima(evaluacionActualizar.getEsRevMinima());
      evaluacion.setFechaDictamen(evaluacionActualizar.getFechaDictamen());
      evaluacion.setMemoria(evaluacionActualizar.getMemoria());
      evaluacion.setVersion(evaluacionActualizar.getVersion());
      evaluacion.setConvocatoriaReunion(evaluacionActualizar.getConvocatoriaReunion());
      evaluacion.setActivo(evaluacionActualizar.getActivo());
      evaluacion.setTipoEvaluacion(evaluacionActualizar.getTipoEvaluacion());
      evaluacion.setEvaluador1(evaluacionActualizar.getEvaluador1());
      evaluacion.setEvaluador2(evaluacionActualizar.getEvaluador2());

      Evaluacion returnValue = evaluacionRepository.save(evaluacion);
      log.debug("update(Evaluacion evaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionActualizar.getId()));
  }

  /**
   * Obtiene la última versión de las memorias en estado "En evaluación
   * seguimiento anual" o "En evaluación seguimiento final" o "En secretaría
   * seguimiento final aclaraciones" .
   * 
   * @param pageable la información de paginación.
   * @param query    información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */

  @Override
  public Page<Evaluacion> findByEvaluacionesEnSeguimientoFinal(String query, Pageable pageable) {
    log.debug("findByEvaluacionesEnSeguimientoFinal(String query,Pageable paging) - start");

    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluacionesEnSeguimientoFinal(query, pageable);
    log.debug("findByEvaluacionesEnSeguimientoFinal(String query,Pageable paging) - end");

    return returnValue;
  }

  /**
   * Elimina las memorias asignadas a una convocatoria de reunión
   * 
   * @param idConvocatoriaReunion id de la {@link ConvocatoriaReunion}
   * @param idEvaluacion          id de la {@link Evaluacion}
   */
  @Override
  @Transactional
  public void deleteEvaluacion(Long idConvocatoriaReunion, Long idEvaluacion) {
    Memoria memoria = null;

    Optional<Evaluacion> evaluacion = evaluacionRepository.findById(idEvaluacion);
    Assert.isTrue(evaluacion.get().getConvocatoriaReunion().getId() == idConvocatoriaReunion,
        "La evaluación no pertenece a esta convocatoria de reunión");
    if (evaluacion.isPresent()) {
      Optional<Memoria> memoriaOpt = memoriaRepository.findById(evaluacion.get().getMemoria().getId());
      if (!memoriaOpt.isPresent()) {
        throw new MemoriaNotFoundException(evaluacion.get().getMemoria().getId());

      } else {
        memoria = memoriaOpt.get();
      }

    } else {
      throw new EvaluacionNotFoundException(idEvaluacion);
    }

    // Volvemos al estado anterior de la memoria
    memoria = memoriaService.getEstadoAnteriorMemoria(memoria);

    memoria.setVersion(memoria.getVersion() - 1);

    Assert.isTrue(evaluacion.get().getConvocatoriaReunion().getFechaEvaluacion().isAfter(LocalDateTime.now()),
        "La fecha de la convocatoria es anterior a la actual");

    Assert.isNull(evaluacion.get().getDictamen(), "No se pueden eliminar memorias que ya contengan un dictamen");

    Assert.isTrue(comentarioRepository.countByEvaluacionId(evaluacion.get().getId()) == 0L,
        "No se puede eliminar una memoria que tenga comentarios asociados");

    memoriaRepository.save(memoria);
    evaluacion.get().setActivo(Boolean.FALSE);
    evaluacionRepository.save(evaluacion.get());
  }

  @Override
  public Page<Evaluacion> findAllByMemoriaId(Long id, Pageable pageable) {
    log.debug("findAllByMemoriaId(Long id,Pageable paging) - start");

    Assert.notNull(id, "El id de la memoria no puede ser nulo para mostrar sus evaluaciones");

    return memoriaRepository.findByIdAndActivoTrue(id).map(memoria -> {

      Specification<Evaluacion> specMemoriaId = EvaluacionSpecifications.memoriaId(id);

      Specification<Evaluacion> specEvaluacionActiva = EvaluacionSpecifications.activos();

      Specification<Evaluacion> specs = Specification.where(specMemoriaId).and(specEvaluacionActiva);

      Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, pageable);

      log.debug("findAllByMemoriaId(Long id,Pageable paging) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(id));

  }
}
