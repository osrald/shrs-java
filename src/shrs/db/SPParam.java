/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shrs.db;

/**
 *
 * @author osraldo
 */
public class SPParam<T> {
    private int nFieldType;
    private TypeParam paramType;  
    private int nPosition;
    private String sName;
    private T gValue;

    public SPParam(int nFT, TypeParam tp, int nPos, String sN, T gVal) {
            nFieldType = nFT;
            paramType = tp;
            nPosition = nPos;
            sName = sN;
            gValue = gVal;
    }

    public int getFieldType() {
            return nFieldType;
    }

    public void setFieldType(int nFT) {
            nFieldType = nFT;
    }

    public TypeParam getParamType() {
            return paramType;
    }

    public void setParamType(TypeParam tp) {
            paramType = tp;
    }

    public int getParamPosition() {
            return nPosition;
    }

    public void setParamPosition(int nPos) {
            nPosition = nPos;
    }

    public String getParamName() {
            return sName;
    }

    public void setParamName(String sPN) {
            sName = sPN;
    }

    public T getParamValue() {
            return gValue;
    }

    public void setParamValue(T gVal) {
            gValue = gVal;
    }
}
