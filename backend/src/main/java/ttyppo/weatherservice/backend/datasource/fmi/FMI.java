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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
            WeatherForecast forecast = new WeatherForecast();
            forecast.setWeatherServiceName(DISPLAY_NAME);
            forecast.setLocation(location);
            forecast.setCurrentWeather(getCurrentWeather(location));
            forecast.setForecastConditions(getWeatherForecast(location));
            return forecast;
        } catch (RestClientException | MissingParameterException | NumberFormatException e) {
            log.error("Failed to fetch weather forecast!", e);
        }
        return null;
    }

    private WeatherCondition getCurrentWeather(Location location) throws MissingParameterException {
        FeatureCollection featureCollection = restTemplate.getForObject(apiUrl, FeatureCollection.class,
                getQueryParameters(location, 0, 1));
        return featureCollection != null ? getWeatherConditions(featureCollection).get(0) : null;
    }

    private List<WeatherCondition> getWeatherForecast(Location location) {
        FeatureCollection featureCollection = restTemplate.getForObject(apiUrl, FeatureCollection.class,
                getQueryParameters(location, 5, 12 * 60));
        return featureCollection != null ? getWeatherConditions(featureCollection) : null;
    }

    private static Map<String, String> getQueryParameters(Location location, int daysForward, int intervalInMinutes) {
        Map<String, String> params = new HashMap<>();
        params.put("service", "WFS");
        params.put("version", "2.0.0");
        params.put("request", "getFeature");
        params.put("storedquery_id", "fmi::forecast::hirlam::surface::point::simple");
        params.put("place", location.getName().replaceAll(" ", ""));
        params.put("parameters", Arrays.stream(Parameters.values())
                .map(Parameters::getName)
                .collect(Collectors.joining(",")));
        params.put("timestep", Integer.toString(intervalInMinutes));
        ZonedDateTime zonedDateTime = ZonedDateTime.now()
                .withZoneSameInstant(ZoneOffset.UTC)
                .withSecond(0)
                .plusDays(daysForward);
        params.put("endtime", zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        return params;
    }

    static List<WeatherCondition> getWeatherConditions(FeatureCollection featureCollection) {
        return featureCollection.getMember().stream()
                .map(Member::getBsWfsElement)
                .filter(element -> !element.getParameterValue().equals("NaN"))
                .collect(Collectors.toMap(BsWfsElement::getTime, element -> {
                    WeatherCondition weatherCondition = new WeatherCondition();
                    weatherCondition.setTime(ZonedDateTime.parse(element.getTime()));
                    String parameterName = element.getParameterName();
                    if (Parameters.TEMPERATURE.name.equals(parameterName)) {
                        weatherCondition.setTemperature((Float.parseFloat(element.getParameterValue())));
                    } else if (Parameters.WEATHER_SYMBOL_3.name.equals(parameterName)) {
                        weatherCondition.setIconId((int) Float.parseFloat(element.getParameterValue()));
                    }
                    return weatherCondition;
                }, (condition1, condition2) -> {
                    WeatherCondition mergedCondition = new WeatherCondition();
                    mergedCondition.setTime(condition1.getTime());
                    mergedCondition.setIconId(condition1.getIconId() != null ?
                            condition1.getIconId() : condition2.getIconId());
                    mergedCondition.setTemperature(condition1.getTemperature() != null ?
                            condition1.getTemperature() : condition2.getTemperature());
                    return mergedCondition;
                })).values().stream().sorted(Comparator.comparing(WeatherCondition::getTime))
                .collect(Collectors.toList());
    }

    public enum Parameters {

        TEMPERATURE("Temperature"),
        WEATHER_SYMBOL_3("WeatherSymbol3");

        private final String name;

        private Parameters(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
