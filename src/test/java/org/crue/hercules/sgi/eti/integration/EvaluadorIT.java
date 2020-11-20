package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
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
 * Test de integracion de Evaluador.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvaluadorIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUADOR_CONTROLLER_BASE_PATH = "/evaluadores";
  private static final String PATH_PARAMETER_EVALUACIONES = "/evaluaciones";
  private static final String PATH_PARAMETER_SINCONFLICTOINTERES = "/comite/{idComite}/sinconflictointereses/{idMemoria}";

  private HttpEntity<Evaluador> buildRequest(HttpHeaders headers, Evaluador entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }
    HttpEntity<Evaluador> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluador_WithId_ReturnsEvaluador() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-E", "ETI-EVR-V")));

    final ResponseEntity<Evaluador> response = restTemplate.exchange(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Evaluador.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluador evaluador = response.getBody();

    Assertions.assertThat(evaluador.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addEvaluador_ReturnsEvaluador() throws Exception {

    Evaluador nuevoEvaluador = new Evaluador();
    nuevoEvaluador.setResumen("Evaluador1");
    nuevoEvaluador.setComite(new Comite(2L, "Comite2", new Formulario(2L, "M20", "Descripcion"), Boolean.TRUE));
    nuevoEvaluador.setCargoComite(new CargoComite(2L, "CargoComite2", Boolean.TRUE));
    nuevoEvaluador.setPersonaRef("user-001");
    nuevoEvaluador.setActivo(Boolean.TRUE);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-C")));

    final ResponseEntity<Evaluador> response = restTemplate.exchange(EVALUADOR_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(headers, nuevoEvaluador), Evaluador.class);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluador_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-B", "ETI-EVR-V")));

    final ResponseEntity<Evaluador> response = restTemplate.exchange(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Evaluador.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluador_DoNotGetEvaluador() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-B", "ETI-EVR-V")));

    restTemplate.exchange(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(headers, null), Evaluador.class, 1L);

    final ResponseEntity<Evaluador> response = restTemplate.exchange(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Evaluador.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceEvaluador_ReturnsEvaluador() throws Exception {

    Evaluador replaceEvaluador = generarMockEvaluador(1L, "Evaluador1");

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-E")));

    final ResponseEntity<Evaluador> response = restTemplate.exchange(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(headers, replaceEvaluador), Evaluador.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluador evaluador = response.getBody();

    Assertions.assertThat(evaluador.getId()).isNotNull();
    Assertions.assertThat(evaluador.getResumen()).isEqualTo(replaceEvaluador.getResumen());
    Assertions.assertThat(evaluador.getActivo()).isEqualTo(replaceEvaluador.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsEvaluadorSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-V")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Evaluador>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluador>>() {
        });

    // then: Respuesta OK, Evaluadores retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluador> evaluadores = response.getBody();
    Assertions.assertThat(evaluadores.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de resumen='Evaluador6' a 'Evaluador8'
    Assertions.assertThat(evaluadores.get(0).getResumen()).isEqualTo("Evaluador6");
    Assertions.assertThat(evaluadores.get(1).getResumen()).isEqualTo("Evaluador7");
    Assertions.assertThat(evaluadores.get(2).getResumen()).isEqualTo("Evaluador8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluadorList() throws Exception {
    // when: Búsqueda por resumen like e id equals
    Long id = 5L;
    String query = "resumen~Evaluador%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-V")));

    final ResponseEntity<List<Evaluador>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluador>>() {
        });

    // then: Respuesta OK, Evaluadores retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluador> evaluadores = response.getBody();
    Assertions.assertThat(evaluadores.size()).isEqualTo(1);
    Assertions.assertThat(evaluadores.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(evaluadores.get(0).getResumen()).startsWith("Evaluador");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEvaluadorList() throws Exception {
    // when: Ordenación por resumen desc
    String query = "resumen-";

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-V")));

    final ResponseEntity<List<Evaluador>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluador>>() {
        });

    // then: Respuesta OK, Evaluadores retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluador> evaluadores = response.getBody();
    Assertions.assertThat(evaluadores.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Evaluador evaluador = evaluadores.get(i);
      Assertions.assertThat(evaluador.getId()).isEqualTo(8 - i);
      Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEvaluadorSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por resumen desc
    String sort = "resumen-";
    // when: Filtra por resumen like e id equals
    String filter = "resumen~%00%";

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Evaluador>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluador>>() {
        });

    // then: Respuesta OK, Evaluadores retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluador> evaluadores = response.getBody();
    Assertions.assertThat(evaluadores.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene resumen='Evaluador001', 'Evaluador002',
    // 'Evaluador003'
    Assertions.assertThat(evaluadores.get(0).getResumen()).isEqualTo("Evaluador" + String.format("%03d", 3));
    Assertions.assertThat(evaluadores.get(1).getResumen()).isEqualTo("Evaluador" + String.format("%03d", 2));
    Assertions.assertThat(evaluadores.get(2).getResumen()).isEqualTo("Evaluador" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findByEvaluadorPersonaRef_Unlimited_ReturnsEmptyEvaluacionList() throws Exception {
    // when: Obtiene la page=1 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-VR", "ETI-EVC-EVALR")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_EVALUACIONES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        }, "user-001");

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findEvaluacionesEnSeguimiento_Unlimited_ReturnsEmptyEvaluacionList() throws Exception {
    // when: Obtiene la page=1 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-VR", "ETI-EVC-EVALR")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        EVALUADOR_CONTROLLER_BASE_PATH + "/evaluaciones-seguimiento", HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        }, "user-001");

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findEvaluacionesEnSeguimiento_Unlimited_ReturnsEvaluacionList() throws Exception {
    // when: Obtiene la page=1 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-006", "ETI-EVC-VR", "ETI-EVC-EVALR")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        EVALUADOR_CONTROLLER_BASE_PATH + "/evaluaciones-seguimiento", HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByComiteSinconflictoInteresesMemoria_Unlimited_ReturnsEvaluadorSubList() throws Exception {

    // given: idComite, idMemoria
    Long idComite = 1L;
    Long idMemoria = 100L;
    // when: Busca los evaluadores del comite 1L que no
    // partician en tareas de la memoria 100L
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Evaluador>> response = restTemplate.exchange(
        EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_SINCONFLICTOINTERES, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluador>>() {
        }, idComite, idMemoria);

    // then: Respuesta OK, Evaluadores retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluador> evaluadores = response.getBody();
    Assertions.assertThat(evaluadores.size()).isEqualTo(4);

    // Contiene los evaluadores con Resumen 'Evaluador001', 'Evaluador002' y
    // 'Evaluador003'
    Assertions.assertThat(evaluadores.get(0).getResumen()).isEqualTo("Evaluador001");
    Assertions.assertThat(evaluadores.get(1).getResumen()).isEqualTo("Evaluador003");
    Assertions.assertThat(evaluadores.get(2).getResumen()).isEqualTo("Evaluador005");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByComiteSinconflictoInteresesMemoria_WithPaging_ReturnsEvaluadorSubList() throws Exception {

    // given: idComite, idMemoria
    Long idComite = 1L;
    Long idMemoria = 100L;
    // when: Busca la page=1 con pagesize=2 de los evaluadores del comite 1L que
    // no
    // partician en tareas de la memoria 100L
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    final ResponseEntity<List<Evaluador>> response = restTemplate.exchange(
        EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_SINCONFLICTOINTERES, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluador>>() {
        }, idComite, idMemoria);

    // then: Respuesta OK, Evaluadores retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluador> evaluadores = response.getBody();
    Assertions.assertThat(evaluadores.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");

    // Contiene de resumen='Evaluador005'
    Assertions.assertThat(evaluadores.get(0).getResumen()).isEqualTo("Evaluador005");
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */

  public Evaluador generarMockEvaluador(Long id, String resumen) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(LocalDate.now());
    evaluador.setFechaBaja(LocalDate.now());
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-00" + id);
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

}