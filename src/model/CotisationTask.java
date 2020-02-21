package model;

import controller.ContributionController;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class CotisationTask extends Task<ObservableList<Cotisation>> {

    @Override
    protected ObservableList<Cotisation> call() throws Exception {
        for (int i = 0; i < 100; i++) {
            updateProgress(i, 100);
            Thread.sleep(5);
        }
        ObservableList<Cotisation> data = ContributionController.data;
        return data;
    }
}
