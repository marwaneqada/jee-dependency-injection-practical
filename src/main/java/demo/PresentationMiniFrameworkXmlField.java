package demo;

import framework.context.ApplicationContext;
import framework.context.XmlApplicationContext;
import metier.IMetier;

public class PresentationMiniFrameworkXmlField {

    public static void main(String[] args) {
        ApplicationContext context = new XmlApplicationContext("mini-framework-config.xml");

        IMetier metier = (IMetier) context.getBean("metierXmlField");

        System.out.println("RES = " + metier.calcul());
    }
}
