package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.crue.hercules.sgi.eti.service.TipoMemoriaComiteService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * TipoMemoriaComiteControllerTest
 */
@WebMvcTest(TipoMemoriaComiteController.class)
public class TipoMemoriaComiteControllerTest extends BaseControllerTest {

  @MockBean
  private TipoMemoriaComiteService tipoMemoriaComiteService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH = "/tipomemoriacomites";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-VER" })
  public void getTipoMemoriaComite_WithId_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    BDDMockito.given(tipoMemoriaComiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoMemoriaComite(1L, comite, tipoMemoria)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoMemoria").value(tipoMemoria))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(comite));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-VER" })
  public void getTipoMemoriaComite_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoMemoriaComiteService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoMemoriaComiteNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-EDITAR" })
  public void newTipoMemoriaComite_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    // given: Un tipo memoria comite nuevo
    String nuevoTipoMemoriaComiteJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"tipoMemoria\": {\"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}}";

    TipoMemoriaComite tipoMemoriaComite = generarMockTipoMemoriaComite(1L, comite, tipoMemoria);

    BDDMockito.given(tipoMemoriaComiteService.create(ArgumentMatchers.<TipoMemoriaComite>any()))
        .willReturn(tipoMemoriaComite);

    // when: Creamos un tipo memoria comite
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoMemoriaComiteJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo tipo memoria comite y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(comite));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-EDITAR" })
  public void newTipoMemoriaComite_Error_Returns400() throws Exception {
    // given: Un tipo memoria comite nuevo que produce un error al crearse
    String nuevoTipoMemoriaComiteJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"tipoMemoria\": {\"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}}";

    BDDMockito.given(tipoMemoriaComiteService.create(ArgumentMatchers.<TipoMemoriaComite>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoMemoriaComiteJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-EDITAR" })
  public void replaceTipoMemoriaComite_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    // given: Un tipo memoria comite a modificar
    String replaceTipoMemoriaComiteJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"tipoMemoria\": {\"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}}";

    TipoMemoriaComite tipoMemoriaComite = generarMockTipoMemoriaComite(1L, comite, tipoMemoria);

    BDDMockito.given(tipoMemoriaComiteService.update(ArgumentMatchers.<TipoMemoriaComite>any()))
        .willReturn(tipoMemoriaComite);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoMemoriaComiteJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el tipo memoria comite y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoMemoria").value(tipoMemoria));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-EDITAR" })
  public void replaceTipoMemoriaComite_NotFound() throws Exception {
    // given: Un tipo memoria comite a modificar
    String replaceTipoMemoriaComiteJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"tipoMemoria\": {\"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}}";

    BDDMockito.given(tipoMemoriaComiteService.update(ArgumentMatchers.<TipoMemoriaComite>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoMemoriaComiteNotFoundException(((TipoMemoriaComite) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoMemoriaComiteJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-EDITAR" })
  public void removeTipoMemoriaComite_ReturnsOk() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    BDDMockito.given(tipoMemoriaComiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoMemoriaComite(1L, comite, tipoMemoria));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-VER" })
  public void findAll_Unlimited_ReturnsFullTipoMemoriaComiteList() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    // given: One hundred TipoMemoriaComite
    List<TipoMemoriaComite> tipoMemoriaComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemoriaComites.add(generarMockTipoMemoriaComite(Long.valueOf(i), comite, tipoMemoria));
    }

    BDDMockito.given(tipoMemoriaComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoMemoriaComites));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred TipoMemoriaComite
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoMemoriaComite empty
    List<TipoMemoriaComite> tipoMemoriaComites = new ArrayList<>();

    BDDMockito.given(tipoMemoriaComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoMemoriaComites));
    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-VER" })
  public void findAll_WithPaging_ReturnsTipoMemoriaComiteSubList() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    // given: One hundred TipoMemoriaComite
    List<TipoMemoriaComite> tipoMemoriaComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comite.setComite("Comite" + String.format("%03d", i));
      tipoMemoriaComites.add(generarMockTipoMemoriaComite(Long.valueOf(i), comite, tipoMemoria));
    }

    BDDMockito.given(tipoMemoriaComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoMemoriaComite>>() {
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

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked TipoMemoriaComites are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoMemoriaComite> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoMemoriaComite>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoMemoriaComite tipoMemoriaComite = actual.get(i);
      Assertions.assertThat(tipoMemoriaComite.getId()).isEqualTo(j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOMEMORIACOMITE-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoMemoriaComiteList() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    // given: One hundred TipoMemoriaComite and a search query
    List<TipoMemoriaComite> tipoMemoriaComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comite.setComite(comite.getComite() + String.format("%03d", i));
      tipoMemoriaComites.add(generarMockTipoMemoriaComite(Long.valueOf(i), comite, tipoMemoria));
    }
    String query = "id:5";

    BDDMockito.given(tipoMemoriaComiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoMemoriaComite>>() {
          @Override
          public Page<TipoMemoriaComite> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoMemoriaComite> content = new ArrayList<>();
            for (TipoMemoriaComite tipoMemoriaComite : tipoMemoriaComites) {
              if (tipoMemoriaComite.getId().equals(5L)) {
                content.add(tipoMemoriaComite);
              }
            }
            Page<TipoMemoriaComite> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred TipoMemoriaComite
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto TipoMemoriaComite
   * 
   * @param id          id del TipoMemoriaComite
   * @param comite      el Comite de TipoMemoriaComite
   * @param tipoMemoria el TipoMemoria de TipoMemoriaComite
   * @return el objeto TipoMemoriaComite
   */

  public TipoMemoriaComite generarMockTipoMemoriaComite(Long id, Comite comite, TipoMemoria tipoMemoria) {

    TipoMemoriaComite tipoMemoriaComite = new TipoMemoriaComite();
    tipoMemoriaComite.setId(id);
    tipoMemoriaComite.setComite(comite);
    tipoMemoriaComite.setTipoMemoria(tipoMemoria);

    return tipoMemoriaComite;
  }

}