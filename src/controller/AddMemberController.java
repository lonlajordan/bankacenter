package controller;

import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Communaute;
import model.Driver;
import model.Membre;
import model.Sound;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddMemberController implements Initializable {

    public static ObservableList<Communaute> communities = FXCollections.observableArrayList();
    public static ComboBox<String> dropdown;
    public static Membre membre = null;
    public static CheckBox select = null;

    @FXML
    private TextField name, surname, telephone;

    @FXML
    private JFXRadioButton physique, morale, oui, Non;

    @FXML
    private GridPane grid;

    @FXML
    private ComboBox<String> association1, association2, association3;

    @FXML
    private Button save, cancel;

    @FXML
    void onNon(ActionEvent event) {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        grid.setVisible(false);
    }

    @FXML
    void onOui(ActionEvent event) {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        grid.setVisible(true);
    }

    @FXML
    void processCancel(ActionEvent event) {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        if(select != null) {
            select.setSelected(false);
            select.getOnAction().handle(null);
        }
        Stage stage = (Stage)cancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void processSave(ActionEvent event) throws SQLException {
        boolean error = false;
        Stage stage = (Stage)save.getScene().getWindow();
        String content = "";
        String title = "Information requise";
        if(name.getText().equals("")){
            content = "Veuillez entrer le nom du membre !";
            error = true;
        }else if(telephone.getText().equals("")){
            content = "Veuillez entrer le numéro de téléphone du membre !";
            error = true;
        }

        if(!error){
            if(membre == null){
                error = !(new Membre(name.getText(),surname.getText(),physique.isSelected(),telephone.getText())).save();
            }else{
                error = !membre.update(name.getText(),surname.getText(),telephone.getText(),physique.isSelected());
            }
            if(error){
                if(physique.isSelected()){
                    content = "Un membre ayant ce nom et ce prénom existe déjà !";
                }else{
                    content = "Un membre ayant ce nom existe déjà !";
                }
            }else{
                if(membre != null){
                    Connection connection = Driver.connexion();
                    connection.createStatement().executeUpdate("DELETE FROM Membre_Communaute WHERE Membre_ID = '"+membre.getId()+"'");
                }
                if(oui.isSelected()){
                    int id = Membre.getID(name.getText(),surname.getText());
                    int x = association1.getSelectionModel().getSelectedIndex();
                    int y = association2.getSelectionModel().getSelectedIndex();
                    int z = association3.getSelectionModel().getSelectedIndex();
                    Connection connection = Driver.connexion();
                    if(x >= 0){
                        connection.createStatement().executeUpdate("INSERT INTO Membre_Communaute(Membre_ID, Communaute_ID) VALUES ('" + id + "','" + communities.get(x).getId() + "')");
                    }
                    if(y >= 0 && y != x){
                        connection.createStatement().executeUpdate("INSERT INTO Membre_Communaute(Membre_ID, Communaute_ID) VALUES ('" + id + "','" + communities.get(y).getId() + "')");
                    }
                    if(z >= 0 && z != x && z != y){
                        connection.createStatement().executeUpdate("INSERT INTO Membre_Communaute(Membre_ID, Communaute_ID) VALUES ('" + id + "','" + communities.get(z).getId() + "')");
                    }
                }
            }
        }

        if(error){
            Alert alert = new Alert(Alert.AlertType.ERROR, content);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText(title);
            alert.setTitle(null);
            Sound.play("/ressource/sounds/Windows-Foreground.wav");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK);
        }else{
            Sound.play("/ressource/sounds/Windows-Notify.wav");
            stage.close();
            if(select != null){
                select.setSelected(false);
                select.getOnAction().handle(null);
            }
            dropdown.getOnAction().handle(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            communities = FXCollections.observableArrayList(Communaute.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        telephone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[0-9]*")) {
                    telephone.setText(oldValue);
                }
            }
        });

        if(membre != null){
            name.setText(membre.getNom());
            surname.setText(membre.getPrenom());
            telephone.setText(membre.getTelephone());
            physique.setSelected(membre.isType());
            morale.setSelected(!membre.isType());
            try {
                Connection connection = Driver.connexion();
                ResultSet rs = connection.createStatement().executeQuery("SELECT Nom_C FROM Communaute NATURAL JOIN Membre_Communaute WHERE Membre_ID ='"+membre.getId()+"'");
                int n = 0;
                while (rs.next()){
                    n++;
                    switch (n){
                        case 1:
                            association1.getSelectionModel().select(rs.getString("Nom_C"));
                            break;
                        case 2:
                            association2.getSelectionModel().select(rs.getString("Nom_C"));
                            break;
                        case 3:
                            association3.getSelectionModel().select(rs.getString("Nom_C"));
                            break;
                        default:
                            break;
                    }
                }
                if(n == 0){
                    oui.setSelected(false);
                    Non.setSelected(true);
                    grid.setVisible(false);
                }else{
                    oui.setSelected(true);
                    Non.setSelected(false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        association1.getEditor().setOnMouseClicked(event -> {
            if(association1.getEditor().getText().equals("")){
                association1.getSelectionModel().clearSelection();
            }
        });
        association2.getEditor().setOnMouseClicked(event -> {
            if(association2.getEditor().getText().equals("")){
                association2.getSelectionModel().clearSelection();
            }
        });
        association3.getEditor().setOnMouseClicked(event -> {
            if(association3.getEditor().getText().equals("")){
                association3.getSelectionModel().clearSelection();
            }
        });
        new Thread(){
            @Override
            public void run(){
                for(Communaute c : communities){
                    association1.getItems().add(c.getNom());
                    association2.getItems().add(c.getNom());
                    association3.getItems().add(c.getNom());
                }
                TextFields.bindAutoCompletion(association1.getEditor(), association1.getItems());
                TextFields.bindAutoCompletion(association2.getEditor(), association2.getItems());
                TextFields.bindAutoCompletion(association3.getEditor(), association3.getItems());
            }
        }.start();
    }
}
