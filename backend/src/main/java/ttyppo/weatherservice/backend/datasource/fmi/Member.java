package ttyppo.weatherservice.backend.datasource.fmi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Member {

    @XmlElement(name = "BsWfsElement", namespace = "http://xml.fmi.fi/schema/wfs/2.0")
    private BsWfsElement bsWfsElement;

    public BsWfsElement getBsWfsElement() {
        return bsWfsElement;
    }

    public void setBsWfsElement(BsWfsElement bsWfsElement) {
        this.bsWfsElement = bsWfsElement;
    }
}

