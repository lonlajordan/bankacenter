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
import model.*;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssociationController extends Navigation implements Initializable {

    public static ObservableList<Communaute> communities = FXCollections.observableArrayList();
    public static ObservableList<Membre> data = FXCollections.observableArrayList();

    @FXML
    private StackPane stackPane;

    @FXML
    private ImageView back;

    @FXML
    public ComboBox<String> association;

    @FXML
    private JFXRadioButton physique, morale;

    @FXML
    private Button modifyCom, deleteCom, add, delete;

    @FXML
    private TableView<Membre> table;

    @FXML
    private CheckBox selectAll;

    @FXML
    private TableColumn<Membre, String> checkCol, nameCol, surnameCol, telCol;

    @FXML
    void processAdd(ActionEvent event) throws IOException {
        AddMemberController.membre = null;
        AddMemberController.dropdown = association;
        Stage preview = (Stage)add.getScene().getWindow();
        Stage stage = loadWindow("Nouveau membre","addMember.fxml","Bank.png",false,preview,false,false,false);
        stage.initOwner(preview);
        stage.initModality(Modality.APPLICATION_MODAL);
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        stage.show();
    }

    @FXML
    void processAssociation(ActionEvent event) throws SQLException {
        String type = null;
        int n = association.getItems().size();
        int m = association.getSelectionModel().getSelectedIndex();
		
        if(m == n-1){
            deleteCom.setDisable(true);
            modifyCom.setDisable(true);
        }else{
            deleteCom.setDisable(false);
            modifyCom.setDisable(false);
        }
		
        if(physique.isSelected()){
            type = "true";
        }else if(morale.isSelected()){
            type = "false";
        }
		
        if(association.getSelectionModel().isSelected(n-1)){
            data.setAll(Membre.getMembers(null,type));
        }else{
            if(m < 0){
                data.setAll(Membre.getMembers("-1",type));
            }else{
                data.setAll(Membre.getMembers(communities.get(m).getId()+"",type));
            }
        }
    }

    @FXML
    void processBack(MouseEvent event) throws IOException {
        Stage preview = (Stage)add.getScene().getWindow();
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,preview,true,true,false);
    }

    @FXML
    void processDelete(ActionEvent event) throws SQLException, UnsupportedEncodingException {
        Stage stage = (Stage)delete.getScene().getWindow();
        int n = table.getSelectionModel().getSelectedItems().size();
        if(n == 0){
            Sound.play("/ressource/sounds/Windows-Foreground.wav");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Aucun membre sélectionné !");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Erreur");
            alert.setTitle(null);
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK);
        }else{
            int x = association.getSelectionModel().getSelectedIndex();
            String message = "Voulez-vous vraiment supprimer les "+n+" membres sélectionnés ?";
            if(x >= 0 && x < association.getItems().size()-1) message = "Voulez-vous vraiment retirer les "+n+" membres sélectionnés de l'association "+communities.get(x).getNom()+" ?";
            if(n == 1){
                message = "Voulez-vous vraiment supprimer le membre sélectionné ?";
                if(x >= 0 && x < association.getItems().size()-1) message = "Voulez-vous vraiment retirer le membre sélectionné de l'association "+communities.get(x).getNom()+" ?";
            }
            Sound.play("/ressource/sounds/Windows-Exclamation.wav");
            ButtonType yes = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,yes,no);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Confirmation");
            alert.setTitle(null);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(no) == yes) {
                Connection connection = Driver.connexion();
                if(x >= 0 && x < association.getItems().size()-1){
                    for(Membre m : table.getSelectionModel().getSelectedItems()){
                        connection.createStatement().executeUpdate("DELETE FROM Membre_Communaute WHERE Membre_ID = '" + m.getId() + "' AND Communaute_ID = '" + communities.get(x).getId() + "'");
                    }
                }else{
                    for(Membre m : table.getSelectionModel().getSelectedItems()){
                        connection.createStatement().executeUpdate("DELETE FROM Membre_Communaute WHERE Membre_ID = '" + m.getId() + "'");
                        connection.createStatement().executeUpdate("DELETE FROM Membre WHERE Membre_ID = '" + m.getId() + "'");
                    }
                }
                Sound.play("/ressource/sounds/Windows-Recycle.wav");
                processAssociation(null);
            }
            selectAll.setSelected(false);
            table.getSelectionModel().clearSelection();
        }
    }

    @FXML
    void processMorale(ActionEvent event) throws SQLException {
        if(association.getSelectionModel().isSelected(association.getItems().size()-1)){
            data.setAll(Membre.getMembers(null,"false"));
        }else{
            int n = association.getSelectionModel().getSelectedIndex();
            if(n < 0){
                data.setAll(Membre.getMembers("-1","false"));
            }else{
                data.setAll(Membre.getMembers(communities.get(association.getSelectionModel().getSelectedIndex()).getId()+"","false"));
            }
        }
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }

    @FXML
    void processPhysique(ActionEvent event) throws SQLException {
        if(association.getSelectionModel().isSelected(association.getItems().size()-1)){
            data.setAll(Membre.getMembers(null,"true"));
        }else{
            int n = association.getSelectionModel().getSelectedIndex();
            if(n < 0){
                data.setAll(Membre.getMembers("-1","true"));
            }else{
                data.setAll(Membre.getMembers(communities.get(association.getSelectionModel().getSelectedIndex()).getId()+"","true"));
            }

        }
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }

    @FXML
    void processTypeAll(ActionEvent event) throws SQLException {
        if(association.getSelectionModel().isSelected(association.getItems().size()-1)){
            data.setAll(Membre.getMembers(null,null));
        }else{
            int n = association.getSelectionModel().getSelectedIndex();
            if(n < 0){
                data.setAll(Membre.getMembers("-1",null));
            }else{
                data.setAll(Membre.getMembers(communities.get(association.getSelectionModel().getSelectedIndex()).getId()+"",null));
            }
        }
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }

    void showTable(){
        if(stackPane.getChildren().size() == 3){
            stackPane.getChildren().remove(1,2);
        }
        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        ProgressIndicator p = new ProgressIndicator();
        p.setMaxSize(150, 150);
        stackPane.getChildren().addAll(veil,p);
        Task<ObservableList<Membre>> task = new MembreTask();
        p.progressProperty().bind(task.progressProperty());
        veil.visibleProperty().bind(task.runningProperty());
        p.visibleProperty().bind(task.runningProperty());
        table.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
        checkCol.setCellValueFactory(cellData -> cellData.getValue().getNumeroProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNomProperty());
        surnameCol.setCellValueFactory(cellData -> cellData.getValue().getPrenomProperty());
        telCol.setCellValueFactory(cellData -> cellData.getValue().getTelProperty());
        telCol.setStyle("-fx-alignment: CENTER;");
        checkCol.setMaxWidth(50);
        checkCol.setMinWidth(50);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    void processSelectAll(ActionEvent event) {
        if(selectAll.isSelected()){
            table.getSelectionModel().selectAll();
        }else{
            table.getSelectionModel().clearSelection();
        }
    }

    @FXML
    void processAddCom(ActionEvent event) throws IOException {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        AddAssociationController.communaute = null;
        AddAssociationController.dropdown = association;
        Stage preview = (Stage)add.getScene().getWindow();
        Stage stage = loadWindow("Nouvelle association","addAssociation.fxml","Bank.png",false,preview,false,false,false);
        stage.initOwner(preview);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    void processDeleteCom(ActionEvent event) throws SQLException {
        Sound.play("/ressource/sounds/Windows-Exclamation.wav");
        Stage stage = (Stage)delete.getScene().getWindow();
        ButtonType yes = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer l'association sélectionnée ?",yes,no);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);
        alert.getDialogPane().setHeaderText("Confirmation");
        alert.setTitle(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == yes) {
            Sound.play("/ressource/sounds/Windows-Recycle.wav");
            communities.get(association.getSelectionModel().getSelectedIndex()).delete();
            communities = FXCollections.observableArrayList(Communaute.getAll());
            association.getItems().clear();
            for(Communaute c : AssociationController.communities){
                association.getItems().add(c.getNom());
            }
            association.getItems().add("Toutes...");
            association.getSelectionModel().selectFirst();
        }


    }

    @FXML
    void processModifyCom(ActionEvent event) throws IOException {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        AddAssociationController.communaute = communities.get(association.getSelectionModel().getSelectedIndex());
        Stage preview = (Stage)add.getScene().getWindow();
        Stage stage = loadWindow("Modification de l'association","addAssociation.fxml","Bank.png",false,preview,false,false,false);
        stage.initOwner(preview);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        back.setOnMouseExited((event)->{
            back.setImage(new Image("/ressource/img/GoBack.png"));
        });

        back.setOnMouseEntered((event)->{
            back.setImage(new Image("/ressource/img/BackArrow.png"));
        });
        try {
            communities = FXCollections.observableArrayList(Communaute.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //association.getEditor().setEditable(false);
        association.getEditor().setOnMouseClicked(event -> {
            if(association.getEditor().getText().equals("")){
                association.getSelectionModel().clearSelection();
                modifyCom.setDisable(true);
                deleteCom.setDisable(true);
            }
        });

        association.getItems().clear();
        for(Communaute c : communities){
            association.getItems().add(c.getNom());
        }
        association.getItems().add("Toutes...");
        association.getSelectionModel().selectFirst();
        try {
            processAssociation(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        MenuItem item1 = new MenuItem("Editer");
        MenuItem item2 = new MenuItem("Supprimer");
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(item1,item2);
        table.setContextMenu(contextMenu);
        String idCom = (association.getSelectionModel().isSelected(association.getItems().size()-1))?null:communities.get(association.getSelectionModel().getSelectedIndex()).getId()+"";
        try {
            data = FXCollections.observableArrayList(Membre.getMembers(idCom,"true"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        item1.setOnAction(event -> {
            AddMemberController.select = selectAll;
            AddMemberController.membre = table.getSelectionModel().getSelectedItem();
            AddMemberController.dropdown = association;
            Stage preview = (Stage)add.getScene().getWindow();
            Stage stage = null;
            try {
                stage = loadWindow("Modification du membre","addMember.fxml","Bank.png",false,preview,false,false,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initOwner(preview);
            stage.initModality(Modality.APPLICATION_MODAL);
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
            stage.show();
        });
        item2.setOnAction(event -> {
            try {
                processDelete(null);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        TextFields.bindAutoCompletion(association.getEditor(), association.getItems());
        showTable();
    }
}
