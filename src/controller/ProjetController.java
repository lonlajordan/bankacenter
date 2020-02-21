package controller;

import com.jfoenix.controls.JFXRadioButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Projet;
import model.ProjetTask;
import model.Sound;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProjetController extends Navigation implements Initializable {

    public static ObservableList<Projet> data = FXCollections.observableArrayList();

    @FXML
    private ImageView back;

    @FXML
    public JFXRadioButton actif, inactif, allState;

    @FXML
    private Button add, delete;

    @FXML
    private StackPane stackPane;

    @FXML
    private CheckBox selectAll;

    @FXML
    private TableView<Projet> table;


    @FXML
    private TableColumn<Projet, String> checkCol, nameCol, budjetCol, dateCol, progressCol;


    @FXML
    void processActif() throws SQLException, ParseException {
        add.setDisable(false);
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        reload();
    }

    public void reload() throws SQLException, ParseException {
        table.setContextMenu(new ContextMenu());
        MenuItem item1 = new MenuItem("Clôturer");
        MenuItem item2 = new MenuItem("Détails");
        MenuItem item3 = new MenuItem("Editer");
        item1.setOnAction(event -> {
            try {
                Projet.changeStatus(table.getSelectionModel().getSelectedItem().getId(),inactif.isSelected());
                Sound.play("/ressource/sounds/Windows-Notify.wav");
                reload();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        item2.setOnAction(event -> {
            DetailProjetController.projet = table.getSelectionModel().getSelectedItem();
            Stage preview = (Stage)add.getScene().getWindow();
            Stage stage = null;
            try {
                stage = loadWindow("Bilan du projet "+table.getSelectionModel().getSelectedItem().getDesignation(),"detailProjet.fxml","Bank.png",false,preview,false,false,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initOwner(preview);
            stage.initModality(Modality.APPLICATION_MODAL);
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
            stage.show();

        });
        item3.setOnAction(event -> {
            AddProjetController.projet = table.getSelectionModel().getSelectedItem();
            Stage preview = (Stage)add.getScene().getWindow();
            Stage stage = null;
            try {
                stage = loadWindow("Modification du projet","addProject.fxml","Bank.png",false,preview,false,false,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initOwner(preview);
            stage.initModality(Modality.APPLICATION_MODAL);
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
            stage.show();

        });
        if(actif.isSelected()){
            data.setAll(Projet.getProjects("true"));
            table.getContextMenu().getItems().setAll(item1,item2,item3);
        }else if(inactif.isSelected()){
            data.setAll(Projet.getProjects("false"));
            item1.setText("Relancer");
            table.getContextMenu().getItems().setAll(item1,item2,item3);
        }else{
            data.setAll(Projet.getProjects(null));
            table.getContextMenu().getItems().setAll(item2,item3);
        }
    }

    @FXML
    void processAdd(ActionEvent event) throws IOException {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        AddProjetController.projet = null;
        Stage preview = (Stage)add.getScene().getWindow();
        Stage stage = loadWindow("Nouveau projet","addProject.fxml","Bank.png",false,preview,false,false,false);
        stage.initOwner(preview);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    void processAllState(ActionEvent event) throws SQLException, ParseException {
        add.setDisable(true);
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        reload();
    }

    @FXML
    void processBack(MouseEvent event) throws IOException {
        Stage preview = (Stage)add.getScene().getWindow();
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,preview,true,true,false);
    }

    @FXML
    void processDelete(ActionEvent event) throws SQLException, ParseException {
        Stage stage = (Stage)delete.getScene().getWindow();
        int n = table.getSelectionModel().getSelectedItems().size();
        if(n == 0){
            Sound.play("/ressource/sounds/Windows-Foreground.wav");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Aucun projet sélectionné !");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Erreur");
            alert.setTitle(null);
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK);
        }else{
            Sound.play("/ressource/sounds/Windows-Exclamation.wav");
            String message = "Voulez-vous vraiment supprimer les "+n+" projets sélectionnés ?";
            if(n == 1)message = "Voulez-vous vraiment supprimer le projet sélectionné ?";
            ButtonType yes = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,yes,no);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Confirmation");
            alert.setTitle(null);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(no) == yes) {
                deleteSelection();
            }else{
                Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
                table.getSelectionModel().clearSelection();
            }
        }
    }

    void deleteSelection() throws SQLException, ParseException {
        Sound.play("/ressource/sounds/Windows-Recycle.wav");
        Projet.deleteProjects(table.getSelectionModel().getSelectedItems());
        reload();
    }

    @FXML
    void processInactif(ActionEvent event) throws SQLException, ParseException {
        add.setDisable(true);
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        reload();
    }

    @FXML
    void processSelectAll(ActionEvent event) {
        if(selectAll.isSelected()){
            table.getSelectionModel().selectAll();
        }else{
            table.getSelectionModel().clearSelection();
        }
    }

    public void showTable(){
        if(stackPane.getChildren().size() == 3){
            stackPane.getChildren().remove(1,2);
        }
        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        ProgressIndicator p = new ProgressIndicator();
        p.setMaxSize(150, 150);
        stackPane.getChildren().addAll(veil,p);
        Task<ObservableList<Projet>> task = new ProjetTask();
        p.progressProperty().bind(task.progressProperty());
        veil.visibleProperty().bind(task.runningProperty());
        p.visibleProperty().bind(task.runningProperty());
        table.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
        checkCol.setCellValueFactory(cellData -> cellData.getValue().getNumeroProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNomProperty());
        budjetCol.setCellValueFactory(cellData -> cellData.getValue().getBudjetProperty());
        dateCol.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
        progressCol.setCellValueFactory(cellData -> cellData.getValue().getProgressProperty());
        checkCol.setMaxWidth(50);
        checkCol.setMinWidth(50);
        budjetCol.setStyle("-fx-alignment: CENTER_RIGHT;");
        dateCol.setStyle("-fx-alignment: CENTER;");
        progressCol.setStyle("-fx-alignment: CENTER_RIGHT;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        back.setOnMouseExited((event)->{
            back.setImage(new Image("/ressource/img/GoBack.png"));
        });

        back.setOnMouseEntered((event)->{
            back.setImage(new Image("/ressource/img/BackArrow.png"));
        });
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setOnContextMenuRequested(event -> {
            int n = table.getSelectionModel().getSelectedIndices().size();
            if(n == 0 || n > 1){
                table.getContextMenu().hide();
            }
        });
        table.setOnMouseClicked(event -> {
            int n = table.getSelectionModel().getSelectedIndices().size();
            int m = table.getItems().size();
            if(n == m){
                selectAll.setSelected(true);
            }else{
                selectAll.setSelected(false);
            }
        });
        try {
            reload();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showTable();
    }
}
