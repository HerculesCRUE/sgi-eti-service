package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.repository.custom.CustomActaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Acta}.
 */
@Repository
public interface ActaRepository
    extends JpaRepository<Acta, Long>, JpaSpecificationExecutor<Acta>, CustomActaRepository {

}