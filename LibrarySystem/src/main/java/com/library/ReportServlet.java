package com.library;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/reports/*")
public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                showReportDashboard(request, response);
            } else {
                switch (pathInfo) {
                    case "/overdue" -> showOverdueBooks(request, response);
                    case "/popular-authors" -> showPopularAuthors(request, response);
                    case "/popular-categories" -> showPopularCategories(request, response);
                    case "/loan-statistics" -> showLoanStatistics(request, response);
                    default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private void showReportDashboard(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        Map<String, Integer> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total books
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM books")) {
                if (rs.next()) {
                    stats.put("totalBooks", rs.getInt("count"));
                }
            }
            
            // Total members
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM members")) {
                if (rs.next()) {
                    stats.put("totalMembers", rs.getInt("count"));
                }
            }
            
            // Active loans
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM loans WHERE returned = FALSE")) {
                if (rs.next()) {
                    stats.put("activeLoans", rs.getInt("count"));
                }
            }
            
            // Overdue books
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM loans WHERE returned = FALSE AND due_date < CURDATE()")) {
                if (rs.next()) {
                    stats.put("overdueBooks", rs.getInt("count"));
                }
            }
        }
        
        request.setAttribute("stats", stats);
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    private void showOverdueBooks(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        List<Map<String, Object>> overdueBooks = new ArrayList<>();
        
        String sql = "SELECT l.id as loan_id, b.title as book_title, b.author, " +
                    "m.name as member_name, m.email as member_email, " +
                    "l.loan_date, l.due_date, " +
                    "DATEDIFF(CURDATE(), l.due_date) as days_overdue " +
                    "FROM loans l " +
                    "JOIN books b ON l.book_id = b.id " +
                    "JOIN members m ON l.member_id = m.id " +
                    "WHERE l.returned = FALSE AND l.due_date < CURDATE() " +
                    "ORDER BY days_overdue DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> book = new HashMap<>();
                book.put("loanId", rs.getInt("loan_id"));
                book.put("bookTitle", rs.getString("book_title"));
                book.put("author", rs.getString("author"));
                book.put("memberName", rs.getString("member_name"));
                book.put("memberEmail", rs.getString("member_email"));
                book.put("loanDate", rs.getDate("loan_date"));
                book.put("dueDate", rs.getDate("due_date"));
                book.put("daysOverdue", rs.getInt("days_overdue"));
                overdueBooks.add(book);
            }
        }
        
        request.setAttribute("overdueBooks", overdueBooks);
        request.getRequestDispatcher("/overdue.jsp").forward(request, response);
    }

    private void showPopularAuthors(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        List<Map<String, Object>> authorStats = new ArrayList<>();
        
        String sql = "SELECT author, COUNT(*) as book_count, " +
                    "SUM(quantity - available_quantity) as times_borrowed " +
                    "FROM books " +
                    "GROUP BY author " +
                    "ORDER BY times_borrowed DESC, book_count DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("author", rs.getString("author"));
                stat.put("bookCount", rs.getInt("book_count"));
                stat.put("timesBorrowed", rs.getInt("times_borrowed"));
                authorStats.add(stat);
            }
        }
        
        request.setAttribute("authorStats", authorStats);
        request.getRequestDispatcher("/popular-authors.jsp").forward(request, response);
    }

    private void showPopularCategories(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        
        String sql = "SELECT category, COUNT(*) as total_books, " +
                    "SUM(quantity) as total_copies, " +
                    "SUM(quantity - available_quantity) as copies_borrowed " +
                    "FROM books " +
                    "WHERE category IS NOT NULL " +
                    "GROUP BY category " +
                    "ORDER BY total_books DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("category", rs.getString("category"));
                stat.put("totalBooks", rs.getInt("total_books"));
                stat.put("totalCopies", rs.getInt("total_copies"));
                stat.put("copiesBorrowed", rs.getInt("copies_borrowed"));
                categoryStats.add(stat);
            }
        }
        
        request.setAttribute("categoryStats", categoryStats);
        request.getRequestDispatcher("/popular-categories.jsp").forward(request, response);
    }

    private void showLoanStatistics(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String period = request.getParameter("period");
        if (period == null) period = "30";
        
        List<Map<String, Object>> loanStats = new ArrayList<>();
        
        String sql = "SELECT DATE(loan_date) as date, COUNT(*) as loan_count " +
                    "FROM loans " +
                    "WHERE loan_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                    "GROUP BY DATE(loan_date) " +
                    "ORDER BY date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(period));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("date", rs.getDate("date"));
                    stat.put("loanCount", rs.getInt("loan_count"));
                    loanStats.add(stat);
                }
            }
        }
        
        request.setAttribute("loanStats", loanStats);
        request.setAttribute("period", period);
        request.getRequestDispatcher("/loan-statistics.jsp").forward(request, response);
    }
}