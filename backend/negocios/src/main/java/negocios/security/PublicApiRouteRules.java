package negocios.security;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Replica de gateway.security.PublicApiRouteRules — mantener ambas versiones sincronizadas.
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
}
