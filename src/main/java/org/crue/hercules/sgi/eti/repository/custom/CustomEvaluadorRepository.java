package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Evaluador}.
 */
@Component
public interface CustomEvaluadorRepository {

  /**
   * Devuelve los evaluadores activos del comité indicado que no entre en
   * conflicto de intereses con ningún miembro del equipo investigador de la
   * memoria.
   * 
   * @param idComite  Identificador del {@link Comite}
   * @param idMemoria Identificador de la {@link Memoria}
   * @param pageable  la información de paginación.
   * @return lista de evaluadores sin conflictos de intereses
   */
  Page<Evaluador> findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria, Pageable pageable);

}
