package org.crue.hercules.sgi.eti.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.service.impl.PeticionEvaluacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * PeticionEvaluacionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class PeticionEvaluacionServiceTest extends BaseServiceTest {

  @Mock
  private PeticionEvaluacionRepository peticionEvaluacionRepository;

  private PeticionEvaluacionService peticionEvaluacionService;

  @BeforeEach
  public void setUp() throws Exception {
    peticionEvaluacionService = new PeticionEvaluacionServiceImpl(peticionEvaluacionRepository);
  }

  @Test
  public void find_WithId_ReturnsPeticionEvaluacion() {
    BDDMockito.given(peticionEvaluacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1")));

    PeticionEvaluacion peticionEvaluacion = peticionEvaluacionService.findById(1L);

    Assertions.assertThat(peticionEvaluacion.getId()).isEqualTo(1L);

    Assertions.assertThat(peticionEvaluacion.getTitulo()).isEqualTo("PeticionEvaluacion1");

  }

  @Test
  public void find_NotFound_ThrowsPeticionEvaluacionNotFoundException() throws Exception {
    BDDMockito.given(peticionEvaluacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> peticionEvaluacionService.findById(1L))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);
  }

  @Test
  public void create_ReturnsPeticionEvaluacion() {
    // given: Un nuevo PeticionEvaluacion
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacionNew");

    BDDMockito.given(peticionEvaluacionRepository.save(peticionEvaluacionNew)).willReturn(peticionEvaluacion);

    // when: Creamos el PeticionEvaluacion
    PeticionEvaluacion peticionEvaluacionCreado = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: El PeticionEvaluacion se crea correctamente
    Assertions.assertThat(peticionEvaluacionCreado).isNotNull();
    Assertions.assertThat(peticionEvaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(peticionEvaluacionCreado.getTitulo()).isEqualTo("PeticionEvaluacionNew");
  }

  @Test
  public void create_PeticionEvaluacionWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo de actividad que ya tiene id
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(1L, "PeticionEvaluacionNew");
    // when: Creamos el peticionEvaluacion
    // then: Lanza una excepcion porque el peticionEvaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> peticionEvaluacionService.create(peticionEvaluacionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsPeticionEvaluacion() {
    // given: Un nuevo peticionEvaluacion con el servicio actualizado
    PeticionEvaluacion peticionEvaluacionServicioActualizado = generarMockPeticionEvaluacion(1L,
        "PeticionEvaluacion1 actualizada");

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");

    BDDMockito.given(peticionEvaluacionRepository.findById(1L)).willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(peticionEvaluacionRepository.save(peticionEvaluacion))
        .willReturn(peticionEvaluacionServicioActualizado);

    // when: Actualizamos el peticionEvaluacion
    PeticionEvaluacion peticionEvaluacionActualizado = peticionEvaluacionService.update(peticionEvaluacion);

    // then: El peticionEvaluacion se actualiza correctamente.
    Assertions.assertThat(peticionEvaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(peticionEvaluacionActualizado.getTitulo()).isEqualTo("PeticionEvaluacion1 actualizada");

  }

  @Test
  public void update_ThrowsPeticionEvaluacionNotFoundException() {
    // given: Un nuevo peticionEvaluacion a actualizar
    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion");

    // then: Lanza una excepcion porque el peticionEvaluacion no existe
    Assertions.assertThatThrownBy(() -> peticionEvaluacionService.update(peticionEvaluacion))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un PeticionEvaluacion que venga sin id
    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(null, "PeticionEvaluacion");

    Assertions.assertThatThrownBy(
        // when: update PeticionEvaluacion
        () -> peticionEvaluacionService.update(peticionEvaluacion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> peticionEvaluacionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsPeticionEvaluacionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(peticionEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> peticionEvaluacionService.delete(1L))
        // then: Lanza PeticionEvaluacionNotFoundException
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesPeticionEvaluacion() {
    // given: Id existente
    BDDMockito.given(peticionEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(peticionEvaluacionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> peticionEvaluacionService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullPeticionEvaluacionList() {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(peticionEvaluacionRepository.findAll(ArgumentMatchers.<Specification<PeticionEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(peticionEvaluaciones));

    // when: find unlimited
    Page<PeticionEvaluacion> page = peticionEvaluacionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred PeticionEvaluaciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred PeticionEvaluaciones
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(peticionEvaluacionRepository.findAll(ArgumentMatchers.<Specification<PeticionEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<PeticionEvaluacion> content = peticionEvaluaciones.subList(fromIndex, toIndex);
            Page<PeticionEvaluacion> page = new PageImpl<>(content, pageable, peticionEvaluaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<PeticionEvaluacion> page = peticionEvaluacionService.findAll(null, paging);

    // then: A Page with ten PeticionEvaluaciones are returned containing
    // descripcion='PeticionEvaluacion031' to 'PeticionEvaluacion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      PeticionEvaluacion peticionEvaluacion = page.getContent().get(i);
      Assertions.assertThat(peticionEvaluacion.getTitulo()).isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */

  public PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación" + id);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial("Valor social");
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }
}