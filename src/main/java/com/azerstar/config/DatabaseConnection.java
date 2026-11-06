package com.azerstar.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    private static final String CONFIG_FILE = "azerstar.properties";

    private Connection databaseLink;

    public Connection getConnection() {
        Properties config = readConfig();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url(config), config.getProperty("db.user"), config.getProperty("db.password"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaseLink;
    }

    // Parol kodda saxlanmasın deyə qoşulma məlumatları layihə qovluğundakı azerstar.properties faylından oxunur
    public static Properties readConfig() {
        Properties config = new Properties();
        config.setProperty("db.host", "localhost");
        config.setProperty("db.port", "3306");
        config.setProperty("db.name", "azerstar");
        config.setProperty("db.user", "root");
        config.setProperty("db.password", "");
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            System.err.println(CONFIG_FILE + " tapılmadı, susmaya görə dəyərlər istifadə olunur");
        }
        return config;
    }

    private static String url(Properties config) {
        return "jdbc:mysql://" + config.getProperty("db.host") + ":" + config.getProperty("db.port") + "/" + config.getProperty("db.name");
    }

    /** Verilən ayarlarla qoşulmağa çalışır. Uğurlu olarsa null, olmazsa xəta mətnini qaytarır. */
    public static String test(Properties config) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.setLoginTimeout(5);
            try (Connection c = DriverManager.getConnection(url(config), config.getProperty("db.user"), config.getProperty("db.password"))) {
                return c.isValid(3) ? null : "Bağlantı qurulub, amma cavab vermir.";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /** Ayarları azerstar.properties faylına yazır; növbəti sorğudan bu ayarlarla işləyir. */
    public static void saveConfig(Properties config) throws IOException {
        try (Writer out = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            Properties clean = new Properties();
            for (String key : new String[]{"db.host", "db.port", "db.name", "db.user", "db.password"}) {
                clean.setProperty(key, config.getProperty(key, ""));
            }
            clean.store(out, null);
        }
    }
}
