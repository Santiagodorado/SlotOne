package gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gateway.filter.JwtGatewayFilter;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtGatewayFilter jwtGatewayFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // ============== RUTAS PÚBLICAS (SIN JWT) ==============
            
            // Ruta para autenticación y registro (manejo automático por filtro)
            .route("public-routes", r -> r
                .path("/api/usuarios/**")
                .filters(f -> f.filter(jwtGatewayFilter.apply(new JwtGatewayFilter.Config())))
                .uri("http://localhost:5000"))
            
            // Negocios y agenda: solo proxy (sin JwtGatewayFilter) para que GET/POST funcionen sin Bearer
            .route("negocios-service", r -> r
                .path("/api/negocios/**")
                .uri("http://localhost:5004"))
            
            .route("agenda-service", r -> r
                .path("/api/agenda/**")
                .uri("http://localhost:5005"))
            
            // ============== RUTAS PROTEGIDAS (CON JWT) ==============
            
            // Rutas de roles
            .route("roles-service", r -> r
                .path("/api/roles/**")
                .filters(f -> f.filter(jwtGatewayFilter.apply(new JwtGatewayFilter.Config())))
                .uri("http://localhost:5000"))
            
            .build();
    }
} 