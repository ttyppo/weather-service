package ttyppo.weatherservice.backend.datasource.fmi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs/2.0" )
@XmlAccessorType(XmlAccessType.NONE)
public class FeatureCollection {

    @XmlElement(name = "member", namespace = "http://www.opengis.net/wfs/2.0")
    private List<Member> member;

    public List<Member> getMember() {
        return member;
    }

    public void setMember(List<Member> member) {
        this.member = member;
    }
}

