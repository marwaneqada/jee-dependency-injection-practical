package demo;

import dao.IDao;
import framework.annotations.Component;

@Component("daoDemo")
public class DaoDemo implements IDao {

    @Override
    public double getData() {
        System.out.println("Version DAO avec mini framework");
        return 30;
    }
}
