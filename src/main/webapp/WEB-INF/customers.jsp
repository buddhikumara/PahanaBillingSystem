<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Pahana | Customers</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body{background:#f6f7fb}
    .card{border:none;border-radius:1rem;box-shadow:0 6px 20px rgba(0,0,0,.06)}
    .table thead th{background:#f1f3f8;font-weight:600}
    .btn-rounded{border-radius:999px}
    .search{max-width:340px}
    .badge-role{font-size:.75rem}
  </style>
</head>
<body>
<nav class="navbar navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Pahana Billing System</a>
    <div class="text-white">
      <span class="badge bg-secondary badge-role">${authUser.role}</span>
      &nbsp;Welcome, <strong><c:out value="${authUser.username}"/></strong>
      &nbsp;|&nbsp;<a class="link-light" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
  </div>
</nav>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
  <div class="d-flex align-items-center gap-2">
    <!-- Back to Dashboard -->
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary btn-rounded">← Back</a>
    <h4 class="mb-0">Customers</h4>
  </div>

  <!-- Search (server-side) -->
  <form class="d-flex gap-2" method="get" action="${pageContext.request.contextPath}/customers">
    <input name="q" value="${q}" class="form-control search" placeholder="Search ID, name, phone, email, address">
    <button class="btn btn-outline-primary btn-rounded" type="submit">Search</button>
    <a class="btn btn-light btn-rounded" href="${pageContext.request.contextPath}/customers">Clear</a>
  </form>

  <!-- Add button: ADMIN/CASHIER/USER -->
  <c:if test="${authUser.role == 'ADMIN' || authUser.role == 'CASHIER' || authUser.role == 'USER'}">
    <button class="btn btn-primary btn-rounded" data-bs-toggle="modal" data-bs-target="#addModal">+ Add Customer</button>
  </c:if>
</div>


  <!-- flash / error -->
  <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${errorMessage}"/>
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <div class="card">
    <div class="card-body">
      <div class="table-responsive">
        <table id="tbl" class="table align-middle">
          <thead>
          <tr>
            <th>#</th>
            <th>Customer ID</th>
            <th>Name</th>
            <th>Phone</th>
            <th>Email</th>
            <th>Address</th>
            <th class="text-end">Units</th>
            <th class="text-end">Actions</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="c" items="${customers}" varStatus="st">
            <tr>
              <td>${st.index + 1}</td>
              <td><code>${c.customerId}</code></td>
              <td>${c.name}</td>
              <td>${c.phone}</td>
              <td>${c.email}</td>
              <td>${c.address}</td>
              <td class="text-end"><c:out value="${c.units}"/>&nbsp;</td>
              <td class="text-end">
                <!-- ADMIN: Edit/Delete -->
                <c:if test="${authUser.role == 'ADMIN'}">
                  <button class="btn btn-sm btn-outline-secondary me-1"
                          data-bs-toggle="modal" data-bs-target="#editModal"
                          data-customerid="${c.customerId}"
                          data-name="${c.name}"
                          data-phone="${c.phone}"
                          data-email="${c.email}"
                          data-address="${c.address}"
                          data-units="${c.units}">
                    Edit
                  </button>

                  <form class="d-inline" method="post" action="${pageContext.request.contextPath}/customers"
                        onsubmit="return confirm('Delete customer ${c.customerId}?');">
                    <input type="hidden" name="action" value="delete"/>
                    <input type="hidden" name="customerId" value="${c.customerId}"/>
                    <button class="btn btn-sm btn-outline-danger">Delete</button>
                  </form>
                </c:if>
                <!-- CASHIER/USER: no actions, view only -->
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>

      <c:if test="${empty customers}">
        <div class="text-muted">No customers yet.</div>
      </c:if>
    </div>
  </div>
</div>

<!-- Add Modal (ADMIN, CASHIER, USER) -->
<c:if test="${authUser.role == 'ADMIN' || authUser.role == 'CASHIER' || authUser.role == 'USER'}">
  <div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content rounded-4">
        <form method="post" action="${pageContext.request.contextPath}/customers">
          <input type="hidden" name="action" value="add"/>
          <div class="modal-header">
            <h5 class="modal-title">Add Customer</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Customer ID</label>
              <input name="customerId" class="form-control" maxlength="20" required>
              <div class="form-text">Use your account number or unique ID.</div>
            </div>
            <div class="mb-3"><label class="form-label">Name</label><input name="name" class="form-control" required></div>
            <div class="mb-3"><label class="form-label">Phone</label><input name="phone" class="form-control"></div>
            <div class="mb-3"><label class="form-label">Email</label><input type="email" name="email" class="form-control"></div>
            <div class="mb-3"><label class="form-label">Address</label><textarea name="address" class="form-control" rows="2"></textarea></div>
            <div class="mb-3"><label class="form-label">Units</label><input type="number" name="units" class="form-control" step="1" min="0"></div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
            <button class="btn btn-primary">Save</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</c:if>

<!-- Edit Modal (ADMIN only) -->
<c:if test="${authUser.role == 'ADMIN'}">
  <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content rounded-4">
        <form method="post" action="${pageContext.request.contextPath}/customers">
          <input type="hidden" name="action" value="update"/>
          <div class="modal-header">
            <h5 class="modal-title">Edit Customer</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Customer ID</label>
              <input id="edit-id" name="customerId" class="form-control" readonly>
            </div>
            <div class="mb-3"><label class="form-label">Name</label><input id="edit-name" name="name" class="form-control" required></div>
            <div class="mb-3"><label class="form-label">Phone</label><input id="edit-phone" name="phone" class="form-control"></div>
            <div class="mb-3"><label class="form-label">Email</label><input id="edit-email" type="email" name="email" class="form-control"></div>
            <div class="mb-3"><label class="form-label">Address</label><textarea id="edit-address" name="address" class="form-control" rows="2"></textarea></div>
            <div class="mb-3"><label class="form-label">Units</label><input id="edit-units" type="number" name="units" class="form-control" step="1" min="0"></div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
            <button class="btn btn-primary">Update</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Client-side search filter
  document.getElementById('q')?.addEventListener('input', function () {
    const q = this.value.toLowerCase();
    const rows = document.querySelectorAll('#tbl tbody tr');
    rows.forEach(r => {
      const txt = r.innerText.toLowerCase();
      r.style.display = txt.includes(q) ? '' : 'none';
    });
  });

  // Fill Edit modal (ADMIN)
  const editModal = document.getElementById('editModal');
  if (editModal){
    editModal.addEventListener('show.bs.modal', e => {
      const b = e.relatedTarget;
      document.getElementById('edit-id').value = b.getAttribute('data-customerid');
      document.getElementById('edit-name').value = b.getAttribute('data-name');
      document.getElementById('edit-phone').value = b.getAttribute('data-phone');
      document.getElementById('edit-email').value = b.getAttribute('data-email');
      document.getElementById('edit-address').value = b.getAttribute('data-address');
      document.getElementById('edit-units').value = b.getAttribute('data-units');
    });
  }
</script>
</body>
</html>
