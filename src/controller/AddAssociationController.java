package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Communaute;
import model.Sound;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddAssociationController implements Initializable {
    public static Communaute communaute = null;
    public static ComboBox<String> dropdown;

    @FXML
    private TextField name;

    @FXML
    void processCancel(ActionEvent event) {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        Stage stage = (Stage)name.getScene().getWindow();
        stage.close();
    }

    @FXML
    void processSave(ActionEvent event) throws IOException, SQLException {
        Stage stage = (Stage)name.getScene().getWindow();
        boolean error = name.getText().replaceAll(" ","").equals("");
        String content = "Veuillez entrer le nom de l'association !";
        String title = "Information requise";

        if(!error){
            if(communaute == null){
                error = !(new Communaute(name.getText())).save();
                if(error){
                    content = "Une association portant le même nom existe déjà !";
                    title = "Association existante";
                }
            }else{
                communaute.setNom(name.getText());
                error = !communaute.update();
                if(error){
                    content = "Une autre association portant le même nom existe déjà !";
                    title = "Association existante";
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
            AssociationController.communities = FXCollections.observableArrayList(Communaute.getAll());
            dropdown.getItems().clear();
            for(Communaute c : AssociationController.communities){
                dropdown.getItems().add(c.getNom());
            }
            dropdown.getItems().add("Toutes...");
            dropdown.getSelectionModel().selectFirst();
            Sound.play("/ressource/sounds/Windows-Notify.wav");
            stage.close();

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(communaute != null){
            name.setText(communaute.getNom());
        }
    }
}
