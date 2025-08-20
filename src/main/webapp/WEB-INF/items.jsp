<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  String _ok = (String) session.getAttribute("flashSuccess");
  if (_ok != null) { request.setAttribute("flashSuccess", _ok); session.removeAttribute("flashSuccess"); }
  String _err = (String) session.getAttribute("flashError");
  if (_err != null) { request.setAttribute("flashError", _err); session.removeAttribute("flashError"); }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Pahana | Items</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body{background:#f5f7fb}
    .hero{background:linear-gradient(135deg,#6a5acd,#9c8cff);color:#fff;border-radius:1rem;box-shadow:0 10px 30px rgba(0,0,0,.2)}
    .card{border:none;border-radius:1rem;box-shadow:0 6px 20px rgba(0,0,0,.06)}
    .btn-rounded{border-radius:999px}
    .table thead th{background:#f1f3f8;font-weight:600}
    .empty{border:2px dashed #d9e1f2;border-radius:1rem;padding:40px;color:#6c7a91;background:#fff}
    .modal-content{border:0;border-radius:1rem;box-shadow:0 20px 60px rgba(0,0,0,.2)}
    code{background:#f1f5ff;padding:.15rem .35rem;border-radius:.5rem}
  </style>
</head>
<body>
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
      <h3 class="mb-1">Items</h3>
      <div class="opacity-75">Search by ID, name, or description</div>
    </div>
    <div class="d-flex gap-2">
      <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-light btn-rounded">← Back</a>
      <c:if test="${authUser.role == 'ADMIN' || authUser.role == 'USER'}">
        <button type="button" class="btn btn-dark btn-rounded" data-bs-toggle="modal" data-bs-target="#addModal">+ Add Item</button>
      </c:if>
    </div>
  </div>

  <form class="d-flex gap-2 mb-3" method="get" action="${pageContext.request.contextPath}/items">
    <input type="hidden" name="search" value="1"/>
    <input name="q" value="${q}" class="form-control" placeholder="Type here and press Search... (empty = load all)">
    <button class="btn btn-primary btn-rounded" type="submit">Search</button>
    <a class="btn btn-outline-secondary btn-rounded" href="${pageContext.request.contextPath}/items">Reset</a>
  </form>

  <c:if test="${!hasSearched}">
    <div class="empty text-center">
      <h5 class="mb-2">No data loaded</h5>
      <div>Click <strong>Search</strong> with an empty box to load all items.</div>
    </div>
  </c:if>

  <c:if test="${hasSearched}">
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table align-middle">
            <thead>
            <tr>
              <th>#</th>
              <th>Item ID</th>
              <th>Name</th>
              <th>Description</th>
              <th class="text-end">Cost</th>
              <th class="text-end">Retail</th>
              <th class="text-end">Qty</th>
              <th class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="it" items="${items}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td><code>${it.itemId}</code></td>
                <td>${it.itemName}</td>
                <td>${it.description}</td>
                <td class="text-end">${it.costPrice}</td>
                <td class="text-end">${it.retailPrice}</td>
                <td class="text-end"><c:out value="${it.quantity}"/></td>
                <td class="text-end">
                  <c:if test="${authUser.role == 'ADMIN'}">
                    <button type="button"
                            class="btn btn-sm btn-outline-secondary me-1 edit-btn"
                            data-itemid="${it.itemId}"
                            data-name="${it.itemName}"
                            data-desc="${it.description}"
                            data-cost="${it.costPrice}"
                            data-retail="${it.retailPrice}"
                            data-qty="${it.quantity}">
                      Edit
                    </button>
                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/items"
                          onsubmit="return confirm('Delete item ${it.itemId}?');">
                      <input type="hidden" name="action" value="delete"/>
                      <input type="hidden" name="itemId" value="${it.itemId}"/>
                      <button class="btn btn-sm btn-outline-danger">Delete</button>
                    </form>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>
        <c:if test="${empty items}">
          <div class="text-muted">No matching records.</div>
        </c:if>
      </div>
    </div>
  </c:if>
</div>

<c:if test="${authUser.role == 'ADMIN' || authUser.role == 'USER'}">
  <div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <form class="needs-validation" novalidate method="post" action="${pageContext.request.contextPath}/items">
          <input type="hidden" name="action" value="add"/>
          <div class="modal-header">
            <h5 class="modal-title">Add Item</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <c:if test="${openModal == 'add' && not empty formErrors}">
              <div class="alert alert-danger"><ul class="mb-0">
                <c:forEach var="e" items="${formErrors}"><li>${e}</li></c:forEach>
              </ul></div>
            </c:if>
            <div class="mb-3">
              <label class="form-label">Item ID</label>
              <input name="itemId" class="form-control" required maxlength="20" value="${form.itemId}">
              <div class="invalid-feedback">Item ID is required.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Name</label>
              <input name="itemName" class="form-control" required maxlength="100" value="${form.itemName}">
              <div class="invalid-feedback">Name is required.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Description</label>
              <textarea name="description" class="form-control" maxlength="255" rows="2">${form.description}</textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">Cost price</label>
              <input name="costPrice" type="number" min="1" max="1000000" step="0.01" class="form-control" required value="${form.costPrice}">
              <div class="invalid-feedback">Enter a valid cost price.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Retail price</label>
              <input name="retailPrice" type="number" min="1" max="1000000" step="0.01" class="form-control" required value="${form.retailPrice}">
              <div class="invalid-feedback">Enter a valid retail price.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Quantity</label>
              <input name="quantity" type="number" min="0" step="1" class="form-control" value="${form.quantity}">
              <div class="invalid-feedback">Quantity must be ≥ 0.</div>
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

<c:if test="${authUser.role == 'ADMIN'}">
  <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <form class="needs-validation" novalidate method="post" action="${pageContext.request.contextPath}/items">
          <input type="hidden" name="action" value="update"/>
          <div class="modal-header">
            <h5 class="modal-title">Edit Item</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <c:if test="${openModal == 'edit' && not empty formErrors}">
              <div class="alert alert-danger"><ul class="mb-0">
                <c:forEach var="e" items="${formErrors}"><li>${e}</li></c:forEach>
              </ul></div>
            </c:if>
            <div class="mb-3">
              <label class="form-label">Item ID</label>
              <input id="edit-id" name="itemId" class="form-control" readonly value="${form.itemId}">
            </div>
            <div class="mb-3">
              <label class="form-label">Name</label>
              <input id="edit-name" name="itemName" class="form-control" required maxlength="100" value="${form.itemName}">
              <div class="invalid-feedback">Name is required.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Description</label>
              <textarea id="edit-desc" name="description" class="form-control" maxlength="255" rows="2">${form.description}</textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">Cost price</label>
              <input id="edit-cost" name="costPrice" type="number" min="1" max="1000000" step="0.01" class="form-control" required value="${form.costPrice}">
              <div class="invalid-feedback">Enter a valid cost price.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Retail price</label>
              <input id="edit-retail" name="retailPrice" type="number" min="1" max="1000000" step="0.01" class="form-control" required value="${form.retailPrice}">
              <div class="invalid-feedback">Enter a valid retail price.</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Quantity</label>
              <input id="edit-qty" name="quantity" type="number" min="0" step="1" class="form-control" value="${form.quantity}">
              <div class="invalid-feedback">Quantity must be ≥ 0.</div>
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
  (() => {
    const ok = document.getElementById('toastSuccess');
    const er = document.getElementById('toastError');
    if (ok) new bootstrap.Toast(ok, { delay: 3000 }).show();
    if (er) new bootstrap.Toast(er, { delay: 4000 }).show();
  })();

  (() => {
    const forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(form => {
      form.addEventListener('submit', function (event) {
        if (!form.checkValidity()) { event.preventDefault(); event.stopPropagation(); }
        form.classList.add('was-validated');
      }, false);
    });
  })();

  document.addEventListener('click', function (e) {
    const btn = e.target.closest('.edit-btn');
    if (!btn) return;
    e.preventDefault();
    const set = (id, val) => { const el = document.getElementById(id); if (el) el.value = (val ?? ''); };
    set('edit-id',     btn.getAttribute('data-itemid'));
    set('edit-name',   btn.getAttribute('data-name'));
    set('edit-desc',   btn.getAttribute('data-desc'));
    set('edit-cost',   btn.getAttribute('data-cost'));
    set('edit-retail', btn.getAttribute('data-retail'));
    set('edit-qty',    btn.getAttribute('data-qty'));
    bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal')).show();
  });

  (() => {
    const open = "${openModal}";
    if (open === "edit") bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal')).show();
    if (open === "add")  bootstrap.Modal.getOrCreateInstance(document.getElementById('addModal')).show();
  })();
</script>
</body>
</html>
