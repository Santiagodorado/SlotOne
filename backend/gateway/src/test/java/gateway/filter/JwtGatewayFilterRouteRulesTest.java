package gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import gateway.security.PublicApiRouteRules;

class JwtGatewayFilterRouteRulesTest {

  private static boolean isPublic(MockServerHttpRequest req) {
    String method = req.getMethod() != null ? req.getMethod().name() : "";
    String path = PublicApiRouteRules.normalizePath(req.getURI().getRawPath());
    return PublicApiRouteRules.isPublicGatewayRoute(
        method, path, req.getQueryParams().getFirst("duenioId"));
  }

  @Test
  void normalizeTrimsTrailingSlash() {
    assertThat(PublicApiRouteRules.normalizePath("/api/negocios/")).isEqualTo("/api/negocios");
  }

  @Test
  void publicCatalogoNegocios() {
    var req = MockServerHttpRequest.method(HttpMethod.GET, "/api/negocios").build();
    assertThat(isPublic(req)).isTrue();
  }

  @Test
  void negociosPorDuenoRequiereJwt() {
    var req =
        MockServerHttpRequest.method(HttpMethod.GET, "/api/negocios?duenioId=7").build();
    assertThat(isPublic(req)).isFalse();
  }

  @Test
  void detalleNegocioPublico() {
    var req = MockServerHttpRequest.method(HttpMethod.GET, "/api/negocios/12").build();
    assertThat(isPublic(req)).isTrue();
  }

  @Test
  void crearNegocioRequiereJwt() {
    var req = MockServerHttpRequest.method(HttpMethod.POST, "/api/negocios").build();
    assertThat(isPublic(req)).isFalse();
  }

  @Test
  void postReservaPublica_ListarNo() {
    var post = MockServerHttpRequest.method(HttpMethod.POST, "/api/agenda/reservas").build();
    assertThat(isPublic(post)).isTrue();

    var get = MockServerHttpRequest.method(HttpMethod.GET, "/api/agenda/reservas?negocioId=1").build();
    assertThat(isPublic(get)).isFalse();
  }

  @Test
  void loginSinJwt() {
    var req =
        MockServerHttpRequest.method(HttpMethod.POST, "/api/usuarios/auth/login").build();
    assertThat(isPublic(req)).isTrue();
  }
}
