package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
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
 * Test de integracion de Comite.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComiteIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String COMITE_CONTROLLER_BASE_PATH = "/comites";

  private HttpEntity<Comite> buildRequest(HttpHeaders headers, Comite entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Comite> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<TipoMemoria> buildRequestTipoMemoria(HttpHeaders headers, TipoMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<TipoMemoria> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<Memoria> buildRequestMemoria(HttpHeaders headers, Memoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Memoria> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getComite_WithId_ReturnsComite() throws Exception {
    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Comite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comite comite = response.getBody();

    Assertions.assertThat(comite.getId()).isEqualTo(1L);
    Assertions.assertThat(comite.getComite()).isEqualTo("Comite1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addComite_ReturnsComite() throws Exception {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");

    Comite nuevoComite = new Comite();
    nuevoComite.setComite("Comite1");
    nuevoComite.setActivo(Boolean.TRUE);
    nuevoComite.setFormulario(formulario);

    ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoComite), Comite.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Comite comite = response.getBody();

    Assertions.assertThat(comite.getId()).isNotNull();
    Assertions.assertThat(comite.getComite()).isEqualTo(nuevoComite.getComite());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComite_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Comite.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComite_DoNotGetComite() throws Exception {

    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Comite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceComite_ReturnsComite() throws Exception {

    Comite replaceComite = new Comite();
    replaceComite.setComite("Comite2");
    replaceComite.setFormulario(new Formulario(2L, "M20", "Descripcion"));
    replaceComite.setActivo(Boolean.TRUE);

    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, replaceComite), Comite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comite comite = response.getBody();

    Assertions.assertThat(comite.getId()).isNotNull();
    Assertions.assertThat(comite.getComite()).isEqualTo(replaceComite.getComite());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsComiteSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));

    final ResponseEntity<List<Comite>> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, ComiteS retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de comite='Comite6' a 'Comite8'
    Assertions.assertThat(comites.get(0).getComite()).isEqualTo("Comite6");
    Assertions.assertThat(comites.get(1).getComite()).isEqualTo("Comite7");
    Assertions.assertThat(comites.get(2).getComite()).isEqualTo("Comite8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredComiteList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "comite~Comite%,id:" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));

    URI uri = UriComponentsBuilder.fromUriString(COMITE_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Comite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, ComiteS retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(1);
    Assertions.assertThat(comites.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(comites.get(0).getComite()).startsWith("Comite");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoFungibleList() throws Exception {
    // when: Ordenación por comite desc
    String query = "comite-";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));

    URI uri = UriComponentsBuilder.fromUriString(COMITE_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Comite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, Comites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Comite comite = comites.get(i);
      Assertions.assertThat(comite.getId()).isEqualTo(8 - i);
      Assertions.assertThat(comite.getComite()).isEqualTo("Comite" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsComiteSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));
    // when: Ordena por comite desc
    String sort = "comite-";
    // when: Filtra por comite like e id equals
    String filter = "comite~%00%";

    URI uri = UriComponentsBuilder.fromUriString(COMITE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Comite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, Comites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene comite='Comite001', 'Comite002',
    // 'Comite003'
    Assertions.assertThat(comites.get(0).getComite()).isEqualTo("Comite" + String.format("%03d", 3));
    Assertions.assertThat(comites.get(1).getComite()).isEqualTo("Comite" + String.format("%03d", 2));
    Assertions.assertThat(comites.get(2).getComite()).isEqualTo("Comite" + String.format("%03d", 1));

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findMemorias_ReturnsMemoria() throws Exception {

    // when: find unlimited asignables para el comité
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", HttpMethod.GET,
        buildRequestMemoria(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        }, 2L);

    // then: Obtiene las memorias del comité 2.
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memoria = response.getBody();
    Assertions.assertThat(memoria.size()).isEqualTo(2);

    Assertions.assertThat(memoria.get(0).getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoria.get(1).getTitulo()).isEqualTo("Memoria2");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findMemoria_Unlimited_ReturnsEmptyMemoria() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", HttpMethod.GET,
        buildRequestMemoria(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        }, 2L);

    // then: No existen memorias.
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findTipoMemoria_ReturnsTipoMemoria() throws Exception {

    // when: find unlimited asignables para el comité
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tipo-memorias", HttpMethod.GET,
        buildRequestTipoMemoria(headers, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        }, 1L);

    // then: Obtiene los tipos de memoria del comité 1
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoria> tipoMemoria = response.getBody();
    Assertions.assertThat(tipoMemoria.size()).isEqualTo(2);

    Assertions.assertThat(tipoMemoria.get(0).getNombre()).isEqualTo("Nueva");
    Assertions.assertThat(tipoMemoria.get(1).getNombre()).isEqualTo("Modificada");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findTipoMemoria_Unlimited_ReturnsEmptyTipoMemoria() throws Exception {

    // when: find unlimited asignables para el comité
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tipo-memorias", HttpMethod.GET,
        buildRequestTipoMemoria(headers, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        }, 2L);

    // then: No obtiene ningún tipo de memoria para el comité 2.
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

}