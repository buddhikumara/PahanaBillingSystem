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
    <title>Item Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">

    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold text-primary"><i class="fa-solid fa-box"></i> Item Management</h2>
        <input type="text" id="searchInput" class="form-control me-2" placeholder="Search items...">
        <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#itemModal" onclick="openAddModal()">
            <i class="fa-solid fa-plus"></i> Add New Item
        </button>
    </div>




    <!-- Item Table -->
    <div class="card shadow-lg border-0">
        <div class="card-body">
            <table class="table table-hover table-striped align-middle">
                <thead class="table-primary">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Cost Price</th>
                    <th>Retail Price</th>
                    <th>Quantity</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (items != null && !items.isEmpty()) {
                        for (ItemDTO item : items) {
                %>
                <tr>
                    <td><%= item.getItemId() %></td>
                    <td><%= item.getItemName() %></td>
                    <td><%= item.getDescription() %></td>
                    <td>$<%= item.getCostPrice() %></td>
                    <td>$<%= item.getRetailPrice() %></td>
                    <td><%= item.getQuantity() %></td>
                    <td class="text-center">
                        <button class="btn btn-warning btn-sm"
                                onclick="openEditModal('<%= item.getItemId() %>', '<%= item.getItemName() %>', '<%= item.getDescription() %>', '<%= item.getCostPrice() %>', '<%= item.getRetailPrice() %>', '<%= item.getQuantity() %>')">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                        <form action="items" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="itemId" value="<%= item.getItemId() %>">
                            <button class="btn btn-danger btn-sm" onclick="return confirm('Are you sure?');">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </form>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr>
                    <td colspan="7" class="text-center text-muted">No items available.</td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal for Add/Edit Item -->
<div class="modal fade" id="itemModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow-lg">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="itemModalTitle">Add Item</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="itemForm" action="items" method="post">
                    <input type="hidden" name="action" id="formAction" value="add">
                    <input type="hidden" name="itemId" id="itemId">

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Item Name</label>
                            <input type="text" name="itemName" id="itemName" class="form-control" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Quantity</label>
                            <input type="number" name="quantity" id="quantity" class="form-control" required>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Cost Price ($)</label>
                            <input type="number" step="0.01" name="costPrice" id="costPrice" class="form-control" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Retail Price ($)</label>
                            <input type="number" step="0.01" name="retailPrice" id="retailPrice" class="form-control" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea name="description" id="description" rows="3" class="form-control"></textarea>
                    </div>

                    <div class="text-end">
                        <button type="submit" class="btn btn-primary"><i class="fa-solid fa-save"></i> Save</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function openAddModal() {
        document.getElementById("itemModalTitle").innerText = "Add Item";
        document.getElementById("formAction").value = "add";
        document.getElementById("itemForm").reset();
        document.getElementById("itemId").value = "";
    }

    function openEditModal(id, name, description, costPrice, retailPrice, quantity) {
        document.getElementById("itemModalTitle").innerText = "Edit Item";
        document.getElementById("formAction").value = "update";
        document.getElementById("itemId").value = id;
        document.getElementById("itemName").value = name;
        document.getElementById("description").value = description;
        document.getElementById("costPrice").value = costPrice;
        document.getElementById("retailPrice").value = retailPrice;
        document.getElementById("quantity").value = quantity;

        new bootstrap.Modal(document.getElementById('itemModal')).show();
    }

    document.getElementById("searchInput").addEventListener("keyup", function () {
        let filter = this.value.toLowerCase();
        let rows = document.querySelectorAll("table tbody tr");

        rows.forEach(row => {
            let text = row.innerText.toLowerCase();
            row.style.display = text.includes(filter) ? "" : "none";
        });
    });

</script>

</body>
</html>
