package presentation;

import dao.IDao;
import metier.IMetier;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Scanner;

public class PresentationDynamique {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File("src/main/resources/config.txt"));

        String daoClassName = scanner.nextLine();
        Class<?> daoClass = Class.forName(daoClassName);
        IDao dao = (IDao) daoClass.getDeclaredConstructor().newInstance();

        String metierClassName = scanner.nextLine();
        Class<?> metierClass = Class.forName(metierClassName);
        IMetier metier = (IMetier) metierClass.getDeclaredConstructor().newInstance();

        Method setDao = metierClass.getMethod("setDao", IDao.class);
        setDao.invoke(metier, dao);

        System.out.println("RES = " + metier.calcul());
    }
}