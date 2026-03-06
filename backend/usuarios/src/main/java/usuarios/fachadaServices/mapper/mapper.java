package usuarios.fachadaServices.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class mapper {
    @Bean 
    public ModelMapper crearMapper() {
        ModelMapper objMapper= new ModelMapper();
        return objMapper;//El objeto retornado se almacena en el contenedor de Spring
    }
}

