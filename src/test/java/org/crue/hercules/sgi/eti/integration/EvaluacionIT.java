package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
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
 * Test de integracion de Evaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvaluacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUACION_CONTROLLER_BASE_PATH = "/evaluaciones";
  private static final String EVALUACION_LIST_PATH = "/evaluables";
  private static final String EVALUACION_SEGUIMIENTO_PATH = "/memorias-seguimiento-final";

  private HttpEntity<Evaluacion> buildRequest(HttpHeaders headers, Evaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Evaluacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<Comentario> buildRequestComentario(HttpHeaders headers, Comentario entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Comentario> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluacion_WithId_ReturnsEvaluacion() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        Evaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluacion evaluacion = response.getBody();

    Assertions.assertThat(evaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addEvaluacion_ReturnsEvaluacion() throws Exception {

    Evaluacion nuevoEvaluacion = generarMockEvaluacion(null, "1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-C", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(EVALUACION_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(headers, nuevoEvaluacion), Evaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluacion_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    // when: Delete
    long id = 1L;
    final ResponseEntity<Evaluacion> response = restTemplate.exchange("/convocatoriareuniones/1/evaluacion/1",
        HttpMethod.DELETE, buildRequest(headers, generarMockEvaluacion(1L, null)), Evaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluacion_DoNotGetEvaluacion() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-B", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        Evaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceEvaluacion_ReturnsEvaluacion() throws Exception {

    Evaluacion replaceEvaluacion = generarMockEvaluacion(1L, null);

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(headers, replaceEvaluacion),
        Evaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluacion evaluacion = response.getBody();

    Assertions.assertThat(evaluacion.getId()).isNotNull();
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(EVALUACION_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de memoria.titulo='Memoria6' a 'Memoria8'
    // Contiene de dictamen.nombre='Dictamen6' a 'Dictamen8'
    // Contiene de convocatoriaReunion.codigo='CR-6' a 'CR-8'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria6");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).isEqualTo("Dictamen6");
    Assertions.assertThat(evaluaciones.get(1).getMemoria().getTitulo()).isEqualTo("Memoria7");
    Assertions.assertThat(evaluaciones.get(1).getDictamen().getNombre()).isEqualTo("Dictamen7");
    Assertions.assertThat(evaluaciones.get(2).getMemoria().getTitulo()).isEqualTo("Memoria8");
    Assertions.assertThat(evaluaciones.get(2).getDictamen().getNombre()).isEqualTo("Dictamen8");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluacionList() throws Exception {
    // when: Búsqueda por esRevMinima equals e id equals
    Long id = 5L;
    String query = "esRevMinima:true,id:" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(evaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).startsWith("Memoria");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).startsWith("Dictamen");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEvaluacionList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Evaluacion evaluacion = evaluaciones.get(i);
      Assertions.assertThat(evaluacion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 8 - i));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));
    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por version equals
    String filter = "version:2";

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene memoria.titulo='Memoria001', 'Memoria002','Memoria003'
    // Contiene dictamen.nombre='Dictamen001', 'Dictamen002','Dictamen003'
    // Contiene convocatoriaReunion.codigo='CR-001', 'CR-002','CR-003'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 3));
    Assertions.assertThat(evaluaciones.get(1).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 2));
    Assertions.assertThat(evaluaciones.get(2).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 1));

    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 3));
    Assertions.assertThat(evaluaciones.get(1).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 2));
    Assertions.assertThat(evaluaciones.get(2).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 1));

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de memoria.titulo='Memoria6' a 'Memoria8'
    // Contiene de dictamen.nombre='Dictamen6' a 'Dictamen8'
    // Contiene de convocatoriaReunion.codigo='CR-6' a 'CR-8'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria6");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).isEqualTo("Dictamen6");
    Assertions.assertThat(evaluaciones.get(1).getMemoria().getTitulo()).isEqualTo("Memoria7");
    Assertions.assertThat(evaluaciones.get(1).getDictamen().getNombre()).isEqualTo("Dictamen7");
    Assertions.assertThat(evaluaciones.get(2).getMemoria().getTitulo()).isEqualTo("Memoria8");
    Assertions.assertThat(evaluaciones.get(2).getDictamen().getNombre()).isEqualTo("Dictamen8");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithSearchQuery_ReturnsFilteredEvaluacionList()
      throws Exception {
    // when: Búsqueda por esRevMinima equals e id equals
    Long id = 5L;
    String query = "esRevMinima:true,id:" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
        .queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(evaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).startsWith("Memoria");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).startsWith("Dictamen");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithSortQuery_ReturnsOrderedEvaluacionList()
      throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
        .queryParam("s", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Evaluacion evaluacion = evaluaciones.get(i);
      Assertions.assertThat(evaluacion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 8 - i));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithPagingSortingAndFiltering_ReturnsEvaluacionSubList()
      throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));
    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por version equals
    String filter = "version:1";

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene memoria.titulo='Memoria001', 'Memoria002','Memoria003'
    // Contiene dictamen.nombre='Dictamen001', 'Dictamen002','Dictamen003'
    // Contiene convocatoriaReunion.codigo='CR-001', 'CR-002','CR-003'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 3));
    Assertions.assertThat(evaluaciones.get(1).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 2));
    Assertions.assertThat(evaluaciones.get(2).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 1));

    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 3));
    Assertions.assertThat(evaluaciones.get(1).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 2));
    Assertions.assertThat(evaluaciones.get(2).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findByEvaluacionesEnSeguimientoFinal_ReturnsEvaluacionList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V", "ETI-EVC-EVAL")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_SEGUIMIENTO_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene de memoria.titulo='Memoria2'

    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria2");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getComentariosGestor_WithId_ReturnsComentario() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");

    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentarios-gestor", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comentario>>() {
        }, 200L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<Comentario> comentarios = response.getBody();

    Assertions.assertThat(comentarios.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene de comentario.id=1L

    Assertions.assertThat(comentarios.get(0).getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getComentariosEvaluador_WithId_ReturnsComentario() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");

    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVALR")));

    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentarios-evaluador", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comentario>>() {
        }, 200L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<Comentario> comentarios = response.getBody();

    Assertions.assertThat(comentarios.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene de comentario.id=2L
    Assertions.assertThat(comentarios.get(0).getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addComentarioGestor_ReturnsComentario() throws Exception {

    Apartado apartado = new Apartado();
    apartado.setId(100L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Memoria memoria = new Memoria();
    memoria.setId(1L);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setMemoria(memoria);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVAL")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 200L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addComentarioEvaluador_ReturnsError400() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(200L, "eval");
    Apartado apartado = new Apartado();
    apartado.setId(100L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setEvaluacion(evaluacion);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVALR", "ETI-EVC-EVALR-INV")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 200L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addComentarioEvaluador_ReturnsComentario() throws Exception {

    Apartado apartado = new Apartado();
    apartado.setId(100L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Memoria memoria = new Memoria();
    memoria.setId(1L);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setMemoria(memoria);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVALR", "ETI-EVC-EVALR-INV")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 200L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addComentarioGestor_ReturnsError400() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(200L, "eval");
    Apartado apartado = new Apartado();
    apartado.setId(100L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setEvaluacion(evaluacion);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 200L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceComentarioGestor_ReturnsComentario() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(200L, null);
    Apartado apartado = new Apartado();
    apartado.setId(100L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(1L);

    Comentario comentarioReplace = new Comentario();
    comentarioReplace.setId(1L);
    comentarioReplace.setApartado(apartado);
    comentarioReplace.setEvaluacion(evaluacion);
    comentarioReplace.setTipoComentario(tipoComentario);
    comentarioReplace.setTexto("Actualizado");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVAL")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor" + "/{idComentario}", HttpMethod.PUT,
        buildRequestComentario(headers, comentarioReplace), Comentario.class, 200L, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comentario comentario = response.getBody();

    Assertions.assertThat(comentario.getId()).isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).isEqualTo(200L);
    Assertions.assertThat(comentario.getTexto()).isEqualTo("Actualizado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceComentarioEvaluador_ReturnsComentario() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(200L, null);
    Apartado apartado = new Apartado();
    apartado.setId(100L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(2L);

    Comentario comentarioReplace = new Comentario();
    comentarioReplace.setId(1L);
    comentarioReplace.setApartado(apartado);
    comentarioReplace.setEvaluacion(evaluacion);
    comentarioReplace.setTipoComentario(tipoComentario);
    comentarioReplace.setTexto("Actualizado");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVALR", "ETI-EVC-EVALR-INV")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador" + "/{idComentario}",
        HttpMethod.PUT, buildRequestComentario(headers, comentarioReplace), Comentario.class, 200L, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comentario comentario = response.getBody();

    Assertions.assertThat(comentario.getId()).isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).isEqualTo(200L);
    Assertions.assertThat(comentario.getTexto()).isEqualTo("Actualizado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComentarioGestor_Success() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    // when: Delete con id existente
    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor" + "/{idComentario}",
        HttpMethod.DELETE, buildRequestComentario(headers, null), Comentario.class, 200L, 1L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComentarioEvaluador_Success() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVAL", "ETI-EVC-EVALR-INV")));

    // when: Delete con id existente
    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador" + "/{idComentario}",
        HttpMethod.DELETE, buildRequestComentario(headers, null), Comentario.class, 200L, 1L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(1L);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setTipoEvaluacion(tipoEvaluacion);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación");
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial("Valor social");
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.now(), Boolean.FALSE,
        new Retrospectiva(1L, new EstadoRetrospectiva(3L, "En evaluación", Boolean.TRUE), LocalDate.now()), 3,
        "CodOrganoCompetente", Boolean.TRUE, null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(LocalDateTime.now());
    convocatoriaReunion.setFechaLimite(LocalDate.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Evaluador evaluador1 = new Evaluador();
    evaluador1.setId(1L);
    evaluador1.setResumen("Evaluador1");
    evaluador1.setComite(comite);
    evaluador1.setCargoComite(cargoComite);
    evaluador1.setFechaAlta(LocalDate.of(2020, 7, 1));
    evaluador1.setFechaBaja(LocalDate.of(2021, 7, 1));
    evaluador1.setPersonaRef("user-001");
    evaluador1.setActivo(Boolean.TRUE);

    Evaluador evaluador2 = new Evaluador();
    evaluador2.setId(2L);
    evaluador2.setResumen("Evaluador2");
    evaluador2.setComite(comite);
    evaluador2.setCargoComite(cargoComite);
    evaluador2.setFechaAlta(LocalDate.of(2020, 7, 1));
    evaluador2.setFechaBaja(LocalDate.of(2021, 7, 1));
    evaluador2.setPersonaRef("user");
    evaluador2.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

}