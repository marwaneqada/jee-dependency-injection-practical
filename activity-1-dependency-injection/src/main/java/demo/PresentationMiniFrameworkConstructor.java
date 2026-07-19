package demo;

import framework.context.AnnotationApplicationContext;
import framework.context.ApplicationContext;
import metier.IMetier;

public class PresentationMiniFrameworkConstructor {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationApplicationContext(
                DaoDemo.class,
                MetierConstructorDemo.class
        );

        IMetier metier = (IMetier) context.getBean("metierConstructorDemo");

        System.out.println("RES = " + metier.calcul());
    }
}
