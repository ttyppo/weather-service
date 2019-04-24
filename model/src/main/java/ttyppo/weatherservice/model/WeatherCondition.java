package ttyppo.weatherservice.model;

import java.time.ZonedDateTime;

public class WeatherCondition {

    private Integer iconId;
    private Float temperature;
    private ZonedDateTime time;

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
}
