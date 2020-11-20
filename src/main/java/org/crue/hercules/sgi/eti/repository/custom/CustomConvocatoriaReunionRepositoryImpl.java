package org.crue.hercules.sgi.eti.repository.custom;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import javax.persistence.criteria.Predicate;

import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Acta_;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ConvocatoriaReunion}.
 */
@Slf4j
@Component
public class CustomConvocatoriaReunionRepositoryImpl implements CustomConvocatoriaReunionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obteniene la entidad {@link ConvocatoriaReunionDatosGenerales} que contiene
   * la convocatoria con el identificador proporcionado, un campo que nos indica
   * el número de evaluaciones activas que no son revisión mínima y otro para
   * indicar si tiene Acta.
   *
   * @param idConvocatoria id de la convocatoria.
   * 
   * @return la {@link ConvocatoriaReunionDatosGenerales}
   */
  @Override
  public Optional<ConvocatoriaReunionDatosGenerales> findByIdWithDatosGenerales(Long idConvocatoria) {
    log.debug("findByIdWithDatosGenerales(Long idConvocatoria) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq = cb.createQuery(ConvocatoriaReunionDatosGenerales.class);

    // Define FROM clause
    Root<ConvocatoriaReunion> root = cq.from(ConvocatoriaReunion.class);

    // Where
    if (idConvocatoria != null) {
      cq.where(cb.equal(root.get(ConvocatoriaReunion_.id), idConvocatoria));
      // Execute query
      cq.multiselect(root, getNumEvaluacionesActivasNoRevMin(root, cb, cq, idConvocatoria).alias("numEvaluaciones"),
          getActaConvocatoria(root, cb, cq, idConvocatoria).alias("idActa"));
    }

    TypedQuery<ConvocatoriaReunionDatosGenerales> typedQuery = entityManager.createQuery(cq);
    ConvocatoriaReunionDatosGenerales result = null;

    try {
      result = typedQuery.getSingleResult();
    } catch (NoResultException nre) {
      return Optional.empty();
    }

    log.debug("findByIdWithDatosGenerales(Long idConvocatoria) - start");

    return Optional.of(result);
  }

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta
   * 
   * @param pageable la información de la paginación.
   *
   * @return la lista de convocatorias de reunión
   */
  @Override
  public Page<ConvocatoriaReunion> findConvocatoriasReunionSinActa(Pageable pageable) {
    log.debug("findConvocatoriasReunionSinActa(List<QueryCriteria> query) - start");

    // Create Query

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ConvocatoriaReunion> cq = cb.createQuery(ConvocatoriaReunion.class);

    // Definir FROM clause
    Root<ConvocatoriaReunion> root = cq.from(ConvocatoriaReunion.class);

    List<Predicate> listPredicates = new ArrayList<Predicate>();

    listPredicates.add(cb.equal(root.get(ConvocatoriaReunion_.activo), Boolean.TRUE));
    listPredicates.add(root.get(ConvocatoriaReunion_.id).in(getConvocatoriasActa(cb, cq, root)).not());

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    TypedQuery<ConvocatoriaReunion> typedQuery = entityManager.createQuery(cq);
    List<ConvocatoriaReunion> result = typedQuery.getResultList();
    Page<ConvocatoriaReunion> returnValue = new PageImpl<ConvocatoriaReunion>(result, pageable, result.size());

    log.debug("findConvocatoriasReunionSinActa(List<QueryCriteria> query) - end");

    return returnValue;

  }

  /**
   * Subquery para obtener el número de {@link Evaluacion} de una
   * {@link ConvocatoriaReunion} activas y que no son revisión mínima
   * 
   * @param root
   * @param cb
   * @param cq
   * @param idConvocatoria
   * @return Subquery<Long>
   */
  private Subquery<Long> getNumEvaluacionesActivasNoRevMin(Root<ConvocatoriaReunion> root, CriteriaBuilder cb,
      CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) {

    log.debug(
        "getNumEvaluacionesActivasNoRevMin(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - start");

    Subquery<Long> queryNumEvaluaciones = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryNumEvaluaciones.from(Evaluacion.class);
    queryNumEvaluaciones.select(cb.countDistinct(subqRoot.get(Evaluacion_.id)))
        .where(cb.and(
            cb.equal(subqRoot.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id), idConvocatoria),
            cb.and(cb.isTrue(subqRoot.get(Evaluacion_.activo)), cb.isFalse(subqRoot.get(Evaluacion_.esRevMinima)))));

    log.debug(
        "getNumEvaluacionesActivasNoRevMin(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - end");

    return queryNumEvaluaciones;
  }

  /**
   * Subquery para obtener el {@link Acta} de la {@link ConvocatoriaReunion}
   * 
   * @param root
   * @param cb
   * @param cq
   * @param idConvocatoria
   * @return Subquery<Long>
   */
  private Subquery<Long> getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb,
      CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) {

    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - start");

    Subquery<Long> queryGetActa = cq.subquery(Long.class);
    Root<Acta> subqRoot = queryGetActa.from(Acta.class);
    queryGetActa.select(subqRoot.get(Acta_.id))
        .where(cb.and(cb.equal(subqRoot.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id), idConvocatoria),
            cb.isTrue(subqRoot.get(Acta_.activo))));
    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - end");

    return queryGetActa;
  }

  /**
   * Devuelve una subconsulta con el listado de Convocatorias de Reunión que no
   * tienen acta asociada y que además esté activa
   * 
   * @param cb   Criteria builder
   * @param cq   criteria query
   * @param root root a ConvocatoriaReunion
   * 
   * @return Subquery<Long> Listado de Convocatorias de Reunión que no tienen acta
   *         asociada
   */
  private Subquery<Long> getConvocatoriasActa(CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunion> cq,
      Root<ConvocatoriaReunion> root) {

    log.debug("getConvocatoriasActa : {} - start");

    Subquery<Long> queryActasConvocatoria = cq.subquery(Long.class);
    Root<Acta> subqRoot = queryActasConvocatoria.from(Acta.class);
    queryActasConvocatoria.select(subqRoot.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id))
        .where(cb.equal(subqRoot.get(Acta_.activo), Boolean.TRUE));

    log.debug("getConvocatoriasActa : {} - end");

    return queryActasConvocatoria;
  }

}
