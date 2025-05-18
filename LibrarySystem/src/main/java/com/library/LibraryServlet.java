package com.library;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/library/*")
public class LibraryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        try {
            switch (action) {
                case "/books":
                    listBooks(request, response);
                    break;
                case "/searchBooks":
                    searchBooks(request, response);
                    break;
                case "/members":
                    listMembers(request, response);
                    break;
                case "/searchMembers":
                    searchMembers(request, response);
                    break;
                case "/loans":
                    listLoans(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();

        try {
            switch (action) {
                case "/addBook":
                    addBook(request, response);
                    break;
                case "/deleteBook":
                    deleteBook(request, response);
                    break;
                case "/updateStock":
                    updateStock(request, response);
                    break;
                case "/addMember":
                    addMember(request, response);
                    break;
                case "/deleteMember":
                    deleteMember(request, response);
                    break;
                case "/addLoan":
                    addLoan(request, response);
                    break;
                case "/returnBook":
                    returnBook(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response)
        throws SQLException, ServletException, IOException {
    List<Book> books = new ArrayList<>();
    String sql = "SELECT * FROM books ORDER BY title";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(rs.getString("author"));
            book.setIsbn(rs.getString("isbn"));
            book.setCategory(rs.getString("category"));
            book.setQuantity(rs.getInt("quantity"));
            book.setAvailableQuantity(rs.getInt("available_quantity"));
            book.setAvailable(rs.getInt("available_quantity") > 0);
            books.add(book);
        }
    }

    request.setAttribute("books", books);
    request.getRequestDispatcher("/books.jsp").forward(request, response);
}

private void searchBooks(HttpServletRequest request, HttpServletResponse response)
        throws SQLException, ServletException, IOException {
    String searchQuery = request.getParameter("query");
    String searchType = request.getParameter("searchType");
    
    if (searchQuery == null || searchQuery.trim().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/library/books");
        return;
    }

    List<Book> books = new ArrayList<>();
    String sql;
    
    if ("title".equals(searchType)) {
        sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";
    } else if ("author".equals(searchType)) {
        sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY title";
    } else if ("isbn".equals(searchType)) {
        sql = "SELECT * FROM books WHERE isbn LIKE ? ORDER BY title";
    } else if ("category".equals(searchType)) {
        sql = "SELECT * FROM books WHERE category LIKE ? ORDER BY title";
    } else {
        sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? OR category LIKE ? ORDER BY title";
    }

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String searchPattern = "%" + searchQuery + "%";
        stmt.setString(1, searchPattern);
        
        if (searchType == null || "all".equals(searchType)) {
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setQuantity(rs.getInt("quantity"));
                book.setAvailableQuantity(rs.getInt("available_quantity"));
                book.setAvailable(rs.getInt("available_quantity") > 0);
                books.add(book);
            }
        }
    }

    request.setAttribute("books", books);
    request.setAttribute("searchQuery", searchQuery);
    request.setAttribute("searchType", searchType);
    request.getRequestDispatcher("/books.jsp").forward(request, response);
}

private void addBook(HttpServletRequest request, HttpServletResponse response)
        throws SQLException, IOException {
    String title = request.getParameter("title");
    String author = request.getParameter("author");
    String isbn = request.getParameter("isbn");
    String category = request.getParameter("category");
    int quantity = Integer.parseInt(request.getParameter("quantity"));

    String sql = "INSERT INTO books (title, author, isbn, category, quantity, available_quantity, available) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, title);
        stmt.setString(2, author);
        stmt.setString(3, isbn);
        stmt.setString(4, category);
        stmt.setInt(5, quantity);
        stmt.setInt(6, quantity);
        stmt.setBoolean(7, quantity > 0);
        stmt.executeUpdate();
    }

    response.sendRedirect(request.getContextPath() + "/library/books");
}

    private void deleteBook(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

        response.sendRedirect(request.getContextPath() + "/library/books");
    }

    private void updateStock(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        int newQuantity = Integer.parseInt(request.getParameter("quantity"));

        // First, get the current book info
        String selectSql = "SELECT * FROM books WHERE id = ?";
        int currentLoaned = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            
            selectStmt.setInt(1, bookId);
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                int currentAvailable = rs.getInt("available_quantity");
                currentLoaned = currentQuantity - currentAvailable;
            }
        }

        // Update the stock
        String updateSql = "UPDATE books SET quantity = ?, available_quantity = ?, available = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            int newAvailable = newQuantity - currentLoaned;
            if (newAvailable < 0) newAvailable = 0;
            
            updateStmt.setInt(1, newQuantity);
            updateStmt.setInt(2, newAvailable);
            updateStmt.setBoolean(3, newAvailable > 0);
            updateStmt.setInt(4, bookId);
            updateStmt.executeUpdate();
        }

        response.sendRedirect(request.getContextPath() + "/library/books");
    }

    private void listMembers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setEmail(rs.getString("email"));
                members.add(member);
            }
        }

        request.setAttribute("members", members);
        request.getRequestDispatcher("/members.jsp").forward(request, response);
    }

    private void searchMembers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String searchQuery = request.getParameter("query");
        
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/library/members");
            return;
        }

        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name LIKE ? OR email LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchQuery + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setName(rs.getString("name"));
                    member.setEmail(rs.getString("email"));
                    members.add(member);
                }
            }
        }

        request.setAttribute("members", members);
        request.setAttribute("searchQuery", searchQuery);
        request.getRequestDispatcher("/members.jsp").forward(request, response);
    }

    private void addMember(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        String sql = "INSERT INTO members (name, email) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
        }

        response.sendRedirect(request.getContextPath() + "/library/members");
    }

    private void deleteMember(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

        response.sendRedirect(request.getContextPath() + "/library/members");
    }

    private void listLoans(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Object[]> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.title, m.name FROM loans l " +
                    "JOIN books b ON l.book_id = b.id " +
                    "JOIN members m ON l.member_id = m.id " +
                    "ORDER BY l.loan_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] loan = new Object[7];
                loan[0] = rs.getInt("id");
                loan[1] = rs.getString("title");
                loan[2] = rs.getString("name");
                loan[3] = rs.getDate("loan_date");
                loan[4] = rs.getDate("return_date");
                loan[5] = rs.getBoolean("returned");
                loans.add(loan);
            }
        }

        // Get available books and members for the form
        List<Book> availableBooks = new ArrayList<>();
        List<Member> members = new ArrayList<>();

        String bookSql = "SELECT * FROM books WHERE available_quantity > 0";
        String memberSql = "SELECT * FROM members";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(bookSql);
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                availableBooks.add(book);
            }

            rs = stmt.executeQuery(memberSql);
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                members.add(member);
            }
        }

        request.setAttribute("loans", loans);
        request.setAttribute("availableBooks", availableBooks);
        request.setAttribute("members", members);
        request.getRequestDispatcher("/loans.jsp").forward(request, response);
    }

    private void addLoan(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        int memberId = Integer.parseInt(request.getParameter("memberId"));

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Check if book is available
            String checkSql = "SELECT available_quantity FROM books WHERE id = ?";
            int availableQuantity = 0;
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    availableQuantity = rs.getInt("available_quantity");
                }
            }

            if (availableQuantity <= 0) {
                throw new SQLException("Book is not available");
            }

            // Add loan record
            String sql = "INSERT INTO loans (book_id, member_id, loan_date, returned) VALUES (?, ?, CURDATE(), false)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, bookId);
                stmt.setInt(2, memberId);
                stmt.executeUpdate();
            }

            // Update book availability
            String updateSql = "UPDATE books SET available_quantity = available_quantity - 1, available = (available_quantity - 1) > 0 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, bookId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }

        response.sendRedirect(request.getContextPath() + "/library/loans");
    }
    

    private void returnBook(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int loanId = Integer.parseInt(request.getParameter("loanId"));

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Get book ID
            int bookId;
            String selectSql = "SELECT book_id FROM loans WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setInt(1, loanId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    bookId = rs.getInt("book_id");
                } else {
                    throw new SQLException("Loan not found");
                }
            }

            // Update loan record
            String updateLoanSql = "UPDATE loans SET return_date = CURDATE(), returned = true WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateLoanSql)) {
                stmt.setInt(1, loanId);
                stmt.executeUpdate();
            }

            // Update book availability
            String updateBookSql = "UPDATE books SET available_quantity = available_quantity + 1, available = true WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookSql)) {
                stmt.setInt(1, bookId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }

        response.sendRedirect(request.getContextPath() + "/library/loans");
    }
}