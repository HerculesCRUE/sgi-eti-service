package org.crue.hercules.sgi.eti.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * PeticionEvaluacion
 */

@Entity
@Table(name = "peticion_evaluacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PeticionEvaluacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "peticion_evaluacion_seq")
  @SequenceGenerator(name = "peticion_evaluacion_seq", sequenceName = "peticion_evaluacion_seq", allocationSize = 1)
  private Long id;

  /** Referencia solicitud convocatoria */
  @Column(name = "solicitud_convocatoria_ref", length = 250, nullable = true)
  private String solicitudConvocatoriaRef;

  /** Código */
  @Column(name = "codigo", length = 250, nullable = false)
  private String codigo;

  /** Título */
  @Column(name = "titulo", length = 250, nullable = false)
  private String titulo;

  /** Tipo Actividad */
  @ManyToOne
  @JoinColumn(name = "tipo_actividad_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PETICIONEVALUACION_TIPOACTIVIDAD"))
  private TipoActividad tipoActividad;

  /** Tipo Investigacion Tutelada */
  @ManyToOne
  @JoinColumn(name = "tipo_investigacion_tutelada_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PETICIONEVALUACION_TIPOINVESTIGACIONTUTELADA"))
  private TipoInvestigacionTutelada tipoInvestigacionTutelada;

  /** Fuente financiacion */
  @Column(name = "fuente_financiacion", length = 250, nullable = false)
  private String fuenteFinanciacion;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio")
  private LocalDate fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin")
  private LocalDate fechaFin;

  /** Resumen */
  @Column(name = "resumen", length = 8000, nullable = false)
  private String resumen;

  /** Valor social */
  @Column(name = "valor_social", length = 2000, nullable = false)
  private String valorSocial;

  /** Objetivos */
  @Column(name = "objetivos", length = 2000, nullable = false)
  private String objetivos;

  /** Diseño metodológico */
  @Column(name = "dis_metodologico", length = 2000, nullable = false)
  private String disMetodologico;

  /** Externo */
  @Column(name = "externo", columnDefinition = "boolean default false", nullable = false)
  private Boolean externo;

  /** Tiene fondos propios */
  @Column(name = "tiene_fondos_propios", columnDefinition = "boolean default false", nullable = false)
  private Boolean tieneFondosPropios;

  /** Referencia usuario */
  @Column(name = "persona_ref", length = 250, nullable = false)
  private String personaRef;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}