package model;

import controller.ProjetController;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class ProjetTask extends Task<ObservableList<Projet>> {

    @Override
    protected ObservableList<Projet> call() throws Exception {
        for (int i = 0; i < 100; i++) {
            updateProgress(i, 100);
            Thread.sleep(5);
        }
        ObservableList<Projet> data = ProjetController.data;
        return data;
    }
}
