package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
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
 * Test de integracion de Respuesta.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RespuestaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String RESPUESTA_CONTROLLER_BASE_PATH = "/respuestas";

  private HttpEntity<Respuesta> buildRequest(HttpHeaders headers, Respuesta entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-RESPUESTA-EDITAR", "ETI-RESPUESTA-VER")));

    HttpEntity<Respuesta> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getRespuesta_WithId_ReturnsRespuesta() throws Exception {
    final ResponseEntity<Respuesta> response = restTemplate.exchange(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Respuesta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Respuesta respuesta = response.getBody();

    Assertions.assertThat(respuesta.getId()).isEqualTo(1L);
    Assertions.assertThat(respuesta.getValor()).isEqualTo("{\"valor\":\"Valor1\"}");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addRespuesta_ReturnsRespuesta() throws Exception {

    Respuesta nuevoRespuesta = new Respuesta();
    nuevoRespuesta.setValor("{\"valor\":\"Valor1\"}");

    restTemplate.exchange(RESPUESTA_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, nuevoRespuesta),
        Respuesta.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeRespuesta_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Respuesta> response = restTemplate.exchange(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Respuesta.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeRespuesta_DoNotGetRespuesta() throws Exception {

    final ResponseEntity<Respuesta> response = restTemplate.exchange(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Respuesta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceRespuesta_ReturnsRespuesta() throws Exception {

    Respuesta replaceRespuesta = generarMockRespuesta(1L);

    final ResponseEntity<Respuesta> response = restTemplate.exchange(RESPUESTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, replaceRespuesta), Respuesta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Respuesta respuesta = response.getBody();

    Assertions.assertThat(respuesta.getId()).isNotNull();
    Assertions.assertThat(respuesta.getValor()).isEqualTo(replaceRespuesta.getValor());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsRespuestaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<Respuesta>> response = restTemplate.exchange(RESPUESTA_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<Respuesta>>() {
        });

    // then: Respuesta OK, Respuestas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Respuesta> respuestas = response.getBody();
    Assertions.assertThat(respuestas.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de valor='Valor6' a 'Valor8'
    Assertions.assertThat(respuestas.get(0).getValor()).isEqualTo("{\"valor\":\"Valor6\"}");
    Assertions.assertThat(respuestas.get(1).getValor()).isEqualTo("{\"valor\":\"Valor7\"}");
    Assertions.assertThat(respuestas.get(2).getValor()).isEqualTo("{\"valor\":\"Valor8\"}");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredRespuestaList() throws Exception {
    // when: Búsqueda por valor like e id equals
    Long id = 5L;
    String query = "valor~Valor%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(RESPUESTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Respuesta>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Respuesta>>() {
        });

    // then: Respuesta OK, Respuestas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Respuesta> respuestas = response.getBody();
    Assertions.assertThat(respuestas.size()).isEqualTo(1);
    Assertions.assertThat(respuestas.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(respuestas.get(0).getValor()).startsWith("{\"valor\":\"Valor");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedRespuestaList() throws Exception {
    // when: Ordenación por valor desc
    String query = "valor-";

    URI uri = UriComponentsBuilder.fromUriString(RESPUESTA_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Respuesta>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Respuesta>>() {
        });

    // then: Respuesta OK, Respuestas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Respuesta> respuestas = response.getBody();
    Assertions.assertThat(respuestas.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Respuesta respuesta = respuestas.get(i);
      Assertions.assertThat(respuesta.getId()).isEqualTo(8 - i);
      Assertions.assertThat(respuesta.getValor())
          .isEqualTo("{\"valor\":\"Valor" + String.format("%03d", 8 - i) + "\"}");
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsRespuestaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por valor desc
    String sort = "valor-";
    // when: Filtra por valor like e id equals
    String filter = "valor~%00%";

    URI uri = UriComponentsBuilder.fromUriString(RESPUESTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Respuesta>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Respuesta>>() {
        });

    // then: Respuesta OK, Respuestas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Respuesta> respuestas = response.getBody();
    Assertions.assertThat(respuestas.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene valor='Valor001', 'Valor002',
    // 'Valor003'
    Assertions.assertThat(respuestas.get(0).getValor()).isEqualTo("{\"valor\":\"Valor003\"}");
    Assertions.assertThat(respuestas.get(1).getValor()).isEqualTo("{\"valor\":\"Valor002\"}");
    Assertions.assertThat(respuestas.get(2).getValor()).isEqualTo("{\"valor\":\"Valor001\"}");

  }

  /**
   * Función que devuelve un objeto Respuesta
   * 
   * @param id id del Respuesta
   * @return el objeto Respuesta
   */

  public Respuesta generarMockRespuesta(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(id);

    Formulario formulario = new Formulario();
    formulario.setId(id);

    Apartado apartado = getMockApartado(id, 1L, null);

    Respuesta respuesta = new Respuesta();
    respuesta.setId(id);
    respuesta.setMemoria(memoria);
    respuesta.setApartado(apartado);
    respuesta.setValor("{\"valor\":\"Valor" + id + "\"}");

    return respuesta;
  }

  /**
   * Genera un objeto {@link Apartado}
   * 
   * @param id
   * @param bloqueId
   * @param componenteFormularioId
   * @param padreId
   * @return Apartado
   */
  private Apartado getMockApartado(Long id, Long bloqueId, Long padreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1");
    Bloque Bloque = new Bloque(bloqueId, formulario, "Bloque " + bloqueId, bloqueId.intValue());

    Apartado padre = (padreId != null) ? getMockApartado(padreId, bloqueId, null) : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final Apartado data = new Apartado();
    data.setId(id);
    data.setBloque(Bloque);
    data.setNombre("Apartado" + txt);
    data.setPadre(padre);
    data.setOrden(id.intValue());
    data.setEsquema("{\"nombre\":\"EsquemaApartado" + txt + "\"}");

    return data;
  }
}