/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.ui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;

import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
import shrs.utility.SysDefaults;
import shrs.utility.FieldValidator;

/**
 *
 * @author osraldo
 */
public class JUpdateStudent extends javax.swing.JDialog {
    private int m_nPrimaryKey;
    /**
     * Creates new form JUpdateStudent
     */
    public JUpdateStudent(javax.swing.JDialog parent, boolean modal, int nPrimaryKey) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        this.setTitle("Update Student Record");
        m_nPrimaryKey = nPrimaryKey;
        InitializeCboItems();
        LoadData(nPrimaryKey);
    }
    
    private void InitializeCboItems() {
        jcboJHSRegion.removeAllItems();
        jcboElemRegion.removeAllItems();
        jcboDOBYear.removeAllItems();
        jcboJHSPeptPasserYear.removeAllItems();
        jcboJHSAEPasserYear.removeAllItems();
        jcboJHSGradYear.removeAllItems();
        jcboElemPeptPasserYear.removeAllItems();
        jcboElemAEPasserYear.removeAllItems();
        jcboElemGradYear.removeAllItems();
        
        DBManager dbm = new DBManager();
        try {
            ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", "cf_major = 'RGN'", "cf_minor asc");
            String sRegion;
            while(rs.next()) {
                sRegion = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                jcboJHSRegion.addItem(sRegion);
                jcboElemRegion.addItem(sRegion);
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        
        Calendar cal = Calendar.getInstance();
        for(int ii = cal.get(Calendar.YEAR); ii >= SysDefaults.getMinimumCboYearValue(); ii--) {
            jcboDOBYear.addItem(String.valueOf(ii));
            jcboJHSPeptPasserYear.addItem(String.valueOf(ii));
            jcboJHSAEPasserYear.addItem(String.valueOf(ii));
            jcboJHSGradYear.addItem(String.valueOf(ii));
            jcboElemPeptPasserYear.addItem(String.valueOf(ii));
            jcboElemAEPasserYear.addItem(String.valueOf(ii));
            jcboElemGradYear.addItem(String.valueOf(ii));
        }
    }
    
    private void LoadData(int nPrimaryKey) {
        DBManager dbm = new DBManager();
        ResultSet rsSD  = null;
        int nRowCnt = 0;
        String sTbl = "students as sd join configs as jr on sd.sd_jhs_region_cd = jr.cf_minor and ";
        sTbl += "jr.cf_major = 'RGN' join configs as er on sd.sd_elem_region_cd = er.cf_minor and er.cf_major = 'RGN'";
        String sCols = "sd_lrn_no,sd_lastname,sd_firstname,sd_middlename,";
        sCols += "sd_gender,sd_dob,sd_nationality,sd_birthplace,sd_elem_name,";
        sCols += "sd_elem_school_addr,sd_elem_comp_month,sd_elem_comp_year,";
        sCols += "concat(er.cf_minor, ' - ', er.cf_name) as elem_region,";
        sCols += "sd_elem_pept_passer,sd_elem_pept_month,sd_elem_pept_year,sd_elem_ae_passer,sd_elem_ae_month,";
        sCols += "sd_elem_ae_year,sd_jhs_name,sd_jhs_addr,sd_jhs_comp_month,";
        sCols += "sd_jhs_comp_year,concat(jr.cf_minor, ' - ', jr.cf_name) as jhs_region,";
        sCols += "sd_jhs_pept_passer,sd_jhs_pept_month,sd_jhs_pept_year,sd_jhs_ae_passer,";
        sCols += "sd_jhs_ae_month,sd_jhs_ae_year";

        String sCond = "sd_primary_key = " + nPrimaryKey;

        try {
            rsSD = dbm.getResultSet(sTbl, sCols, sCond, "");
            rsSD.last();
            nRowCnt = rsSD.getRow();
            rsSD.beforeFirst();
            if (nRowCnt > 0) {
                rsSD.next();
                jtxfLastname.setText(rsSD.getString("sd_lastname"));
                jtxfFirstname.setText(rsSD.getString("sd_firstname"));
                jtxfMiddlename.setText(rsSD.getString("sd_middlename"));
                jtxfLRN.setText(rsSD.getString("sd_lrn_no"));
                jtxfNationality.setText(rsSD.getString("sd_nationality"));
                jtxfPlaceOfBirth.setText(rsSD.getString("sd_birthplace"));
                if(rsSD.getString("sd_gender").equalsIgnoreCase("M"))
                    jrdoMale.setSelected(true);
                else
                    jrdoFemale.setSelected(true);
                String sDOB = rsSD.getString("sd_dob");
                jcboDOBYear.setSelectedItem(sDOB.substring(0, 4));
                jcboDOBMonth.setSelectedItem(sDOB.substring(5, 7));
                jcboDOBDay.setSelectedItem(sDOB.substring(8));
                
                jtxfJHSSchoolName.setText(rsSD.getString("sd_jhs_name"));
                jtxfJHSSchoolAddress.setText(rsSD.getString("sd_jhs_addr"));
                jcboJHSRegion.setSelectedItem(rsSD.getString("jhs_region"));
                jcboJHSGradMonth.setSelectedItem(rsSD.getString("sd_jhs_comp_month"));
                jcboJHSGradYear.setSelectedItem(rsSD.getString("sd_jhs_comp_year"));
                if(rsSD.getString("sd_jhs_pept_passer").equalsIgnoreCase("Y")) {
                    jrdoJHSPeptPasserYes.setSelected(true);
                    jcboJHSPeptPasserMonth.setEnabled(true);
                    jcboJHSPeptPasserMonth.setSelectedItem(rsSD.getString("sd_jhs_pept_month"));
                    jcboJHSPeptPasserYear.setEnabled(true);
                    jcboJHSPeptPasserYear.setSelectedItem(rsSD.getString("sd_jhs_pept_year"));
                }
                else
                    jrdoJHSPeptPasserNo.setSelected(true);
                if(rsSD.getString("sd_jhs_ae_passer").equalsIgnoreCase("Y")) {
                    jrdoJHSAEPasserYes.setSelected(true);
                    jcboJHSAEPasserMonth.setEnabled(true);
                    jcboJHSAEPasserMonth.setSelectedItem(rsSD.getString("sd_jhs_ae_month"));
                    jcboJHSAEPasserYear.setEnabled(true);
                    jcboJHSAEPasserYear.setSelectedItem(rsSD.getString("sd_jhs_ae_year"));
                }
                else
                    jrdoJHSAEPasserNo.setSelected(true);

                jtxfElemSchoolName.setText(rsSD.getString("sd_elem_name"));
                jtxfElemSchoolAddress.setText(rsSD.getString("sd_elem_school_addr"));
                jcboElemRegion.setSelectedItem(rsSD.getString("elem_region"));
                jcboElemGradMonth.setSelectedItem(rsSD.getString("sd_elem_comp_month"));
                jcboElemGradYear.setSelectedItem(rsSD.getString("sd_elem_comp_year"));
                if(rsSD.getString("sd_elem_pept_passer").equalsIgnoreCase("Y")) {
                    jrdoElemPeptPasserYes.setSelected(true);
                    jcboElemPeptPasserMonth.setEnabled(true);
                    jcboElemPeptPasserMonth.setSelectedItem(rsSD.getString("sd_elem_pept_month"));
                    jcboElemPeptPasserYear.setEnabled(true);
                    jcboElemPeptPasserYear.setSelectedItem(rsSD.getString("sd_elem_pept_year"));
                }
                else
                    jrdoElemPeptPasserNo.setSelected(true);
                if(rsSD.getString("sd_elem_ae_passer").equalsIgnoreCase("Y")) {
                    jrdoElemAEPasserYes.setSelected(true);
                    jcboElemAEPasserMonth.setEnabled(true);
                    jcboElemAEPasserMonth.setSelectedItem(rsSD.getString("sd_elem_ae_month"));
                    jcboElemAEPasserYear.setEnabled(true);
                    jcboElemAEPasserYear.setSelectedItem(rsSD.getString("sd_elem_ae_year"));
                }
                else
                    jrdoElemAEPasserNo.setSelected(true);
            }
        } 
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        finally	{
            if (rsSD != null) {
                try {
                    rsSD.close();
                } 
                catch(SQLException sqlEx){}//ignore
                rsSD = null;
            }
        }
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
        
        String sDateOfBirth = (String)jcboDOBMonth.getSelectedItem();
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboDOBDay.getSelectedItem());
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboDOBYear.getSelectedItem());
        try {
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            df.setLenient(false);
            df.parse(sDateOfBirth);
        }
        catch(ParseException pex) {
            JOptionPane.showMessageDialog(this, "[" + sDateOfBirth + "] Date is not valid", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jcboDOBDay.grabFocus();
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

        fv = new FieldValidator(jtxfJHSSchoolName.getText(), "Name of Junior High School");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfJHSSchoolName.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfJHSSchoolAddress.getText(), "Junior High School Address");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfJHSSchoolAddress.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfElemSchoolName.getText(), "Name of Elementary School");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfElemSchoolName.grabFocus();
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
        
        return true;
    }
    
    private SPParam[] UpdateStudentRecord() {
        SPParam[] arrParams = new SPParam[33];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
                m_nPrimaryKey);
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psd_lrn_no", 
            jtxfLRN.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psd_lastname", 
            jtxfLastname.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psd_firstname", 
            jtxfFirstname.getText());
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psd_middlename", 
            jtxfMiddlename.getText());
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "psd_gender", 
            jrdoMale.isSelected() ? "M" : "F");
        String sDateOfBirth = (String)jcboDOBYear.getSelectedItem();
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboDOBMonth.getSelectedItem());
        sDateOfBirth = sDateOfBirth.concat("-");
        sDateOfBirth = sDateOfBirth.concat((String)jcboDOBDay.getSelectedItem());
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psd_dob", sDateOfBirth);
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psd_nationality", 
            jtxfNationality.getText());	
        arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "psd_birthplace", 
            jtxfPlaceOfBirth.getText());
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 10, "psd_elem_name", 
            jtxfElemSchoolName.getText());
        arrParams[10] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 11, "psd_elem_school_addr", 
            jtxfElemSchoolAddress.getText());
        arrParams[11] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 12, "psd_elem_comp_month", 
            (String)jcboElemGradMonth.getSelectedItem());
        arrParams[12] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 13, "psd_elem_comp_year", 
            (String)jcboElemGradYear.getSelectedItem());
        arrParams[13] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 14, "psd_elem_region", 
            ((String)jcboElemRegion.getSelectedItem()).substring(0, 3));
        arrParams[14] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 15, "psd_elem_pept_passer", 
            jrdoElemPeptPasserNo.isSelected() ? "N" : "Y");
        arrParams[15] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 16, "psd_elem_pept_month", 
            jrdoElemPeptPasserNo.isSelected() ? "" : (String)jcboElemPeptPasserMonth.getSelectedItem());
        arrParams[16] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 17, "psd_elem_pept_year", 
            jrdoElemPeptPasserNo.isSelected() ? "" : (String)jcboElemPeptPasserYear.getSelectedItem());
        arrParams[17] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 18, "psd_elem_ae_passer", 
            jrdoElemAEPasserNo.isSelected() ? "N" : "Y");
        arrParams[18] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 19, "psd_elem_ae_month", 
            jrdoElemAEPasserNo.isSelected() ? "" : (String)jcboElemAEPasserMonth.getSelectedItem());
        arrParams[19] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 20, "psd_elem_ae_year", 
            jrdoElemAEPasserNo.isSelected() ? "" : (String)jcboElemAEPasserYear.getSelectedItem());
        arrParams[20] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 21, "psd_jhs_name", 
            jtxfJHSSchoolName.getText());
        arrParams[21] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 22, "psd_jhs_school_addr", 
            jtxfJHSSchoolAddress.getText());
        arrParams[22] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 23, "psd_jhs_comp_month", 
            (String)jcboJHSGradMonth.getSelectedItem());
        arrParams[23] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 24, "psd_jhs_comp_year", 
            (String)jcboJHSGradYear.getSelectedItem());
        arrParams[24] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 25, "psd_jhs_region", 
            ((String)jcboJHSRegion.getSelectedItem()).substring(0, 3));
        arrParams[25] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 26, "psd_jhs_pept_passer", 
            jrdoJHSPeptPasserNo.isSelected() ? "N" : "Y");
        arrParams[26] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 27, "psd_jhs_pept_month", 
            jrdoJHSPeptPasserNo.isSelected() ? "" : (String)jcboJHSPeptPasserMonth.getSelectedItem());
        arrParams[27] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 28, "psd_jhs_pept_year", 
            jrdoJHSPeptPasserNo.isSelected() ? "" : (String)jcboJHSPeptPasserYear.getSelectedItem());
        arrParams[28] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 29, "psd_jhs_ae_passer", 
            jrdoJHSAEPasserNo.isSelected() ? "N" : "Y");
        arrParams[29] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 30, "psd_jhs_ae_month", 
            jrdoJHSAEPasserNo.isSelected() ? "" : (String)jcboJHSAEPasserMonth.getSelectedItem());
        arrParams[30] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 31, "psd_jhs_ae_year", 
            jrdoJHSAEPasserNo.isSelected() ? "" : (String)jcboJHSAEPasserYear.getSelectedItem());

        arrParams[31] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 32, "psd_flag", 0);
        arrParams[32] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 33, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_UpdateStudentRecord", arrParams);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jbtgGender = new javax.swing.ButtonGroup();
        jbtgJHSPept = new javax.swing.ButtonGroup();
        jbtgJHSAE = new javax.swing.ButtonGroup();
        jbtgElemPept = new javax.swing.ButtonGroup();
        jbtgElemAE = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxfLastname = new javax.swing.JTextField();
        jtxfFirstname = new javax.swing.JTextField();
        jtxfMiddlename = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jrdoMale = new javax.swing.JRadioButton();
        jrdoFemale = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtxfLRN = new javax.swing.JTextField();
        jtxfNationality = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxfPlaceOfBirth = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jcboDOBMonth = new javax.swing.JComboBox<>();
        jcboDOBDay = new javax.swing.JComboBox<>();
        jcboDOBYear = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jtxfJHSSchoolName = new javax.swing.JTextField();
        jtxfJHSSchoolAddress = new javax.swing.JTextField();
        jcboJHSRegion = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jcboJHSGradMonth = new javax.swing.JComboBox<>();
        jcboJHSGradYear = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jrdoJHSPeptPasserNo = new javax.swing.JRadioButton();
        jrdoJHSPeptPasserYes = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jcboJHSPeptPasserMonth = new javax.swing.JComboBox<>();
        jcboJHSPeptPasserYear = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jrdoJHSAEPasserNo = new javax.swing.JRadioButton();
        jrdoJHSAEPasserYes = new javax.swing.JRadioButton();
        jLabel14 = new javax.swing.JLabel();
        jcboJHSAEPasserMonth = new javax.swing.JComboBox<>();
        jcboJHSAEPasserYear = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jtxfElemSchoolName = new javax.swing.JTextField();
        jtxfElemSchoolAddress = new javax.swing.JTextField();
        jcboElemRegion = new javax.swing.JComboBox<>();
        jcboElemGradMonth = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jrdoElemPeptPasserNo = new javax.swing.JRadioButton();
        jrdoElemPeptPasserYes = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        jcboElemPeptPasserMonth = new javax.swing.JComboBox<>();
        jcboElemPeptPasserYear = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jrdoElemAEPasserNo = new javax.swing.JRadioButton();
        jrdoElemAEPasserYes = new javax.swing.JRadioButton();
        jLabel20 = new javax.swing.JLabel();
        jcboElemAEPasserMonth = new javax.swing.JComboBox<>();
        jcboElemAEPasserYear = new javax.swing.JComboBox<>();
        jcboElemGradYear = new javax.swing.JComboBox<>();
        jbtnCancel = new javax.swing.JButton();
        jbtnUpdate = new javax.swing.JButton();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Student Information"));

        jLabel1.setText("Lastname:");

        jLabel2.setText("Firstname:");

        jLabel4.setText("Middlename:");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Gender"));
        jPanel2.setToolTipText("");

        jbtgGender.add(jrdoMale);
        jrdoMale.setText("Male");

        jbtgGender.add(jrdoFemale);
        jrdoFemale.setText("Female");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jrdoMale)
                    .addComponent(jrdoFemale))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jrdoMale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jrdoFemale)
                .addContainerGap())
        );

        jLabel5.setText("LRN:");

        jLabel6.setText("Nationality:");

        jLabel7.setText("Place of Birth:");

        jLabel8.setText("DOB(mm-dd-yyyy):");

        jcboDOBMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        jcboDOBDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jcboDOBYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jtxfLRN)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jcboDOBMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcboDOBDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcboDOBYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jtxfNationality))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxfPlaceOfBirth))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxfFirstname, jtxfLastname, jtxfMiddlename});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jtxfLRN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jtxfNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jcboDOBMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcboDOBDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcboDOBYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jtxfPlaceOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Junior High School"));

        jLabel9.setText("School Name:");

        jLabel10.setText("School Address:");

        jLabel11.setText("Region:");

        jcboJHSRegion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NCR", "CAR Cordillera", "Region I Ilocos", "Region II Cagayan Valley", "Region III C.Luzon", "Region IV-A CALABARZON", "Region IV-B MIMAROPA", "Region V Bicol", "Region VI W.Visayas", "Region VII C.Visayas", "Region VIII E.Visayas", "Region IX Zamboanga", "Region X N.Mindanao", "Region XI Davao", "Region XII SOCCSKSARGEN", "Region XIII Caraga", "ARMM" }));

        jLabel12.setText("Grad. (Mnth/Yr):");

        jcboJHSGradMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));

        jcboJHSGradYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("PEPT Passer"));

        jbtgJHSPept.add(jrdoJHSPeptPasserNo);
        jrdoJHSPeptPasserNo.setText("No");
        jrdoJHSPeptPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoJHSPeptPasserNoActionPerformed(evt);
            }
        });

        jbtgJHSPept.add(jrdoJHSPeptPasserYes);
        jrdoJHSPeptPasserYes.setText("Yes");
        jrdoJHSPeptPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoJHSPeptPasserYesActionPerformed(evt);
            }
        });

        jLabel13.setText("Comp(Mn/Yr):");

        jcboJHSPeptPasserMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboJHSPeptPasserMonth.setEnabled(false);

        jcboJHSPeptPasserYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));
        jcboJHSPeptPasserYear.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jrdoJHSPeptPasserNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrdoJHSPeptPasserYes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboJHSPeptPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboJHSPeptPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jrdoJHSPeptPasserNo)
                .addComponent(jrdoJHSPeptPasserYes)
                .addComponent(jLabel13)
                .addComponent(jcboJHSPeptPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jcboJHSPeptPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("A&E Test Passer"));

        jbtgJHSAE.add(jrdoJHSAEPasserNo);
        jrdoJHSAEPasserNo.setText("No");
        jrdoJHSAEPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoJHSAEPasserNoActionPerformed(evt);
            }
        });

        jbtgJHSAE.add(jrdoJHSAEPasserYes);
        jrdoJHSAEPasserYes.setText("Yes");
        jrdoJHSAEPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoJHSAEPasserYesActionPerformed(evt);
            }
        });

        jLabel14.setText("Comp(Mn/Yr):");

        jcboJHSAEPasserMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboJHSAEPasserMonth.setEnabled(false);

        jcboJHSAEPasserYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));
        jcboJHSAEPasserYear.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jrdoJHSAEPasserNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrdoJHSAEPasserYes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboJHSAEPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboJHSAEPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrdoJHSAEPasserNo)
                    .addComponent(jrdoJHSAEPasserYes)
                    .addComponent(jLabel14)
                    .addComponent(jcboJHSAEPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboJHSAEPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jcboJHSGradMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboJHSGradYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jtxfJHSSchoolAddress)
                    .addComponent(jtxfJHSSchoolName, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jcboJHSRegion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jtxfJHSSchoolName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jtxfJHSSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboJHSRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboJHSGradMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jcboJHSGradYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Elementary"));

        jLabel15.setText("School Name:");

        jLabel16.setText("School Address:");

        jLabel17.setText("Region:");

        jLabel18.setText("Grad. (Mnth/Yr):");

        jcboElemRegion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NCR", "CAR Cordillera", "Region I Ilocos", "Region II Cagayan Valley", "Region III C.Luzon", "Region IV-A CALABARZON", "Region IV-B MIMAROPA", "Region V Bicol", "Region VI W.Visayas", "Region VII C.Visayas", "Region VIII E.Visayas", "Region IX Zamboanga", "Region X N.Mindanao", "Region XI Davao", "Region XII SOCCSKSARGEN", "Region XIII Caraga", "ARMM" }));

        jcboElemGradMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("PEPT Passer"));

        jbtgElemPept.add(jrdoElemPeptPasserNo);
        jrdoElemPeptPasserNo.setText("No");
        jrdoElemPeptPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoElemPeptPasserNoActionPerformed(evt);
            }
        });

        jbtgElemPept.add(jrdoElemPeptPasserYes);
        jrdoElemPeptPasserYes.setText("Yes");
        jrdoElemPeptPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoElemPeptPasserYesActionPerformed(evt);
            }
        });

        jLabel19.setText("Comp(Mn/Yr):");

        jcboElemPeptPasserMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboElemPeptPasserMonth.setEnabled(false);

        jcboElemPeptPasserYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));
        jcboElemPeptPasserYear.setEnabled(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jrdoElemPeptPasserNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrdoElemPeptPasserYes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboElemPeptPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboElemPeptPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrdoElemPeptPasserNo)
                    .addComponent(jrdoElemPeptPasserYes)
                    .addComponent(jLabel19)
                    .addComponent(jcboElemPeptPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboElemPeptPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("A&E Test Passer"));

        jbtgElemAE.add(jrdoElemAEPasserNo);
        jrdoElemAEPasserNo.setText("No");
        jrdoElemAEPasserNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoElemAEPasserNoActionPerformed(evt);
            }
        });

        jbtgElemAE.add(jrdoElemAEPasserYes);
        jrdoElemAEPasserYes.setText("Yes");
        jrdoElemAEPasserYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoElemAEPasserYesActionPerformed(evt);
            }
        });

        jLabel20.setText("Comp(Mn/Yr):");

        jcboElemAEPasserMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jcboElemAEPasserMonth.setEnabled(false);

        jcboElemAEPasserYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));
        jcboElemAEPasserYear.setEnabled(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jrdoElemAEPasserNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrdoElemAEPasserYes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboElemAEPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboElemAEPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrdoElemAEPasserNo)
                    .addComponent(jrdoElemAEPasserYes)
                    .addComponent(jLabel20)
                    .addComponent(jcboElemAEPasserMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboElemAEPasserYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jcboElemGradYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001" }));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jtxfElemSchoolAddress, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jcboElemGradMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboElemGradYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jcboElemRegion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxfElemSchoolName, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jtxfElemSchoolName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jtxfElemSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jcboElemRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jcboElemGradMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcboElemGradYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 12, Short.MAX_VALUE))))
        );

        jbtnCancel.setMnemonic(KeyEvent.VK_C);
        jbtnCancel.setText("Cancel");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jbtnUpdate.setMnemonic(KeyEvent.VK_U);
        jbtnUpdate.setText("Update");
        jbtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnCancel)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnCancel, jbtnUpdate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancel)
                    .addComponent(jbtnUpdate))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpdateActionPerformed
        if(ValidateFields()) {
            if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm your update",
                this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SPParam[] outParams = UpdateStudentRecord();
                if (outParams != null) {
                    Integer nOut = new Integer(outParams[0].getParamValue().toString());
                    if(nOut != 0) {
                        String sErrMsg =  outParams[1].getParamValue().toString();
                        JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Update Successfull!. This will exit the update screen.");
                        this.dispose();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbtnUpdateActionPerformed

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCancelActionPerformed

    private void jrdoJHSPeptPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoJHSPeptPasserNoActionPerformed
        jcboJHSPeptPasserMonth.setEnabled(false);
        jcboJHSPeptPasserYear.setEnabled(false);
    }//GEN-LAST:event_jrdoJHSPeptPasserNoActionPerformed

    private void jrdoJHSPeptPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoJHSPeptPasserYesActionPerformed
        jcboJHSPeptPasserMonth.setEnabled(true);
        jcboJHSPeptPasserYear.setEnabled(true);
    }//GEN-LAST:event_jrdoJHSPeptPasserYesActionPerformed

    private void jrdoJHSAEPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoJHSAEPasserNoActionPerformed
        jcboJHSAEPasserMonth.setEnabled(false);
        jcboJHSAEPasserYear.setEnabled(false);
    }//GEN-LAST:event_jrdoJHSAEPasserNoActionPerformed

    private void jrdoJHSAEPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoJHSAEPasserYesActionPerformed
        jcboJHSAEPasserMonth.setEnabled(true);
        jcboJHSAEPasserYear.setEnabled(true);
    }//GEN-LAST:event_jrdoJHSAEPasserYesActionPerformed

    private void jrdoElemPeptPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoElemPeptPasserNoActionPerformed
        jcboElemPeptPasserMonth.setEnabled(false);
        jcboElemPeptPasserYear.setEnabled(false);
    }//GEN-LAST:event_jrdoElemPeptPasserNoActionPerformed

    private void jrdoElemPeptPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoElemPeptPasserYesActionPerformed
        jcboElemPeptPasserMonth.setEnabled(true);
        jcboElemPeptPasserYear.setEnabled(true);
    }//GEN-LAST:event_jrdoElemPeptPasserYesActionPerformed

    private void jrdoElemAEPasserNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoElemAEPasserNoActionPerformed
        jcboElemAEPasserMonth.setEnabled(false);
        jcboElemAEPasserYear.setEnabled(false);
    }//GEN-LAST:event_jrdoElemAEPasserNoActionPerformed

    private void jrdoElemAEPasserYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoElemAEPasserYesActionPerformed
        jcboElemAEPasserMonth.setEnabled(true);
        jcboElemAEPasserYear.setEnabled(true);
    }//GEN-LAST:event_jrdoElemAEPasserYesActionPerformed

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
            java.util.logging.Logger.getLogger(JUpdateStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JUpdateStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JUpdateStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JUpdateStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JUpdateStudent dialog = new JUpdateStudent(new javax.swing.JDialog(), true, 1);
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
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.ButtonGroup jbtgElemAE;
    private javax.swing.ButtonGroup jbtgElemPept;
    private javax.swing.ButtonGroup jbtgGender;
    private javax.swing.ButtonGroup jbtgJHSAE;
    private javax.swing.ButtonGroup jbtgJHSPept;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnUpdate;
    private javax.swing.JComboBox<String> jcboDOBDay;
    private javax.swing.JComboBox<String> jcboDOBMonth;
    private javax.swing.JComboBox<String> jcboDOBYear;
    private javax.swing.JComboBox<String> jcboElemAEPasserMonth;
    private javax.swing.JComboBox<String> jcboElemAEPasserYear;
    private javax.swing.JComboBox<String> jcboElemGradMonth;
    private javax.swing.JComboBox<String> jcboElemGradYear;
    private javax.swing.JComboBox<String> jcboElemPeptPasserMonth;
    private javax.swing.JComboBox<String> jcboElemPeptPasserYear;
    private javax.swing.JComboBox<String> jcboElemRegion;
    private javax.swing.JComboBox<String> jcboJHSAEPasserMonth;
    private javax.swing.JComboBox<String> jcboJHSAEPasserYear;
    private javax.swing.JComboBox<String> jcboJHSGradMonth;
    private javax.swing.JComboBox<String> jcboJHSGradYear;
    private javax.swing.JComboBox<String> jcboJHSPeptPasserMonth;
    private javax.swing.JComboBox<String> jcboJHSPeptPasserYear;
    private javax.swing.JComboBox<String> jcboJHSRegion;
    private javax.swing.JRadioButton jrdoElemAEPasserNo;
    private javax.swing.JRadioButton jrdoElemAEPasserYes;
    private javax.swing.JRadioButton jrdoElemPeptPasserNo;
    private javax.swing.JRadioButton jrdoElemPeptPasserYes;
    private javax.swing.JRadioButton jrdoFemale;
    private javax.swing.JRadioButton jrdoJHSAEPasserNo;
    private javax.swing.JRadioButton jrdoJHSAEPasserYes;
    private javax.swing.JRadioButton jrdoJHSPeptPasserNo;
    private javax.swing.JRadioButton jrdoJHSPeptPasserYes;
    private javax.swing.JRadioButton jrdoMale;
    private javax.swing.JTextField jtxfElemSchoolAddress;
    private javax.swing.JTextField jtxfElemSchoolName;
    private javax.swing.JTextField jtxfFirstname;
    private javax.swing.JTextField jtxfJHSSchoolAddress;
    private javax.swing.JTextField jtxfJHSSchoolName;
    private javax.swing.JTextField jtxfLRN;
    private javax.swing.JTextField jtxfLastname;
    private javax.swing.JTextField jtxfMiddlename;
    private javax.swing.JTextField jtxfNationality;
    private javax.swing.JTextField jtxfPlaceOfBirth;
    // End of variables declaration//GEN-END:variables
}
