package gateway.security;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Catálogo de rutas públicas alineadas con el gateway y los microservicios negocios/agenda.
 *
 * Si cambian reglas JWT en el gateway, actualizar también los copies en los microservicios.
 */
public final class PublicApiRouteRules {

    private static final Pattern NEGOCIO_DETAIL = Pattern.compile("^/api/negocios/[0-9]+$");

    private PublicApiRouteRules() {}

    public static String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        if (path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static boolean isPublicGatewayRoute(String method, String path, String duenioIdQuery) {
        String m = method == null ? "" : method.toUpperCase(Locale.ROOT);
        String p = normalizePath(path);

        if (isPublicUsuarioRoute(m, p)) {
            return true;
        }
        if (p.startsWith("/api/negocios") && isPublicNegociosRoute(m, p, duenioIdQuery)) {
            return true;
        }
        if (p.startsWith("/api/agenda") && isPublicAgendaRoute(m, p)) {
            return true;
        }
        return false;
    }

    public static boolean isPublicUsuarioRoute(String method, String path) {
        String m = method == null ? "" : method.toUpperCase(Locale.ROOT);
        String p = normalizePath(path);
        if (p.startsWith("/api/usuarios/auth/")) {
            return true;
        }
        if (p.startsWith("/api/usuarios/test/")) {
            return true;
        }
        return "POST".equals(m) && "/api/usuarios".equals(p);
    }

    public static boolean isPublicNegociosRoute(String method, String path, String duenioIdQuery) {
        String m = method == null ? "" : method.toUpperCase(Locale.ROOT);
        String p = normalizePath(path);
        if (!p.startsWith("/api/negocios")) {
            return false;
        }
        if ("GET".equals(m)) {
            if ("/api/negocios".equals(p)) {
                return duenioIdQuery == null || duenioIdQuery.isBlank();
            }
            return NEGOCIO_DETAIL.matcher(p).matches();
        }
        return false;
    }

    public static boolean isPublicAgendaRoute(String method, String path) {
        String m = method == null ? "" : method.toUpperCase(Locale.ROOT);
        String p = normalizePath(path);
        if (!p.startsWith("/api/agenda")) {
            return false;
        }
        if (p.startsWith("/api/agenda/horarios/cubre")) {
            return "GET".equals(m);
        }
        if (p.startsWith("/api/agenda/horarios")) {
            return "GET".equals(m);
        }
        if (p.startsWith("/api/agenda/servicios")) {
            return "GET".equals(m);
        }
        if (p.startsWith("/api/agenda/trabajadores")) {
            return "GET".equals(m);
        }
        if (p.startsWith("/api/agenda/disponibilidad")) {
            return "GET".equals(m);
        }
        if ("/api/agenda/reservas".equals(p)) {
            return "POST".equals(m);
        }
        return false;
    }
}
