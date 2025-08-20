<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  // ---- FLASH (move from session -> request, then clear) ----
  String _ok = (String) session.getAttribute("flashSuccess");
  if (_ok != null) { request.setAttribute("flashSuccess", _ok); session.removeAttribute("flashSuccess"); }
  String _err = (String) session.getAttribute("flashError");
  if (_err != null) { request.setAttribute("flashError", _err); session.removeAttribute("flashError"); }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Pahana | Customers</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body{background:#f5f7fb}
    .hero{background:linear-gradient(135deg,#1f7aec,#6bc3ff); color:#fff; border-radius:1rem;
      box-shadow:0 10px 30px rgba(31,122,236,.25)}
    .card{border:none;border-radius:1rem;box-shadow:0 6px 20px rgba(0,0,0,.06)}
    .btn-rounded{border-radius:999px}
    .table thead th{background:#f1f3f8;font-weight:600}
    .empty{border:2px dashed #d9e1f2; border-radius:1rem; padding:40px; color:#6c7a91; background:#fff}
    .modal-content{border:0; border-radius:1rem; box-shadow:0 20px 60px rgba(0,0,0,.2)}
    code{background:#f1f5ff;padding:.15rem .35rem;border-radius:.5rem}
  </style>
</head>
<body>
<!-- Toasts -->
<div class="position-fixed top-0 end-0 p-3" style="z-index:1080">
  <c:if test="${not empty flashSuccess}">
    <div id="toastSuccess" class="toast align-items-center text-bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">${flashSuccess}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    </div>
  </c:if>
  <c:if test="${not empty flashError}">
    <div id="toastError" class="toast align-items-center text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">${flashError}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    </div>
  </c:if>
</div>

<nav class="navbar navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Pahana Billing System</a>
    <div class="text-white">
      <span class="badge bg-secondary">${authUser.role}</span>
      &nbsp;Welcome, <strong><c:out value="${authUser.username}"/></strong>
      &nbsp;|&nbsp;<a class="link-light" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
  </div>
</nav>

<div class="container py-4">
  <div class="hero p-4 mb-4 d-flex flex-wrap justify-content-between align-items-center gap-3">
    <div>
      <h3 class="mb-1">Customers</h3>
      <div class="opacity-75">Search by ID, name, phone, email, or address</div>
    </div>
    <div class="d-flex gap-2">
      <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-light btn-rounded">← Back</a>
      <c:if test="${authUser.role == 'ADMIN' || authUser.role == 'CASHIER' || authUser.role == 'USER'}">
        <button class="btn btn-dark btn-rounded" data-bs-toggle="modal" data-bs-target="#addModal">+ Add Customer</button>
      </c:if>
    </div>
  </div>

  <!-- Search -->
  <form class="d-flex gap-2 mb-3" method="get" action="${pageContext.request.contextPath}/customers">
    <input type="hidden" name="search" value="1"/>
    <input name="q" value="${q}" class="form-control" placeholder="Type here and press Search... (empty = load all)">
    <button class="btn btn-primary btn-rounded" type="submit">Search</button>
    <a class="btn btn-outline-secondary btn-rounded" href="${pageContext.request.contextPath}/customers">Reset</a>
  </form>

  <!-- Empty state -->
  <c:if test="${!hasSearched}">
    <div class="empty text-center">
      <h5 class="mb-2">No data loaded</h5>
      <div>Click <strong>Search</strong> with an empty box to load all customers.</div>
    </div>
  </c:if>

  <!-- Results -->
  <c:if test="${hasSearched}">
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table align-middle">
            <thead>
            <tr>
              <th>#</th><th>Customer ID</th><th>Name</th><th>Phone</th><th>Email</th><th>Address</th>
              <th class="text-end">Units</th><th class="text-end">Actions</th>
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
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>

        <c:if test="${empty customers}">
          <div class="text-muted">No matching records.</div>
        </c:if>
      </div>
    </div>
  </c:if>
</div>

<!-- Add Modal -->
<c:if test="${authUser.role == 'ADMIN' || authUser.role == 'CASHIER' || authUser.role == 'USER'}">
  <div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <form class="needs-validation" novalidate method="post" action="${pageContext.request.contextPath}/customers">
          <input type="hidden" name="action" value="add"/>
          <div class="modal-header">
            <h5 class="modal-title">Add Customer</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>

          <div class="modal-body">
            <!-- server-side error list -->
            <c:if test="${openModal == 'add' && not empty formErrors}">
              <div class="alert alert-danger"><ul class="mb-0">
                <c:forEach var="e" items="${formErrors}"><li>${e}</li></c:forEach></ul>
              </div>
            </c:if>

            <div class="mb-3">
              <label class="form-label">Customer ID</label>
              <input name="customerId" class="form-control" maxlength="20"
                     value="${form.customerId}"
                     pattern="[A-Za-z0-9\\-]+"
                     title="Letters, numbers and hyphen only"
                     required>
              <div class="invalid-feedback">Customer ID is required (letters, numbers, - only).</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Name</label>
              <input name="name" class="form-control" value="${form.name}" required>
              <div class="invalid-feedback">Name is required.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Phone</label>
              <input name="phone" class="form-control" value="${form.phone}"
                     pattern="[0-9+\\- ]{7,15}" title="7–15 digits, you may use + - space">
              <div class="invalid-feedback">Phone must be 7–15 digits (you may use + - space).</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Email</label>
              <input type="email" name="email" id="email" class="form-control" value="${form.email}">
              <div class="invalid-feedback">Please enter a valid email address.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Address</label>
              <textarea name="address" class="form-control" rows="2">${form.address}</textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">Units</label>
              <input type="number" name="units" class="form-control" step="1" min="0" value="${form.units}">
              <div class="invalid-feedback">Units must be a number ≥ 0.</div>
            </div>
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
      <div class="modal-content">
        <form class="needs-validation" novalidate method="post" action="${pageContext.request.contextPath}/customers">
          <input type="hidden" name="action" value="update"/>
          <div class="modal-header">
            <h5 class="modal-title">Edit Customer</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>

          <div class="modal-body">
            <c:if test="${openModal == 'edit' && not empty formErrors}">
              <div class="alert alert-danger"><ul class="mb-0">
                <c:forEach var="e" items="${formErrors}"><li>${e}</li></c:forEach></ul>
              </div>
            </c:if>

            <div class="mb-3">
              <label class="form-label">Customer ID</label>
              <input id="edit-id" name="customerId" class="form-control" readonly value="${form.customerId}">
            </div>
            <div class="mb-3">
              <label class="form-label">Name</label>
              <input id="edit-name" name="name" class="form-control" required value="${form.name}">
              <div class="invalid-feedback">Name is required.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Phone</label>
              <input id="edit-phone" name="phone" class="form-control"
                     pattern="[0-9+\\- ]{7,15}" title="7–15 digits, you may use + - space"
                     value="${form.phone}">
              <div class="invalid-feedback">Phone must be 7–15 digits (you may use + - space).</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Email</label>
              <input id="edit-email" type="email" name="email" class="form-control" value="${form.email}">
              <div class="invalid-feedback">Please enter a valid email address.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Address</label>
              <textarea id="edit-address" name="address" class="form-control" rows="2">${form.address}</textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">Units</label>
              <input id="edit-units" type="number" name="units" class="form-control" step="1" min="0" value="${form.units}">
              <div class="invalid-feedback">Units must be a number ≥ 0.</div>
            </div>
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
  // Bootstrap toasts
  (() => {
    const ok = document.getElementById('toastSuccess');
    const er = document.getElementById('toastError');
    if (ok) new bootstrap.Toast(ok, { delay: 3000 }).show();
    if (er) new bootstrap.Toast(er, { delay: 4000 }).show();
  })();

  // Client-side validation
  (() => {
    const forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(form => {
      form.addEventListener('submit', function (event) {
        if (!form.checkValidity()) {
          event.preventDefault(); event.stopPropagation();
        }
        form.classList.add('was-validated');
      }, false);
    });
  })();

  // Fill Edit modal when opened from table
  const editModal = document.getElementById('editModal');
  if (editModal){
    editModal.addEventListener('show.bs.modal', e => {
      const b = e.relatedTarget;
      if (!b) return;
      document.getElementById('edit-id').value      = b.getAttribute('data-customerid');
      document.getElementById('edit-name').value    = b.getAttribute('data-name');
      document.getElementById('edit-phone').value   = b.getAttribute('data-phone');
      document.getElementById('edit-email').value   = b.getAttribute('data-email');
      document.getElementById('edit-address').value = b.getAttribute('data-address');
      document.getElementById('edit-units').value   = b.getAttribute('data-units');
    });
  }

  // If server told us to reopen a modal (after validation error)
  (() => {
    const open = "${openModal}";
    if (open === "add")  new bootstrap.Modal(document.getElementById('addModal')).show();
    if (open === "edit") new bootstrap.Modal(document.getElementById('editModal')).show();
  })();
</script>
</body>
</html>
