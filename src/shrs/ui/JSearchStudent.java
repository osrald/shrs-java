/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.ui;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.event.KeyEvent;
import java.sql.Types;

import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
/**
 *
 * @author osraldo
 */
public class JSearchStudent extends javax.swing.JDialog {
    private int nPrimaryKey;
    private String m_sUserAccessLevel;
    
    /**
     * Creates new form JSearchStudent
     */
    public JSearchStudent(java.awt.Frame parent, boolean modal, String sUserAccessLevel) {
        super(parent, modal);
        initComponents();
        
        this.setLocationRelativeTo(parent);
        this.setTitle("Search Student");
        
        m_sUserAccessLevel = sUserAccessLevel;
        jtblSearch.setModel(new StudentTableModel()); 
        SetAdditionalTableConfig();
        jtblSearch.getSelectionModel().addListSelectionListener(new StudentTableRowListener());
    }
    
    private void SetAdditionalTableConfig() {
        TableColumn tc = jtblSearch.getColumnModel().getColumn(0);
        tc.setMinWidth(0);
        tc.setMaxWidth(0);
        tc = jtblSearch.getColumnModel().getColumn(1);
        tc.setMinWidth(15);
        tc.setPreferredWidth(75);
        tc.setMaxWidth(100);
        tc = jtblSearch.getColumnModel().getColumn(2);
        tc.setMinWidth(15);
        tc.setPreferredWidth(75);
        tc.setMaxWidth(100);
        tc = jtblSearch.getColumnModel().getColumn(3);
        tc.setMinWidth(15);
        tc.setPreferredWidth(75);
        tc.setMaxWidth(100);
        tc = jtblSearch.getColumnModel().getColumn(4);
        tc.setMinWidth(15);
        tc.setPreferredWidth(50);
        tc.setMaxWidth(255);
        tc = jtblSearch.getColumnModel().getColumn(5);
        tc.setMinWidth(15);
        tc.setPreferredWidth(45);
        tc.setMaxWidth(75);
        tc = jtblSearch.getColumnModel().getColumn(6);
        tc.setMinWidth(15);
        tc.setPreferredWidth(75);
        tc.setMaxWidth(255);
        tc = jtblSearch.getColumnModel().getColumn(7);
        tc.setMinWidth(15);
        tc.setPreferredWidth(45);
        tc.setMaxWidth(75);
        
        jtblSearch.getTableHeader().setReorderingAllowed(false);
    }
    
    private boolean hasDeleteRights() {
        if("Level 2".equalsIgnoreCase(m_sUserAccessLevel) || "Level 3".equalsIgnoreCase(m_sUserAccessLevel))
            return true;
        else
            return false;
    }
    
