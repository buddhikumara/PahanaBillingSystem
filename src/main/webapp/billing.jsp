<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.pahana.business.dto.ItemDTO" %>

<%
    List<ItemDTO> items = (List<ItemDTO>) request.getAttribute("items");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Cashier Billing</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f1f4f9;
        }

        .card {
            border-radius: 15px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.05);
        }

        .table > thead {
            background-color: #007bff;
            color: white;
        }

        .form-control, .form-select {
            border-radius: 8px;
        }

        .btn-rounded {
            border-radius: 30px;
            padding-left: 20px;
            padding-right: 20px;
        }

        .highlight-box {
            background-color: #fff6e6;
            padding: 10px 20px;
            border-radius: 10px;
            font-size: 1.3rem;
            font-weight: bold;
        }

        .table td, .table th {
            vertical-align: middle;
        }
    </style>
</head>
<body>

<% if (request.getAttribute("submitted") != null) { %>
<div class="alert alert-success alert-dismissible fade show" role="alert">
    ✅ Bill submitted successfully! Total:
    <strong>Rs. <%= request.getAttribute("grandTotal") %></strong>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<% } %>


<div class="container mt-5">
    <div class="card p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="text-primary"><i class="fa-solid fa-cart-shopping me-2"></i>Cashier Billing</h3>
            <button type="button" class="btn btn-success btn-rounded" onclick="addRow()">
                <i class="fa-solid fa-plus me-1"></i> Add Item
            </button>
        </div>

        <form id="billForm" action="billing" method="post">
            <div class="table-responsive">
                <table class="table table-hover align-middle text-center" id="billTable">
                    <thead>
                    <tr>
                        <th style="width: 18%">Item</th>
                        <th>Description</th>
                        <th style="width: 12%">Qty</th>
                        <th style="width: 15%">Price (Rs.)</th>
                        <th style="width: 15%">Total (Rs.)</th>
                        <th style="width: 10%">Remove</th>
                    </tr>
                    </thead>
                    <tbody id="billBody">
                    <!-- JS will add rows here -->
                    </tbody>
                </table>
            </div>

            <div class="d-flex justify-content-end mt-4">
                <div class="highlight-box">
                    Total: Rs. <span id="grandTotal">0.00</span>
                </div>
            </div>

            <div class="d-flex justify-content-end mt-3">
                <button type="submit" class="btn btn-primary btn-rounded">
                    <i class="fa-solid fa-paper-plane me-1"></i> Submit Bill
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Hidden item list template -->
<select class="form-select d-none" id="itemSelectTemplate">
    <option value="">-- Select Item --</option>
    <% if (items != null && !items.isEmpty()) {
        for (ItemDTO item : items) { %>
    <option value="<%= item.getItemId() %>"
            data-name="<%= item.getItemName() %>"
            data-description="<%= item.getDescription() %>"
            data-price="<%= item.getRetailPrice() %>">
        <%= item.getItemName() %>
    </option>
    <%  }
    } else { %>
    <option disabled>No items found</option>
    <% } %>
</select>



<script>
    function addRow() {
        const tbody = document.getElementById("billBody");
        const row = document.createElement("tr");

        // Clone dropdown
        const template = document.getElementById("itemSelectTemplate");
        const select = template.cloneNode(true);
        select.classList.remove("d-none");
        select.removeAttribute("id");
        select.setAttribute("name", "itemId");
        select.classList.add("form-select");
        select.setAttribute("onchange", "updateRow(this)");

        row.innerHTML = `
        <td></td>
        <td><input type="text" name="description" class="form-control" readonly></td>
        <td><input type="number" name="quantity" class="form-control text-center" value="1" min="1" onchange="recalculate(this)"></td>
        <td><input type="number" name="price" class="form-control text-end" readonly></td>
        <td><input type="number" name="total" class="form-control text-end" readonly></td>
        <td><button type="button" class="btn btn-outline-danger btn-sm" onclick="removeRow(this)"><i class="fa fa-trash"></i></button></td>
    `;

        // Insert select into first <td>
        row.children[0].appendChild(select);

        tbody.appendChild(row);
    }


    function updateRow(select) {
        const selected = select.options[select.selectedIndex];
        const row = select.closest('tr');

        row.querySelector('[name=description]').value = selected.getAttribute('data-description');
        row.querySelector('[name=price]').value = selected.getAttribute('data-price');
        row.querySelector('[name=quantity]').value = 1;

        recalculate(row.querySelector('[name=quantity]'));
    }

    function recalculate(input) {
        const row = input.closest('tr');
        const qty = parseFloat(row.querySelector('[name=quantity]').value) || 0;
        const price = parseFloat(row.querySelector('[name=price]').value) || 0;
        const total = qty * price;

        row.querySelector('[name=total]').value = total.toFixed(2);
        updateGrandTotal();
    }

    function updateGrandTotal() {
        const totals = document.querySelectorAll('[name=total]');
        let sum = 0;

        totals.forEach(t => {
            sum += parseFloat(t.value) || 0;
        });

        document.getElementById('grandTotal').innerText = sum.toFixed(2);
    }

    function removeRow(btn) {
        btn.closest('tr').remove();
        updateGrandTotal();
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
