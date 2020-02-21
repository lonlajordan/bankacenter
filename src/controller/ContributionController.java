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
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import org.controlsfx.control.textfield.TextFields;

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
import java.util.ResourceBundle;

public class ContributionController extends Navigation implements Initializable {
    public static ObservableList<Projet> projects = FXCollections.observableArrayList();
    public static ObservableList<Communaute> communities = FXCollections.observableArrayList();
    public static ObservableList<Cotisation> data = FXCollections.observableArrayList();

    @FXML
    private ImageView back;

    @FXML
    private ComboBox<String> project, association;

    @FXML
    private Button add;

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView<Cotisation> table;

    @FXML
    private TableColumn<Cotisation, String> checkCol,nameCol, surnameCol, montantCol;

    @FXML
    private CheckBox selectAll;

    @FXML
    void processAdd(ActionEvent event) throws IOException {
        AddCotisationController.cotisation = null;
        AddCotisationController.association = association;
        AddCotisationController.idProjet = projects.get(project.getSelectionModel().getSelectedIndex()).getId()+"";
        int n = association.getSelectionModel().getSelectedIndex();
        if(n >= 0){
            AddCotisationController.idCom = communities.get(n).getId()+"";
        }else{
            AddCotisationController.idCom = "-1";
        }
        Stage preview = (Stage)add.getScene().getWindow();
        Stage stage = loadWindow("Nouvelle contribution","addCotisation.fxml","Bank.png",false,preview,false,false,false);
        stage.initOwner(preview);
        stage.initModality(Modality.APPLICATION_MODAL);
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        stage.show();
    }

    @FXML
    void processAssociation(ActionEvent event) throws SQLException, ParseException {
        processProject(event);
    }


