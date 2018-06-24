package com.example.jay.theperdiemapp.models;

public class Expense {

    private String expenseID;
    private String expenseName;
    private Double expenseTotal;
    private String expenseDate;
    private String expenseLocation;
    private String expenseMemo;
    private String expenseCategory;
    private String addressToImage;
    private String associatedReport;
    private String mileageFromAddress;
    private String mileageToAddress;
    private Double mileagePerMileRate;
    private Double mileageTotalMiles;



    public Expense(){
    }


    public Expense(String Name, String Date, Double Total, String ReportID, String Catagory, String FromAddress){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        this.expenseName = Name;
        this.expenseDate = Date;
        this.expenseTotal = Total;
        this.associatedReport = ReportID;
        this.expenseCategory = Catagory;
        this.expenseID = Name +" "+ ts;
        this.mileageFromAddress = "N/A";
        this.mileageToAddress = "N/A";
        this.mileagePerMileRate = 0.0;
        this.mileageTotalMiles = 0.0;
        this.expenseLocation = FromAddress;
        this.addressToImage = "N/A";
        this.expenseMemo = "No Memo Provided";

    }


    public Expense(String Name, String Date, Double Total, String ReportID, String Catagory, String FromAddress, String url){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        this.expenseName = Name;
        this.expenseDate = Date;
        this.expenseTotal = Total;
        this.associatedReport = ReportID;
        this.expenseCategory = Catagory;
        this.expenseID = Name +" "+ ts;
        this.mileageFromAddress = "N/A";
        this.mileageToAddress = "N/A";
        this.mileagePerMileRate = 0.0;
        this.mileageTotalMiles = 0.0;
        this.expenseLocation = FromAddress;
        this.addressToImage = url;
        this.expenseMemo = "No Memo Provided";

    }



    public Expense(String From, String To, String Date, Double Rate, Double Distance, Double Total, String ReportID, String Catagory){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        this.mileageFromAddress = From;
        this.mileageToAddress = To;
        this.expenseDate = Date;
        this.mileagePerMileRate = Rate;
        this.mileageTotalMiles = Distance;
        this.expenseTotal = Total;
        this.associatedReport = ReportID;
        this.expenseName = "Mileage To: "+To;
        this.expenseLocation = From;
        this.expenseCategory = Catagory;
        this.addressToImage = "N/A";
        this.expenseID = To +" "+ ts;
        this.expenseMemo = "No Memo Provided";

    }



    public Expense(String From, String To, String Date, Double Rate, Double Distance, Double Total, String ReportID, String Catagory, String Memo){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        this.mileageFromAddress = From;
        this.mileageToAddress = To;
        this.expenseDate = Date;
        this.mileagePerMileRate = Rate;
        this.mileageTotalMiles = Distance;
        this.expenseTotal = Total;
        this.associatedReport = ReportID;
        this.expenseName = "Mileage To: "+To;
        this.expenseLocation = From;
        this.expenseCategory = Catagory;
        this.addressToImage = "N/A";
        this.expenseID = To +" "+ ts;
        this.expenseMemo = Memo;

    }

    public String getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public Double getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(Double expenseTotal) {
        this.expenseTotal = expenseTotal;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseLocation() {
        return expenseLocation;
    }

    public void setExpenseLocation(String expenseLocation) {
        this.expenseLocation = expenseLocation;
    }

    public String getExpenseMemo() {
        return expenseMemo;
    }

    public void setExpenseMemo(String expenseMemo) {
        this.expenseMemo = expenseMemo;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public String getAddressToImage() {
        return addressToImage;
    }

    public void setAddressToImage(String addressToImage) {
        this.addressToImage = addressToImage;
    }

    public String getAssociatedReport() {
        return associatedReport;
    }

    public void setAssociatedReport(String associatedReport) {
        this.associatedReport = associatedReport;
    }

    public String getMileageFromAddress() {
        return mileageFromAddress;
    }

    public void setMileageFromAddress(String mileageFromAddress) {
        this.mileageFromAddress = mileageFromAddress;
    }

    public String getMileageToAddress() {
        return mileageToAddress;
    }

    public void setMileageToAddress(String mileageToAddress) {
        this.mileageToAddress = mileageToAddress;
    }

    public Double getMileagePerMileRate() {
        return mileagePerMileRate;
    }

    public void setMileagePerMileRate(Double mileagePerMileRate) {
        this.mileagePerMileRate = mileagePerMileRate;
    }

    public Double getMileageTotalMiles() {
        return mileageTotalMiles;
    }

    public void setMileageTotalMiles(Double mileageTotalMiles) {
        this.mileageTotalMiles = mileageTotalMiles;
    }
}
