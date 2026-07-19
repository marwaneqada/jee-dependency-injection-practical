package demo;

import dao.IDao;
import framework.annotations.Autowired;
import framework.annotations.Component;
import metier.IMetier;

@Component("metierConstructorDemo")
public class MetierConstructorDemo implements IMetier {

    private final IDao dao;

    @Autowired
    public MetierConstructorDemo(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double data = dao.getData();
        return data * 43 / 3;
    }
}
