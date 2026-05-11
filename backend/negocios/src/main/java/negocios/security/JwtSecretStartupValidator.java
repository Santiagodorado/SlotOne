package negocios.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class JwtSecretStartupValidator implements ApplicationRunner {

    private final Environment env;

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    public JwtSecretStartupValidator(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        StrictJwtSecretPolicy.validateOnStartup(jwtSecret, env);
    }
}
