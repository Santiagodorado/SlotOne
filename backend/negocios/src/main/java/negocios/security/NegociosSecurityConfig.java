package negocios.security;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@EnableWebSecurity
public class NegociosSecurityConfig {

    @Bean
    SecurityFilterChain negociosSecurity(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(publicNegociosMatcher()).permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(j -> j.decoder(jwtDecoder)));
        return http.build();
    }

    private static RequestMatcher publicNegociosMatcher() {
        return request -> {
            String path = pathWithoutContextPath(request);
            return PublicApiRouteRules.isPublicNegociosRoute(
                    request.getMethod(),
                    path,
                    request.getParameter("duenioId"));
        };
    }

    static String pathWithoutContextPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        return PublicApiRouteRules.normalizePath(uri);
    }
}
