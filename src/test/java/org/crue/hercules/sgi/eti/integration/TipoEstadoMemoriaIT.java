package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
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
 * Test de integracion de TipoEstadoMemoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoEstadoMemoriaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH = "/tipoestadomemorias";

  private HttpEntity<TipoEstadoMemoria> buildRequest(HttpHeaders headers, TipoEstadoMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-TIPOESTADOMEMORIA-EDITAR", "ETI-TIPOESTADOMEMORIA-VER")));

    HttpEntity<TipoEstadoMemoria> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getTipoEstadoMemoria_WithId_ReturnsTipoEstadoMemoria() throws Exception {
    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.exchange(
        TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoEstadoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoMemoria tipoEstadoMemoria = response.getBody();

    Assertions.assertThat(tipoEstadoMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoMemoria.getNombre()).isEqualTo("En elaboración");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoEstadoMemoria_ReturnsTipoEstadoMemoria() throws Exception {

    TipoEstadoMemoria nuevoTipoEstadoMemoria = new TipoEstadoMemoria(1L, "Completada", Boolean.TRUE);

    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.exchange(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoEstadoMemoria), TipoEstadoMemoria.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isEqualTo(nuevoTipoEstadoMemoria);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEstadoMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.exchange(
        TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoEstadoMemoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEstadoMemoria_DoNotGetTipoEstadoMemoria() throws Exception {
    restTemplate.exchange(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), TipoEstadoMemoria.class, 1L);

    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.exchange(
        TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoEstadoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoEstadoMemoria_ReturnsTipoEstadoMemoria() throws Exception {

    TipoEstadoMemoria replaceTipoEstadoMemoria = generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria1");

    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.exchange(

        TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceTipoEstadoMemoria), TipoEstadoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoMemoria tipoEstadoMemoria = response.getBody();

    Assertions.assertThat(tipoEstadoMemoria.getId()).isNotNull();
    Assertions.assertThat(tipoEstadoMemoria.getNombre()).isEqualTo(replaceTipoEstadoMemoria.getNombre());
    Assertions.assertThat(tipoEstadoMemoria.getActivo()).isEqualTo(replaceTipoEstadoMemoria.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoEstadoMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(5);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("10");

    // Contiene de nombre='Favorable Pendiente de Modificaciones Mínimas', 'No
    // procede evaluar', 'Fin evaluación' y
    // 'Archivado'
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre())
        .isEqualTo("Favorable Pendiente de Modificaciones Mínimas");
    Assertions.assertThat(tipoEstadoMemorias.get(1).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(tipoEstadoMemorias.get(2).getNombre()).isEqualTo("No procede evaluar");
    Assertions.assertThat(tipoEstadoMemorias.get(3).getNombre()).isEqualTo("Fin evaluación");
    Assertions.assertThat(tipoEstadoMemorias.get(4).getNombre()).isEqualTo("Archivado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEstadoMemoriaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~en%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoMemorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre()).startsWith("En evaluación");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoEstadoMemoriaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(10);

    Assertions.assertThat(tipoEstadoMemorias.get(0).getId()).isEqualTo(7);
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(tipoEstadoMemorias.get(1).getId()).isEqualTo(8);
    Assertions.assertThat(tipoEstadoMemorias.get(1).getNombre()).isEqualTo("No procede evaluar");
    Assertions.assertThat(tipoEstadoMemorias.get(2).getId()).isEqualTo(9);
    Assertions.assertThat(tipoEstadoMemorias.get(2).getNombre()).isEqualTo("Fin evaluación");
    Assertions.assertThat(tipoEstadoMemorias.get(3).getId()).isEqualTo(6);
    Assertions.assertThat(tipoEstadoMemorias.get(3).getNombre())
        .isEqualTo("Favorable Pendiente de Modificaciones Mínimas");
    Assertions.assertThat(tipoEstadoMemorias.get(4).getId()).isEqualTo(4);
    Assertions.assertThat(tipoEstadoMemorias.get(4).getNombre()).isEqualTo("En secretaría revisión mínima");
    Assertions.assertThat(tipoEstadoMemorias.get(5).getId()).isEqualTo(3);
    Assertions.assertThat(tipoEstadoMemorias.get(5).getNombre()).isEqualTo("En secretaría");
    Assertions.assertThat(tipoEstadoMemorias.get(6).getId()).isEqualTo(5);
    Assertions.assertThat(tipoEstadoMemorias.get(6).getNombre()).isEqualTo("En evaluación");
    Assertions.assertThat(tipoEstadoMemorias.get(7).getId()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoMemorias.get(7).getNombre()).isEqualTo("En elaboración");
    Assertions.assertThat(tipoEstadoMemorias.get(8).getId()).isEqualTo(2);
    Assertions.assertThat(tipoEstadoMemorias.get(8).getNombre()).isEqualTo("Completada");
    Assertions.assertThat(tipoEstadoMemorias.get(9).getId()).isEqualTo(10);
    Assertions.assertThat(tipoEstadoMemorias.get(9).getNombre()).isEqualTo("Archivado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoEstadoMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%en%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene nombre='Pendiente de correcciones', 'Favorable Pendiente de
    // Modificaciones Mínimas',
    // 'En secretaría revisión mínima'
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(tipoEstadoMemorias.get(1).getNombre())
        .isEqualTo("Favorable Pendiente de Modificaciones Mínimas");
    Assertions.assertThat(tipoEstadoMemorias.get(2).getNombre()).isEqualTo("En secretaría revisión mínima");

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @param id     id del TipoEstadoMemoria
   * @param nombre nombre del TipoEstadoMemoria
   * @return el objeto TipoEstadoMemoria
   */

  public TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre) {

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);
    tipoEstadoMemoria.setNombre(nombre);
    tipoEstadoMemoria.setActivo(Boolean.TRUE);

    return tipoEstadoMemoria;
  }

}