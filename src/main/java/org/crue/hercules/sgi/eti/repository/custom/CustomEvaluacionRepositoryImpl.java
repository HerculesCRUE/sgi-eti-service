package org.crue.hercules.sgi.eti.repository.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario_;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Evaluador_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Evaluacion}.
 */
@Component
@Slf4j
public class CustomEvaluacionRepositoryImpl implements CustomEvaluacionRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtener todas las entidades {@link EvaluacionWithNumComentario} paginadas
   * asociadas a una memoria y anteriores a la evaluación recibida.
   *
   * @param idMemoria    id de la memoria.
   * @param idEvaluacion id de la evaluación.
   * @param pageable     la información de la paginación.
   * @return la lista de entidades {@link EvaluacionWithNumComentario} paginadas
   *         y/o filtradas.
   */
  public Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Pageable pageable) {
    // TODO: Revisar uso de pageable, porque se página pero no se tiene en cuenta la
    // ordenación y se está paginando pero sin hacer la count del query
    log.debug("findEvaluacionesAnterioresByMemoria : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<EvaluacionWithNumComentario> cq = cb.createQuery(EvaluacionWithNumComentario.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    cq.multiselect(root.alias("evaluacion"), getNumComentarios(root, cb, cq).alias("numComentarios"));

    cq.where(cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.id), idMemoria),
        cb.notEqual(root.get(Evaluacion_.id), idEvaluacion), cb.isTrue(root.get(Evaluacion_.activo)));

    TypedQuery<EvaluacionWithNumComentario> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<EvaluacionWithNumComentario> result = typedQuery.getResultList();

    Page<EvaluacionWithNumComentario> returnValue = new PageImpl<EvaluacionWithNumComentario>(result, pageable,
        result.size());

    log.debug("findEvaluacionesAnterioresByMemoria : {} - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas
   * asociadas a determinados tipos de seguimiento final
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  public Page<Evaluacion> findByEvaluacionesEnSeguimientoFinal(String query, Pageable pageable) {

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);

    List<Predicate> listPredicates = getPredicatesEvaluacionesEnSeguimientoFinal(root, cb);
    List<Predicate> listPredicatesCount = getPredicatesEvaluacionesEnSeguimientoFinal(rootCount, cb);

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicates.add(spec.toPredicate(root, cq, cb));
      listPredicatesCount.add(spec.toPredicate(rootCount, cq, cb));
    }

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Ordenación
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    countQuery.select(cb.count(rootCount));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    // Paginación
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<Evaluacion>(result, pageable, count);

    return returnValue;

  }

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  public Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable) {
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    Predicate predicateContent = this.getByMemoriaAndRetrospectivaEnEvaluacionPredicate(query, root, cq, cb);
    Predicate predicateCount = this.getByMemoriaAndRetrospectivaEnEvaluacionPredicate(query, rootCount, countQuery, cb);

    // Filtros
    cq.where(predicateContent);

    countQuery.where(predicateCount);

    // Ordenación
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Paginación
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<Evaluacion>(result, pageable, count);

    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable) - end");

    return returnValue;

  }

  private Predicate getByMemoriaAndRetrospectivaEnEvaluacionPredicate(String query, Root<Evaluacion> root,
      CriteriaQuery<?> cq, CriteriaBuilder cb) {
    Predicate predicate = cb.and(cb.or(
        cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 1L),
            cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasRestropectivas(cb, cq, root))),
        cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L),
            cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasEstadoActual(cb, cq, root)))));

    predicate = cb.and(predicate, cb.equal(root.get(Evaluacion_.activo), Boolean.TRUE));

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      predicate = cb.and(predicate, spec.toPredicate(root, cq, cb));
    }

    return predicate;
  }

  /**
   * Devuelve una subconsulta con el listado de Memorias con estado 4 o 5 donde
   * además la versión de la memoria sea igual a la versión del listado de
   * evaluación.
   * 
   * @return Subquery<Long> Listado de Memorias con estado En Evaluacion o En
   *         secretaría revisión mínima
   */
  private Subquery<Long> getIdsMemoriasEstadoActual(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Evaluacion> root) {

    log.debug("getIdsMemoriasEstadoActual : {} - start");

    Subquery<Long> queryMemoriasEstadoActual = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryMemoriasEstadoActual.from(Memoria.class);
    queryMemoriasEstadoActual.select(subqRoot.get(Memoria_.id))
        .where(cb.and(subqRoot.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id).in(Arrays.asList(4L, 5L)),
            cb.equal(root.get(Evaluacion_.version), subqRoot.get(Memoria_.version))));

    log.debug("getIdsMemoriasEstadoActual : {} - end");

    return queryMemoriasEstadoActual;
  }

  /**
   * Recupera las evaluaciones del tipo memoria en estado 'En evaluacion' (id = 4)
   * o 'En secretaria revisión minima'(id = 5), o tipo retrospectiva, memoria que
   * requiere retrospectiva y el estado de la RETROSPECTIVA es 'En evaluacion' (id
   * = 4).
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findByEvaluador(String personaRef, String query, Pageable pageable) {
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Evaluacion> rootCount = countQuery.from(Evaluacion.class);
    countQuery.select(cb.count(rootCount));

    // Define FROM clause
    Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);

    List<Predicate> listPredicates = getPredicatesByEvaluador(rootEvaluacion, cb, cq, personaRef);

    List<Predicate> listPredicatesCount = getPredicatesByEvaluador(rootCount, cb, cq, personaRef);

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicates.add(spec.toPredicate(rootEvaluacion, cq, cb));
      listPredicatesCount.add(spec.toPredicate(rootCount, cq, cb));
    }

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Ordenación
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), rootEvaluacion, cb);
    cq.orderBy(orders);

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    // Paginación
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }
    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<Evaluacion>(result, pageable, count);

    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador.
   * 
   * @param personaRef Persona Ref del {@link Evaluador}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query,
      Pageable pageable) {
    // TODO: Revisar uso pageable. Aunque se está paginando, no se está calculando
    // el count total.
    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);

    // Evaluaciones en seguimiento
    List<Predicate> listPredicates = getPredicateEvaluacionEnSeguimiento(rootEvaluacion, cb, personaRef);

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = SgiRSQLJPASupport.toSpecification(query);
      listPredicates.add(spec.toPredicate(rootEvaluacion, cq, cb));
    }

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Ordenación
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), rootEvaluacion, cb);
    cq.orderBy(orders);

    // Paginación
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }
    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<Evaluacion>(result, pageable, result.size());

    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Crea el predicate necesario para recuperar las evaluaciones de tipo memoria
   * que se encuentran en seguimiento:
   * 
   * "En evaluación seguimiento anual" (id = 11), "En evaluación seguimiento
   * final" (id = 12) o "En secretaría seguimiento final aclaraciones" (id = 13)
   * 
   * @param rootEvaluacion
   * @param cb
   * @return lista con los predicates necesarios
   */
  private List<Predicate> getPredicateEvaluacionEnSeguimiento(Root<Evaluacion> rootEvaluacion, CriteriaBuilder cb,
      String personaRef) {

    log.debug("getPredicateEvaluacionEnSeguimiento : {} - start");
    List<Predicate> listPredicates = new ArrayList<Predicate>();

    listPredicates.add(rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(11L, 12L, 13L)));

    listPredicates.add(cb.isTrue(rootEvaluacion.get(Evaluacion_.activo)));
    listPredicates
        .add(rootEvaluacion.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id).in(Arrays.asList(3L, 4L)));
    listPredicates
        .add(cb.or(cb.equal(rootEvaluacion.get(Evaluacion_.evaluador1).get(Evaluador_.personaRef), personaRef),
            cb.equal(rootEvaluacion.get(Evaluacion_.evaluador2).get(Evaluador_.personaRef), personaRef)));

    Predicate memoriaVersion = cb.equal(rootEvaluacion.get(Evaluacion_.version),
        rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.version));
    listPredicates.add(memoriaVersion);

    log.debug("getPredicateEvaluacionEnSeguimiento : {} - end");

    return listPredicates;
  }

  private Subquery<Long> getNumComentarios(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<EvaluacionWithNumComentario> cq) {

    log.debug("getNumComentarios : {} - start");

    Subquery<Long> queryNumComentarios = cq.subquery(Long.class);
    Root<Comentario> subqRoot = queryNumComentarios.from(Comentario.class);
    queryNumComentarios.select(cb.count(subqRoot.get(Comentario_.id)))
        .where(cb.equal(subqRoot.get(Comentario_.evaluacion).get(Evaluacion_.id), root.get(Evaluacion_.id)));

    log.debug("getNumComentarios : {} - end");
    return queryNumComentarios;
  }

  /**
   * Devuelve una subconsulta con el listado de Memorias con Retrospectivas en
   * estado 4 (En Evaluacion) donde la versión de la memoria sea igual a la
   * versión del listado de evaluación.
   * 
   * @return Subquery<Long> Listado de Memorias con Retrospectivas en estado En
   *         Evaluacion
   */
  private Subquery<Long> getIdsMemoriasRestropectivas(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Evaluacion> root) {

    log.debug("getIdsMemoriasRestropectivas : {} - start");

    Subquery<Long> queryMemorias = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryMemorias.from(Memoria.class);
    queryMemorias.select(subqRoot.get(Memoria_.id)).where(
        cb.and(cb.in(subqRoot.get(Memoria_.retrospectiva).get(Retrospectiva_.id)).value(getRetrospectivas(cb, cq)),
            cb.equal(root.get(Evaluacion_.version), subqRoot.get(Memoria_.version))));

    log.debug("getIdsMemoriasRestropectivas : {} - end");

    return queryMemorias;
  }

  /**
   * Devuelve una subconsulta con el listado de retrospectivas en estado 4 (En
   * Evaluacion)
   * 
   * @return Subquery<Long> Listado de Retrospectivas en estado En Evaluacion
   */

  private Subquery<Long> getRetrospectivas(CriteriaBuilder cb, CriteriaQuery<?> cq) {

    log.debug("getRetrospectivas : {} - start");

    Subquery<Long> queryRetrospectivas = cq.subquery(Long.class);
    Root<Retrospectiva> subqRoot = queryRetrospectivas.from(Retrospectiva.class);
    queryRetrospectivas.select(subqRoot.get(Retrospectiva_.id))
        .where(cb.equal(subqRoot.get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id), 4L));

    log.debug("getRetrospectivas : {} - end");

    return queryRetrospectivas;

  }

  /**
   * Devuelve la lista de predicados para la búsqueda de evaluaciones en
   * seguimiento final.
   * 
   * @param root root
   * @param cb   Criteria builder
   * @return lista de predicates.
   */
  private List<Predicate> getPredicatesEvaluacionesEnSeguimientoFinal(Root<Evaluacion> root, CriteriaBuilder cb) {
    log.debug("getPredicatesEvaluacionesEnSeguimientoFinal : {} - start");
    List<Predicate> listPredicates = new ArrayList<Predicate>();

    listPredicates.add(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id).in(Arrays.asList(3L, 4L)));

    listPredicates.add(root.get(Evaluacion_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)

        .in(Arrays.asList(18L, 19L, 13L)));

    listPredicates.add(cb.equal(root.get(Evaluacion_.version), root.get(Evaluacion_.memoria).get(Memoria_.version)));

    listPredicates.add(cb.and(cb.isTrue(root.get(Evaluacion_.activo))));
    log.debug("getPredicatesEvaluacionesEnSeguimientoFinal : {} - end");
    return listPredicates;
  }

  /**
   * Recupera los predicados para la búsqueda de evaluaciones por evaluador.
   * 
   * @param root       Root
   * @param cb         criteria builder
   * @param cq         criteria query
   * @param personaRef Referencia de persona
   * @return listado de predicados
   */
  private List<Predicate> getPredicatesByEvaluador(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<Evaluacion> cq, String personaRef) {

    List<Predicate> listPredicates = new ArrayList<>();
    Predicate memoriaVersion = cb.equal(root.get(Evaluacion_.version),
        root.get(Evaluacion_.memoria).get(Memoria_.version));

    // Memoria en estado 'En evaluacion' (id = 4)
    // o 'En secretaria revisión minima'(id = 5)

    Predicate memoria = cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L),
        cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasEstadoActual(cb, cq, root)),
        memoriaVersion, cb.or(cb.equal(root.get(Evaluacion_.evaluador1).get(Evaluador_.personaRef), personaRef),
            cb.equal(root.get(Evaluacion_.evaluador2).get(Evaluador_.personaRef), personaRef)));

    // Tipo retrospectiva, memoria Requiere retrospectiva y el estado de la
    // RETROSPECTIVA es 'En evaluacion' (id = 4)

    Subquery<Long> queryRetrospectiva = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryRetrospectiva.from(Evaluacion.class);

    Predicate requiereRetrospectiva = cb.isTrue(subqRoot.get(Evaluacion_.memoria).get(Memoria_.requiereRetrospectiva));
    Predicate estadoRetrospectiva = cb
        .equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.retrospectiva).get(Retrospectiva_.id), 4L);
    Predicate comite = cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id), 2L);
    Predicate tipoEvaluacion = cb.equal(subqRoot.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 1L);
    Predicate evaluador = cb.or(cb.equal(subqRoot.get(Evaluacion_.evaluador1).get(Evaluador_.personaRef), personaRef),
        cb.equal(subqRoot.get(Evaluacion_.evaluador2).get(Evaluador_.personaRef), personaRef));

    queryRetrospectiva.select(subqRoot.get(Evaluacion_.id))
        .where(cb.and(requiereRetrospectiva, estadoRetrospectiva, comite, tipoEvaluacion, evaluador, memoriaVersion));

    Predicate retrospectiva = cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(queryRetrospectiva);

    listPredicates.add(cb.or(memoria, retrospectiva));

    return listPredicates;
  }
}