    private SPParam[] DeleteStudent() {
        SPParam[] arrParams = new SPParam[3];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
                nPrimaryKey);
        arrParams[1] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 2, "psd_flag", 0);
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 3, "psd_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_DeleteStudent", arrParams);
    }
    
    private class StudentTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting()) {
                return;
            }
            for (int c : jtblSearch.getSelectedRows()) {
                nPrimaryKey = new Integer(jtblSearch.getValueAt(c, 0).toString());
            }
            jbtnUpdate.setEnabled(true);
            jbtnPrint.setEnabled(true);
            jbtnDelete.setEnabled(true && hasDeleteRights());
            jrdoUpdateStudentRecord.setEnabled(true);
            jrdoUpdateStudentRegistration.setEnabled(true);
            jrdoUpdateEnrollmentRecord.setEnabled(true);
            jrdoUpdateEnrollmentSubjects.setEnabled(true);
        }
    }
    
    class StudentTableModel extends AbstractTableModel {
        private int nRowCnt;
        String[] columnNames = { 
            "sd_primary_key", "Last Name", "First Name", "Middle Name", 
            "Junior High School", "Year Graduated", "Elementary", "Year Graduated"
        };
        Object[][] data = new Object[0][0];

        StudentTableModel() {
            super();
        }

        public void reloadTableData() {
            DBManager dbm = new DBManager();
            ResultSet rsTM  = null;
            nRowCnt = 0;
            String sTbl = "students";
            String sCols = "sd_primary_key, sd_lastname, sd_firstname, sd_middlename, sd_jhs_name,";
            sCols += "sd_jhs_comp_year, sd_elem_name, sd_elem_comp_year";
            
            String sCond;
            if(jtxfSearch.getText().isEmpty()) {
                sCond = "";
            }
            else {
                sCond  = "sd_lastname like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "sd_firstname like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "sd_middlename like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "sd_jhs_name like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "sd_elem_name like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', sd_lastname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_lastname, ' ', sd_firstname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', sd_middlename, ' ', sd_lastname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', substring(sd_middlename, 1, 1), '. ', sd_lastname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', substring(sd_middlename, 1, 1), ' ', sd_lastname) like '%" + jtxfSearch.getText() + "%'";
            }
            
            try {
                rsTM = dbm.getResultSet(sTbl, sCols, sCond, "");
                rsTM.last();
                nRowCnt = rsTM.getRow();
                rsTM.beforeFirst();
                if (nRowCnt > 0) {
                    data = new Object[nRowCnt][8];
                    int nCtr = 0;
                    while(rsTM.next()) {
                        data[nCtr][0] = rsTM.getString("sd_primary_key");
                        data[nCtr][1] = rsTM.getString("sd_lastname");
                        data[nCtr][2] = rsTM.getString("sd_firstname");
                        data[nCtr][3] = rsTM.getString("sd_middlename");
                        data[nCtr][4] = rsTM.getString("sd_jhs_name");
                        data[nCtr][5] = rsTM.getString("sd_jhs_comp_year");
                        data[nCtr][6] = rsTM.getString("sd_elem_name");
                        data[nCtr][7] = rsTM.getString("sd_elem_comp_year");

                        nCtr++;
                    }
                }
            } 
            catch(SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
            finally	{
                if (rsTM != null) {
                    try {
                        rsTM.close();
                    } 
                    catch(SQLException sqlEx){}//ignore
                    rsTM = null;
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
            //return data.length;
            return nRowCnt;
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

        btgStudentUpdateType = new javax.swing.ButtonGroup();
        jtxfSearch = new javax.swing.JTextField();
        jbtnSearch = new javax.swing.JButton();
        jbtnExit = new javax.swing.JButton();
        jbtnDelete = new javax.swing.JButton();
        jbtnUpdate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblSearch = new javax.swing.JTable();
        jrdoUpdateStudentRecord = new javax.swing.JRadioButton();
        jrdoUpdateStudentRegistration = new javax.swing.JRadioButton();
        jrdoUpdateEnrollmentRecord = new javax.swing.JRadioButton();
        jrdoUpdateEnrollmentSubjects = new javax.swing.JRadioButton();
        jbtnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jbtnSearch.setMnemonic(KeyEvent.VK_S);
        jbtnSearch.setText("Search");
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        jbtnExit.setMnemonic(KeyEvent.VK_X);
        jbtnExit.setText("Exit");
        jbtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExitActionPerformed(evt);
            }
        });

        jbtnDelete.setText("Delete");
        jbtnDelete.setEnabled(false);
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
            }
        });

        jbtnUpdate.setMnemonic(KeyEvent.VK_U);
        jbtnUpdate.setText("Update");
        jbtnUpdate.setEnabled(false);
        jbtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpdateActionPerformed(evt);
            }
        });

        jtblSearch.setAutoCreateRowSorter(true);
        jtblSearch.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jtblSearch);

        btgStudentUpdateType.add(jrdoUpdateStudentRecord);
        jrdoUpdateStudentRecord.setMnemonic(KeyEvent.VK_R);
        jrdoUpdateStudentRecord.setSelected(true);
        jrdoUpdateStudentRecord.setText("Update Student Record");
        jrdoUpdateStudentRecord.setEnabled(false);

        btgStudentUpdateType.add(jrdoUpdateStudentRegistration);
        jrdoUpdateStudentRegistration.setMnemonic(KeyEvent.VK_G);
        jrdoUpdateStudentRegistration.setText("Update Student Registration");
        jrdoUpdateStudentRegistration.setEnabled(false);

        btgStudentUpdateType.add(jrdoUpdateEnrollmentRecord);
        jrdoUpdateEnrollmentRecord.setMnemonic(KeyEvent.VK_E);
        jrdoUpdateEnrollmentRecord.setText("Update Enrollment Record");
        jrdoUpdateEnrollmentRecord.setEnabled(false);

        btgStudentUpdateType.add(jrdoUpdateEnrollmentSubjects);
        jrdoUpdateEnrollmentSubjects.setMnemonic(KeyEvent.VK_J);
        jrdoUpdateEnrollmentSubjects.setText("Update Enrollment Subjects");
        jrdoUpdateEnrollmentSubjects.setEnabled(false);

        jbtnPrint.setMnemonic(KeyEvent.VK_P);
        jbtnPrint.setText("Print");
        jbtnPrint.setToolTipText("Print Early Registration Form");
        jbtnPrint.setEnabled(false);
        jbtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtxfSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jrdoUpdateEnrollmentRecord)
                            .addComponent(jrdoUpdateStudentRecord))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jrdoUpdateStudentRegistration)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbtnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnDelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnExit))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jrdoUpdateEnrollmentSubjects)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 785, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnDelete, jbtnExit, jbtnUpdate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnExit)
                    .addComponent(jbtnDelete)
                    .addComponent(jbtnUpdate)
                    .addComponent(jrdoUpdateStudentRecord)
                    .addComponent(jrdoUpdateStudentRegistration)
                    .addComponent(jbtnPrint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrdoUpdateEnrollmentRecord)
                    .addComponent(jrdoUpdateEnrollmentSubjects))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        StudentTableModel tblMdl = (StudentTableModel)jtblSearch.getModel();
        tblMdl.reloadTableData();
        tblMdl.fireTableDataChanged();
        if (jtblSearch.getSelectionModel().isSelectionEmpty()) {
            jbtnDelete.setEnabled(false);
            jbtnUpdate.setEnabled(false);
            jrdoUpdateStudentRecord.setEnabled(false);
            jrdoUpdateStudentRegistration.setEnabled(false);
            jrdoUpdateEnrollmentRecord.setEnabled(false);
            jrdoUpdateEnrollmentSubjects.setEnabled(false);
        }
        if(tblMdl.getRowCount() <= 0)
            jtblSearch.getSelectionModel().clearSelection();
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void jbtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpdateActionPerformed
        if(jrdoUpdateStudentRecord.isSelected()) {
            JUpdateStudent dlg = new JUpdateStudent(this, true, nPrimaryKey);
            dlg.setVisible(true);
        }
        else if(jrdoUpdateStudentRegistration.isSelected()) {
            JUpdateStudentRegistration dlg = new JUpdateStudentRegistration(this, true, nPrimaryKey);
            dlg.setVisible(true);
        }
        else if(jrdoUpdateEnrollmentRecord.isSelected() || jrdoUpdateEnrollmentSubjects.isSelected()) {
            ResultSet rsEnrInfo = LoadEnrollmentSubjectInfo();
            try {
                int nRowCnt = 0;
                rsEnrInfo.last();
                nRowCnt = rsEnrInfo.getRow();
                if (nRowCnt > 0) {
                    if(jrdoUpdateEnrollmentRecord.isSelected()) {
                        JUpdateEnrollmentRecord dlg = new JUpdateEnrollmentRecord(this, true, nPrimaryKey);
                        dlg.setVisible(true);
                    }
                    else {
                        JUpdateEnrollmentSubjects dlg = new JUpdateEnrollmentSubjects(this, true, nPrimaryKey, rsEnrInfo);
                        dlg.setVisible(true);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Student is not enrolled!", 
                        this.getTitle(), JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
            finally	{
                if (rsEnrInfo != null) {
                    try {
                        rsEnrInfo.close();
                    } 
                    catch(SQLException sqlEx){}//ignore
                    rsEnrInfo = null;
                }
            }
        }
    }//GEN-LAST:event_jbtnUpdateActionPerformed
    private ResultSet LoadEnrollmentSubjectInfo() {
        DBManager dbm = new DBManager();
        ResultSet rsEnInfo  = null;
        String sCols = "sd_shs_sy, sd_shs_sem, sd_shs_gradesection, sd_shs_class_adviser";
        String sCond = "sd_primary_key = " + nPrimaryKey + " and sd_status_cd = 'CEN'";

        rsEnInfo = dbm.getResultSet("students", sCols, sCond, "");       
        return rsEnInfo;
    }
    private void jbtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Are you sure to exit search?",
            this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_jbtnExitActionPerformed

    private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm delete",
            this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            SPParam[] outParams = DeleteStudent();
            if (outParams != null) {
                Integer nOut = new Integer(outParams[0].getParamValue().toString());
                if(nOut != 0) {
                    String sErrMsg =  outParams[1].getParamValue().toString();
                    JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                            this.getTitle(), JOptionPane.ERROR_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(this, "Delete Successfull!.");
                    StudentTableModel tblMdl = (StudentTableModel)jtblSearch.getModel();
                    tblMdl.reloadTableData();
                    tblMdl.fireTableDataChanged();
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbtnDeleteActionPerformed

    private void jbtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPrintActionPerformed
        shrs.reports.JEarlyRegistrationReport dlg = new shrs.reports.JEarlyRegistrationReport(nPrimaryKey);
        dlg.showEarlyRegistrationForm();
    }//GEN-LAST:event_jbtnPrintActionPerformed

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
            java.util.logging.Logger.getLogger(JSearchStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JSearchStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JSearchStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JSearchStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JSearchStudent dialog = new JSearchStudent(new javax.swing.JFrame(), true, "");
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
    private javax.swing.ButtonGroup btgStudentUpdateType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnExit;
    private javax.swing.JButton jbtnPrint;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JButton jbtnUpdate;
    private javax.swing.JRadioButton jrdoUpdateEnrollmentRecord;
    private javax.swing.JRadioButton jrdoUpdateEnrollmentSubjects;
    private javax.swing.JRadioButton jrdoUpdateStudentRecord;
    private javax.swing.JRadioButton jrdoUpdateStudentRegistration;
    private javax.swing.JTable jtblSearch;
    private javax.swing.JTextField jtxfSearch;
    // End of variables declaration//GEN-END:variables
}
