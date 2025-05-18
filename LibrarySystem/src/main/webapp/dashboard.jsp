<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reports Dashboard - Library Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
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
        <h2>Library Reports Dashboard</h2>
        
        <!-- Summary Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card text-white bg-primary">
                    <div class="card-body">
                        <h5 class="card-title">Total Books</h5>
                        <h2>${stats.totalBooks}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-success">
                    <div class="card-body">
                        <h5 class="card-title">Total Members</h5>
                        <h2>${stats.totalMembers}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-info">
                    <div class="card-body">
                        <h5 class="card-title">Active Loans</h5>
                        <h2>${stats.activeLoans}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-danger">
                    <div class="card-body">
                        <h5 class="card-title">Overdue Books</h5>
                        <h2>${stats.overdueBooks}</h2>
                    </div>
                </div>
            </div>
        </div>

        <!-- Report Links -->
        <div class="row">
            <div class="col-md-6">
                <div class="card mb-3">
                    <div class="card-header">
                        <h5>Available Reports</h5>
                    </div>
                    <div class="list-group list-group-flush">
                        <a href="${pageContext.request.contextPath}/reports/overdue" class="list-group-item list-group-item-action">
                            <i class="bi bi-exclamation-triangle-fill text-danger"></i> Overdue Books Report
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/popular-authors" class="list-group-item list-group-item-action">
                            <i class="bi bi-person-fill text-primary"></i> Popular Authors Report
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/popular-categories" class="list-group-item list-group-item-action">
                            <i class="bi bi-bookmark-fill text-success"></i> Popular Categories Report
                        </a>
                       </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>