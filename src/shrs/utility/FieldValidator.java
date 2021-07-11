/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.utility;

/**
 *
 * @author osraldo
 */
public class FieldValidator {
    public boolean isValidated = false;
	
    private String sValidationMsg = "";
    private String sFieldValue = "";
    private String sFieldname = "";
    private boolean isRequired = false;
    private int nFieldlength = 0;

    public FieldValidator(String sFV, String sFN) {
        sFieldValue = sFV;
        sFieldname = sFN;
    }

    public void setRequired(boolean bReq) {
        isRequired = bReq;
    }

    public void setFieldlength(int nFL) {
        nFieldlength = nFL;
    }

    public String getValidationMessage() {
        if (isValidated == true) 
            return sFieldname + " Field Successfully Validated. ";
        return sValidationMsg;
    }
    
    public void doValidation() {
        if (isRequired == true &&  (sFieldValue == null || "".equals(sFieldValue))) 
            sValidationMsg = sFieldname + " Field is required. ";
        else if ((nFieldlength > 0 && sFieldValue != null) && sFieldValue.length() > nFieldlength)
            sValidationMsg = sFieldname + " Field length must not exceed to " + nFieldlength;
        else
            isValidated = true;
    }
}
