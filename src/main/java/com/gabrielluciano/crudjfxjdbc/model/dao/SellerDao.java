package com.gabrielluciano.crudjfxjdbc.model.dao;

import java.util.List;

import com.gabrielluciano.crudjfxjdbc.model.entities.Department;
import com.gabrielluciano.crudjfxjdbc.model.entities.Seller;

public interface SellerDao {

    void insert(Seller seller);

    void update(Seller seller);

    void deleteById(Integer id);

    Seller findById(Integer id);

    List<Seller> findByDepartment(Department department);

    List<Seller> findAll();
}
