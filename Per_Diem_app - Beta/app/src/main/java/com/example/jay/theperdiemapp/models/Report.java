package com.example.jay.theperdiemapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Report implements Serializable {

    private String name;
    private String startDate;
    private String endDate;
    private String memo;
    private String reportID;
    private String status;
    private Double total;


    public Report (){}

    public Report (String Name, String StartDate, String EndDate){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        this.name = Name;
        this.startDate = StartDate;
        this.endDate = EndDate;
        this.reportID = Name+ts;
        this.memo = "";
        this.total = 0.00;
        this.status = "Created";

    }

    public Report (String Name, String StartDate, String EndDate, String Memo){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        this.name = Name;
        this.startDate = StartDate;
        this.endDate = EndDate;
        this.memo = Memo;
        this.reportID = Name+ts;
        this.total = 0.00;
        this.status = "Created";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getReportID() {
        return reportID;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
