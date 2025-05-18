<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Popular Authors Report</title>
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
            <h2>Popular Authors Report</h2>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">Back to Reports</a>
        </div>

        <div class="card">
            <div class="card-body">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Author</th>
                            <th>Number of Books</th>
                            <th>Times Borrowed</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${authorStats}" var="stat">
                            <tr>
                                <td>${stat.author}</td>
                                <td>${stat.bookCount}</td>
                                <td>${stat.timesBorrowed}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>