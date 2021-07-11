/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.ui;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
import shrs.utility.FieldValidator;
import shrs.utility.SysDefaults;

/**
 *
 * @author white_000
 */
public class JJuniorHighReg extends javax.swing.JDialog {

    /**
     * Creates new form JJuniorHighReg
     */
    public JJuniorHighReg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setLocationRelativeTo(parent);
        
        InitDefaults();
        InitializeJCBO();
        
        LoadBarangays();
    }
    
    private void InitDefaults() {
        jtxfBarangay.setEnabled(false);
    }
    
    private void InitializeJCBO() {
        jcboYear.removeAllItems();
        jcboBarangay.removeAllItems();
     
        Calendar cal = Calendar.getInstance();
        
        setTitle("New Junior High School Student - Registration  [School Year " + cal.get(Calendar.YEAR) 
                + "-" + (cal.get(Calendar.YEAR)+1) + "]");
        
        for(int ii = cal.get(Calendar.YEAR)+1; ii >= SysDefaults.getMinimumCboYearValue(); ii--) {
            jcboYear.addItem(String.valueOf(ii));
        }
        
        jcboYear.setSelectedItem(String.valueOf(cal.get(Calendar.YEAR) - 12));
    }
    
    private void LoadBarangays() {
        DBManager dbm = new DBManager();
        try {
            ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", "cf_major = 'BGY'", "cf_minor asc");
            String sBgy = "";
            while(rs.next()) {
                sBgy = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                jcboBarangay.addItem(sBgy);
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
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
              
        fv = new FieldValidator(jtxfHouseNo.getText(), "House Number");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfHouseNo.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfStreet.getText(), "Street Name");
        fv.setRequired(true);
        fv.setFieldlength(95);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfStreet.grabFocus();
            return fv.isValidated;
        }
        
        if(jrdoMakatiResidentNo.isSelected()) {
            fv = new FieldValidator(jtxfBarangay.getText(), "Barangay Name");
            fv.setRequired(true);
            fv.setFieldlength(95);
            fv.doValidation();
            if(fv.isValidated == false) {
                JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jtxfBarangay.grabFocus();
                return fv.isValidated;
            }
            
            fv = new FieldValidator(jtxfCityMunicipality.getText(), "City/Municipality Name");
            fv.setRequired(true);
            fv.setFieldlength(95);
            fv.doValidation();
            if(fv.isValidated == false) {
                JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jtxfCityMunicipality.grabFocus();
                return fv.isValidated;
            }
        }
          
        fv = new FieldValidator(jtxfGuardianName.getText(), "Name of Guardian");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfGuardianName.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfSchoolLastAttended.getText(), "School Last Attended");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfSchoolLastAttended.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfSchoolLastAttendedAddr.getText(), "School Last Attended Address");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfSchoolLastAttendedAddr.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfLRN.getText(), "LRN");
        fv.setFieldlength(13);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfLRN.grabFocus();
            return fv.isValidated;
        }
        
        if(IsOldStudentDetected()) {
            JOptionPane.showMessageDialog(this, "Old student name detected, please inform your facilitator", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfLastname.grabFocus();
            return false;
        }

        return true;
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
    
    private SPParam[] RegisterStudent() {
        SPParam[] arrParams = new SPParam[17];
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
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "psd_lrn_no", 
            jtxfLRN.getText());
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psd_addr_houseno", 
            jtxfHouseNo.getText());
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psd_addr_street", 
            jtxfStreet.getText());
        if(jrdoMakatiResidentYes.isSelected()) {
        arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "psd_addr_barangay", 
            ((String)jcboBarangay.getSelectedItem()).substring(0, 3));
        }
        else {
        arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "psd_addr_barangay", 
            jtxfBarangay.getText());
        }
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 10, "psd_addr_citymunicipality", 
            jtxfCityMunicipality.getText());
        arrParams[10] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 11, "pso_guardian_name", 
            jtxfGuardianName.getText());
        arrParams[11] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 12, "pso_guardian_relation", 
            (String)jcboGuardianRelationship.getSelectedItem());
        arrParams[12] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 13, "pso_guardian_contact_no", 
            jtxfContactNo.getText());
        arrParams[13] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 14, "psd_lastschool_attended", 
            jtxfSchoolLastAttended.getText());
        arrParams[14] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 15, "psd_lastschool_address", 
            jtxfSchoolLastAttendedAddr.getText());
        
        arrParams[15] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 16, "psd_flag", 0);
        arrParams[16] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 17, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_JuniorHighRegister", arrParams);
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
        jbtgMakatiResident = new javax.swing.ButtonGroup();
        jLabel24 = new javax.swing.JLabel();
        jcboGuardianRelationship = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        jtxfLRN = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtxfLastname = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxfFirstname = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jtxfContactNo = new javax.swing.JTextField();
        jtxfMiddlename = new javax.swing.JTextField();
        jpnlGender2 = new javax.swing.JPanel();
        jrdoMale = new javax.swing.JRadioButton();
        jrdoFemale = new javax.swing.JRadioButton();
        jLabel23 = new javax.swing.JLabel();
        jtxfGuardianName = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jtxfHouseNo = new javax.swing.JTextField();
        jtxfStreet = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jtxfBarangay = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jtxfCityMunicipality = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jrdoMakatiResidentNo = new javax.swing.JRadioButton();
        jrdoMakatiResidentYes = new javax.swing.JRadioButton();
        jcboBarangay = new javax.swing.JComboBox<>();
        jpnlDOB3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jcboMonth = new javax.swing.JComboBox<>();
        jcboDay = new javax.swing.JComboBox<>();
        jcboYear = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jtxfSchoolLastAttended = new javax.swing.JTextField();
        jtxfSchoolLastAttendedAddr = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jbtnRegister = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel24.setText("Relationship:");

        jcboGuardianRelationship.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Parent", "Relative", "Non-Relative" }));

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("LRN - Learner's Reference Number"));

        jtxfLRN.setMaximumSize(new java.awt.Dimension(92, 24));
        jtxfLRN.setMinimumSize(new java.awt.Dimension(92, 24));
        jtxfLRN.setPreferredSize(new java.awt.Dimension(92, 24));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtxfLRN, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtxfLRN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel6.setText("Lastname:");

        jLabel7.setText("Firstname:");

        jLabel26.setText("Cel.No./Tel.No.:");

        jLabel8.setText("Middlename:");

        jpnlGender2.setBorder(javax.swing.BorderFactory.createTitledBorder("Gender"));

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

        jLabel23.setText("Name of Guardian:");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));

        jLabel16.setText("House No.");

        jtxfHouseNo.setMaximumSize(new java.awt.Dimension(72, 24));
        jtxfHouseNo.setMinimumSize(new java.awt.Dimension(72, 24));
        jtxfHouseNo.setPreferredSize(new java.awt.Dimension(72, 24));

        jtxfStreet.setMaximumSize(new java.awt.Dimension(210, 24));
        jtxfStreet.setMinimumSize(new java.awt.Dimension(210, 24));
        jtxfStreet.setPreferredSize(new java.awt.Dimension(210, 24));

        jLabel17.setText("Street");

        jtxfBarangay.setMaximumSize(new java.awt.Dimension(198, 24));
        jtxfBarangay.setMinimumSize(new java.awt.Dimension(198, 24));
        jtxfBarangay.setPreferredSize(new java.awt.Dimension(198, 24));

        jLabel18.setText("Barangay");

        jtxfCityMunicipality.setEditable(false);
        jtxfCityMunicipality.setText("Makati City");
        jtxfCityMunicipality.setMaximumSize(new java.awt.Dimension(190, 24));
        jtxfCityMunicipality.setMinimumSize(new java.awt.Dimension(190, 24));
        jtxfCityMunicipality.setPreferredSize(new java.awt.Dimension(190, 24));

        jLabel19.setText("City/Municipality");

        jLabel9.setText("Makati Resident?");

        jbtgMakatiResident.add(jrdoMakatiResidentNo);
        jrdoMakatiResidentNo.setText("No");
        jrdoMakatiResidentNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoMakatiResidentNoActionPerformed(evt);
            }
        });

        jbtgMakatiResident.add(jrdoMakatiResidentYes);
        jrdoMakatiResidentYes.setSelected(true);
        jrdoMakatiResidentYes.setText("Yes");
        jrdoMakatiResidentYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrdoMakatiResidentYesActionPerformed(evt);
            }
        });

        jcboBarangay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "001 - Poblacion", "002 - East Rembo", "003 - West Rembo", "004 - Forbes Park" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jtxfHouseNo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jtxfStreet, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrdoMakatiResidentYes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrdoMakatiResidentNo)
                        .addGap(78, 78, 78)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jcboBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxfCityMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(59, 59, 59))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jrdoMakatiResidentYes, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jrdoMakatiResidentNo, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxfHouseNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfStreet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfCityMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpnlDOB3.setBorder(javax.swing.BorderFactory.createTitledBorder("Date of Birth (Month, Day, Year)"));

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

        jLabel10.setText("School Last Attended:");

        jtxfSchoolLastAttended.setMaximumSize(new java.awt.Dimension(150, 24));
        jtxfSchoolLastAttended.setMinimumSize(new java.awt.Dimension(150, 24));
        jtxfSchoolLastAttended.setPreferredSize(new java.awt.Dimension(150, 24));

        jtxfSchoolLastAttendedAddr.setMaximumSize(new java.awt.Dimension(263, 24));
        jtxfSchoolLastAttendedAddr.setMinimumSize(new java.awt.Dimension(263, 24));
        jtxfSchoolLastAttendedAddr.setPreferredSize(new java.awt.Dimension(263, 24));

        jLabel32.setText("Address:");

        jbtnRegister.setMnemonic(KeyEvent.VK_R);
        jbtnRegister.setText("Register");
        jbtnRegister.setToolTipText("Click to register");
        jbtnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRegisterActionPerformed(evt);
            }
        });

        jbtnCancel.setMnemonic(KeyEvent.VK_C);
        jbtnCancel.setText("Cancel");
        jbtnCancel.setToolTipText("Click to cancel registration");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnRegister)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxfFirstname)
                                    .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jpnlGender2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jpnlDOB3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jtxfGuardianName, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                            .addComponent(jcboGuardianRelationship, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel32))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jtxfSchoolLastAttended, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jtxfSchoolLastAttendedAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jpnlGender2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpnlDOB3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jtxfContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jtxfGuardianName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jcboGuardianRelationship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jtxfSchoolLastAttended, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32)
                            .addComponent(jtxfSchoolLastAttendedAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancel)
                    .addComponent(jbtnRegister))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Are you sure to cancel the registration?",
            this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        this.dispose();
        }
    }//GEN-LAST:event_jbtnCancelActionPerformed

    private void jrdoMakatiResidentNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoMakatiResidentNoActionPerformed
        jcboBarangay.setEnabled(false);
        jcboBarangay.setSelectedIndex(0);
        jtxfBarangay.setEnabled(true);
        jtxfBarangay.setText("");
        jtxfCityMunicipality.setText("");
        jtxfCityMunicipality.setEditable(true);
    }//GEN-LAST:event_jrdoMakatiResidentNoActionPerformed

    private void jrdoMakatiResidentYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrdoMakatiResidentYesActionPerformed
        jcboBarangay.setEnabled(true);
        jtxfBarangay.setEnabled(false);
        jtxfBarangay.setText("");
        jtxfCityMunicipality.setText("Makati City");
        jtxfCityMunicipality.setEditable(false);
    }//GEN-LAST:event_jrdoMakatiResidentYesActionPerformed

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
            java.util.logging.Logger.getLogger(JJuniorHighReg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JJuniorHighReg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JJuniorHighReg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JJuniorHighReg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JJuniorHighReg dialog = new JJuniorHighReg(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel5;
    private javax.swing.ButtonGroup jbtgGender;
    private javax.swing.ButtonGroup jbtgMakatiResident;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnRegister;
    private javax.swing.JComboBox<String> jcboBarangay;
    private javax.swing.JComboBox<String> jcboDay;
    private javax.swing.JComboBox<String> jcboGuardianRelationship;
    private javax.swing.JComboBox<String> jcboMonth;
    private javax.swing.JComboBox<String> jcboYear;
    private javax.swing.JPanel jpnlDOB3;
    private javax.swing.JPanel jpnlGender2;
    private javax.swing.JRadioButton jrdoFemale;
    private javax.swing.JRadioButton jrdoMakatiResidentNo;
    private javax.swing.JRadioButton jrdoMakatiResidentYes;
    private javax.swing.JRadioButton jrdoMale;
    private javax.swing.JTextField jtxfBarangay;
    private javax.swing.JTextField jtxfCityMunicipality;
    private javax.swing.JTextField jtxfContactNo;
    private javax.swing.JTextField jtxfFirstname;
    private javax.swing.JTextField jtxfGuardianName;
    private javax.swing.JTextField jtxfHouseNo;
    private javax.swing.JTextField jtxfLRN;
    private javax.swing.JTextField jtxfLastname;
    private javax.swing.JTextField jtxfMiddlename;
    private javax.swing.JTextField jtxfSchoolLastAttended;
    private javax.swing.JTextField jtxfSchoolLastAttendedAddr;
    private javax.swing.JTextField jtxfStreet;
    // End of variables declaration//GEN-END:variables
}
