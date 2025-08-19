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
    .hero{background:linear-gradient(135deg,#6a5acd,#9c8cff); color:#fff; border-radius:1rem; box-shadow:0 10px 30px rgba(0,0,0,.2)}
    .card{border:none;border-radius:1rem;box-shadow:0 6px 20px rgba(0,0,0,.06)}
    .btn-rounded{border-radius:999px}
    .table thead th{background:#f1f3f8;font-weight:600}
    .empty{border:2px dashed #d9e1f2; border-radius:1rem; padding:40px; color:#6c7a91; background:#fff}
    .modal-content{border:0; border-radius:1rem; box-shadow:0 20px 60px rgba(0,0,0,.2)}
  </style>
</head>
<body>

<!-- Toasts -->
<div class="position-fixed top-0 end-0 p-3" style="z-index:1080">
  <c:if test="${not empty flashSuccess}">
    <div id="toastSuccess" class="toast align-items-center text-bg-success border-0">
      <div class="d-flex">
        <div class="toast-body">${flashSuccess}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    </div>
  </c:if>
  <c:if test="${not empty flashError}">
    <div id="toastError" class="toast align-items-center text-bg-danger border-0">
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

  <!-- Search -->
  <form class="d-flex gap-2 mb-3" method="get" action="${pageContext.request.contextPath}/items">
    <input type="hidden" name="search" value="1"/>
    <input name="q" value="${q}" class="form-control" placeholder="Type here and press Search...">
    <button class="btn btn-primary btn-rounded" type="submit">Search</button>
    <a class="btn btn-outline-secondary btn-rounded" href="${pageContext.request.contextPath}/items">Reset</a>
  </form>

  <!-- Results -->
  <c:if test="${hasSearched}">
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table align-middle">
            <thead>
            <tr>
              <th>#</th><th>Item ID</th><th>Name</th><th>Description</th>
              <th class="text-end">Cost</th><th class="text-end">Retail</th>
              <th class="text-end">Qty</th><th class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
              <c:when test="${empty items}">
                <tr>
                  <td colspan="8" class="text-center text-muted py-4">
                    <em>No results found</em>
                  </td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="it" items="${items}" varStatus="st">
                  <tr>
                    <td>${st.index + 1}</td>
                    <td><code>${it.itemId}</code></td>
                    <td>${it.itemName}</td>
                    <td>${it.description}</td>
                    <td class="text-end">${it.costPrice}</td>
                    <td class="text-end">${it.retailPrice}</td>
                    <td class="text-end">${it.quantity}</td>
                    <td class="text-end">
                      <c:if test="${authUser.role == 'ADMIN'}">
                        <button type="button" class="btn btn-sm btn-outline-secondary me-1 edit-btn"
                                data-itemid="${it.itemId}" data-name="${it.itemName}" data-desc="${it.description}"
                                data-cost="${it.costPrice}" data-retail="${it.retailPrice}" data-qty="${it.quantity}">
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
              </c:otherwise>
            </c:choose>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </c:if>
</div>

<!-- Add Item Modal -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <form id="addItemForm" class="needs-validation" novalidate method="post"
            action="${pageContext.request.contextPath}/items">
        <input type="hidden" name="action" value="add"/>

        <div class="modal-header">
          <h5 class="modal-title">Add Item</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>

        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Name</label>
            <input name="itemName" id="add-name" class="form-control" required maxlength="100">
            <div class="invalid-feedback">Item name is required (max 100 chars).</div>
          </div>

          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea name="description" id="add-desc" class="form-control" maxlength="255" rows="2"></textarea>
            <div class="invalid-feedback">Description must be 255 characters or less.</div>
          </div>

          <div class="row g-2">
            <div class="col-md-6">
              <label class="form-label">Cost price</label>
              <input name="costPrice" id="add-cost" type="number" inputmode="decimal"
                     min="0.01" max="1000000" step="0.01" class="form-control" required>
              <div class="invalid-feedback">Cost price is required (≥ 0.01).</div>
            </div>
            <div class="col-md-6">
              <label class="form-label">Retail price</label>
              <input name="retailPrice" id="add-retail" type="number" inputmode="decimal"
                     min="0.01" max="1000000" step="0.01" class="form-control" required>
              <div class="invalid-feedback">Retail price is required and must be ≥ cost.</div>
            </div>
          </div>

          <div class="mt-3">
            <label class="form-label">Quantity</label>
            <input name="quantity" id="add-qty" type="number" inputmode="numeric"
                   min="0" step="1" class="form-control" required>
            <div class="invalid-feedback">Quantity is required (integer ≥ 0).</div>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
          <button class="btn btn-primary" id="add-submit-btn">Save</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Item Modal -->
<div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <form id="editItemForm" class="needs-validation" novalidate method="post" action="${pageContext.request.contextPath}/items">
        <input type="hidden" name="action" value="update"/>
        <div class="modal-header">
          <h5 class="modal-title">Edit Item</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Item ID</label>
            <input id="edit-id" name="itemId" class="form-control" readonly>
          </div>
          <div class="mb-3">
            <label class="form-label">Name</label>
            <input id="edit-name" name="itemName" class="form-control" required maxlength="100">
            <div class="invalid-feedback">Item name is required.</div>
          </div>
          <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea id="edit-desc" name="description" class="form-control" maxlength="255" rows="2"></textarea>
          </div>
          <div class="row g-2">
            <div class="col-md-6">
              <label class="form-label">Cost price</label>
              <input id="edit-cost" name="costPrice" type="number" inputmode="decimal"
                     min="0.01" step="0.01" class="form-control" required>
              <div class="invalid-feedback">Cost must be ≥ 0.01.</div>
            </div>
            <div class="col-md-6">
              <label class="form-label">Retail price</label>
              <input id="edit-retail" name="retailPrice" type="number" inputmode="decimal"
                     min="0.01" step="0.01" class="form-control" required>
              <div class="invalid-feedback">Retail must be ≥ cost.</div>
            </div>
          </div>
          <div class="mt-3">
            <label class="form-label">Quantity</label>
            <input id="edit-qty" name="quantity" type="number" inputmode="numeric" min="0" step="1" class="form-control" required>
            <div class="invalid-feedback">Quantity must be integer ≥ 0.</div>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Toasts
  (function () {
    var ok = document.getElementById('toastSuccess');
    var er = document.getElementById('toastError');
    if (ok) new bootstrap.Toast(ok, { delay: 3000 }).show();
    if (er) new bootstrap.Toast(er, { delay: 4000 }).show();
  })();

  // open Edit modal pre-filled
  document.addEventListener('click', function (e) {
    var btn = e.target.closest('.edit-btn');
    if (!btn) return;
    e.preventDefault();
    document.getElementById('edit-id').value     = btn.getAttribute('data-itemid');
    document.getElementById('edit-name').value   = btn.getAttribute('data-name');
    document.getElementById('edit-desc').value   = btn.getAttribute('data-desc');
    document.getElementById('edit-cost').value   = btn.getAttribute('data-cost');
    document.getElementById('edit-retail').value = btn.getAttribute('data-retail');
    document.getElementById('edit-qty').value    = btn.getAttribute('data-qty');
    bootstrap.Modal.getOrCreateInstance(document.getElementById('editModal')).show();
  });

  // ===== Validation helpers =====
  function toNum(v) {
    if (v == null) return NaN;
    var s = (""+v).trim().replace(/,/g, "");
    return s === "" ? NaN : Number(s);
  }
  function setupFormValidation(formId, ids) {
    var form = document.getElementById(formId);
    if (!form) return;

    // clear invalid on input
    form.querySelectorAll('input,textarea').forEach(function (el) {
      el.addEventListener('input', function(){ el.classList.remove('is-invalid'); });
    });

    form.addEventListener('submit', function (e) {
      var name   = document.getElementById(ids.name);
      var cost   = document.getElementById(ids.cost);
      var retail = document.getElementById(ids.retail);
      var qty    = document.getElementById(ids.qty);

      var valid = true;

      // EMPTY checks
      if (!name.value.trim())   { name.classList.add('is-invalid');   valid = false; }
      if (!cost.value.trim())   { cost.classList.add('is-invalid');   valid = false; }
      if (!retail.value.trim()) { retail.classList.add('is-invalid'); valid = false; }
      if (!qty.value.trim())    { qty.classList.add('is-invalid');    valid = false; }

      // Only continue if non-empty
      if (valid) {
        var costV   = toNum(cost.value);
        var retailV = toNum(retail.value);
        var qtyRaw  = toNum(qty.value);
        var qtyV    = Math.floor(qtyRaw);

        if (!isFinite(costV) || costV < 0.01) { cost.classList.add('is-invalid'); valid = false; }
        if (!isFinite(retailV) || retailV < 0.01) { retail.classList.add('is-invalid'); valid = false; }

        if (isFinite(costV) && isFinite(retailV) && retailV < costV) {
          retail.classList.add('is-invalid'); valid = false;
          var fb = retail.nextElementSibling; if (fb) fb.textContent = 'Retail price must be ≥ cost price.';
        }

        if (!Number.isFinite(qtyV) || qtyV < 0 || qtyV !== qtyRaw) {
          qty.classList.add('is-invalid'); valid = false;
        } else {
          qty.value = String(qtyV);
        }

        // normalize prices
        if (valid) {
          cost.value   = costV.toFixed(2);
          retail.value = retailV.toFixed(2);
        }
      }

      if (!valid) {
        e.preventDefault(); e.stopPropagation();
        var firstInvalid = form.querySelector('.is-invalid, :invalid');
        if (firstInvalid) firstInvalid.focus();
      }
    });
  }

  // hook both forms
  setupFormValidation('addItemForm',  { name:'add-name',  cost:'add-cost',  retail:'add-retail',  qty:'add-qty'  });
  setupFormValidation('editItemForm', { name:'edit-name', cost:'edit-cost', retail:'edit-retail', qty:'edit-qty' });
</script>
</body>
</html>
