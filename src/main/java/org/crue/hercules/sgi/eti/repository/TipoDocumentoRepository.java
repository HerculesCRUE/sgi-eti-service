package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoDocumento}.
 */

@Repository
public interface TipoDocumentoRepository
    extends JpaRepository<TipoDocumento, Long>, JpaSpecificationExecutor<TipoDocumento> {

  /**
   * Devuelve un tipo de documento activo por id
   * 
   * @param id id {link {@link TipoDocumento}
   * @return {@link TipoDocumento}
   */
  Optional<TipoDocumento> findByIdAndActivoTrue(Long id);

}