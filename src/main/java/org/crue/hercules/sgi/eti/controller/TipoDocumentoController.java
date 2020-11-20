package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoDocumentoController
 */
@RestController
@RequestMapping("/tipodocumentos")
@Slf4j
public class TipoDocumentoController {

  /** TipoDocumento service */
  private final TipoDocumentoService service;

  /**
   * Instancia un nuevo TipoDocumentoController.
   * 
   * @param service TipoDocumentoService
   */
  public TipoDocumentoController(TipoDocumentoService service) {
    log.debug("TipoDocumentoController(TipoDocumentoService service) - start");
    this.service = service;
    log.debug("TipoDocumentoController(TipoDocumentoService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoDocumento>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<TipoDocumento> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoDocumento}.
   * 
   * @param nuevoTipoDocumento {@link TipoDocumento}. que se quiere crear.
   * @return Nuevo {@link TipoDocumento} creado.
   */
  @PostMapping
  ResponseEntity<TipoDocumento> newTipoDocumento(@Valid @RequestBody TipoDocumento nuevoTipoDocumento) {
    log.debug("newTipoDocumento(TipoDocumento nuevoTipoDocumento) - start");
    TipoDocumento returnValue = service.create(nuevoTipoDocumento);
    log.debug("newTipoDocumento(TipoDocumento nuevoTipoDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoDocumento}.
   * 
   * @param updatedTipoDocumento {@link TipoDocumento} a actualizar.
   * @param id                   id {@link TipoDocumento} a actualizar.
   * @return {@link TipoDocumento} actualizado.
   */
  @PutMapping("/{id}")
  TipoDocumento replaceTipoDocumento(@Valid @RequestBody TipoDocumento updatedTipoDocumento, @PathVariable Long id) {
    log.debug("replaceTipoDocumento(TipoDocumento updatedTipoDocumento, Long id) - start");
    updatedTipoDocumento.setId(id);
    TipoDocumento returnValue = service.update(updatedTipoDocumento);
    log.debug("replaceTipoDocumento(TipoDocumento updatedTipoDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link TipoDocumento}.
   * @return {@link TipoDocumento} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoDocumento one(@PathVariable Long id) {
    log.debug("TipoDocumento one(Long id) - start");
    TipoDocumento returnValue = service.findById(id);
    log.debug("TipoDocumento one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoDocumento} con id indicado.
   * 
   * @param id Identificador de {@link TipoDocumento}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoDocumento tipoDocumento = this.one(id);
    tipoDocumento.setActivo(Boolean.TRUE);
    service.update(tipoDocumento);
    log.debug("delete(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} inicial de una
   * memoria.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping("/iniciales")
  ResponseEntity<Page<TipoDocumento>> findTipoDocumentacionInicial(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findTipoDocumentacionInicial(List<QueryCriteria> query,Pageable paging) - start");
    Page<TipoDocumento> page = service.findTipoDocumentacionInicial(query, paging);

    if (page.isEmpty()) {
      log.debug("findTipoDocumentacionInicial(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findTipoDocumentacionInicial(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
