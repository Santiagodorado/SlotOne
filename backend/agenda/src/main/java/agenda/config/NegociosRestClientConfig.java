package agenda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NegociosRestClientConfig {

    @Bean(name = "negociosRestClient")
    RestClient negociosRestClient(
            @Value("${slotone.negocios.base-url:http://localhost:5004}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
