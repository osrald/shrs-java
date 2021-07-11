/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.ui;

import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import shrs.utility.FieldValidator;
import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
import shrs.utility.SysDefaults;
/**
 *
 * @author osraldo
 */
public class JdlgRegStudentNew extends javax.swing.JDialog implements JICustomDialogEvents {

    private int m_MinCboYear = 0;
    private boolean m_bFlagFirstChoiceSchool = false;
    private boolean m_bFlagFirstChoiceTrack = false;
    private String m_sSchoolFirstChoice_others_nm = "";
    private String m_sSchoolFirstChoice_others_addr = "";
    private String m_sSchoolSecondChoice_others_nm = "";
    private String m_sSchoolSecondChoice_others_addr = "";
    /**
     * Creates new form JdlgRegStudentNew
     */
    public JdlgRegStudentNew(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
              
        setLocationRelativeTo(parent);
        
        InitDefaults();
        InitializeJCBOYears();
        LoadRegions();
        LoadSchools();
        LoadFirstChoiceTracks();
        LoadFirstChoiceStrandAndSpecs();

        m_bFlagFirstChoiceSchool = true;
        m_bFlagFirstChoiceTrack = true;
    }
    
    @Override
    public void setDialogSuccess(String sID) {
        //Intentionally Void
    }
    
    @Override
    public void setDialogSuccess(String sData1, String sData2, String sData3) {
        if("FirstChoice".equalsIgnoreCase(sData1)) {
            m_sSchoolFirstChoice_others_nm = sData2;
            m_sSchoolFirstChoice_others_addr = sData3;
        }
        else {
            m_sSchoolSecondChoice_others_nm = sData2;
            m_sSchoolSecondChoice_others_addr = sData3;
        }
    }
    
    private void InitDefaults() {
        m_MinCboYear = SysDefaults.getMinimumCboYearValue();
        jtxfNationality.setText(SysDefaults.getDefaultNationality());
    }
    
