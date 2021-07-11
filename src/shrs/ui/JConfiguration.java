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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;
import shrs.utility.FieldValidator;

/**
 *
 * @author osraldo
 */
public class JConfiguration extends javax.swing.JDialog {
    Integer m_nPrimaryKey;
    /**
     * Creates new form JConfiguration
     */
    public JConfiguration(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setLocationRelativeTo(parent);
        initComponents();
        this.setTitle("Setup Configuration");
        
        jtblConfigType.setModel(new ConfigTableModel());
        jtblConfigType.getTableHeader().setReorderingAllowed(false);
        jtblConfigType.getSelectionModel().addListSelectionListener(new ConfigTableRowListener());
        
        SetTableWidth();
    }
    
    private void SetTableWidth() {
        TableColumn tc = jtblConfigType.getColumnModel().getColumn(0);
        tc.setMinWidth(0);
        tc.setMaxWidth(0);
        tc = jtblConfigType.getColumnModel().getColumn(1);
        tc.setMinWidth(15);
        tc.setPreferredWidth(65);
        tc.setMaxWidth(65);
    }
    
    private class ConfigTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting()) {
                return;
            }
            for (int c : jtblConfigType.getSelectedRows()) {
                jbtnSaveUpdate.setEnabled(true);
                m_nPrimaryKey = new Integer(jtblConfigType.getValueAt(c, 0).toString());
                jtxfMinor.setText(jtblConfigType.getValueAt(c, 1).toString());
                jtxfName.setText(jtblConfigType.getValueAt(c, 2).toString());
                jtxfDesc.setText(jtblConfigType.getValueAt(c, 3).toString());
                jtxfGeneral_1.setText(jtblConfigType.getValueAt(c, 4).toString());
                jtxfGeneral_2.setText(jtblConfigType.getValueAt(c, 5).toString());
            }
        }
    }
    
    private void ClearDataControls() {
        jtblConfigType.getSelectionModel().clearSelection();
        jtxfMinor.setText("");
        jtxfName.setText("");
        jtxfDesc.setText("");
        jtxfGeneral_1.setText("");
        jtxfGeneral_2.setText("");
    }
    
    private void SetEditableDataControlsForUpdate(boolean bEnabled) {
        jtxfName.setEditable(bEnabled);
        jtxfDesc.setEditable(bEnabled);
        jtxfGeneral_1.setEditable(bEnabled);
        jtxfGeneral_2.setEditable(bEnabled);
    }
    
    private boolean ValidateFields() {
        FieldValidator fv = new FieldValidator(jtxfName.getText(), "Config Name");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfName.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfDesc.getText(), "Description");
        fv.setRequired(true);
        fv.setFieldlength(100);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfDesc.grabFocus();
            return fv.isValidated;
        }
        
        return true;
    }
    
    private SPParam[] UpdateConfig() {
        SPParam[] arrParams = new SPParam[7];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "pcf_primary_key", 
            m_nPrimaryKey.intValue());
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "pcf_name", 
            jtxfName.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "pcf_desc", 
            jtxfDesc.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "pcf_general_1", 
            jtxfGeneral_1.getText());
       arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "pcf_general_2", 
            jtxfGeneral_2.getText());
        
        arrParams[5] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 6, "pcf_flag", 0);
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 7, "pcf_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_ConfigUpdate", arrParams);        
    }
    
    
    class ConfigTableModel extends AbstractTableModel {
        String[] columnNames = { 
            "cf_primary_key", "Minor Cd", "Config Name", "Description", 
            "General Value 1", "General Value 2"
        };
        Object[][] data = new Object[0][0];
        
        ConfigTableModel() {
            super();
            reloadTableData();
        }
        
        public void reloadTableData() {
            DBManager dbm = new DBManager();
            ResultSet rsConfigs  = null;
            int nRowCnt = 0;
            String sMajorCd = ((String)jcboConfigType.getSelectedItem()).substring(0, 3);
                        
            try {
                rsConfigs = dbm.getResultSet("configs", "*", "cf_major = '" + sMajorCd + "'", "");
                rsConfigs.last();
                nRowCnt = rsConfigs.getRow();
                rsConfigs.beforeFirst();
                if (nRowCnt > 0) {
                    data = new Object[nRowCnt][6];
                    int nCtr = 0;
                    while(rsConfigs.next()) {
                        data[nCtr][0] = rsConfigs.getString("cf_primary_key");
                        data[nCtr][1] = rsConfigs.getString("cf_minor");
                        data[nCtr][2] = rsConfigs.getString("cf_name");
                        data[nCtr][3] = rsConfigs.getString("cf_desc");
                        String sGen1 = rsConfigs.getString("cf_general_1");
                        data[nCtr][4] = sGen1 == null ? "" : sGen1;
                        String sGen2 = rsConfigs.getString("cf_general_2");
                        data[nCtr][5] = sGen2 == null ? "" : sGen2;

                        nCtr++;
                    }
                }
            } 
            catch(SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
            finally	{
                if (rsConfigs != null) {
                    try {
                        rsConfigs.close();
                    } 
                    catch(SQLException sqlEx){}//ignore
                    rsConfigs = null;
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

        jLabel1 = new javax.swing.JLabel();
        jcboConfigType = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxfMinor = new javax.swing.JTextField();
        jtxfName = new javax.swing.JTextField();
        jtxfDesc = new javax.swing.JTextField();
        jtxfGeneral_1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtxfGeneral_2 = new javax.swing.JTextField();
        jbtnCancelExit = new javax.swing.JButton();
        jbtnSaveUpdate = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblConfigType = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel1.setText("Configuration Type:");

        jcboConfigType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CFG - System Configuration", "DSG - User Designation", "SCH - Schools", "TRK - Tracks", "SAS - Strand/Specialization", "RGN - Regions", "BGY - Barangays", "SST - Student's Statuses" }));
        jcboConfigType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboConfigTypeActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Minor Code:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Config Name:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Description:");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("General Value 1:");

        jtxfMinor.setEditable(false);

        jtxfName.setEditable(false);

        jtxfDesc.setEditable(false);

        jtxfGeneral_1.setEditable(false);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("General Value 2:");

        jtxfGeneral_2.setEditable(false);

        jbtnCancelExit.setText("Exit");
        jbtnCancelExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelExitActionPerformed(evt);
            }
        });

        jbtnSaveUpdate.setText("Update");
        jbtnSaveUpdate.setEnabled(false);
        jbtnSaveUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveUpdateActionPerformed(evt);
            }
        });

        jtblConfigType.setAutoCreateRowSorter(true);
        jtblConfigType.setModel(new javax.swing.table.DefaultTableModel(
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
        jtblConfigType.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jtblConfigType);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcboConfigType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfGeneral_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfGeneral_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxfMinor, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnSaveUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnCancelExit))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxfDesc, jtxfGeneral_1, jtxfGeneral_2, jtxfMinor, jtxfName});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnCancelExit, jbtnSaveUpdate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jcboConfigType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxfMinor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtxfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jtxfDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtxfGeneral_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jtxfGeneral_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancelExit)
                    .addComponent(jbtnSaveUpdate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcboConfigTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboConfigTypeActionPerformed
        ((ConfigTableModel)jtblConfigType.getModel()).reloadTableData();
        ((ConfigTableModel)jtblConfigType.getModel()).fireTableDataChanged();
    }//GEN-LAST:event_jcboConfigTypeActionPerformed

    private void jbtnSaveUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveUpdateActionPerformed
        if(jbtnSaveUpdate.getText() == "Update") {
            jtblConfigType.setEnabled(false);
            jbtnSaveUpdate.setText("Save");
            jbtnCancelExit.setText("Cancel");
            SetEditableDataControlsForUpdate(true);
            jtxfName.grabFocus();
        }
         else {
            if (ValidateFields()) {
                if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm",
                    this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    SPParam[] outParams = UpdateConfig();
                    if (outParams != null) {
                        Integer nOut = new Integer(outParams[0].getParamValue().toString());
                        if(nOut != 0) {
                            String sErrMsg =  outParams[1].getParamValue().toString();
                            JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "Selected Configuration Successfully Updated!");
                            jtblConfigType.setEnabled(true);
                            jbtnSaveUpdate.setEnabled(false);
                            jbtnSaveUpdate.setText("Update");
                            jbtnCancelExit.setText("Exit");
                            ClearDataControls();
                            SetEditableDataControlsForUpdate(false);
                            
                            ((ConfigTableModel)jtblConfigType.getModel()).reloadTableData();
                            ((ConfigTableModel)jtblConfigType.getModel()).fireTableDataChanged();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                        this.getTitle(), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }//GEN-LAST:event_jbtnSaveUpdateActionPerformed

    private void jbtnCancelExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelExitActionPerformed
        if(jbtnCancelExit.getText() == "Cancel") {
            jtblConfigType.setEnabled(true);
            jbtnSaveUpdate.setEnabled(false);
            jbtnSaveUpdate.setText("Update");
            jbtnCancelExit.setText("Exit");
            ClearDataControls();
            SetEditableDataControlsForUpdate(false);
        }
        else {
            this.dispose();
        }
    }//GEN-LAST:event_jbtnCancelExitActionPerformed

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
            java.util.logging.Logger.getLogger(JConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JConfiguration dialog = new JConfiguration(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnCancelExit;
    private javax.swing.JButton jbtnSaveUpdate;
    private javax.swing.JComboBox<String> jcboConfigType;
    private javax.swing.JTable jtblConfigType;
    private javax.swing.JTextField jtxfDesc;
    private javax.swing.JTextField jtxfGeneral_1;
    private javax.swing.JTextField jtxfGeneral_2;
    private javax.swing.JTextField jtxfMinor;
    private javax.swing.JTextField jtxfName;
    // End of variables declaration//GEN-END:variables
}
