package demo;

import framework.context.ApplicationContext;
import framework.context.XmlApplicationContext;
import metier.IMetier;

public class PresentationMiniFrameworkXmlSetter {

    public static void main(String[] args) {
        ApplicationContext context = new XmlApplicationContext("mini-framework-config.xml");

        IMetier metier = (IMetier) context.getBean("metierXmlSetter");

        System.out.println("RES = " + metier.calcul());
    }
}
