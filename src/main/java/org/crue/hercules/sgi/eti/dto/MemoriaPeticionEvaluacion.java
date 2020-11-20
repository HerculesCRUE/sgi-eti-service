package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoriaPeticionEvaluacion implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;

  private String numReferencia;

  private String titulo;

  private Comite comite;

  private TipoEstadoMemoria estadoActual;

  private boolean requiereRetrospectiva;

  private Retrospectiva retrospectiva;

  private LocalDateTime fechaEvaluacion;

  private LocalDate fechaLimite;

  private boolean isResponsable;

  public MemoriaPeticionEvaluacion(Long id, String numReferencia, String titulo, Comite comite,
      TipoEstadoMemoria estadoActual, LocalDateTime fechaEvaluacion, LocalDate fechaLimite, boolean isResponsable) {

    this.id = id;
    this.numReferencia = numReferencia;
    this.titulo = titulo;
    this.comite = comite;
    this.estadoActual = estadoActual;
    this.fechaEvaluacion = fechaEvaluacion;
    this.fechaLimite = fechaLimite;
    this.isResponsable = isResponsable;

  }

}