    private void LoadRegions() {
        DBManager dbm = new DBManager();
        try {
            ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", "cf_major = 'RGN'", "cf_minor asc");
            String sRegion = "";
            while(rs.next()) {
                sRegion = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                jcboElemRegion.addItem(sRegion);
                jcboJhsRegion.addItem(sRegion);
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void LoadSchools() {
        DBManager dbm = new DBManager();
        try {
            ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", "cf_major = 'SCH'", "cf_minor asc");
            String sSchool = "--- - --------------------------------------------------------";
            jcboSecondChoiceSchool.addItem(sSchool);
            while(rs.next()) {
                sSchool = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                jcboFirstChoiceSchool.addItem(sSchool);
                jcboSecondChoiceSchool.addItem(sSchool);
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void LoadFirstChoiceTracks() {
        DBManager dbm = new DBManager();
        try {
            String sFirstChoiceSchCd = (String)jcboFirstChoiceSchool.getSelectedItem();
            if(sFirstChoiceSchCd != null && !sFirstChoiceSchCd.isEmpty()) {
                sFirstChoiceSchCd = sFirstChoiceSchCd.substring(0, 3);
                String sFirstChoiceTrkCd = "";
                ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", 
                    "cf_major = 'TRK' and cf_general_2 = '" + sFirstChoiceSchCd + "'", 
                    "cf_primary_key asc");
                while(rs.next()) {
                    sFirstChoiceTrkCd = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                    jcboFirstChoiceTrack.addItem(sFirstChoiceTrkCd);
                }
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void LoadSecondChoiceTracks() {
        DBManager dbm = new DBManager();
        try {
            String sSecondChoiceSchCd = (String)jcboSecondChoiceSchool.getSelectedItem();
            if(sSecondChoiceSchCd != null && !sSecondChoiceSchCd.isEmpty()) {
                sSecondChoiceSchCd = sSecondChoiceSchCd.substring(0, 3);
                String sSecondChoiceTrkCd = "";
                ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", 
                    "cf_major = 'TRK' and cf_general_2 = '" + sSecondChoiceSchCd + "'", 
                    "cf_primary_key asc");
                while(rs.next()) {
                    sSecondChoiceTrkCd = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                    jcboSecondChoiceTrack.addItem(sSecondChoiceTrkCd);
                }
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }    
    
    private void LoadFirstChoiceStrandAndSpecs() {
        DBManager dbm = new DBManager();
        try {
            String sFirstChoiceSchCd = (String)jcboFirstChoiceSchool.getSelectedItem();
            String sFirstChoiceTrk = (String)jcboFirstChoiceTrack.getSelectedItem();
            if(sFirstChoiceSchCd != null && !sFirstChoiceSchCd.isEmpty()
                && sFirstChoiceTrk != null && !sFirstChoiceTrk.isEmpty()) {
                sFirstChoiceSchCd = sFirstChoiceSchCd.substring(0, 3);
                sFirstChoiceTrk = sFirstChoiceTrk.substring(0, 3);
                String sCond = "cf_major = 'SAS' and ";
                sCond += "cf_general_1 = '" + sFirstChoiceTrk + "' and ";
                sCond += "cf_general_2 = '" + sFirstChoiceSchCd + "'";
                String sFirstChoiceStrandSpecs = "";
                ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", 
                    sCond, "cf_primary_key asc");
                while(rs.next()) {
                    sFirstChoiceStrandSpecs = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                    jcboFirstChoiceStrandSpecs.addItem(sFirstChoiceStrandSpecs);
                }

                if("ACA".equalsIgnoreCase(sFirstChoiceTrk)) {
                    jlblFirstChoiceStrandSpecs.setText("Strand:");
                }
                else {
                    jlblFirstChoiceStrandSpecs.setText("Specialization:");
                }
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void LoadSecondChoiceStrandAndSpecs() {
        DBManager dbm = new DBManager();
        try {
            String sSecondChoiceSchCd = (String)jcboSecondChoiceSchool.getSelectedItem();
            String sSecondChoiceTrk = (String)jcboSecondChoiceTrack.getSelectedItem();
            if(sSecondChoiceSchCd != null && !sSecondChoiceSchCd.isEmpty()
                && sSecondChoiceTrk != null && !sSecondChoiceTrk.isEmpty()) {
                sSecondChoiceSchCd = sSecondChoiceSchCd.substring(0, 3);
                sSecondChoiceTrk = sSecondChoiceTrk.substring(0, 3);
                String sCond = "cf_major = 'SAS' and ";
                sCond += "cf_general_1 = '" + sSecondChoiceTrk + "' and ";
                sCond += "cf_general_2 = '" + sSecondChoiceSchCd + "'";
                String sSecondChoiceStrandSpecs = "";
                ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", 
                    sCond, "cf_primary_key asc");
                while(rs.next()) {
                    sSecondChoiceStrandSpecs = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                    jcboSecondChoiceStrandSpecs.addItem(sSecondChoiceStrandSpecs);
                }

                if("ACA".equalsIgnoreCase(sSecondChoiceTrk)) {
                    jlblSecondChoiceStrandSpecs.setText("Strand:");
                }
                else {
                    jlblSecondChoiceStrandSpecs.setText("Specialization:");
                }
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void InitializeJCBOYears() {
        jcboElemRegion.removeAllItems();
        jcboJhsRegion.removeAllItems();
        jcboYear.removeAllItems();
        jcboElemYrComp.removeAllItems();
        jcboJhsYrComp.removeAllItems();
        jcboElemPeptCompYr.removeAllItems();
        jcboElemAeTestCompYr.removeAllItems();
        jcboJhsPeptCompYr.removeAllItems();
        jcboJhsAeTestCompYr.removeAllItems();
        jcboFirstChoiceSchool.removeAllItems();
        jcboFirstChoiceTrack.removeAllItems();
        jcboSecondChoiceSchool.removeAllItems();
        jcboSecondChoiceTrack.removeAllItems();
        jcboFirstChoiceStrandSpecs.removeAllItems();
        jcboSecondChoiceStrandSpecs.removeAllItems();
        
        Calendar cal = Calendar.getInstance();
        
        setTitle("New Student - Registration  [School Year " + cal.get(Calendar.YEAR) 
                + "-" + (cal.get(Calendar.YEAR)+1) + "]");
        
        for(int ii = cal.get(Calendar.YEAR)+1; ii >= m_MinCboYear; ii--) {
            jcboYear.addItem(String.valueOf(ii));
            jcboElemYrComp.addItem(String.valueOf(ii));
            jcboJhsYrComp.addItem(String.valueOf(ii));
            jcboElemPeptCompYr.addItem(String.valueOf(ii));
            jcboElemAeTestCompYr.addItem(String.valueOf(ii));
            jcboJhsPeptCompYr.addItem(String.valueOf(ii));
            jcboJhsAeTestCompYr.addItem(String.valueOf(ii));
        }
        
        jcboYear.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR) - 16));
        jcboElemYrComp.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR) - 4));
        jcboJhsYrComp.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR)));
        jcboElemPeptCompYr.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR) - 4));
        jcboElemAeTestCompYr.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR) - 4));
        jcboJhsPeptCompYr.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR)));
        jcboJhsAeTestCompYr.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR)));
    }
    
    private boolean IsOldStudentDetected() {
        SPParam[] arrParams = new SPParam[5];
        arrParams[0] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 1, "psd_lastname", 
            jtxfLastname.getText());
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psd_firstname", 
            jtxfFirstname.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psd_middlename", 
            jtxfMiddlename.getText());
        arrParams[3] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 4, "psd_flag", 0);
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 5, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        SPParam[] outParams = dbm.executeStoredProc("SP_DetectStudent", arrParams);
        if (outParams != null) {
            Integer nOut = new Integer(outParams[0].getParamValue().toString());
            if(nOut == 1) {
                return true;
            }
        }
        else {
            JOptionPane.showMessageDialog(this, " Validation error: Please contact your system admin.",
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }
    
    private boolean ValidateFields() {
        FieldValidator fv = new FieldValidator(jtxfLastname.getText(), "Lastname");
        fv.setRequired(true);
        fv.setFieldlength(30);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfLastname.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfFirstname.getText(), "Firstname");
        fv.setRequired(true);
        fv.setFieldlength(30);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfFirstname.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfMiddlename.getText(), "Middlename");
        fv.setRequired(true);
        fv.setFieldlength(30);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfMiddlename.grabFocus();
            return fv.isValidated;
        }
        
        if(jbtgGender.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Gender (Male/Female) Required", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jrdoMale.grabFocus();
            return false;
        }
        
        String sDateOfBirth = (String)jcboMonth.getSelectedItem();
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboDay.getSelectedItem());
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboYear.getSelectedItem());
        try {
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            df.setLenient(false);
            df.parse(sDateOfBirth);
        }
        catch(ParseException pex) {
            JOptionPane.showMessageDialog(this, "[" + sDateOfBirth + "] Date is not valid", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jcboDay.grabFocus();
            return false;
        }
        
        fv = new FieldValidator(jtxfNationality.getText(), "Nationality");
        fv.setRequired(true);
        fv.setFieldlength(30);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfNationality.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfPlaceOfBirth.getText(), "Place of Birth");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfPlaceOfBirth.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxflElemSchoolName.getText(), "Name of Elementary School");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxflElemSchoolName.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfElemSchoolAddress.getText(), "Elementary School Address");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfElemSchoolAddress.grabFocus();
            return fv.isValidated;
        }
        
        if(jbtgElemPeptPasser.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Elementary PEPT is Required, Select No if not applicable", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jrdolElemPeptPasserNo.grabFocus();
            return false;
        }
        
        if(jbtgElemAeTestPasser.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Elementary A&E is Required, Select No if not applicable", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jrdoElemAeTestPasserNo.grabFocus();
            return false;
        }
        
        fv = new FieldValidator(jtxfJhsSchoolName.getText(), "Name of Junior High School");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfJhsSchoolName.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfJhsSchoolAddress.getText(), "Junior High School Address");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfJhsSchoolAddress.grabFocus();
            return fv.isValidated;
        }
        
        if(jbtgJhsPeptPasser.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "JHS PEPT is Required, Select No if not applicable", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jrdolJhsPeptPasserNo.grabFocus();
            return false;
        }
        
        if(jbtgJhsAeTestPasser.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "JHS A&E is Required, Select No if not applicable", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jrdoJhsAeTestPasserNo.grabFocus();
            return false;
        }
        
        if("999".equalsIgnoreCase(((String)jcboFirstChoiceSchool.getSelectedItem()).substring(0, 3))) {
            if(m_sSchoolFirstChoice_others_nm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have selected other schools as first choice, prease indicate the school name", 
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jcboFirstChoiceSchool.grabFocus();
                return false;
            }
            else if(m_sSchoolFirstChoice_others_addr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have selected other schools as first choice, prease indicate the school address", 
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jcboFirstChoiceSchool.grabFocus();
                return false;               
            }
        }
        
        if("999".equalsIgnoreCase(((String)jcboSecondChoiceSchool.getSelectedItem()).substring(0, 3))) {
            if(m_sSchoolSecondChoice_others_nm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have selected other schools as second choice, prease indicate the school name", 
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jcboSecondChoiceSchool.grabFocus();
                return false;
            }
            else if(m_sSchoolSecondChoice_others_addr.isEmpty())  {
                JOptionPane.showMessageDialog(this, "You have selected other schools as second choice, prease indicate the school address", 
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jcboSecondChoiceSchool.grabFocus();
                return false;   
            }
        }
        
        if(IsOldStudentDetected()) {
            JOptionPane.showMessageDialog(this, "Old student name detected, please inform your facilitator", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfLastname.grabFocus();
            return false;
        }

        return true;
    }
   
    private SPParam[] RegisterStudent() {
        SPParam[] arrParams = new SPParam[42];
        arrParams[0] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 1, "psd_lastname", 
            jtxfLastname.getText());
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psd_firstname", 
            jtxfFirstname.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psd_middlename", 
            jtxfMiddlename.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psd_gender", 
            jrdoMale.isSelected() ? "M" : "F");
        String sDateOfBirth = (String)jcboYear.getSelectedItem();
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboMonth.getSelectedItem());
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboDay.getSelectedItem());
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psd_dob", sDateOfBirth);
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "psd_nationality", 
            jtxfNationality.getText());	
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psd_birthplace", 
            jtxfPlaceOfBirth.getText());
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psd_elem_name", 
            jtxflElemSchoolName.getText());
        arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "psd_elem_school_addr", 
            jtxfElemSchoolAddress.getText());
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 10, "psd_elem_comp_month", 
            (String)jcboElemMnthComp.getSelectedItem());
        arrParams[10] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 11, "psd_elem_comp_year", 
            (String)jcboElemYrComp.getSelectedItem());
        arrParams[11] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 12, "psd_elem_region_cd", 
            ((String)jcboElemRegion.getSelectedItem()).substring(0, 3));
        arrParams[12] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 13, "psd_elem_pept_passer", 
            jrdolElemPeptPasserNo.isSelected() ? "N" : "Y");
        arrParams[13] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 14, "psd_elem_pept_month", 
            jrdolElemPeptPasserNo.isSelected() ? "" : (String)jcboElemPeptCompMnt.getSelectedItem());
        arrParams[14] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 15, "psd_elem_pept_year", 
            jrdolElemPeptPasserNo.isSelected() ? "" : (String)jcboElemPeptCompYr.getSelectedItem());
        arrParams[15] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 16, "psd_elem_ae_passer", 
            jrdoElemAeTestPasserNo.isSelected() ? "N" : "Y");
        arrParams[16] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 17, "psd_elem_ae_month", 
            jrdoElemAeTestPasserNo.isSelected() ? "" : (String)jcboElemAeTestCompMnt.getSelectedItem());
        arrParams[17] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 18, "psd_elem_ae_year", 
            jrdoElemAeTestPasserNo.isSelected() ? "" : (String)jcboElemAeTestCompYr.getSelectedItem());
        arrParams[18] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 19, "psd_jhs_name", 
            jtxfJhsSchoolName.getText());
        arrParams[19] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 20, "psd_jhs_school_addr", 
            jtxfJhsSchoolAddress.getText());
        arrParams[20] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 21, "psd_jhs_comp_month", 
            (String)jcboJhsMnthComp.getSelectedItem());
        arrParams[21] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 22, "psd_jhs_comp_year", 
            (String)jcboJhsYrComp.getSelectedItem());
        arrParams[22] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 23, "psd_jhs_region_cd", 
            ((String)jcboJhsRegion.getSelectedItem()).substring(0, 3));
        arrParams[23] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 24, "psd_jhs_pept_passer", 
            jrdolJhsPeptPasserNo.isSelected() ? "N" : "Y");
        arrParams[24] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 25, "psd_jhs_pept_month", 
            jrdolJhsPeptPasserNo.isSelected() ? "" : (String)jcboJhsPeptCompMnt.getSelectedItem());
        arrParams[25] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 26, "psd_jhs_pept_year", 
            jrdolJhsPeptPasserNo.isSelected() ? "" : (String)jcboJhsPeptCompYr.getSelectedItem());
        arrParams[26] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 27, "psd_jhs_ae_passer", 
            jrdoJhsAeTestPasserNo.isSelected() ? "N" : "Y");
        arrParams[27] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 28, "psd_jhs_ae_month", 
            jrdoJhsAeTestPasserNo.isSelected() ? "" : (String)jcboJhsAeTestCompMnt.getSelectedItem());
        arrParams[28] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 29, "psd_jhs_ae_year", 
            jrdoJhsAeTestPasserNo.isSelected() ? "" : (String)jcboJhsAeTestCompYr.getSelectedItem());
        
        arrParams[29] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 30, "psd_shs_school_firstchoice_cd", 
            ((String)jcboFirstChoiceSchool.getSelectedItem()).substring(0, 3));
        arrParams[30] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 31, "psd_shs_school_firstchoice_others_nm", 
            m_sSchoolFirstChoice_others_nm);  
        arrParams[31] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 32, "psd_shs_school_firstchoice_others_addr", 
            m_sSchoolFirstChoice_others_addr);
        
        arrParams[32] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 33, "psd_shs_track_firstchoice_cd", 
            ((String)jcboFirstChoiceTrack.getSelectedItem()).substring(0, 3));
        arrParams[33] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 34, "psd_shs_strspec_firstchoice_cd", 
            ((String)jcboFirstChoiceStrandSpecs.getSelectedItem()).substring(0, 3));
        
        if (!"---".equalsIgnoreCase(((String)jcboSecondChoiceSchool.getSelectedItem()).substring(0, 3))) {
            arrParams[34] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 35, "psd_shs_school_secondchoice_cd", 
                ((String)jcboSecondChoiceSchool.getSelectedItem()).substring(0, 3));
            arrParams[35] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 36, "psd_shs_school_secondchoice_others_nm", 
                m_sSchoolSecondChoice_others_nm);  
            arrParams[36] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 37, "psd_shs_school_secondchoice_others_addr", 
                m_sSchoolSecondChoice_others_addr);        

            arrParams[37] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 38, "psd_shs_track_secondchoice_cd",
                ((String)jcboSecondChoiceTrack.getSelectedItem()).substring(0, 3));           
            arrParams[38] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 39, "psd_shs_strspec_secondchoice_cd", 
                 ((String)jcboSecondChoiceStrandSpecs.getSelectedItem()).substring(0, 3));
        }
        else {
            arrParams[34] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 35, "psd_shs_school_secondchoice_cd", "");
            arrParams[35] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 36, "psd_shs_school_secondchoice_others_nm", "");  
            arrParams[36] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 37, "psd_shs_school_secondchoice_others_addr", "");        
            arrParams[37] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 38, "psd_shs_track_secondchoice_cd", "");           
            arrParams[38] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 39, "psd_shs_strspec_secondchoice_cd", "");           
        }

        arrParams[39] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 40, "psd_lrn_no", 
            jtxfLRN.getText());
        
        arrParams[40] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 41, "psd_flag", 0);
        arrParams[41] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 42, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_RegisterStudent", arrParams);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtgGender = new javax.swing.ButtonGroup();
        jbtgElemPeptPasser = new javax.swing.ButtonGroup();
        jbtgElemAeTestPasser = new javax.swing.ButtonGroup();
        jbtgJhsPeptPasser = new javax.swing.ButtonGroup();
        jbtgJhsAeTestPasser = new javax.swing.ButtonGroup();
        jpnlNameOfStudent1 = new javax.swing.JPanel();
        jlblLastname = new javax.swing.JLabel();
        jlblFirstname = new javax.swing.JLabel();
        jlblMiddlename = new javax.swing.JLabel();
        jtxfLastname = new javax.swing.JTextField();
        jtxfFirstname = new javax.swing.JTextField();
        jtxfMiddlename = new javax.swing.JTextField();
        jpnlSeniorHigh8 = new javax.swing.JPanel();
        jpnlFirstChoice = new javax.swing.JPanel();
        jlblFirstChoiceTrack = new javax.swing.JLabel();
        jlblFirstChoiceSchool = new javax.swing.JLabel();
        jcboFirstChoiceSchool = new javax.swing.JComboBox<>();
        jcboFirstChoiceTrack = new javax.swing.JComboBox<>();
        jcboFirstChoiceStrandSpecs = new javax.swing.JComboBox<>();
        jlblFirstChoiceStrandSpecs = new javax.swing.JLabel();
        jpnlSecondChoice = new javax.swing.JPanel();
        jlblSecondChoiceTrack = new javax.swing.JLabel();
        jlblSecondChoiceSchool = new javax.swing.JLabel();
        jcboSecondChoiceSchool = new javax.swing.JComboBox<>();
        jcboSecondChoiceTrack = new javax.swing.JComboBox<>();
        jlblSecondChoiceStrandSpecs = new javax.swing.JLabel();
        jcboSecondChoiceStrandSpecs = new javax.swing.JComboBox<>();
        jbtnCancel = new javax.swing.JButton();
        jbtnRegister = new javax.swing.JButton();
        jpnlGender2 = new javax.swing.JPanel();
        jrdoMale = new javax.swing.JRadioButton();
        jrdoFemale = new javax.swing.JRadioButton();
        jpnlDOB3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jcboMonth = new javax.swing.JComboBox<>();
        jcboDay = new javax.swing.JComboBox<>();
        jcboYear = new javax.swing.JComboBox<>();
        jpnlPlaceOfBirth4 = new javax.swing.JPanel();
        jtxfPlaceOfBirth = new javax.swing.JTextField();
        jpnlNationality5 = new javax.swing.JPanel();
        jtxfNationality = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxflElemSchoolName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxfElemSchoolAddress = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jcboElemMnthComp = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jcboElemRegion = new javax.swing.JComboBox<>();
        jcboElemYrComp = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jrdolElemPeptPasserNo = new javax.swing.JRadioButton();
        jrdolElemPeptPasserYes = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jrdoElemAeTestPasserNo = new javax.swing.JRadioButton();
        jrdoElemAeTestPasserYes = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jcboElemPeptCompMnt = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jcboElemAeTestCompMnt = new javax.swing.JComboBox<>();
        jcboElemPeptCompYr = new javax.swing.JComboBox<>();
        jcboElemAeTestCompYr = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jtxfJhsSchoolName = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jtxfJhsSchoolAddress = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jcboJhsMnthComp = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jcboJhsRegion = new javax.swing.JComboBox<>();
        jcboJhsYrComp = new javax.swing.JComboBox<>();
        jPanel9 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jrdolJhsPeptPasserNo = new javax.swing.JRadioButton();
        jrdolJhsPeptPasserYes = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        jrdoJhsAeTestPasserNo = new javax.swing.JRadioButton();
        jrdoJhsAeTestPasserYes = new javax.swing.JRadioButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jcboJhsPeptCompMnt = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jcboJhsAeTestCompMnt = new javax.swing.JComboBox<>();
        jcboJhsPeptCompYr = new javax.swing.JComboBox<>();
        jcboJhsAeTestCompYr = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        jtxfLRN = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        jpnlNameOfStudent1.setBorder(javax.swing.BorderFactory.createTitledBorder("1. Name of Student"));
        jpnlNameOfStudent1.setToolTipText("Print or type your full name in the following sequence: LAST, FIRST, MIDDLE");

        jlblLastname.setText("Lastname:");

        jlblFirstname.setText("Firstname:");

        jlblMiddlename.setText("Middlename:");

        javax.swing.GroupLayout jpnlNameOfStudent1Layout = new javax.swing.GroupLayout(jpnlNameOfStudent1);
        jpnlNameOfStudent1.setLayout(jpnlNameOfStudent1Layout);
        jpnlNameOfStudent1Layout.setHorizontalGroup(
            jpnlNameOfStudent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlNameOfStudent1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jpnlNameOfStudent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblMiddlename, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblFirstname, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblLastname, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlNameOfStudent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxfLastname, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(jtxfFirstname)
                    .addComponent(jtxfMiddlename, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jpnlNameOfStudent1Layout.setVerticalGroup(
            jpnlNameOfStudent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlNameOfStudent1Layout.createSequentialGroup()
                .addGroup(jpnlNameOfStudent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblLastname)
                    .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlNameOfStudent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblFirstname)
                    .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlNameOfStudent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblMiddlename)
                    .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jpnlSeniorHigh8.setBorder(javax.swing.BorderFactory.createTitledBorder("8. Senior High School (SHS) Applied For"));
        jpnlSeniorHigh8.setToolTipText("Choose from the list of schools offering SHS (up to two choices allowed). Do not indicate the same SHS  twice. Make sure that track (Academics, TVL, Sports, Arts and Design), strand and specialization choices are offered in the SHS indicated. Select NONE if you do not have other choices of SHS/track/strand/specialization");

        jpnlFirstChoice.setBorder(javax.swing.BorderFactory.createTitledBorder("First Choice"));

        jlblFirstChoiceTrack.setText("Track:");

        jlblFirstChoiceSchool.setText("School:");

        jcboFirstChoiceSchool.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Makati High School", "Makati Science High School", "Fort Bonifacio High School" }));
        jcboFirstChoiceSchool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboFirstChoiceSchoolActionPerformed(evt);
            }
        });

        jcboFirstChoiceTrack.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Academic", "TVL", "Music & Arts", "Sports" }));
        jcboFirstChoiceTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboFirstChoiceTrackActionPerformed(evt);
            }
        });

        jcboFirstChoiceStrandSpecs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STEM", "ABM", "HUMMS" }));

        jlblFirstChoiceStrandSpecs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblFirstChoiceStrandSpecs.setText("Specialization:");

        javax.swing.GroupLayout jpnlFirstChoiceLayout = new javax.swing.GroupLayout(jpnlFirstChoice);
        jpnlFirstChoice.setLayout(jpnlFirstChoiceLayout);
        jpnlFirstChoiceLayout.setHorizontalGroup(
            jpnlFirstChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlFirstChoiceLayout.createSequentialGroup()
                .addGroup(jpnlFirstChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblFirstChoiceTrack)
                    .addComponent(jlblFirstChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblFirstChoiceSchool))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlFirstChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboFirstChoiceTrack, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboFirstChoiceSchool, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboFirstChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnlFirstChoiceLayout.setVerticalGroup(
            jpnlFirstChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlFirstChoiceLayout.createSequentialGroup()
                .addGroup(jpnlFirstChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblFirstChoiceSchool)
                    .addComponent(jcboFirstChoiceSchool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlFirstChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblFirstChoiceTrack)
                    .addComponent(jcboFirstChoiceTrack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlFirstChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblFirstChoiceStrandSpecs)
                    .addComponent(jcboFirstChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jpnlSecondChoice.setBorder(javax.swing.BorderFactory.createTitledBorder("Second Choice"));
        jpnlSecondChoice.setPreferredSize(new java.awt.Dimension(396, 113));

        jlblSecondChoiceTrack.setText("Track:");

        jlblSecondChoiceSchool.setText("School:");

        jcboSecondChoiceSchool.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Makati High School", "Makati Science High School", "Fort Bonifacio High School" }));
        jcboSecondChoiceSchool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboSecondChoiceSchoolActionPerformed(evt);
            }
        });

        jcboSecondChoiceTrack.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Academic", "TVL", "Music & Arts", "Sports" }));
        jcboSecondChoiceTrack.setEnabled(false);
        jcboSecondChoiceTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboSecondChoiceTrackActionPerformed(evt);
            }
        });

        jlblSecondChoiceStrandSpecs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblSecondChoiceStrandSpecs.setText("Specialization:");

        jcboSecondChoiceStrandSpecs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STEM", "ABM", "HUMMS" }));
        jcboSecondChoiceStrandSpecs.setEnabled(false);

        javax.swing.GroupLayout jpnlSecondChoiceLayout = new javax.swing.GroupLayout(jpnlSecondChoice);
        jpnlSecondChoice.setLayout(jpnlSecondChoiceLayout);
        jpnlSecondChoiceLayout.setHorizontalGroup(
            jpnlSecondChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlSecondChoiceLayout.createSequentialGroup()
                .addGroup(jpnlSecondChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblSecondChoiceTrack)
                    .addComponent(jlblSecondChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSecondChoiceSchool))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlSecondChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboSecondChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboSecondChoiceTrack, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboSecondChoiceSchool, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        jpnlSecondChoiceLayout.setVerticalGroup(
            jpnlSecondChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlSecondChoiceLayout.createSequentialGroup()
                .addGroup(jpnlSecondChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblSecondChoiceSchool)
                    .addComponent(jcboSecondChoiceSchool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlSecondChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblSecondChoiceTrack)
                    .addComponent(jcboSecondChoiceTrack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnlSecondChoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblSecondChoiceStrandSpecs)
                    .addComponent(jcboSecondChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jpnlSeniorHigh8Layout = new javax.swing.GroupLayout(jpnlSeniorHigh8);
        jpnlSeniorHigh8.setLayout(jpnlSeniorHigh8Layout);
        jpnlSeniorHigh8Layout.setHorizontalGroup(
            jpnlSeniorHigh8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlSeniorHigh8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpnlFirstChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpnlSecondChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnlSeniorHigh8Layout.setVerticalGroup(
            jpnlSeniorHigh8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpnlFirstChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jpnlSecondChoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jbtnCancel.setMnemonic(KeyEvent.VK_C);
        jbtnCancel.setText("Cancel");
        jbtnCancel.setToolTipText("Click to cancel registration");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jbtnRegister.setMnemonic(KeyEvent.VK_R);
        jbtnRegister.setText("Register");
        jbtnRegister.setToolTipText("Click to register");
        jbtnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRegisterActionPerformed(evt);
            }
        });

        jpnlGender2.setBorder(javax.swing.BorderFactory.createTitledBorder("2. Gender"));

        jbtgGender.add(jrdoMale);
        jrdoMale.setText("M");

        jbtgGender.add(jrdoFemale);
        jrdoFemale.setText("F");

        javax.swing.GroupLayout jpnlGender2Layout = new javax.swing.GroupLayout(jpnlGender2);
        jpnlGender2.setLayout(jpnlGender2Layout);
        jpnlGender2Layout.setHorizontalGroup(
            jpnlGender2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlGender2Layout.createSequentialGroup()
                .addComponent(jrdoMale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jrdoFemale)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        jpnlGender2Layout.setVerticalGroup(
            jpnlGender2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlGender2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jrdoMale)
                .addComponent(jrdoFemale))
        );

        jpnlDOB3.setBorder(javax.swing.BorderFactory.createTitledBorder("3. Date of Birth (Month, Day, Year)"));

        jLabel4.setText("-");

        jcboMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        jcboDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jcboYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));

        javax.swing.GroupLayout jpnlDOB3Layout = new javax.swing.GroupLayout(jpnlDOB3);
        jpnlDOB3.setLayout(jpnlDOB3Layout);
        jpnlDOB3Layout.setHorizontalGroup(
            jpnlDOB3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlDOB3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcboMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboYear, 0, 80, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpnlDOB3Layout.setVerticalGroup(
            jpnlDOB3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlDOB3Layout.createSequentialGroup()
                .addGroup(jpnlDOB3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jcboMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jpnlPlaceOfBirth4.setBorder(javax.swing.BorderFactory.createTitledBorder("4. Place of Birth"));
        jpnlPlaceOfBirth4.setToolTipText("City/Town or Province");

        javax.swing.GroupLayout jpnlPlaceOfBirth4Layout = new javax.swing.GroupLayout(jpnlPlaceOfBirth4);
        jpnlPlaceOfBirth4.setLayout(jpnlPlaceOfBirth4Layout);
        jpnlPlaceOfBirth4Layout.setHorizontalGroup(
            jpnlPlaceOfBirth4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlPlaceOfBirth4Layout.createSequentialGroup()
                .addComponent(jtxfPlaceOfBirth)
                .addContainerGap())
        );
        jpnlPlaceOfBirth4Layout.setVerticalGroup(
            jpnlPlaceOfBirth4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtxfPlaceOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jpnlNationality5.setBorder(javax.swing.BorderFactory.createTitledBorder("5. Nationality"));
        jpnlNationality5.setMinimumSize(new java.awt.Dimension(114, 47));

        javax.swing.GroupLayout jpnlNationality5Layout = new javax.swing.GroupLayout(jpnlNationality5);
        jpnlNationality5.setLayout(jpnlNationality5Layout);
        jpnlNationality5Layout.setHorizontalGroup(
            jpnlNationality5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlNationality5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtxfNationality)
                .addContainerGap())
        );
        jpnlNationality5Layout.setVerticalGroup(
            jpnlNationality5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlNationality5Layout.createSequentialGroup()
                .addComponent(jtxfNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("6. Elementary School"));

        jLabel1.setText("Elementary  School Name (Do not abbreviate)");

        jLabel2.setText("Address (City / Town or Province)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jtxflElemSchoolName, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jtxfElemSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxfElemSchoolAddress, jtxflElemSchoolName});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxflElemSchoolName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfElemSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Mnth/Yr of Comp.");

        jcboElemMnthComp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));

        jLabel5.setText("Region");

        jcboElemRegion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NCR", "CAR Cordillera", "Region I Ilocos", "Region II Cagayan Valley", "Region III C.Luzon", "Region IV-A CALABARZON", "Region IV-B MIMAROPA", "Region V Bicol", "Region VI W.Visayas", "Region VII C.Visayas", "Region VIII E.Visayas", "Region IX Zamboanga", "Region X N.Mindanao", "Region XI Davao", "Region XII SOCCSKSARGEN", "Region XIII Caraga", "ARMM" }));
        jcboElemRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboElemRegionActionPerformed(evt);
            }
        });

        jcboElemYrComp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002", "2003", "2004" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jcboElemMnthComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboElemYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5)
                    .addComponent(jcboElemRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboElemMnthComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboElemYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboElemRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setText("PEPT for Elem. Level Passer?");

        jbtgElemPeptPasser.add(jrdolElemPeptPasserNo);
        jrdolElemPeptPasserNo.setText("No");
        jrdolElemPeptPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdolElemPeptPasserNoActionPerformed(evt);
            }
        });

        jbtgElemPeptPasser.add(jrdolElemPeptPasserYes);
        jrdolElemPeptPasserYes.setText("Yes");
        jrdolElemPeptPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdolElemPeptPasserYesActionPerformed(evt);
            }
        });

        jLabel7.setText("A&E Test for Elem. Level Passer?");

        jbtgElemAeTestPasser.add(jrdoElemAeTestPasserNo);
        jrdoElemAeTestPasserNo.setText("No");
        jrdoElemAeTestPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoElemAeTestPasserNoActionPerformed(evt);
            }
        });

        jbtgElemAeTestPasser.add(jrdoElemAeTestPasserYes);
        jrdoElemAeTestPasserYes.setText("Yes");
        jrdoElemAeTestPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoElemAeTestPasserYesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jrdolElemPeptPasserNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrdolElemPeptPasserYes))
                    .addComponent(jLabel7)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jrdoElemAeTestPasserNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrdoElemAeTestPasserYes)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jrdolElemPeptPasserNo, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jrdolElemPeptPasserYes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jrdoElemAeTestPasserNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jrdoElemAeTestPasserYes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel8.setText("Mnt/Yr Completion");

        jcboElemPeptCompMnt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboElemPeptCompMnt.setEnabled(false);

        jLabel9.setText("Mnt/Yr Completion");

        jcboElemAeTestCompMnt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboElemAeTestCompMnt.setEnabled(false);

        jcboElemPeptCompYr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002" }));
        jcboElemPeptCompYr.setEnabled(false);

        jcboElemAeTestCompYr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002" }));
        jcboElemAeTestCompYr.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jcboElemPeptCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboElemPeptCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jcboElemAeTestCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboElemAeTestCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboElemPeptCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboElemPeptCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboElemAeTestCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboElemAeTestCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("7. Junior High School (JHS)"));

        jPanel7.setPreferredSize(new java.awt.Dimension(267, 114));

        jLabel10.setText("JHS Name (Do not abbreviate)");

        jLabel11.setText("Address (City / Town or Province)");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jtxfJhsSchoolName, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jtxfJhsSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfJhsSchoolName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfJhsSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel12.setText("Mnth/Yr of Comp.");

        jcboJhsMnthComp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));

        jLabel13.setText("Region");

        jcboJhsRegion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NCR", "CAR Cordillera", "Region I Ilocos", "Region II Cagayan Valley", "Region III C.Luzon", "Region IV-A CALABARZON", "Region IV-B MIMAROPA", "Region V Bicol", "Region VI W.Visayas", "Region VII C.Visayas", "Region VIII E.Visayas", "Region IX Zamboanga", "Region X N.Mindanao", "Region XI Davao", "Region XII SOCCSKSARGEN", "Region XIII Caraga", "ARMM" }));

        jcboJhsYrComp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002", "2003", "2004" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jcboJhsMnthComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboJhsYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel13)
                    .addComponent(jcboJhsRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboJhsMnthComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboJhsYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboJhsRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel9.setPreferredSize(new java.awt.Dimension(201, 112));

        jLabel14.setText("PEPT for JHS Level Passer?");

        jbtgJhsPeptPasser.add(jrdolJhsPeptPasserNo);
        jrdolJhsPeptPasserNo.setText("No");
        jrdolJhsPeptPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdolJhsPeptPasserNoActionPerformed(evt);
            }
        });

        jbtgJhsPeptPasser.add(jrdolJhsPeptPasserYes);
        jrdolJhsPeptPasserYes.setText("Yes");
        jrdolJhsPeptPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdolJhsPeptPasserYesActionPerformed(evt);
            }
        });

        jLabel15.setText("A&E Test for JHS Level Passer?");

        jbtgJhsAeTestPasser.add(jrdoJhsAeTestPasserNo);
        jrdoJhsAeTestPasserNo.setText("No");
        jrdoJhsAeTestPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoJhsAeTestPasserNoActionPerformed(evt);
            }
        });

        jbtgJhsAeTestPasser.add(jrdoJhsAeTestPasserYes);
        jrdoJhsAeTestPasserYes.setText("Yes");
        jrdoJhsAeTestPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoJhsAeTestPasserYesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jrdolJhsPeptPasserNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrdolJhsPeptPasserYes))
                    .addComponent(jLabel15)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jrdoJhsAeTestPasserNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrdoJhsAeTestPasserYes)))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jrdolJhsPeptPasserNo, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jrdolJhsPeptPasserYes, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jrdoJhsAeTestPasserYes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jrdoJhsAeTestPasserNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setPreferredSize(new java.awt.Dimension(129, 108));

        jLabel16.setText("Mnt/Yr Completion");

        jcboJhsPeptCompMnt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboJhsPeptCompMnt.setEnabled(false);

        jLabel17.setText("Mnt/Yr Completion");

        jcboJhsAeTestCompMnt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboJhsAeTestCompMnt.setEnabled(false);

        jcboJhsPeptCompYr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002" }));
        jcboJhsPeptCompYr.setEnabled(false);

        jcboJhsAeTestCompYr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002" }));
        jcboJhsAeTestCompYr.setEnabled(false);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jcboJhsPeptCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboJhsPeptCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel17)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jcboJhsAeTestCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboJhsAeTestCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboJhsPeptCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboJhsPeptCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboJhsAeTestCompMnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboJhsAeTestCompYr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("5.a LRN"));

        jtxfLRN.setMaximumSize(new java.awt.Dimension(92, 24));
        jtxfLRN.setMinimumSize(new java.awt.Dimension(92, 24));
        jtxfLRN.setPreferredSize(new java.awt.Dimension(92, 24));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtxfLRN, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtxfLRN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jbtnRegister)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnCancel))
                            .addComponent(jpnlSeniorHigh8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jpnlNameOfStudent1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jpnlGender2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jpnlDOB3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jpnlPlaceOfBirth4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jpnlNationality5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnCancel, jbtnRegister});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jpnlNameOfStudent1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(9, 9, 9))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jpnlDOB3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpnlGender2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpnlNationality5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpnlPlaceOfBirth4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpnlSeniorHigh8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancel)
                    .addComponent(jbtnRegister))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jbtnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRegisterActionPerformed
        if(ValidateFields()) {
            if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm your registration",
                this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SPParam[] outParams = RegisterStudent();
                if (outParams != null) {
                    Integer nOut = new Integer(outParams[0].getParamValue().toString());
                    if(nOut != 0) {
                        String sErrMsg =  outParams[1].getParamValue().toString();
                        JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Registration Successfull!. This will exit the registration.");
                        this.dispose();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbtnRegisterActionPerformed

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Are you sure to cancel the registration?",
            this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_jbtnCancelActionPerformed

    private void jrdolElemPeptPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdolElemPeptPasserNoActionPerformed
        jcboElemPeptCompMnt.setEnabled(false);
        jcboElemPeptCompYr.setEnabled(false);
    }//GEN-LAST:event_jrdolElemPeptPasserNoActionPerformed

    private void jrdolElemPeptPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdolElemPeptPasserYesActionPerformed
        jcboElemPeptCompMnt.setEnabled(true);
        jcboElemPeptCompYr.setEnabled(true);
    }//GEN-LAST:event_jrdolElemPeptPasserYesActionPerformed

    private void jrdoElemAeTestPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoElemAeTestPasserNoActionPerformed
        jcboElemAeTestCompMnt.setEnabled(false);
        jcboElemAeTestCompYr.setEnabled(false);
    }//GEN-LAST:event_jrdoElemAeTestPasserNoActionPerformed

    private void jrdoElemAeTestPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoElemAeTestPasserYesActionPerformed
        jcboElemAeTestCompMnt.setEnabled(true);
        jcboElemAeTestCompYr.setEnabled(true);
    }//GEN-LAST:event_jrdoElemAeTestPasserYesActionPerformed

    private void jrdolJhsPeptPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdolJhsPeptPasserNoActionPerformed
        jcboJhsPeptCompMnt.setEnabled(false);
        jcboJhsPeptCompYr.setEnabled(false);
    }//GEN-LAST:event_jrdolJhsPeptPasserNoActionPerformed

    private void jrdolJhsPeptPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdolJhsPeptPasserYesActionPerformed
        jcboJhsPeptCompMnt.setEnabled(true);
        jcboJhsPeptCompYr.setEnabled(true);
    }//GEN-LAST:event_jrdolJhsPeptPasserYesActionPerformed

    private void jrdoJhsAeTestPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoJhsAeTestPasserNoActionPerformed
       jcboJhsAeTestCompMnt.setEnabled(false);
       jcboJhsAeTestCompYr.setEnabled(false);
    }//GEN-LAST:event_jrdoJhsAeTestPasserNoActionPerformed

    private void jrdoJhsAeTestPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoJhsAeTestPasserYesActionPerformed
       jcboJhsAeTestCompMnt.setEnabled(true);
       jcboJhsAeTestCompYr.setEnabled(true);
    }//GEN-LAST:event_jrdoJhsAeTestPasserYesActionPerformed

    private void jcboFirstChoiceSchoolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboFirstChoiceSchoolActionPerformed
        if (m_bFlagFirstChoiceSchool == false) return;
        if(jcboFirstChoiceTrack != null || jcboFirstChoiceTrack.getItemCount() > 0) {
            jcboFirstChoiceTrack.removeAllItems();
            LoadFirstChoiceTracks();
        }
        if("999 - Other Schools".equalsIgnoreCase((String)jcboFirstChoiceSchool.getSelectedItem())) {
           JOtherSchools dlg = new JOtherSchools(this, this, jcboFirstChoiceSchool, "FirstChoice", 
                m_sSchoolFirstChoice_others_nm, m_sSchoolFirstChoice_others_addr, true);
           dlg.setVisible(true);
        }
    }//GEN-LAST:event_jcboFirstChoiceSchoolActionPerformed

    private void jcboFirstChoiceTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboFirstChoiceTrackActionPerformed
        if (m_bFlagFirstChoiceTrack == false) return;
        if(jcboFirstChoiceTrack != null || jcboFirstChoiceTrack.getItemCount() > 0) {
            jcboFirstChoiceStrandSpecs.removeAllItems();
            LoadFirstChoiceStrandAndSpecs();
        }
    }//GEN-LAST:event_jcboFirstChoiceTrackActionPerformed

    private void jcboSecondChoiceSchoolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboSecondChoiceSchoolActionPerformed
        String sSchCd = (String)jcboSecondChoiceSchool.getSelectedItem();
        if(sSchCd != null && !sSchCd.isEmpty()) {
            sSchCd = sSchCd.substring(0, 3);
            if("999".equalsIgnoreCase(sSchCd)) {
               JOtherSchools dlg = new JOtherSchools(this, this, jcboSecondChoiceSchool, "SecondChoice", 
                    m_sSchoolSecondChoice_others_nm, m_sSchoolSecondChoice_others_addr, true);
               dlg.setVisible(true);
            }

            if("---".equalsIgnoreCase(sSchCd)) {
                jcboSecondChoiceTrack.setEnabled(false);
                jcboSecondChoiceTrack.removeAllItems();
                jcboSecondChoiceStrandSpecs.setEnabled(false);
            }
            else {
                if(jcboSecondChoiceTrack != null || jcboSecondChoiceTrack.getItemCount() > 0) {
                    jcboSecondChoiceTrack.setEnabled(true);
                    jcboSecondChoiceTrack.removeAllItems();
                    jcboSecondChoiceStrandSpecs.setEnabled(true);
                    LoadSecondChoiceTracks();
                }
            }
        }
    }//GEN-LAST:event_jcboSecondChoiceSchoolActionPerformed

    private void jcboSecondChoiceTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboSecondChoiceTrackActionPerformed
        if(jcboSecondChoiceTrack != null || jcboSecondChoiceTrack.getItemCount() > 0) {
            jcboSecondChoiceStrandSpecs.removeAllItems();
            LoadSecondChoiceStrandAndSpecs();
        }
    }//GEN-LAST:event_jcboSecondChoiceTrackActionPerformed

    private void jcboElemRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboElemRegionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcboElemRegionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JdlgRegStudentNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JdlgRegStudentNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JdlgRegStudentNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JdlgRegStudentNew.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JdlgRegStudentNew dialog = new JdlgRegStudentNew(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.ButtonGroup jbtgElemAeTestPasser;
    private javax.swing.ButtonGroup jbtgElemPeptPasser;
    private javax.swing.ButtonGroup jbtgGender;
    private javax.swing.ButtonGroup jbtgJhsAeTestPasser;
    private javax.swing.ButtonGroup jbtgJhsPeptPasser;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnRegister;
    private javax.swing.JComboBox<String> jcboDay;
    private javax.swing.JComboBox<String> jcboElemAeTestCompMnt;
    private javax.swing.JComboBox<String> jcboElemAeTestCompYr;
    private javax.swing.JComboBox<String> jcboElemMnthComp;
    private javax.swing.JComboBox<String> jcboElemPeptCompMnt;
    private javax.swing.JComboBox<String> jcboElemPeptCompYr;
    private javax.swing.JComboBox<String> jcboElemRegion;
    private javax.swing.JComboBox<String> jcboElemYrComp;
    private javax.swing.JComboBox<String> jcboFirstChoiceSchool;
    private javax.swing.JComboBox<String> jcboFirstChoiceStrandSpecs;
    private javax.swing.JComboBox<String> jcboFirstChoiceTrack;
    private javax.swing.JComboBox<String> jcboJhsAeTestCompMnt;
    private javax.swing.JComboBox<String> jcboJhsAeTestCompYr;
    private javax.swing.JComboBox<String> jcboJhsMnthComp;
    private javax.swing.JComboBox<String> jcboJhsPeptCompMnt;
    private javax.swing.JComboBox<String> jcboJhsPeptCompYr;
    private javax.swing.JComboBox<String> jcboJhsRegion;
    private javax.swing.JComboBox<String> jcboJhsYrComp;
    private javax.swing.JComboBox<String> jcboMonth;
    private javax.swing.JComboBox<String> jcboSecondChoiceSchool;
    private javax.swing.JComboBox<String> jcboSecondChoiceStrandSpecs;
    private javax.swing.JComboBox<String> jcboSecondChoiceTrack;
    private javax.swing.JComboBox<String> jcboYear;
    private javax.swing.JLabel jlblFirstChoiceSchool;
    private javax.swing.JLabel jlblFirstChoiceStrandSpecs;
    private javax.swing.JLabel jlblFirstChoiceTrack;
    private javax.swing.JLabel jlblFirstname;
    private javax.swing.JLabel jlblLastname;
    private javax.swing.JLabel jlblMiddlename;
    private javax.swing.JLabel jlblSecondChoiceSchool;
    private javax.swing.JLabel jlblSecondChoiceStrandSpecs;
    private javax.swing.JLabel jlblSecondChoiceTrack;
    private javax.swing.JPanel jpnlDOB3;
    private javax.swing.JPanel jpnlFirstChoice;
    private javax.swing.JPanel jpnlGender2;
    private javax.swing.JPanel jpnlNameOfStudent1;
    private javax.swing.JPanel jpnlNationality5;
    private javax.swing.JPanel jpnlPlaceOfBirth4;
    private javax.swing.JPanel jpnlSecondChoice;
    private javax.swing.JPanel jpnlSeniorHigh8;
    private javax.swing.JRadioButton jrdoElemAeTestPasserNo;
    private javax.swing.JRadioButton jrdoElemAeTestPasserYes;
    private javax.swing.JRadioButton jrdoFemale;
    private javax.swing.JRadioButton jrdoJhsAeTestPasserNo;
    private javax.swing.JRadioButton jrdoJhsAeTestPasserYes;
    private javax.swing.JRadioButton jrdoMale;
    private javax.swing.JRadioButton jrdolElemPeptPasserNo;
    private javax.swing.JRadioButton jrdolElemPeptPasserYes;
    private javax.swing.JRadioButton jrdolJhsPeptPasserNo;
    private javax.swing.JRadioButton jrdolJhsPeptPasserYes;
    private javax.swing.JTextField jtxfElemSchoolAddress;
    private javax.swing.JTextField jtxfFirstname;
    private javax.swing.JTextField jtxfJhsSchoolAddress;
    private javax.swing.JTextField jtxfJhsSchoolName;
    private javax.swing.JTextField jtxfLRN;
    private javax.swing.JTextField jtxfLastname;
    private javax.swing.JTextField jtxfMiddlename;
    private javax.swing.JTextField jtxfNationality;
    private javax.swing.JTextField jtxfPlaceOfBirth;
    private javax.swing.JTextField jtxflElemSchoolName;
    // End of variables declaration//GEN-END:variables

}
