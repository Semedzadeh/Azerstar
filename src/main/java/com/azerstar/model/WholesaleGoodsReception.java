package com.azerstar.model;

public class WholesaleGoodsReception {

    private int wholesale_receipt_id;
    private String date_time;
    private String customer_name;
    private double weight;
    private double unit_price;
    private double total_amount;
    private String note;

    public WholesaleGoodsReception(int wholesale_receipt_id, String date_time, String customer_name, double weight, double unit_price, double total_amount, String note) {
        this.wholesale_receipt_id = wholesale_receipt_id;
        this.date_time = date_time;
        this.customer_name = customer_name;
        this.weight = weight;
        this.unit_price = unit_price;
        this.total_amount = total_amount;
        this.note = note;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
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

    public int getWholesale_receipt_id() {
        return wholesale_receipt_id;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public double getWeight() {
        return weight;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public String getNote() {
        return note;
    }
}
