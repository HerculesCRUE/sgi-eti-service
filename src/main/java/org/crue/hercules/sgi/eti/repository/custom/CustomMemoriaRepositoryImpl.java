package org.crue.hercules.sgi.eti.repository.custom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva_;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Memoria}.
 */
@Slf4j
@Component
public class CustomMemoriaRepositoryImpl implements CustomMemoriaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
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
    // TODO: Revisar uso pageable. Se está haciendo consulta páginada pero ne está
    // obteniendo el count total.
    log.debug("findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion, Pageable pageable) - start");
    final List<Predicate> predicates = new ArrayList<>();

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Memoria> cq = cb.createQuery(Memoria.class);

    // Fecha limite convocatoria
    Subquery<LocalDate> sqFechaLimiteConvocatoria = cq.subquery(LocalDate.class);
    Root<ConvocatoriaReunion> convocatoriaFechaLimiteRoot = sqFechaLimiteConvocatoria.from(ConvocatoriaReunion.class);
    sqFechaLimiteConvocatoria.select(convocatoriaFechaLimiteRoot.get(ConvocatoriaReunion_.fechaLimite));
    sqFechaLimiteConvocatoria
        .where(cb.equal(convocatoriaFechaLimiteRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion));

    // Comite de la convocatoria si la convocatoria es de tipo ordinaria o
    // extraordinaria
    Subquery<Long> sqComiteConvocatoriaOrdinariaExtraordinaria = cq.subquery(Long.class);
    Root<ConvocatoriaReunion> convocatoriasOrdExtraordRoot = sqComiteConvocatoriaOrdinariaExtraordinaria
        .from(ConvocatoriaReunion.class);
    Join<ConvocatoriaReunion, Comite> joinConvocatoriaReunionOrdExtraordComite = convocatoriasOrdExtraordRoot
        .join(ConvocatoriaReunion_.comite);
    Join<ConvocatoriaReunion, TipoConvocatoriaReunion> joinConvocatoriaReunionOrdExtraordTipo = convocatoriasOrdExtraordRoot
        .join(ConvocatoriaReunion_.tipoConvocatoriaReunion);

    sqComiteConvocatoriaOrdinariaExtraordinaria.select(joinConvocatoriaReunionOrdExtraordComite.get(Comite_.id));
    sqComiteConvocatoriaOrdinariaExtraordinaria
        .where(
            cb.and(cb.equal(convocatoriasOrdExtraordRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion),
                joinConvocatoriaReunionOrdExtraordTipo.get(TipoConvocatoriaReunion_.id)
                    .in(Arrays.asList(Constantes.TIPO_CONVOCATORIA_REUNION_ORDINARIA,
                        Constantes.TIPO_CONVOCATORIA_REUNION_EXTRAORDINARIA))));

    // Comite de la convocatoria si la convocatoria es de tipo seguimiento
    Subquery<Long> sqComiteConvocatoriaSeguimiento = cq.subquery(Long.class);
    Root<ConvocatoriaReunion> convocatoriaSeguimientoRoot = sqComiteConvocatoriaSeguimiento
        .from(ConvocatoriaReunion.class);
    Join<ConvocatoriaReunion, Comite> joinConvocatoriaSeguimientoComite = convocatoriaSeguimientoRoot
        .join(ConvocatoriaReunion_.comite);
    Join<ConvocatoriaReunion, TipoConvocatoriaReunion> joinConvocatoriaSeguimientoTipo = convocatoriaSeguimientoRoot
        .join(ConvocatoriaReunion_.tipoConvocatoriaReunion);

    sqComiteConvocatoriaSeguimiento.select(joinConvocatoriaSeguimientoComite.get(Comite_.id));
    sqComiteConvocatoriaSeguimiento
        .where(cb.and(cb.equal(convocatoriaSeguimientoRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion),
            cb.equal(joinConvocatoriaSeguimientoTipo.get(TipoConvocatoriaReunion_.id),
                Constantes.TIPO_CONVOCATORIA_REUNION_SEGUIMIENTO)));

    // Define FROM clause
    Root<Memoria> root = cq.from(Memoria.class);
    Join<Memoria, Comite> joinMemoriaComite = root.join(Memoria_.comite);
    Join<Memoria, TipoEstadoMemoria> joinMemoriaTipoEstado = root.join(Memoria_.estadoActual);
    Join<Memoria, Retrospectiva> joinMemoriaRetrospectiva = root.join(Memoria_.retrospectiva, JoinType.LEFT);

    // Memorias convocatoria ordinaria o extraordinaria
    Predicate comiteConvocatoriaReunionOrdExtraord = cb.equal(joinMemoriaComite.get(Comite_.id),
        sqComiteConvocatoriaOrdinariaExtraordinaria);
    Predicate memoriasSecretaria = cb.equal(joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id),
        Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA);
    Predicate fechaEnvioMenorFechaLimite = cb.lessThanOrEqualTo(root.get(Memoria_.fechaEnvioSecretaria),
        sqFechaLimiteConvocatoria);
    Predicate retrospectivaSecretaria = cb.equal(
        joinMemoriaRetrospectiva.get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id),
        Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA);

    Predicate memoriasConvocatoriaOrdinariaExtraordinaria = cb.and(comiteConvocatoriaReunionOrdExtraord,
        cb.or(cb.and(memoriasSecretaria, fechaEnvioMenorFechaLimite), retrospectivaSecretaria));

    // Memorias convocatoria seguimiento
    Predicate comiteConvocatoriaReunionSeguimiento = cb.equal(joinMemoriaComite.get(Comite_.id),
        sqComiteConvocatoriaSeguimiento);
    Predicate memoriasSecretariaSeguimientoAnualYFinal = joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL,
            Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL));
    Predicate memoriasConvocatoriaSeguimiento = cb.and(comiteConvocatoriaReunionSeguimiento,
        cb.and(memoriasSecretariaSeguimientoAnualYFinal, fechaEnvioMenorFechaLimite));

    // Memorias activos
    Predicate memoriasActivas = cb.equal(root.get(Memoria_.activo), Boolean.TRUE);

    // WHERE
    predicates.add(memoriasActivas);
    predicates.add(cb.or(memoriasConvocatoriaOrdinariaExtraordinaria, memoriasConvocatoriaSeguimiento));

    // Join all restrictions
    cq.where(cb.and(predicates.toArray(new Predicate[] {})));

    // Execute query

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    TypedQuery<Memoria> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Memoria> result = typedQuery.getResultList();
    Page<Memoria> returnValue = new PageImpl<Memoria>(result, pageable, result.size());

    log.debug("findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion, Pageable pageable) - end");

    return returnValue;
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
  public Page<MemoriaPeticionEvaluacion> findMemoriasEvaluacion(Long idPeticionEvaluacion, Pageable pageable,
      String personaRefConsulta) {
    log.debug("findMemoriasEvaluacion( Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<MemoriaPeticionEvaluacion> cq = cb.createQuery(MemoriaPeticionEvaluacion.class);
    Root<Memoria> root = cq.from(Memoria.class);
    root.join(Memoria_.retrospectiva, JoinType.LEFT);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Memoria> rootCount = countQuery.from(Memoria.class);

    cq.multiselect(root.get(Memoria_.id), root.get(Memoria_.numReferencia), root.get(Memoria_.titulo),
        root.get(Memoria_.comite), root.get(Memoria_.estadoActual), root.get(Memoria_.requiereRetrospectiva),
        root.get(Memoria_.retrospectiva), getFechaEvaluacion(root, cb, cq).alias("fechaEvaluacion"),
        getFechaLimite(root, cb, cq).alias("fechaLimite"),
        isResponsable(root, cb, cq, personaRefConsulta).isNotNull().alias("isResponsable"), root.get(Memoria_.activo));

    cq.where(cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id), idPeticionEvaluacion),
        cb.isTrue(root.get(Memoria_.activo)));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    countQuery
        .where(cb.equal(rootCount.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id), idPeticionEvaluacion));
    countQuery.select(cb.count(rootCount));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<MemoriaPeticionEvaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<MemoriaPeticionEvaluacion> result = typedQuery.getResultList();
    Page<MemoriaPeticionEvaluacion> returnValue = new PageImpl<MemoriaPeticionEvaluacion>(result, pageable, count);

    log.debug("findMemoriasEvaluacion( Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Recupera la fecha de evaluación de la máxima versión de una memoria.
   * 
   * @param root root
   * @param cb   criteria builder
   * @param cq   criteria query
   * @return subquery que recupera la fecha de evaluación.
   */
  private Subquery<LocalDateTime> getFechaEvaluacion(Root<Memoria> root, CriteriaBuilder cb,
      CriteriaQuery<MemoriaPeticionEvaluacion> cq) {
    log.debug("getFechaEvaluacion : {} - start");

    Subquery<LocalDateTime> queryFechaEvaluacion = cq.subquery(LocalDateTime.class);
    Root<Evaluacion> subqRoot = queryFechaEvaluacion.from(Evaluacion.class);

    queryFechaEvaluacion.select(subqRoot.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.fechaEvaluacion))
        .where(cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.id), root.get(Memoria_.id)),
            cb.equal(subqRoot.get(Evaluacion_.version), root.get(Memoria_.version)),
            cb.equal(subqRoot.get(Evaluacion_.activo), true),
            cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.activo), true));

    log.debug("getFechaEvaluacion : {} - end");

    return queryFechaEvaluacion;
  }

  /**
   * Recupera la fecha limite de la máxima versión de una memoria.
   * 
   * @param root root
   * @param cb   criteria builder
   * @param cq   criteria query
   * @return subquery que recupera la fecha limite.
   */
  private Subquery<LocalDate> getFechaLimite(Root<Memoria> root, CriteriaBuilder cb,
      CriteriaQuery<MemoriaPeticionEvaluacion> cq) {
    log.debug("getFechaLimite : {} - start");

    Subquery<LocalDate> queryFechaLimite = cq.subquery(LocalDate.class);
    Root<Evaluacion> subqRoot = queryFechaLimite.from(Evaluacion.class);

    queryFechaLimite.select(subqRoot.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.fechaLimite)).where(
        cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.id), root.get(Memoria_.id)),
        cb.equal(subqRoot.get(Evaluacion_.version), root.get(Memoria_.version)),
        cb.equal(subqRoot.get(Evaluacion_.activo), true),
        cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.activo), true));

    log.debug("getFechaLimite : {} - end");

    return queryFechaLimite;
  }

  /**
   * Devuelve todas las memorias con la fecha límite y de evaluación.
   * 
   * @param specs              datos de búsqueda.
   * @param pageable           información de paginación
   * @param personaRefConsulta responsable
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAllMemoriasEvaluaciones(Specification<Memoria> specs, Pageable pageable,
      String personaRefConsulta) {
    log.debug("findAllMemoriasEvaluaciones( Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<MemoriaPeticionEvaluacion> cq = cb.createQuery(MemoriaPeticionEvaluacion.class);
    Root<Memoria> root = cq.from(Memoria.class);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Memoria> rootCount = countQuery.from(Memoria.class);
    countQuery.select(cb.count(rootCount));

    List<Predicate> predicates = new ArrayList<Predicate>();
    List<Predicate> predicatesCount = new ArrayList<Predicate>();

    if (personaRefConsulta != null) {
      Predicate predicateMemoria = cb.in(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
          .value(getIdsPeticionEvaluacionMemoria(root, cb, cq, specs, personaRefConsulta));
      Predicate predicateMemoriaCount = cb.in(rootCount.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
          .value(getIdsPeticionEvaluacionMemoria(rootCount, cb, cq, specs, personaRefConsulta));

      Predicate predicatePersonaRefPeticion = cb
          .equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef), personaRefConsulta);
      Predicate predicatePersonaRefPeticionCount = cb
          .equal(rootCount.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef), personaRefConsulta);
      Predicate predicatePersonaRefMemoria = cb.equal(root.get(Memoria_.personaRef), personaRefConsulta);
      Predicate predicatePersonaRefMemoriaCount = cb.equal(rootCount.get(Memoria_.personaRef), personaRefConsulta);
      predicates.add(cb.or(cb.or(predicatePersonaRefPeticion, predicatePersonaRefMemoria), predicateMemoria));
      predicatesCount
          .add(cb.or(cb.or(predicatePersonaRefPeticionCount, predicatePersonaRefMemoriaCount), predicateMemoriaCount));
    }

    // Where
    if (specs != null) {
      Predicate predicateSpecs = specs.toPredicate(root, cq, cb);
      predicates.add(predicateSpecs);
      Predicate predicateSpecsCount = specs.toPredicate(rootCount, cq, cb);
      predicatesCount.add(predicateSpecsCount);
    }

    cq.multiselect(root.get(Memoria_.id), root.get(Memoria_.numReferencia), root.get(Memoria_.titulo),
        root.get(Memoria_.comite), root.get(Memoria_.estadoActual),
        getFechaEvaluacion(root, cb, cq).alias("fechaEvaluacion"), getFechaLimite(root, cb, cq).alias("fechaLimite"),
        isResponsable(root, cb, cq, personaRefConsulta).isNotNull().alias("isResponsable"), root.get(Memoria_.activo))
        .distinct(true);

    cq.where(predicates.toArray(new Predicate[] {}));

    countQuery.where(predicatesCount.toArray(new Predicate[] {}));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<MemoriaPeticionEvaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<MemoriaPeticionEvaluacion> result = typedQuery.getResultList();
    Page<MemoriaPeticionEvaluacion> returnValue = new PageImpl<MemoriaPeticionEvaluacion>(result, pageable, count);

    log.debug("findAllMemoriasEvaluaciones( Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las peticiones de evaluación en las que es responsable de memoria
   * 
   * @param root
   * @param cb
   * @param cq
   * @param specsMem
   * @param personaRef
   * @return
   */
  private Subquery<Long> getIdsPeticionEvaluacionMemoria(Root<Memoria> root, CriteriaBuilder cb,
      CriteriaQuery<MemoriaPeticionEvaluacion> cq, Specification<Memoria> specsMem, String personaRef) {

    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - start");

    Subquery<Long> queryGetIdPeticionEvaluacion = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryGetIdPeticionEvaluacion.from(Memoria.class);

    List<Predicate> predicates = new ArrayList<Predicate>();
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.activo)));
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.activo)));
    if (personaRef != null) {
      predicates.add(cb.equal(subqRoot.get(Memoria_.personaRef), personaRef));
    }

    queryGetIdPeticionEvaluacion.select(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
        .where(predicates.toArray(new Predicate[] {}));
    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - end");

    return queryGetIdPeticionEvaluacion;
  }

  /**
   * Identifica si es responsable de la memoria el usuario de la consulta
   * 
   * @param root               root
   * @param cb                 criteria builder
   * @param cq                 criteria query
   * @param personaRefConsulta usuario de la consulta
   * @return subquery que la persona es responsable
   */
  private Subquery<Memoria> isResponsable(Root<Memoria> root, CriteriaBuilder cb,
      CriteriaQuery<MemoriaPeticionEvaluacion> cq, String personaRefConsulta) {
    log.debug("isResponsable : {} - start");

    Subquery<Memoria> queryResponsable = cq.subquery(Memoria.class);
    Root<Memoria> rootQueryResponsable = queryResponsable.from(Memoria.class);

    queryResponsable.select(rootQueryResponsable).where(
        cb.equal(rootQueryResponsable.get(Memoria_.id), root.get(Memoria_.id)),
        cb.equal(root.get(Memoria_.personaRef), personaRefConsulta != null ? personaRefConsulta : ""));

    log.debug("isResponsable : {} - end");

    return queryResponsable;
  }

}