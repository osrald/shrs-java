/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.ui;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.KeyEvent;
import shrs.db.DBManager;

/**
 *
 * @author OS
 */
public class JJuniorHighRegPrint extends javax.swing.JDialog {
    private int m_nPrimaryKey;

    /**
     * Creates new form JJuniorHighRegPrint
     */
    public JJuniorHighRegPrint(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.setLocationRelativeTo(parent);
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
        tc.setPreferredWidth(75);
        tc.setMaxWidth(100);
        tc = jtblSearch.getColumnModel().getColumn(5);
        tc.setMinWidth(15);
        tc.setPreferredWidth(35);
        tc.setMaxWidth(75);
        
        jtblSearch.getTableHeader().setReorderingAllowed(false);
    }
    
   private class StudentTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting()) {
                return;
            }
            for (int c : jtblSearch.getSelectedRows()) {
                m_nPrimaryKey = new Integer(jtblSearch.getValueAt(c, 0).toString());
            }
            jbtnPrint.setEnabled(true);
        }
    }
    
    class StudentTableModel extends AbstractTableModel {
        private int nRowCnt;
        String[] columnNames = { 
            "sd_primary_key", "Last Name", "First Name", "Middle Name", "Birthdate", "Age"
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
            String sCols = "sd_primary_key, sd_lastname, sd_firstname, sd_middlename, sd_dob,";
            sCols += "timestampdiff(year, sd_dob, curdate()) as age";
            
            String sCond;
            if(jtxfSearch.getText().isEmpty()) {
                sCond = "sd_status_cd = 'JRE'";
            }
            else {
                sCond  = "(sd_lastname like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "sd_firstname like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "sd_middlename like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', sd_lastname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_lastname, ' ', sd_firstname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', sd_middlename, ' ', sd_lastname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', substring(sd_middlename, 1, 1), '. ', sd_lastname) like '%" + jtxfSearch.getText() + "%' or ";
                sCond += "concat(sd_firstname, ' ', substring(sd_middlename, 1, 1), ' ', sd_lastname) like '%" + jtxfSearch.getText() + "%') and ";
                sCond += "sd_status_cd = 'JRE'";
            }
            
            try {
                rsTM = dbm.getResultSet(sTbl, sCols, sCond, "");
                rsTM.last();
                nRowCnt = rsTM.getRow();
                rsTM.beforeFirst();
                if (nRowCnt > 0) {
                    data = new Object[nRowCnt][6];
                    int nCtr = 0;
                    while(rsTM.next()) {
                        data[nCtr][0] = rsTM.getString("sd_primary_key");
                        data[nCtr][1] = rsTM.getString("sd_lastname");
                        data[nCtr][2] = rsTM.getString("sd_firstname");
                        data[nCtr][3] = rsTM.getString("sd_middlename");
                        data[nCtr][4] = rsTM.getString("sd_dob");
                        data[nCtr][5] = rsTM.getString("age"); 

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

        jtxfSearch = new javax.swing.JTextField();
        jbtnSearch = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblSearch = new javax.swing.JTable();
        jbntCancel = new javax.swing.JButton();
        jbtnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Print Registration Form");

        jbtnSearch.setMnemonic(KeyEvent.VK_S);
        jbtnSearch.setText("Search");
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        jtblSearch.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jtblSearch);

        jbntCancel.setMnemonic(KeyEvent.VK_C);
        jbntCancel.setText("Cancel");
        jbntCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbntCancelActionPerformed(evt);
            }
        });

        jbtnPrint.setMnemonic(KeyEvent.VK_P);
        jbtnPrint.setText("Print");
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
                        .addComponent(jbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbntCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbntCancel)
                    .addComponent(jbtnPrint))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        StudentTableModel tblMdl = (StudentTableModel)jtblSearch.getModel();
        tblMdl.reloadTableData();
        tblMdl.fireTableDataChanged();
        if (jtblSearch.getSelectionModel().isSelectionEmpty()) {
            jbtnPrint.setEnabled(false);
        }
        if(tblMdl.getRowCount() <= 0)
            jtblSearch.getSelectionModel().clearSelection();
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void jbtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPrintActionPerformed
        shrs.reports.JJuniorHighRegReport dlg = new shrs.reports.JJuniorHighRegReport(m_nPrimaryKey);
        dlg.showEarlyRegistrationForm();
    }//GEN-LAST:event_jbtnPrintActionPerformed

    private void jbntCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbntCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbntCancelActionPerformed

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
            java.util.logging.Logger.getLogger(JJuniorHighRegPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JJuniorHighRegPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JJuniorHighRegPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JJuniorHighRegPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JJuniorHighRegPrint dialog = new JJuniorHighRegPrint(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbntCancel;
    private javax.swing.JButton jbtnPrint;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JTable jtblSearch;
    private javax.swing.JTextField jtxfSearch;
    // End of variables declaration//GEN-END:variables
}
