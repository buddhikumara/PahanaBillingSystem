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
        body { background-color: #f8f9fa; }
        .card { border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .form-control, .form-select { border-radius: 6px; }
        .highlight-box {
            background: #e9ffe9; padding: 10px 20px;
            border-radius: 10px; font-weight: bold; font-size: 1.2rem;
        }
        @media print {
            .no-print { display: none; }
            body { background: white; }
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <div class="card p-4">
        <h3 class="text-primary mb-4"><i class="fa-solid fa-cart-shopping me-2"></i>Cashier Billing</h3>

        <form id="billForm" action="billing" method="post" onsubmit="return validateForm()">
            <div class="row mb-3">
                <div class="col-md-8">

                    <select class="form-select" id="itemSelect">
                        <option value="">-- Select item --</option>
                        <% if (items != null) {
                            for (ItemDTO item : items) { %>
                        <option
                                value="<%= item.getItemId() %>"
                                data-name="<%= item.getItemName() %>"
                                data-description="<%= item.getDescription() %>"
                                data-price="<%= item.getRetailPrice() %>">
                            <%= item.getItemName() %>
                        </option>
                        <% }} %>
                    </select>

                </div>
                <div class="col-md-2">
                    <input type="number" id="qtyInput" class="form-control" placeholder="Qty" value="1" min="1">
                </div>
                <div class="col-md-2">
                    <button type="button" class="btn btn-outline-success w-100" onclick="addSelectedItem()">
                        <i class="fa fa-plus"></i> Add
                    </button>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle text-center" id="billTable">
                    <thead class="table-primary">
                    <tr>
                        <th>Item</th>
                        <th>Description</th>
                        <th>Qty</th>
                        <th>Price (Rs.)</th>
                        <th>Total (Rs.)</th>
                        <th class="no-print">Action</th>
                    </tr>
                    </thead>
                    <tbody id="billBody"></tbody>
                </table>
            </div>

            <div class="mb-3">
                <label class="form-label fw-bold">Payment Method:</label>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="payment" value="Cash" checked>
                    <label class="form-check-label">Cash</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="payment" value="Card">
                    <label class="form-check-label">Card</label>
                </div>
            </div>

            <div class="text-end highlight-box mb-3">
                Grand Total: Rs. <span id="grandTotal">0.00</span>
            </div>

            <div class="text-end">
                <button type="submit" class="btn btn-primary btn-lg no-print">
                    <i class="fa fa-check"></i> Submit Bill (Ctrl + Enter)
                </button>
                <button type="button" class="btn btn-danger btn-lg no-print ms-2" onclick="deleteSelectedRows()">
                    <i class="fa fa-trash"></i> Delete Selected
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    function addSelectedItem() {
        const select = document.getElementById('itemSelect');
        const option = select.options[select.selectedIndex];
        const qtyInput = document.getElementById('qtyInput');

        if (!option || !option.value || option.value === "") {
            alert("Please select a valid item.");
            return;
        }

        const itemId = option.value;
        const name = option.dataset.name;
        const desc = option.dataset.description;
        const price = parseFloat(option.dataset.price);
        const qty = parseInt(qtyInput.value);

        if (!itemId || !name || !desc || isNaN(price) || isNaN(qty) || qty <= 0) {
            alert("Invalid item or quantity.");
            return;
        }

        const total = qty * price;

        const row = document.createElement('tr');
        row.innerHTML = `
        <td><input type="hidden" name="itemId" value="${itemId}">${name}</td>
        <td>${desc}</td>
        <td>${qty}</td>
        <td>${price.toFixed(2)}</td>
        <td class="item-total">${total.toFixed(2)}</td>
        <td class="no-print">
            <input type="checkbox" class="form-check-input">
        </td>
    `;

        document.getElementById('billBody').appendChild(row);
        select.selectedIndex = 0;
        qtyInput.value = '1';
        updateGrandTotal();
    }

    function updateGrandTotal() {
        let total = 0;
        document.querySelectorAll(".item-total").forEach(el => {
            total += parseFloat(el.textContent.trim()) || 0;
        });
        document.getElementById("grandTotal").innerText = total.toFixed(2);
    }
</script>



<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
