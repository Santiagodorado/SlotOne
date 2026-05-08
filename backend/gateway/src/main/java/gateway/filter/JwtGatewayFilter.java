package gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import gateway.config.JwtUtils;

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
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            
            if ("OPTIONS".equalsIgnoreCase(method)) {
                System.out.println("🔓 Preflight OPTIONS permitido: " + path);
                return chain.filter(exchange);
            }
            
            // Permitir rutas públicas sin validar JWT (negocios/agenda no usan este filtro en GatewayConfig)
            if (path.contains("/auth/") ||
                path.contains("/h2-console") ||
                (path.equals("/api/usuarios") && method.equals("POST"))) {

                System.out.println("🔓 Ruta pública permitida: " + method + " " + path);
                return chain.filter(exchange);
            }

            System.out.println("🔒 Validando JWT para: " + method + " " + path);
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            
            try {
                if (!jwtUtils.validateJwtToken(token)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
                }
                
                // Extraer información del usuario del token
                String username = jwtUtils.extractUsername(token);
                
                // Agregar headers con información del usuario a la petición
                exchange.getRequest().mutate()
                    .header("X-User-Name", username)
                    .build();
                    
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + e.getMessage());
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuración del filtro si es necesaria
    }
} 