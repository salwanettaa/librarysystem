<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Books - Library Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Library System</a>
            <div class="navbar-nav">
                <a class="nav-link active" href="${pageContext.request.contextPath}/library/books">Books</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/members">Members</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/library/loans">Loans</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/reports">Reports</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <h2>Books Management</h2>
        
        <!-- Search Form -->
        <div class="card mb-4">
            <div class="card-header">Search Books</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/library/searchBooks" method="get" class="row g-3">
                    <div class="col-md-6">
                        <input type="text" name="query" class="form-control" placeholder="Search..." value="${searchQuery}">
                    </div>
                    <div class="col-md-4">
                        <select name="searchType" class="form-control">
                            <option value="all" ${searchType == 'all' ? 'selected' : ''}>All Fields</option>
                            <option value="title" ${searchType == 'title' ? 'selected' : ''}>Title</option>
                            <option value="author" ${searchType == 'author' ? 'selected' : ''}>Author</option>
                            <option value="isbn" ${searchType == 'isbn' ? 'selected' : ''}>ISBN</option>
                            <option value="category" ${searchType == 'category' ? 'selected' : ''}>Category</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Add Book Form -->
        <div class="card mb-4">
            <div class="card-header">Add New Book</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/library/addBook" method="post">
                    <div class="row">
                        <div class="col-md-3">
                            <input type="text" name="title" class="form-control" placeholder="Title" required>
                        </div>
                        <div class="col-md-2">
                            <input type="text" name="author" class="form-control" placeholder="Author" required>
                        </div>
                        <div class="col-md-2">
                            <input type="text" name="isbn" class="form-control" placeholder="ISBN" required>
                        </div>
                        <div class="col-md-2">
                            <select name="category" class="form-control" required>
                                <option value="">Select Category</option>
                                <option value="Fiction">Fiction</option>
                                <option value="Non-Fiction">Non-Fiction</option>
                                <option value="Science">Science</option>
                                <option value="Technology">Technology</option>
                                <option value="History">History</option>
                                <option value="Biography">Biography</option>
                                <option value="Literature">Literature</option>
                                <option value="Education">Education</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>
                        <div class="col-md-1">
                            <input type="number" name="quantity" class="form-control" placeholder="Qty" min="1" required>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary">Add Book</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Books Table -->
        <div class="card">
            <div class="card-header">Book List</div>
            <div class="card-body">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Author</th>
                            <th>ISBN</th>
                            <th>Category</th>
                            <th>Total Stock</th>
                            <th>Available</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${books}" var="book">
                            <tr>
                                <td>${book.id}</td>
                                <td>${book.title}</td>
                                <td>${book.author}</td>
                                <td>${book.isbn}</td>
                                <td>${book.category}</td>
                                <td>${book.quantity}</td>
                                <td>${book.availableQuantity}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${book.availableQuantity > 0}">
                                            <span class="badge bg-success">Available</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger">Out of Stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <button type="button" class="btn btn-warning btn-sm" onclick="showUpdateStockModal(${book.id}, ${book.quantity})">Update Stock</button>
                                    <form action="${pageContext.request.contextPath}/library/deleteBook" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="${book.id}">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this book?')">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Update Stock Modal -->
    <div class="modal fade" id="updateStockModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Update Stock</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/library/updateStock" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="bookId" id="updateBookId">
                        <div class="mb-3">
                            <label for="updateQuantity" class="form-label">New Quantity</label>
                            <input type="number" class="form-control" id="updateQuantity" name="quantity" min="0" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Update</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function showUpdateStockModal(bookId, currentQuantity) {
            document.getElementById('updateBookId').value = bookId;
            document.getElementById('updateQuantity').value = currentQuantity;
            new bootstrap.Modal(document.getElementById('updateStockModal')).show();
        }
    </script>
</body>
</html>