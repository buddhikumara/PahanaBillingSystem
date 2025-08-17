<%--
  Created by IntelliJ IDEA.
  User: Buddhi
  Date: 17/08/2025
  Time: 23:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Pahana | Help</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">Help & Getting Started</h3>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary">← Back to Dashboard</a>
    </div>

    <!-- Quick Start -->
    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <h5 class="card-title mb-3">Quick Start</h5>
            <ol class="mb-0">
                <li><strong>Login</strong> using your username & password.</li>
                <li><strong>Customers</strong> → add or select existing customers.</li>
                <li><strong>Items</strong> → add inventory with prices & stock.</li>
                <li><strong>Billing</strong> → select customer, add items, save & print invoice.</li>
                <li><strong>Reports</strong> → daily sales, item-wise, customer-wise, bills list, summary.</li>
            </ol>
        </div>
    </div>

    <!-- Keyboard Shortcuts -->
    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <h5 class="card-title mb-3">Keyboard Shortcuts (Billing)</h5>
            <ul class="mb-0">
                <li><kbd>Enter</kbd> → add item row</li>
                <li><kbd>Ctrl</kbd> + <kbd>S</kbd> → Save & Print</li>
                <li><kbd>Ctrl</kbd> + <kbd>F</kbd> → focus item search</li>
                <li><kbd>Esc</kbd> → clear current row / close modal</li>
            </ul>
        </div>
    </div>

    <!-- FAQs -->
    <div class="accordion" id="helpAccordion">
        <div class="accordion-item">
            <h2 class="accordion-header" id="h1">
                <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#c1">
                    How do I print an invoice?
                </button>
            </h2>
            <div id="c1" class="accordion-collapse collapse show" data-bs-parent="#helpAccordion">
                <div class="accordion-body">
                    After saving a bill, the system opens the invoice in a new tab/window. If blocked, allow pop-ups for this site.
                </div>
            </div>
        </div>

        <div class="accordion-item">
            <h2 class="accordion-header" id="h2">
                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#c2">
                    Stock goes negative — what should I check?
                </button>
            </h2>
            <div id="c2" class="accordion-collapse collapse" data-bs-parent="#helpAccordion">
                <div class="accordion-body">
                    Verify item stock in <strong>Items</strong>, ensure warnings are enabled, and confirm quantities before saving.
                </div>
            </div>
        </div>

        <div class="accordion-item">
            <h2 class="accordion-header" id="h3">
                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#c3">
                    How do I see customer-wise sales?
                </button>
            </h2>
            <div id="c3" class="accordion-collapse collapse" data-bs-parent="#helpAccordion">
                <div class="accordion-body">
                    Go to <strong>Reports → Customer-wise</strong>, pick a customer and date range, then click <em>View</em>.
                </div>
            </div>
        </div>

        <div class="accordion-item">
            <h2 class="accordion-header" id="h4">
                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#c4">
                    I forgot my password — what now?
                </button>
            </h2>
            <div id="c4" class="accordion-collapse collapse" data-bs-parent="#helpAccordion">
                <div class="accordion-body">
                    Contact an admin user to reset your password from the <strong>Users</strong> module.
                </div>
            </div>
        </div>
    </div>

    <!-- Contact / About -->
    <div class="card shadow-sm mt-3">
        <div class="card-body">
            <h5 class="card-title mb-2">Need more help?</h5>
            <p class="mb-2">Please contact your system administrator or email IT support.</p>
            <small class="text-muted">Pahana Billing System • v2.0</small>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

