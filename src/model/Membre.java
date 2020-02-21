package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Membre {
    private int numero;
    private HBox hBox;
    private int id;
    private String nom;
    private String prenom;
    private boolean type;
    private String telephone;

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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    public Membre(int numero,int id, String nom, String prenom, boolean type, String telephone) {
        this.numero = numero;
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.telephone = telephone;
    }

    public Membre(String nom, String prenom, boolean type, String telephone) {
        this.numero = numero;
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.telephone = telephone;
    }

    public static ArrayList<Membre> getMembers(String Communaute_ID, String type) throws SQLException {
        ArrayList<Membre> list = new ArrayList();
        Connection connection = Driver.connexion();
        String SQL = "";
        if(Communaute_ID == null){
            if(type == null){
                SQL = "SELECT DISTINCT * FROM Membre ORDER BY Nom, Prenom ASC";
            }else{
                SQL = "SELECT DISTINCT * FROM Membre WHERE Type_ = "+type+"  ORDER BY Nom, Prenom ASC";
            }
        }else{
            if(type == null){
                if(Communaute_ID.equals("-1")){
                    SQL = "SELECT DISTINCT * FROM Membre AS A WHERE (SELECT COUNT(*) FROM Membre_Communaute AS B WHERE A.Membre_ID = B.Membre_ID) = 0 ORDER BY Nom, Prenom ASC";
                }else{
                    SQL = "SELECT DISTINCT * FROM Membre NATURAL JOIN Membre_Communaute WHERE Communaute_ID = "+Communaute_ID+" ORDER BY Nom, Prenom ASC";
                }
            }else{
                if(Communaute_ID.equals("-1")){
                    SQL = "SELECT DISTINCT * FROM Membre AS A WHERE (SELECT COUNT(*) FROM Membre_Communaute AS B WHERE A.Membre_ID = B.Membre_ID) = 0 AND A.Type_ = "+type+" ORDER BY Nom, Prenom ASC";
                }else{
                    SQL = "SELECT DISTINCT * FROM Membre NATURAL JOIN Membre_Communaute WHERE Communaute_ID = "+Communaute_ID+" AND Type_ = "+type+" ORDER BY Nom, Prenom ASC";
                }

            }
        }
        ResultSet rs = connection.createStatement().executeQuery(SQL);
        int n = 0;
        while (rs.next()){
            n++;
            list.add(new Membre(n,rs.getInt("Membre_ID"),rs.getString("Nom"),rs.getString("prenom"),rs.getBoolean("Type_"),rs.getString("Telephone")));
        }
        return list;
    }

    public boolean save(){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT Membre_ID FROM Membre WHERE Nom = '" + nom.toUpperCase() + "' AND Prenom = '" + prenom.toLowerCase() + "'");
            if (!rs.next()) {
                connection.createStatement().executeUpdate("INSERT INTO Membre(Nom, Prenom, Type_, Telephone) VALUES ('" + nom.toUpperCase() + "','" + prenom.toLowerCase() + "','" + type + "','" + telephone.replaceAll("-", "") + "')");
            }else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static int getID(String name, String surname){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT Membre_ID FROM Membre WHERE Nom = '" + name.toUpperCase() + "' AND Prenom = '" + surname.toLowerCase() + "'");
            if (rs.next()) {
                return rs.getInt("Membre_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(String name, String surname, String telephone, boolean type){
        try {
            Connection connection = Driver.connexion();
            ResultSet rs = connection.createStatement().executeQuery("SELECT Membre_ID FROM Membre WHERE Nom = '" + name.toUpperCase() + "' AND Prenom = '" + surname.toLowerCase() + "' AND Membre_ID != '" + this.id + "'");
            if (!rs.next()) {
                connection.createStatement().executeUpdate("UPDATE Membre SET Nom = '" + name.toUpperCase() + "', Prenom = '" + surname.toLowerCase() + "', Type_ = '" + type + "',Telephone = '" + telephone.replaceAll("-", "") + "'\n"
                        + "WHERE Membre_ID = '" + this.id + "'");
            }else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static ArrayList<Communaute> getMemberCommunities(int Membre_ID) throws SQLException {
        ArrayList<Communaute> list = new ArrayList();
        Connection connection = Driver.connexion();
        ResultSet rs = connection.createStatement().executeQuery("SELECT Communaute_ID, Nom_C FROM Communaute NATURAL JOIN Membre_Communaute WHERE Membre_ID = '"+Membre_ID+"' ORDER BY Nom_C ASC");
        while (rs.next()){
            list.add(new Communaute(rs.getInt("Communaute_ID"),rs.getString("Nom_C")));
        }
        return list;
    }

    public StringProperty getNumeroProperty(){
        return new SimpleStringProperty(numero+"-");
    }

    public StringProperty getNomProperty(){
        return new SimpleStringProperty(nom);
    }

    public StringProperty getPrenomProperty(){
        return new SimpleStringProperty(prenom);
    }

    public StringProperty getTelProperty(){
        return new SimpleStringProperty(telephone);
    }
}
