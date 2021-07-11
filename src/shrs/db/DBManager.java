/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.db;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.CallableStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author osraldo
 */
public class DBManager {
    DBConnection dbConn = null;
    Statement dbStmt = null;
    private String m_sConnStr = ""; //jdbc:mysql://localhost:3306/shrs
    private String m_sUsr = ""; //root
    private String m_sPwd = ""; //Admin1234

    private boolean bCurrentQryHasError = true;
    private String sErrorMessage = "";

    public DBManager() {
        getConectionStringFromFile();
        String sMD5Pwd = "";
        try { sMD5Pwd = shrs.utility.HashCode.md5(m_sPwd); } 
        catch (NoSuchAlgorithmException ex) {}
        dbConn = new DBConnection(m_sConnStr, m_sUsr, sMD5Pwd);
    }

    public boolean isCurrentQueryHasError() {
        return bCurrentQryHasError;
    }

    public String getErrorMessage() {
        return sErrorMessage;
    }

    public ResultSet getResultSet(String sDBTable, String sDBColumns, String sDBCondition, String sOrderBy) {
        ResultSet rsData = null;
        String sQry = "select " + sDBColumns + " from " + sDBTable;
        if (!"".equals(sDBCondition)) sQry += " where " + sDBCondition;
        if (!"".equals(sOrderBy)) sQry += " order by " + sOrderBy;

        try {
                dbStmt = dbConn.getDBConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rsData = dbStmt.executeQuery(sQry);
                bCurrentQryHasError = false;
                sErrorMessage = "";

                return rsData;
        } catch(SQLException ex) {
                sErrorMessage = "SQLException: " + ex.getMessage();
                bCurrentQryHasError = true;
        }

        return rsData;
    }
    
    public ResultSet retrieveStoredProc(String sSPName, SPParam[] arrParams) {
        ResultSet rs = null;
        String sParam = "{call " + sSPName + "(";
        try {
            for(int ii = 0; ii < arrParams.length; ii++) sParam += "?, ";
            sParam = sParam.substring(0, sParam.length() - 2);
            sParam += ")}";

            CallableStatement cStmt = dbConn.getDBConnection().prepareCall(sParam, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            for(SPParam par : arrParams) {
                if(par.getParamType() == TypeParam.In) {
                    switch(par.getFieldType()) {
                        case Types.VARCHAR :
                        case Types.NVARCHAR :
                        case Types.CHAR :
                        case Types.NCHAR :
                                cStmt.setString(par.getParamPosition(), (String)par.getParamValue());
                                break;
                        case Types.INTEGER :
                        case Types.FLOAT :
                        case Types.SMALLINT :
                        case Types.TINYINT :
                        case Types.DOUBLE :
                        case Types.DECIMAL :
                                cStmt.setInt(par.getParamPosition(), (Integer)par.getParamValue());
                                break;
                    }
                } 
            }

            rs = cStmt.executeQuery();
        }
        catch (SQLException ex) {
            sErrorMessage = "SQLException: " + ex.getMessage();
            System.out.println(sErrorMessage);
            bCurrentQryHasError = true;
        }

        return rs;
    }
    
    public SPParam[] executeStoredProc(String sSPName, SPParam[] arrParams) {
        int nOutCount = 0;
        SPParam[] parOut = null;
        String sParam = "{call " + sSPName + "(";
        try {
            for(int ii = 0; ii < arrParams.length; ii++) sParam += "?, ";
            sParam = sParam.substring(0, sParam.length() - 2);
            sParam += ")}";

            CallableStatement cStmt = dbConn.getDBConnection().prepareCall(sParam);
            for(SPParam par : arrParams) {
                if(par.getParamType() == TypeParam.In) {
                    switch(par.getFieldType()) {
                        case Types.VARCHAR :
                        case Types.NVARCHAR :
                        case Types.CHAR :
                        case Types.NCHAR :
                                cStmt.setString(par.getParamPosition(), (String)par.getParamValue());
                                break;
                        case Types.INTEGER :
                        case Types.FLOAT :
                        case Types.SMALLINT :
                        case Types.TINYINT :
                        case Types.DOUBLE :
                        case Types.DECIMAL :
                                cStmt.setInt(par.getParamPosition(), (Integer)par.getParamValue());
                                break;
                    }
                } 
                else if(par.getParamType() == TypeParam.Out) {
                    cStmt.registerOutParameter(par.getParamPosition(), par.getFieldType());
                    nOutCount++;
                }
            }

            cStmt.execute();

            if(nOutCount > 0) {
                parOut = new SPParam[nOutCount];
                int n = 0;
                for(SPParam par : arrParams) {
                    if(par.getParamType() == TypeParam.Out || par.getParamType() == TypeParam.InOut) {
                        switch(par.getFieldType()) {
                            case Types.VARCHAR :
                            case Types.NVARCHAR :
                            case  Types.CHAR :
                            case  Types.NCHAR :
                                SPParam<String> pString = new SPParam<String>(par.getFieldType(), par.getParamType(), 
                                    par.getParamPosition(), par.getParamName(), cStmt.getString(par.getParamPosition()));
                                parOut[n] = pString;
                                break;
                            case  Types.INTEGER :
                            case  Types.FLOAT :
                            case  Types.SMALLINT :
                            case  Types.TINYINT :
                            case  Types.DOUBLE :
                            case  Types.DECIMAL :
                                SPParam<Integer> pInt = new SPParam<Integer>(par.getFieldType(), par.getParamType(), 
                                    par.getParamPosition(), par.getParamName(), cStmt.getInt(par.getParamPosition()));
                                parOut[n] = pInt;
                                break;
                        }
                        n++;
                    }
                }
            }
        }
        catch (SQLException ex) {
            sErrorMessage = "SQLException: " + ex.getMessage();
            System.out.println(sErrorMessage);
            bCurrentQryHasError = true;
        }

        return parOut;
    }
    
    private void getConectionStringFromFile() {
        Path fpPath = null;
        int nDatCnt;
        int nSemiColonCnt = 0;
        try {
            fpPath = Paths.get("config.txt");
        } 
        catch(InvalidPathException ipEx) {
            System.out.println("Path Error " + ipEx);
            System.exit(1);
        }

        try (SeekableByteChannel skChn = Files.newByteChannel(fpPath))
        {
            // Allocate a buffer.
            ByteBuffer meBuf = ByteBuffer.allocate(128);
            do {
                // Read a buffer.
                nDatCnt = skChn.read(meBuf);
                // Stop when end of file is reached.
                if(nDatCnt != -1) {
                    // Rewind the buffer so that it can be read.
                    meBuf.rewind();
                    // Read bytes from the buffer and show
                    // them on the screen as characters.
                    for(int i=0; i < nDatCnt; i++) {
                        char cData = (char)meBuf.get();
                        if(cData == ';') {
                            nSemiColonCnt++;
                        }
                        else
                        {
                            switch(nSemiColonCnt) {
                                case 0:
                                    m_sConnStr += cData;
                                    break;
                                case 1:
                                    m_sUsr += cData;
                                    break;
                                case 2:
                                    m_sPwd += cData;
                                    break;
                            }
                        }
                    }
                }   
            } while(nDatCnt != -1);
        } 
        catch (IOException ieEx) {
            System.out.println("I/O Error " + ieEx);
            System.exit(1);
        }
    }
}
