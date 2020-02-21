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
import java.util.Date;

public class Projet {
    private int numero;
    private int id;
    private String designation; // représente le nom du projet
    private int budjet; // représente le budjet alloué pour ce projet
    private String date; // représente la date de début du projet
    private boolean active; // permet de savoir si le projet est en cours ou pas
    private int progress;

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getBudjet() {
        return budjet;
    }

    public void setBudjet(int budjet) {
        this.budjet = budjet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public StringProperty getNumeroProperty(){
        return new SimpleStringProperty(numero+"-");
    }

    public StringProperty getNomProperty(){
        return new SimpleStringProperty(designation);
    }

    public StringProperty getBudjetProperty(){
        return new SimpleStringProperty(monetaire(budjet+""));
    }

    public StringProperty getDateProperty(){
        return new SimpleStringProperty(date);
    }

    public StringProperty getProgressProperty(){
        return new SimpleStringProperty(monetaire(progress+""));
    }

    public Projet(String designation, int budjet, String date) {
        this.designation = designation;
        this.budjet = budjet;
        this.date = date;
    }

    public static int getIdByName(String Designation) throws SQLException {
        Connection connection = Driver.connexion();
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Projet WHERE Designation = '" + Designation.toUpperCase() + "'");
        if (rs.next()){
            return rs.getInt("Projet_ID");
        }
        return -1;
    }

    public Projet(int numero, int id, String designation, int budjet, String date, boolean active, int progress) {
        this.numero = numero;
        this.id = id;
        this.designation = designation;
        this.budjet = budjet;
        this.date = date;
        this.active = active;
        this.progress = progress;
    }

    public boolean save(){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Projet WHERE Designation = '" + this.designation.toUpperCase() + "'");
            if(rs.next()){
                return false;
            }else{
                connection.createStatement().executeUpdate("INSERT INTO Projet(Designation, Budget, Date_Debut,status) VALUES ('" + this.designation.toUpperCase() + "', '" + this.budjet + "', '" + this.date.toString() + "', true )");
            }
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return true;
    }

    public boolean update(){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Projet WHERE Designation = '" + this.designation + "' AND Projet_ID != '" + this.id + "'");
            if(rs.next()){
                return false;
            }else{
                connection.createStatement().executeUpdate("UPDATE Projet SET Designation = '" + this.designation.toUpperCase() + "', Budget = '" + this.budjet + "', Date_Debut = '" + this.date + "'  WHERE Projet_ID = '" + this.id + "'");
            }
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return true;
    }


    public static ArrayList<Projet> getProjects(String state) throws SQLException, ParseException {
        ArrayList<Projet> list = new ArrayList();
        Connection connection = Driver.connexion();
        String SQL = "";
        if(state == null){
            SQL = "SELECT DISTINCT Projet_ID, Designation, Budget, Date_Debut, status , (SELECT SUM(C.Montant) AS M FROM Cotisation AS C WHERE C.Projet_ID = P.Projet_ID) AS T FROM Projet AS P ORDER BY Designation, Budget ASC";
        }else{
            SQL = "SELECT DISTINCT Projet_ID, Designation, Budget, Date_Debut, status , (SELECT SUM(C.Montant) AS M FROM Cotisation AS C WHERE C.Projet_ID = P.Projet_ID) AS T FROM Projet AS P WHERE P.status = "+state+" ORDER BY Designation, Budget ASC";
        }
        ResultSet rs = connection.createStatement().executeQuery(SQL);
        int n = 0;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
        Date date = null;
        String newDate = null;
        while (rs.next()){
            n++;
            date = dt.parse(rs.getString("Date_Debut").substring(0,10));
            newDate = new SimpleDateFormat("dd-mm-YYYY").format(date);
            list.add(new Projet(n,rs.getInt("Projet_ID"),rs.getString("Designation"),rs.getInt("Budget"),newDate,rs.getBoolean("status"),rs.getInt("T")));
        }
        return list;
    }

    public static void deleteProjects(ObservableList<Projet> list) throws SQLException {
        Connection conn = Driver.connexion();
        for(int i = 0; i < list.size(); i++){
            conn.createStatement().executeUpdate("DELETE FROM Cotisation WHERE Projet_ID = '" + list.get(i).id + "'");
            conn.createStatement().executeUpdate("DELETE FROM Projet WHERE Projet_ID = '" + list.get(i).id + "'");
        }
    }

    public static void changeStatus(int id, boolean status) throws SQLException {
        Connection conn = Driver.connexion();
        conn.createStatement().executeUpdate("UPDATE Projet SET status  = '"+status+"' WHERE Projet_ID = '" + id + "'");
    }

    public String monetaire(String montant){
        String valeur = "0";
        int sig = 1;
        int n = Integer.parseInt(montant.replace(" ", ""));
        if(n <0){
            n = -n;
            sig = -1;
        }
        if(n != 0){
            do{
                int j = n%1000;
                String t = j+"";
                while(t.length() != 3){
                    t = "0"+t;
                }
                if(n == sig*Integer.parseInt(montant.replace(" ", ""))){
                    valeur = ""+t;
                }else{
                    valeur = t+" "+valeur;
                }
                n = n/1000;
            }while(n!=0);

            while(valeur.charAt(0) == '0'){
                valeur = valeur.substring(1);
            }
            if(sig == -1) valeur = "-"+valeur;
        }

        return valeur;
    }

}
