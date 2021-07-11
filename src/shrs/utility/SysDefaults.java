/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import shrs.db.DBManager;

/**
 *
 * @author osraldo
 */
public class SysDefaults {
    private static String sSchoolName = null;
    private static String sSchoolAddr = null;
    private static String sDefaultNationality = null;
    private static int nMinComboYearValue = 0;
    
    public SysDefaults() {
        if(sSchoolName == null) InitDefaults();
    }
    
    private static void InitDefaults() {
        DBManager dbm = new DBManager();
        try {
            ResultSet rs = dbm.getResultSet("configs", "cf_minor,cf_general_1", "cf_major = 'CFG'", "");
            while(rs.next()) {
                switch(rs.getString("cf_minor")) {
                    case "SCN":
                        sSchoolName = rs.getString("cf_general_1");
                        break;
                    case "SAR":
                        sSchoolAddr = rs.getString("cf_general_1");
                        break;
                    case "MYR":
                        nMinComboYearValue = rs.getInt("cf_general_1");
                        break;
                    case "DFN":
                        sDefaultNationality = rs.getString("cf_general_1");
                        break;
                }
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    public static String getDefaultSchoolname() {
        if(sSchoolName == null) InitDefaults();
        return sSchoolName;
    }
    
    public static String getDefaultSchooladdress() {
        if(sSchoolAddr == null) InitDefaults();
        return sSchoolAddr;
    }
    
    public static String getDefaultNationality() {
        if(sDefaultNationality == null) InitDefaults();
        return sDefaultNationality;
    }
    
    public static int getMinimumCboYearValue() {
        if(nMinComboYearValue == 0) InitDefaults();
        return nMinComboYearValue;
    }
}
