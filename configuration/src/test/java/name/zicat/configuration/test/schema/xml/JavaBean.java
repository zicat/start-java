package name.zicat.configuration.test.schema.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "configuration")
public class JavaBean {

    private String name;
    private String value;

    public JavaBean() {

    }

    public JavaBean(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
