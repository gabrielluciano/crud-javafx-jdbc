package com.gabrielluciano.crudjfxjdbc.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.gabrielluciano.crudjfxjdbc.db.DbException;
import com.gabrielluciano.crudjfxjdbc.gui.listeners.DataChangeListener;
import com.gabrielluciano.crudjfxjdbc.model.entities.Department;
import com.gabrielluciano.crudjfxjdbc.model.entities.Seller;
import com.gabrielluciano.crudjfxjdbc.model.exceptions.ValidationException;
import com.gabrielluciano.crudjfxjdbc.model.services.DepartmentService;
import com.gabrielluciano.crudjfxjdbc.model.services.SellerService;
import com.gabrielluciano.crudjfxjdbc.util.Alerts;
import com.gabrielluciano.crudjfxjdbc.util.Constraints;
import com.gabrielluciano.crudjfxjdbc.util.GUIUtils;
import com.gabrielluciano.crudjfxjdbc.util.NumberUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class SellerFormController implements Initializable {

    private Seller entity;

    private SellerService service;

    private DepartmentService departmentService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private DatePicker dpBirthDate;

    @FXML
    private TextField txtBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    private ObservableList<Department> obsList;

    public void setSeller(Seller entity) {
        this.entity = entity;
    }

    public void setServices(SellerService service, DepartmentService departmentService) {
        this.service = service;
        this.departmentService = departmentService;
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
        } catch (ValidationException e) {
            setErrorMessages(e.getErrors());
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

    public void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();
        if (fields.contains("name")) {
            labelErrorName.setText(errors.get("name"));
        }
    }

    public void updateFormData() {
        if (entity == null)
            throw new IllegalStateException("Entity was null");
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
        txtEmail.setText(entity.getEmail());
        Locale.setDefault(Locale.US);
        txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        dpBirthDate.setValue(entity.getBirthDate());
        if (entity.getDepartment() != null) {
            comboBoxDepartment.setValue(entity.getDepartment());
        } else {
            comboBoxDepartment.getSelectionModel().selectFirst();
        }
    }

    public void loadAssociatedObjects() {
        if (departmentService == null)
            throw new IllegalStateException("DepartmentService was null");

        List<Department> list = departmentService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(obsList);
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 70);
        Constraints.setTextFieldDouble(txtBaseSalary);
        Constraints.setTextFieldMaxLength(txtEmail, 60);
        GUIUtils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
        initializeComboBoxDepartment();
    }

    private Seller getFormData() {
        Seller seller = new Seller();

        ValidationException exception = new ValidationException("Validation error");

        seller.setId(NumberUtils.tryParseToInt(txtId.getText()));

        if (txtName.getText() == null || txtName.getText().isBlank()) {
            exception.addError("name", "Field can't be empty");
        }
        seller.setName(txtName.getText());

        if (exception.getErrors().size() > 0) {
            throw exception;
        }

        return seller;
    }

    private void notifyDataChangeListeners() {
        dataChangeListeners.stream().forEach(DataChangeListener::onDataChanged);
    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }
}
