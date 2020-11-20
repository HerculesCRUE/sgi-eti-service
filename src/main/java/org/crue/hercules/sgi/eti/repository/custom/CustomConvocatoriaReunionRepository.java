package org.crue.hercules.sgi.eti.repository.custom;

import java.util.Optional;

import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * CustomConvocatoriaReunionRepository
 */
@Component
public interface CustomConvocatoriaReunionRepository {

  /**
   * Obteniene la entidad {@link ConvocatoriaReunionDatosGenerales} que contiene
   * la convocatoria con el identificador proporcionado y un campo que nos indica
   * el número de evaluaciones activas que no son revisión mínima.
   *
   * @param idConvocatoria id de la convocatoria.
   * 
   * @return la {@link ConvocatoriaReunionDatosGenerales}
   */
  Optional<ConvocatoriaReunionDatosGenerales> findByIdWithDatosGenerales(Long idConvocatoria);

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta**
   * 
   * 
   * @param pageable datos de la paginación
   * @return lista paginada de convocatoria reunión
   */
  public Page<ConvocatoriaReunion> findConvocatoriasReunionSinActa(Pageable pageable);

}
