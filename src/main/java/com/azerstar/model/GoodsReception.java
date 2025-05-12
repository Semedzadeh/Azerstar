package com.azerstar.model;

public class GoodsReception {

    private int retail_receipt_id;
    private String date_time;
    private String customer_name;
    private double weight;
    private double unit_price;
    private double total_amount;
    private String note;

    public GoodsReception(int retail_receipt_id, String date_time, String customer_name, double weight, double unit_price, double total_amount, String note) {
        this.retail_receipt_id = retail_receipt_id;
        this.date_time = date_time;
        this.customer_name = customer_name;
        this.weight = weight;
        this.unit_price = unit_price;
        this.total_amount = total_amount;
        this.note = note;
    }
    public GoodsReception(String customer_name, String date_time, double weight, double unit_price, double total_amount, String note) {
        this.customer_name = customer_name;
        this.date_time = date_time;
        this.weight = weight;
        this.unit_price = unit_price;
        this.total_amount = total_amount;
        this.note = note;
    }


    public String getCustomer_name() {
        return customer_name;
    }

    public String getDate_time() {
        return date_time;
    }

    public int getRetail_receipt_id() {
        return retail_receipt_id;
    }

    public double getWeight() {
        return weight;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public String getNote() {
        return note;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
