package demo;

import framework.context.ApplicationContext;
import framework.context.XmlApplicationContext;
import metier.IMetier;

public class PresentationMiniFrameworkXmlConstructor {

    public static void main(String[] args) {
        ApplicationContext context = new XmlApplicationContext("mini-framework-config.xml");

        IMetier metier = (IMetier) context.getBean("metierXmlConstructor");

        System.out.println("RES = " + metier.calcul());
    }
}
