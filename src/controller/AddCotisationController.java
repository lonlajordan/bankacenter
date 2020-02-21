package controller;

import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Cotisation;
import model.Membre;
import model.Sound;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddCotisationController implements Initializable {

    public static ObservableList<Membre> data = FXCollections.observableArrayList();
    public static String idCom, idProjet;
    public static Cotisation cotisation;
    public static ComboBox<String> association;
    public int day = 0, month = 0, year = 0;

    @FXML
    private TextField montant;

    @FXML
    private JFXDatePicker date;

    @FXML
    private ComboBox<String> contributor;

    @FXML
    private Button save, cancel;

    @FXML
    void processCancel(ActionEvent event) {
        Stage stage = (Stage)cancel.getScene().getWindow();
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        stage.close();
    }

    @FXML
    void processSave(ActionEvent event) throws IOException, ParseException {
        boolean error = false;
        Stage stage = (Stage)save.getScene().getWindow();
        String content = "";
        String title = "Information requise";
        if(montant.getText().equals("")){
            content = "Veuillez entrer le montant de la contribution !";
            error = true;
        }else if(date.getEditor().getText().equals("")){
            content = "Veuillez choisir la date de la contribution !";
            error = true;
        }

        if(!error){
            if(cotisation == null){
                error = !(new Cotisation(idProjet, idCom,data.get(contributor.getSelectionModel().getSelectedIndex()).getId(),0, "nom", "prenom", montant.getText(), date.getEditor().getText() + " 00:00:00")).save();
            }else{
                cotisation.setMontant(Integer.parseInt(montant.getText())+"");
                cotisation.setDate(date.getEditor().getText() + " 00:00:00");
                error = !cotisation.update(day, month, year);
            }
            if(error){
                title = "Attention";
                content = "Vous avez déjà enregistré une contribution en ce nom et à cette date";
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
            association.getOnAction().handle(null);
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

        date.setConverter(converter);
        montant.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[0-9]*")) {
                    montant.setText(oldValue);
                }
            }
        });
        try {
            if(idCom.equals("-1")){
                data = FXCollections.observableArrayList(Membre.getMembers(null,null));
            }else{
                data = FXCollections.observableArrayList(Membre.getMembers(idCom,null));
            }

            for(Membre m : data){
                contributor.getItems().add(m.getNom()+" "+m.getPrenom());
            }

            if(!data.isEmpty()){
                contributor.getSelectionModel().selectFirst();
                TextFields.bindAutoCompletion(contributor.getEditor(), contributor.getItems());
            }else{
                save.setDisable(true);
            }

            date.setValue(LocalDate.now());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(cotisation != null){
            montant.setText(cotisation.getMontant());
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
            date.setValue(LocalDate.parse(cotisation.getDate(),DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            contributor.getSelectionModel().select(cotisation.getNom()+" "+cotisation.getPrenom());
            contributor.setDisable(true);
            day = date.getValue().getDayOfMonth();
            month = date.getValue().getMonthValue();
            year = date.getValue().getYear();
        }
    }
}
