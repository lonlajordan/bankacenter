package controller;

import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Projet;
import model.Sound;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddProjetController implements Initializable {

    public static Projet projet = null;

    @FXML
    public TextField designation, budjet;

    @FXML
    private JFXDatePicker dateLancement;

    @FXML
    private Button cancel, save;

    @FXML
    void processCancel(ActionEvent event) {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        Stage stage = (Stage)cancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void processSave(ActionEvent event) throws SQLException, IOException, ParseException {
        boolean error = false;
        Stage stage = (Stage)save.getScene().getWindow();
        String content = "";
        String title = "Information requise";

        if(designation.getText().replaceAll(" ","").equals("")){
            content = "Veuillez entrer le nom du projet !";
            error = true;
        }else if(budjet.getText().equals("")){
            content = "Veuillez entrer le montant du budjet !";
            error = true;
        }else if(dateLancement.getEditor().getText().equals("")){
            content = "Veuillez choisir la date de lancement du projet !";
            error = true;
        }

        if(!error){
            if(projet == null){
                error = !(new Projet(designation.getText(),Integer.parseInt(budjet.getText()),dateLancement.getEditor().getText() + " 00:00:00")).save();
                if(error){
                    content = "Un projet portant le même nom existe déjà !";
                    title = "Projet existant";
                }
            }else{
                projet.setDesignation(designation.getText());
                projet.setBudjet(Integer.parseInt(budjet.getText()));
                projet.setDate(dateLancement.getEditor().getText() + " 00:00:00");
                error = !projet.update();
                if(error){
                    content = "Un autre projet portant le même nom existe déjà !";
                    title = "Projet existant";
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
            FXMLLoader loader = new FXMLLoader();
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(ProjetController.class.getResource("/view/project.fxml"));
            loader.load();
            ProjetController controller = loader.getController();
            controller.reload();
            Sound.play("/ressource/sounds/Windows-Notify.wav");
            stage.close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final String pattern = "yyyy-MM-dd";
        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        dateLancement.setConverter(converter);
        budjet.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[0-9]*")) {
                    budjet.setText(oldValue);
                }
            }
        });
        if(projet != null){
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
            designation.setText(projet.getDesignation());
            budjet.setText(projet.getBudjet()+"");
            dateLancement.setValue(LocalDate.parse(projet.getDate(),DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }

    }
}
