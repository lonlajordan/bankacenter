package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Communaute {
    private int id;
    private String nom;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Communaute(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Communaute(String nom) {
        this.nom = nom;
    }

    public static String getNameByID(String id){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT Nom_C FROM Communaute WHERE Communaute_ID = '" + id + "'");
            if (rs.next()) {
                return rs.getString("Nom_C");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean save(){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Communaute WHERE Nom_C = '" + this.nom.toUpperCase() + "'");
            if(rs.next()){
                return false;
            }else{
                connection.createStatement().executeUpdate("INSERT INTO Communaute(Nom_C) VALUES ('" + this.nom.toUpperCase() + "')");
            }
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return true;
    }

    public boolean update(){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Communaute WHERE Nom_C = '" + this.nom + "' AND Communaute_ID != '" + this.id + "'");
            if(rs.next()){
                return false;
            }else{
                connection.createStatement().executeUpdate("UPDATE Communaute SET Nom_C = '" + this.nom.toUpperCase() + "' WHERE Communaute_ID = '" + this.id + "'");
            }
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return true;
    }

    public void delete() throws SQLException {
        Connection conn = Driver.connexion();
        conn.createStatement().executeUpdate("DELETE FROM Cotisation WHERE Communaute_ID = '" + this.id + "'");
        conn.createStatement().executeUpdate("DELETE FROM Membre_Communaute WHERE Communaute_ID = '" + this.id + "'");
        conn.createStatement().executeUpdate("DELETE FROM Communaute WHERE Communaute_ID = '" + this.id + "'");
    }


    public static ArrayList<Communaute> getAll() throws SQLException {
        ArrayList<Communaute> list = new ArrayList();
        Connection connection = Driver.connexion();
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Communaute ORDER BY Nom_C ASC");
        while (rs.next()){
            list.add(new Communaute(rs.getInt("Communaute_ID"),rs.getString("Nom_C")));
        }
        return list;
    }
}
