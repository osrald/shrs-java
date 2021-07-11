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
import java.awt.event.KeyEvent;

import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;

/**
 *
 * @author osraldo
 */
public class JUpdateStudentRegistration extends javax.swing.JDialog implements JICustomDialogEvents {
    private int m_nPrimaryKey;
    private boolean m_isLoadData = false;
    private boolean m_bFlagFirstChoiceSchool = false;
    private boolean m_bFlagFirstChoiceTrack = false;
    private String m_sSchoolFirstChoice_others_nm = "";
    private String m_sSchoolFirstChoice_others_addr = "";
    private String m_sSchoolSecondChoice_others_nm = "";
    private String m_sSchoolSecondChoice_others_addr = "";
    /**
     * Creates new form JUpdateStudentRegistration
     */
    public JUpdateStudentRegistration(javax.swing.JDialog parent, boolean modal, int nPrimaryKey) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        this.setTitle("Update Student Registration");
        m_nPrimaryKey = nPrimaryKey;
        
        InitializeCombos();
        LoadSchools();
        LoadFirstChoiceTracks();
        LoadFirstChoiceStrandAndSpecs();
        
        m_bFlagFirstChoiceSchool = true;
        m_bFlagFirstChoiceTrack = true;
        
        LoadData(nPrimaryKey);
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
    
