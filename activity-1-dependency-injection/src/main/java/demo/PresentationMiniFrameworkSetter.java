package demo;

import framework.context.AnnotationApplicationContext;
import framework.context.ApplicationContext;
import metier.IMetier;

public class PresentationMiniFrameworkSetter {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationApplicationContext(
                DaoDemo.class,
                MetierSetterDemo.class
        );

        IMetier metier = (IMetier) context.getBean("metierSetterDemo");

        System.out.println("RES = " + metier.calcul());
    }
}
