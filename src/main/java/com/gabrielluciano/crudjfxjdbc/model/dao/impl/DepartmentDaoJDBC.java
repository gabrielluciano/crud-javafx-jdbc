package com.gabrielluciano.crudjfxjdbc.model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gabrielluciano.crudjfxjdbc.db.DB;
import com.gabrielluciano.crudjfxjdbc.db.DbException;
import com.gabrielluciano.crudjfxjdbc.model.dao.DepartmentDao;
import com.gabrielluciano.crudjfxjdbc.model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department department) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement("""
                    INSERT INTO department
                    (Name) VALUES (?)
                    """, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, department.getName());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    department.setId(id);
                }
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException("Error inserting Department: " + e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void update(Department department) {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement("""
                    UPDATE department
                    SET Name = ?
                    WHERE Id = ?
                    """);
            stmt.setString(1, department.getName());
            stmt.setInt(2, department.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Error updating Department: " + e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement("""
                    DELETE FROM department
                    WHERE Id = ?
                    """);
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Error deleting Department: " + e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public Department findById(Integer id) {
        Department department = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement("""
                    SELECT Id, Name FROM department
                    WHERE Id = ?
                    """);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                department = instantiateDepartment(rs);
            }

            return department;
        } catch (SQLException e) {
            throw new DbException("Error getting Department: " + e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stmt);
        }
    }

    @Override
    public List<Department> findAll() {
        Department department = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement("""
                    SELECT Id, Name FROM department
                    ORDER By Name
                        """);
            rs = stmt.executeQuery();

            List<Department> departments = new ArrayList<>();
            while (rs.next()) {
                department = instantiateDepartment(rs);
                departments.add(department);
            }

            return departments;
        } catch (SQLException e) {
            throw new DbException("Error getting all Departments: " + e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(stmt);
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("Id"));
        department.setName(rs.getString("Name"));
        return department;
    }
}
