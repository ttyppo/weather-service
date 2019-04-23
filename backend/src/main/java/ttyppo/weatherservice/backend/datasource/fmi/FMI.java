package ttyppo.weatherservice.backend.datasource.fmi;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.backend.datasource.fmi.exception.MissingParameterException;
import ttyppo.weatherservice.backend.model.WeatherService;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherCondition;
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
                String currentTemperature = getParameterValue(featureCollection, "Temperature");
                String currentWeatherCondition = getParameterValue(featureCollection, "WeatherSymbol3");
                WeatherCondition currentWeather = new WeatherCondition();
                currentWeather.setTemperature(Float.parseFloat(currentTemperature));
                currentWeather.setIconId((int)Float.parseFloat(currentWeatherCondition));
                WeatherForecast forecast = new WeatherForecast();
                forecast.setWeatherServiceName(DISPLAY_NAME);
                forecast.setLocation(location);
                forecast.setCurrentWeather(currentWeather);
                return forecast;
            }
        } catch (RestClientException | MissingParameterException | NumberFormatException e) {
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
        params.put("parameters", "Temperature,WeatherSymbol3");
        params.put("timestep", "1");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Paris"));
        params.put("endtime", zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return params;
    }

    private static String getParameterValue(FeatureCollection featureCollection, String parameterName)
            throws MissingParameterException {
        return featureCollection.getMember().stream()
                .filter(member -> member.getBsWfsElement().getParameterName().equals(parameterName))
                .findFirst()
                .orElseThrow(() ->
                        new MissingParameterException("No '" + parameterName + "' found in the fetched data!"))
                .getBsWfsElement().getParameterValue();
    }
}
