package org.crue.hercules.sgi.eti.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.repository.specification.DocumentacionMemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link DocumentacionMemoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DocumentacionMemoriaServiceImpl implements DocumentacionMemoriaService {

  /** Documentacion memoria repository */
  private final DocumentacionMemoriaRepository documentacionMemoriaRepository;

  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;

  /** Tipo Memoria repository */
  private final TipoDocumentoRepository tipoDocumentoRepository;

  public DocumentacionMemoriaServiceImpl(DocumentacionMemoriaRepository documentacionMemoriaRepository,
      MemoriaRepository memoriaRepository, TipoDocumentoRepository tipoDocumentoRepository) {
    this.documentacionMemoriaRepository = documentacionMemoriaRepository;
    this.memoriaRepository = memoriaRepository;
    this.tipoDocumentoRepository = tipoDocumentoRepository;
  }

  /**
   * Crea {@link DocumentacionMemoria} inicial de una memoria (aquella
   * documentación que no es de seguimiento anual, final o retrospectiva).
   *
   * @param idMemoria            Id de la {@link Memoria}
   * @param documentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             guardar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  @Transactional
  public DocumentacionMemoria createDocumentacionInicial(Long idMemoria, DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria : {} - start", documentacionMemoria);
    Assert.isNull(documentacionMemoria.getId(),
        "DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento asociado a esta");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.isTrue(
          (memoria.getEstadoActual().getId().equals(1L) || memoria.getEstadoActual().getId().equals(2L)
              || memoria.getEstadoActual().getId().equals(6L) || memoria.getEstadoActual().getId().equals(7L)
              || memoria.getEstadoActual().getId().equals(8L)),
          "La memoria no se encuentra en un estado adecuado para añadir documentación.");

      documentacionMemoria.setMemoria(memoria);
      return documentacionMemoriaRepository.save(documentacionMemoria);
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene una entidad {@link DocumentacionMemoria} por id.
   *
   * @param id el id de la entidad {@link DocumentacionMemoria}.
   * @return la entidad {@link DocumentacionMemoria}.
   * @throws DocumentacionMemoriaNotFoundException Si no existe ningún
   *                                               {@link DocumentacionMemoria}
   *                                               con ese id.
   */
  public DocumentacionMemoria findById(final Long id) throws DocumentacionMemoriaNotFoundException {
    log.debug("Petición a get DocumentacionMemoria : {}  - start", id);
    final DocumentacionMemoria DocumentacionMemoria = documentacionMemoriaRepository.findById(id)
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(id));
    log.debug("Petición a get DocumentacionMemoria : {}  - end", id);
    return DocumentacionMemoria;

  }

  /**
   * Actualiza los datos del {@link DocumentacionMemoria} inicial de una memoria
   * (aquella documentación que no es de seguimiento anual, final o
   * retrospectiva).
   * 
   * @param idMemoria                      Id de la {@link Memoria}
   * @param documentacionMemoriaActualizar {@link DocumentacionMemoria} con los
   *                                       datos actualizados.
   * @return El {@link DocumentacionMemoria} actualizado.
   * @throws DocumentacionMemoriaNotFoundException Si no existe ningún
   *                                               {@link DocumentacionMemoria}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si el
   *                                               {@link DocumentacionMemoria} no
   *                                               tiene id.
   */

  @Transactional
  public DocumentacionMemoria updateDocumentacionInicial(Long idMemoria,
      final DocumentacionMemoria documentacionMemoriaActualizar) {
    log.debug("updateDocumentacionInicial(DocumentacionMemoria DocumentacionMemoriaActualizar) - start");

    Assert.notNull(documentacionMemoriaActualizar.getId(),
        "DocumentacionMemoria id no puede ser null para actualizar una documentación memoria");

    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar una documentación memoria");

    return documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndMemoriaActivoTrue(documentacionMemoriaActualizar.getId(), idMemoria)
        .map(documentacionMemoria -> {

          Assert.isTrue(
              (documentacionMemoria.getMemoria().getEstadoActual().getId().equals(1L)
                  || documentacionMemoria.getMemoria().getEstadoActual().getId().equals(2L)
                  || documentacionMemoria.getMemoria().getEstadoActual().getId().equals(6L)
                  || documentacionMemoria.getMemoria().getEstadoActual().getId().equals(7L)
                  || documentacionMemoria.getMemoria().getEstadoActual().getId().equals(8L)),
              "La memoria no se encuentra en un estado adecuado para añadir documentación.");

          documentacionMemoria.setDocumentoRef(documentacionMemoriaActualizar.getDocumentoRef());
          documentacionMemoria.setMemoria(documentacionMemoriaActualizar.getMemoria());
          documentacionMemoria.setTipoDocumento(documentacionMemoriaActualizar.getTipoDocumento());
          documentacionMemoria.setAportado(documentacionMemoriaActualizar.getAportado());

          DocumentacionMemoria returnValue = documentacionMemoriaRepository.save(documentacionMemoria);
          log.debug("updateDocumentacionInicial(DocumentacionMemoria DocumentacionMemoriaActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new DocumentacionMemoriaNotFoundException(documentacionMemoriaActualizar.getId()));
  }

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria}.
   *
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findByMemoriaId(Long id, Pageable pageable) {
    log.debug("findByMemoriaId(Long id, Pageable pageable) - start");
    Assert.isTrue(id != null, "El id de la memoria no puede ser nulo para mostrar su documentación");

    Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findByMemoriaId(id, pageable);

    log.debug("findByMemoriaId(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria}.
   *
   * @param id               Id de {@link Memoria}.
   * @param idTipoEvaluacion Id de {@link TipoEvaluacion}
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findByMemoriaIdAndTipoEvaluacion(Long id, Long idTipoEvaluacion,
      Pageable pageable) {
    log.debug("findByMemoriaIdAndTipoEvaluacion(Long id, Long idTipoEvaluacion, Pageable pageable) - start");
    Assert.isTrue(id != null, "El id de la memoria no puede ser nulo para mostrar su documentación");
    Assert.isTrue(idTipoEvaluacion != null,
        "El id del tipo de evaluación no puede ser nulo para mostrar su documentacion");

    Page<DocumentacionMemoria> returnValue = null;
    Optional<TipoDocumento> tipoDocumento = null;

    // TipoEvaluación Retrospectiva muestra la documentación de tipo Retrospectiva
    if (idTipoEvaluacion.equals(1L)) {
      tipoDocumento = tipoDocumentoRepository.findById(3L);
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoId(id, tipoDocumento.get().getId(),
          pageable);
    }
    // TipoEvaluación Memoria muestra todas la documentación que no sea de tipo
    // Retrospectiva, Seguimiento Anual o Seguimiento Final
    if (idTipoEvaluacion.equals(2L)) {
      List<Long> tipoDocumentos = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoIdNotIn(id, tipoDocumentos, pageable);
    }
    // TipoEvaluación Seguimiento Anual muestra la documentación de tipo Seguimiento
    // Anual
    if (idTipoEvaluacion.equals(3L)) {
      tipoDocumento = tipoDocumentoRepository.findById(1L);
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoId(id, tipoDocumento.get().getId(),
          pageable);
    }
    // TipoEvaluación Seguimiento Final muestra la documentación de tipo Seguimiento
    // Final
    if (idTipoEvaluacion.equals(4L)) {
      tipoDocumento = tipoDocumentoRepository.findById(2L);
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoId(id, tipoDocumento.get().getId(),
          pageable);
    }

    log.debug("findByMemoriaIdAndTipoEvaluacion(Long id, Long idTipoEvaluacion, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria}.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionMemoria(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionMemoria(Long idMemoria, Pageable pageable) - start");
    Assert.isTrue(idMemoria != null, "El id de la memoria no puede ser nulo para mostrar su documentación");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      // Aquellos que no son del tipo 1: Seguimiento Anual, 2: Seguimiento Final y 3:
      // Retrospectiva
      Specification<DocumentacionMemoria> specTipoDocumentoNotIn = DocumentacionMemoriaSpecifications
          .tipoDocumentoNotIn(Arrays.asList(1L, 2L, 3L));

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumentoNotIn);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

      log.debug("findDocumentacionMemoria(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Anual.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionSeguimientoAnual(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionSeguimientoAnual(Long idMemoria, Pageable pageable) - start");
    Assert.isTrue(idMemoria != null, "El id de la memoria no puede ser nulo para mostrar su documentación");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      // Aquellos que no son del tipo 1: Seguimiento Anual
      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications.tipoDocumento(1L);

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumento);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

      log.debug("findDocumentacionSeguimientoAnual(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Final.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionSeguimientoFinal(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionSeguimientoFinal(Long idMemoria, Pageable pageable) - start");
    Assert.isTrue(idMemoria != null, "El id de la memoria no puede ser nulo para mostrar su documentación");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      // Aquellos que no son del tipo 2: Seguimiento Final
      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications.tipoDocumento(2L);

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumento);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

      log.debug("findDocumentacionSeguimientoFinal(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Retrospectiva.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionRetrospectiva(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionRetrospectiva(Long idMemoria, Pageable pageable) - start");
    Assert.isTrue(idMemoria != null, "El id de la memoria no puede ser nulo para mostrar su documentación");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      // Aquellos que no son del tipo 3: Retrospectiva
      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications.tipoDocumento(3L);

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumento);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

      log.debug("findDocumentacionRetrospectiva(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Crea un {@link DocumentacionMemoria} del tipo seguimiento anual.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  @Transactional
  @Override
  public DocumentacionMemoria createSeguimientoAnual(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria seguimiento anual : {} - start", documentacionMemoria);
    Assert.isNull(documentacionMemoria.getId(),
        "DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento anual asociado a esta");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.isTrue((memoria.getEstadoActual().getId().equals(9L) || memoria.getEstadoActual().getId().equals(11L)),
          "La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento anual");

      documentacionMemoria.setMemoria(memoria);
      return tipoDocumentoRepository.findByIdAndActivoTrue(1L).map(tipoDocumento -> {
        documentacionMemoria.setTipoDocumento(tipoDocumento);
        return documentacionMemoriaRepository.save(documentacionMemoria);
      }).orElseThrow(() -> new TipoDocumentoNotFoundException(1L));

    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));
  }

  /**
   * Crea un {@link DocumentacionMemoria} del tipo seguimiento final.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  @Transactional
  @Override
  public DocumentacionMemoria createSeguimientoFinal(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria seguimiento final : {} - start", documentacionMemoria);
    Assert.isNull(documentacionMemoria.getId(),
        "DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento final asociado a esta");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.isTrue(
          (memoria.getEstadoActual().getId().equals(14L)
              || memoria.getEstadoActual().getId().equals(16L) | memoria.getEstadoActual().getId().equals(21L)),
          "La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento final");

      documentacionMemoria.setMemoria(memoria);
      return tipoDocumentoRepository.findByIdAndActivoTrue(2L).map(tipoDocumento -> {
        documentacionMemoria.setTipoDocumento(tipoDocumento);
        return documentacionMemoriaRepository.save(documentacionMemoria);
      }).orElseThrow(() -> new TipoDocumentoNotFoundException(1L));

    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));
  }

  /**
   * Crea un {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  @Transactional
  @Override
  public DocumentacionMemoria createRetrospectiva(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria retrospectiva : {} - start", documentacionMemoria);
    Assert.isNull(documentacionMemoria.getId(),
        "DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo retrospectiva asociado a esta");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.notNull(memoria.getRetrospectiva(),
          "La retrospectiva no puede ser null para crear documentación de tipo retrospectiva");

      Assert.isTrue(
          (memoria.getRetrospectiva().getEstadoRetrospectiva().getId().equals(1L)
              || memoria.getRetrospectiva().getEstadoRetrospectiva().getId().equals(2L)),
          "La retrospectiva no se encuentra en un estado adecuado para crear documentación de tipo retrospectiva");

      documentacionMemoria.setMemoria(memoria);
      return tipoDocumentoRepository.findByIdAndActivoTrue(3L).map(tipoDocumento -> {
        documentacionMemoria.setTipoDocumento(tipoDocumento);
        return documentacionMemoriaRepository.save(documentacionMemoria);
      }).orElseThrow(() -> new TipoDocumentoNotFoundException(1L));

    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));
  }

  /**
   * Elimina {@link DocumentacionMemoria} del tipo seguimiento anual.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  @Transactional
  @Override
  public void deleteDocumentacionSeguimientoAnual(Long idMemoria, Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionSeguimientoAnual(Long idMemoria, Long idDocumentacionMemoria) -- start");

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento anual");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento anual asociado a esta");

    memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.isTrue((memoria.getEstadoActual().getId().equals(9L) || memoria.getEstadoActual().getId().equals(11L)),
          "La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento anual");

      documentacionMemoriaRepository.findByIdAndMemoriaIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria)
          .map(documentacionMemoria -> {
            Assert.isTrue(documentacionMemoria.getTipoDocumento().getId().equals(1L),
                "La documentación recuperada no se corresponde con el tipo Seguimiento Anual");
            documentacionMemoriaRepository.delete(documentacionMemoria);
            return Optional.empty();
          }).orElseThrow(() -> new TipoDocumentoNotFoundException(1L));

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Elimina {@link DocumentacionMemoria} del tipo seguimiento final.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  @Transactional
  @Override
  public void deleteDocumentacionSeguimientoFinal(Long idMemoria, Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionSeguimientoFinal(Long idMemoria, Long idDocumentacionMemoria) -- start");

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento final");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento final asociado a esta");

    memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.isTrue(
          (memoria.getEstadoActual().getId().equals(14L) || memoria.getEstadoActual().getId().equals(16L)
              || memoria.getEstadoActual().getId().equals(21L)),
          "La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento final");

      documentacionMemoriaRepository.findByIdAndMemoriaIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria)
          .map(documentacionMemoria -> {

            Assert.isTrue(documentacionMemoria.getTipoDocumento().getId().equals(2L),
                "La documentación recuperada no se corresponde con el tipo Seguimiento Final");
            documentacionMemoriaRepository.delete(documentacionMemoria);
            return Optional.empty();
          }).orElseThrow(() -> new TipoDocumentoNotFoundException(1L));

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Elimina {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  @Transactional
  @Override
  public void deleteDocumentacionRetrospectiva(Long idMemoria, Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionRetrospectiva(Long idMemoria, Long idDocumentacionMemoria) -- start");

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo retrospectiva");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo retrospectiva asociado a esta");

    memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.notNull(memoria.getRetrospectiva(),
          "La retrospectiva no puede ser null para eliminar un documento de tipo retrospectiva");

      Assert.isTrue(
          (memoria.getRetrospectiva().getEstadoRetrospectiva().getId().equals(1L)
              || memoria.getRetrospectiva().getEstadoRetrospectiva().getId().equals(2L)),
          "La retrospectiva no se encuentra en un estado adecuado para eliminar documentación de retrospectiva");

      documentacionMemoriaRepository.findByIdAndMemoriaIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria)
          .map(documentacionMemoria -> {

            Assert.isTrue(documentacionMemoria.getTipoDocumento().getId().equals(3L),
                "La documentación recuperada no se corresponde con el tipo retrospectiva");
            documentacionMemoriaRepository.delete(documentacionMemoria);
            return Optional.empty();
          }).orElseThrow(() -> new TipoDocumentoNotFoundException(1L));

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Elimina {@link DocumentacionMemoria} inicial.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  @Transactional
  @Override
  public void deleteDocumentacionInicial(Long idMemoria, Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionInicial(Long idMemoria, Long idDocumentacionMemoria) -- start");

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo inicial");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo inicial asociado a esta");

    memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.isTrue(
          (memoria.getEstadoActual().getId().equals(1L) || memoria.getEstadoActual().getId().equals(2L)
              || memoria.getEstadoActual().getId().equals(6L) || memoria.getEstadoActual().getId().equals(7L)
              || memoria.getEstadoActual().getId().equals(8L)),
          "La memoria no se encuentra en un estado adecuado para eliminar documentación inicial");

      documentacionMemoriaRepository.findByIdAndMemoriaIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria)
          .map(documentacionMemoria -> {

            Assert.isTrue(
                !documentacionMemoria.getTipoDocumento().getId().equals(1L)
                    && !documentacionMemoria.getTipoDocumento().getId().equals(2L)
                    && !documentacionMemoria.getTipoDocumento().getId().equals(3L),
                "La documentación recuperada no se corresponde con los tipos correspondientes a documentación inicial");
            documentacionMemoriaRepository.delete(documentacionMemoria);
            return Optional.empty();
          }).orElseThrow(() -> new TipoDocumentoNotFoundException(1L));

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

}
