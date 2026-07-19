package demo;

import dao.IDao;
import framework.annotations.Autowired;
import framework.annotations.Component;
import metier.IMetier;

@Component("metierDemo")
public class MetierDemo implements IMetier {

    @Autowired
    private IDao dao;

    @Override
    public double calcul() {
        double data = dao.getData();
        return data * 43 / 3;
    }
}
