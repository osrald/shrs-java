/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.view.JasperViewer;
import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;

/**
 *
 * @author osraldo
 */
public class JEnrollStudentReport {
    private Integer m_nPrimaryKey;
    
    public JEnrollStudentReport(Integer nPrimaryKey) {
        m_nPrimaryKey = nPrimaryKey;
    }
    
    public void showTestImage() {
       SPParam[] arrParams = new SPParam[1];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            m_nPrimaryKey);
        
        java.io.File fl = new java.io.File("reports/Test.csv");
        JRCsvDataSource src = null;
        try {
            src = new JRCsvDataSource(fl);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        

        //DBManager dbm = new DBManager();
        //ResultSet rs = new JResultSetMock();//dbm.retrieveStoredProc("SP_EnrollmentReport", arrParams);
    
        try
        {
            /*File reportFile = new File("reports/EnrollmentForm.jasper");

            JasperRunManager.runReportToPdfFile(reportFile.getPath(), new HashMap(),
                new JRResultSetDataSource(rs));*/
            
            JasperCompileManager.compileReportToFile("reports/ImageLoad.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport("reports/ImageLoad.jasper", new HashMap(),
                    src);
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Report Viewer");
            jasperViewer.setVisible(true);
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void showEnrollmentForm() {
       SPParam[] arrParams = new SPParam[1];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            m_nPrimaryKey);

        DBManager dbm = new DBManager();
        ResultSet rs = dbm.retrieveStoredProc("SP_EnrollmentReport", arrParams);
        
        try
        {
            /*File reportFile = new File("reports/EnrollmentForm.jasper");

            JasperRunManager.runReportToPdfFile(reportFile.getPath(), new HashMap(),
                new JRResultSetDataSource(rs));*/
            JasperCompileManager.compileReportToFile("reports/EnrollmentForm.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport("reports/EnrollmentForm.jasper", new HashMap(),
                    new JRResultSetDataSource(rs));
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Report Viewer");
            jasperViewer.setVisible(true);
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void showEnrollmentSubjects() {
       SPParam[] arrParams = new SPParam[1];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            m_nPrimaryKey);

        DBManager dbm = new DBManager();
        String sTables = "subjects su left join students st on su.sj_student_primary_key = st.sd_primary_key";
        String sColumns = "su.sj_primary_key, su.sj_date_enrolled, su.sj_sy, su.sj_gradesection, ";
        sColumns += "su.sj_time, su.sj_subject, su.sj_subject_teacher, su.sj_room_no, st.sd_shs_class_adviser, ";
        sColumns += "concat(st.sd_firstname, ' ', st.sd_middlename, ' ', st.sd_lastname) as sd_student_name, ";
        sColumns += "(select cf_general_1 from configs where cf_major = 'CFG' and cf_minor = 'PRN') as sd_shs_principal, ";
        sColumns += "(select cf_general_1 from configs where cf_major = 'CFG' and cf_minor = 'PRL') as sd_shs_principal_lvl ";
        String sCond = "su.sj_student_primary_key = " + m_nPrimaryKey;
        ResultSet rs = dbm.getResultSet(sTables, sColumns, sCond, "su.sj_primary_key");
        
        try
        {
            JasperCompileManager.compileReportToFile("reports/EnrollmentSubjects.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport("reports/EnrollmentSubjects.jasper", new HashMap(),
                    new JRResultSetDataSource(rs));
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Report Viewer");
            jasperViewer.setVisible(true);
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
