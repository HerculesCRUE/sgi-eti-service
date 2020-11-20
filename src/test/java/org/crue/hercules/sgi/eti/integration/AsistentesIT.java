package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
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
 * Test de integracion de Asistentes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AsistentesIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ASISTENTE_CONTROLLER_BASE_PATH = "/asistentes";

  private HttpEntity<Asistentes> buildRequest(HttpHeaders headers, Asistentes entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization",
          String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));
    }

    HttpEntity<Asistentes> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getAsistentes_WithId_ReturnsAsistentes() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        Asistentes.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Asistentes asistente = response.getBody();

    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo1");
    Assertions.assertThat(asistente.getAsistencia()).isTrue();
    Assertions.assertThat(asistente.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getEvaluador().getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addAsistentes_ReturnsAsistentes() throws Exception {

    Asistentes nuevoAsistente = new Asistentes();
    nuevoAsistente.setMotivo("Motivo 1");
    nuevoAsistente.setAsistencia(Boolean.TRUE);
    nuevoAsistente.setConvocatoriaReunion(new ConvocatoriaReunion());
    nuevoAsistente.getConvocatoriaReunion().setId(1L);
    nuevoAsistente.setEvaluador(new Evaluador());
    nuevoAsistente.getEvaluador().setId(1L);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C")));

    final ResponseEntity<Asistentes> response = restTemplate.exchange(ASISTENTE_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(headers, nuevoAsistente), Asistentes.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeAsistentes_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        Asistentes.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeAsistentes_DoNotGetAsistentes() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        Asistentes.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceAsistentes_ReturnsAsistentes() throws Exception {

    Asistentes replaceAsistente = new Asistentes();
    replaceAsistente.setMotivo("Motivo 1");
    replaceAsistente.setAsistencia(Boolean.TRUE);
    replaceAsistente.setConvocatoriaReunion(new ConvocatoriaReunion());
    replaceAsistente.getConvocatoriaReunion().setId(1L);
    replaceAsistente.setEvaluador(new Evaluador());
    replaceAsistente.getEvaluador().setId(1L);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));

    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(headers, replaceAsistente),
        Asistentes.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Asistentes asistente = response.getBody();

    Assertions.assertThat(asistente.getId()).isNotNull();
    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo 1");
    Assertions.assertThat(asistente.getAsistencia()).isTrue();
    Assertions.assertThat(asistente.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getEvaluador().getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsAsistentesSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "3");

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(ASISTENTE_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de motivo='Motivo4' a
    // 'Motivo6'
    Assertions.assertThat(asistentes.get(0).getMotivo()).isEqualTo("Motivo4");
    Assertions.assertThat(asistentes.get(1).getMotivo()).isEqualTo("Motivo5");
    Assertions.assertThat(asistentes.get(2).getMotivo()).isEqualTo("Motivo6");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredAsistentesList() throws Exception {
    // when: Búsqueda por motivo like e id equals
    Long id = 5L;
    String query = "motivo~motivo%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(1);
    Assertions.assertThat(asistentes.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(asistentes.get(0).getMotivo()).startsWith("Motivo5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedAsistentesList() throws Exception {
    // when: Ordenación por motivo desc
    String query = "motivo-";

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(6);
    for (int i = 0; i < 6; i++) {
      Asistentes asistente = asistentes.get(i);
      Assertions.assertThat(asistente.getId()).isEqualTo(6 - i);
      Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo" + String.valueOf(6 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsAsistentesSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "motivo-";
    // when: Filtra por motivo like
    String filter = "motivo~%motivo%";

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de motivo='Motivo6' a
    // 'Motivo4'
    Assertions.assertThat(asistentes.get(0).getMotivo()).isEqualTo("Motivo6");
    Assertions.assertThat(asistentes.get(1).getMotivo()).isEqualTo("Motivo5");
    Assertions.assertThat(asistentes.get(2).getMotivo()).isEqualTo("Motivo4");

  }

}
