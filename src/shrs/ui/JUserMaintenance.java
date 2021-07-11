/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.ui;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
import shrs.utility.FieldValidator;
import shrs.utility.HashCode;
/**
 *
 * @author osraldo
 */
public class JUserMaintenance extends javax.swing.JDialog {
    private Integer nPrimaryKey;
    /**
     * Creates new form JUserMaintenance
     */
    public JUserMaintenance(java.awt.Frame parent, boolean modal) {
        super(parent, "System User Administration", modal);
        initComponents();
        
        setLocationRelativeTo(parent);
        
        jtblSystemUsers.setModel(new SystemUserTableModel());
        jtblSystemUsers.getTableHeader().setReorderingAllowed(false);
        jtblSystemUsers.getSelectionModel().addListSelectionListener(new SystemUsersTableRowListener());
        
        PopulateComboBox();
        SetTableWidth();
    }
    
    private void SetTableWidth() {
        TableColumn tc = jtblSystemUsers.getColumnModel().getColumn(0);
        tc.setMinWidth(0);
        tc.setMaxWidth(0);
        tc = jtblSystemUsers.getColumnModel().getColumn(1);
        tc.setMinWidth(15);
        tc.setPreferredWidth(75);
        tc.setMaxWidth(100);
        tc = jtblSystemUsers.getColumnModel().getColumn(2);
        tc.setMinWidth(0);
        tc.setMaxWidth(0);
        tc = jtblSystemUsers.getColumnModel().getColumn(3);
        tc.setMinWidth(15);
        tc.setPreferredWidth(200);
        tc.setMaxWidth(250);
        tc = jtblSystemUsers.getColumnModel().getColumn(4);
        tc.setMinWidth(15);
        tc.setPreferredWidth(75);
        tc.setMaxWidth(100);
        tc = jtblSystemUsers.getColumnModel().getColumn(5);
        tc.setMinWidth(15);
        tc.setPreferredWidth(75);
        tc.setMaxWidth(100);
    }
    
