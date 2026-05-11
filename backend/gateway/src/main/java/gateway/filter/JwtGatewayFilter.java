package gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import gateway.config.JwtUtils;
import gateway.security.PublicApiRouteRules;

/**
 * JWT obligatorio salvo rutas públicas (catálogo, disponibilidad, alta de reserva, login/registro).
 */
@Component
public class JwtGatewayFilter extends AbstractGatewayFilterFactory<JwtGatewayFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    public JwtGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpMethod methodObj = request.getMethod();
            String method = methodObj != null ? methodObj.name() : "";
            if ("OPTIONS".equalsIgnoreCase(method)) {
                return chain.filter(exchange);
            }

            String path = PublicApiRouteRules.normalizePath(request.getURI().getRawPath());            String duenio = request.getQueryParams().getFirst("duenioId");

            if (PublicApiRouteRules.isPublicGatewayRoute(method, path, duenio)) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            try {
                if (!jwtUtils.validateJwtToken(token)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
                }
                String username = jwtUtils.extractUsername(token);
                ServerHttpRequest mutated = request.mutate()
                        .header("X-User-Name", username)
                        .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
            }
        };
    }

    public static class Config {
    }
}
