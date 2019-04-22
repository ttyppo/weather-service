package ttyppo.weatherservice.backend.datasource.fmi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class BsWfsElement {

    @XmlElement(name = "ParameterName", namespace = "http://xml.fmi.fi/schema/wfs/2.0")
    private String parameterName;

    @XmlElement(name = "ParameterValue", namespace = "http://xml.fmi.fi/schema/wfs/2.0")
    private String parameterValue;

    @XmlElement(name = "Time", namespace = "http://xml.fmi.fi/schema/wfs/2.0")
    private String time;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
