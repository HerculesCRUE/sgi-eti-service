package org.crue.hercules.sgi.eti.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoRetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.repository.specification.MemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.InformeService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Memoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemoriaServiceImpl implements MemoriaService {

  /** Comentario repository */
  private final ComentarioRepository comentarioRepository;

  /** Comité repository */
  private final ComiteRepository comiteRepository;

  /** Documentacion memoria repository. */
  private final DocumentacionMemoriaRepository documentacionMemoriaRepository;

  /** Estado Memoria Repository. */
  private final EstadoMemoriaRepository estadoMemoriaRepository;

  /** Estado Retrospectiva repository */
  private final EstadoRetrospectivaRepository estadoRetrospectivaRepository;

  /** Evaluacion repository */
  private final EvaluacionRepository evaluacionRepository;

  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;

  /** Petición evaluación repository */
  private final PeticionEvaluacionRepository peticionEvaluacionRepository;

  /** Respuesta repository */
  private final RespuestaRepository respuestaRepository;

  /** Informe service */
  private final InformeService informeService;

  public MemoriaServiceImpl(MemoriaRepository memoriaRepository, EstadoMemoriaRepository estadoMemoriaRepository,
      EstadoRetrospectivaRepository estadoRetrospectivaRepository, EvaluacionRepository evaluacionRepository,
      ComentarioRepository comentarioRepository, InformeService informeService,
      PeticionEvaluacionRepository peticionEvaluacionRepository, ComiteRepository comiteRepository,
      DocumentacionMemoriaRepository documentacionMemoriaRepository, RespuestaRepository respuestaRepository) {
    this.memoriaRepository = memoriaRepository;
    this.estadoMemoriaRepository = estadoMemoriaRepository;
    this.estadoRetrospectivaRepository = estadoRetrospectivaRepository;
    this.evaluacionRepository = evaluacionRepository;
    this.comentarioRepository = comentarioRepository;
    this.informeService = informeService;
    this.peticionEvaluacionRepository = peticionEvaluacionRepository;
    this.comiteRepository = comiteRepository;
    this.documentacionMemoriaRepository = documentacionMemoriaRepository;
    this.respuestaRepository = respuestaRepository;
  }

  /**
   * Guarda la entidad {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a guardar.
   * @return la entidad {@link Memoria} persistida.
   */
  @Transactional
  @Override
  public Memoria create(Memoria memoria) {
    log.debug("Memoria create(Memoria memoria) - start");

    validacionesCreateMemoria(memoria);

    Assert.isTrue(memoria.getTipoMemoria().getId().equals(1L) || memoria.getTipoMemoria().getId().equals(3L),
        "La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");

    // La memoria se crea con tipo estado memoria "En elaboración".
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION);
    memoria.setEstadoActual(tipoEstadoMemoria);

    memoria.setNumReferencia(
        getReferenciaMemoria(memoria.getTipoMemoria().getId(), memoria.getNumReferencia(), memoria.getComite()));

    // Requiere retrospectiva
    memoria.setRequiereRetrospectiva(Boolean.FALSE);

    // Versión
    memoria.setVersion(0);

    // Activo
    memoria.setActivo(Boolean.TRUE);

    return memoriaRepository.save(memoria);
  }

  @Transactional
  @Override
  public Memoria createModificada(Memoria nuevaMemoria, Long id) {
    log.debug("Memoria createModificada(Memoria memoria, id) - start");

    validacionesCreateMemoria(nuevaMemoria);

    Assert.isTrue(nuevaMemoria.getTipoMemoria().getId().equals(2L),
        "La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(id).orElseThrow(() -> new MemoriaNotFoundException(id));

    nuevaMemoria.setRequiereRetrospectiva(memoria.getRequiereRetrospectiva());
    nuevaMemoria.setVersion(1);
    nuevaMemoria.setActivo(Boolean.TRUE);
    nuevaMemoria.setMemoriaOriginal(memoria);

    // La memoria se crea con tipo estado memoria "En elaboración".
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION);
    nuevaMemoria.setEstadoActual(tipoEstadoMemoria);

    nuevaMemoria.setNumReferencia(
        getReferenciaMemoria(nuevaMemoria.getTipoMemoria().getId(), memoria.getNumReferencia(), memoria.getComite()));

    final Memoria memoriaCreada = memoriaRepository.save(nuevaMemoria);

    Page<DocumentacionMemoria> documentacionesMemoriaPage = documentacionMemoriaRepository
        .findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(), null);

    List<DocumentacionMemoria> documentacionesMemoriaList = documentacionesMemoriaPage.getContent().stream()
        .map(documentacionMemoria -> {
          return new DocumentacionMemoria(null, memoriaCreada, documentacionMemoria.getTipoDocumento(),
              documentacionMemoria.getDocumentoRef(), documentacionMemoria.getAportado());
        }).collect(Collectors.toList());

    documentacionMemoriaRepository.saveAll(documentacionesMemoriaList);

    Page<Respuesta> respuestasPage = respuestaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(), null);

    List<Respuesta> respuestaList = respuestasPage.getContent().stream().map(respuesta -> {
      return new Respuesta(null, memoriaCreada, respuesta.getApartado(), respuesta.getValor());
    }).collect(Collectors.toList());

    respuestaRepository.saveAll(respuestaList);

    log.debug("Memoria createModificada(Memoria memoria, id) - end");
    return memoriaCreada;
  }

  /**
   * Obtiene todas las entidades {@link MemoriaPeticionEvaluacion} paginadas y
   * filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link MemoriaPeticionEvaluacion} paginadas y
   *         filtradas.
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Memoria> specs = MemoriaSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findAllMemoriasEvaluaciones(specs, paging, null);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * 
   * Devuelve una lista paginada de {@link Memoria} asignables para una
   * convocatoria determinada
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoriaReunion Identificador del {@link ConvocatoriaReunion}
   * @param pageable              la información de paginación.
   * @return lista de memorias asignables a la convocatoria.
   */
  @Override
  public Page<Memoria> findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion, Pageable pageable) {
    log.debug("findAllMemoriasAsignables(Long idConvocatoriaReunion, Pageable pageable) - start");
    Page<Memoria> returnValue = memoriaRepository.findAllMemoriasAsignablesConvocatoria(idConvocatoriaReunion,
        pageable);
    log.debug("findAllMemoriasAsignables(Long idConvocatoriaReunion, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Ordinaria" o "Extraordinaria".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   */
  @Override
  public Page<Memoria> findAllAsignablesTipoConvocatoriaOrdExt(String query, Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(String query,Pageable pageable) - start");

    Specification<Memoria> specs = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications.estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA)).or(
            MemoriaSpecifications.estadoRetrospectivaIn(Arrays.asList(Constantes.ESTADO_RETROSPECTIVA_EN_SECRETARIA))))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Memoria> returnValue = memoriaRepository.findAll(specs, pageable);

    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(String query,Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Seguimiento".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité y Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   */
  @Override
  public Page<Memoria> findAllAsignablesTipoConvocatoriaSeguimiento(String query, Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - start");

    Specification<Memoria> specs = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications
            .estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL,
                Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL)))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Memoria> returnValue = memoriaRepository.findAll(specs, pageable);

    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   * @return la entidad {@link Memoria}.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  @Override
  public Memoria findById(final Long id) throws MemoriaNotFoundException {
    log.debug("Petición a get Memoria : {}  - start", id);
    final Memoria Memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));
    log.debug("Petición a get Memoria : {}  - end", id);
    return Memoria;

  }

  /**
   * Elimina una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   */
  @Transactional
  @Override
  public void delete(Long id) throws MemoriaNotFoundException {
    log.debug("Petición a delete Memoria : {}  - start", id);
    Assert.notNull(id, "El id de Memoria no puede ser null.");
    if (!memoriaRepository.existsById(id)) {
      throw new MemoriaNotFoundException(id);
    }
    memoriaRepository.deleteById(id);
    log.debug("Petición a delete Memoria : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link Memoria}.
   * 
   * @param memoriaActualizar {@link Memoria} con los datos actualizados.
   * @return El {@link Memoria} actualizado.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Memoria} no tiene id.
   */

  @Transactional
  @Override
  public Memoria update(final Memoria memoriaActualizar) {
    log.debug("update(Memoria MemoriaActualizar) - start");

    Assert.notNull(memoriaActualizar.getId(), "Memoria id no puede ser null para actualizar un tipo memoria");

    return memoriaRepository.findById(memoriaActualizar.getId()).map(memoria -> {

      // Se comprueba si se está desactivando la memoria
      if (memoria.getActivo() && !memoriaActualizar.getActivo()) {
        Assert.isTrue(
            memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA,
            "El estado actual de la memoria no es el correcto para desactivar la memoria");
      }

      memoria.setNumReferencia(memoriaActualizar.getNumReferencia());
      memoria.setPeticionEvaluacion(memoriaActualizar.getPeticionEvaluacion());
      memoria.setComite((memoriaActualizar.getComite()));
      memoria.setTitulo(memoriaActualizar.getTitulo());
      memoria.setPersonaRef(memoriaActualizar.getPersonaRef());
      memoria.setTipoMemoria(memoriaActualizar.getTipoMemoria());
      memoria.setEstadoActual(memoriaActualizar.getEstadoActual());
      memoria.setFechaEnvioSecretaria(memoriaActualizar.getFechaEnvioSecretaria());
      memoria.setRequiereRetrospectiva(memoriaActualizar.getRequiereRetrospectiva());
      memoria.setRetrospectiva(memoriaActualizar.getRetrospectiva());
      memoria.setVersion(memoriaActualizar.getVersion());
      memoria.setCodOrganoCompetente(memoriaActualizar.getCodOrganoCompetente());
      memoria.setActivo(memoriaActualizar.getActivo());

      Memoria returnValue = memoriaRepository.save(memoria);
      log.debug("update(Memoria memoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(memoriaActualizar.getId()));
  }

  /**
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @param pageable             información de paginación
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findMemoriaByPeticionEvaluacionMaxVersion(Long idPeticionEvaluacion,
      Pageable pageable) {
    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findMemoriasEvaluacion(idPeticionEvaluacion,
        pageable, null);
    return returnValue;
  }

  /**
   * Se crea el nuevo estado para la memoria recibida y se actualiza el estado
   * actual de esta.
   * 
   * @param memoria             {@link Memoria} a actualizar estado.
   * @param idTipoEstadoMemoria identificador del estado nuevo de la memoria.
   */
  @Override
  public void updateEstadoMemoria(Memoria memoria, long idTipoEstadoMemoria) {
    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - start");

    // se crea el nuevo estado para la memoria
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria);
    EstadoMemoria estadoMemoria = new EstadoMemoria(null, memoria, tipoEstadoMemoria, LocalDateTime.now());

    estadoMemoriaRepository.save(estadoMemoria);

    // Se actualiza la memoria con el nuevo tipo estado memoria

    memoria.setEstadoActual(tipoEstadoMemoria);
    memoriaRepository.save(memoria);

    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - end");
  }

  /**
   * Obtiene todas las entidades {@link Memoria} paginadas y filtadas.
   *
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @param personaRef la referencia de la persona
   * @return el listado de entidades {@link Memoria} paginadas y filtradas.
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
      String query, Pageable paging, String personaRef) {
    log.debug(
        "findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(String query,Pageable paging, String personaRef) - start");
    // TODO: Eliminar cuando el custom repository contemple Predicates a null
    Specification<Memoria> specs = null;
    if (StringUtils.isNotBlank(query)) {
      specs = SgiRSQLJPASupport.toSpecification(query);
    }

    Page<MemoriaPeticionEvaluacion> page = memoriaRepository.findAllMemoriasEvaluaciones(specs, paging, personaRef);
    log.debug(
        "findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(String query,Pageable paging, String personaRef) - end");
    return page;
  }

  /**
   * Actualiza el estado de la memoria a su estado anterior
   * 
   * @param id identificador del objeto {@link Memoria}
   * @return la {@link Memoria} si se ha podido actualizar el estado
   */
  @Transactional
  @Override
  public Memoria updateEstadoAnteriorMemoria(Long id) {

    Optional<Memoria> returnMemoria = memoriaRepository.findById(id);
    Memoria memoria = null;
    if (returnMemoria.isPresent()) {
      memoria = returnMemoria.get();
      if (memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
          || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA
          || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO
          || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION) {

        try {
          // Si la memoria se cambió al estado anterior estando en evaluación, se
          // eliminará la evaluación.
          if (memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION) {
            Evaluacion evaluacion = evaluacionRepository.findByMemoriaIdAndVersion(memoria.getId(),
                memoria.getVersion());

            Assert.isTrue(evaluacion.getConvocatoriaReunion().getFechaEvaluacion().isAfter(LocalDateTime.now()),
                "La fecha de la convocatoria es anterior a la actual");

            Assert.isNull(evaluacion.getDictamen(), "No se pueden eliminar memorias que ya contengan un dictamen");

            Assert.isTrue(comentarioRepository.countByEvaluacionId(evaluacion.getId()) == 0L,
                "No se puede eliminar una memoria que tenga comentarios asociados");

            evaluacion.setActivo(Boolean.FALSE);
            evaluacionRepository.save(evaluacion);
          }

          if (memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
              || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA) {
            // se eliminan los informes en caso de que las memorias tengan alguno asociado
            memoria.setVersion(memoria.getVersion() - 1);
            informeService.deleteInformeMemoria(memoria.getId());
          }

          // Se retrocede el estado de la memoria, no se hace nada con el estado de la
          // retrospectiva
          memoria = this.getEstadoAnteriorMemoria(memoria, false);
          // Se actualiza la memoria con el estado anterior
          return memoriaRepository.save(memoria);
        } catch (Exception e) {
          log.error("No se ha podido recuperar el estado anterior de la memoria", e);
          return null;
        }

      } else {
        Assert.isTrue(
            memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION,
            "El estado actual de la memoria no es el correcto para recuperar el estado anterior");
        return null;
      }

    } else {
      throw new MemoriaNotFoundException(id);
    }
  }

  /**
   * Recupera la memoria con su estado anterior seteado ya sea memoria o
   * retrospectiva
   * 
   * @param memoria el objeto {@link Memoria}
   * @return la memoria o retrospectiva con su estado anterior
   */
  @Override
  public Memoria getEstadoAnteriorMemoria(Memoria memoria) {

    return this.getEstadoAnteriorMemoria(memoria, true);
  }

  /**
   * Recupera la memoria con su estado anterior seteado ya sea memoria o
   * retrospectiva
   * 
   * @param memoria                    el objeto {@link Memoria}
   * @param cambiarEstadoRetrospectiva si se desea cambiar o no el estado de la
   *                                   retrospectiva
   * @return la memoria o retrospectiva con su estado anterior
   */
  public Memoria getEstadoAnteriorMemoria(Memoria memoria, Boolean cambiarEstadoRetrospectiva) {

    if (memoria.getRetrospectiva() == null || !cambiarEstadoRetrospectiva) {
      List<EstadoMemoria> estadosMemoria = estadoMemoriaRepository
          .findAllByMemoriaIdOrderByFechaEstadoDesc(memoria.getId());

      Optional<EstadoMemoria> estadoAnteriorMemoria = estadosMemoria.stream()
          .filter(estadoMemoria -> estadoMemoria.getTipoEstadoMemoria().getId() != memoria.getEstadoActual().getId())
          .findFirst();

      Assert.isTrue(estadoAnteriorMemoria.isPresent(), "No se puede recuperar el estado anterior de la memoria");

      Optional<EstadoMemoria> estadoMemoriaActual = estadosMemoria.stream()
          .filter(estadoMemoria -> estadoMemoria.getTipoEstadoMemoria().getId() == memoria.getEstadoActual().getId())
          .findAny();

      Assert.isTrue(estadoMemoriaActual.isPresent(), "No se puede recuperar el estado actual de la memoria");

      memoria.setEstadoActual(estadoAnteriorMemoria.get().getTipoEstadoMemoria());
      // eliminamos el estado a cambiar en el histórico
      estadoMemoriaRepository.deleteById(estadoMemoriaActual.get().getId());
    } else {
      // El estado anterior de la retrospectiva es el estado con id anterior al que
      // tiene actualmente
      Optional<EstadoRetrospectiva> estadoRetrospectiva = estadoRetrospectivaRepository
          .findById(memoria.getRetrospectiva().getEstadoRetrospectiva().getId() - 1);

      Assert.isTrue(estadoRetrospectiva.isPresent(), "No se puede recuperar el estado anterior de la retrospectiva");
      if (estadoRetrospectiva.isPresent()) {
        memoria.getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva.get());
      }
    }
    return memoria;
  }

  /**
   * Actualiza el estado de la {@link Memoria} a 'En Secretaria' o 'En Secretaría
   * Revisión Mínima'
   * 
   * @param idMemoria de la memoria.
   */
  @Transactional
  @Override
  public void enviarSecretaria(Long idMemoria, String personaRef) {
    log.debug("enviarSecretaria(Long id) - start");
    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar la memoria");

    memoriaRepository.findById(idMemoria).map(memoria -> {
      Assert.isTrue(
          memoria.getEstadoActual().getId() == 2L || memoria.getEstadoActual().getId() == 6L
              || memoria.getEstadoActual().getId() == 7L || memoria.getEstadoActual().getId() == 8L
              || memoria.getEstadoActual().getId() == 11L || memoria.getEstadoActual().getId() == 16L
              || memoria.getEstadoActual().getId() == 21L,
          "La memoria no está en un estado correcto para pasar al estado 'En secretaría'");

      Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
          "El usuario no es el propietario de la petición evaluación.");

      boolean crearEvaluacion = false;

      // Si el estado es 'Completada', 'Pendiente de correcciones' o 'No procede
      // evaluar' se cambia el estado de la memoria a 'En secretaría'
      if (memoria.getEstadoActual().getId() == 2L || memoria.getEstadoActual().getId() == 7L
          || memoria.getEstadoActual().getId() == 8L) {
        updateEstadoMemoria(memoria, 3L);
      }

      // Si el estado es 'Favorable pendiente de modificaciones mínimas'
      // se cambia el estado de la memoria a 'En secretaría revisión mínima'
      if (memoria.getEstadoActual().getId() == 6L) {
        crearEvaluacion = true;
        updateEstadoMemoria(memoria, 4L);
      }

      // Si el estado es 'Completada seguimiento anual'
      // se cambia el estado de la memoria a 'En secretaría seguimiento anual'
      if (memoria.getEstadoActual().getId() == 11L) {
        updateEstadoMemoria(memoria, 12L);
      }

      // Si el estado es 'Completada seguimiento final'
      // se cambia el estado de la memoria a 'En secretaría seguimiento final'
      if (memoria.getEstadoActual().getId() == 16L) {
        updateEstadoMemoria(memoria, 17L);
      }

      // Si el estado es 'En aclaración seguimiento final'
      // se cambia el estado de la memoria a 'En secretaría seguimiento final
      // aclaraciones'
      if (memoria.getEstadoActual().getId() == 21L) {
        crearEvaluacion = true;
        updateEstadoMemoria(memoria, 18L);
      }

      if (crearEvaluacion) {
        evaluacionRepository.findFirstByMemoriaIdAndActivoTrueOrderByVersionDesc(memoria.getId()).map(evaluacion -> {
          Evaluacion evaluacionNueva = new Evaluacion();
          BeanUtils.copyProperties(evaluacion, evaluacionNueva);
          evaluacionNueva.setId(null);
          evaluacionNueva.setVersion(memoria.getVersion() + 1);
          evaluacionNueva.setEsRevMinima(true);
          evaluacionRepository.save(evaluacionNueva);

          return evaluacionNueva;
        }).orElseThrow(() -> new EvaluacionNotFoundException(idMemoria));

        memoria.setVersion(memoria.getVersion() + 1);
      }

      memoria.setFechaEnvioSecretaria(LocalDate.now());
      memoriaRepository.save(memoria);

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    // TODO crear un fichero en formato pdf con los datos del proyecto y con
    // los datos del formulario y subirlo al gestor documental y que el sistema
    // guarde en informes el identificador del documento.

    log.debug("enviarSecretaria(Long id) - end");
  }

  /**
   * 
   * Actualiza el estado de la Retrospectiva de {@link Memoria} a 'En Secretaria'
   * 
   * @param idMemoria de la memoria.
   */
  @Transactional
  @Override
  public void enviarSecretariaRetrospectiva(Long idMemoria, String personaRef) {
    log.debug("enviarSecretariaRetrospectiva(Long id) - start");
    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar la memoria");

    memoriaRepository.findById(idMemoria).map(memoria -> {
      // Si el estado es 'Completada', Requiere retrospectiva y el comité es CEEA
      Assert.isTrue(
          (memoria.getEstadoActual().getId() == 2L && memoria.getRequiereRetrospectiva()
              && memoria.getComite().getComite().equals("CEEA")),
          "La memoria no está en un estado correcto para pasar al estado 'En secretaría'");

      Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
          "El usuario no es el propietario de la petición evaluación.");

      estadoRetrospectivaRepository.findById(3L).map(estadoRetrospectiva -> {

        memoria.getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva);
        memoriaRepository.save(memoria);
        return estadoRetrospectiva;

      }).orElseThrow(() -> new EstadoRetrospectivaNotFoundException(3L));

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    // FALTA: crear un fichero en formato pdf con los datos del proyecto y con los
    // datos del formulario y subirlo al gestor documental y que el sistema guarde
    // en informes el identificador del documento.

    log.debug("enviarSecretariaRetrospectiva(Long id) - end");
  }

  @Override
  public Page<Memoria> findByComite(Long idComite, Pageable paging) {
    log.debug("findByComite(Long idComite) - start");

    Assert.notNull(idComite,
        "El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");

    return comiteRepository.findByIdAndActivoTrue(idComite).map(comite -> {
      log.debug("findByComite(Long idComite) - end");
      return memoriaRepository.findByComiteIdAndActivoTrueAndComiteActivoTrue(idComite, paging);

    }).orElseThrow(() -> new ComiteNotFoundException(idComite));

  }

  private void validacionesCreateMemoria(Memoria memoria) {
    log.debug("validacionesCreateMemoria(Memoria memoria) - start");

    Assert.isNull(memoria.getId(), "Memoria id tiene que ser null para crear una nueva memoria");
    Assert.notNull(memoria.getPeticionEvaluacion().getId(),
        "Petición evaluación id no puede ser null para crear una nueva memoria");

    peticionEvaluacionRepository.findByIdAndActivoTrue(memoria.getPeticionEvaluacion().getId())
        .orElseThrow(() -> new PeticionEvaluacionNotFoundException(memoria.getPeticionEvaluacion().getId()));

    comiteRepository.findByIdAndActivoTrue(memoria.getComite().getId())
        .orElseThrow(() -> new ComiteNotFoundException(memoria.getComite().getId()));

    log.debug("validacionesCreateMemoria(Memoria memoria) - end");
  }

  /**
   * Recupera la referencia de una memoria según su tipo y comité.
   * 
   * @param idTipoMemoria Identificador {@link TipoMemoria}
   * @param numReferencia Referencia de la memoria copiada en caso de ser memoria
   *                      modificada.
   * @param comite        {ælink {@link Comite}}
   * @return número de referencia.
   */
  private String getReferenciaMemoria(Long idTipoMemoria, String numReferencia, Comite comite) {

    log.debug("getReferenciaMemoria(Long id, String numReferencia) - start");

    // Referencia memoria
    int anioActual = LocalDate.now().getYear();
    StringBuffer sbNumReferencia = new StringBuffer();
    sbNumReferencia.append(comite.getFormulario().getNombre()).append("/").append(anioActual).append("/");

    String numMemoria = "001";

    switch (idTipoMemoria.intValue()) {
      case 1: {
        // NUEVA
        // Se recupera la última memoria para el comité seleccionado
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(
                String.valueOf(anioActual), 2L, comite.getId());

        // Se incrementa el número de la memoria para el comité
        if (ultimaMemoriaComite != null) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("/")[2].split("R")[0]);
          numeroUltimaMemoria++;
          numMemoria = String.format("%03d", numeroUltimaMemoria);
        }

        break;
      }
      case 2: {
        // MODIFICACIÓN

        // Se recupera la última memoria modificada de la memoria de la que se realiza
        // la copia y del comité de la memoria.
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndComiteIdOrderByNumReferenciaDesc(numReferencia, comite.getId());

        StringBuilder sbReferencia = new StringBuilder();
        sbReferencia.append(ultimaMemoriaComite.getNumReferencia().split("MR")[0].split("/")[2].split("R")[0])
            .append("MR");
        if (ultimaMemoriaComite != null && ultimaMemoriaComite.getNumReferencia().contains("MR")) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("MR")[1]);
          numeroUltimaMemoria++;
          sbReferencia.append(numeroUltimaMemoria);

        } else {
          sbReferencia.append("1");
        }

        numMemoria = sbReferencia.toString();

        break;
      }
      case 3: {
        // RATIFICACIÓN
        // Se recupera la última memoria para el comité seleccionado
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(
                String.valueOf(anioActual), 2L, comite.getId());

        // Se incrementa el número de la memoria para el comité
        if (ultimaMemoriaComite != null) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("/")[2].split("R")[0]);
          numeroUltimaMemoria++;

          StringBuilder sbReferencia = new StringBuilder();
          sbReferencia.append(String.format("%03d", numeroUltimaMemoria)).append("R");
          numMemoria = sbReferencia.toString();
        }

        break;
      }
    }
    ;

    sbNumReferencia.append(numMemoria);

    log.debug("getReferenciaMemoria(Long id, String numReferencia) - end");
    return sbNumReferencia.toString();
  }

}
