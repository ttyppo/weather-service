package ttyppo.weatherservice.backend.datasource.fmi;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherCondition;
import ttyppo.weatherservice.model.WeatherForecast;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FMITests {

    @Test
    public void weatherForecastShouldBeFetchedOK() throws Exception {
        // given
        Float temperature = 22.1f;
        Integer weatherSymbol = 1;
        String time = ZonedDateTime.now().withSecond(0).withZoneSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        Location location = new Location();
        location.setName("Some Town");
        List<Member> members = new ArrayList<>();
        members.add(getMember("Temperature", temperature.toString(), time));
        members.add(getMember("WeatherSymbol3", weatherSymbol.toString(), time));
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setMember(members);
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
        assertEquals(forecast.getCurrentWeather().getTemperature(), temperature);
        assertEquals(forecast.getCurrentWeather().getIconId(), weatherSymbol);
    }

    @Test
    public void shouldReturnNullIfRestClientException() throws Exception {
        // given
        String temperature = "22.1";
        Integer weatherSymbol = 1;
        String time = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        Location location = new Location();
        location.setName("Some Town");
        List<Member> members = new ArrayList<>();
        members.add(getMember("Temperature", temperature.toString(), time));
        members.add(getMember("WeatherSymbol3", weatherSymbol.toString(), time));
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setMember(members);
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

    @Test
    public void weatherConditionsShouldBeParsedOK() {
        // given
        Float temperature = 22.1f;
        Integer weatherSymbol = 1;
        String time = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        Float temperature2 = 22.2f;
        Integer weatherSymbol2 = 2;
        String time2 = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).plusHours(12).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        Location location = new Location();
        location.setName("Some Town");
        List<Member> members = new ArrayList<>();
        members.add(getMember("Temperature", temperature.toString(), time));
        members.add(getMember("WeatherSymbol3", weatherSymbol.toString(), time));
        members.add(getMember("Temperature", temperature2.toString(), time2));
        members.add(getMember("WeatherSymbol3", weatherSymbol2.toString(), time2));
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setMember(members);

        // when
        List<WeatherCondition> weatherConditions = FMI.getWeatherConditions(featureCollection)
                .collect(Collectors.toList());

        // expect
        assertNotNull(weatherConditions);
        assertEquals(weatherConditions.size(), 2);
        assertEquals(weatherConditions.get(0).getTemperature(), temperature);
        assertEquals(weatherConditions.get(0).getIconId(), weatherSymbol);
        assertEquals(weatherConditions.get(0).getTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME), time);
        assertEquals(weatherConditions.get(1).getTemperature(), temperature2);
        assertEquals(weatherConditions.get(1).getIconId(), weatherSymbol2);
        assertEquals(weatherConditions.get(1).getTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME), time2);
    }

    private Member getMember(String parameterName, String parameterValue, String time) {
        Member member = new Member();
        member.setBsWfsElement(getBsWfsElement(parameterName, parameterValue, time));
        return member;
    }

    private BsWfsElement getBsWfsElement(String parameterName, String parameterValue, String time) {
        BsWfsElement bsWfsElement = new BsWfsElement();
        bsWfsElement.setParameterName(parameterName);
        bsWfsElement.setParameterValue(parameterValue);
        bsWfsElement.setTime(time);
        return bsWfsElement;
    }
}
