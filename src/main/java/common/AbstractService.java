package common;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public abstract class AbstractService {

//    private static final String URL = "jdbc:mysql://localhost:3306/bugtracker";
//    private static final String USER = "root";
//    private static final String PASSWORD = "Mustafa_1903";
//
//    public static Connection getConnection() throws SQLException {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }

    //this gets the DB props from another file for more secure way and if needed change in only 1 place.
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream input = AbstractService.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new RuntimeException("database.properties file not found");
            }
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void close(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    public void close(PreparedStatement ps, Connection connection) throws SQLException {
        if (ps != null) {
            ps.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    public void close(ResultSet rs, PreparedStatement ps, Connection connection) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        close(ps, connection);
    }
}
