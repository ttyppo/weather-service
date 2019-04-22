package ttyppo.weatherservice.backend.datasource.fmi;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.backend.model.WeatherService;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherForecast;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FMI implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(FMI.class);

    static final String DISPLAY_NAME = "FMI (Ilmatieteenlaitos)";
    static final String API_URL_PARAMS = "/wfs?service={service}&version={version}&request={request}&" +
            "storedquery_id={storedquery_id}&place={place}&parameters={parameters}&timestep={timestep}&" +
            "endtime={endtime}";

    private final String apiUrl;
    private final RestTemplate restTemplate;

    public FMI(String baseUrl, RestTemplate restTemplate) {
        this.apiUrl = StringUtils.removeEnd(baseUrl, "/") + API_URL_PARAMS;
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherForecast fetchForecast(Location location) {
        try {
            FeatureCollection featureCollection = restTemplate.getForObject(apiUrl, FeatureCollection.class,
                    getQueryParameters(location));
            if (featureCollection != null) {
                String currentTemperature = featureCollection.getMember().stream()
                        .filter(member -> member.getBsWfsElement().getParameterName().equals("Temperature"))
                        .findFirst()
                        .orElseThrow(() -> new RestClientException("No 'Temperature' found in fetched data!"))
                        .getBsWfsElement().getParameterValue();
                WeatherForecast forecast = new WeatherForecast();
                forecast.setWeatherServiceName(DISPLAY_NAME);
                forecast.setLocation(location);
                forecast.setCurrentTemperature(currentTemperature);
                return forecast;
            }
        } catch (RestClientException e) {
            log.error("Weather request failed!", e);
        }
        return null;
    }

    static Map<String, String> getQueryParameters(Location location) {
        Map<String, String> params = new HashMap<>();
        params.put("service", "WFS");
        params.put("version", "2.0.0");
        params.put("request", "getFeature");
        params.put("storedquery_id", "fmi::forecast::hirlam::surface::point::simple");
        params.put("place", location.getName().replaceAll(" ", ""));
        params.put("parameters", "Temperature");
        params.put("timestep", "1");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Paris"));
        params.put("endtime", zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return params;
    }
}
