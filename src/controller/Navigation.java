package controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public abstract class Navigation implements Navigate {

    @Override
    public Stage loadWindow(String title, String window,String image, boolean resizable, Stage owner, boolean close, boolean show,boolean customMax) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream("/view/"+window);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource("/view/"+window));
        Parent root = loader.load(in);
        stage.setTitle(title);
        stage.centerOnScreen();
        if(customMax){
            stage.setMaximized(true);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(primaryScreenBounds.getMinX());
            stage.setY(primaryScreenBounds.getMinY());
            stage.setMaxWidth(primaryScreenBounds.getWidth());
            stage.setMinWidth(primaryScreenBounds.getWidth());
            stage.setMaxHeight(primaryScreenBounds.getHeight());
            stage.setMinHeight(primaryScreenBounds.getHeight());
        }

        stage.getIcons().add(new Image("ressource/img/"+image));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(resizable);
        if(show){
            stage.show();
        }
        if(close){
            owner.close();
        }
        return stage;
    }
}
