package gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gateway.filter.JwtGatewayFilter;

@Configuration
public class GatewayConfig {

    private final JwtGatewayFilter jwtGatewayFilter;
    private final String usuariosUri;
    private final String negociosUri;
    private final String agendaUri;

    public GatewayConfig(
            JwtGatewayFilter jwtGatewayFilter,
            @Value("${slotone.gateway.usuarios-uri}") String usuariosUri,
            @Value("${slotone.gateway.negocios-uri}") String negociosUri,
            @Value("${slotone.gateway.agenda-uri}") String agendaUri) {
        this.jwtGatewayFilter = jwtGatewayFilter;
        this.usuariosUri = usuariosUri;
        this.negociosUri = negociosUri;
        this.agendaUri = agendaUri;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // ============== RUTAS PÚBLICAS (SIN JWT) ==============

            .route(
                    "public-routes",
                    r -> r.path("/api/usuarios/**")
                            .filters(f -> f.filter(jwtGatewayFilter.apply(new JwtGatewayFilter.Config())))
                            .uri(usuariosUri))

            .route(
                    "negocios-service",
                    r -> r.path("/api/negocios/**")
                            .filters(f -> f.filter(jwtGatewayFilter.apply(new JwtGatewayFilter.Config())))
                            .uri(negociosUri))

            .route(
                    "agenda-service",
                    r -> r.path("/api/agenda/**")
                            .filters(f -> f.filter(jwtGatewayFilter.apply(new JwtGatewayFilter.Config())))
                            .uri(agendaUri))

            // ============== RUTAS PROTEGIDAS (CON JWT) ==============

            .route(
                    "roles-service",
                    r -> r.path("/api/roles/**")
                            .filters(f -> f.filter(jwtGatewayFilter.apply(new JwtGatewayFilter.Config())))
                            .uri(usuariosUri))

            .build();
    }
}
