package demo;

import dao.IDao;
import framework.annotations.Autowired;
import framework.annotations.Component;
import metier.IMetier;

@Component("metierSetterDemo")
public class MetierSetterDemo implements IMetier {

    private IDao dao;

    @Autowired
    public void setDao(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double data = dao.getData();
        return data * 43 / 3;
    }
}
