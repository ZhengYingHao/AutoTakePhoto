package com.example.zyh.autotakephoto.model;



public class DateData {

    public static final String DATE_ID= "dateId", DATE_YEAR = "year", DATE_MONTH = "month", DATE = "date";

    private int dateId, year, month;
    private String date;

    public DateData() {

    }

    public void setDateId(int dateId) { this.dateId = dateId; }
    public int getDateId() { return this.dateId; }

    public void setYear(int year) { this.year = year; }
    public int getYear() { return this.year; }

    public void setMonth(int month) { this.month = month; }
    public int getMonth() { return this.month; }

    public void setDate(String date) { this.date = date; }
    public String getDate() { return this.date; }
}
