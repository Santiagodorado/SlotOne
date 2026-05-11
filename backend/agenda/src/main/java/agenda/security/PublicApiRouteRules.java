package agenda.security;

import java.util.Locale;

/**
 * Réplica parcial — solo rutas agenda; debe alinearse con gateway.security.PublicApiRouteRules.
 */
public final class PublicApiRouteRules {

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