    public void ficheCommunaute(String Nom_C, String Designation, int Projet_ID) throws SQLException, IOException, DocumentException {
        int total = 0, n1 = 0, n2 = 0, total1 = 0, b = 1, w = 1, h = 20;
        ArrayList<Cotisation> list = new ArrayList();
        Connection conn = Driver.connexion();
        String id = "-1";
        ResultSet rsn = conn.createStatement().executeQuery("SELECT Communaute_ID FROM Communaute WHERE Nom_C = '" + Nom_C + "'");
        if(rsn.next()) id = rsn.getString("Communaute_ID");
        ResultSet rs1 = conn.createStatement().executeQuery("SELECT Nom, Prenom, Type_, Membre_ID FROM Membre WHERE Membre_ID IN (SELECT Membre_ID FROM Cotisation WHERE Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + id + "') ORDER BY 3, 1, 2");
        ResultSet rs0 = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation WHERE Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + id + "'");
        if(rs0.next()) total = rs0.getInt(1);
        ResultSet rs2 = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation NATURAL JOIN Membre WHERE Type_ = 1 AND Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + id + "'");
        if(rs2.next()) total1 = rs2.getInt(1);
        ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT Membre_ID) FROM Cotisation NATURAL JOIN Membre WHERE Type_ = 1 AND Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + id + "'");
        if(rs3.next()) n1 = rs3.getInt(1);
        ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT Membre_ID) FROM Cotisation NATURAL JOIN Membre WHERE Type_ = 0 AND Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + id + "'");
        if(rs4.next()) n2 = rs4.getInt(1);
        while (rs1.next()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation WHERE Membre_ID = '" + rs1.getString(4) + "' AND Projet_ID = '" + Projet_ID + "' AND Communaute_ID = '" + id + "'");
            if (rs.next() && rs.getInt(1) != 0) {
                String m = "0";
                if (rs.getString(1) != null) {
                    m = rs.getString(1);
                }
                list.add(new Cotisation(rs1.getString("Nom"), rs1.getString("Prenom"), rs1.getString("Type_"), m, rs1.getInt("Membre_ID")));
            }
        }

        if(!list.isEmpty()){
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(Main.setting[2]+"//Bilan_" + ((Nom_C+"").equals("null")? "Individuel" : Nom_C) + "_"+ Designation +".pdf"));
            PdfPTable table = new PdfPTable(3);
            PdfPCell cell ;
            table.setWidthPercentage(100);
            table.flushContent();
            doc.open();
            Font font = new Font(Font.FontFamily.HELVETICA, 25, Font.BOLD);
            Paragraph paragraph = new Paragraph("FICHE DE COTISATION GLOBALE",font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(10);
            doc.add(paragraph);
            font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            paragraph = new Paragraph("PROJET "+Designation.toUpperCase(),font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(10);
            doc.add(paragraph);
            font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            paragraph = new Paragraph(((Nom_C+"").equals("null") ? "INDIVIDUALITéS".toUpperCase() : "ASSOCIATION "+Nom_C),font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(20);
            doc.add(paragraph);
            cell = new PdfPCell(new Phrase("TYPE", FontFactory.getFont("Arial",14,Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setBorderWidth(w);
            cell.setMinimumHeight(h);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("NOM ET PRENOM",FontFactory.getFont("Arial",14,Font.BOLD)));
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

                String x1 = list.get(0).getType(), x2 = x1;
                for (int i = 0; i < l; i++) {
                    x2 = list.get(Math.min(i + 1, l - 1)).getType();
                    if (i == 0 || b == 2) {
                        if(b==2)b++;
                        cell = new PdfPCell(new Phrase(list.get(i).getType(),FontFactory.getFont("Arial",11)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        if(x1.equalsIgnoreCase("Physique")){
                            cell.setRowspan(n1);
                        }else{
                            cell.setRowspan(n2);
                        }
                        cell.setBorderWidth(w);
                        cell.setMinimumHeight(h);
                        table.addCell(cell);
                    }

                    cell = new PdfPCell(new Phrase((list.get(i).getNom() + " " + list.get(i).getPrenom()).toUpperCase(),FontFactory.getFont("Arial",11)));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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
                        cell = new PdfPCell(new Phrase("SOUS TOTAL "+(b < 3 ? b : b-1) ,FontFactory.getFont("Arial",11, Font.BOLD)));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setColspan(2);
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setBorderWidth(w);
                        cell.setMinimumHeight(h);
                        table.addCell(cell);
                        b++;
                        cell = new PdfPCell(new Phrase(x1.equalsIgnoreCase("Physique")?Formatter.monetaire(total1+""):Formatter.monetaire((total-total1)+""),FontFactory.getFont("Arial",11,Font.BOLD)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setBorderWidth(w);
                        cell.setMinimumHeight(h);
                        table.addCell(cell);
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
            Desktop.getDesktop().open(new File(Main.setting[2]+"//Bilan_" + ((Nom_C+"").equals("null") ? "Individuel" : Nom_C) + "_"+ Designation +".pdf"));
        }else{
            Stage stage = (Stage)add.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aucune contribution de cette association enregistrée au compte du projet "+Designation);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Compte vide");
            alert.setTitle(null);
            Sound.play("/ressource/sounds/Windows-Notify.wav");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK);
        }
    }

    public void ficheProjet(int Projet_ID, String Designation) throws SQLException, IOException, DocumentException {
        int total = 0, n1 = 0, n2 = 0, total1 = 0, b = 1, w = 1, h = 20;
        ArrayList<Cotisation> list = new ArrayList();
        Connection conn = Driver.connexion();
        ResultSet rs1 = conn.createStatement().executeQuery("SELECT Nom, Prenom, Type_, Membre_ID FROM Membre WHERE Membre_ID IN (SELECT Membre_ID FROM Cotisation WHERE Projet_ID = '" + Projet_ID + "') ORDER BY 3, 1, 2");
        ResultSet rs0 = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation WHERE Projet_ID = '" + Projet_ID + "'");
        if(rs0.next()) total = rs0.getInt(1);
        ResultSet rs2 = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation NATURAL JOIN Membre WHERE Type_ = 1 AND Projet_ID = '" + Projet_ID + "'");
        if(rs2.next()) total1 = rs2.getInt(1);
        ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT Membre_ID) FROM Cotisation NATURAL JOIN Membre WHERE Type_ = 1 AND Projet_ID = '" + Projet_ID + "'");
        if(rs3.next()) n1 = rs3.getInt(1);
        ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT Membre_ID) FROM Cotisation NATURAL JOIN Membre WHERE Type_ = 0 AND Projet_ID = '" + Projet_ID + "'");
        if(rs4.next()) n2 = rs4.getInt(1);

        while (rs1.next()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT SUM(Montant) FROM Cotisation WHERE Membre_ID = '" + rs1.getString(4) + "' AND Projet_ID = '" + Projet_ID + "'");
            if (rs.next() && rs.getInt(1) != 0) {
                String m = "0";
                if (rs.getString(1) != null) {
                    m = rs.getString(1);
                }
                list.add(new Cotisation(rs1.getString("Nom"), rs1.getString("Prenom"), rs1.getString("Type_"), m, rs1.getInt("Membre_ID")));
            }
        }

        if(!list.isEmpty()){
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(Main.setting[2]+"//Bilan_" + Designation + ".pdf"));
            PdfPTable table = new PdfPTable(3);
            PdfPCell cell ;
            table.setWidthPercentage(100);
            table.flushContent();
            doc.open();
            Font font = new Font(Font.FontFamily.HELVETICA, 25, Font.BOLD);
            Paragraph paragraph = new Paragraph("FICHE DE COTISATION GLOBALE",font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(10);
            doc.add(paragraph);
            font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            paragraph = new Paragraph("PROJET "+Designation.toUpperCase(),font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(20);
            doc.add(paragraph);
            cell = new PdfPCell(new Phrase("TYPE", FontFactory.getFont("Arial",14,Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setBorderWidth(w);
            cell.setMinimumHeight(h);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("NOM ET PRENOM",FontFactory.getFont("Arial",14,Font.BOLD)));
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

                String x1 = list.get(0).getType(), x2 = x1;
                for (int i = 0; i < l; i++) {
                    x2 = list.get(Math.min(i + 1, l - 1)).getType();
                    if (i == 0 || b == 2) {
                        if(b==2)b++;
                        cell = new PdfPCell(new Phrase(list.get(i).getType(),FontFactory.getFont("Arial",11)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        if(x1.equalsIgnoreCase("Physique")){
                            cell.setRowspan(n1);
                        }else{
                            cell.setRowspan(n2);
                        }
                        cell.setBorderWidth(w);
                        cell.setMinimumHeight(h);
                        table.addCell(cell);
                    }

                    cell = new PdfPCell(new Phrase((list.get(i).getNom() + " " + list.get(i).getPrenom()).toUpperCase(),FontFactory.getFont("Arial",11)));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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
                        cell = new PdfPCell(new Phrase("SOUS TOTAL "+(b < 3 ? b : b-1) ,FontFactory.getFont("Arial",11, Font.BOLD)));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setColspan(2);
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setBorderWidth(w);
                        cell.setMinimumHeight(h);
                        table.addCell(cell);
                        b++;
                        cell = new PdfPCell(new Phrase(x1.equalsIgnoreCase("Physique")?Formatter.monetaire(total1+""):Formatter.monetaire((total-total1)+""),FontFactory.getFont("Arial",11,Font.BOLD)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setBorderWidth(w);
                        cell.setMinimumHeight(h);
                        table.addCell(cell);
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
            Desktop.getDesktop().open(new File(Main.setting[2]+"//Bilan_" + Designation + ".pdf"));
        }else{
            Stage stage = (Stage)add.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aucune contribution enregistrée au compte du projet "+Designation);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.getDialogPane().setHeaderText("Compte vide");
            alert.setTitle(null);
            Sound.play("/ressource/sounds/Windows-Notify.wav");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK);
        }
    }

    @FXML
    void processBack(MouseEvent event) throws IOException {
        Stage preview = (Stage)add.getScene().getWindow();
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        loadWindow("Système de gestion de projets","home.fxml","Bank.png",false,preview,true,true,false);
    }

    void deleteSelection() throws SQLException, ParseException {
        Sound.play("/ressource/sounds/Windows-Recycle.wav");
        Cotisation.deleteCotisations(table.getSelectionModel().getSelectedItems());
        processProject(null);
    }

    @FXML
    void processBilan1(ActionEvent event) throws IOException, SQLException, DocumentException {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        int n = project.getSelectionModel().getSelectedIndex();
        if(n >= 0)ficheCommunaute(association.getSelectionModel().getSelectedItem(),projects.get(n).getDesignation(),projects.get(n).getId());
    }

    @FXML
    void processBilan(ActionEvent event) throws IOException, SQLException, DocumentException {
        Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
        int n = project.getSelectionModel().getSelectedIndex();
        if(n >= 0) ficheProjet(projects.get(n).getId(),projects.get(n).getDesignation());
    }

    @FXML
    void processProject(ActionEvent event) throws SQLException, ParseException {
        int a = project.getItems().size();
        int b = association.getItems().size();
        if(a > 0) data.setAll(Cotisation.getCotisations(projects.get(project.getSelectionModel().getSelectedIndex()).getId()+"",null));
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
        Task<ObservableList<Cotisation>> task = new CotisationTask();
        p.progressProperty().bind(task.progressProperty());
        veil.visibleProperty().bind(task.runningProperty());
        p.visibleProperty().bind(task.runningProperty());
        table.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
        checkCol.setCellValueFactory(cellData -> cellData.getValue().getNumeroProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNomProperty());
        surnameCol.setCellValueFactory(cellData -> cellData.getValue().getPrenomProperty());
        montantCol.setCellValueFactory(cellData -> cellData.getValue().getMontantProperty());
        checkCol.setMaxWidth(50);
        checkCol.setMinWidth(50);
        montantCol.setStyle("-fx-alignment: CENTER_RIGHT;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setContextMenu(new ContextMenu());
        MenuItem item = new MenuItem("Détails");
        item.setOnAction(event -> {
            ContributionDetailController.project = project.getSelectionModel().getSelectedItem();
            ContributionDetailController.name = table.getSelectionModel().getSelectedItem().getNom();
            ContributionDetailController.surname = table.getSelectionModel().getSelectedItem().getPrenom();
            Stage preview = (Stage)add.getScene().getWindow();
            Stage stage = null;
            try {
                stage = loadWindow("Cotisations de "+ContributionDetailController.name+" "+ContributionDetailController.surname,"cotisationDetails.fxml","Bank.png",false,preview,false,false,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initOwner(preview);
            stage.initModality(Modality.APPLICATION_MODAL);
            Sound.play("/ressource/sounds/Windows-Navigation-Start.wav");
            stage.setOnCloseRequest(event1 -> {
                try {
                    processProject(null);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            stage.show();
        });
        table.getContextMenu().getItems().setAll(item);
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
        back.setOnMouseExited((event)->{
            back.setImage(new Image("/ressource/img/GoBack.png"));
        });

        back.setOnMouseEntered((event)->{
            back.setImage(new Image("/ressource/img/BackArrow.png"));
        });
        try {
            projects = FXCollections.observableArrayList(Projet.getProjects(null));
            communities = FXCollections.observableArrayList(Communaute.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

                project.getItems().clear();
                for(Projet p : projects){
                    project.getItems().add(p.getDesignation());
                }
                if(!projects.isEmpty())project.getSelectionModel().selectFirst();

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
                    processProject(null);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        showTable();
        TextFields.bindAutoCompletion(association.getEditor(), association.getItems());

    }
}
