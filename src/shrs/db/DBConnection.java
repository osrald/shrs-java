/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author osraldo
 */
public class DBConnection {
    private Connection cnDb = null;
    private boolean bIsConnected = false;
    private boolean bHasErrorConnection = true;
    private String sErrorMessage = "";


    public DBConnection(String sConn, String sUsername, String sUserpassword) {
        setDBConnection(sConn, sUsername, sUserpassword);
    }

    public void setDBConnection(String sConn, String sUSN, String sPWD){
               
        sConn += "?" + "user=" + sUSN + "&password=" + sPWD;

        try {
            cnDb = DriverManager.getConnection(sConn);
            bHasErrorConnection = false;
            bIsConnected = true;
            sErrorMessage = "";

        } catch(SQLException ex) {
            sErrorMessage = "SQLException: " + ex.getMessage();

            bHasErrorConnection = true;
            bIsConnected = false;
        }
    }

    public Connection getDBConnection() {
        return cnDb;
    }

    public boolean dbIsConnected() {
        return bIsConnected;
    }

    public boolean connHasError(){
        return bHasErrorConnection;
    }

    public String getErrorMessage() {
        return sErrorMessage;
    }

    public void setDBDriver(String sDriver) {
        try {
            Class.forName(sDriver).newInstance();
        } catch (Exception ex) {
            sErrorMessage = "SQLException: " + ex.getMessage();
            bHasErrorConnection = true;
        }
    }
}
