package com.azerstar.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    private static final String CONFIG_FILE = "azerstar.properties";

    private Connection databaseLink;

    public Connection getConnection() {

        Properties config = loadConfig();

        String host = config.getProperty("db.host", "localhost");
        String port = config.getProperty("db.port", "3306");
        String databaseName = config.getProperty("db.name", "azerstar");
        String databaseUser = config.getProperty("db.user", "root");
        String databasePassword = config.getProperty("db.password", "");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url,databaseUser,databasePassword);

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;

    }

    // Parol kodda saxlanmasın deyə qoşulma məlumatları layihə qovluğundakı azerstar.properties faylından oxunur
    private Properties loadConfig() {
        Properties config = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            System.err.println(CONFIG_FILE + " tapılmadı, susmaya görə dəyərlər istifadə olunur");
        }
        return config;
    }
}
