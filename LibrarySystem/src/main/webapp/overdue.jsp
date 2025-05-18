<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Overdue Books Report</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Library System</a>
            <div class="navbar-nav">
                <a class="nav-link" href="${pageContext.request.contextPath}/library/books">Books</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/members">Members</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/loans">Loans</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/reports">Reports</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Overdue Books Report</h2>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">Back to Reports</a>
        </div>

        <div class="card">
            <div class="card-body">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Book Title</th>
                            <th>Author</th>
                            <th>Member</th>
                            <th>Email</th>
                            <th>Loan Date</th>
                            <th>Due Date</th>
                            <th>Days Overdue</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${overdueBooks}" var="book">
                            <tr>
                                <td>${book.bookTitle}</td>
                                <td>${book.author}</td>
                                <td>${book.memberName}</td>
                                <td>${book.memberEmail}</td>
                                <td><fmt:formatDate value="${book.loanDate}" pattern="yyyy-MM-dd"/></td>
                                <td><fmt:formatDate value="${book.dueDate}" pattern="yyyy-MM-dd"/></td>
                                <td><span class="badge bg-danger">${book.daysOverdue} days</span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>