package controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Sound;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends Navigation implements Initializable {

    @FXML
    private Button setting;

    @FXML
    private Label message;

    public static int etat = 1;
    public static String startmessage = "";

    @FXML
    void processAssociation(ActionEvent event) throws IOException {
        Stage preview = (Stage)setting.getScene().getWindow();
        Stage stage = loadWindow("Contributeurs","association.fxml","Bank.png",true,preview,true,true,true);
        stage.setOnCloseRequest(e->{
            try {
                loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,stage,true,true,false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        });
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }

    @FXML
    void processContribution(ActionEvent event) throws IOException {
        Stage preview = (Stage)setting.getScene().getWindow();
        Stage stage = loadWindow("Cotisations","cotisation.fxml","Bank.png",true,preview,true,true,true);
        stage.setOnCloseRequest(e->{
            try {
                loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,stage,true,true,false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        });
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }

    @FXML
    void processProject(ActionEvent event) throws IOException {
        Stage preview = (Stage)setting.getScene().getWindow();
        Stage stage = loadWindow("Projets","project.fxml","Bank.png",true,preview,true,true,true);
        stage.setOnCloseRequest(e->{
            try {
                loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,stage,true,true,false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        });
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }

    @FXML
    void processSetting(ActionEvent event) throws IOException {
        Stage preview = (Stage)setting.getScene().getWindow();
        Stage stage = loadWindow("Paramètres","setting.fxml","Bank.png",true,preview,true,true,true);
        stage.setOnCloseRequest(e->{
            try {
                loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,stage,true,true,false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        });
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
    }


    void setMessage(){
        if(etat < startmessage.length()){
            message.setAlignment(Pos.CENTER_RIGHT);
            message.setText(startmessage.substring(0,etat));
        }else{
            message.setAlignment(Pos.CENTER_LEFT);
            message.setText(startmessage.substring(etat-startmessage.length(), startmessage.length()));
            if(etat == 2*startmessage.length())etat  = 0;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startmessage = "SYSTEME DE GESTION DE PROJETS";
        etat = 1;
        if(Main.setting[5].equals("1")){
            TranslateTransition t = new TranslateTransition(Duration.seconds(0.1),message);
            t.play();
            t.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    etat++;
                    setMessage();
                    t.play();
                }

            });
        }
    }
}