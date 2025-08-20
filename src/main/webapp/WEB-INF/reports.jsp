<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  String ctx = request.getContextPath();
  String from = (String) request.getAttribute("from");
  String to   = (String) request.getAttribute("to");
%>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Pahana | Reports</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body{background:#f6f7fb}
    .card{border:none;border-radius:1rem;box-shadow:0 6px 20px rgba(0,0,0,.06)}
    .metric{padding:1rem;border-radius:.75rem;background:#fff}
    .metric .label{font-size:.85rem;color:#6c757d}
    .metric .value{font-size:1.5rem;font-weight:700}
    .section-title{font-weight:600;color:#495057}
    .table thead th{background:#f1f3f8}
  </style>
</head>
<body>
<nav class="navbar navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="<%=ctx%>/dashboard">Pahana Billing System</a>
    <a class="btn btn-outline-light btn-sm" href="<%=ctx%>/dashboard">⇠ Back to Dashboard</a>
  </div>
</nav>

<div class="container py-4">

  <!-- Filter -->
  <form class="card card-body mb-4" method="get" action="<%=ctx%>/reports">
    <div class="row g-3 align-items-end">
      <div class="col-md-3">
        <label class="form-label">From</label>
        <input type="date" class="form-control" name="from" value="<%=from%>">
      </div>
      <div class="col-md-3">
        <label class="form-label">To</label>
        <input type="date" class="form-control" name="to" value="<%=to%>">
      </div>
      <div class="col-md-2">
        <label class="form-label">Items filter</label>
        <input type="text" class="form-control" name="qItem" value="${qItem}" placeholder="id/name">
      </div>
      <div class="col-md-2">
        <label class="form-label">Customers filter</label>
        <input type="text" class="form-control" name="qCustomer" value="${qCustomer}" placeholder="id/name">
      </div>
      <div class="col-md-2">
        <label class="form-label">Bills filter</label>
        <input type="text" class="form-control" name="qBill" value="${qBill}" placeholder="id/customer">
      </div>
      <div class="col-12 text-end">
        <button class="btn btn-primary px-4">Apply</button>
        <a class="btn btn-outline-secondary" href="<%=ctx%>/reports">Reset</a>
      </div>
    </div>
  </form>

  <!-- Summary -->
  <div class="row g-3 mb-4">
    <div class="col-md-3"><div class="metric"><div class="label">Bills</div><div class="value">${summary.bills}</div></div></div>
    <div class="col-md-3"><div class="metric"><div class="label">Items sold</div><div class="value">${summary.items_sold}</div></div></div>
    <div class="col-md-3"><div class="metric"><div class="label">Gross (Rs.)</div><div class="value"><c:out value="${summary.gross}"/></div></div></div>
    <div class="col-md-3"><div class="metric"><div class="label">Net (Rs.)</div><div class="value"><c:out value="${summary.net}"/></div></div></div>
    <c:if test="${summary.discount ne null}">
      <div class="col-md-3"><div class="metric"><div class="label">Discount (Rs.)</div><div class="value"><c:out value="${summary.discount}"/></div></div></div>
    </c:if>
    <c:if test="${summary.cash ne null}">
      <div class="col-md-3"><div class="metric"><div class="label">Cash (Rs.)</div><div class="value"><c:out value="${summary.cash}"/></div></div></div>
      <div class="col-md-3"><div class="metric"><div class="label">Card (Rs.)</div><div class="value"><c:out value="${summary.card}"/></div></div></div>
    </c:if>
    <c:if test="${summary.top_item ne null}">
      <div class="col-md-3"><div class="metric">
        <div class="label">Top Item</div>
        <div class="value">${summary.top_item.item_name}</div>
        <small class="text-muted">Qty: ${summary.top_item.qty}, Rs. ${summary.top_item.amount}</small>
      </div></div>
    </c:if>
  </div>

  <!-- Daily Sales -->
  <div class="card mb-4">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <div class="section-title">Daily Sales</div>
        <a class="btn btn-outline-primary btn-sm"
           href="<%=ctx%>/report/daily?date=<%=to%>" target="_blank">Print Daily Report (for To date)</a>
      </div>
      <div class="table-responsive">
        <table class="table table-sm table-striped align-middle">
          <thead><tr><th>Date</th><th class="text-end">Bills</th><th class="text-end">Total (Rs.)</th></tr></thead>
          <tbody>
          <c:forEach var="d" items="${daily}">
            <tr>
              <td>${d.date}</td>
              <td class="text-end">${d.bills}</td>
              <td class="text-end">${d.total}</td>
            </tr>
          </c:forEach>
          <c:if test="${empty daily}">
            <tr><td colspan="3" class="text-center text-muted py-4">No data for this range.</td></tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Item-wise -->
  <div class="card mb-4">
    <div class="card-body">
      <div class="section-title mb-2">Item-wise Sales</div>
      <div class="table-responsive">
        <table class="table table-sm table-striped align-middle">
          <thead><tr><th>#</th><th>Item</th><th class="text-end">Qty</th><th class="text-end">Amount (Rs.)</th></tr></thead>
          <tbody>
          <c:forEach var="r" items="${itemwise}" varStatus="st">
            <tr>
              <td>${st.index + 1}</td>
              <td><span class="text-muted">#${r.item_id}</span> &nbsp; ${r.item_name}</td>
              <td class="text-end">${r.qty}</td>
              <td class="text-end">${r.amount}</td>
            </tr>
          </c:forEach>
          <c:if test="${empty itemwise}">
            <tr><td colspan="4" class="text-center text-muted py-4">No matching items.</td></tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Customer-wise (only if supported) -->
  <c:if test="${customerJoin}">
    <div class="card mb-4">
      <div class="card-body">
        <div class="section-title mb-2">Customer-wise Sales</div>
        <div class="table-responsive">
          <table class="table table-sm table-striped align-middle">
            <thead><tr><th>#</th><th>Customer</th><th class="text-end">Bills</th><th class="text-end">Amount (Rs.)</th></tr></thead>
            <tbody>
            <c:forEach var="r" items="${customerwise}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td><span class="text-muted">${r.customer_id}</span> &nbsp; ${r.name}</td>
                <td class="text-end">${r.bills}</td>
                <td class="text-end">${r.amount}</td>
              </tr>
            </c:forEach>
            <c:if test="${empty customerwise}">
              <tr><td colspan="4" class="text-center text-muted py-4">No matching customers.</td></tr>
            </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </c:if>
  <c:if test="${!customerJoin}">
    <div class="alert alert-warning">
      Customer-wise report hidden: your <code>bills</code> table doesn’t have a customer column yet (e.g. <code>customer_id</code> / <code>customerId</code>).
    </div>
  </c:if>

  <!-- Bills list -->
  <div class="card mb-4">
    <div class="card-body">
      <div class="section-title mb-2">Bills</div>
      <div class="table-responsive">
        <table class="table table-sm table-striped align-middle">
          <thead>
          <tr>
            <th>Bill ID</th><th>Date/Time</th>
            <c:if test="${not empty bills && bills[0].customer_id ne null}">
              <th>Customer</th>
            </c:if>
            <th class="text-end">Total (Rs.)</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="r" items="${bills}">
            <tr>
              <td>${r.bill_id}</td>
              <td>
                <c:choose>
                  <c:when test="${r.bill_date ne null}">${r.bill_date}</c:when>
                  <c:otherwise>-</c:otherwise>
                </c:choose>
              </td>
              <c:if test="${r.customer_id ne null}">
                <td>${r.customer_id}</td>
              </c:if>
              <td class="text-end">${r.total_amount}</td>
            </tr>
          </c:forEach>
          <c:if test="${empty bills}">
            <tr><td colspan="4" class="text-center text-muted py-4">No bills in this range.</td></tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>

</div>
</body>
</html>
