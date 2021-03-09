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
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de TipoMemoriaComite.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/formulario.sql",
  "classpath:scripts/tipo_memoria.sql",
  "classpath:scripts/tipo_memoria_comite.sql" 
// @formatter:on  
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
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

  @Test
  public void getTipoMemoriaComite_WithId_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Comite comite = new Comite(2L, "Comite2", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria001", Boolean.TRUE);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoMemoriaComite.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoMemoriaComite tipoMemoriaComite = response.getBody();

    Assertions.assertThat(tipoMemoriaComite.getId()).isEqualTo(2L);
    Assertions.assertThat(tipoMemoriaComite.getComite()).isEqualTo(comite);
    Assertions.assertThat(tipoMemoriaComite.getTipoMemoria()).isEqualTo(tipoMemoria);
  }

  @Test
  public void addTipoMemoriaComite_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(2L, "M20", "Formulario M20");
    Comite comite = new Comite(2L, "Comite2", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria001", Boolean.TRUE);

    TipoMemoriaComite nuevoTipoMemoriaComite = generarMockTipoMemoriaComite(null, comite, tipoMemoria);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoTipoMemoriaComite), TipoMemoriaComite.class);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Test
  public void removeTipoMemoriaComite_Success() throws Exception {

    // when: Delete con id existente
    long id = 2L;
    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoMemoriaComite.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeTipoMemoriaComite_DoNotGetTipoMemoriaComite() throws Exception {
    restTemplate.exchange(TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null), TipoMemoriaComite.class, 1L);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoMemoriaComite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceTipoMemoriaComite_ReturnsTipoMemoriaComite() throws Exception {

    Formulario formulario = new Formulario(2L, "M20", "Formulario M20");
    Comite comite = new Comite(2L, "Comite2", formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria001", Boolean.TRUE);

    TipoMemoriaComite replaceTipoMemoriaComite = generarMockTipoMemoriaComite(2L, comite, tipoMemoria);

    final ResponseEntity<TipoMemoriaComite> response = restTemplate.exchange(
        TIPO_MEMORIA_COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceTipoMemoriaComite), TipoMemoriaComite.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoMemoriaComite tipoMemoriaComite = response.getBody();

    Assertions.assertThat(tipoMemoriaComite.getId()).isNotNull();
    Assertions.assertThat(tipoMemoriaComite.getComite()).isEqualTo(replaceTipoMemoriaComite.getComite());
    Assertions.assertThat(tipoMemoriaComite.getTipoMemoria()).isEqualTo(replaceTipoMemoriaComite.getTipoMemoria());
  }

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
    Assertions.assertThat(tipoMemoriaComites.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("7");

    // Contiene de id='7' a '8'
    Assertions.assertThat(tipoMemoriaComites.get(0).getId()).isEqualTo(7L);
    Assertions.assertThat(tipoMemoriaComites.get(1).getId()).isEqualTo(8L);
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoMemoriaComiteList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "id==" + id;

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

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoMemoriaComiteList() throws Exception {
    // when: Ordenación por id desc
    String query = "id,desc";

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
    Assertions.assertThat(tipoMemoriaComites.size()).isEqualTo(7);
    for (int i = 0; i < 7; i++) {
      TipoMemoriaComite tipoMemoriaComite = tipoMemoriaComites.get(i);
      Assertions.assertThat(tipoMemoriaComite.getId()).isEqualTo(8 - i);
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoMemoriaComiteSubList() throws Exception {
    // when: Obtiene page=1 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "1");
    Long id = 2L;

    // when: Ordena por id desc
    String sort = "id,desc";
    // when: Filtra por id equals
    String filter = "id==" + id;

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
    Assertions.assertThat(tipoMemoriaComites.get(0).getId()).isEqualTo(2L);
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