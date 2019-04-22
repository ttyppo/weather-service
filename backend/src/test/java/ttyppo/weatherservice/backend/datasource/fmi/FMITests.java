package ttyppo.weatherservice.backend.datasource.fmi;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherForecast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class FMITests {

    @Test
    public void weatherForecastShouldBeFetchedOK() throws Exception {
        // given
        String temperature = "22.1";
        Location location = new Location();
        location.setName("Some Town");
        BsWfsElement bsWfsElement = new BsWfsElement();
        bsWfsElement.setParameterValue(temperature);
        bsWfsElement.setParameterName("Temperature");
        bsWfsElement.setTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Member member = new Member();
        member.setBsWfsElement(bsWfsElement);
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setMember(Collections.singletonList(member));
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        String hostname = "http://localhost";

        // when
        Mockito.when(restTemplate.getForObject(
                eq(hostname + FMI.API_URL_PARAMS), eq(FeatureCollection.class), anyMap()
        )).thenReturn(featureCollection);
        FMI fmi = new FMI(hostname, restTemplate);
        WeatherForecast forecast = fmi.fetchForecast(location);

        // then
        assertNotNull(forecast);
        assertEquals(forecast.getWeatherServiceName(), FMI.DISPLAY_NAME);
        assertEquals(forecast.getLocation().getName(), location.getName());
        assertEquals(forecast.getCurrentTemperature(), temperature);
    }

    @Test
    public void shouldReturnNullIfRestClientException() throws Exception {
        // given
        String temperature = "22.1";
        Location location = new Location();
        location.setName("Some Town");
        BsWfsElement bsWfsElement = new BsWfsElement();
        bsWfsElement.setParameterValue(temperature);
        Member member = new Member();
        member.setBsWfsElement(bsWfsElement);
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setMember(Collections.singletonList(member));
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        String hostname = "http://localhost";

        // when
        Mockito.when(restTemplate.getForObject(
                eq(hostname + FMI.API_URL_PARAMS), eq(FeatureCollection.class), anyMap()
        )).thenThrow(new RestClientException("Problem occured!"));
        FMI fmi = new FMI(hostname, restTemplate);
        WeatherForecast forecast = fmi.fetchForecast(location);

        // then
        assertNull(forecast);
    }
}
