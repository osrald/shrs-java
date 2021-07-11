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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
import java.awt.event.KeyEvent;
import shrs.utility.FieldValidator;

/**
 *
 * @author osraldo
 */
public class JEnrollStudent extends javax.swing.JDialog {
    private boolean m_bFlagTrack = false;
    private Integer m_nPrimaryKey;
    /**
     * Creates new form JEnrollStudent
     */
    public JEnrollStudent(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        this.setTitle("Enrollment");
        InitializeCombos();
        LoadTracks();
        LoadStrandAndSpecs();
        
        jtblSubjects.getSelectionModel().addListSelectionListener(new SubjectsTableRowListener());
        
        m_bFlagTrack = true;
    }
    
    private void LoadDefaults() {
        DBManager dbm = new DBManager();
        try {
            ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_general_1", "cf_major = 'CFG'", "cf_minor asc");
            String sMinor;
            while(rs.next()) {
                sMinor = rs.getString("cf_minor");
                switch(sMinor) {
                    case "DOE":
                        jtxfEnrollmentDate.setText(rs.getString("cf_general_1"));
                        break;
                    case "CSY":
                        jtxfSYCurrent.setText(rs.getString("cf_general_1"));
                        break;
                }
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void LoadData() {
        SPParam[] arrParams = new SPParam[4];
        arrParams[0] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 1, "psd_lrn_no", 
            jtxfLRN.getText());
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psd_lastname", 
            jtxfLastname.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psd_firstname", 
            jtxfFirstname.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psd_middlename", 
            jtxfMiddlename.getText());

        DBManager dbm = new DBManager();
        ResultSet rs = dbm.retrieveStoredProc("SP_RetrieveStudentRecord", arrParams);
        
        if(rs == null) {
            JOptionPane.showMessageDialog(this, "Student record not retrieved.",
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            rs.last();
            int nRecCnt = rs.getRow();
            if(nRecCnt <= 0) {
                JOptionPane.showMessageDialog(this, "Student record not retrieved.",
                   this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jtxfLRN.grabFocus();
                return;
            }
            else if(nRecCnt > 1) {
                JOptionPane.showMessageDialog(this, "Multiple Student records have been retrieved. Please inform the facilitator.",
                   this.getTitle(), JOptionPane.ERROR_MESSAGE);
                jtxfLRN.grabFocus();
                return;              
            }
            else {
                if("CEN".equalsIgnoreCase(rs.getString("sd_status_cd"))) {
                    JOptionPane.showMessageDialog(this, "Student is already enrolled",
                       this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    jtxfLRN.grabFocus();
                    return;              
                }
                if("KIO".equalsIgnoreCase(rs.getString("sd_status_cd"))) {
                    JOptionPane.showMessageDialog(this, "Student is already kicked-out from this school. Please inform your facilitator.",
                       this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    jtxfLRN.grabFocus();
                    return;              
                }
                if("GRD".equalsIgnoreCase(rs.getString("sd_status_cd"))) {
                    JOptionPane.showMessageDialog(this, "Student is already graduated from this school. Please inform your facilitator.",
                       this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    jtxfLRN.grabFocus();
                    return;              
                }
                
                m_nPrimaryKey = rs.getInt("sd_primary_key");
                jbtnEnroll.setEnabled(true);
                jtxfLRN.setEditable(false);
                jtxfLRN.setText(rs.getString("sd_lrn_no"));
                jtxfLastname.setEditable(false);
                jtxfLastname.setText(rs.getString("sd_lastname"));
                jtxfFirstname.setEditable(false);
                jtxfFirstname.setText(rs.getString("sd_firstname"));
                jtxfMiddlename.setEditable(false);
                jtxfMiddlename.setText(rs.getString("sd_middlename"));
                jtxfGender.setText(rs.getString("sd_gender"));
                jtxfDateOfBirth.setText(rs.getString("sd_dob"));
                jtxfAge.setText(rs.getString("age"));
                jtxfPlaceOfBirth.setText(rs.getString("sd_birthplace"));
                jtxfJHSSchoolname.setText(rs.getString("sd_jhs_name"));
                jtxfJHSYearComp.setText(rs.getString("sd_jhs_comp_year"));
                String sTmp = rs.getString("sd_jhs_pept_passer");
                jtxfJHSPeptPasser.setText(sTmp);
                if("Y".equalsIgnoreCase(sTmp)) {
                    sTmp = rs.getString("sd_jhs_pept_month") + "/" + rs.getString("sd_jhs_pept_year");
                    jtxfJHSPeptMnthYrComp.setText(sTmp);
                }
                sTmp = rs.getString("sd_jhs_ae_passer");
                jtxfJHSAETestPasser.setText(sTmp);
                if("Y".equalsIgnoreCase(sTmp)) {
                    sTmp = rs.getString("sd_jhs_ae_month") + "/" + rs.getString("sd_jhs_ae_year");
                    jtxfJHSAETestMnthYrComp.setText(sTmp);
                }
                jtxfElemSchoolname.setText(rs.getString("sd_elem_name"));
                jtxfElemYearGrad.setText(rs.getString("sd_elem_comp_year"));
                sTmp = rs.getString("sd_elem_pept_passer");
                jtxfElemPeptPasser.setText(sTmp);
                if("Y".equalsIgnoreCase(sTmp)) {
                    sTmp = rs.getString("sd_elem_pept_month") + "/" + rs.getString("sd_elem_pept_year");
                    jtxfElemPeptMnthYrComp.setText(sTmp);
                }
                sTmp = rs.getString("sd_elem_ae_passer");
                jtxfElemAETestPasser.setText(sTmp);
                if("Y".equalsIgnoreCase(sTmp)) {
                    sTmp = rs.getString("sd_elem_ae_month") + "/" + rs.getString("sd_elem_ae_year");
                    jtxfElemAETestMnthYrComp.setText(sTmp);
                }
                jcboTrack.setSelectedItem(rs.getString("track"));
                jcboStrSpec.setSelectedItem(rs.getString("strspec"));
            }
        }
        catch(SQLException ex) {
           System.out.println("SQLException: " + ex.getMessage());
        }
        finally	{
            if (rs != null) {
                try {
                    rs.close();
                } 
                catch(SQLException sqlEx){}//ignore
                rs = null;
            }
        }       
    }
    
    private void InitializeCombos() {
        jcboTrack.removeAllItems();
        jcboStrSpec.removeAllItems();
    }
    
    private void LoadTracks() {
        DBManager dbm = new DBManager();
        try {
            String sTrack;
            ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", 
                "cf_major = 'TRK' and cf_general_2 = '001'", 
                "cf_primary_key asc");
            while(rs.next()) {
                sTrack = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                jcboTrack.addItem(sTrack);
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void LoadStrandAndSpecs() {
        DBManager dbm = new DBManager();
        try {
            String sTrack = (String)jcboTrack.getSelectedItem();
            if(sTrack == null) sTrack = "ACA";
            sTrack = sTrack.substring(0, 3);
            String sCond = "cf_major = 'SAS' and ";
            sCond += "cf_general_1 = '" + sTrack + "' and ";
            sCond += "cf_general_2 = '001'";
            String sStrandSpecs;
            ResultSet rs = dbm.getResultSet("configs", "cf_minor, cf_name", 
                sCond, "cf_primary_key asc");
            while(rs.next()) {
                sStrandSpecs = rs.getString("cf_minor") + " - " + rs.getString("cf_name");
                jcboStrSpec.addItem(sStrandSpecs);
            }

            if("ACA".equalsIgnoreCase(sTrack)) {
                jlblStrandSpecs.setText("Strand:");
            }
            else {
                jlblStrandSpecs.setText("Specialization:");
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private boolean ValidateFields() {       
        FieldValidator fv = new FieldValidator(jtxfGradeSectionCurrent.getText(), "Grade & Section");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfGradeSectionCurrent.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfHouseNo.getText(), "House No.");
        fv.setRequired(true);
        fv.setFieldlength(45);
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
        
        fv = new FieldValidator(jtxfCityMunicipality.getText(), "City/Municipality");
        fv.setRequired(true);
        fv.setFieldlength(95);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfCityMunicipality.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfFathersName.getText(), "Name of Father");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfFathersName.grabFocus();
            return fv.isValidated;
        }

        fv = new FieldValidator(jtxfMothersName.getText(), "Name of Mother");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(0);
            jtxfMothersName.grabFocus();
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
        
        fv = new FieldValidator(jtxfSchoolLastAttended.getText(), "Last School Attended");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfSchoolLastAttended.grabFocus();
            return fv.isValidated;
        } 
        
        fv = new FieldValidator(jtxfSchoolLastAttendedAddr.getText(), "Last School Attended Address");
        fv.setRequired(true);
        fv.setFieldlength(255);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfSchoolLastAttendedAddr.grabFocus();
            return fv.isValidated;
        } 
        
        fv = new FieldValidator(jtxfFormerYearSection.getText(), "Former Year & Section");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfFormerYearSection.grabFocus();
            return fv.isValidated;
        } 
        
        fv = new FieldValidator(jtxfFormerAdviser.getText(), "Former Class Adviser");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfFormerAdviser.grabFocus();
            return fv.isValidated;
        } 
        
        fv = new FieldValidator(jtxfFormerSY.getText(), "Former School Year");
        fv.setRequired(true);
        fv.setFieldlength(15);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jtxfFormerSY.grabFocus();
            return fv.isValidated;
        } 
        
        if(jcboTrack.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select your applied track.", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jcboTrack.grabFocus();
            return false;   
        }
        
        if(jcboStrSpec.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select your applied strand/specialization.", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(1);
            jcboStrSpec.grabFocus();
            return false;   
        }
        
        fv = new FieldValidator(jtxfClassAdviser.getText(), "Class Adviser");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(2);
            jtxfClassAdviser.grabFocus();
            return fv.isValidated;
        } 
        
        DefaultTableModel mdl = (DefaultTableModel)jtblSubjects.getModel();
        if(mdl.getRowCount() <= 0) {
            JOptionPane.showMessageDialog(this, "There are no subjects enrolled", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(2);
            jtblSubjects.grabFocus();
            return false;
        }
        
        return true;
    }
    
    private SPParam[] EnrollStudent() {
        SPParam[] arrParams = new SPParam[33];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            m_nPrimaryKey);
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psd_shs_date_enrolled", 
            jtxfEnrollmentDate.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psd_shs_sy", 
            jtxfSYCurrent.getText());  
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psd_shs_exam_result", 
            jtxfExamResultCurrent.getText());
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "pso_religion", 
            jtxfReligion.getText());
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "pso_dialect_spoken", 
            jtxfDialectSpoken.getText());
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psd_addr_houseno", 
            jtxfHouseNo.getText());
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psd_addr_street", 
            jtxfStreet.getText());  
        arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "psd_addr_barangay", 
            jtxfBarangay.getText());        
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 10, "psd_addr_citymunicipality",
            jtxfCityMunicipality.getText());           
        arrParams[10] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 11, "pso_fathers_name", 
             jtxfFathersName.getText());
        arrParams[11] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 12, "pso_fathers_occupation", 
             jtxfFathersOccupation.getText());
        arrParams[12] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 13, "pso_fathers_contact_no", 
             jtxfFathersContactNo.getText());
        arrParams[13] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 14, "pso_mothers_name", 
             jtxfMothersName.getText());       
        arrParams[14] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 15, "pso_mothers_occupation", 
             jtxfMothersOccupation.getText());       
        arrParams[15] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 16, "pso_mothers_contact_no", 
             jtxfMothersContactNo.getText());        
        arrParams[16] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 17, "pso_guardian_name", 
             jtxfGuardianName.getText());
        arrParams[17] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 18, "pso_guardian_relation", 
             (String)jcboGuardianRelationship.getSelectedItem());        
        arrParams[18] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 19, "pso_guardian_occupation", 
             jtxfGuadianOccupation.getText());        
        arrParams[19] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 20, "pso_guardian_contact_no", 
             jtxfGuardianContactNo.getText());        
        arrParams[20] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 21, "pso_guardian_address", 
             jtxfGuardianAddress.getText());        
        
        arrParams[21] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 22, "psd_lastschool_attended", 
             jtxfSchoolLastAttended.getText());
        arrParams[22] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 23, "psd_lastschool_address", 
             jtxfSchoolLastAttendedAddr.getText());
        arrParams[23] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 24, "psd_lastschool_average", 
             jtxfSchoolLastAttendedAverage.getText());
        arrParams[24] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 25, "psd_lastschool_yearsection", 
             jtxfFormerYearSection.getText());
        arrParams[25] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 26, "psd_lastschool_adviser", 
             jtxfFormerAdviser.getText());        
        arrParams[26] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 27, "psd_lastschool_sy", 
             jtxfFormerSY.getText());        
        arrParams[27] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 28, "psd_shs_track_enrolled_cd", 
             ((String)jcboTrack.getSelectedItem()).substring(0, 3));       
        arrParams[28] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 29, "psd_shs_strspec_enrolled_cd", 
             ((String)jcboStrSpec.getSelectedItem()).substring(0, 3));    
        arrParams[29] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 30, "psd_shs_class_adviser", 
             jtxfClassAdviser.getText());  
         arrParams[30] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 31, "psd_shs_gradesection", 
             jtxfGradeSectionCurrent.getText());         
        arrParams[31] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 32, "psd_flag", 0);
        arrParams[32] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 33, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_EnrollStudent", arrParams);
    }
    
    private SPParam[] EnrollSubjects(String sTime, String sSubject, String sSubjectTeacher, String sRoomNo) {
        SPParam[] arrParams = new SPParam[10];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_student_primary_key", 
            m_nPrimaryKey);
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psj_date_enrolled", 
            jtxfEnrollmentDate.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psj_sy", 
            jtxfSYCurrent.getText());  
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psj_gradesection", 
            jtxfGradeSectionCurrent.getText());
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psj_time", 
            sTime);
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "psj_subject", 
            sSubject);
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psj_subject_teacher", 
            sSubjectTeacher);
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psj_room_no", 
            sRoomNo);           
        arrParams[8] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 9, "psj_flag", 0);
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 10, "psj_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_EnrollSubjects", arrParams);
    }
    
    private class SubjectsTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting()) {
                return;
            }
            for (int c : jtblSubjects.getSelectedRows()) {
                jbtnUpdateSubject.setEnabled(true);
                jbtnRemoveSubject.setEnabled(true);
                jtxfTime.setText(jtblSubjects.getValueAt(c, 0).toString());
                jtxfSubject.setText(jtblSubjects.getValueAt(c, 1).toString());
                jtxfSubjectTeacher.setText(jtblSubjects.getValueAt(c, 2).toString());
                jtxfRoomNo.setText(jtblSubjects.getValueAt(c, 3).toString());
            }
        }
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
        jtxfLRN = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxfEnrollmentDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxfSYCurrent = new javax.swing.JTextField();
        jbtnRetrieve = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jtxfGradeSectionCurrent = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtxfExamResultCurrent = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jtxfLastname = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxfFirstname = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jtxfMiddlename = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jtxfGender = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jtxfReligion = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jtxfDialectSpoken = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jtxfDateOfBirth = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jtxfAge = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jtxfHouseNo = new javax.swing.JTextField();
        jtxfStreet = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jtxfBarangay = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jtxfCityMunicipality = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jtxfPlaceOfBirth = new javax.swing.JTextField();
        jtxfFathersName = new javax.swing.JTextField();
        jtxfMothersName = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jtxfGuardianName = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jcboGuardianRelationship = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jtxfFathersOccupation = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jtxfFathersContactNo = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jtxfMothersOccupation = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jtxfMothersContactNo = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jtxfGuadianOccupation = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jtxfGuardianContactNo = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jtxfGuardianAddress = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jtxfSchoolLastAttended = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jtxfFormerYearSection = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jtxfSchoolLastAttendedAddr = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jtxfFormerSY = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jtxfFormerAdviser = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jtxfSchoolLastAttendedAverage = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jtxfElemSchoolname = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jtxfElemYearGrad = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jtxfElemPeptPasser = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jtxfElemPeptMnthYrComp = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jtxfElemAETestPasser = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jtxfElemAETestMnthYrComp = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jtxfJHSSchoolname = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jtxfJHSYearComp = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jtxfJHSPeptPasser = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jtxfJHSPeptMnthYrComp = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jtxfJHSAETestPasser = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jtxfJHSAETestMnthYrComp = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jlblStrandSpecs = new javax.swing.JLabel();
        jcboTrack = new javax.swing.JComboBox<>();
        jcboStrSpec = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jtxfClassAdviser = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jtxfTime = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jtxfSubject = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jtxfSubjectTeacher = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jtxfRoomNo = new javax.swing.JTextField();
        jbtnAddSubject = new javax.swing.JButton();
        jbtnRemoveSubject = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblSubjects = new javax.swing.JTable();
        jbtnUpdateSubject = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jbtnEnroll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Learner's Reference No. (LRN): ");

        jtxfLRN.setMaximumSize(new java.awt.Dimension(162, 24));
        jtxfLRN.setMinimumSize(new java.awt.Dimension(162, 24));
        jtxfLRN.setPreferredSize(new java.awt.Dimension(162, 24));

        jLabel2.setText("Date of Enrollment:");

        jtxfEnrollmentDate.setEditable(false);
        jtxfEnrollmentDate.setMaximumSize(new java.awt.Dimension(97, 24));
        jtxfEnrollmentDate.setMinimumSize(new java.awt.Dimension(97, 24));
        jtxfEnrollmentDate.setPreferredSize(new java.awt.Dimension(97, 24));

        jLabel3.setText("S.Y.:");

        jtxfSYCurrent.setEditable(false);
        jtxfSYCurrent.setMaximumSize(new java.awt.Dimension(75, 24));
        jtxfSYCurrent.setMinimumSize(new java.awt.Dimension(75, 24));
        jtxfSYCurrent.setPreferredSize(new java.awt.Dimension(75, 24));

        jbtnRetrieve.setMnemonic(KeyEvent.VK_R);
        jbtnRetrieve.setText("Retrieve");
        jbtnRetrieve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRetrieveActionPerformed(evt);
            }
        });

        jLabel4.setText("Grade & Section:");

        jtxfGradeSectionCurrent.setMaximumSize(new java.awt.Dimension(166, 24));
        jtxfGradeSectionCurrent.setMinimumSize(new java.awt.Dimension(166, 24));
        jtxfGradeSectionCurrent.setPreferredSize(new java.awt.Dimension(166, 24));

        jLabel5.setText("Exam Result:");

        jtxfExamResultCurrent.setMaximumSize(new java.awt.Dimension(45, 24));
        jtxfExamResultCurrent.setMinimumSize(new java.awt.Dimension(45, 24));
        jtxfExamResultCurrent.setPreferredSize(new java.awt.Dimension(45, 24));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfLRN, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnRetrieve))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfEnrollmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfSYCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfGradeSectionCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfExamResultCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxfLRN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnRetrieve))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxfEnrollmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jtxfSYCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jtxfGradeSectionCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jtxfExamResultCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Lastname:");

        jtxfLastname.setMaximumSize(new java.awt.Dimension(200, 24));
        jtxfLastname.setMinimumSize(new java.awt.Dimension(200, 24));
        jtxfLastname.setPreferredSize(new java.awt.Dimension(200, 24));

        jLabel7.setText("Firstname:");

        jtxfFirstname.setMaximumSize(new java.awt.Dimension(200, 24));
        jtxfFirstname.setMinimumSize(new java.awt.Dimension(200, 24));
        jtxfFirstname.setPreferredSize(new java.awt.Dimension(200, 24));

        jLabel8.setText("Middlename:");

        jtxfMiddlename.setMaximumSize(new java.awt.Dimension(200, 24));
        jtxfMiddlename.setMinimumSize(new java.awt.Dimension(200, 24));
        jtxfMiddlename.setPreferredSize(new java.awt.Dimension(200, 24));

        jLabel11.setText("Gender:");

        jtxfGender.setEditable(false);
        jtxfGender.setMaximumSize(new java.awt.Dimension(30, 24));
        jtxfGender.setMinimumSize(new java.awt.Dimension(30, 24));
        jtxfGender.setPreferredSize(new java.awt.Dimension(30, 24));

        jLabel12.setText("Religion:");

        jtxfReligion.setMaximumSize(new java.awt.Dimension(292, 24));
        jtxfReligion.setMinimumSize(new java.awt.Dimension(292, 24));
        jtxfReligion.setPreferredSize(new java.awt.Dimension(292, 24));

        jLabel13.setText("Dialect:");
        jLabel13.setToolTipText("Dialect(s) Spoken");

        jtxfDialectSpoken.setMaximumSize(new java.awt.Dimension(252, 24));
        jtxfDialectSpoken.setMinimumSize(new java.awt.Dimension(252, 24));
        jtxfDialectSpoken.setPreferredSize(new java.awt.Dimension(252, 24));

        jLabel14.setText("Date of Birth:");

        jtxfDateOfBirth.setEditable(false);
        jtxfDateOfBirth.setMaximumSize(new java.awt.Dimension(100, 24));
        jtxfDateOfBirth.setMinimumSize(new java.awt.Dimension(100, 24));
        jtxfDateOfBirth.setPreferredSize(new java.awt.Dimension(100, 24));

        jLabel15.setText("Age:");

        jtxfAge.setEditable(false);
        jtxfAge.setMaximumSize(new java.awt.Dimension(40, 24));
        jtxfAge.setMinimumSize(new java.awt.Dimension(40, 24));
        jtxfAge.setPreferredSize(new java.awt.Dimension(40, 24));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));
        jPanel5.setMaximumSize(new java.awt.Dimension(720, 91));
        jPanel5.setMinimumSize(new java.awt.Dimension(720, 91));
        jPanel5.setPreferredSize(new java.awt.Dimension(720, 91));

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

        jtxfCityMunicipality.setMaximumSize(new java.awt.Dimension(190, 24));
        jtxfCityMunicipality.setMinimumSize(new java.awt.Dimension(190, 24));
        jtxfCityMunicipality.setPreferredSize(new java.awt.Dimension(190, 24));

        jLabel19.setText("City/Municipality");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jtxfHouseNo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jtxfStreet, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxfBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jtxfCityMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
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
                    .addComponent(jtxfCityMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel20.setText("Place of Birth:");

        jLabel21.setText("Name of Father:");

        jLabel22.setText("Mother's Maiden Name:");

        jtxfPlaceOfBirth.setEditable(false);
        jtxfPlaceOfBirth.setMaximumSize(new java.awt.Dimension(581, 24));
        jtxfPlaceOfBirth.setMinimumSize(new java.awt.Dimension(581, 24));
        jtxfPlaceOfBirth.setPreferredSize(new java.awt.Dimension(581, 24));

        jtxfFathersName.setMaximumSize(new java.awt.Dimension(200, 24));
        jtxfFathersName.setMinimumSize(new java.awt.Dimension(200, 24));
        jtxfFathersName.setPreferredSize(new java.awt.Dimension(200, 24));

        jtxfMothersName.setMaximumSize(new java.awt.Dimension(200, 24));
        jtxfMothersName.setMinimumSize(new java.awt.Dimension(200, 24));
        jtxfMothersName.setPreferredSize(new java.awt.Dimension(200, 24));

        jLabel23.setText("Name of Guardian:");

        jtxfGuardianName.setMaximumSize(new java.awt.Dimension(200, 24));
        jtxfGuardianName.setMinimumSize(new java.awt.Dimension(200, 24));
        jtxfGuardianName.setPreferredSize(new java.awt.Dimension(200, 24));

        jLabel24.setText("Relationship:");

        jcboGuardianRelationship.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Parent", "Relative", "Non-Relative" }));
        jcboGuardianRelationship.setMaximumSize(new java.awt.Dimension(101, 26));

        jLabel25.setText("Occupation:");

        jtxfFathersOccupation.setMaximumSize(new java.awt.Dimension(70, 24));
        jtxfFathersOccupation.setMinimumSize(new java.awt.Dimension(70, 24));
        jtxfFathersOccupation.setPreferredSize(new java.awt.Dimension(70, 24));

        jLabel26.setText("Cel.No./Tel.No.:");

        jtxfFathersContactNo.setMaximumSize(new java.awt.Dimension(136, 24));
        jtxfFathersContactNo.setMinimumSize(new java.awt.Dimension(136, 24));
        jtxfFathersContactNo.setPreferredSize(new java.awt.Dimension(136, 24));

        jLabel27.setText("Occupation:");

        jtxfMothersOccupation.setMaximumSize(new java.awt.Dimension(70, 24));
        jtxfMothersOccupation.setMinimumSize(new java.awt.Dimension(70, 24));
        jtxfMothersOccupation.setPreferredSize(new java.awt.Dimension(70, 24));

        jLabel28.setText("Cel.No./Tel.No.:");

        jtxfMothersContactNo.setMaximumSize(new java.awt.Dimension(136, 24));
        jtxfMothersContactNo.setMinimumSize(new java.awt.Dimension(136, 24));
        jtxfMothersContactNo.setPreferredSize(new java.awt.Dimension(136, 24));

        jLabel29.setText("Occupation:");

        jtxfGuadianOccupation.setMaximumSize(new java.awt.Dimension(70, 24));
        jtxfGuadianOccupation.setMinimumSize(new java.awt.Dimension(70, 24));
        jtxfGuadianOccupation.setPreferredSize(new java.awt.Dimension(70, 24));

        jLabel30.setText("Cel.No./Tel.No.:");

        jtxfGuardianContactNo.setMaximumSize(new java.awt.Dimension(136, 24));
        jtxfGuardianContactNo.setMinimumSize(new java.awt.Dimension(136, 24));
        jtxfGuardianContactNo.setPreferredSize(new java.awt.Dimension(136, 24));

        jLabel31.setText("Address:");

        jtxfGuardianAddress.setMaximumSize(new java.awt.Dimension(417, 24));
        jtxfGuardianAddress.setMinimumSize(new java.awt.Dimension(417, 24));
        jtxfGuardianAddress.setPreferredSize(new java.awt.Dimension(417, 24));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxfFirstname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jtxfReligion, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxfGender, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfDateOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfAge, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jtxfDialectSpoken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jcboGuardianRelationship, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfGuardianAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jtxfGuardianName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxfMothersName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxfFathersName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfFathersOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel29)
                                            .addComponent(jLabel27))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jtxfMothersOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jtxfGuadianOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel30)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfGuardianContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfFathersContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxfMothersContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jtxfPlaceOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel11)
                        .addComponent(jtxfGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(jtxfDateOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(jtxfAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jtxfReligion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jtxfDialectSpoken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jtxfPlaceOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jtxfFathersName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jtxfFathersOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jtxfFathersContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jtxfMothersName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jtxfMothersOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(jtxfMothersContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jtxfGuardianName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jtxfGuadianOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jtxfGuardianContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jcboGuardianRelationship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(jtxfGuardianAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Student Information", jPanel2);

        jLabel9.setText("School Last Attended:");

        jtxfSchoolLastAttended.setMaximumSize(new java.awt.Dimension(150, 24));
        jtxfSchoolLastAttended.setMinimumSize(new java.awt.Dimension(150, 24));
        jtxfSchoolLastAttended.setPreferredSize(new java.awt.Dimension(150, 24));

        jLabel10.setText("Former Year & Section:");

        jtxfFormerYearSection.setMaximumSize(new java.awt.Dimension(150, 24));
        jtxfFormerYearSection.setMinimumSize(new java.awt.Dimension(150, 24));
        jtxfFormerYearSection.setPreferredSize(new java.awt.Dimension(150, 24));

        jLabel32.setText("Address:");

        jtxfSchoolLastAttendedAddr.setMaximumSize(new java.awt.Dimension(263, 24));
        jtxfSchoolLastAttendedAddr.setMinimumSize(new java.awt.Dimension(263, 24));
        jtxfSchoolLastAttendedAddr.setPreferredSize(new java.awt.Dimension(263, 24));

        jLabel33.setText("Former Adviser:");

        jtxfFormerSY.setMaximumSize(new java.awt.Dimension(75, 24));
        jtxfFormerSY.setMinimumSize(new java.awt.Dimension(75, 24));
        jtxfFormerSY.setPreferredSize(new java.awt.Dimension(75, 24));

        jLabel34.setText("S.Y.:");

        jtxfFormerAdviser.setMaximumSize(new java.awt.Dimension(220, 24));
        jtxfFormerAdviser.setMinimumSize(new java.awt.Dimension(220, 24));
        jtxfFormerAdviser.setPreferredSize(new java.awt.Dimension(220, 24));

        jLabel35.setText("Average:");

        jtxfSchoolLastAttendedAverage.setMaximumSize(new java.awt.Dimension(45, 24));
        jtxfSchoolLastAttendedAverage.setMinimumSize(new java.awt.Dimension(45, 24));
        jtxfSchoolLastAttendedAverage.setPreferredSize(new java.awt.Dimension(45, 24));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Elementary School"));
        jPanel6.setMaximumSize(new java.awt.Dimension(720, 110));
        jPanel6.setMinimumSize(new java.awt.Dimension(720, 110));
        jPanel6.setPreferredSize(new java.awt.Dimension(720, 110));

        jLabel36.setText("School Name:");

        jtxfElemSchoolname.setEditable(false);
        jtxfElemSchoolname.setMaximumSize(new java.awt.Dimension(419, 24));
        jtxfElemSchoolname.setMinimumSize(new java.awt.Dimension(419, 24));
        jtxfElemSchoolname.setPreferredSize(new java.awt.Dimension(419, 24));

        jLabel37.setText("Yr. Grad.:");

        jtxfElemYearGrad.setEditable(false);
        jtxfElemYearGrad.setMaximumSize(new java.awt.Dimension(125, 24));
        jtxfElemYearGrad.setMinimumSize(new java.awt.Dimension(125, 24));
        jtxfElemYearGrad.setPreferredSize(new java.awt.Dimension(125, 24));

        jLabel38.setText("PEPT Passer:");

        jtxfElemPeptPasser.setEditable(false);
        jtxfElemPeptPasser.setMaximumSize(new java.awt.Dimension(36, 24));
        jtxfElemPeptPasser.setMinimumSize(new java.awt.Dimension(36, 24));
        jtxfElemPeptPasser.setPreferredSize(new java.awt.Dimension(36, 24));

        jLabel39.setText("Mnth/Yr Comp.:");

        jtxfElemPeptMnthYrComp.setEditable(false);
        jtxfElemPeptMnthYrComp.setMaximumSize(new java.awt.Dimension(133, 24));
        jtxfElemPeptMnthYrComp.setMinimumSize(new java.awt.Dimension(133, 24));
        jtxfElemPeptMnthYrComp.setPreferredSize(new java.awt.Dimension(133, 24));

        jLabel40.setText("A&E Passer:");

        jtxfElemAETestPasser.setEditable(false);
        jtxfElemAETestPasser.setMaximumSize(new java.awt.Dimension(36, 24));
        jtxfElemAETestPasser.setMinimumSize(new java.awt.Dimension(36, 24));
        jtxfElemAETestPasser.setPreferredSize(new java.awt.Dimension(36, 24));

        jLabel41.setText("Mnth/Yr Comp.:");

        jtxfElemAETestMnthYrComp.setEditable(false);
        jtxfElemAETestMnthYrComp.setMaximumSize(new java.awt.Dimension(125, 24));
        jtxfElemAETestMnthYrComp.setMinimumSize(new java.awt.Dimension(125, 24));
        jtxfElemAETestMnthYrComp.setPreferredSize(new java.awt.Dimension(125, 24));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jtxfElemPeptPasser, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfElemPeptMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfElemAETestPasser, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jtxfElemSchoolname, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtxfElemAETestMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxfElemYearGrad, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(jtxfElemSchoolname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(jtxfElemYearGrad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jtxfElemPeptPasser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(jtxfElemPeptMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40)
                    .addComponent(jtxfElemAETestPasser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(jtxfElemAETestMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Junior High School"));
        jPanel7.setMaximumSize(new java.awt.Dimension(720, 110));
        jPanel7.setMinimumSize(new java.awt.Dimension(720, 110));
        jPanel7.setPreferredSize(new java.awt.Dimension(720, 110));

        jLabel42.setText("School Name:");

        jtxfJHSSchoolname.setEditable(false);
        jtxfJHSSchoolname.setMaximumSize(new java.awt.Dimension(413, 24));
        jtxfJHSSchoolname.setMinimumSize(new java.awt.Dimension(413, 24));
        jtxfJHSSchoolname.setPreferredSize(new java.awt.Dimension(413, 24));

        jLabel43.setText("Yr. Comp.:");

        jtxfJHSYearComp.setEditable(false);
        jtxfJHSYearComp.setMaximumSize(new java.awt.Dimension(125, 24));
        jtxfJHSYearComp.setMinimumSize(new java.awt.Dimension(125, 24));
        jtxfJHSYearComp.setPreferredSize(new java.awt.Dimension(125, 24));

        jLabel44.setText("PEPT Passer:");

        jtxfJHSPeptPasser.setEditable(false);
        jtxfJHSPeptPasser.setMaximumSize(new java.awt.Dimension(36, 24));
        jtxfJHSPeptPasser.setMinimumSize(new java.awt.Dimension(36, 24));
        jtxfJHSPeptPasser.setPreferredSize(new java.awt.Dimension(36, 24));

        jLabel45.setText("Mnth/Yr Comp.:");

        jtxfJHSPeptMnthYrComp.setEditable(false);
        jtxfJHSPeptMnthYrComp.setMaximumSize(new java.awt.Dimension(133, 24));
        jtxfJHSPeptMnthYrComp.setMinimumSize(new java.awt.Dimension(133, 24));
        jtxfJHSPeptMnthYrComp.setPreferredSize(new java.awt.Dimension(133, 24));

        jLabel46.setText("A&E Passer:");

        jtxfJHSAETestPasser.setEditable(false);
        jtxfJHSAETestPasser.setMaximumSize(new java.awt.Dimension(36, 24));
        jtxfJHSAETestPasser.setMinimumSize(new java.awt.Dimension(36, 24));
        jtxfJHSAETestPasser.setPreferredSize(new java.awt.Dimension(36, 24));

        jLabel47.setText("Mnth/Yr Comp.:");

        jtxfJHSAETestMnthYrComp.setEditable(false);
        jtxfJHSAETestMnthYrComp.setMaximumSize(new java.awt.Dimension(125, 24));
        jtxfJHSAETestMnthYrComp.setMinimumSize(new java.awt.Dimension(125, 24));
        jtxfJHSAETestMnthYrComp.setPreferredSize(new java.awt.Dimension(125, 24));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jtxfJHSSchoolname, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfJHSYearComp, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jtxfJHSPeptPasser, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfJHSPeptMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfJHSAETestPasser, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxfJHSAETestMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(jtxfJHSSchoolname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43)
                    .addComponent(jtxfJHSYearComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jtxfJHSPeptPasser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45)
                    .addComponent(jtxfJHSPeptMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46)
                    .addComponent(jtxfJHSAETestPasser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47)
                    .addComponent(jtxfJHSAETestMnthYrComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Senior High School (SHS) Applied For:"));
        jPanel8.setMaximumSize(new java.awt.Dimension(720, 75));
        jPanel8.setMinimumSize(new java.awt.Dimension(720, 75));
        jPanel8.setPreferredSize(new java.awt.Dimension(720, 75));

        jLabel48.setText("Track:");

        jlblStrandSpecs.setText("Specialization:");

        jcboTrack.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Academic", "TVL", "Music & Arts", "Sports" }));
        jcboTrack.setMaximumSize(new java.awt.Dimension(200, 26));
        jcboTrack.setMinimumSize(new java.awt.Dimension(200, 26));
        jcboTrack.setPreferredSize(new java.awt.Dimension(200, 26));
        jcboTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboTrackActionPerformed(evt);
            }
        });

        jcboStrSpec.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STEM", "ABM", "HUMMS" }));
        jcboStrSpec.setMaximumSize(new java.awt.Dimension(311, 26));
        jcboStrSpec.setMinimumSize(new java.awt.Dimension(311, 26));
        jcboStrSpec.setPreferredSize(new java.awt.Dimension(311, 26));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboTrack, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblStrandSpecs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboStrSpec, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jcboTrack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblStrandSpecs)
                    .addComponent(jcboStrSpec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxfSchoolLastAttended, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxfFormerYearSection, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfFormerAdviser, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfFormerSY, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfSchoolLastAttendedAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfSchoolLastAttendedAverage, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jtxfSchoolLastAttended, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(jtxfSchoolLastAttendedAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jtxfSchoolLastAttendedAverage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jtxfFormerYearSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(jtxfFormerSY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(jtxfFormerAdviser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("School Information", jPanel4);

        jLabel50.setText("Class Adviser:");

        jLabel51.setText("Time:");

        jLabel52.setText("Subject:");

        jLabel53.setText("Subject Teacher:");

        jLabel54.setText("Room No.:");

        jbtnAddSubject.setText("Add");
        jbtnAddSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddSubjectActionPerformed(evt);
            }
        });

        jbtnRemoveSubject.setText("Remove");
        jbtnRemoveSubject.setEnabled(false);
        jbtnRemoveSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRemoveSubjectActionPerformed(evt);
            }
        });

        jtblSubjects.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Time", "Subject", "Subject Teacher", "Room No."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtblSubjects.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtblSubjects.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jtblSubjects);
        jtblSubjects.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jbtnUpdateSubject.setText("Update");
        jbtnUpdateSubject.setEnabled(false);
        jbtnUpdateSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpdateSubjectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxfTime, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                                    .addComponent(jtxfSubjectTeacher))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel54))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGap(17, 17, 17)
                                        .addComponent(jLabel52)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxfSubject)
                                    .addComponent(jtxfRoomNo)))
                            .addComponent(jtxfClassAdviser)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jbtnAddSubject)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnUpdateSubject)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnRemoveSubject)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnAddSubject, jbtnRemoveSubject, jbtnUpdateSubject});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(jtxfClassAdviser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(jtxfTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52)
                    .addComponent(jtxfSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(jtxfSubjectTeacher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54)
                    .addComponent(jtxfRoomNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnAddSubject)
                    .addComponent(jbtnRemoveSubject)
                    .addComponent(jbtnUpdateSubject))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Subjects", jPanel3);

        jbtnCancel.setMnemonic(KeyEvent.VK_C);
        jbtnCancel.setText("Cancel");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jbtnEnroll.setMnemonic(KeyEvent.VK_E);
        jbtnEnroll.setText("Enroll");
        jbtnEnroll.setEnabled(false);
        jbtnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEnrollActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnEnroll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnCancel)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnCancel, jbtnEnroll});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancel)
                    .addComponent(jbtnEnroll))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcboTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboTrackActionPerformed
        if(m_bFlagTrack == false) return;
        if(jcboTrack != null || jcboTrack.getItemCount() > 0) {
            jcboStrSpec.removeAllItems();
            LoadStrandAndSpecs();
        }
    }//GEN-LAST:event_jcboTrackActionPerformed

    private void jbtnAddSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddSubjectActionPerformed
        String sTime = jtxfTime.getText();
        if(sTime == null || sTime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify the subject time.", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfTime.grabFocus();
            return;
        }
        String sSubject = jtxfSubject.getText();
        if(sSubject == null || sSubject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify the subject.", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfSubject.grabFocus();
            return;
        }
        String sSubjectTeacher = jtxfSubjectTeacher.getText();
        if(sSubjectTeacher == null || sSubjectTeacher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify the subject teacher.", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfSubjectTeacher.grabFocus();
            return;
        }   
        String sRoomNo = jtxfRoomNo.getText();
        if(sRoomNo == null || sRoomNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify the room number.", 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfRoomNo.grabFocus();
            return;
        } 
        DefaultTableModel mdl = (DefaultTableModel)jtblSubjects.getModel();
        mdl.addRow(new Object[]{jtxfTime.getText(), jtxfSubject.getText(), 
            jtxfSubjectTeacher.getText(), jtxfRoomNo.getText()});
        jtxfTime.setText("");
        jtxfSubject.setText("");
        jtxfSubjectTeacher.setText("");
        jtxfRoomNo.setText("");
        
        jbtnUpdateSubject.setEnabled(false);
        jbtnRemoveSubject.setEnabled(false);
        jtblSubjects.clearSelection();
    }//GEN-LAST:event_jbtnAddSubjectActionPerformed

    private void jbtnRemoveSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRemoveSubjectActionPerformed
         DefaultTableModel mdl = (DefaultTableModel)jtblSubjects.getModel();
          for (int c : jtblSubjects.getSelectedRows()) {
            mdl.removeRow(c);
            jtxfTime.setText("");
            jtxfSubject.setText("");
            jtxfSubjectTeacher.setText("");
            jtxfRoomNo.setText("");
            jbtnUpdateSubject.setEnabled(false);
            jbtnRemoveSubject.setEnabled(false);
          }
          jtblSubjects.clearSelection();
    }//GEN-LAST:event_jbtnRemoveSubjectActionPerformed

    private void jbtnRetrieveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRetrieveActionPerformed
        LoadData();
        LoadDefaults();
    }//GEN-LAST:event_jbtnRetrieveActionPerformed

    private void jbtnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEnrollActionPerformed
        if(ValidateFields()) {
            if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm the enrollment",
                this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SPParam[] outParams = EnrollStudent();
                if (outParams != null) {
                    Integer nOut = new Integer(outParams[0].getParamValue().toString());
                    if(nOut != 0) {
                        String sErrMsg =  outParams[1].getParamValue().toString();
                        JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        DefaultTableModel mdl = (DefaultTableModel)jtblSubjects.getModel();
                        boolean bHasErrorInSubjects = false;
                        for(int ii = 0; ii < mdl.getRowCount(); ii++) {
                            SPParam[] outSubjParams = EnrollSubjects(mdl.getValueAt(ii, 0).toString(),
                                mdl.getValueAt(ii, 1).toString(), mdl.getValueAt(ii, 2).toString(),
                                mdl.getValueAt(ii, 3).toString());
                            
                            if(bHasErrorInSubjects == false) {
                                Integer nOutSubj = new Integer(outSubjParams[0].getParamValue().toString());
                                if(nOutSubj != 0) bHasErrorInSubjects = true;
                            }
                        }    
                        
                        if(bHasErrorInSubjects == false){
                            JOptionPane.showMessageDialog(this, "Enrollment Successfull!. This will exit the update screen.");
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "An error occured during subject enrollment. Please inform your facilitator.");                           
                        }
                        
                        this.dispose();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbtnEnrollActionPerformed

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Are you sure to cancel the enrollment?",
            this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_jbtnCancelActionPerformed

    private void jbtnUpdateSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpdateSubjectActionPerformed
        DefaultTableModel mdl = (DefaultTableModel)jtblSubjects.getModel();
        for (int r : jtblSubjects.getSelectedRows()) {
            mdl.setValueAt(jtxfTime.getText(), r, 0);
            mdl.setValueAt(jtxfSubject.getText(), r, 1);
            mdl.setValueAt(jtxfSubjectTeacher.getText(), r, 2);
            mdl.setValueAt(jtxfRoomNo.getText(), r, 3);
            jbtnUpdateSubject.setEnabled(false);
            jbtnRemoveSubject.setEnabled(false);
        }
        mdl.fireTableDataChanged();
        jtblSubjects.clearSelection();
    }//GEN-LAST:event_jbtnUpdateSubjectActionPerformed

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
            java.util.logging.Logger.getLogger(JEnrollStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JEnrollStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JEnrollStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JEnrollStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JEnrollStudent dialog = new JEnrollStudent(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton jbtnAddSubject;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnEnroll;
    private javax.swing.JButton jbtnRemoveSubject;
    private javax.swing.JButton jbtnRetrieve;
    private javax.swing.JButton jbtnUpdateSubject;
    private javax.swing.JComboBox<String> jcboGuardianRelationship;
    private javax.swing.JComboBox<String> jcboStrSpec;
    private javax.swing.JComboBox<String> jcboTrack;
    private javax.swing.JLabel jlblStrandSpecs;
    private javax.swing.JTable jtblSubjects;
    private javax.swing.JTextField jtxfAge;
    private javax.swing.JTextField jtxfBarangay;
    private javax.swing.JTextField jtxfCityMunicipality;
    private javax.swing.JTextField jtxfClassAdviser;
    private javax.swing.JTextField jtxfDateOfBirth;
    private javax.swing.JTextField jtxfDialectSpoken;
    private javax.swing.JTextField jtxfElemAETestMnthYrComp;
    private javax.swing.JTextField jtxfElemAETestPasser;
    private javax.swing.JTextField jtxfElemPeptMnthYrComp;
    private javax.swing.JTextField jtxfElemPeptPasser;
    private javax.swing.JTextField jtxfElemSchoolname;
    private javax.swing.JTextField jtxfElemYearGrad;
    private javax.swing.JTextField jtxfEnrollmentDate;
    private javax.swing.JTextField jtxfExamResultCurrent;
    private javax.swing.JTextField jtxfFathersContactNo;
    private javax.swing.JTextField jtxfFathersName;
    private javax.swing.JTextField jtxfFathersOccupation;
    private javax.swing.JTextField jtxfFirstname;
    private javax.swing.JTextField jtxfFormerAdviser;
    private javax.swing.JTextField jtxfFormerSY;
    private javax.swing.JTextField jtxfFormerYearSection;
    private javax.swing.JTextField jtxfGender;
    private javax.swing.JTextField jtxfGradeSectionCurrent;
    private javax.swing.JTextField jtxfGuadianOccupation;
    private javax.swing.JTextField jtxfGuardianAddress;
    private javax.swing.JTextField jtxfGuardianContactNo;
    private javax.swing.JTextField jtxfGuardianName;
    private javax.swing.JTextField jtxfHouseNo;
    private javax.swing.JTextField jtxfJHSAETestMnthYrComp;
    private javax.swing.JTextField jtxfJHSAETestPasser;
    private javax.swing.JTextField jtxfJHSPeptMnthYrComp;
    private javax.swing.JTextField jtxfJHSPeptPasser;
    private javax.swing.JTextField jtxfJHSSchoolname;
    private javax.swing.JTextField jtxfJHSYearComp;
    private javax.swing.JTextField jtxfLRN;
    private javax.swing.JTextField jtxfLastname;
    private javax.swing.JTextField jtxfMiddlename;
    private javax.swing.JTextField jtxfMothersContactNo;
    private javax.swing.JTextField jtxfMothersName;
    private javax.swing.JTextField jtxfMothersOccupation;
    private javax.swing.JTextField jtxfPlaceOfBirth;
    private javax.swing.JTextField jtxfReligion;
    private javax.swing.JTextField jtxfRoomNo;
    private javax.swing.JTextField jtxfSYCurrent;
    private javax.swing.JTextField jtxfSchoolLastAttended;
    private javax.swing.JTextField jtxfSchoolLastAttendedAddr;
    private javax.swing.JTextField jtxfSchoolLastAttendedAverage;
    private javax.swing.JTextField jtxfStreet;
    private javax.swing.JTextField jtxfSubject;
    private javax.swing.JTextField jtxfSubjectTeacher;
    private javax.swing.JTextField jtxfTime;
    // End of variables declaration//GEN-END:variables
}
