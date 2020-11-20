package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.service.EquipoTrabajoService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
import org.crue.hercules.sgi.eti.service.TareaService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
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
import org.springframework.util.ReflectionUtils;

/**
 * PeticionEvaluacionControllerTest
 */
@WebMvcTest(PeticionEvaluacionController.class)
public class PeticionEvaluacionControllerTest extends BaseControllerTest {

  @MockBean
  private PeticionEvaluacionService peticionEvaluacionService;

  @MockBean
  private EquipoTrabajoService equipoTrabajoService;

  @MockBean
  private MemoriaService memoriaService;

  @MockBean
  private TareaService tareaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PETICION_EVALUACION_CONTROLLER_BASE_PATH = "/peticionevaluaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-ER-INV", "ETI-PEV-V" })
  public void getPeticionEvaluacion_WithId_ReturnsPeticionEvaluagetPeticionEvaluacion_NotFound_Returns404cion()
      throws Exception {
    BDDMockito.given(peticionEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value("PeticionEvaluacion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-V", "ETI-PEV-VR-INV", "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void getPeticionEvaluacion_NotFound_Returns404() throws Exception {
    BDDMockito.given(peticionEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new PeticionEvaluacionNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-CR", "ETI-MEM-CR" })
  public void newPeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {
    // given: Un peticionEvaluacion nuevo
    String nuevoPeticionEvaluacionJson = "{\"titulo\": \"PeticionEvaluacion1\", \"activo\": \"true\"}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");

    BDDMockito.given(peticionEvaluacionService.create(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willReturn(peticionEvaluacion);

    // when: Creamos un peticionEvaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoPeticionEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo peticionEvaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value("PeticionEvaluacion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-MEM-C-INV" })
  public void newPeticionEvaluacion_Error_Returns400() throws Exception {
    // given: Un peticionEvaluacion nuevo que produce un error al crearse
    String nuevoPeticionEvaluacionJson = "{\"titulo\": \"PeticionEvaluacion1\", \"activo\": \"true\"}";

    BDDMockito.given(peticionEvaluacionService.create(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un peticionEvaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoPeticionEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-ER-INV" })
  public void replacePeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {
    // given: Un peticionEvaluacion a modificar
    String replacePeticionEvaluacionJson = "{\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "Replace PeticionEvaluacion1");

    BDDMockito.given(peticionEvaluacionService.update(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willReturn(peticionEvaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replacePeticionEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el peticionEvaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value("Replace PeticionEvaluacion1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-ER-INV" })
  public void replacePeticionEvaluacion_NotFound() throws Exception {
    // given: Un peticionEvaluacion a modificar
    String replacePeticionEvaluacionJson = "{\"id\": 1, \"titulo\": \"PeticionEvaluacion1\", \"activo\": \"true\"}";

    BDDMockito.given(peticionEvaluacionService.update(ArgumentMatchers.<PeticionEvaluacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new PeticionEvaluacionNotFoundException(((PeticionEvaluacion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replacePeticionEvaluacionJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-BR-INV" })
  public void removePeticionEvaluacion_ReturnsOk() throws Exception {
    BDDMockito.given(peticionEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-VR-INV", "ETI-PEV-V" })
  public void findAll_Unlimited_ReturnsFullPeticionEvaluacionList() throws Exception {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(peticionEvaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(peticionEvaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred PeticionEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-VR-INV", "ETI-PEV-V" })
  public void findAll_WithPaging_ReturnsPeticionEvaluacionSubList() throws Exception {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(peticionEvaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
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

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked PeticionEvaluacions are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<PeticionEvaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<PeticionEvaluacion>>() {
        });

    // containing titulo='PeticionEvaluacion031' to 'PeticionEvaluacion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      PeticionEvaluacion peticionEvaluacion = actual.get(i);
      Assertions.assertThat(peticionEvaluacion.getTitulo()).isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-VR-INV", "ETI-PEV-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredPeticionEvaluacionList() throws Exception {
    // given: One hundred PeticionEvaluacion and a search query
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }
    String query = "titulo~PeticionEvaluacion%,id:5";

    BDDMockito.given(peticionEvaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<PeticionEvaluacion> content = new ArrayList<>();
            for (PeticionEvaluacion peticionEvaluacion : peticionEvaluaciones) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(PeticionEvaluacion.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, peticionEvaluacion).toString();
                switch (queryCriteria.getOperation()) {
                  case EQUALS:
                    if (!fieldValue.equals(queryCriteria.getValue())) {
                      add = false;
                    }
                    break;
                  case GREATER:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) > 0)) {
                      add = false;
                    }
                    break;
                  case GREATER_OR_EQUAL:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) >= 0)) {
                      add = false;
                    }
                    break;
                  case LIKE:
                    if (!fieldValue.matches((queryCriteria.getValue().toString().replaceAll("%", ".*")))) {
                      add = false;
                    }
                    break;
                  case LOWER:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) < 0)) {
                      add = false;
                    }
                    break;
                  case LOWER_OR_EQUAL:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) <= 0)) {
                      add = false;
                    }
                    break;
                  case NOT_EQUALS:
                    if (fieldValue.equals(queryCriteria.getValue())) {
                      add = false;
                    }
                    break;
                  case NOT_LIKE:
                    if (fieldValue.matches((queryCriteria.getValue().toString().replaceAll("%", ".*")))) {
                      add = false;
                    }
                    break;
                  default:
                    break;
                }
              }
              if (add) {
                content.add(peticionEvaluacion);
              }
            }
            Page<PeticionEvaluacion> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred PeticionEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-CR", "ETI-PEV-ER-INV", "ETI-PEV-VR-INV", "ETI-PEV-C-INV",
      "ETI-PEV-ER-INV" })
  public void findEquipoInvestigador_ReturnsEquipoTrabajoSubList() throws Exception {
    // given: 10 EquipoTrabajos por PeticionEvaluacion
    List<EquipoTrabajoWithIsEliminable> equipoTrabajos = new ArrayList<>();
    for (int i = 1, j = 1; i <= 10; i++, j++) {
      equipoTrabajos.add(generarMockEquipoTrabajoWithIsEliminable(Long.valueOf(i * 10 + j - 10),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }

    BDDMockito.given(equipoTrabajoService.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<EquipoTrabajoWithIsEliminable>>() {
          @Override
          public Page<EquipoTrabajoWithIsEliminable> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EquipoTrabajoWithIsEliminable> content = equipoTrabajos.subList(fromIndex, toIndex);
            Page<EquipoTrabajoWithIsEliminable> page = new PageImpl<>(content, pageable, equipoTrabajos.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/equipo-investigador", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "1").header("X-Page-Size", "5")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked EquipoTrabajos are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "1"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "5"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "10"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(5))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<EquipoTrabajo> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EquipoTrabajo>>() {
        });

    // containing peticionEvaluacion.titulo='PeticionEvaluacion006' to
    // 'PeticionEvaluacion010'
    for (int i = 0, j = 6; i < 10 & j <= 10; i++, j++) {
      EquipoTrabajo equipoTrabajo = actual.get(i);
      Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo())
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findTareas_ReturnsTareaSubList() throws Exception {
    // given: One hundred tareas
    List<TareaWithIsEliminable> tareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tareas.add(generarMockTareaWithIsEliminable(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            tareaService.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TareaWithIsEliminable>>() {
          @Override
          public Page<TareaWithIsEliminable> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TareaWithIsEliminable> content = tareas.subList(fromIndex, toIndex);
            Page<TareaWithIsEliminable> page = new PageImpl<>(content, pageable, tareas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tareas", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked tareas are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TareaWithIsEliminable> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TareaWithIsEliminable>>() {
        });

    // containing tarea='Tarea031' to 'Tarea040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TareaWithIsEliminable tarea = actual.get(i);
      Assertions.assertThat(tarea.getTarea()).isEqualTo("Tarea" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findMemorias_NotFound_Returns404() throws Exception {

    BDDMockito.given(memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).will((InvocationOnMock invocation) -> {
          throw new PeticionEvaluacionNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findMemorias_listMemoriaPeticionEvaluacion_ReturnsOk() throws Exception {

    List<MemoriaPeticionEvaluacion> listMemoriaPeticionEvaluacion = new ArrayList<MemoriaPeticionEvaluacion>();

    for (int i = 1, j = 1; i <= 10; i++, j++) {
      listMemoriaPeticionEvaluacion.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i * 10 + j - 10)));
    }

    BDDMockito.given(memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(listMemoriaPeticionEvaluacion));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void newTarea_ReturnsTarea() throws Exception {
    // given: Una tarea nueva
    String nuevaTareaJson = "{\"tarea\": \"Tarea1\", \"equipoTrabajo\": {\"id\": 100}, \"memoria\": {\"id\": 200}, \"formacion\": \"Formacion1\", \"formacionEspecifica\": {\"id\": 300}, \"organismo\": \"Organismo1\", \"anio\": 2020}";

    Tarea tarea = generarMockTarea(1L, "Tarea1");
    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    BDDMockito.given(equipoTrabajoService.findById(1L)).willReturn(equipoTrabajo);
    BDDMockito.given(tareaService.create(ArgumentMatchers.<Tarea>any())).willReturn(tarea);

    // when: Creamos una tarea
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH
                + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaTareaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea la nueva tarea y la devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("tarea").value("Tarea1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void newTarea_Error_Returns400() throws Exception {
    // given: Una tarea nueva que produce un error al crearse
    String nuevaTareaJson = "{\"id\": 1, \"tarea\": \"Tarea1\", \"equipoTrabajo\": {\"id\": 100}, \"memoria\": {\"id\": 200}, \"formacion\": \"Formacion1\", \"formacionEspecifica\": {\"id\": 300}, \"organismo\": \"Organismo1\", \"anio\": 2020}";

    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));
    BDDMockito.given(equipoTrabajoService.findById(1L)).willReturn(equipoTrabajo);
    BDDMockito.given(tareaService.create(ArgumentMatchers.<Tarea>any())).willThrow(new IllegalArgumentException());

    // when: Creamos una tarea
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH
                + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaTareaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void newEquipoTrabajo_ReturnsEquipoTrabajo() throws Exception {
    // given: Un equipo de trabajo nuevo
    String nuevoEquipoTrabajoJson = "{\"personaRef\": \"user-001\", \"peticionEvaluacion\": {\"titulo\": \"PeticionEvaluacion1\", \"activo\": \"true\"}}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "peticionEvaluacion1");

    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    BDDMockito.given(peticionEvaluacionService.findById(1L)).willReturn(peticionEvaluacion);
    BDDMockito.given(equipoTrabajoService.create(ArgumentMatchers.<EquipoTrabajo>any())).willReturn(equipoTrabajo);

    // when: Creamos un EquipoTrabajo
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEquipoTrabajoJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo EquipoTrabajo y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("personaRef").value("user-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("peticionEvaluacion.titulo").value("PeticionEvaluacion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void newEquipoTrabajo_Error_Returns400() throws Exception {
    // given: Un equipo de trabajo nuevo que produce un error al crearse
    String nuevoEquipoTrabajoJson = "{\"personaRef\": \"user-001\", \"peticionEvaluacion\": {\"titulo\": \"PeticionEvaluacion1\", \"activo\": \"true\"}}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "peticionEvaluacion1");
    BDDMockito.given(peticionEvaluacionService.findById(1L)).willReturn(peticionEvaluacion);
    BDDMockito.given(equipoTrabajoService.create(ArgumentMatchers.<EquipoTrabajo>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un equipo de trabajo
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEquipoTrabajoJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-ER-INV" })
  public void removeTarea_ReturnsOk() throws Exception {

    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 1L;
    long idTarea = 1L;

    BDDMockito.given(tareaService.findById(idTarea)).willReturn(generarMockTarea(1L, "Tarea1"));
    BDDMockito.doNothing().when(tareaService).delete(idTarea);

    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(
                PETICION_EVALUACION_CONTROLLER_BASE_PATH
                    + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas/{idTarea}",
                idPeticionEvaluacion, idEquipoTrabajo, idTarea)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-ER-INV" })
  public void removeEquipoTrabajo_ReturnsOk() throws Exception {
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 111L;

    BDDMockito.given(equipoTrabajoService.findById(idEquipoTrabajo))
        .willReturn((generarMockEquipoTrabajo(1L, generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"))));

    BDDMockito.doNothing().when(tareaService).deleteByEquipoTrabajo(idEquipoTrabajo);
    BDDMockito.doNothing().when(equipoTrabajoService).delete(idEquipoTrabajo);

    mockMvc.perform(MockMvcRequestBuilders
        .delete(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}",
            idPeticionEvaluacion, idEquipoTrabajo)
        .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON));
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

  /**
   * Crea una memoria de petición evaluación.
   * 
   * @param id identificador.
   */
  private MemoriaPeticionEvaluacion generarMockMemoriaPeticionEvaluacion(Long id) {

    MemoriaPeticionEvaluacion memoria = new MemoriaPeticionEvaluacion();
    memoria.setId(id);

    Comite comite = new Comite();
    comite.setId(id);
    memoria.setComite(comite);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);
    memoria.setEstadoActual(tipoEstadoMemoria);

    memoria.setFechaEvaluacion(LocalDateTime.of(2020, 7, 15, 0, 0, 1));
    memoria.setFechaLimite(LocalDate.of(2020, 8, 18));
    return memoria;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  public EquipoTrabajo generarMockEquipoTrabajo(Long id, PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00" + id);

    return equipoTrabajo;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajoWithIsEliminable
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  public EquipoTrabajoWithIsEliminable generarMockEquipoTrabajoWithIsEliminable(Long id,
      PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajoWithIsEliminable equipoTrabajo = new EquipoTrabajoWithIsEliminable();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00" + id);
    equipoTrabajo.setEliminable(true);

    return equipoTrabajo;
  }

  /**
   * Función que devuelve un objeto Tarea
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto Tarea
   */
  public Tarea generarMockTarea(Long id, String descripcion) {
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);
    tipoTarea.setNombre("Eutanasia");
    tipoTarea.setActivo(Boolean.TRUE);

    Tarea tarea = new Tarea();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setTarea(descripcion);
    tarea.setFormacion("Formacion" + id);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo("Organismo" + id);
    tarea.setAnio(2020);
    tarea.setTipoTarea(tipoTarea);

    return tarea;
  }

  /**
   * Función que devuelve un objeto TareaWithIsEliminable
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto TareaWithIsEliminable
   */
  public TareaWithIsEliminable generarMockTareaWithIsEliminable(Long id, String descripcion) {
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);
    tipoTarea.setNombre("Eutanasia");
    tipoTarea.setActivo(Boolean.TRUE);

    TareaWithIsEliminable tarea = new TareaWithIsEliminable();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setTarea(descripcion);
    tarea.setFormacion("Formacion" + id);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo("Organismo" + id);
    tarea.setEliminable(true);

    return tarea;
  }

}
