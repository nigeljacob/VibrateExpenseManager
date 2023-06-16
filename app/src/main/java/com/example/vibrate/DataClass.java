package com.example.vibrate;

import java.util.Map;

public class DataClass {

    private String Title;
    private String Catagory;
    private String Description;
    private String Amount;
    private String Email;
    private String Bill;
    private String time;
    private String UniqueID;
    private String Month;
    private String Year;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCatagory() {
        return Catagory;
    }

    public void setCatagory(String catagory) {
        Catagory = catagory;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBill() {
        return Bill;
    }

    public void setBill(String bill) {
        Bill = bill;
    }

    public String getTimestamp() {
        return time;
    }
    public void setTimestamp(String timeStamp) {
        time= timeStamp;
    }

    public String getUniqueID() {
        return UniqueID;
    }

    public void setUniqueID(String uniqueID) {
        UniqueID = uniqueID;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }


    public DataClass(String title, String catagory, String description, String amount, String email, String bill, String timestamp, String uniqueID, String month, String year ) {
        Title = title;
        Catagory = catagory;
        Description = description;
        Amount = amount;
        Email = email;
        Bill = bill;
        time = timestamp;
        UniqueID = uniqueID;
        Month = month;
        Year = year;
    }

    public  DataClass() {

    }
}
