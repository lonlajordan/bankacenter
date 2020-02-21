package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {
    public static String[] setting = new String[6];

    @Override
    public void start(Stage primaryStage) throws Exception{
        loadSetting();
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream("/view/login.fxml");
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource("/view/login.fxml"));
        Parent root = loader.load(in);
        primaryStage.setTitle("Connexion");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image("/ressource/img/Bank.png"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadSetting() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("setting.txt"));
        String line = reader.readLine();
        int i = -1;
        while (line != null){
            i++;
            setting[i] = line;
            line = reader.readLine();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
