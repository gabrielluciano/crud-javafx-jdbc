package com.gabrielluciano.crudjfxjdbc.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.gabrielluciano.crudjfxjdbc.application.Main;
import com.gabrielluciano.crudjfxjdbc.db.DbException;
import com.gabrielluciano.crudjfxjdbc.gui.listeners.DataChangeListener;
import com.gabrielluciano.crudjfxjdbc.model.entities.Seller;
import com.gabrielluciano.crudjfxjdbc.model.services.DepartmentService;
import com.gabrielluciano.crudjfxjdbc.model.services.SellerService;
import com.gabrielluciano.crudjfxjdbc.util.Alerts;
import com.gabrielluciano.crudjfxjdbc.util.GUIUtils;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SellerListController implements Initializable, DataChangeListener {

    private SellerService service;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, LocalDate> tableColumnBirthDate;

    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Seller, Seller> tableColumnEdit;

    @FXML
    private TableColumn<Seller, Seller> tableColumnRemove;

    @FXML
    private Button btNew;

    private ObservableList<Seller> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Seller seller = new Seller();
        createDialogForm(seller, "/fxml/SellerForm.fxml", GUIUtils.currentStage(event));
    }

    public void setSellerService(SellerService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        GUIUtils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        GUIUtils.formatTableColumnDouble(tableColumnBaseSalary, 2);

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    private void initEditButtons() {
        tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Seller seller, boolean empty) {
                super.updateItem(seller, empty);

                if (seller == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                seller, "/fxml/SellerForm.fxml", GUIUtils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("remove");

            @Override
            protected void updateItem(Seller seller, boolean empty) {
                super.updateItem(seller, empty);

                if (seller == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> removeEntity(seller));
            }
        });
    }

    private void removeEntity(Seller seller) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
        if (result.get() == ButtonType.OK) {
            if (service == null)
                throw new IllegalStateException("Service was null");
            try {
                service.remove(seller);
                updateTableView();
            } catch (DbException e) {
                Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }

    public void updateTableView() {
        if (service == null)
            throw new IllegalStateException("Service was null");
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewSeller.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Seller seller, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();
            SellerFormController controller = loader.getController();
            controller.setSeller(seller);
            controller.setServices(new SellerService(), new DepartmentService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Seller data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(),
                    AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }
}
