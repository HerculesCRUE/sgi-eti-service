package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.TipoMemoriaComiteRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoMemoriaComiteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * TipoMemoriaComiteServiceTest
 */
public class TipoMemoriaComiteServiceTest extends BaseServiceTest {

  @Mock
  private TipoMemoriaComiteRepository tipoMemoriaComiteRepository;

  @Mock
  private ComiteRepository comiteRepository;

  private TipoMemoriaComiteService tipoMemoriaComiteService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoMemoriaComiteService = new TipoMemoriaComiteServiceImpl(tipoMemoriaComiteRepository, comiteRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoMemoriaComite() {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    BDDMockito.given(tipoMemoriaComiteRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoMemoriaComite(1L, comite, tipoMemoria)));

    TipoMemoriaComite tipoMemoriaComite = tipoMemoriaComiteService.findById(1L);

    Assertions.assertThat(tipoMemoriaComite.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoriaComite.getComite()).isEqualTo(comite);
    Assertions.assertThat(tipoMemoriaComite.getTipoMemoria()).isEqualTo(tipoMemoria);

  }

  @Test
  public void find_NotFound_ThrowsTipoMemoriaComiteNotFoundException() throws Exception {
    BDDMockito.given(tipoMemoriaComiteRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoMemoriaComiteService.findById(1L))
        .isInstanceOf(TipoMemoriaComiteNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoMemoriaComite() {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(null, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    // given: Un nuevo TipoMemoriaComite
    TipoMemoriaComite tipoMemoriaComiteNew = generarMockTipoMemoriaComite(null, comite, tipoMemoria);

    TipoMemoriaComite tipoMemoriaComite = generarMockTipoMemoriaComite(1L, comite, tipoMemoria);

    BDDMockito.given(tipoMemoriaComiteRepository.save(tipoMemoriaComiteNew)).willReturn(tipoMemoriaComite);

    // when: Creamos el tipo memoria
    TipoMemoriaComite tipoMemoriaComiteCreado = tipoMemoriaComiteService.create(tipoMemoriaComiteNew);

    // then: El tipo memoria se crea correctamente
    Assertions.assertThat(tipoMemoriaComiteCreado).isNotNull();
    Assertions.assertThat(tipoMemoriaComiteCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoriaComiteCreado.getComite()).isEqualTo(comite);
    Assertions.assertThat(tipoMemoriaComiteCreado.getTipoMemoria()).isEqualTo(tipoMemoria);
  }

  @Test
  public void create_TipoMemoriaComiteWithId_ThrowsIllegalArgumentException() {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    // given: Un nuevo tipo de Memoria Comite que ya tiene id
    TipoMemoriaComite tipoMemoriaComiteNew = generarMockTipoMemoriaComite(1L, comite, tipoMemoria);
    // when: Creamos el tipo de Memoria Comite
    // then: Lanza una excepcion porque el tipo Memoria Comite ya tiene id
    Assertions.assertThatThrownBy(() -> tipoMemoriaComiteService.create(tipoMemoriaComiteNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoMemoriaComite() {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);
    TipoMemoria tipoMemoria2 = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    // given: Un nuevo tipo Memoria Comite con el servicio actualizado
    TipoMemoriaComite tipoMemoriaComiteServicioActualizado = generarMockTipoMemoriaComite(1L, comite, tipoMemoria2);

    TipoMemoriaComite tipoMemoriaComite = generarMockTipoMemoriaComite(1L, comite, tipoMemoria);

    BDDMockito.given(tipoMemoriaComiteRepository.findById(1L)).willReturn(Optional.of(tipoMemoriaComite));
    BDDMockito.given(tipoMemoriaComiteRepository.save(tipoMemoriaComite))
        .willReturn(tipoMemoriaComiteServicioActualizado);

    // when: Actualizamos el tipo Memoria Comite
    TipoMemoriaComite tipoMemoriaComiteActualizado = tipoMemoriaComiteService.update(tipoMemoriaComite);

    // then: El tipo Memoria Comite se actualiza correctamente.
    Assertions.assertThat(tipoMemoriaComiteActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoriaComiteActualizado.getTipoMemoria()).isEqualTo(tipoMemoria2);

  }

  @Test
  public void update_ThrowsTipoMemoriaComiteNotFoundException() {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    // given: Un nuevo tipo Memoria Comite a actualizar
    TipoMemoriaComite tipoMemoriaComite = generarMockTipoMemoriaComite(1L, comite, tipoMemoria);

    // then: Lanza una excepcion porque el tipo Memoria Comite no existe
    Assertions.assertThatThrownBy(() -> tipoMemoriaComiteService.update(tipoMemoriaComite))
        .isInstanceOf(TipoMemoriaComiteNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    // given: Un TipoMemoriaComite que venga sin id
    TipoMemoriaComite tipoMemoriaComite = generarMockTipoMemoriaComite(null, comite, tipoMemoria);

    Assertions.assertThatThrownBy(
        // when: update TipoMemoriaComite
        () -> tipoMemoriaComiteService.update(tipoMemoriaComite))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoMemoriaComiteService.deleteById(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoMemoriaComiteNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoMemoriaComiteRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoMemoriaComiteService.deleteById(1L))
        // then: Lanza TipoMemoriaComiteNotFoundException
        .isInstanceOf(TipoMemoriaComiteNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoMemoriaComite() {
    // given: Id existente
    BDDMockito.given(tipoMemoriaComiteRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoMemoriaComiteRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoMemoriaComiteService.deleteById(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllTipoMemoriaComite() {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    // given: One hundred TipoMemoriaComite
    List<TipoMemoriaComite> tipoMemoriaComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemoriaComites.add(generarMockTipoMemoriaComite(Long.valueOf(i), comite, tipoMemoria));
    }

    BDDMockito.doNothing().when(tipoMemoriaComiteRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> tipoMemoriaComiteService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoMemoriaComiteList() {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    // given: One hundred TipoMemoriaComite
    List<TipoMemoriaComite> tipoMemoriaComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemoriaComites.add(generarMockTipoMemoriaComite(Long.valueOf(i), comite, tipoMemoria));
    }

    BDDMockito.given(tipoMemoriaComiteRepository.findAll(ArgumentMatchers.<Specification<TipoMemoriaComite>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoMemoriaComites));

    // when: find unlimited
    Page<TipoMemoriaComite> page = tipoMemoriaComiteService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoMemoriaComites
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    // given: One hundred TipoMemoriaComites
    List<TipoMemoriaComite> tipoMemoriaComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comite.setComite("Comite" + (String.format("%03d", i)));
      TipoMemoriaComite tipoMemoriaComite = new TipoMemoriaComite();
      tipoMemoriaComite.setId(Long.valueOf(i));
      tipoMemoriaComite.setComite(comite);
      tipoMemoriaComite.setTipoMemoria(tipoMemoria);

      tipoMemoriaComites.add(tipoMemoriaComite);
    }

    BDDMockito.given(tipoMemoriaComiteRepository.findAll(ArgumentMatchers.<Specification<TipoMemoriaComite>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoMemoriaComite>>() {
          @Override
          public Page<TipoMemoriaComite> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoMemoriaComite> content = tipoMemoriaComites.subList(fromIndex, toIndex);
            Page<TipoMemoriaComite> page = new PageImpl<>(content, pageable, tipoMemoriaComites.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoMemoriaComite> page = tipoMemoriaComiteService.findAll(null, paging);

    // then: A Page with ten TipoMemoriaComites are returned containing
    // id='31' to '40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoMemoriaComite tipoMemoriaComite = page.getContent().get(i);
      Assertions.assertThat(tipoMemoriaComite.getId()).isEqualTo(j);
    }
  }

  @Test
  public void findByComite_WithId_ReturnsMemoria() {

    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(new Comite()));

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    List<TipoMemoriaComite> tipoMemoriasComite = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemoriasComite.add(generarMockTipoMemoriaComite(Long.valueOf(i), comite, tipoMemoria));
    }

    BDDMockito.given(tipoMemoriaComiteRepository.findByComiteIdAndComiteActivoTrue(1L, Pageable.unpaged()))
        .willReturn(new PageImpl<>(tipoMemoriasComite));

    // when: find unlimited
    Page<TipoMemoria> page = tipoMemoriaComiteService.findByComite(1L, Pageable.unpaged());
    // then: Get a page with one hundred TipoMemorias
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);

  }

  @Test
  public void findByComite_NotFound_ThrowsComiteNotFoundException() throws Exception {
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoMemoriaComiteService.findByComite(1L, null))
        .isInstanceOf(ComiteNotFoundException.class);
  }

  @Test
  public void findByComite_ComiteIdNull() throws Exception {

    try {
      // when: Creamos la memoria
      tipoMemoriaComiteService.findByComite(null, null);
      Assertions.fail("El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");
    }
  }

  /**
   * Función que devuelve un objeto TipoMemoriaComite
   * 
   * @param id          id del TipoMemoriaComite
   * @param comite      el Comite de TipoMemoriaComite
   * @param tipoMemoria el TipoMemoria de TipoMemoriaComite
   * @return el objeto TipoMemoriaComite
   */

  private TipoMemoriaComite generarMockTipoMemoriaComite(Long id, Comite comite, TipoMemoria tipoMemoria) {

    TipoMemoriaComite tipoMemoriaComite = new TipoMemoriaComite();
    tipoMemoriaComite.setId(id);
    tipoMemoriaComite.setComite(comite);
    tipoMemoriaComite.setTipoMemoria(tipoMemoria);

    return tipoMemoriaComite;
  }
}