package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import model.DetailProjet;
import model.Driver;
import model.Projet;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DetailProjetController implements Initializable {

    public static Projet projet = null;
    public ArrayList<DetailProjet> list = new ArrayList();

    @FXML
    private TreeTableView<DetailProjet> table;

    @FXML
    private TreeTableColumn<DetailProjet, String> contributor, amount;

    @FXML
    private Label total;


    public void showTable() throws SQLException {
        amount.setStyle("-fx-alignment: CENTER_RIGHT;");
        final TreeItem<DetailProjet> root = new TreeItem<>(new DetailProjet("contributeur", "0"));
        Connection conn = Driver.connexion();
        ResultSet rs = conn.createStatement().executeQuery("SELECT Communaute_ID, Nom_C, (SELECT SUM(C.Montant) FROM Cotisation AS C WHERE C.Projet_ID = '" + projet.getId() + "' AND C.Communaute_ID = A.Communaute_ID) AS M  FROM Communaute AS A WHERE A.Communaute_ID IN (SELECT B.Communaute_ID FROM Cotisation AS B WHERE B.Projet_ID = '"+projet.getId()+"') ORDER BY Nom_C ASC");
        ResultSet rs2 = conn.createStatement().executeQuery("SELECT SUM(Montant) AS S FROM Cotisation WHERE Projet_ID = '" + projet.getId() + "' AND Communaute_ID = -1");
        ResultSet rs1 = null;
        try {
            int montant = 0, i = 0;
            while (rs.next()) {
                montant += rs.getInt("M");
                list.add(new DetailProjet(rs.getString("Nom_C"), rs.getString("M")));
                root.getChildren().add(new TreeItem<DetailProjet>(list.get(i)));
                    rs1 = conn.createStatement().executeQuery("SELECT DISTINCT Nom, SUM(Montant) AS S FROM Membre NATURAL JOIN Cotisation WHERE Communaute_ID = '" + rs.getString("Communaute_ID") + "' AND Projet_ID = '" + projet.getId() + "' GROUP BY Nom ORDER BY Nom ASC");
                    while (rs1.next()){
                        final TreeItem<DetailProjet> child = new TreeItem<>(new DetailProjet(rs1.getString("Nom"), rs1.getString("S")));
                        root.getChildren().get(i).getChildren().add(child);
                    }
                i++;
            }

            if (rs2.next()) {
                int surplus = rs2.getInt("S");
                if(surplus != 0){
                    montant += surplus;
                    list.add(new DetailProjet("INDIVIDUEL", surplus+""));
                    root.getChildren().add(new TreeItem<DetailProjet>(list.get(i)));
                    rs1 = conn.createStatement().executeQuery("SELECT DISTINCT Nom, SUM(Montant) AS S FROM Membre NATURAL JOIN Cotisation WHERE Communaute_ID = -1 AND Projet_ID = '" + projet.getId() + "' GROUP BY Nom ORDER BY Nom ASC");
                    while (rs1.next()){
                        final TreeItem<DetailProjet> child = new TreeItem<>(new DetailProjet(rs1.getString("Nom"), rs1.getString("S")));
                        root.getChildren().get(i).getChildren().add(child);
                    }
                    i++;
                }
            }

            total.setText("TOTAL : "+(new DetailProjet("contributor",montant+"").getAmount()+" FCFA"));
            root.setExpanded(true);
            table.setRoot(root);
            table.setShowRoot(false);
            contributor.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getValue().getContributor()));
            amount.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getValue().getAmount()));
        } catch (Exception ex) {
            System.err.println("Exception : " + ex.getMessage());
        }

        table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(projet != null){
            try {
                showTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
