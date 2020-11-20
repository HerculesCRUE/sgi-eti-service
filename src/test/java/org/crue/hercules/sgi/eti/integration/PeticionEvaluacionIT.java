package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de PeticionEvaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeticionEvaluacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PETICION_EVALUACION_CONTROLLER_BASE_PATH = "/peticionevaluaciones";

  private HttpEntity<PeticionEvaluacion> buildRequest(HttpHeaders headers, PeticionEvaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV",
        "ETI-PEV-V", "ETI-PEV-CR", "ETI-MEM-CR", "ETI-PEV-BR-INV", "ETI-PEV-C-INV", "ETI-PEV-V")));

    HttpEntity<PeticionEvaluacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<EquipoTrabajo> buildRequestEquipoTrabajo(HttpHeaders headers, EquipoTrabajo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-PEV-C-INV", "ETI-PEV-ER-INV", "ETI-PEV-VR-INV")));

    HttpEntity<EquipoTrabajo> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<Tarea> buildRequestTarea(HttpHeaders headers, Tarea entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    HttpEntity<Tarea> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<EquipoTrabajo> buildRequestMemoriaPeticionEvaluacion(HttpHeaders headers, EquipoTrabajo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-PEV-CR", "ETI-PEV-ER-INV", "ETI-EVR-V", "ETI-PEV-C-INV")));

    HttpEntity<EquipoTrabajo> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getPeticionEvaluacion_WithId_ReturnsPeticionEvaluacion() throws Exception {
    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        PeticionEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final PeticionEvaluacion tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoActividad.getTitulo()).isEqualTo("PeticionEvaluacion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addPeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {

    PeticionEvaluacion nuevoPeticionEvaluacion = generarMockPeticionEvaluacion(1L, "titulo");
    nuevoPeticionEvaluacion.setId(null);

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(PETICION_EVALUACION_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoPeticionEvaluacion), PeticionEvaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removePeticionEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        PeticionEvaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removePeticionEvaluacion_DoNotGetPeticionEvaluacion() throws Exception {
    restTemplate.exchange(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), PeticionEvaluacion.class, 1L);

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        PeticionEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replacePeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {

    PeticionEvaluacion replacePeticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replacePeticionEvaluacion), PeticionEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final PeticionEvaluacion tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isNotNull();
    Assertions.assertThat(tipoActividad.getTitulo()).isEqualTo(replacePeticionEvaluacion.getTitulo());
    Assertions.assertThat(tipoActividad.getActivo()).isEqualTo(replacePeticionEvaluacion.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsPeticionEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    String query = "id+";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de titulo='PeticionEvaluacion6' a 'PeticionEvaluacion8'
    List<String> titulos = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      titulos.add(peticionEvaluaciones.get(i).getTitulo());
    }
    Assertions.assertThat(titulos).contains("PeticionEvaluacion6", "PeticionEvaluacion7", "PeticionEvaluacion8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredPeticionEvaluacionList() throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 5L;
    String query = "titulo~PeticionEvaluacion%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(peticionEvaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(peticionEvaluaciones.get(0).getTitulo()).startsWith("PeticionEvaluacion");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedPeticionEvaluacionList() throws Exception {
    // when: Ordenación por titulo desc
    String query = "titulo-";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      PeticionEvaluacion tipoActividad = peticionEvaluaciones.get(i);
      Assertions.assertThat(tipoActividad.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoActividad.getTitulo()).isEqualTo("PeticionEvaluacion" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsPeticionEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por titulo desc
    String sort = "titulo-";
    // when: Filtra por titulo like e id equals
    String filter = "titulo~%00%";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene titulo='PeticionEvaluacion001', 'PeticionEvaluacion002',
    // 'PeticionEvaluacion003'
    Assertions.assertThat(peticionEvaluaciones.get(0).getTitulo())
        .isEqualTo("PeticionEvaluacion" + String.format("%03d", 3));
    Assertions.assertThat(peticionEvaluaciones.get(1).getTitulo())
        .isEqualTo("PeticionEvaluacion" + String.format("%03d", 2));
    Assertions.assertThat(peticionEvaluaciones.get(2).getTitulo())
        .isEqualTo("PeticionEvaluacion" + String.format("%03d", 1));

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findEquipoInvestigador_WithPaging_ReturnsEquipoInvestigadorSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<EquipoTrabajoWithIsEliminable>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/equipo-investigador", HttpMethod.GET,
        buildRequestEquipoTrabajo(headers, null),
        new ParameterizedTypeReference<List<EquipoTrabajoWithIsEliminable>>() {
        }, 1L);

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EquipoTrabajoWithIsEliminable> equiposTrabajo = response.getBody();
    Assertions.assertThat(equiposTrabajo.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de id='6' a '8'
    Assertions.assertThat(equiposTrabajo.get(0).getId()).isEqualTo(6);
    Assertions.assertThat(equiposTrabajo.get(1).getId()).isEqualTo(7);
    Assertions.assertThat(equiposTrabajo.get(2).getId()).isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findTareas_WithPaging_ReturnsTareasSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<TareaWithIsEliminable>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tareas", HttpMethod.GET,
        buildRequestEquipoTrabajo(headers, null), new ParameterizedTypeReference<List<TareaWithIsEliminable>>() {
        }, 1L);

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TareaWithIsEliminable> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de id='6' a '8'
    Assertions.assertThat(tareas.get(0).getId()).isEqualTo(6);
    Assertions.assertThat(tareas.get(1).getId()).isEqualTo(7);
    Assertions.assertThat(tareas.get(2).getId()).isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findMemorias_WithPaging_ReturnsMemoriaPeticionEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        }, 1L);

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> memoriasPeticionEvaluacion = response.getBody();
    Assertions.assertThat(memoriasPeticionEvaluacion.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de id='6' a '8'
    Assertions.assertThat(memoriasPeticionEvaluacion.get(0).getId()).isEqualTo(6);
    Assertions.assertThat(memoriasPeticionEvaluacion.get(1).getId()).isEqualTo(7);
    Assertions.assertThat(memoriasPeticionEvaluacion.get(2).getId()).isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTarea_ReturnsTarea() throws Exception {

    Tarea nuevaTarea = generarMockTarea(null, "Tarea");

    final ResponseEntity<Tarea> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas",
        HttpMethod.POST, buildRequestTarea(null, nuevaTarea), Tarea.class, 1L, 100L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final Tarea tarea = response.getBody();

    Assertions.assertThat(tarea.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(tarea.getEquipoTrabajo()).as("equipoTrabajo").isNotNull();
    Assertions.assertThat(tarea.getEquipoTrabajo().getId()).as("equipoTrabajo.id").isEqualTo(100L);
    Assertions.assertThat(tarea.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(tarea.getMemoria().getId()).as("memoria.id").isEqualTo(200L);
    Assertions.assertThat(tarea.getTarea()).as("tarea").isEqualTo("Tarea");
    Assertions.assertThat(tarea.getFormacion()).as("formacion").isEqualTo("Formacion");
    Assertions.assertThat(tarea.getFormacionEspecifica()).as("formacionEspecifica").isNotNull();
    Assertions.assertThat(tarea.getFormacionEspecifica().getId()).as("formacionEspecifica.id").isEqualTo(300L);
    Assertions.assertThat(tarea.getOrganismo()).as("organismo").isEqualTo("Organismo");
    Assertions.assertThat(tarea.getAnio()).as("anio").isEqualTo(2020);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addEquipoTrabajo_ReturnsEquipoTrabajo() throws Exception {

    EquipoTrabajo nuevoEquipoTrabajo = generarMockEquipoTrabajo(null,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    final ResponseEntity<EquipoTrabajo> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo", HttpMethod.POST,
        buildRequestEquipoTrabajo(null, nuevoEquipoTrabajo), EquipoTrabajo.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final EquipoTrabajo equipoTrabajo = response.getBody();

    Assertions.assertThat(equipoTrabajo.getId()).isNotNull();
    Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getId()).isEqualTo(1);
    Assertions.assertThat(equipoTrabajo.getPersonaRef()).isEqualTo("user-001");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEquipoTrabajo_Success() throws Exception {

    // when: Delete con id existente
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 1L;
    final ResponseEntity<EquipoTrabajo> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}",
        HttpMethod.DELETE, buildRequest(null, null), EquipoTrabajo.class, idPeticionEvaluacion, idEquipoTrabajo);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEquipoTrabajo_DoNotGetEquipoTrabajo() throws Exception {

    // when: Delete con id no existente
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 111L;

    final ResponseEntity<EquipoTrabajo> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}",
        HttpMethod.DELETE, buildRequest(null, null), EquipoTrabajo.class, idPeticionEvaluacion, idEquipoTrabajo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTarea_Success() throws Exception {

    // when: Delete con id existente
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 100L;
    long idTarea = 1L;
    final ResponseEntity<Tarea> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH
            + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas/{idTarea}",
        HttpMethod.DELETE, buildRequest(null, null), Tarea.class, idPeticionEvaluacion, idEquipoTrabajo, idTarea);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTarea_DoNotGetTarea() throws Exception {

    // when: Delete con id no existente
    long idPeticionEvaluacion = 1L;
    long idTarea = 111L;
    final ResponseEntity<Tarea> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/tareas/{idTarea}", HttpMethod.DELETE,
        buildRequest(null, null), Tarea.class, idPeticionEvaluacion, idTarea);

    // then: 404
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
    peticionEvaluacion.setValorSocial("valor social");
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
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
    equipoTrabajo.setPersonaRef("user-00" + (id == null ? 1 : id));

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
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(100L);

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
    tarea.setFormacion("Formacion" + (id != null ? id : ""));
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo("Organismo" + (id != null ? id : ""));
    tarea.setAnio(2020);
    tarea.setTipoTarea(tipoTarea);

    return tarea;
  }

}