package org.crue.hercules.sgi.eti.repository;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class EquipoTrabajoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private EquipoTrabajoRepository repository;

  @Test
  public void findAllByPeticionEvaluacionId_WithPaging_ReturnsPage() throws Exception {

    // given: Hay 2 equipoTrabajo para la 1ª peticion evaluacion
    TipoActividad tipoActividad = generarMockTipoActividad();
    TipoActividad tipoActividadCreado = entityManager.persistFlushFind(tipoActividad);

    PeticionEvaluacion peticionEvaluacion1 = generarMockPeticionEvaluacion(tipoActividadCreado);
    PeticionEvaluacion peticionEvaluacion1Creada = entityManager.persistFlushFind(peticionEvaluacion1);

    PeticionEvaluacion peticionEvaluacion2 = generarMockPeticionEvaluacion(tipoActividadCreado);
    PeticionEvaluacion peticionEvaluacion2Creada = entityManager.persistFlushFind(peticionEvaluacion2);

    EquipoTrabajo equipoTrabajo1 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo1);

    EquipoTrabajo equipoTrabajo2 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo2);

    EquipoTrabajo equipoTrabajo3 = generarMockEquipoTrabajo(peticionEvaluacion2Creada);
    entityManager.persistFlushFind(equipoTrabajo3);

    // página 0 con 5 elementos por página
    Pageable pageable = PageRequest.of(0, 5);

    // when: Se buscan los datos paginados
    Page<EquipoTrabajoWithIsEliminable> result = repository.findAllByPeticionEvaluacionId(peticionEvaluacion1.getId(),
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).as("Number").isEqualTo(0);
    Assertions.assertThat(result.getSize()).as("Size").isEqualTo(5);
    Assertions.assertThat(result.getTotalElements()).as("TotalElements").isEqualTo(2);
    Assertions.assertThat(result.getContent()).as("Content").isNotEmpty();
    Assertions.assertThat(result.getContent().size()).as("Content.size").isEqualTo(2);
  }

  @Test
  public void findAllByPeticionEvaluacionId_WithPaging_ReturnsEmptyPage() throws Exception {

    // given: Hay 0 equipoTrabajo para la 2ª peticion evaluacion
    TipoActividad tipoActividad = generarMockTipoActividad();
    TipoActividad tipoActividadCreado = entityManager.persistFlushFind(tipoActividad);

    PeticionEvaluacion peticionEvaluacion1 = generarMockPeticionEvaluacion(tipoActividadCreado);
    PeticionEvaluacion peticionEvaluacion1Creada = entityManager.persistFlushFind(peticionEvaluacion1);

    PeticionEvaluacion peticionEvaluacion2 = generarMockPeticionEvaluacion(tipoActividadCreado);
    entityManager.persistFlushFind(peticionEvaluacion2);

    EquipoTrabajo equipoTrabajo1 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo1);

    EquipoTrabajo equipoTrabajo2 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo2);

    // página 0 con 5 elementos por página
    Pageable pageable = PageRequest.of(0, 5);

    // when: Se buscan los datos paginados
    Page<EquipoTrabajoWithIsEliminable> result = repository.findAllByPeticionEvaluacionId(peticionEvaluacion2.getId(),
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(0);
    Assertions.assertThat(result.getSize()).isEqualTo(5);
    Assertions.assertThat(result.getTotalElements()).isEqualTo(0);
    Assertions.assertThat(result.getContent()).isEmpty();
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @return el objeto TipoActividad
   */
  public TipoActividad generarMockTipoActividad() {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    return tipoActividad;
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   *
   * @param tipoActividad el tipo de actividad de la PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  public PeticionEvaluacion generarMockPeticionEvaluacion(TipoActividad tipoActividad) {

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setCodigo("Codigo");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación");
    peticionEvaluacion.setObjetivos("Objetivos");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("Titulo");
    peticionEvaluacion.setPersonaRef("user-00");
    peticionEvaluacion.setValorSocial("Valor social");
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  public EquipoTrabajo generarMockEquipoTrabajo(PeticionEvaluacion peticionEvaluacion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00");

    return equipoTrabajo;
  }

}