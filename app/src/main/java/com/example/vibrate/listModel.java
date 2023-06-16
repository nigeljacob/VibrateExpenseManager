package com.example.vibrate;

public class listModel {

    String Title;
    String Color;
    String Percentage;
    String Amount;


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getPercentage() {
        return Percentage;
    }

    public String getAmount() {
        return Amount;
    }

    public void setPercentage(String percentage) {
        Percentage = percentage;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public listModel(String title, String color, String percentage, String amount) {
        Title = title;
        Color = color;
        Percentage = percentage;
        Amount = amount;
    }

    public  listModel() {

    }


}
