<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Members - Library Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Library System</a>
            <div class="navbar-nav">
                <a class="nav-link" href="${pageContext.request.contextPath}/library/books">Books</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/library/members">Members</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/loans">Loans</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/reports">Reports</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <h2>Members Management</h2>
        
        <!-- Search Form -->
        <div class="card mb-4">
            <div class="card-header">Search Members</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/library/searchMembers" method="get" class="row g-3">
                    <div class="col-md-8">
                        <input type="text" name="query" class="form-control" placeholder="Search by name or email..." value="${searchQuery}">
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                    <div class="col-md-2">
                        <a href="${pageContext.request.contextPath}/library/members" class="btn btn-secondary">Clear</a>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Add Member Form -->
        <div class="card mb-4">
            <div class="card-header">Add New Member</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/library/addMember" method="post">
                    <div class="row">
                        <div class="col-md-4">
                            <input type="text" name="name" class="form-control" placeholder="Name" required>
                        </div>
                        <div class="col-md-4">
                            <input type="email" name="email" class="form-control" placeholder="Email" required>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary">Add Member</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Members Table -->
        <div class="card">
            <div class="card-header">Member List</div>
            <div class="card-body">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${members}" var="member">
                            <tr>
                                <td>${member.id}</td>
                                <td>${member.name}</td>
                                <td>${member.email}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/library/deleteMember" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="${member.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
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