package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
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
 * Test de integracion de TipoMemoriaComite.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoMemoriaComiteIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH = "/tipomemoriacomites";

  private HttpEntity<TipoMemoriaComite> buildRequest(HttpHeaders headers, TipoMemoriaComite entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-TIPOMEMORIACOMITE-EDITAR", "ETI-TIPOMEMORIACOMITE-VER")));

    HttpEntity<TipoMemoriaComite> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getTipoMemoriaComite_WithId_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoMemoriaComite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoMemoriaComite tipoMemoriaComite = response.getBody();

    Assertions.assertThat(tipoMemoriaComite.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoriaComite.getComite()).isEqualTo(comite);
    Assertions.assertThat(tipoMemoriaComite.getTipoMemoria()).isEqualTo(tipoMemoria);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoMemoriaComite_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    TipoMemoriaComite nuevoTipoMemoriaComite = generarMockTipoMemoriaComite(null, comite, tipoMemoria);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoMemoriaComite), TipoMemoriaComite.class);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoMemoriaComite_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoMemoriaComite.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoMemoriaComite_DoNotGetTipoMemoriaComite() throws Exception {
    restTemplate.exchange(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null), TipoMemoriaComite.class, 1L);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoMemoriaComite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoMemoriaComite_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);

    TipoMemoriaComite replaceTipoMemoriaComite = generarMockTipoMemoriaComite(1L, comite, tipoMemoria);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceTipoMemoriaComite), TipoMemoriaComite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoMemoriaComite tipoMemoriaComite = response.getBody();

    Assertions.assertThat(tipoMemoriaComite.getId()).isNotNull();
    Assertions.assertThat(tipoMemoriaComite.getComite()).isEqualTo(replaceTipoMemoriaComite.getComite());
    Assertions.assertThat(tipoMemoriaComite.getTipoMemoria()).isEqualTo(replaceTipoMemoriaComite.getTipoMemoria());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoMemoriaComiteSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoMemoriaComite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoMemoriaComite>>() {
        });

    // then: Respuesta OK, TipoMemoriaComites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoriaComite> tipoMemoriaComites = response.getBody();
    Assertions.assertThat(tipoMemoriaComites.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de id='6' a '8'
    Assertions.assertThat(tipoMemoriaComites.get(0).getId()).isEqualTo(6L);
    Assertions.assertThat(tipoMemoriaComites.get(1).getId()).isEqualTo(7L);
    Assertions.assertThat(tipoMemoriaComites.get(2).getId()).isEqualTo(8L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoMemoriaComiteList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoMemoriaComite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoMemoriaComite>>() {
        });

    // then: Respuesta OK, TipoMemoriaComites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoriaComite> tipoMemoriaComites = response.getBody();
    Assertions.assertThat(tipoMemoriaComites.size()).isEqualTo(1);
    Assertions.assertThat(tipoMemoriaComites.get(0).getId()).isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoMemoriaComiteList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoMemoriaComite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoMemoriaComite>>() {
        });

    // then: Respuesta OK, TipoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoriaComite> tipoMemoriaComites = response.getBody();
    Assertions.assertThat(tipoMemoriaComites.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      TipoMemoriaComite tipoMemoriaComite = tipoMemoriaComites.get(i);
      Assertions.assertThat(tipoMemoriaComite.getId()).isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoMemoriaComiteSubList() throws Exception {
    // when: Obtiene page=1 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "1");
    Long id = 1L;

    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por id equals
    String filter = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoMemoriaComite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoMemoriaComite>>() {
        });

    // then: Respuesta OK, TipoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoriaComite> tipoMemoriaComites = response.getBody();
    Assertions.assertThat(tipoMemoriaComites.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene id='1'
    Assertions.assertThat(tipoMemoriaComites.get(0).getId()).isEqualTo(1L);
  }

  /**
   * Función que devuelve un objeto TipoMemoriaComite
   * 
   * @param id          id de TipoMemoriaComite
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