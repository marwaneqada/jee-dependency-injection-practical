package framework.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "beans")
public class BeansConfiguration {

    private List<BeanDefinition> beans = new ArrayList<>();

    @XmlElement(name = "bean")
    public List<BeanDefinition> getBeans() {
        return beans;
    }

    public void setBeans(List<BeanDefinition> beans) {
        this.beans = beans;
    }
}
