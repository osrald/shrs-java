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
public class JUpdateEnrollmentSubjects extends javax.swing.JDialog {
    private int m_nStudentPrimaryKey;
    private int m_nPrimaryKey;
    /**
     * Creates new form JUpdateEnrollmentSubjects
     */
    public JUpdateEnrollmentSubjects(javax.swing.JDialog parent, boolean modal, int nPrimaryKey, ResultSet rsEnrInfo) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        m_nStudentPrimaryKey = nPrimaryKey;
        this.setTitle("Update Enrollment Subjects");
        jtblSubjects.setModel(new SubjectsTableModel());
        jtblSubjects.getTableHeader().setReorderingAllowed(false);
        jtblSubjects.getSelectionModel().addListSelectionListener(new SubjectsTableRowListener());
        
        SetTableWidth();
        LoadEnrollmentInfo(rsEnrInfo);
    }
    
    private void SetTableWidth() {
        TableColumn tc = jtblSubjects.getColumnModel().getColumn(0);
        tc.setMinWidth(0);
        tc.setMaxWidth(0);
    }    
    
    private void ClearDataControls() {
        jtblSubjects.getSelectionModel().clearSelection();
        m_nPrimaryKey = -1;
        jtxfTime.setText("");
        jtxfSubject.setText("");
        jtxfSubjectTeacher.setText("");
        jtxfRoomNo.setText("");
    }
    
    private void SetEditableDataControls(boolean bEnabled) {
        jtxfTime.setEditable(bEnabled);
        jtxfSubject.setEditable(bEnabled);
        jtxfSubjectTeacher.setEditable(bEnabled);
        jtxfRoomNo.setEditable(bEnabled);
    }
    
    private void LoadEnrollmentInfo(ResultSet rsEnrInfo) {
        try {
            jtxfSchoolYear.setText(rsEnrInfo.getString("sd_shs_sy"));
            jtxfSemester.setText(rsEnrInfo.getString("sd_shs_sem"));
            jtxfGradeSection.setText(rsEnrInfo.getString("sd_shs_gradesection"));
            jtxfClassAdviser.setText(rsEnrInfo.getString("sd_shs_class_adviser"));
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
    
    private boolean ValidateFields() {
        FieldValidator fv = new FieldValidator(jtxfTime.getText(), "Time");
        fv.setRequired(true);
        fv.setFieldlength(30);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfTime.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfSubject.getText(), "Subject");
        fv.setRequired(true);
        fv.setFieldlength(45);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfSubject.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfSubjectTeacher.getText(), "Subject Teacher");
        fv.setRequired(true);
        fv.setFieldlength(50);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfSubjectTeacher.grabFocus();
            return fv.isValidated;
        }
        
        fv = new FieldValidator(jtxfRoomNo.getText(), "Room No.");
        fv.setRequired(true);
        fv.setFieldlength(30);
        fv.doValidation();
        if(fv.isValidated == false) {
            JOptionPane.showMessageDialog(this, fv.getValidationMessage(), 
                this.getTitle(), JOptionPane.ERROR_MESSAGE);
            jtxfRoomNo.grabFocus();
            return fv.isValidated;
        }
           
        return true;
    }  
   
    private SPParam[] AddNewSubject() {
        SPParam[] arrParams = new SPParam[10];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psj_student_primary_key", 
            m_nStudentPrimaryKey);
        arrParams[1] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 2, "psj_sy", 
            jtxfSchoolYear.getText());
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psj_sem", 
            jtxfSemester.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psj_gradesection", 
            jtxfGradeSection.getText());
         arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psj_time", 
            jtxfTime.getText());
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "psj_subject", 
            jtxfSubject.getText());
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psj_subject_teacher", 
            jtxfSubjectTeacher.getText());
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psj_room_no", 
            jtxfRoomNo.getText());

        arrParams[8] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 9, "psj_flag", 0);
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 10, "psj_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_EnrollSubjectAdd", arrParams);   
    }
    
    private SPParam[] UpdateSubject() {
        SPParam[] arrParams = new SPParam[10];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psj_primary_key", 
            m_nPrimaryKey);
        arrParams[1] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 2, "psj_student_primary_key", 
            m_nStudentPrimaryKey);
        arrParams[2] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 3, "psj_gradesection", 
            jtxfGradeSection.getText());
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 4, "psj_time", 
            jtxfTime.getText());
        arrParams[4] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 5, "psj_subject", 
            jtxfSubject.getText());
        arrParams[5] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 6, "psj_subject_teacher", 
            jtxfSubjectTeacher.getText());
        arrParams[6] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 7, "psj_room_no", 
            jtxfRoomNo.getText());
        arrParams[7] = new SPParam<String>(Types.VARCHAR, TypeParam.In, 8, "psd_shs_class_adviser", 
            jtxfClassAdviser.getText());

        arrParams[8] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 9, "psj_flag", 0);
        arrParams[9] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 10, "psj_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_EnrollSubjectUpdate", arrParams);  
    }
    
    private SPParam[] DeleteSubject() {
        SPParam[] arrParams = new SPParam[4];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psj_primary_key", 
            m_nPrimaryKey);
        arrParams[1] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 2, "psj_student_primary_key", 
            m_nStudentPrimaryKey);

        arrParams[2] = new SPParam<Integer>(Types.INTEGER, TypeParam.Out, 3, "psj_flag", 0);
        arrParams[3] = new SPParam<String>(Types.VARCHAR, TypeParam.Out, 4, "psj_error_desc", "");

        DBManager dbm = new DBManager();
        return dbm.executeStoredProc("SP_EnrollSubjectDelete", arrParams);  
    }
    
    private class SubjectsTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting()) {
                return;
            }
            for (int c : jtblSubjects.getSelectedRows()) {
                jbtnUpdateSave.setEnabled(true);
                jbtnDelete.setEnabled(true);
                m_nPrimaryKey = new Integer(jtblSubjects.getValueAt(c, 0).toString());
                jtxfTime.setText(jtblSubjects.getValueAt(c, 1).toString());
                jtxfSubject.setText(jtblSubjects.getValueAt(c, 2).toString());
                jtxfSubjectTeacher.setText(jtblSubjects.getValueAt(c, 3).toString());
                jtxfRoomNo.setText(jtblSubjects.getValueAt(c, 4).toString());
            }
        }
    }
    
    private class SubjectsTableModel extends AbstractTableModel {
        String[] columnNames = { 
            "sj_primary_key", "Time", "Subject", "Subject Teacher", 
            "Room No."
        };
        Object[][] data = new Object[0][0];
        
        SubjectsTableModel() {
            super();
            reloadTableData();
        }
        
        public void reloadTableData() {
            DBManager dbm = new DBManager();
            ResultSet rsSubjects  = null;
            int nRowCnt = 0;
            String sCols = "sj_primary_key, sj_time, sj_subject, sj_subject_teacher, sj_room_no";
                        
            try {
                rsSubjects = dbm.getResultSet("subjects", sCols, "sj_student_primary_key = " + m_nStudentPrimaryKey, 
                        "sj_primary_key");
                rsSubjects.last();
                nRowCnt = rsSubjects.getRow();
                rsSubjects.beforeFirst();
                if (nRowCnt > 0) {
                    data = new Object[nRowCnt][6];
                    int nCtr = 0;
                    while(rsSubjects.next()) {
                        data[nCtr][0] = rsSubjects.getString("sj_primary_key");
                        data[nCtr][1] = rsSubjects.getString("sj_time");
                        data[nCtr][2] = rsSubjects.getString("sj_subject");
                        data[nCtr][3] = rsSubjects.getString("sj_subject_teacher");
                        data[nCtr][4] = rsSubjects.getString("sj_room_no");

                        nCtr++;
                    }
                }
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
        jtxfSchoolYear = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxfSemester = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxfGradeSection = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxfClassAdviser = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtxfTime = new javax.swing.JTextField();
        jtxfSubjectTeacher = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jtxfSubject = new javax.swing.JTextField();
        jtxfRoomNo = new javax.swing.JTextField();
        jbtnExitCancel = new javax.swing.JButton();
        jbtnUpdateSave = new javax.swing.JButton();
        jbtnNewAdd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblSubjects = new javax.swing.JTable();
        jbtnDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel1.setText("School Year:");

        jtxfSchoolYear.setEditable(false);

        jLabel2.setText("Semester:");

        jtxfSemester.setEditable(false);

        jLabel3.setText("Grade & Section:");

        jtxfGradeSection.setEditable(false);

        jLabel4.setText("Class Adviser:");

        jtxfClassAdviser.setEditable(false);

        jLabel5.setText("Time:");

        jLabel6.setText("Subject Teacher:");

        jtxfTime.setEditable(false);

        jtxfSubjectTeacher.setEditable(false);

        jLabel7.setText("Subject:");

        jLabel8.setText("Room No.:");

        jtxfSubject.setEditable(false);

        jtxfRoomNo.setEditable(false);

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

        jtblSubjects.setModel(new javax.swing.table.DefaultTableModel(
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
        jtblSubjects.setMaximumSize(new java.awt.Dimension(679, 258));
        jtblSubjects.setMinimumSize(new java.awt.Dimension(679, 258));
        jtblSubjects.setPreferredSize(new java.awt.Dimension(679, 258));
        jtblSubjects.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jtblSubjects);

        jbtnDelete.setText("Delete");
        jbtnDelete.setEnabled(false);
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxfGradeSection, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                            .addComponent(jtxfSchoolYear))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxfClassAdviser)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxfSemester, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxfTime)
                            .addComponent(jtxfSubjectTeacher, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxfSubject)
                                    .addComponent(jtxfRoomNo)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jbtnNewAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnUpdateSave, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnDelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnExitCancel))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnDelete, jbtnExitCancel, jbtnNewAdd, jbtnUpdateSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxfSchoolYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jtxfSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtxfGradeSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jtxfClassAdviser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtxfTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jtxfSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jtxfSubjectTeacher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jtxfRoomNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnExitCancel)
                    .addComponent(jbtnUpdateSave)
                    .addComponent(jbtnNewAdd)
                    .addComponent(jbtnDelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnNewAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNewAddActionPerformed
       if(jbtnNewAdd.getText() == "New") {
            jtblSubjects.setEnabled(false);
            jbtnNewAdd.setText("Add");
            jbtnUpdateSave.setEnabled(false);
            jbtnDelete.setEnabled(false);
            jbtnExitCancel.setText("Cancel");
            jtxfTime.grabFocus();
            ClearDataControls();
            SetEditableDataControls(true);
        }
        else {
            if (ValidateFields()) {
                if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm",
                    this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    SPParam[] outParams = AddNewSubject();
                    if (outParams != null) {
                        Integer nOut = new Integer(outParams[0].getParamValue().toString());
                        if(nOut != 0) {
                            String sErrMsg =  outParams[1].getParamValue().toString();
                            JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "New Subject Successfully Added!");
                            jtblSubjects.setEnabled(true);
                            jbtnNewAdd.setText("New");
                            jbtnExitCancel.setText("Exit");
                            ClearDataControls();
                            SetEditableDataControls(false);
                            
                            ((SubjectsTableModel)jtblSubjects.getModel()).reloadTableData();
                            ((SubjectsTableModel)jtblSubjects.getModel()).fireTableDataChanged();
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
            jtblSubjects.setEnabled(false);
            jbtnUpdateSave.setText("Save");
            jbtnNewAdd.setEnabled(false);
            jbtnDelete.setEnabled(false);
            jbtnExitCancel.setText("Cancel");
            jtxfGradeSection.setEditable(true);
            jtxfClassAdviser.setEditable(true);
            SetEditableDataControls(true);
            jtxfTime.grabFocus();
        }
        else {
            if (ValidateFields()) {
                if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm",
                    this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    SPParam[] outParams = UpdateSubject();
                    if (outParams != null) {
                        Integer nOut = new Integer(outParams[0].getParamValue().toString());
                        if(nOut != 0) {
                            String sErrMsg =  outParams[1].getParamValue().toString();
                            JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "Subject Successfully Updated!");
                            jtblSubjects.setEnabled(true);
                            jbtnUpdateSave.setEnabled(false);
                            jbtnDelete.setEnabled(false);
                            jbtnUpdateSave.setText("Update");
                            jbtnNewAdd.setEnabled(true);
                            jbtnExitCancel.setText("Exit");
                            ClearDataControls();
                            jtxfGradeSection.setEditable(false);
                            jtxfClassAdviser.setEditable(false);
                            SetEditableDataControls(false);
                            
                            ((SubjectsTableModel)jtblSubjects.getModel()).reloadTableData();
                            ((SubjectsTableModel)jtblSubjects.getModel()).fireTableDataChanged();
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

    private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Click Yes to confirm",
              this.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
              SPParam[] outParams = DeleteSubject();
              if (outParams != null) {
                  Integer nOut = new Integer(outParams[0].getParamValue().toString());
                  if(nOut != 0) {
                      String sErrMsg =  outParams[1].getParamValue().toString();
                      JOptionPane.showMessageDialog(this, sErrMsg + " Please contact your system admin.",
                              this.getTitle(), JOptionPane.ERROR_MESSAGE);
                  }
                  else {
                      JOptionPane.showMessageDialog(this, "Subject Successfully Deleted!");
                      jtblSubjects.setEnabled(true);
                      jbtnUpdateSave.setEnabled(false);
                      jbtnDelete.setEnabled(false);
                      jbtnUpdateSave.setText("Update");
                      jbtnNewAdd.setEnabled(true);
                      jbtnExitCancel.setText("Exit");
                      ClearDataControls();
                      SetEditableDataControls(false);

                      ((SubjectsTableModel)jtblSubjects.getModel()).reloadTableData();
                      ((SubjectsTableModel)jtblSubjects.getModel()).fireTableDataChanged();
                  }
              }
              else {
                  JOptionPane.showMessageDialog(this, "An error occured. Please contact your system admin.",
                  this.getTitle(), JOptionPane.ERROR_MESSAGE);
              }
        }
    }//GEN-LAST:event_jbtnDeleteActionPerformed

    private void jbtnExitCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitCancelActionPerformed
        if(jbtnExitCancel.getText() == "Cancel") {
            jtblSubjects.setEnabled(true);
            jbtnNewAdd.setEnabled(true);
            jbtnNewAdd.setText("New");
            jbtnUpdateSave.setEnabled(false);
            jbtnDelete.setEnabled(false);
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
            java.util.logging.Logger.getLogger(JUpdateEnrollmentSubjects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JUpdateEnrollmentSubjects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JUpdateEnrollmentSubjects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JUpdateEnrollmentSubjects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JUpdateEnrollmentSubjects dialog = new JUpdateEnrollmentSubjects(new javax.swing.JDialog(), true, 1, null);
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnExitCancel;
    private javax.swing.JButton jbtnNewAdd;
    private javax.swing.JButton jbtnUpdateSave;
    private javax.swing.JTable jtblSubjects;
    private javax.swing.JTextField jtxfClassAdviser;
    private javax.swing.JTextField jtxfGradeSection;
    private javax.swing.JTextField jtxfRoomNo;
    private javax.swing.JTextField jtxfSchoolYear;
    private javax.swing.JTextField jtxfSemester;
    private javax.swing.JTextField jtxfSubject;
    private javax.swing.JTextField jtxfSubjectTeacher;
    private javax.swing.JTextField jtxfTime;
    // End of variables declaration//GEN-END:variables
}
