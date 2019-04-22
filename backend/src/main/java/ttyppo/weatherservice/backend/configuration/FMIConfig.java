package ttyppo.weatherservice.backend.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.backend.datasource.fmi.FMI;

@Configuration
public class FMIConfig {

    private static final String BASE_URL = "http://opendata.fmi.fi";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public FMI fmi(RestTemplate restTemplate) {
        return new FMI(BASE_URL, restTemplate);
    }

}
