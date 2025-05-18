<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Loans - Library Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Library System</a>
            <div class="navbar-nav">
                <a class="nav-link" href="${pageContext.request.contextPath}/library/books">Books</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/members">Members</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/library/loans">Loans</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/reports">Reports</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <h2>Loans Management</h2>
        
        <!-- Add Loan Form -->
        <div class="card mb-4">
            <div class="card-header">Create New Loan</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/library/addLoan" method="post">
                    <div class="row">
                        <div class="col-md-4">
                            <select name="bookId" class="form-control" required>
                                <option value="">Select Book</option>
                                <c:forEach items="${availableBooks}" var="book">
                                    <option value="${book.id}">${book.title} by ${book.author}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <select name="memberId" class="form-control" required>
                                <option value="">Select Member</option>
                                <c:forEach items="${members}" var="member">
                                    <option value="${member.id}">${member.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary">Create Loan</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Loans Table -->
        <div class="card">
            <div class="card-header">Loan List</div>
            <div class="card-body">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Book</th>
                            <th>Member</th>
                            <th>Loan Date</th>
                            <th>Return Date</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${loans}" var="loan">
                            <tr>
                                <td>${loan[0]}</td>
                                <td>${loan[1]}</td>
                                <td>${loan[2]}</td>
                                <td>${loan[3]}</td>
                                <td>${loan[4]}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${loan[5]}">
                                            <span class="badge bg-success">Returned</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning">Borrowed</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${!loan[5]}">
                                        <form action="${pageContext.request.contextPath}/library/returnBook" method="post" style="display:inline;">
                                            <input type="hidden" name="loanId" value="${loan[0]}">
                                            <button type="submit" class="btn btn-success btn-sm">Return</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>