    private void LoadData(int nPrimaryKey) {
        m_isLoadData = true;
        DBManager dbm = new DBManager();
        ResultSet rsSD  = null;
        int nRowCnt = 0;
        String sTbls = "students st ";
        sTbls += "join configs fcsc on st.sd_shs_school_firstchoice_cd = fcsc.cf_minor and fcsc.cf_major = 'SCH' ";
        sTbls += "join configs fct on st.sd_shs_track_firstchoice_cd = fct.cf_minor and fct.cf_major = 'TRK' and ";
        sTbls += "fct.cf_general_2 = st.sd_shs_school_firstchoice_cd ";
        sTbls += "join configs fcs on st.sd_shs_strspec_firstchoice_cd = fcs.cf_minor and ";
        sTbls += "fcs.cf_major = 'SAS' and fcs.cf_general_1 = st.sd_shs_track_firstchoice_cd and ";
        sTbls += "fcs.cf_general_2 = st.sd_shs_school_firstchoice_cd ";
        sTbls += "left join configs scsc on st.sd_shs_school_secondchoice_cd = scsc.cf_minor and scsc.cf_major = 'SCH' ";
        sTbls += "left join configs sct on st.sd_shs_track_secondchoice_cd = sct.cf_minor and sct.cf_major = 'TRK' and ";
        sTbls += "sct.cf_general_2 = st.sd_shs_school_secondchoice_cd ";
        sTbls += "left join configs scs on st.sd_shs_strspec_secondchoice_cd = scs.cf_minor and scs.cf_major = 'SAS' and ";
        sTbls += "scs.cf_general_1 = st.sd_shs_track_secondchoice_cd and scs.cf_general_2 = st.sd_shs_school_secondchoice_cd";

        String sCols = "st.sd_lastname,st.sd_firstname,st.sd_middlename,";
        sCols += "concat(st.sd_shs_school_firstchoice_cd, ' - ', fcsc.cf_name) as shs_school_firstchoice,";
        sCols += "st.sd_shs_school_firstchoice_others_nm,st.sd_shs_school_firstchoice_others_addr,";
        sCols += "concat(st.sd_shs_track_firstchoice_cd, ' - ', fct.cf_name) as shs_track_firstchoice,";
        sCols += "concat(st.sd_shs_strspec_firstchoice_cd, ' - ', fcs.cf_name) as shs_strspec_firstchoice,";
        sCols += "concat(st.sd_shs_school_secondchoice_cd, ' - ', scsc.cf_name) as shs_school_secondchoice,";
        sCols += "st.sd_shs_school_secondchoice_others_nm,st.sd_shs_school_secondchoice_others_addr,";
        sCols += "concat(st.sd_shs_track_secondchoice_cd, ' - ', sct.cf_name) as shs_track_secondchoice,";
        sCols += "concat(st.sd_shs_strspec_secondchoice_cd, ' - ', scs.cf_name) as shs_strspec_secondchoice";

        try {
            rsSD = dbm.getResultSet(sTbls, sCols, "sd_primary_key = " + nPrimaryKey, "");
            rsSD.last();
            nRowCnt = rsSD.getRow();
            rsSD.beforeFirst();
            if (nRowCnt > 0) {
                rsSD.next();
                jtxfLastname.setText(rsSD.getString("sd_lastname"));
                jtxfFirstname.setText(rsSD.getString("sd_firstname"));
                jtxfMiddlename.setText(rsSD.getString("sd_middlename"));
                m_sSchoolFirstChoice_others_nm = rsSD.getString("sd_shs_school_firstchoice_others_nm");
                m_sSchoolFirstChoice_others_addr = rsSD.getString("sd_shs_school_firstchoice_others_addr");
                jcboFirstChoiceSchool.setSelectedItem(rsSD.getString("shs_school_firstchoice"));
                jcboFirstChoiceTrack.setSelectedItem(rsSD.getString("shs_track_firstchoice"));
                jcboFirstChoiceStrandSpecs.setSelectedItem(rsSD.getString("shs_strspec_firstchoice"));
                
                String sSCSchool = rsSD.getString("shs_school_secondchoice");
                if(sSCSchool == null || sSCSchool.isEmpty()) {
                    m_sSchoolSecondChoice_others_nm = "";
                    m_sSchoolSecondChoice_others_addr = "";
                    jcboSecondChoiceSchool.setSelectedIndex(0);
                }
                else {
                    jcboSecondChoiceSchool.setSelectedItem(rsSD.getString("shs_school_secondchoice"));
                    m_sSchoolSecondChoice_others_nm = rsSD.getString("sd_shs_school_secondchoice_others_nm");
                    m_sSchoolSecondChoice_others_addr = rsSD.getString("sd_shs_school_secondchoice_others_addr");
                    jcboSecondChoiceTrack.setSelectedItem(rsSD.getString("shs_track_secondchoice"));
                    jcboSecondChoiceStrandSpecs.setSelectedItem(rsSD.getString("shs_strspec_secondchoice"));
                }
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
        m_isLoadData = false;
    }
    
    private void InitializeCombos() {
        jcboFirstChoiceSchool.removeAllItems();
        jcboSecondChoiceSchool.removeAllItems();
        
        jcboFirstChoiceSchool.removeAllItems();
        jcboFirstChoiceTrack.removeAllItems();
        jcboFirstChoiceStrandSpecs.removeAllItems();
        
        jcboSecondChoiceSchool.removeAllItems();
        jcboSecondChoiceTrack.removeAllItems();
        jcboSecondChoiceStrandSpecs.removeAllItems();
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
    
    private boolean ValidateFields() {
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

        return true;
    }
    
    private SPParam[] UpdateStudentRegistration() {
        SPParam[] arrParams = new SPParam[13];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            m_nPrimaryKey);
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psd_shs_school_firstchoice_cd", 
            ((String)jcboFirstChoiceSchool.getSelectedItem()).substring(0, 3));
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psd_shs_school_firstchoice_others_nm", 
            m_sSchoolFirstChoice_others_nm);  
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psd_shs_school_firstchoice_others_addr", 
            m_sSchoolFirstChoice_others_addr);
        
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psd_shs_track_firstchoice_cd", 
            ((String)jcboFirstChoiceTrack.getSelectedItem()).substring(0, 3));
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "psd_shs_strspec_firstchoice_cd", 
            ((String)jcboFirstChoiceStrandSpecs.getSelectedItem()).substring(0, 3));
        
        if (!"---".equalsIgnoreCase(((String)jcboSecondChoiceSchool.getSelectedItem()).substring(0, 3))) {
            arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psd_shs_school_secondchoice_cd", 
                ((String)jcboSecondChoiceSchool.getSelectedItem()).substring(0, 3));
            arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psd_shs_school_secondchoice_others_nm", 
                m_sSchoolSecondChoice_others_nm);  
            arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "psd_shs_school_secondchoice_others_addr", 
                m_sSchoolSecondChoice_others_addr);        

            arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 10, "psd_shs_track_secondchoice_cd",
                ((String)jcboSecondChoiceTrack.getSelectedItem()).substring(0, 3));           
            arrParams[10] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 11, "psd_shs_strspec_secondchoice_cd", 
                 ((String)jcboSecondChoiceStrandSpecs.getSelectedItem()).substring(0, 3));
        }
        else {
            arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psd_shs_school_secondchoice_cd", "");
            arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psd_shs_school_secondchoice_others_nm", "");  
            arrParams[8] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 9, "psd_shs_school_secondchoice_others_addr", "");        
            arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 10, "psd_shs_track_secondchoice_cd", "");           
            arrParams[10] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 11, "psd_shs_strspec_secondchoice_cd", "");           
        }

        arrParams[11] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 12, "psd_flag", 0);
        arrParams[12] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 13, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_UpdateStudentRegistration", arrParams);
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
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlblFirstChoiceStrandSpecs = new javax.swing.JLabel();
        jcboFirstChoiceSchool = new javax.swing.JComboBox<>();
        jcboFirstChoiceTrack = new javax.swing.JComboBox<>();
        jcboFirstChoiceStrandSpecs = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jlblSecondChoiceStrandSpecs = new javax.swing.JLabel();
        jcboSecondChoiceSchool = new javax.swing.JComboBox<>();
        jcboSecondChoiceTrack = new javax.swing.JComboBox<>();
        jcboSecondChoiceStrandSpecs = new javax.swing.JComboBox<>();
        jbtnCancel = new javax.swing.JButton();
        jbtnUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Student Information"));

        jLabel1.setText("Lastname:");

        jtxfLastname.setEditable(false);

        jLabel2.setText("Firstname:");

        jtxfFirstname.setEditable(false);

        jLabel3.setText("Middlename:");

        jtxfMiddlename.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxfFirstname, jtxfLastname, jtxfMiddlename});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jtxfLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2)
                .addComponent(jtxfFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel3)
                .addComponent(jtxfMiddlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Senior High School (SHS) Applied For:"));
        jPanel2.setToolTipText("");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("First Choice:"));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("School:");
        jLabel4.setMaximumSize(new java.awt.Dimension(82, 16));
        jLabel4.setMinimumSize(new java.awt.Dimension(82, 16));
        jLabel4.setPreferredSize(new java.awt.Dimension(82, 16));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Track:");
        jLabel5.setMaximumSize(new java.awt.Dimension(82, 16));
        jLabel5.setMinimumSize(new java.awt.Dimension(82, 16));
        jLabel5.setPreferredSize(new java.awt.Dimension(82, 16));

        jlblFirstChoiceStrandSpecs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblFirstChoiceStrandSpecs.setText("Specialization:");

        jcboFirstChoiceSchool.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Makati High School", "Makati Science High School", "Fort Bonifacio High School" }));
        jcboFirstChoiceSchool.setMaximumSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceSchool.setMinimumSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceSchool.setPreferredSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceSchool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboFirstChoiceSchoolActionPerformed(evt);
            }
        });

        jcboFirstChoiceTrack.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Academic", "TVL", "Music & Arts", "Sports" }));
        jcboFirstChoiceTrack.setMaximumSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceTrack.setMinimumSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceTrack.setPreferredSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboFirstChoiceTrackActionPerformed(evt);
            }
        });

        jcboFirstChoiceStrandSpecs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STEM", "ABM", "HUMMS" }));
        jcboFirstChoiceStrandSpecs.setMaximumSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceStrandSpecs.setMinimumSize(new java.awt.Dimension(285, 26));
        jcboFirstChoiceStrandSpecs.setPreferredSize(new java.awt.Dimension(285, 26));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlblFirstChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jcboFirstChoiceTrack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboFirstChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboFirstChoiceSchool, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboFirstChoiceSchool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboFirstChoiceTrack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblFirstChoiceStrandSpecs)
                    .addComponent(jcboFirstChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Second Choice:"));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("School:");
        jLabel7.setMaximumSize(new java.awt.Dimension(82, 16));
        jLabel7.setMinimumSize(new java.awt.Dimension(82, 16));
        jLabel7.setPreferredSize(new java.awt.Dimension(82, 16));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Track:");
        jLabel8.setMaximumSize(new java.awt.Dimension(82, 16));
        jLabel8.setMinimumSize(new java.awt.Dimension(82, 16));
        jLabel8.setPreferredSize(new java.awt.Dimension(82, 16));

        jlblSecondChoiceStrandSpecs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblSecondChoiceStrandSpecs.setText("Specialization:");

        jcboSecondChoiceSchool.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Makati High School", "Makati Science High School", "Fort Bonifacio High School" }));
        jcboSecondChoiceSchool.setMaximumSize(new java.awt.Dimension(285, 26));
        jcboSecondChoiceSchool.setMinimumSize(new java.awt.Dimension(285, 26));
        jcboSecondChoiceSchool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboSecondChoiceSchoolActionPerformed(evt);
            }
        });

        jcboSecondChoiceTrack.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Academic", "TVL", "Music & Arts", "Sports" }));
        jcboSecondChoiceTrack.setEnabled(false);
        jcboSecondChoiceTrack.setMaximumSize(new java.awt.Dimension(285, 26));
        jcboSecondChoiceTrack.setMinimumSize(new java.awt.Dimension(285, 26));
        jcboSecondChoiceTrack.setPreferredSize(new java.awt.Dimension(285, 26));
        jcboSecondChoiceTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboSecondChoiceTrackActionPerformed(evt);
            }
        });

        jcboSecondChoiceStrandSpecs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STEM", "ABM", "HUMMS" }));
        jcboSecondChoiceStrandSpecs.setEnabled(false);
        jcboSecondChoiceStrandSpecs.setMaximumSize(new java.awt.Dimension(285, 26));
        jcboSecondChoiceStrandSpecs.setMinimumSize(new java.awt.Dimension(285, 26));
        jcboSecondChoiceStrandSpecs.setPreferredSize(new java.awt.Dimension(285, 26));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlblSecondChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboSecondChoiceSchool, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboSecondChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboSecondChoiceTrack, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboSecondChoiceSchool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboSecondChoiceTrack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblSecondChoiceStrandSpecs)
                    .addComponent(jcboSecondChoiceStrandSpecs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnUpdate)
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
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancel)
                    .addComponent(jbtnUpdate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCancelActionPerformed

    private void jbtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpdateActionPerformed
        if(ValidateFields()) {
            if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm your registration",
                this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SPParam[] outParams = UpdateStudentRegistration();
                if (outParams != null) {
                    Integer nOut = new Integer(outParams[0].getParamValue().toString());
                    if(nOut != 0) {
                        String sErrMsg =  outParams[1].getParamValue().toString();
                        JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Registration Update Successfull!. This will exit the update screen.");
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

    private void jcboFirstChoiceSchoolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboFirstChoiceSchoolActionPerformed
        if (m_bFlagFirstChoiceSchool == false) return;
        if(jcboFirstChoiceTrack != null || jcboFirstChoiceTrack.getItemCount() > 0) {
            jcboFirstChoiceTrack.removeAllItems();
            LoadFirstChoiceTracks();
        }
        if("999 - Other Schools".equalsIgnoreCase((String)jcboFirstChoiceSchool.getSelectedItem())) {
           if(m_isLoadData) return;
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
                if(!m_isLoadData) {
                    JOtherSchools dlg = new JOtherSchools(this, this, jcboSecondChoiceSchool, "SecondChoice", 
                         m_sSchoolSecondChoice_others_nm, m_sSchoolSecondChoice_others_addr, true);
                    dlg.setVisible(true);
                }
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
            java.util.logging.Logger.getLogger(JUpdateStudentRegistration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JUpdateStudentRegistration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JUpdateStudentRegistration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JUpdateStudentRegistration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JUpdateStudentRegistration dialog = new JUpdateStudentRegistration(new javax.swing.JDialog(), true, 0);
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnUpdate;
    private javax.swing.JComboBox<String> jcboFirstChoiceSchool;
    private javax.swing.JComboBox<String> jcboFirstChoiceStrandSpecs;
    private javax.swing.JComboBox<String> jcboFirstChoiceTrack;
    private javax.swing.JComboBox<String> jcboSecondChoiceSchool;
    private javax.swing.JComboBox<String> jcboSecondChoiceStrandSpecs;
    private javax.swing.JComboBox<String> jcboSecondChoiceTrack;
    private javax.swing.JLabel jlblFirstChoiceStrandSpecs;
    private javax.swing.JLabel jlblSecondChoiceStrandSpecs;
    private javax.swing.JTextField jtxfFirstname;
    private javax.swing.JTextField jtxfLastname;
    private javax.swing.JTextField jtxfMiddlename;
    // End of variables declaration//GEN-END:variables
}
