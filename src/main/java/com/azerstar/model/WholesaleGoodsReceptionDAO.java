package com.azerstar.model;

import com.azerstar.config.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class WholesaleGoodsReceptionDAO {

    public static ObservableList<WholesaleGoodsReception> getAllWholesaleGoods(){
        ObservableList<WholesaleGoodsReception> wholesaleGoodsReceptionList = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "select * from wholesale_receipt";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int wholesale_receipt_id = resultSet.getInt("wholesale_receipt_id");
                String date_time = resultSet.getString("date_time");
                String customer_name = resultSet.getString("customer_name");
                double weight = resultSet.getDouble("weight");
                double unit_price = resultSet.getDouble("unit_price");
                double total_amount = resultSet.getDouble("total_amount");
                String note = resultSet.getString("note");

                wholesaleGoodsReceptionList.add(new WholesaleGoodsReception(wholesale_receipt_id,date_time,customer_name,weight,unit_price,total_amount,note));

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return wholesaleGoodsReceptionList;
    }

}
