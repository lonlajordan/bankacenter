package controller;

import javafx.stage.Stage;

import java.io.IOException;

public interface Navigate {
    public Stage loadWindow(String title, String window,String image,boolean resizable, Stage owner, boolean close, boolean show, boolean customMax) throws IOException;
}
