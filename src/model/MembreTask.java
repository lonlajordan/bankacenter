package model;

import controller.AssociationController;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class MembreTask extends Task<ObservableList<Membre>> {

    @Override
    protected ObservableList<Membre> call() throws Exception {
        for (int i = 0; i < 100; i++) {
            updateProgress(i, 100);
            Thread.sleep(5);
        }
        ObservableList<Membre> data = AssociationController.data;
        return data;
    }
}
