package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Sound;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginController extends Navigation{
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorMessage;

    @FXML
    void processForgotPassword(ActionEvent event) throws IOException {
        String user = System.getProperty("user.name");
        PrintWriter ecrivain;
        ecrivain = new PrintWriter(new BufferedWriter(new FileWriter("reset.bat")));
        ecrivain.println("@echo off");
        ecrivain.println("set user="+user);
        ecrivain.println("set a="+ Main.setting[0]);
        ecrivain.println("set b="+ Main.setting[1]);
        ecrivain.println("start cmd /c runas /user:\"%user%\" \"reset.exe \\\"%a%\\\" \\\"%b%\\\"");
        ecrivain.close();
        Process p = Runtime.getRuntime().exec("reset.bat");
    }

    @FXML
    void processLogin(ActionEvent event) throws IOException {
        if (username.getText().equals(Main.setting[0]) && password.getText().equals(Main.setting[1])) {
            Stage preview = (Stage)username.getScene().getWindow();
            loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,preview,true,true,false);
            Sound.play("/ressource/sounds/Windows-Logon.wav");
        }else{
            Sound.play("/ressource/sounds/Windows-Error.wav");
            errorMessage.setText("Nom d'utilisateur ou mot de passe incorrect");
        }
    }

    @FXML
    void resetMessage(KeyEvent event) {
        errorMessage.setText("");
    }
}
