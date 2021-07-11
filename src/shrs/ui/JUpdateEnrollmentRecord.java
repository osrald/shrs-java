/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.ui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JOptionPane;
import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
import shrs.utility.FieldValidator;
import java.awt.event.KeyEvent;

/**
 *
 * @author osraldo
 */
public class JUpdateEnrollmentRecord extends javax.swing.JDialog {
    private int m_nPrimaryKey;
    /**
     * Creates new form JUpdateEnrollmentRecord
     */
    public JUpdateEnrollmentRecord(javax.swing.JDialog parent, boolean modal, int nPrimaryKey) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
         m_nPrimaryKey = nPrimaryKey;
         
         LoadEnrollmentRecord();
    }
    
    private void LoadEnrollmentRecord() {
        DBManager dbm = new DBManager();
        ResultSet rsSubjects  = null;
        String sTbls = "students st left join students_other_info so on st.sd_primary_key = so.so_primary_key";
        String sCols = "st.sd_lastname, st.sd_firstname, st.sd_middlename, ";
        sCols += "st.sd_addr_houseno, st.sd_addr_street, st.sd_addr_barangay, st.sd_addr_citymunicipality, ";
        sCols += "so.so_religion, so.so_dialect_spoken, so.so_fathers_name, so.so_fathers_occupation, ";
        sCols += "so.so_fathers_contact_no, so.so_mothers_name, so.so_mothers_occupation, so.so_mothers_contact_no, ";
        sCols += "so.so_guardian_name, so.so_guardian_occupation, so.so_guardian_contact_no, so.so_guardian_relation, ";
        sCols += "so.so_guardian_address, st.sd_lastschool_attended, st.sd_lastschool_address, st.sd_lastschool_average, ";
        sCols += "st.sd_lastschool_yearsection, st.sd_lastschool_adviser, st.sd_lastschool_sy";
        String sCond = "st.sd_primary_key = " + m_nPrimaryKey;

        try {
            rsSubjects = dbm.getResultSet(sTbls, sCols, sCond, "");
            rsSubjects.last();
            jtxfLastname.setText(rsSubjects.getString("sd_lastname"));
            jtxfFirstname.setText(rsSubjects.getString("sd_firstname"));
            jtxfMiddlename.setText(rsSubjects.getString("sd_middlename"));
            jtxfHouseNo.setText(rsSubjects.getString("sd_addr_houseno"));
            jtxfStreet.setText(rsSubjects.getString("sd_addr_street"));
            jtxfBarangay.setText(rsSubjects.getString("sd_addr_barangay"));
            jtxfMunicipality.setText(rsSubjects.getString("sd_addr_citymunicipality"));
            jtxfReligion.setText(rsSubjects.getString("so_religion"));
            jtxfDialect.setText(rsSubjects.getString("so_dialect_spoken"));
            jtxfFatherName.setText(rsSubjects.getString("so_fathers_name"));
            jtxfFatherOccupation.setText(rsSubjects.getString("so_fathers_occupation"));
            jtxfFatherContactNo.setText(rsSubjects.getString("so_fathers_contact_no"));
            jtxfMotherName.setText(rsSubjects.getString("so_mothers_name"));
            jtxfMotherOccupation.setText(rsSubjects.getString("so_mothers_occupation"));
            jtxfMotherContactNo.setText(rsSubjects.getString("so_mothers_contact_no"));
            jtxfGuardianName.setText(rsSubjects.getString("so_guardian_name"));
            jtxfGuardianOccupation.setText(rsSubjects.getString("so_guardian_occupation"));
            jtxfGuardianContactNo.setText(rsSubjects.getString("so_guardian_contact_no"));
            jcboGuardianRelation.setSelectedItem(rsSubjects.getString("so_guardian_relation"));
            jtxfGuardianAddress.setText(rsSubjects.getString("so_guardian_address"));
            jtxfLastSchoolAttended.setText(rsSubjects.getString("sd_lastschool_attended"));
            jtxfLastSchoolAddress.setText(rsSubjects.getString("sd_lastschool_address"));
            jtxfLastSchoolAverage.setText(rsSubjects.getString("sd_lastschool_average"));
            jtxfLastSchoolYearSection.setText(rsSubjects.getString("sd_lastschool_yearsection"));
            jtxfLastSchoolAdviser.setText(rsSubjects.getString("sd_lastschool_adviser"));
            jtxfLastSchoolYear.setText(rsSubjects.getString("sd_lastschool_sy"));
        } 
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        finally	{
            if (rsSubjects != null) {
                try {
                    rsSubjects.close();
                } 
                catch(SQLException sqlEx){}//ignore
                rsSubjects = null;
            }
        }
    }
    
    private boolean ValidateFields() {
        FieldValidator fv = new FieldValidator(jtxfHouseNo.getText(), "House No.");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfHouseNo.grabFocus();
            return fv.isValidated;
        }
               
        fv = new FieldValidator(jtxfStreet.getText(), "Street");
        fv.setRequired(true);
        fv.setFieldlength(95);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfStreet.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfBarangay.getText(), "Barangay");
        fv.setRequired(true);
        fv.setFieldlength(95);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfBarangay.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfMunicipality.getText(), "City/Municipality");
        fv.setRequired(true);
        fv.setFieldlength(95);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfMunicipality.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfFatherName.getText(), "Name of Father");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfFatherName.grabFocus();
            return fv.isValidated;
        }

        fv = new FieldValidator(jtxfMotherName.getText(), "Name of Mother");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfMotherName.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfGuardianName.getText(), "Name of Guardian");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfGuardianName.grabFocus();
            return fv.isValidated;
        }
     
        fv = new FieldValidator(jtxfGuardianContactNo.getText(), "Guardian Contact No.");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfGuardianContactNo.grabFocus();
            return fv.isValidated;
        }

        fv = new FieldValidator(jtxfGuardianAddress.getText(), "Guardian Address");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfGuardianAddress.grabFocus();
            return fv.isValidated;
        } 
        
        fv = new FieldValidator(jtxfLastSchoolAttended.getText(), "Last School Attended");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfLastSchoolAttended.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfLastSchoolAddress.getText(), "Last School Address");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfLastSchoolAddress.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfLastSchoolYearSection.getText(), "Former School Year and Section");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfLastSchoolYearSection.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfLastSchoolAdviser.getText(), "Former Class Adviser");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfLastSchoolAdviser.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfLastSchoolYear.getText(), "Former School Year");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfLastSchoolYear.grabFocus();
            return fv.isValidated;
        }
        
        return true;
    }
    
    private SPParam[] UpdateEnrollmentRecord() {
        SPParam[] arrParams = new SPParam[26];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            m_nPrimaryKey);
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psd_addr_houseno", 
            jtxfHouseNo.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psd_addr_street", 
            jtxfStreet.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psd_addr_barangay", 
            jtxfBarangay.getText());
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psd_addr_citymunicipality", 
            jtxfMunicipality.getText());
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "pso_religion", 
            jtxfReligion.getText());
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "pso_dialect_spoken", 
            jtxfDialect.getText());
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "pso_fathers_name", 
            jtxfFatherName.getText());
        arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "pso_fathers_occupation", 
            jtxfFatherOccupation.getText());
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 10, "pso_fathers_contact_no", 
            jtxfFatherOccupation.getText());
        arrParams[10] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 11, "pso_mothers_name", 
            jtxfMotherName.getText());
        arrParams[11] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 12, "pso_mothers_occupation", 
            jtxfMotherOccupation.getText());
        arrParams[12] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 13, "pso_mothers_contact_no", 
            jtxfMotherContactNo.getText());
        arrParams[13] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 14, "pso_guardian_name", 
            jtxfGuardianName.getText());
        arrParams[14] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 15, "pso_guardian_occupation", 
            jtxfGuardianOccupation.getText());
        arrParams[15] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 16, "pso_guardian_contact_no", 
            jtxfGuardianContactNo.getText());
        arrParams[16] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 17, "pso_guardian_relation", 
            (String)jcboGuardianRelation.getSelectedItem());
        arrParams[17] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 18, "pso_guardian_address", 
            jtxfGuardianAddress.getText());
        arrParams[18] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 19, "psd_lastschool_attended", 
            jtxfLastSchoolAttended.getText());
        arrParams[19] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 20, "psd_lastschool_address", 
            jtxfLastSchoolAddress.getText());
        arrParams[20] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 21, "psd_lastschool_average", 
            jtxfLastSchoolAverage.getText());
        arrParams[21] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 22, "psd_lastschool_yearsection", 
            jtxfLastSchoolYearSection.getText());
        arrParams[22] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 23, "psd_lastschool_adviser", 
            jtxfLastSchoolAdviser.getText());
        arrParams[23] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 24, "psd_lastschool_sy", 
            jtxfLastSchoolYear.getText());

        arrParams[24] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 25, "psd_flag", 0);
        arrParams[25] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 26, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_EnrollRecordUpdate", arrParams);  
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxfLastname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxfFirstname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxfMiddlename = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtxfHouseNo = new javax.swing.JTextField();
        jtxfStreet = new javax.swing.JTextField();
        jtxfBarangay = new javax.swing.JTextField();
        jtxfMunicipality = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jtxfReligion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jtxfDialect = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jtxfFatherName = new javax.swing.JTextField();
        jtxfMotherName = new javax.swing.JTextField();
        jtxfGuardianName = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jtxfFatherOccupation = new javax.swing.JTextField();
        jtxfMotherOccupation = new javax.swing.JTextField();
        jtxfGuardianOccupation = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jtxfFatherContactNo = new javax.swing.JTextField();
        jtxfMotherContactNo = new javax.swing.JTextField();
        jtxfGuardianContactNo = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jcboGuardianRelation = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jtxfGuardianAddress = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jtxfLastSchoolAttended = new javax.swing.JTextField();
        jtxfLastSchoolAddress = new javax.swing.JTextField();
        jtxfLastSchoolAverage = new javax.swing.JTextField();
        jtxfLastSchoolYearSection = new javax.swing.JTextField();
        jtxfLastSchoolAdviser = new javax.swing.JTextField();
        jtxfLastSchoolYear = new javax.swing.JTextField();
        jbtnCancel = new javax.swing.JButton();
        jbtnUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Update Enrollment Record");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Editing"));

        jLabel1.setText("Lastname:");

        jtxfLastname.setEditable(false);
        jtxfLastname.setMaximumSize(new java.awt.Dimension(162, 24));
        jtxfLastname.setMinimumSize(new java.awt.Dimension(162, 24));
        jtxfLastname.setPreferredSize(new java.awt.Dimension(162, 24));

        jLabel2.setText("Firstname:");

        jtxfFirstname.setEditable(false);
        jtxfFirstname.setMaximumSize(new java.awt.Dimension(172, 24));
        jtxfFirstname.setMinimumSize(new java.awt.Dimension(172, 24));
        jtxfFirstname.setPreferredSize(new java.awt.Dimension(172, 24));

        jLabel3.setText("Middlename:");

        jtxfMiddlename.setEditable(false);
        jtxfMiddlename.setMaximumSize(new java.awt.Dimension(140, 24));
        jtxfMiddlename.setMinimumSize(new java.awt.Dimension(140, 24));
        jtxfMiddlename.setPreferredSize(new java.awt.Dimension(140, 24));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(731, 282));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(731, 282));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));
        jPanel4.setMaximumSize(new java.awt.Dimension(702, 81));
        jPanel4.setMinimumSize(new java.awt.Dimension(702, 81));
        jPanel4.setPreferredSize(new java.awt.Dimension(702, 81));

        jLabel4.setText("House No.");

        jLabel5.setText("Street");

        jLabel6.setText("Barangay");

        jLabel7.setText("City/Municipality");

        jtxfHouseNo.setMaximumSize(new java.awt.Dimension(72, 24));
        jtxfHouseNo.setMinimumSize(new java.awt.Dimension(72, 24));
        jtxfHouseNo.setPreferredSize(new java.awt.Dimension(72, 24));

        jtxfStreet.setMaximumSize(new java.awt.Dimension(210, 24));
        jtxfStreet.setMinimumSize(new java.awt.Dimension(210, 24));
        jtxfStreet.setPreferredSize(new java.awt.Dimension(210, 24));

        jtxfBarangay.setMaximumSize(new java.awt.Dimension(198, 24));
        jtxfBarangay.setMinimumSize(new java.awt.Dimension(198, 24));
        jtxfBarangay.setPreferredSize(new java.awt.Dimension(198, 24));

        jtxfMunicipality.setMaximumSize(new java.awt.Dimension(182, 24));
        jtxfMunicipality.setMinimumSize(new java.awt.Dimension(182, 24));
        jtxfMunicipality.setPreferredSize(new java.awt.Dimension(182, 24));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxfHouseNo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxfStreet, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jtxfBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jtxfMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxfHouseNo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfStreet, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jLabel8.setText("Religion:");

        jLabel9.setText("Dialiect:");

        jtxfDialect.setMaximumSize(new java.awt.Dimension(371, 24));
        jtxfDialect.setMinimumSize(new java.awt.Dimension(371, 24));
        jtxfDialect.setPreferredSize(new java.awt.Dimension(371, 24));

        jLabel10.setText("Father's Name:");

        jLabel11.setText("Mother's Name:");

        jLabel12.setText("Guardian Name:");

        jtxfFatherName.setMaximumSize(new java.awt.Dimension(179, 24));
        jtxfFatherName.setMinimumSize(new java.awt.Dimension(179, 24));
        jtxfFatherName.setPreferredSize(new java.awt.Dimension(179, 24));

        jtxfMotherName.setMaximumSize(new java.awt.Dimension(179, 24));
        jtxfMotherName.setMinimumSize(new java.awt.Dimension(179, 24));
        jtxfMotherName.setPreferredSize(new java.awt.Dimension(179, 24));

        jtxfGuardianName.setMaximumSize(new java.awt.Dimension(178, 24));
        jtxfGuardianName.setMinimumSize(new java.awt.Dimension(178, 24));
        jtxfGuardianName.setPreferredSize(new java.awt.Dimension(178, 24));

        jLabel13.setText("Occupation:");

        jLabel14.setText("Occupation:");

        jLabel15.setText("Occupation:");

        jtxfFatherOccupation.setMaximumSize(new java.awt.Dimension(92, 24));
        jtxfFatherOccupation.setMinimumSize(new java.awt.Dimension(92, 24));
        jtxfFatherOccupation.setPreferredSize(new java.awt.Dimension(92, 24));

        jtxfMotherOccupation.setMaximumSize(new java.awt.Dimension(92, 24));
        jtxfMotherOccupation.setMinimumSize(new java.awt.Dimension(92, 24));
        jtxfMotherOccupation.setPreferredSize(new java.awt.Dimension(92, 24));

        jtxfGuardianOccupation.setMaximumSize(new java.awt.Dimension(92, 24));
        jtxfGuardianOccupation.setMinimumSize(new java.awt.Dimension(92, 24));
        jtxfGuardianOccupation.setPreferredSize(new java.awt.Dimension(92, 24));

        jLabel16.setText("Cel.No./Tel.No.:");

        jLabel17.setText("Cel.No./Tel.No.:");

        jLabel18.setText("Cel.No./Tel.No.:");

        jtxfFatherContactNo.setMaximumSize(new java.awt.Dimension(161, 24));
        jtxfFatherContactNo.setMinimumSize(new java.awt.Dimension(161, 24));
        jtxfFatherContactNo.setPreferredSize(new java.awt.Dimension(161, 24));

        jtxfMotherContactNo.setMaximumSize(new java.awt.Dimension(161, 24));
        jtxfMotherContactNo.setMinimumSize(new java.awt.Dimension(161, 24));
        jtxfMotherContactNo.setPreferredSize(new java.awt.Dimension(161, 24));

        jtxfGuardianContactNo.setMaximumSize(new java.awt.Dimension(161, 24));
        jtxfGuardianContactNo.setMinimumSize(new java.awt.Dimension(161, 24));
        jtxfGuardianContactNo.setPreferredSize(new java.awt.Dimension(161, 24));

        jLabel19.setText("Guardian Relation:");

        jcboGuardianRelation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Parent", "Relative", "Non-Relative" }));
        jcboGuardianRelation.setMaximumSize(new java.awt.Dimension(101, 26));

        jLabel20.setText("Address:");

        jtxfGuardianAddress.setMaximumSize(new java.awt.Dimension(429, 24));
        jtxfGuardianAddress.setMinimumSize(new java.awt.Dimension(429, 24));
        jtxfGuardianAddress.setPreferredSize(new java.awt.Dimension(429, 24));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboGuardianRelation, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfGuardianAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfGuardianName, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfReligion, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxfFatherName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jtxfMotherName, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfDialect, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfGuardianOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfMotherOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfFatherOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfFatherContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfGuardianContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfMotherContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jtxfReligion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jtxfDialect, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jtxfFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jtxfFatherOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jtxfFatherContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jtxfMotherName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jtxfMotherOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jtxfMotherContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jtxfGuardianName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jtxfGuardianOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jtxfGuardianContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jcboGuardianRelation, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jtxfGuardianAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Student Information", jPanel2);

        jLabel21.setText("School Last Attended:");

        jLabel22.setText("Address:");

        jLabel23.setText("Average:");

        jLabel24.setText("Former Year & Section:");

        jLabel25.setText("Former Adviser:");

        jLabel26.setText("Former School Year:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxfLastSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfLastSchoolAverage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfLastSchoolYearSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfLastSchoolAdviser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfLastSchoolYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfLastSchoolAttended, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxfLastSchoolAddress, jtxfLastSchoolAdviser, jtxfLastSchoolAttended, jtxfLastSchoolAverage, jtxfLastSchoolYear, jtxfLastSchoolYearSection});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jtxfLastSchoolAttended, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jtxfLastSchoolAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jtxfLastSchoolAverage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jtxfLastSchoolYearSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jtxfLastSchoolAdviser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jtxfLastSchoolYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("School Information", jPanel3);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 731, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jbtnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnCancel)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnCancel, jbtnUpdate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancel)
                    .addComponent(jbtnUpdate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpdateActionPerformed
        if (ValidateFields()) {
            if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm",
                this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SPParam[] outParams = UpdateEnrollmentRecord();
                if (outParams != null) {
                    Integer nOut = new Integer(outParams[0].getParamValue().toString());
                    if(nOut != 0) {
                        String sErrMsg =  outParams[1].getParamValue().toString();
                        JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Enrollment Record Successfully Updated!");
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
            java.util.logging.Logger.getLogger(JUpdateEnrollmentRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JUpdateEnrollmentRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JUpdateEnrollmentRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JUpdateEnrollmentRecord.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JUpdateEnrollmentRecord dialog = new JUpdateEnrollmentRecord(new javax.swing.JDialog(), true, 1);
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnUpdate;
    private javax.swing.JComboBox<String> jcboGuardianRelation;
    private javax.swing.JTextField jtxfBarangay;
    private javax.swing.JTextField jtxfDialect;
    private javax.swing.JTextField jtxfFatherContactNo;
    private javax.swing.JTextField jtxfFatherName;
    private javax.swing.JTextField jtxfFatherOccupation;
    private javax.swing.JTextField jtxfFirstname;
    private javax.swing.JTextField jtxfGuardianAddress;
    private javax.swing.JTextField jtxfGuardianContactNo;
    private javax.swing.JTextField jtxfGuardianName;
    private javax.swing.JTextField jtxfGuardianOccupation;
    private javax.swing.JTextField jtxfHouseNo;
    private javax.swing.JTextField jtxfLastSchoolAddress;
    private javax.swing.JTextField jtxfLastSchoolAdviser;
    private javax.swing.JTextField jtxfLastSchoolAttended;
    private javax.swing.JTextField jtxfLastSchoolAverage;
    private javax.swing.JTextField jtxfLastSchoolYear;
    private javax.swing.JTextField jtxfLastSchoolYearSection;
    private javax.swing.JTextField jtxfLastname;
    private javax.swing.JTextField jtxfMiddlename;
    private javax.swing.JTextField jtxfMotherContactNo;
    private javax.swing.JTextField jtxfMotherName;
    private javax.swing.JTextField jtxfMotherOccupation;
    private javax.swing.JTextField jtxfMunicipality;
    private javax.swing.JTextField jtxfReligion;
    private javax.swing.JTextField jtxfStreet;
    // End of variables declaration//GEN-END:variables
}
