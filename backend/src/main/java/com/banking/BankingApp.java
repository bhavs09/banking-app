package com.banking;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;

public class BankingApp {
    static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/securebank");
    static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    static final String DB_PASS = System.getenv().getOrDefault("DB_PASS", "password");

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Health check
        server.createContext("/health", exchange -> {
            String response = "{\"status\": \"UP\", \"service\": \"securebank-backend\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        // Get accounts
        server.createContext("/api/accounts", exchange -> {
            StringBuilder sb = new StringBuilder("{\"accounts\":[");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT a.account_number, u.name, a.balance FROM accounts a " +
                     "JOIN users u ON a.user_id = u.id")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    sb.append("{\"account\":\"").append(rs.getString(1))
                      .append("\",\"name\":\"").append(rs.getString(2))
                      .append("\",\"balance\":").append(rs.getDouble(3)).append("}");
                    first = false;
                }
            } catch (Exception e) {
                sb = new StringBuilder("{\"error\":\"" + e.getMessage() + "\"}");
            }
            sb.append("]}");
            String response = sb.toString();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        // Get transactions
        server.createContext("/api/transactions", exchange -> {
            StringBuilder sb = new StringBuilder("{\"transactions\":[");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT from_account, to_account, amount, type, description FROM transactions")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    sb.append("{\"from\":\"").append(rs.getString(1))
                      .append("\",\"to\":\"").append(rs.getString(2))
                      .append("\",\"amount\":").append(rs.getDouble(3))
                      .append(",\"type\":\"").append(rs.getString(4))
                      .append("\",\"description\":\"").append(rs.getString(5)).append("\"}");
                    first = false;
                }
            } catch (Exception e) {
                sb = new StringBuilder("{\"error\":\"" + e.getMessage() + "\"}");
            }
            sb.append("]}");
            String response = sb.toString();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.start();
        System.out.println("SecureBank Backend started on port 8080");
    }
}