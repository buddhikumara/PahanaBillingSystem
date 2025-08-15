<%--
  Created by IntelliJ IDEA.
  User: Buddhi
  Date: 16/08/2025
  Time: 01:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  // Move flash messages from session -> request, then clear
  String _ok = (String) session.getAttribute("flashSuccess");
  if (_ok != null) { request.setAttribute("flashSuccess", _ok); session.removeAttribute("flashSuccess"); }
  String _err = (String) session.getAttribute("flashError");
  if (_err != null) { request.setAttribute("flashError", _err); session.removeAttribute("flashError"); }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Pahana | Users</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body{background:#f5f7fb}
    .hero{background:linear-gradient(135deg,#0f9d58,#34d399); color:#fff; border-radius:1rem; box-shadow:0 10px 30px rgba(0,0,0,.2)}
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
      <div class="d-flex"><div class="toast-body">${flashSuccess}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button></div>
    </div>
  </c:if>
  <c:if test="${not empty flashError}">
    <div id="toastError" class="toast align-items-center text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex"><div class="toast-body">${flashError}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button></div>
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
      <h3 class="mb-1">Users</h3>
      <div class="opacity-75">Search by ID, username, or role</div>
    </div>
    <div class="d-flex gap-2">
      <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-light btn-rounded">← Back</a>
      <button class="btn btn-dark btn-rounded" data-bs-toggle="modal" data-bs-target="#addModal">+ Add User</button>
    </div>
  </div>

  <!-- Search (no auto-load; empty => all) -->
  <form class="d-flex gap-2 mb-3" method="get" action="${pageContext.request.contextPath}/users">
    <input type="hidden" name="search" value="1"/>
    <input name="q" value="${q}" class="form-control" placeholder="Type here and press Search... (empty = load all)">
    <button class="btn btn-primary btn-rounded" type="submit">Search</button>
    <a class="btn btn-outline-secondary btn-rounded" href="${pageContext.request.contextPath}/users">Reset</a>
  </form>

  <!-- Empty state before first search -->
  <c:if test="${!hasSearched}">
    <div class="empty text-center">
      <h5 class="mb-2">No data loaded</h5>
      <div>Click <strong>Search</strong> with an empty box to load all users.</div>
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
              <th>#</th>
              <th>ID</th>
              <th>Username</th>
              <th>Role</th>
              <th>Created</th>
              <th class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${users}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td><code>${u.id}</code></td>
                <td>${u.username}</td>
                <td><span class="badge text-bg-light">${u.role}</span></td>
                <td><c:out value="${u.createdAt}"/></td>
                <td class="text-end">
                  <button type="button"
                          class="btn btn-sm btn-outline-secondary me-1 edit-btn"
                          data-id="${u.id}"
                          data-username="${u.username}"
                          data-role="${u.role}">
                    Edit
                  </button>
                  <form class="d-inline" method="post" action="${pageContext.request.contextPath}/users"
                        onsubmit="return confirm('Delete user ${u.username}?');">
                    <input type="hidden" name="action" value="delete"/>
                    <input type="hidden" name="id" value="${u.id}"/>
                    <button class="btn btn-sm btn-outline-danger">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>
        <c:if test="${empty users}">
          <div class="text-muted">No matching records.</div>
        </c:if>
      </div>
    </div>
  </c:if>
</div>

<!-- Add Modal -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog"><div class="modal-content">
    <form class="needs-validation" novalidate method="post" action="${pageContext.request.contextPath}/users">
      <input type="hidden" name="action" value="add"/>
      <div class="modal-header">
        <h5 class="modal-title">Add User</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <c:if test="${openModal == 'add' && not empty formErrors}">
          <div class="alert alert-danger"><ul class="mb-0">
            <c:forEach var="e" items="${formErrors}"><li>${e}</li></c:forEach>
          </ul></div>
        </c:if>

        <div class="mb-3">
          <label class="form-label">Username</label>
          <input name="username" class="form-control" required maxlength="50" value="${form.username}">
          <div class="invalid-feedback">Username is required (max 50 chars).</div>
        </div>

        <div class="mb-3">
          <label class="form-label">Role</label>
          <select name="role" class="form-select" required>
            <option value="">Select a role</option>
            <option value="ADMIN"  ${form.role == 'ADMIN'  ? 'selected' : ''}>ADMIN</option>
            <option value="USER"   ${empty form.role || form.role == 'USER' ? 'selected' : ''}>USER</option>
            <option value="CASHIER"${form.role == 'CASHIER'? 'selected' : ''}>CASHIER</option>
          </select>
          <div class="invalid-feedback">Please pick a role.</div>
        </div>

        <div class="mb-3">
          <label class="form-label">Password</label>
          <input name="password" type="password" class="form-control" minlength="6" required>
          <div class="invalid-feedback">Password is required (min 6 chars).</div>
        </div>

        <div class="mb-0">
          <label class="form-label">Confirm Password</label>
          <input name="confirm" type="password" class="form-control" minlength="6" required>
          <div class="invalid-feedback">Please confirm the password.</div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
        <button class="btn btn-primary">Save</button>
      </div>
    </form>
  </div></div>
</div>

<!-- Edit Modal -->
<div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog"><div class="modal-content">
    <form class="needs-validation" novalidate method="post" action="${pageContext.request.contextPath}/users">
      <input type="hidden" name="action" value="update"/>
      <div class="modal-header">
        <h5 class="modal-title">Edit User</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <c:if test="${openModal == 'edit' && not empty formErrors}">
          <div class="alert alert-danger"><ul class="mb-0">
            <c:forEach var="e" items="${formErrors}"><li>${e}</li></c:forEach>
          </ul></div>
        </c:if>

        <div class="mb-3">
          <label class="form-label">User ID</label>
          <input id="edit-id" name="id" class="form-control" readonly value="${form.id}">
        </div>

        <div class="mb-3">
          <label class="form-label">Username</label>
          <input id="edit-username" name="username" class="form-control" required maxlength="50" value="${form.username}">
          <div class="invalid-feedback">Username is required (max 50 chars).</div>
        </div>

        <div class="mb-3">
          <label class="form-label">Role</label>
          <select id="edit-role" name="role" class="form-select" required>
            <option value="">Select a role</option>
            <option value="ADMIN">ADMIN</option>
            <option value="USER">USER</option>
            <option value="CASHIER">CASHIER</option>
          </select>
          <div class="invalid-feedback">Please pick a role.</div>
        </div>

        <div class="mb-3">
          <label class="form-label">New Password (optional)</label>
          <input id="edit-password" name="password" type="password" class="form-control" minlength="6" placeholder="Leave blank to keep current">
        </div>

        <div class="mb-0">
          <label class="form-label">Confirm New Password</label>
          <input id="edit-confirm" name="confirm" type="password" class="form-control" minlength="6" placeholder="Leave blank to keep current">
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
        <button class="btn btn-primary">Update</button>
      </div>
    </form>
  </div></div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Toasts
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
        if (!form.checkValidity()) { event.preventDefault(); event.stopPropagation(); }
        form.classList.add('was-validated');
      }, false);
    });
  })();

  // Open + fill Edit modal programmatically (reliable across forms/tables)
  document.addEventListener('click', function (e) {
    const btn = e.target.closest('.edit-btn'); if (!btn) return;
    e.preventDefault();
    const set = (id, val) => { const el = document.getElementById(id); if (el) el.value = (val ?? ''); };
    set('edit-id', btn.getAttribute('data-id'));
    set('edit-username', btn.getAttribute('data-username'));
    const roleSel = document.getElementById('edit-role'); if (roleSel) roleSel.value = btn.getAttribute('data-role') || '';
    // clear optional password fields on open
    const p = document.getElementById('edit-password'); if (p) p.value = '';
    const c = document.getElementById('edit-confirm');  if (c) c.value = '';
    bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal')).show();
  });

  // Reopen a modal after server-side validation errors
  (() => {
    const open = "${openModal}";
    if (open === "add")  bootstrap.Modal.getOrCreateInstance(document.getElementById('addModal')).show();
    if (open === "edit") bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal')).show();
  })();
</script>
</body>
</html>

