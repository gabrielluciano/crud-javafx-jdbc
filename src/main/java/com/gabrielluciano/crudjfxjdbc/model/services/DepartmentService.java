package com.gabrielluciano.crudjfxjdbc.model.services;

import java.util.ArrayList;
import java.util.List;

import com.gabrielluciano.crudjfxjdbc.model.entities.Department;

public class DepartmentService {

    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department(1, "Books"));
        departments.add(new Department(2, "Computers"));
        departments.add(new Department(3, "Electronics"));
        return departments;
    }
}