    private void PopulateComboBox() {
        jcboDesignation.removeAllItems();
        DBManager dbm = new DBManager();
        try {
            ResultSet rs = dbm.getResultSet("configs", "cf_name", "cf_major = 'DSG'", "cf_minor asc");
            while(rs.next()) {
                jcboDesignation.addItem(rs.getString("cf_name"));
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    private void ClearDataControls() {
        jtblSystemUsers.getSelectionModel().clearSelection();
        jtxfUsername.setText("");
        jtxfUsersFullname.setText("");
        jpwfPassword.setText("");
        jcboDesignation.setSelectedIndex(-1);
        jcboAccessLevel.setSelectedIndex(-1);
    }
    
    private void SetEditableDataControls(boolean bEnabled) {
        jtxfUsername.setEditable(bEnabled);
        jpwfPassword.setEditable(bEnabled);
        jtxfUsersFullname.setEditable(bEnabled);
        jcboDesignation.setEnabled(bEnabled);
        jcboAccessLevel.setEnabled(bEnabled);
    }
    
    private void SetEditableDataControlsForUpdate(boolean bEnabled) {
        jtxfUsersFullname.setEditable(bEnabled);
        jcboDesignation.setEnabled(bEnabled);
        jcboAccessLevel.setEnabled(bEnabled);
    }
    
   private boolean ValidateFields() {
        FieldValidator fv = new FieldValidator(jtxfUsername.getText(), "Username");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfUsername.grabFocus();
            return fv.isValidated;
        }
        
        String pw = new String(jpwfPassword.getPassword());
        fv = new FieldValidator(pw, "Password");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jpwfPassword.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfUsersFullname.getText(), "User's Fullname");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfUsersFullname.grabFocus();
            return fv.isValidated;
        }
        
        String sDesig = (String)jcboDesignation.getSelectedItem();
        fv = new FieldValidator(sDesig, "Designation");
        fv.setRequired(true);
        fv.doValidation();
        if (fv.isValidated == false) {
            JOptionPane.showMessageDialog(this,
                fv.getValidationMessage(),
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jcboDesignation.grabFocus();
            return fv.isValidated;
        }
        
        String sAccessLvl = (String)jcboAccessLevel.getSelectedItem();
        fv = new FieldValidator(sAccessLvl, "Access Level");
        fv.setRequired(true);
        fv.doValidation();
        if (fv.isValidated == false) {
            JOptionPane.showMessageDialog(this,
                fv.getValidationMessage(),
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jcboAccessLevel.grabFocus();
            return fv.isValidated;
        }
        
        return true;
    }
   
    private SPParam[] AddNewSystemUser() {
        SPParam[] arrParams = new SPParam[7];
        arrParams[0] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 1, "psu_username", 
            jtxfUsername.getText());
        String sPassword = new String(jpwfPassword.getPassword());
        try{sPassword = HashCode.md5(sPassword);}catch(NoSuchAlgorithmException nex){}
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psu_password", 
            sPassword);
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psu_fullname", 
            jtxfUsersFullname.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psu_designation", 
            (String)jcboDesignation.getSelectedItem());
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psu_access_level", 
            (String)jcboAccessLevel.getSelectedItem());

        arrParams[5] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 6, "psu_flag", 0);
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 7, "psu_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_SystemUserAdd", arrParams);        
    }
    
    private SPParam[] UpdateSystemUser() {
        SPParam[] arrParams = new SPParam[6];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            nPrimaryKey.intValue());
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psu_fullname", 
            jtxfUsersFullname.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psu_designation", 
            (String)jcboDesignation.getSelectedItem());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psu_access_level", 
            (String)jcboAccessLevel.getSelectedItem());

        arrParams[4] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 5, "psu_flag", 0);
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 6, "psu_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_SystemUserUpdate", arrParams);        
    }
   
    private class SystemUsersTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting()) {
                return;
            }
            for (int c : jtblSystemUsers.getSelectedRows()) {
                jbtnUpdateSave.setEnabled(true);
                nPrimaryKey = new Integer(jtblSystemUsers.getValueAt(c, 0).toString());
                jtxfUsername.setText(jtblSystemUsers.getValueAt(c, 1).toString());
                jpwfPassword.setText(jtblSystemUsers.getValueAt(c, 2).toString());
                jtxfUsersFullname.setText(jtblSystemUsers.getValueAt(c, 3).toString());
                jcboDesignation.setSelectedItem(jtblSystemUsers.getValueAt(c, 4).toString());
                jcboAccessLevel.setSelectedItem(jtblSystemUsers.getValueAt(c, 5).toString());
            }
        }
    }

    class SystemUserTableModel extends AbstractTableModel {
        String[] columnNames = { 
            "su_primary_key", "Username", "su_password", "User's Fullname", 
            "Designation", "Access Level"
        };
        Object[][] data = new Object[0][0];
        
        SystemUserTableModel() {
            super();
            reloadTableData();
        }
        
        public void reloadTableData() {
            DBManager dbm = new DBManager();
            ResultSet rsUsers  = null;
            int nRowCnt = 0;
            String sTbl = "system_users as su join configs as cg on ";
            sTbl += "su.su_designation_cd = cg.cf_minor and cg.cf_major = 'DSG'";
            String sCols = "su_primary_key,su_username,su_password,su_fullname,";
            sCols += "cg.cf_name as su_designation, su_access_level";
                        
            try {
                rsUsers = dbm.getResultSet(sTbl, sCols, "", "");
                rsUsers.last();
                nRowCnt = rsUsers.getRow();
                rsUsers.beforeFirst();
                if (nRowCnt > 0) {
                    data = new Object[nRowCnt][6];
                    int nCtr = 0;
                    while(rsUsers.next()) {
                        data[nCtr][0] = rsUsers.getString("su_primary_key");
                        data[nCtr][1] = rsUsers.getString("su_username");
                        data[nCtr][2] = rsUsers.getString("su_password");
                        data[nCtr][3] = rsUsers.getString("su_fullname");
                        data[nCtr][4] = rsUsers.getString("su_designation");
                        data[nCtr][5] = rsUsers.getString("su_access_level");

                        nCtr++;
                    }
                }
            } 
            catch(SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
            finally	{
                if (rsUsers != null) {
                    try {
                        rsUsers.close();
                    } 
                    catch(SQLException sqlEx){}//ignore
                    rsUsers = null;
                }
            }
        }
        
         @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        //JTable used this to align Integers etc.
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jtblSystemUsers = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxfUsername = new javax.swing.JTextField();
        jpwfPassword = new javax.swing.JPasswordField();
        jtxfUsersFullname = new javax.swing.JTextField();
        jcboDesignation = new javax.swing.JComboBox<>();
        jcboAccessLevel = new javax.swing.JComboBox<>();
        jbtnExitCancel = new javax.swing.JButton();
        jbtnUpdateSave = new javax.swing.JButton();
        jbtnNewAdd = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jtblSystemUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jtblSystemUsers);

        jLabel1.setText("Username:");

        jLabel2.setText("Password:");

        jLabel3.setText("User's Fullname:");

        jLabel4.setText("Designation:");

        jLabel5.setText("Access Level:");

        jtxfUsername.setEditable(false);

        jpwfPassword.setEditable(false);

        jtxfUsersFullname.setEditable(false);

        jcboDesignation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Guidance Councilor", "School Admin", "Working Student", "Teacher" }));
        jcboDesignation.setEnabled(false);

        jcboAccessLevel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Level 1", "Level 2", "Level 3" }));
        jcboAccessLevel.setEnabled(false);

        jbtnExitCancel.setText("Exit");
        jbtnExitCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExitCancelActionPerformed(evt);
            }
        });

        jbtnUpdateSave.setText("Update");
        jbtnUpdateSave.setEnabled(false);
        jbtnUpdateSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpdateSaveActionPerformed(evt);
            }
        });

        jbtnNewAdd.setText("New");
        jbtnNewAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnNewAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtnNewAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnUpdateSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnExitCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcboAccessLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcboDesignation, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxfUsersFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpwfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxfUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnExitCancel, jbtnNewAdd, jbtnUpdateSave});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jcboAccessLevel, jcboDesignation, jpwfPassword, jtxfUsername, jtxfUsersFullname});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxfUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jpwfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtxfUsersFullname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jcboDesignation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jcboAccessLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnExitCancel)
                    .addComponent(jbtnUpdateSave)
                    .addComponent(jbtnNewAdd))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnNewAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNewAddActionPerformed
        if(jbtnNewAdd.getText() == "New") {
            jtblSystemUsers.setEnabled(false);
            jbtnNewAdd.setText("Add");
            jbtnUpdateSave.setEnabled(false);
            jbtnExitCancel.setText("Cancel");
            jtxfUsername.grabFocus();
            ClearDataControls();
            SetEditableDataControls(true);
        }
        else {
            if (ValidateFields()) {
                if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm",
                    this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    SPParam[] outParams = AddNewSystemUser();
                    if (outParams != null) {
                        Integer nOut = new Integer(outParams[0].getParamValue().toString());
                        if(nOut != 0) {
                            String sErrMsg =  outParams[1].getParamValue().toString();
                            JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "New System User Successfully Added!");
                            jtblSystemUsers.setEnabled(true);
                            jbtnNewAdd.setText("New");
                            jbtnExitCancel.setText("Exit");
                            ClearDataControls();
                            SetEditableDataControls(false);
                            
                            ((SystemUserTableModel)jtblSystemUsers.getModel()).reloadTableData();
                            ((SystemUserTableModel)jtblSystemUsers.getModel()).fireTableDataChanged();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                        this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }//GEN-LAST:event_jbtnNewAddActionPerformed

    private void jbtnUpdateSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpdateSaveActionPerformed
        if(jbtnUpdateSave.getText() == "Update") {
            jtblSystemUsers.setEnabled(false);
            jbtnUpdateSave.setText("Save");
            jbtnNewAdd.setEnabled(false);
            jbtnExitCancel.setText("Cancel");
            SetEditableDataControlsForUpdate(true);
        }
        else {
            if (ValidateFields()) {
                if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm",
                    this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    SPParam[] outParams = UpdateSystemUser();
                    if (outParams != null) {
                        Integer nOut = new Integer(outParams[0].getParamValue().toString());
                        if(nOut != 0) {
                            String sErrMsg =  outParams[1].getParamValue().toString();
                            JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "System User Successfully Updated!");
                            jtblSystemUsers.setEnabled(true);
                            jbtnUpdateSave.setEnabled(false);
                            jbtnUpdateSave.setText("Update");
                            jbtnNewAdd.setEnabled(true);
                            jbtnExitCancel.setText("Exit");
                            ClearDataControls();
                            SetEditableDataControls(false);
                            
                            ((SystemUserTableModel)jtblSystemUsers.getModel()).reloadTableData();
                            ((SystemUserTableModel)jtblSystemUsers.getModel()).fireTableDataChanged();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                        this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }//GEN-LAST:event_jbtnUpdateSaveActionPerformed

    private void jbtnExitCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitCancelActionPerformed
        if(jbtnExitCancel.getText() == "Cancel") {
            jtblSystemUsers.setEnabled(true);
            jbtnNewAdd.setEnabled(true);
            jbtnNewAdd.setText("New");
            jbtnUpdateSave.setEnabled(false);
            jbtnUpdateSave.setText("Update");
            jbtnExitCancel.setText("Exit");
            ClearDataControls();
            SetEditableDataControls(false);
        }
        else {
            this.dispose();
        }
    }//GEN-LAST:event_jbtnExitCancelActionPerformed

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
            java.util.logging.Logger.getLogger(JUserMaintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JUserMaintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JUserMaintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JUserMaintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JUserMaintenance dialog = new JUserMaintenance(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnExitCancel;
    private javax.swing.JButton jbtnNewAdd;
    private javax.swing.JButton jbtnUpdateSave;
    private javax.swing.JComboBox<String> jcboAccessLevel;
    private javax.swing.JComboBox<String> jcboDesignation;
    private javax.swing.JPasswordField jpwfPassword;
    private javax.swing.JTable jtblSystemUsers;
    private javax.swing.JTextField jtxfUsername;
    private javax.swing.JTextField jtxfUsersFullname;
    // End of variables declaration//GEN-END:variables
}
