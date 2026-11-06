package com.azerstar.util;

import com.azerstar.config.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Bütün sorğular buradan keçir: bağlantı hər dəfə bağlanır, xəta DbException kimi atılır. */
public final class Db {

    private Db() {
    }

    public static class DbException extends RuntimeException {
        public DbException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @FunctionalInterface
    public interface Work<T> {
        T run(Tx tx) throws SQLException;
    }

    /** Bir əməliyyat daxilində eyni bağlantı ilə işləmək üçün. */
    public static final class Tx {
        private final Connection connection;

        private Tx(Connection connection) {
            this.connection = connection;
        }

        public ObservableList<Row> query(String sql, Object... params) throws SQLException {
            return Db.query(connection, sql, params);
        }

        public int update(String sql, Object... params) throws SQLException {
            return Db.update(connection, sql, params);
        }

        public long insert(String sql, Object... params) throws SQLException {
            return Db.insert(connection, sql, params);
        }
    }

    private static Connection open() {
        Connection c = new DatabaseConnection().getConnection();
        if (c == null) {
            throw new DbException("Bazaya qoşulmaq mümkün olmadı. azerstar.properties faylını və MySQL-in işlədiyini yoxlayın.", null);
        }
        return c;
    }

    public static ObservableList<Row> query(String sql, Object... params) {
        try (Connection c = open()) {
            return query(c, sql, params);
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    public static Row queryOne(String sql, Object... params) {
        ObservableList<Row> rows = query(sql, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public static int update(String sql, Object... params) {
        try (Connection c = open()) {
            return update(c, sql, params);
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    public static long insert(String sql, Object... params) {
        try (Connection c = open()) {
            return insert(c, sql, params);
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    public static <T> T tx(Work<T> work) {
        try (Connection c = open()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(new Tx(c));
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    private static ObservableList<Row> query(Connection c, String sql, Object[] params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                ObservableList<Row> rows = FXCollections.observableArrayList();
                while (rs.next()) {
                    Row row = new Row();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
                return rows;
            }
        }
    }

    private static int update(Connection c, String sql, Object[] params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    private static long insert(Connection c, String sql, Object[] params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0;
            }
        }
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p instanceof LocalDateTime t) {
                ps.setTimestamp(i + 1, Timestamp.valueOf(t));
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }

    private static DbException wrap(SQLException e) {
        e.printStackTrace();
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if (msg.contains("foreign key constraint")) {
            return new DbException("Bu qeyd başqa məlumatlarla bağlıdır, silmək və ya dəyişmək olmur.", e);
        }
        if (msg.contains("Duplicate entry")) {
            return new DbException("Bu ad artıq mövcuddur.", e);
        }
        return new DbException("Baza xətası: " + msg, e);
    }
}
