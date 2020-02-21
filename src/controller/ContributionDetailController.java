package controller;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContributionDetailController extends Navigation implements Initializable {
    public static ObservableList<Communaute> communities = FXCollections.observableArrayList();
    public static ObservableList<Cotisation> data = FXCollections.observableArrayList();
    public static String name = "";
    public static String surname = "";
    public static String project = "";


    @FXML
    private Label projetName, contributorName;

    @FXML
    private ComboBox<String> association;

    @FXML
    private Button delete;

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView<Cotisation> table;

    @FXML
    private TableColumn<Cotisation, String> checkCol, dateCol, montantCol;

    @FXML
    private CheckBox selectAll;

    @FXML
    void processAssociation(ActionEvent event) throws SQLException, ParseException {
        int n = association.getSelectionModel().getSelectedIndex();
        if(n >= 0){
            data.setAll(Cotisation.getCotisations(Projet.getIdByName(project)+"",communities.get(n).getId()+"",Membre.getID(name,surname)));
        }else{
            data.setAll(Cotisation.getCotisations(Projet.getIdByName(project)+"","-1",Membre.getID(name,surname)));
        }
    }


    public void ficheMembre(String Nom, String Designation, int Projet_ID) throws SQLException, IOException, DocumentException, ParseException {
        int total, b = 1, w = 1, h = 20;
        boolean val = false;
        ArrayList<Cotisation> list = new ArrayList();
        Connection conn = Driver.connexion();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Cotisation WHERE Membre_ID = '" + Membre.getID(name,surname) + "' AND Projet_ID = '" + Projet_ID + "' ORDER BY Communaute_ID, Date_");
        while (rs.next()) {
            list.add(new Cotisation(rs.getString("Communaute_ID"), rs.getString("Montant"), rs.getString("Date_")));
        }

        ResultSet rst = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation WHERE Membre_ID = '" + Membre.getID(name,surname) + "' AND Projet_ID = '" + Projet_ID + "'");
        rst.next();
        total = rst.getInt(1);

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(Main.setting[2]+"//Bilan_" + Nom + "_"+ Designation +".pdf"));
        PdfPTable table = new PdfPTable(3);
        PdfPCell cell ;
        table.setWidthPercentage(100);
        table.flushContent();
        doc.open();
        Font font = new Font(Font.FontFamily.HELVETICA, 25, Font.BOLD);
        Paragraph paragraph = new Paragraph("FICHE DE COTISATION INDIVIDUELLE",font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(10);
        doc.add(paragraph);
        font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        paragraph = new Paragraph("PROJET "+Designation.toUpperCase(),font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(10);
        doc.add(paragraph);
        font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        paragraph = new Paragraph("CONTRIBUTEUR : "+Nom,font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(20);
        doc.add(paragraph);
        cell = new PdfPCell(new Phrase("ASSOCIATION", FontFactory.getFont("Arial",14,Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setBorderWidth(w);
        cell.setMinimumHeight(h);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("DATE",FontFactory.getFont("Arial",14,Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setBorderWidth(w);
        cell.setMinimumHeight(h);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("MONTANT",FontFactory.getFont("Arial",14,Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setBorderWidth(w);
        cell.setMinimumHeight(h);
        table.addCell(cell);

        int l = list.size();
        if(l != 0){
            String x1 = list.get(0).getIdCommunaute(), x2 = x1;
            for (int i = 0; i < l; i++) {
                x2 = list.get(Math.min(i + 1, l - 1)).getIdCommunaute();
                if (i == 0 || val) {
                    cell = new PdfPCell(new Phrase(Communaute.getNameByID(x1),FontFactory.getFont("Arial",11)));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    ResultSet rsn = conn.createStatement().executeQuery("SELECT COUNT(*) FROM Cotisation WHERE Membre_ID = '" + Membre.getID(name,surname) + "' AND Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + x1 + "'");
                    rsn.next();
                    cell.setRowspan(rsn.getInt(1));
                    cell.setBorderWidth(w);
                    cell.setMinimumHeight(h);
                    table.addCell(cell);
                    val = false;
                }

                cell = new PdfPCell(new Phrase(list.get(i).getDate(),FontFactory.getFont("Arial",11)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidth(w);
                cell.setMinimumHeight(h);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(Formatter.monetaire(list.get(i).getMontant()),FontFactory.getFont("Arial",11)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidth(w);
                cell.setMinimumHeight(h);
                table.addCell(cell);

                if (!x1.equals(x2) || i == l - 1) {
                    cell = new PdfPCell(new Phrase("SOUS TOTAL "+b ,FontFactory.getFont("Arial",11, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setColspan(2);
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setBorderWidth(w);
                    cell.setMinimumHeight(h);
                    table.addCell(cell);
                    b++;
                    rst = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation WHERE Membre_ID = '" + Membre.getID(name,surname) + "' AND Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + x1 + "'");
                    rst.next();
                    cell = new PdfPCell(new Phrase(Formatter.monetaire(rst.getString(1)),FontFactory.getFont("Arial",11,Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setBorderWidth(w);
                    cell.setMinimumHeight(h);
                    table.addCell(cell);
                    val = true;
                    x1 = x2;
                }

            }

            cell = new PdfPCell(new Phrase("TOTAL",FontFactory.getFont("Arial",11,Font.BOLDITALIC)));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setColspan(2);
            cell.setBorderWidth(w);
            cell.setMinimumHeight(h);
            table.addCell(cell);
            b++;
            cell = new PdfPCell(new Phrase(Formatter.monetaire(total+""),FontFactory.getFont("Arial",11,Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidth(w);
            cell.setMinimumHeight(h);
            table.addCell(cell);
        }

        doc.add(table);
        NumberFormat formatter = new RuleBasedNumberFormat(RuleBasedNumberFormat.SPELLOUT);
        paragraph = new Paragraph("La contribution totale s'élève à ",FontFactory.getFont("Arial",12));
        paragraph.setSpacingAfter(10);
        paragraph.setSpacingBefore(10);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        doc.add(paragraph);
        paragraph = new Paragraph(formatter.format(total).toUpperCase().replace("-", " ") + "   FRANCS CFA",FontFactory.getFont("Arial",14,Font.BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        doc.add(paragraph);
        doc.close();
        Desktop.getDesktop().open(new File(Main.setting[2]+"//Bilan_" + Nom + "_"+ Designation +".pdf"));
    }

    @FXML
    void processDelete(ActionEvent event) throws SQLException, ParseException {
        Stage stage = (Stage)delete.getScene().getWindow();
        int n = table.getSelectionModel().getSelectedItems().size();
        if(n == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Aucune contribution sélectionnée !");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Erreur");
            alert.setTitle(null);
            Sound.play("/ressource/sounds/Windows-Foreground.wav");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK);
        }else{
            String message = "Voulez-vous vraiment supprimer les "+n+" contributions sélectionnées ?";
            if(n == 1)message = "Voulez-vous vraiment supprimer la contribution sélectionnée ?";
            ButtonType yes = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,yes,no);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Confirmation");
            alert.setTitle(null);
            Sound.play("/ressource/sounds/Windows-Exclamation.wav");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(no) == yes) {
                deleteSelection();
            }else{
                Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
                table.getSelectionModel().clearSelection();
            }
        }
    }

    void deleteSelection() throws SQLException, ParseException {
        Sound.play("/ressource/sounds/Windows-Recycle.wav");
        Cotisation.deleteCotisations(table.getSelectionModel().getSelectedItems());
        processAssociation(null);
    }

    @FXML
    void processBilan(ActionEvent event) throws IOException, SQLException, DocumentException, ParseException {
       Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
       ficheMembre(name+" "+surname,project,Projet.getIdByName(project));
    }


    public void showTable(){
        if(stackPane.getChildren().size() == 3){
            stackPane.getChildren().remove(1,2);
        }
        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        ProgressIndicator p = new ProgressIndicator();
        p.setMaxSize(150, 150);
        stackPane.getChildren().addAll(veil,p);
        Task<ObservableList<Cotisation>> task = new CotisationDetailTask();
        p.progressProperty().bind(task.progressProperty());
        veil.visibleProperty().bind(task.runningProperty());
        p.visibleProperty().bind(task.runningProperty());
        table.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
        checkCol.setCellValueFactory(cellData -> cellData.getValue().getNumeroProperty());
        dateCol.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
        montantCol.setCellValueFactory(cellData -> cellData.getValue().getMontantProperty());
        checkCol.setMaxWidth(50);
        checkCol.setMinWidth(50);
        dateCol.setStyle("-fx-alignment: CENTER;");
        montantCol.setStyle("-fx-alignment: CENTER_RIGHT;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setContextMenu(new ContextMenu());
        MenuItem item1 = new MenuItem("Editer");
        MenuItem item2 = new MenuItem("Supprimer");
        item1.setOnAction(event -> {
            AddCotisationController.cotisation = table.getSelectionModel().getSelectedItem();
            AddCotisationController.association = association;
            try {
                AddCotisationController.idProjet = Projet.getIdByName(project)+"";
            } catch (SQLException e) {
                e.printStackTrace();
            }
            int n = association.getSelectionModel().getSelectedIndex();
            if(n >= 0){
                AddCotisationController.idCom = communities.get(n).getId()+"";
            }else{
                AddCotisationController.idCom = "-1";
            }
            Stage preview = (Stage)delete.getScene().getWindow();
            Stage stage = null;
            try {
                stage = loadWindow("Modification de la contribution","addCotisation.fxml","Bank.png",false,preview,false,false,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initOwner(preview);
            stage.initModality(Modality.APPLICATION_MODAL);
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
            stage.show();

        });
        item2.setOnAction(event -> {
            try {
                processDelete(null);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        table.getContextMenu().getItems().setAll(item1,item2);
    }

    @FXML
    void processSelectAll(ActionEvent event) {
        if(selectAll.isSelected()){
            table.getSelectionModel().selectAll();
        }else{
            table.getSelectionModel().clearSelection();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            communities = FXCollections.observableArrayList(Membre.getMemberCommunities(Membre.getID(name,surname)));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        projetName.setText(project);
        contributorName.setText(name+" "+surname);
        association.getEditor().setEditable(false);
        association.getEditor().setOnMouseClicked(event -> {
            if(association.getEditor().getText().equals("")){
                association.getSelectionModel().clearSelection();
            }
        });

        new Thread(){
            @Override
            public void run(){
                association.getItems().clear();
                for(Communaute c : communities){
                    association.getItems().add(c.getNom());
                }
                if(!communities.isEmpty())association.getSelectionModel().selectFirst();
                table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                table.setOnContextMenuRequested(event -> {
                    int n = table.getSelectionModel().getSelectedIndices().size();
                    if(n == 0 || n > 1){
                        table.getContextMenu().hide();
                    }
                });
                table.setOnMouseClicked(event -> {
                    int n = table.getSelectionModel().getSelectedIndices().size();
                    int m = table.getItems().size();
                    if(n == m){
                        selectAll.setSelected(true);
                    }else{
                        selectAll.setSelected(false);
                    }
                });
                try {
                    processAssociation(null);
                    showTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
