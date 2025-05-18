<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">Library System</a>
            <div class="navbar-nav">
                <a class="nav-link" href="${pageContext.request.contextPath}/library/books">Books</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/members">Members</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/loans">Loans</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/reports">Reports</a>
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="jumbotron">
            <h1 class="display-4">Welcome to Library Management System</h1>
            <p class="lead">Manage your books, members, and loans efficiently.</p>
            <hr class="my-4">
            <div class="row">
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Books</h5>
                            <p class="card-text">Manage your book inventory</p>
                            <a href="${pageContext.request.contextPath}/library/books" class="btn btn-primary">Go to Books</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Members</h5>
                            <p class="card-text">Manage library members</p>
                            <a href="${pageContext.request.contextPath}/library/members" class="btn btn-primary">Go to Members</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Loans</h5>
                            <p class="card-text">Manage book loans</p>
                            <a href="${pageContext.request.contextPath}/library/loans" class="btn btn-primary">Go to Loans</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>