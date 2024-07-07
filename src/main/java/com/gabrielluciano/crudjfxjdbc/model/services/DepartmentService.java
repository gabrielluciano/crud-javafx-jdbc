package com.gabrielluciano.crudjfxjdbc.model.services;

import java.util.List;

import com.gabrielluciano.crudjfxjdbc.model.dao.DaoFactory;
import com.gabrielluciano.crudjfxjdbc.model.dao.DepartmentDao;
import com.gabrielluciano.crudjfxjdbc.model.entities.Department;

public class DepartmentService {

    private DepartmentDao dao = DaoFactory.createDepartmentDao();

    public List<Department> findAll() {
        return dao.findAll();
    }
}
