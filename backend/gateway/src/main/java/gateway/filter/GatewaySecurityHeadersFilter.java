package gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GatewaySecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse res = exchange.getResponse();
        res.getHeaders().add("X-Content-Type-Options", "nosniff");
        res.getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
        res.getHeaders().add("Permissions-Policy", "geolocation=()");
        res.getHeaders().add("X-Frame-Options", "DENY");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
