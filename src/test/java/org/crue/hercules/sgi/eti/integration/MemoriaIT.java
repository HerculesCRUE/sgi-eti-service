package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
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
 * Test de integracion de Memoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemoriaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_ASIGNABLES = "/asignables/{idConvocatoria}";
  private static final String PATH_PARAMETER_ASIGNABLES_ORDEXT = "/tipo-convocatoria-ord-ext";
  private static final String PATH_PARAMETER_ASIGNABLES_SEG = "/tipo-convocatoria-seg";
  private static final String MEMORIA_CONTROLLER_BASE_PATH = "/memorias";
  private static final String PATH_PARAMETER_EVALUACIONES = "/evaluaciones";

  private HttpEntity<Memoria> buildRequest(HttpHeaders headers, Memoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Memoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<MemoriaPeticionEvaluacion> buildRequestMemoriaPeticionEvaluacion(HttpHeaders headers,
      MemoriaPeticionEvaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<MemoriaPeticionEvaluacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<DocumentacionMemoria> buildRequestDocumentacionMemoria(HttpHeaders headers,
      DocumentacionMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<DocumentacionMemoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getMemoria_WithId_ReturnsMemoria() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-VR-INV", "ETI-PEV-V")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Memoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Memoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoria.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(tipoMemoria.getNumReferencia()).isEqualTo("ref-5588");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addMemoria_ReturnsMemoria() throws Exception {

    Memoria nuevaMemoria = generarMockMemoria(1L, "ref-5588", "Memoria1", 1);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(headers, nuevaMemoria),
        Memoria.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEMORIA-EDITAR", "ETI-MEMORIA-VER")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Memoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeMemoria_DoNotGetMemoria() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEMORIA-EDITAR", "ETI-MEMORIA-VER")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Memoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceMemoria_ReturnsMemoria() throws Exception {

    Memoria replaceMemoria = generarMockMemoria(1L, "ref-5588", "Memoria1", 1);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(headers, replaceMemoria), Memoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Memoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isNotNull();
    Assertions.assertThat(tipoMemoria.getTitulo()).isEqualTo(replaceMemoria.getTitulo());
    Assertions.assertThat(tipoMemoria.getNumReferencia()).isEqualTo(replaceMemoria.getNumReferencia());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-VR-INV", "ETI-PEV-V")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de titulo='Memoria6' a 'Memoria8'
    List<String> titulos = new ArrayList<>();
    for (int i = 0; i < tipoMemorias.size(); i++) {
      titulos.add(tipoMemorias.get(i).getTitulo());
    }
    Assertions.assertThat(titulos).contains("Memoria6");
    Assertions.assertThat(titulos).contains("Memoria7");
    Assertions.assertThat(titulos).contains("Memoria8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredMemoriaList() throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 5L;
    String query = "titulo~Memoria%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-VR-INV", "ETI-PEV-V")));

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(tipoMemorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoMemorias.get(0).getTitulo()).startsWith("Memoria");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedMemoriaList() throws Exception {
    // when: Ordenación por titulo desc
    String query = "titulo-";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-VR-INV", "ETI-PEV-V")));

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      MemoriaPeticionEvaluacion tipoMemoria = tipoMemorias.get(i);
      Assertions.assertThat(tipoMemoria.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoMemoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-VR-INV", "ETI-PEV-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por titulo desc
    String sort = "titulo-";
    // when: Filtra por titulo like e id equals
    String filter = "titulo~%00%";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene titulo='Memoria001', 'Memoria002',
    // 'Memoria003'
    Assertions.assertThat(tipoMemorias.get(0).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 3));
    Assertions.assertThat(tipoMemorias.get(1).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 2));
    Assertions.assertThat(tipoMemorias.get(2).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 1));

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllMemoriasAsignablesConvocatoriaOrdExt_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 1 (ordinaria) o 2 (extraordinaria)
    Long idConvocatoria = 1L;

    // when: Obtiene la memorias asignables para esa convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(4);

    // Las memorias 1,3 y 5 tienen estado 3(En Secretaría) y su
    // fecha de envío es menor que la fecha límite por que son asignables.

    // Memoria 6 no tiene estado 3(En Secretaría) pero tiene retrospectiva de tipo 3
    // (En Secretaría) por lo que sí es asignable.

    // Memoria 7 tiene estado 3(En Secretaría) pero su fecha de envío es menor que
    // la fecha límite, por lo que no es asignable.
    List<String> titulos = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      titulos.add(memorias.get(i).getTitulo());
    }
    Assertions.assertThat(titulos).contains("Memoria1", "Memoria3", "Memoria5", "Memoria6");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllMemoriasAsignablesConvocatoriaOrdExt_WithPaging_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 1 (ordinaria) o 2 (extraordinaria)
    Long idConvocatoria = 1L;

    // when: Obtiene la page=1 con pagesize=2 de la memorias asignables para esa
    // convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");

    // Las memorias 1 y 3 están en la pág0, tienen estado 3(En Secretaría) y su
    // fecha de envío es menor que la fecha límite por que son asignables.

    // Las memorias 5 y 6 están en la pág1
    Assertions.assertThat(memorias.get(0).getTitulo()).isEqualTo("Memoria5");
    Assertions.assertThat(memorias.get(1).getTitulo()).isEqualTo("Memoria6");

    // Memoria 5 tiene estado 3(En Secretaría) y su fecha de envío es menor que la
    // fecha límite por lo que sí es asignable.

    // Memoria 6 no tiene estado 3(En Secretaría) pero tiene retrospectiva de tipo 3
    // (En Secretaría) por lo que sí es asignable.

    // Memoria 7 tiene estado 3(En Secretaría) pero su fecha de envío es menor que
    // la fecha límite, por lo que no es asignable.

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllMemoriasAsignablesConvocatoriaSeg_WithPaging_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 3 (Seguimiento)
    Long idConvocatoria = 3L;

    // when: Obtiene la page=1 con pagesize=2 de la memorias asignables para esa
    // convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    // Las memorias 2 y 4 están en la pág0, tienen estado 12 y 17(En Secretaría
    // seguimiento anual/final) y su fecha de envío es menor que la fecha límite por
    // que son asignables.

    // Las memoria 6 está en la pág1
    Assertions.assertThat(memorias.get(0).getTitulo()).isEqualTo("Memoria6");

    // Memoria 6 tiene estado 12(En Secretaría seguimiento anual) y su fecha de
    // envío es menor que la
    // fecha límite por lo que sí es asignable.

    // Memoria 8 tiene estado 17(En Secretaría seguimiento final) pero su fecha de
    // envío es menor que
    // la fecha límite, por lo que no es asignable.

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllMemoriasAsignablesConvocatoriaSeg_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 3 (Seguimiento)
    Long idConvocatoria = 3L;

    // when: Obtiene memorias asignables para esa convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(3);

    // Las memorias 2, 4 y 6 están en la tienen estados 12 y 17(En Secretaría
    // seguimiento anual/final) y su fecha de envío es menor que la fecha límite por
    // que son asignables.

    Assertions.assertThat(memorias.get(0).getTitulo()).isEqualTo("Memoria2");
    Assertions.assertThat(memorias.get(1).getTitulo()).isEqualTo("Memoria4");
    Assertions.assertThat(memorias.get(2).getTitulo()).isEqualTo("Memoria6");

    // Memoria 8 tiene estado 17(En Secretaría seguimiento final) pero su fecha de
    // envío es menor que
    // la fecha límite, por lo que no es asignable.

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllAsignablesTipoConvocatoriaOrdExt_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // ordinario o extraordinario
    String query = "comite.id:1,fechaEnvioSecretaria<:2020-08-01";
    // String query = "comite.id:1";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_ORDEXT)
        .queryParam("q", query).build(false).toUri();

    // when: find unlimited asignables para tipo convocatoria ordinaria o
    // extraordinaria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Obtiene las
    // memorias en estado "En secretaria" con la fecha de envío es igual o menor a
    // la fecha límite de la convocatoria de reunión y las que tengan una
    // retrospectiva en estado "En secretaría".
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(4);

    // Las memorias 1,3 y 5 tienen estado 3(En Secretaría) y su
    // fecha de envío es menor que la fecha límite por que son asignables.

    // Memoria 6 no tiene estado 3(En Secretaría) pero tiene retrospectiva de tipo 3
    // (En Secretaría) por lo que sí es asignable.

    // Memoria 7 tiene estado 3(En Secretaría) pero su fecha de envío es menor que
    // la fecha límite, por lo que no es asignable.
    Assertions.assertThat(memorias.get(0).getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memorias.get(1).getTitulo()).isEqualTo("Memoria3");
    Assertions.assertThat(memorias.get(2).getTitulo()).isEqualTo("Memoria5");
    Assertions.assertThat(memorias.get(3).getTitulo()).isEqualTo("Memoria6");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllAsignablesTipoConvocatoriaSeguimiento_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // seguimiento

    String query = "comite.id:1,fechaEnvioSecretaria<:2020-08-01";
    // String query = "comite.id:1";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_SEG)
        .queryParam("q", query).build(false).toUri();

    // when: Obtiene memorias asignables para esa convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Respuesta OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(3);

    // Las memorias 2, 4 y 6 están en la tienen estados 12 y 17(En Secretaría
    // seguimiento anual/final) y su fecha de envío es menor que la fecha límite por
    // que son asignables.

    List<String> titulos = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      titulos.add(memorias.get(i).getTitulo());
    }
    Assertions.assertThat(titulos).contains("Memoria2", "Memoria4", "Memoria6");

    // Memoria 8 tiene estado 17(En Secretaría seguimiento final) pero su fecha de
    // envío es menor que
    // la fecha límite, por lo que no es asignable.

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionFormulario_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-formulario", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(4);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-001");
    Assertions.assertThat(documentacionMemoria.get(1).getDocumentoRef()).isEqualTo("doc-002");
    Assertions.assertThat(documentacionMemoria.get(2).getDocumentoRef()).isEqualTo("doc-003");
    Assertions.assertThat(documentacionMemoria.get(3).getDocumentoRef()).isEqualTo("doc-004");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionFormulario_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-formulario", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionSeguimientoAnual_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-anual", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(2);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-001");
    Assertions.assertThat(documentacionMemoria.get(1).getDocumentoRef()).isEqualTo("doc-003");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionSeguimientoAnual_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-anual", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionSeguimientoFinal_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-final", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(2);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-002");
    Assertions.assertThat(documentacionMemoria.get(1).getDocumentoRef()).isEqualTo("doc-004");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionSeguimientoFinal_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-final", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionRetrospectiva_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(2);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-001");
    Assertions.assertThat(documentacionMemoria.get(1).getDocumentoRef()).isEqualTo("doc-002");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionRetrospectiva_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 1L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceDocumentacionMemoria_ReturnsMemoria() throws Exception {

    DocumentacionMemoria replaceDocumentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "Memoria1", 1), generarMockTipoDocumento(1L));
    replaceDocumentacionMemoria.setAportado(Boolean.FALSE);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-inicial/{idDocumentacionMemoria}",
        HttpMethod.PUT, buildRequestDocumentacionMemoria(headers, replaceDocumentacionMemoria),
        DocumentacionMemoria.class, 1L, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final DocumentacionMemoria documentacionMemoria = response.getBody();

    Assertions.assertThat(replaceDocumentacionMemoria.getId()).isNotNull();
    Assertions.assertThat(documentacionMemoria.getMemoria().getNumReferencia())
        .isEqualTo(replaceDocumentacionMemoria.getMemoria().getNumReferencia());
    Assertions.assertThat(documentacionMemoria.getAportado()).isEqualTo(replaceDocumentacionMemoria.getAportado());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByPersonaRefPeticionEvaluacion_WithSortQuery_ReturnsOrderedMemoriaPeticionEvaluacionList()
      throws Exception {
    // when: Ordenación por titulo desc
    String query = "titulo-";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH + "/persona/peticion-evaluacion")
        .queryParam("s", query).build(false).toUri();

    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user-001", "ETI-PEV-VR-INV", "ETI-PEV-V", "ETI-MEM-CR-INV")));

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      MemoriaPeticionEvaluacion memoria = memorias.get(i);
      Assertions.assertThat(memoria.getId()).isEqualTo(8 - i);
      Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addDocumentacionMemoriaFormulario_ReturnsDcoumentacionMemoria() throws Exception {

    DocumentacionMemoria nuevaDocumentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "Memoria1", 1), generarMockTipoDocumento(1L));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-inicial", HttpMethod.POST,
        buildRequestDocumentacionMemoria(headers, nuevaDocumentacionMemoria), Memoria.class, 1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addDocumentacioSeguimientoFinal_ReturnsDcoumentacionMemoria() throws Exception {

    DocumentacionMemoria nuevaDocumentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "Memoria1", 1), generarMockTipoDocumento(1L));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-final", HttpMethod.POST,
        buildRequestDocumentacionMemoria(headers, nuevaDocumentacionMemoria), Memoria.class, 1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByPersonaRefPeticionEvaluacion_WithPagingSortingAndFiltering_ReturnsMemoriaPeticionEvaluacionSubList()
      throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user-001", "ETI-PEV-VR-INV", "ETI-PEV-V", "ETI-MEM-CR-INV")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por titulo desc
    String sort = "titulo-";
    // when: Filtra por titulo like e id equals
    String filter = "titulo~%00%";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH + "/persona/peticion-evaluacion")
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene titulo='Memoria001', 'Memoria002',
    // 'Memoria003'
    Assertions.assertThat(memorias.get(0).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 3));
    Assertions.assertThat(memorias.get(1).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 2));
    Assertions.assertThat(memorias.get(2).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addDocumentacioSeguimientoAnual_ReturnsDcoumentacionMemoria() throws Exception {

    DocumentacionMemoria nuevaDocumentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "Memoria1", 1), generarMockTipoDocumento(1L));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-anual", HttpMethod.POST,
        buildRequestDocumentacionMemoria(headers, nuevaDocumentacionMemoria), Memoria.class, 1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addDocumentacioRetrospectiva_ReturnsDcoumentacionMemoria() throws Exception {

    DocumentacionMemoria nuevaDocumentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "Memoria1", 1), generarMockTipoDocumento(1L));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva",
        HttpMethod.POST, buildRequestDocumentacionMemoria(headers, nuevaDocumentacionMemoria), Memoria.class, 1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluacionesMemoria_ReturnsEvaluadorSubList() throws Exception {

    // given: idMemoria
    Long idMemoria = 2L;
    // when: Busca las evaluaciones de la memoria 1L
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_EVALUACIONES, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, idMemoria);

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(3);

    // Contiene las evaluaciones con Id '1', '2' y '3'
    Assertions.assertThat(evaluaciones.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(evaluaciones.get(1).getId()).isEqualTo(2L);
    Assertions.assertThat(evaluaciones.get(2).getId()).isEqualTo(3L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByPersonaRefPeticionEvaluacion_WithSearchQuery_ReturnsFilteredMemoriaPeticionEvaluacionList()
      throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 5L;
    String query = "titulo~Memoria%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH + "/persona/peticion-evaluacion")
        .queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-PEV-VR-INV", "ETI-PEV-V")));

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(1);
    Assertions.assertThat(memorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(memorias.get(0).getTitulo()).startsWith("Memoria");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void deleteDocumentacionSeguimientoAnual_ReturnsMemoria() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-anual/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 1L, 1L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void deleteDocumentacionSeguimientoFinal_ReturnsMemoria() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-final/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 1L, 1L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluacionesMemoria_ReturnsEmptyList() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_EVALUACIONES, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, 2L);

    // then: La memoria no tiene evaluaciones
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void deleteDocumentacionRetrospectiva_ReturnsMemoria() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 1L, 1L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void deleteDocumentacionInicial_ReturnsMemoria() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-ER-INV")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-inicial/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 1L, 1L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /**
   * Función que devuelve un objeto DocumentacionMemoria
   * 
   * @param id            id de DocumentacionMemoria
   * @param memoria       la Memoria de DocumentacionMemoria
   * @param tipoDocumento el TipoDocumento de DocumentacionMemoria
   * @return el objeto DocumentacionMemoria
   */

  private DocumentacionMemoria generarMockDocumentacionMemoria(Long id, Memoria memoria, TipoDocumento tipoDocumento) {

    DocumentacionMemoria documentacionMemoria = new DocumentacionMemoria();
    documentacionMemoria.setId(id);
    documentacionMemoria.setMemoria(memoria);
    documentacionMemoria.setTipoDocumento(tipoDocumento);
    documentacionMemoria.setDocumentoRef("doc-00" + id);
    documentacionMemoria.setAportado(Boolean.TRUE);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */

  private TipoDocumento generarMockTipoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre("TipoDocumento" + id);

    return tipoDocumento;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void enviarSecretaria_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-PEV-ER-INV")));

    // when: Enviar secretaria con id existente
    long id = 1L;
    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void enviarSecretaria_DoNotGetMemoria() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-PEV-ER-INV")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void enviarSecretariaRetrospectiva_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-PEV-ER-INV")));

    // when: Enviar secretaria con id existente
    long id = 1L;
    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria-retrospectiva", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void enviarSecretariaRetrospectiva_DoNotGetMemoria() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-PEV-ER-INV")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria-retrospectiva", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto tipo Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, "codOrganoCompetente", Boolean.TRUE, null);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
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
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    Formulario formulario = new Formulario(id, "M" + id + "0", "Descripcion");
    return new Comite(id, comite, formulario, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria.
   * 
   * @param id     identificador del TipoEstadoMemoria.
   * @param nombre nombre.
   * @param activo indicador de activo.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()));

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }

}
