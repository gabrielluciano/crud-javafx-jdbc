package com.gabrielluciano.crudjfxjdbc.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.gabrielluciano.crudjfxjdbc.util.Constraints;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {

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

    @FXML
    public void onBtSaveAction() {
        System.out.println("onBtSaveAction");
    }

    @FXML
    public void onBtCancelAction() {
        System.out.println("onBtCancelAction");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTexgFieldMaxLength(txtName, 30);
    }
}
