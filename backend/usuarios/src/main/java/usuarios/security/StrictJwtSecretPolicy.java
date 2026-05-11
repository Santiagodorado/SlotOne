package usuarios.security;

import java.util.Base64;

import org.springframework.core.env.Environment;

public final class StrictJwtSecretPolicy {

    public static final String REPO_FALLBACK_SECRET_BASE64 = "U2kgdmlzIHBhY2VtLCBwYXJhIGJlbGx1bSBNZW1lbnRvIG1vcmk=";

    private StrictJwtSecretPolicy() {}

    public static void validateOnStartup(String jwtSecret, Environment env) {
        if (!strictActivation(env)) {
            return;
        }
        String s = jwtSecret != null ? jwtSecret.strip() : "";
        if (s.equals(REPO_FALLBACK_SECRET_BASE64)) {
            throw new IllegalStateException(
                    "JWT_SECRET no puede usar el valor por defecto del repositorio en este entorno. "
                            + "Defina un secreto aleatorio codificado en Base64 (único por despliegue).");
        }
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(s);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("JWT_SECRET debe ser una cadena Base64 válida.", ex);
        }
        if (decoded.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET decodificado es demasiado corto (< 32 bytes). Genere al menos 32 bytes aleatorios y codifíquelos en Base64.");
        }
    }

    private static boolean strictActivation(Environment env) {
        if (Boolean.parseBoolean(env.getProperty("SLOTONE_STRICT_JWT_SECRET", "false"))) {
            return true;
        }
        if ("true".equalsIgnoreCase(env.getProperty("RENDER", "false"))) {
            return true;
        }
        return env.matchesProfiles("prod", "production");
    }
}
