package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.service.TipoDocumentoService;
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
 * TipoDocumentoControllerTest
 */
@WebMvcTest(TipoDocumentoController.class)
public class TipoDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private TipoDocumentoService tipoDocumentoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_DOCUMENTO_CONTROLLER_BASE_PATH = "/tipodocumentos";
  private static final String TIPO_DOCUMENTO_INICIAL_CONTROLLER_BASE_PATH = "/iniciales";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void getTipoDocumento_WithId_ReturnsTipoDocumento() throws Exception {

    BDDMockito.given(tipoDocumentoService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoDocumento(1L, "TipoDocumento1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoDocumento1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void getTipoDocumento_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoDocumentoService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoDocumentoNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-EDITAR" })
  public void newTipoDocumento_ReturnsTipoDocumento() throws Exception {
    // given: Un tipo Documento nuevo
    String nuevoTipoDocumentoJson = "{\"id\": \"1\",\"nombre\": \"TipoDocumento1\", \"formulario\": {\"nombre\": \"M10\"}}";

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "TipoDocumento1");

    BDDMockito.given(tipoDocumentoService.create(ArgumentMatchers.<TipoDocumento>any())).willReturn(tipoDocumento);

    // when: Creamos un tipo Documento
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoDocumentoJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo tipo Documento y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoDocumento1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-EDITAR" })
  public void newTipoDocumento_Error_Returns400() throws Exception {
    // given: Un tipo Documento nuevo que produce un error al crearse
    String nuevoTipoDocumentoJson = "{\"id\": \"1\",\"nombre\": \"TipoDocumento1\", \"formulario\": {\"nombre\": \"M10\"}}";

    BDDMockito.given(tipoDocumentoService.create(ArgumentMatchers.<TipoDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo Documento
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoDocumentoJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-EDITAR" })
  public void replaceTipoDocumento_ReturnsTipoDocumento() throws Exception {
    // given: Un tipo Documento a modificar
    String replaceTipoDocumentoJson = "{\"id\": \"1\",\"nombre\": \"TipoDocumento1\", \"formulario\": {\"nombre\": \"M10\"}}";

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "Replace TipoDocumento1");

    BDDMockito.given(tipoDocumentoService.update(ArgumentMatchers.<TipoDocumento>any())).willReturn(tipoDocumento);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoDocumentoJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el tipo Documento y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoDocumento1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-EDITAR" })
  public void replaceTipoDocumento_NotFound() throws Exception {
    // given: Un tipo Documento a modificar
    String replaceTipoDocumentoJson = "{\"id\": \"1\",\"nombre\": \"TipoDocumento1\", \"formulario\": {\"nombre\": \"M10\"}}";

    BDDMockito.given(tipoDocumentoService.update(ArgumentMatchers.<TipoDocumento>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoDocumentoNotFoundException(((TipoDocumento) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoDocumentoJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-EDITAR" })
  public void removeTipoDocumento_ReturnsOk() throws Exception {

    BDDMockito.given(tipoDocumentoService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoDocumento(1L, "TipoDocumento1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_Unlimited_ReturnsFullTipoDocumentoList() throws Exception {
    // given: One hundred TipoDocumento
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoDocumentos));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred TipoDocumento
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoDocumento empty
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoDocumentos));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_WithPaging_ReturnsTipoDocumentoSubList() throws Exception {
    // given: One hundred TipoDocumento
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoDocumento> content = tipoDocumentos.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tipoDocumentos.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked TipoDocumentos are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoDocumento> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoDocumento>>() {
        });

    // containing nombre='TipoDocumento031' to 'TipoDocumento040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoDocumento tipoDocumento = actual.get(i);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoDocumentoList() throws Exception {
    // given: One hundred TipoDocumento and a search query
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }
    String query = "nombre~TipoDocumento%,id:5";

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoDocumento> content = new ArrayList<>();
            for (TipoDocumento tipoDocumento : tipoDocumentos) {
              if (tipoDocumento.getNombre().startsWith("TipoDocumento") && tipoDocumento.getId().equals(5L)) {
                content.add(tipoDocumento);
              }
            }
            Page<TipoDocumento> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred TipoDocumento
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findTipoDocumentacionInicial_ReturnsFullTipoDocumentoInicialList() throws Exception {
    // given: One hundred TipoDocumento
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoService.findTipoDocumentacionInicial(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoDocumentos));
    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + TIPO_DOCUMENTO_INICIAL_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred TipoDocumento iniciales
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findTipoDocumentacionInicial_ReturnsNoContent() throws Exception {
    // given: TipoDocumento empty
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();

    BDDMockito.given(tipoDocumentoService.findTipoDocumentacionInicial(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoDocumentos));
    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + TIPO_DOCUMENTO_INICIAL_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        // then: Devuelve error No Content
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id     id del tipoDocumento
   * @param nombre la descripción del tipoDocumento
   * @return el objeto tipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("M10");
    formulario.setDescripcion("Formulario M10");

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setFormulario(formulario);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}
