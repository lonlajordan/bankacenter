package controller;

import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Sound;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingController extends Navigation implements Initializable {

    @FXML
    private TextField filepath, username, password;

    @FXML
    private CheckBox checkBox;

    @FXML
    private JFXToggleButton sound, animation;

    @FXML
    private ImageView back;

    @FXML
    void goHome(MouseEvent event) throws IOException {
        Stage preview = (Stage)username.getScene().getWindow();
        loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,preview,true,true,false);
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }

    @FXML
    void processChooseFile(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Sélectionner un dossier");
        File dir = chooser.showDialog((Stage)username.getScene().getWindow());
        if (dir != null) {
            filepath.setText(dir.getAbsolutePath());
        }
    }

    @FXML
    void processSave(ActionEvent event) throws IOException {
        Stage stage = (Stage)username.getScene().getWindow();
        if(filepath.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez spécifiez l'emplacement de sauvegarde des fichiers !");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Information requise");
            alert.setTitle(null);
            Sound.play("/ressource/sounds/Windows-Foreground.wav");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK);
        }else{
            boolean error1 = password.getText().length() < 6;
            boolean error2 = username.getText().length() < 6;
            password.getTooltip().show(password, 0, 0);
            double tooltipMiddle1 = password.getTooltip().getWidth() / 2;
            password.getTooltip().hide();
            double nodeMiddle1 = password.getWidth() / 2;
            double nodeY1 = password.getHeight() / 2;
            Bounds bounds1 = password.localToScene(password.getBoundsInLocal());

            username.getTooltip().show(username, 0, 0);
            double tooltipMiddle2 = username.getTooltip().getWidth() / 2;
            username.getTooltip().hide();
            double nodeMiddle2 = username.getWidth() / 2;
            double nodeY2 = username.getHeight() / 2;
            Bounds bounds2 = username.localToScene(username.getBoundsInLocal());

            if(error1){
                password.getTooltip().show(password,
                        bounds1.getMinX() + nodeMiddle1 + tooltipMiddle1, bounds1.getMinY() + nodeY1);
            }

            if (error2){
                username.getTooltip().show(username,
                        bounds1.getMinX() + nodeMiddle1 + tooltipMiddle1, bounds2.getMinY() + nodeY2);
            }

            if(!error1 && !error2){
                PrintWriter writer =  new PrintWriter(new BufferedWriter(new FileWriter("setting.txt")));
                Main.setting[0] = username.getText();
                Main.setting[1] = password.getText();
                Main.setting[2] = filepath.getText();
                Main.setting[3] = checkBox.isSelected()?"1":"0";
                Main.setting[4] = sound.isSelected()?"0":"1";
                Main.setting[5] = animation.isSelected()?"0":"1";

                writer.println(username.getText());
                writer.println(password.getText());
                writer.println(filepath.getText());
                writer.println(checkBox.isSelected()?"1":"0");
                writer.println(sound.isSelected()?"0":"1");
                writer.println(animation.isSelected()?"0":"1");
                writer.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Les paramètres ont été enregistré avec succès !");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(stage);
                alert.getDialogPane().setHeaderText("Information");
                alert.setTitle(null);
                Sound.play("/ressource/sounds/Windows-Notify.wav");
                alert.showAndWait()
                        .filter(response -> response == ButtonType.OK);
            }else{
                Sound.play("/ressource/sounds/Windows-Foreground.wav");
            }

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checkBox.setSelected(Main.setting[3].equals("1"));
        username.setText(Main.setting[0]);
        password.setText(Main.setting[1]);
        Tooltip tooltip1 = new Tooltip("Le nom d'utilisateur doit contenir au moins 6 caractères");
        tooltip1.setAutoHide(true);
        Tooltip tooltip2 = new Tooltip("Le mot de passe doit contenir au moins 6 caractères");
        tooltip2.setAutoHide(true);
        username.setTooltip(tooltip1);
        password.setTooltip(tooltip2);
        sound.setSelected(Main.setting[4].equals("0"));
        animation.setSelected(Main.setting[5].equals("0"));
        filepath.setText(Main.setting[2]);
        filepath.selectPositionCaret(0);

        back.setOnMouseExited((event)->{
            back.setImage(new Image("/ressource/img/GoBack.png"));
        });

        back.setOnMouseEntered((event)->{
            back.setImage(new Image("/ressource/img/BackArrow.png"));
        });
    }

}
