package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Cotisation {
    private String nom;
    private String prenom;
    private String type;
    private String montant;
    private int idMembre;
    private int numero;
    private String date;
    private String normalDate;
    private String idCommunaute;
    private String idProjet;

    public Cotisation(String date, String idCommunaute) {
        this.date = date;
        this.idCommunaute = idCommunaute;
    }

    public String getIdCommunaute() {
        return idCommunaute;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(String idProjet) {
        this.idProjet = idProjet;
    }

    public void setIdCommunaute(String idCommunaute) {
        this.idCommunaute = idCommunaute;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public int getIdMembre() {
        return idMembre;
    }

    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }

    public Cotisation(String nom, String prenom, String type, String montant, int idMembre) {
        this.nom = nom;
        this.prenom = prenom;
        if(type.equalsIgnoreCase("true")){
            this.type = "Physique";
        }else{
            this.type = "Morale";
        }
        this.montant = montant;
        this.idMembre = idMembre;
    }

    public Cotisation(String idProjet, String idCommunaute, int idMembre, int numero, String nom, String prenom, String montant) throws ParseException {
        this.idProjet = idProjet;
        this.idCommunaute = idCommunaute;
        this.idMembre = idMembre;
        this.numero = numero;
        this.nom = nom;
        this.prenom = prenom;
        this.montant = montant;
    }

    public Cotisation(String idCommunaute, String montant, String date) throws ParseException {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
        this.date = new SimpleDateFormat("dd-mm-YYYY").format(dt.parse(date.substring(0,10)));
        this.idCommunaute = idCommunaute;
        this.montant = montant;
        this.normalDate = date;
    }

    public Cotisation(String idProjet, String idCommunaute, int idMembre, int numero, String nom, String prenom, String montant, String date) throws ParseException {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
        this.date = new SimpleDateFormat("dd-mm-YYYY").format(dt.parse(date.substring(0,10)));
        this.idProjet = idProjet;
        this.idCommunaute = idCommunaute;
        this.idMembre = idMembre;
        this.numero = numero;
        this.nom = nom;
        this.prenom = prenom;
        this.montant = montant;
        this.normalDate = date;
    }

    public static void deleteCotisations(ObservableList<Cotisation> list) throws SQLException, ParseException {
        Connection conn = Driver.connexion();
        for(int i = 0; i < list.size(); i++){
            conn.createStatement().executeUpdate("DELETE FROM Cotisation WHERE Membre_ID = '" + list.get(i).idMembre + "' AND Communaute_ID = '" + list.get(i).idCommunaute + "' AND Projet_ID = '" + list.get(i).idProjet+ "' AND Date_ = '" + list.get(i).normalDate + "'");
        }
    }

    public boolean save(){
        try {
            SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy");
            String newDate = new SimpleDateFormat("YYYY-mm-dd").format(dt.parse(this.date.substring(0,10)))+" 00:00:00";
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Cotisation WHERE Membre_ID = '" + this.idMembre + "' AND Communaute_ID = '" + this.idCommunaute + "' AND Projet_ID = '" + this.idProjet+ "' AND Date_ = '" + newDate + "'");
            if(rs.next()){
                return false;
            }else{
                connection.createStatement().executeUpdate("INSERT INTO Cotisation(Membre_ID, Communaute_ID, Projet_ID, Montant, Date_) VALUES ('" + this.idMembre + "', '" + this.idCommunaute + "', '" + this.idProjet + "', '" + this.montant+ "','" + newDate + "')");
            }
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return true;
    }

    public boolean update(){
        try {
            Connection connection = Driver.connexion();
            connection.createStatement().executeUpdate("UPDATE Cotisation SET Montant = '" + this.montant+ "' , Date_ = '" + this.date + "' WHERE Membre_ID = '" + this.idMembre + "' AND Communaute_ID = '" + this.idCommunaute + "' AND Projet_ID = '" + this.idProjet+ "' AND Date_ = '" + this.date + "'");
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return true;
    }

    public boolean update(int day, int month, int year){
        try {
            Connection connection = Driver.connexion();
            connection.createStatement().executeUpdate("UPDATE Cotisation SET Montant = '" + this.montant+ "' , Date_ = '" + this.date + "' WHERE Membre_ID = '" + this.idMembre + "' AND Communaute_ID = '" + this.idCommunaute + "' AND Projet_ID = '" + this.idProjet+ "' AND YEAR(Date_) = '" + year + "' AND MONTH(Date_) = '" + month + "' AND DAY(Date_) = '" + day + "'");
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return true;
    }

    public StringProperty getNumeroProperty(){
        return new SimpleStringProperty(numero+"-");
    }
    public StringProperty getNomProperty(){
        return new SimpleStringProperty(this.nom);
    }

    public StringProperty getPrenomProperty(){
        return new SimpleStringProperty(this.prenom);
    }

    public StringProperty getMontantProperty(){
        return new SimpleStringProperty(Formatter.monetaire(this.montant));
    }

    public  StringProperty getDateProperty(){
        return new SimpleStringProperty(this.date);
    }

    public static ArrayList<Cotisation> getCotisations(String Projet_ID, String Communaute_ID, int Membre_ID) throws SQLException, ParseException {
        ArrayList<Cotisation> list = new ArrayList();
        Connection connection = Driver.connexion();
        String SQL = "SELECT * FROM Membre NATURAL JOIN Cotisation WHERE Projet_ID = '"+Projet_ID+"' AND Communaute_ID = '"+Communaute_ID+"' AND Membre_ID = '"+Membre_ID+"' ORDER BY Date_ ASC";
        ResultSet rs = connection.createStatement().executeQuery(SQL);
        int n = 0;
        while (rs.next()){
            n++;
            list.add(new Cotisation(rs.getString("Projet_ID"),rs.getString("Communaute_ID"),rs.getInt("Membre_ID"),n,rs.getString("Nom"),rs.getString("Prenom"),rs.getString("Montant"),rs.getString("Date_")));
        }
        return list;
    }

    public static ArrayList<Cotisation> getCotisations(String Projet_ID, String Communaute_ID) throws SQLException, ParseException {
        ArrayList<Cotisation> list = new ArrayList();
        Connection connection = Driver.connexion();
        String SQL = "";
        if(Projet_ID == null){
            if(Communaute_ID == null){
                SQL = "SELECT Membre_ID, Nom, Prenom, SUM(Montant) AS M FROM Membre NATURAL JOIN Cotisation GROUP BY Membre_ID, Nom, Prenom ORDER BY Nom, Prenom ASC";
            }else{
                SQL = "SELECT Membre_ID, Nom, Prenom, SUM(Montant) AS M FROM Membre NATURAL JOIN Cotisation WHERE Communaute_ID = '"+Communaute_ID+"' GROUP BY Membre_ID, Nom, Prenom ORDER BY Nom, Prenom ASC";
            }
        }else{
            if(Communaute_ID == null){
                SQL = "SELECT Membre_ID, Nom, Prenom, SUM(Montant) AS M FROM Membre NATURAL JOIN Cotisation WHERE Projet_ID = '"+Projet_ID+"' GROUP BY Membre_ID, Nom, Prenom ORDER BY Nom, Prenom ASC";
            }else{
                SQL = "SELECT Membre_ID, Nom, Prenom, SUM(Montant) AS M FROM Membre NATURAL JOIN Cotisation WHERE Projet_ID = '"+Projet_ID+"' AND Communaute_ID = '"+Communaute_ID+"' GROUP BY Membre_ID, Nom, Prenom ORDER BY Nom, Prenom ASC";
            }
        }
        ResultSet rs = connection.createStatement().executeQuery(SQL);
        int n = 0;
        while (rs.next()){
            n++;
            list.add(new Cotisation(Projet_ID,Communaute_ID,rs.getInt("Membre_ID"),n,rs.getString("Nom"),rs.getString("Prenom"),rs.getString("M")));
        }
        return list;
    }

}
