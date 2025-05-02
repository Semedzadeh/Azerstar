package com.azerstar.Model;

import com.azerstar.config.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {

    public static ObservableList<User> getAllUsers() {
        ObservableList<User> usersList = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM user_account"; // Əgər cədvəl adın fərqlidirsə, deyin dəyişək

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int branchId = resultSet.getInt("branch_id");

                usersList.add(new User(accountId, firstname, lastname, username, password, branchId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usersList;
    }
}
