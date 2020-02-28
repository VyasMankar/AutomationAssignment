package com.biosense.aotomationapp.model;

public class info_model
{
    private String colName;
    private String dataType;
    private String dbCol;
    private String data;
    private int rawid;

    public String getColName()
    {
        return colName;
    }

    public info_model setColName(String colName)
    {
        this.colName = colName;
        return this;
    }

    public String getDataType()
    {
        return dataType;
    }

    public info_model setDataType(String dataType)
    {
        this.dataType = dataType;
        return this;
    }

    public String getDbCol()
    {
        return dbCol;
    }

    public info_model setDbCol(String dbCol)
    {
        this.dbCol = dbCol;
        return this;
    }

    public String getData()
    {
        return data;
    }

    public info_model setData(String data)
    {
        this.data = data;
        return this;
    }

    public int getRawid()
    {
        return rawid;
    }

    public info_model setRawid(int rawid)
    {
        this.rawid = rawid;
        return this;
    }
}
