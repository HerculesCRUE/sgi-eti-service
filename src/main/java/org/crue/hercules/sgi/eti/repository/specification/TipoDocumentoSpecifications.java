package org.crue.hercules.sgi.eti.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class TipoDocumentoSpecifications {

  public static Specification<TipoDocumento> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoDocumento_.activo), Boolean.TRUE);
    };
  }

  public static Specification<TipoDocumento> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoDocumento_.activo), Boolean.FALSE);
    };
  }

  public static Specification<TipoDocumento> byIdNotIn(List<Long> idsTipoDocumento) {
    return (root, query, cb) -> {
      return cb.not(root.get(TipoDocumento_.id).in(idsTipoDocumento));
    };
  }
}
