package com.gabrielluciano.crudjfxjdbc.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.gabrielluciano.crudjfxjdbc.db.DbException;
import com.gabrielluciano.crudjfxjdbc.gui.listeners.DataChangeListener;
import com.gabrielluciano.crudjfxjdbc.model.entities.Department;
import com.gabrielluciano.crudjfxjdbc.model.services.DepartmentService;
import com.gabrielluciano.crudjfxjdbc.util.Alerts;
import com.gabrielluciano.crudjfxjdbc.util.Constraints;
import com.gabrielluciano.crudjfxjdbc.util.GUIUtils;
import com.gabrielluciano.crudjfxjdbc.util.NumberUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class DepartmentFormController implements Initializable {

    private Department entity;

    private DepartmentService service;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private Label labelErrorName;

    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    public void setDepartment(Department entity) {
        this.entity = entity;
    }

    public void setDepartmentService(DepartmentService service) {
        this.service = service;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        if (entity == null)
            throw new IllegalStateException("Entity was null");
        if (service == null)
            throw new IllegalStateException("Service was null");

        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            GUIUtils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        GUIUtils.currentStage(event).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeNodes();
    }

    public void updateFormData() {
        if (entity == null)
            throw new IllegalStateException("Entity was null");
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTexgFieldMaxLength(txtName, 30);
    }

    private Department getFormData() {
        Department department = new Department();
        department.setId(NumberUtils.tryParseToInt(txtId.getText()));
        department.setName(txtName.getText());
        return department;
    }

    private void notifyDataChangeListeners() {
        dataChangeListeners.stream().forEach(DataChangeListener::onDataChanged);
    }
}
