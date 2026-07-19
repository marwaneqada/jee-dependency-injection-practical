package demo;

import framework.context.AnnotationApplicationContext;
import framework.context.ApplicationContext;
import metier.IMetier;

public class PresentationMiniFrameworkAnnotations {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationApplicationContext(
                DaoDemo.class,
                MetierDemo.class
        );

        IMetier metier = context.getBean(IMetier.class);

        System.out.println("RES = " + metier.calcul());
    }
}
