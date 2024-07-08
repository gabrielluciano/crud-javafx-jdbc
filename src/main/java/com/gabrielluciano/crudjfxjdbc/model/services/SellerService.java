package com.gabrielluciano.crudjfxjdbc.model.services;

import java.util.List;

import com.gabrielluciano.crudjfxjdbc.model.dao.DaoFactory;
import com.gabrielluciano.crudjfxjdbc.model.dao.SellerDao;
import com.gabrielluciano.crudjfxjdbc.model.entities.Seller;

public class SellerService {

    private SellerDao dao = DaoFactory.createSellerDao();

    public List<Seller> findAll() {
        return dao.findAll();
    }

    public void saveOrUpdate(Seller seller) {
        if (seller.getId() == null) {
            dao.insert(seller);
        } else {
            dao.update(seller);
        }
    }

    public void remove(Seller seller) {
        dao.deleteById(seller.getId());
    }
}
