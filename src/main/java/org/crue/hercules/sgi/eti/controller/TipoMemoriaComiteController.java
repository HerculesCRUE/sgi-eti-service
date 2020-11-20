package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.crue.hercules.sgi.eti.service.TipoMemoriaComiteService;
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
 * TipoMemoriaComiteController
 */
@RestController
@RequestMapping("/tipomemoriacomites")
@Slf4j
public class TipoMemoriaComiteController {

  /** TipoMemoriaComite service */
  private final TipoMemoriaComiteService service;

  /**
   * Instancia un nuevo TipoMemoriaComiteController.
   * 
   * @param service TipoMemoriaComiteService
   */
  public TipoMemoriaComiteController(TipoMemoriaComiteService service) {
    log.debug("TipoMemoriaComiteController(TipoMemoriaComiteService service) - start");
    this.service = service;
    log.debug("TipoMemoriaComiteController(TipoMemoriaComiteService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoMemoriaComite}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoMemoriaComite>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<TipoMemoriaComite> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoMemoriaComite}.
   * 
   * @param nuevoTipoMemoriaComite {@link TipoMemoriaComite}. que se quiere crear.
   * @return Nuevo {@link TipoMemoriaComite} creado.
   */
  @PostMapping
  public ResponseEntity<TipoMemoriaComite> newTipoMemoriaComite(
      @Valid @RequestBody TipoMemoriaComite nuevoTipoMemoriaComite) {
    log.debug("newTipoMemoriaComite(TipoMemoriaComite nuevoTipoMemoriaComite) - start");
    TipoMemoriaComite returnValue = service.create(nuevoTipoMemoriaComite);
    log.debug("newTipoMemoriaComite(TipoMemoriaComite nuevoTipoMemoriaComite) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoMemoriaComite}.
   * 
   * @param updatedTipoMemoriaComite {@link TipoMemoriaComite} a actualizar.
   * @param id                       id {@link TipoMemoriaComite} a actualizar.
   * @return {@link TipoMemoriaComite} actualizado.
   */
  @PutMapping("/{id}")
  TipoMemoriaComite replaceTipoMemoriaComite(@Valid @RequestBody TipoMemoriaComite updatedTipoMemoriaComite,
      @PathVariable Long id) {
    log.debug("replaceTipoMemoriaComite(TipoMemoriaComite updatedTipoMemoriaComite, Long id) - start");
    updatedTipoMemoriaComite.setId(id);
    TipoMemoriaComite returnValue = service.update(updatedTipoMemoriaComite);
    log.debug("replaceTipoMemoriaComite(TipoMemoriaComite updatedTipoMemoriaComite, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoMemoriaComite} con el id indicado.
   * 
   * @param id Identificador de {@link TipoMemoriaComite}.
   * @return {@link TipoMemoriaComite} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoMemoriaComite one(@PathVariable Long id) {
    log.debug("TipoMemoriaComite one(Long id) - start");
    TipoMemoriaComite returnValue = service.findById(id);
    log.debug("TipoMemoriaComite one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoMemoriaComite} con id indicado.
   * 
   * @param id Identificador de {@link TipoMemoriaComite}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.deleteById(id);
    log.debug("delete(Long id) - end");
  }
}