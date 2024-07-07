package com.gabrielluciano.crudjfxjdbc.model.dao;

import com.gabrielluciano.crudjfxjdbc.db.DB;
import com.gabrielluciano.crudjfxjdbc.model.dao.impl.DepartmentDaoJDBC;
import com.gabrielluciano.crudjfxjdbc.model.dao.impl.SellerDaoJDBC;

public class DaoFactory {

    private DaoFactory() {
    }

    public static SellerDao createSellerDao() {
        return new SellerDaoJDBC(DB.getConnection());
    }

    public static DepartmentDao createDepartmentDao() {
        return new DepartmentDaoJDBC(DB.getConnection());
    }
}
