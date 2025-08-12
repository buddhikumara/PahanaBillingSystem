<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  com.pahana.persistence.model.User u =
          (com.pahana.persistence.model.User) session.getAttribute("authUser");
  String role = (u != null && u.getRole() != null) ? u.getRole() : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Pahana | Dashboard</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    .tile { transition: transform .1s; border-radius: 1rem; }
    .tile:hover { transform: translateY(-2px); }
    .tile-icon { font-size: 2rem; }
  </style>
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark">
  <div class="container-fluid">
    <span class="navbar-brand">Pahana Billing System</span>
    <div class="text-white">
      Welcome, <strong><c:out value="${authUser.username}"/>
    </strong>
      &nbsp;|&nbsp; <a class="link-light" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
  </div>
</nav>

<div class="container py-4">
  <h4 class="mb-4">Dashboard</h4>
  <div class="row g-3">
    <!-- ADMIN & USER: Items -->
    <c:if test='<%= "ADMIN".equals(role) || "USER".equals(role) %>'>
      <div class="col-6 col-md-4 col-xl-3">
        <a class="text-decoration-none" href="${pageContext.request.contextPath}/items">
          <div class="card shadow-sm tile">
            <div class="card-body d-flex justify-content-between align-items-center">
              <div><div class="fw-semibold">Items</div><small class="text-muted">Manage & view</small></div>
              <div class="tile-icon">📦</div>
            </div>
          </div>
        </a>
      </div>
    </c:if>

    <!-- ADMIN & USER: Customers -->
    <c:if test='<%= "ADMIN".equals(role) || "USER".equals(role) %>'>
      <div class="col-6 col-md-4 col-xl-3">
        <a class="text-decoration-none" href="${pageContext.request.contextPath}/customers">
          <div class="card shadow-sm tile">
            <div class="card-body d-flex justify-content-between align-items-center">
              <div><div class="fw-semibold">Customers</div><small class="text-muted">Add / View</small></div>
              <div class="tile-icon">👥</div>
            </div>
          </div>
        </a>
      </div>
    </c:if>

    <!-- ADMIN & USER & CASHIER: Billing -->
    <c:if test='<%= "ADMIN".equals(role) || "USER".equals(role) || "CASHIER".equals(role) %>'>
      <div class="col-6 col-md-4 col-xl-3">
        <a class="text-decoration-none" href="${pageContext.request.contextPath}/billing">
          <div class="card shadow-sm tile">
            <div class="card-body d-flex justify-content-between align-items-center">
              <div><div class="fw-semibold">Billing</div><small class="text-muted">Create invoice</small></div>
              <div class="tile-icon">🧾</div>
            </div>
          </div>
        </a>
      </div>
    </c:if>

    <!-- ADMIN: Users -->
    <c:if test='<%= "ADMIN".equals(role) %>'>
      <div class="col-6 col-md-4 col-xl-3">
        <a class="text-decoration-none" href="${pageContext.request.contextPath}/users">
          <div class="card shadow-sm tile">
            <div class="card-body d-flex justify-content-between align-items-center">
              <div><div class="fw-semibold">Users</div><small class="text-muted">Manage staff</small></div>
              <div class="tile-icon">🛡️</div>
            </div>
          </div>
        </a>
      </div>
    </c:if>

    <!-- ADMIN: Reports -->
    <c:if test='<%= "ADMIN".equals(role) %>'>
      <div class="col-6 col-md-4 col-xl-3">
        <a class="text-decoration-none" href="${pageContext.request.contextPath}/reports">
          <div class="card shadow-sm tile">
            <div class="card-body d-flex justify-content-between align-items-center">
              <div><div class="fw-semibold">Reports</div><small class="text-muted">Sales & stock</small></div>
              <div class="tile-icon">📊</div>
            </div>
          </div>
        </a>
      </div>
    </c:if>
  </div>
</div>
</body>
</html>
