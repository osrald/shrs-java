/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.reports;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.view.JasperViewer;
import shrs.db.DBManager;
import shrs.db.SPParam;
import shrs.db.TypeParam;

/**
 *
 * @author osraldo
 */
public class JEarlyRegistrationReport {
    private Integer m_nPrimaryKey;
    
    public JEarlyRegistrationReport(Integer nPrimaryKey) {
        m_nPrimaryKey = nPrimaryKey;
    }
    
    public void showEarlyRegistrationForm() {
       SPParam[] arrParams = new SPParam[1];
        arrParams[0] = new SPParam<Integer>(Types.INTEGER, TypeParam.In, 1, "psd_primary_key", 
            m_nPrimaryKey);

        DBManager dbm = new DBManager();
        ResultSet rs = dbm.retrieveStoredProc("SP_EarlyRegistrationReport", arrParams);
        
        try
        {
            /*File reportFile = new File("reports/EnrollmentForm.jasper");

            JasperRunManager.runReportToPdfFile(reportFile.getPath(), new HashMap(),
                new JRResultSetDataSource(rs));*/
            JasperCompileManager.compileReportToFile("reports/EarlyRegistration.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport("reports/EarlyRegistration.jasper", new HashMap(),
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
