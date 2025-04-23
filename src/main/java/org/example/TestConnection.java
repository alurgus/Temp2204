package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/humanfriends";
        String username = "root";
        String password = "Al10082000";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if (conn != null) {
                System.out.println("✅ Подключение к базе данных успешно!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Ошибка подключения:");
            e.printStackTrace();
        }
    }
}