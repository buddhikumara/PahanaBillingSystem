<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*,java.util.Objects" %>
<%@ page import="com.pahana.business.dto.BillDTO,com.pahana.business.dto.BillItemDTO" %>
<%
    BillDTO bill = (BillDTO) session.getAttribute("billCart");
    if (bill == null) { bill = new com.pahana.business.dto.BillDTO(); session.setAttribute("billCart", bill); }
    String stockWarning = (String) session.getAttribute("stockWarning");
    if (stockWarning != null) { session.removeAttribute("stockWarning"); }
    String ctx = request.getContextPath();

    // Fallback date for "Print Daily Report" (avoid inline complex expressions in attributes)
    String todayStr = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Colombo")).toString();
%>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Cashier Billing</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .page-header { display:flex; align-items:center; justify-content:space-between; }
        .kbd { padding:.2rem .4rem; border:1px solid #ccc; border-bottom-width:3px; border-radius:.25rem; font-family:monospace; }
        .card-title-sm { font-size: .95rem; color:#6c757d; margin-bottom:.25rem; }
    </style>
</head>
<body class="bg-light">
<div class="container py-4">

    <div class="page-header mb-3">
        <div>
            <h3 class="mb-0">Cashier Billing</h3>
            <small class="text-muted">Fast checkout • today: <span id="todayLabel"></span></small>
        </div>
        <div class="d-flex gap-2">
            <a href="<%=ctx%>/logout" class="btn btn-outline-secondary">Exit</a>
        </div>
    </div>

    <% if (stockWarning != null) { %>
    <div class="alert alert-warning py-2"><%= stockWarning %></div>
    <% } %>

    <!-- Customer (search like item) -->
    <form class="card card-body mb-3" method="post" action="<%=ctx%>/billing" id="customerForm">
        <input type="hidden" name="action" value="start">
        <div class="row g-2 align-items-end">
            <div class="col-lg-7">
                <label class="form-label">Customer (search by ID or Name)</label>
                <input class="form-control" type="text" id="customerSearch" list="customerList"
                       placeholder="Type e.g. 1001 or John" autocomplete="off">
                <datalist id="customerList"></datalist>

                <!-- fields submitted to servlet -->
                <input type="hidden" name="customerId" id="customerId">
                <input type="hidden" name="customerName" id="customerName"
                       value="<%= bill.getCustomerName()==null ? "" : bill.getCustomerName() %>">

                <div class="form-text">
                    Tip: type a few letters then press <span class="kbd">Enter</span> to select. Format: <span class="kbd">ID - Name</span>
                </div>
            </div>
            <div class="col-lg-2">
                <button class="btn btn-primary w-100" type="submit">Set Customer</button>
            </div>
            <div class="col text-muted">
                <div class="card-title-sm">Selected customer</div>
                <div><strong id="selectedCustomer"><%= bill.getCustomerName()==null ? "(none)" : bill.getCustomerName() %></strong></div>
            </div>
        </div>
    </form>

    <!-- Add Item -->
    <form class="card card-body mb-3" method="post" action="<%=ctx%>/billing" id="addItemForm">
        <input type="hidden" name="action" value="addItem">
        <div class="row g-2 align-items-end">
            <div class="col-lg-7">
                <label class="form-label">Item (ID or Name)</label>
                <input type="text" name="codeOrName" class="form-control" id="codeOrName"
                       list="itemList" placeholder="Scan/type ID or name" autocomplete="off" required>
                <datalist id="itemList"></datalist>
            </div>
            <div class="col-lg-2">
                <label class="form-label">Qty</label>
                <input type="number" name="qty" class="form-control" id="qty" value="1" min="1" required>
            </div>
            <div class="col-lg-2">
                <button class="btn btn-success w-100" id="addBtn" type="submit">Add</button>
            </div>
        </div>
    </form>

    <!-- Items -->
    <div class="card mb-3">
        <div class="card-body p-0">
            <table class="table table-sm table-striped mb-0 align-middle">
                <thead class="table-light">
                <tr><th>#</th><th>Item</th><th class="text-end">Price</th><th class="text-end">Qty</th><th class="text-end">Total</th><th class="text-center">Stock</th><th></th></tr>
                </thead>
                <tbody id="billTableBody">
                <%
                    int i=0;
                    for (BillItemDTO it : bill.getItems()){
                %>
                <tr class="<%= it.isOutOfStock() ? "table-warning" : "" %>">
                    <td><%= (++i) %></td>
                    <td><%= it.getItemName() %></td>
                    <td class="text-end"><%= String.format("%.2f", it.getUnitPrice()) %></td>
                    <td class="text-end"><%= it.getQty() %></td>
                    <td class="text-end"><%= String.format("%.2f", it.getTotal()) %></td>
                    <td class="text-center">
                        <% if (it.getStockQty() <= 0) { %>
                        <span class="badge text-bg-danger">No stock</span>
                        <% } else if (it.getStockQty() < it.getQty()) { %>
                        <span class="badge text-bg-warning">Only <%=it.getStockQty()%></span>
                        <% } else { %>
                        <span class="badge text-bg-success"><%=it.getStockQty()%></span>
                        <% } %>
                    </td>
                    <td class="text-end">
                        <form method="post" action="<%=ctx%>/billing" onsubmit="return confirm('Remove item?')">
                            <input type="hidden" name="action" value="removeItem">
                            <input type="hidden" name="index" value="<%= (i-1) %>">
                            <button class="btn btn-outline-danger btn-sm">Remove</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <div class="card-footer d-flex flex-wrap gap-3 justify-content-between align-items-center">
            <div class="fs-5">Sub Total: <strong id="subTotal"><%= String.format("%.2f", bill.getSubTotal()) %></strong></div>
            <div class="d-flex align-items-center gap-2">
                <label class="form-label mb-0">Discount</label>
                <input type="number" step="0.01" class="form-control form-control-sm" id="discount" value="<%=bill.getDiscount()%>" style="width:120px">
            </div>
            <div class="fs-5">Grand Total: <strong id="grandTotal"><%= String.format("%.2f", bill.getGrandTotal()) %></strong></div>
        </div>
    </div>

    <!-- Payment & Finish -->
    <form class="card card-body mb-3" method="post" action="<%=ctx%>/billing" id="finishForm" target="_blank">
        <input type="hidden" name="action" value="finish">
        <div class="row g-3">
            <div class="col-md-3">
                <label class="form-label d-block">Payment Method</label>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="paymentMethod" id="pmCash" value="CASH" checked>
                    <label class="form-check-label" for="pmCash">Cash</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="paymentMethod" id="pmCard" value="CARD">
                    <label class="form-check-label" for="pmCard">Card</label>
                </div>
            </div>
            <div class="col-md-3">
                <label class="form-label">Paid Amount</label>
                <input type="number" step="0.01" name="paidAmount" id="paidAmount" class="form-control" value="0">
            </div>
            <div class="col-md-3">
                <label class="form-label">Balance / Change</label>
                <input type="text" class="form-control" id="balance" readonly>
            </div>
            <input type="hidden" name="discount" id="discountHidden" value="<%=bill.getDiscount()%>">
        </div>
        <div class="text-end mt-3">
            <button class="btn btn-dark px-4" id="saveAndPrint">Save & Print (PDF)</button>
        </div>
    </form>

    <!-- Daily Summary -->
    <div class="card">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <div>
                    <div class="card-title-sm">Today’s Cashier Summary</div>
                    <div class="text-muted">For: <span id="sumDate"></span></div>
                </div>
                <div>
                    <!-- server-side fallback href using precomputed todayStr -->
                    <a class="btn btn-outline-primary" id="printDaily"
                       href="<%=ctx%>/report/daily?date=<%= todayStr %>"
                       target="_blank">Print Daily Report</a>
                </div>
            </div>
            <div class="row g-3">
                <div class="col-md-3">
                    <div class="p-3 border rounded bg-white">
                        <div class="text-muted small">Bills</div>
                        <div class="fs-4" id="sumBills">0</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3 border rounded bg-white">
                        <div class="text-muted small">Total Sales</div>
                        <div class="fs-4" id="sumTotal">0.00</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3 border rounded bg-white">
                        <div class="text-muted small">Cash</div>
                        <div class="fs-4" id="sumCash">—</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3 border rounded bg-white">
                        <div class="text-muted small">Card</div>
                        <div class="fs-4" id="sumCard">—</div>
                    </div>
                </div>
            </div>
            <div class="form-text mt-2">Cash/Card split requires payment columns in DB; currently showing total only.</div>
        </div>
    </div>

</div>

<script>
    var ctx = '<%=ctx%>';

    // --- helpers ---
    function escapeHtml(s){ s=s||''; return s.replace(/[&<>"']/g,function(m){return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]);}); }
    function num(x){ return parseFloat(x||"0")||0; }
    function todayLocalISO(){ var d=new Date(); var y=d.getFullYear(), m=('0'+(d.getMonth()+1)).slice(-2), dd=('0'+d.getDate()).slice(-2); return y+'-'+m+'-'+dd; }

    // date labels
    (function(){
        var d=new Date();
        document.getElementById('todayLabel').textContent=d.toLocaleString();
        document.getElementById('sumDate').textContent=d.toDateString();
    })();

    // ----- customer search (datalist) -----
    var custInput = document.getElementById('customerSearch');
    var custList  = document.getElementById('customerList');
    var custId    = document.getElementById('customerId');
    var custName  = document.getElementById('customerName');
    var custForm  = document.getElementById('customerForm');
    var selectedCustomer = document.getElementById('selectedCustomer');

    function debounce(fn,ms){ var t; return function(){ var a=arguments; clearTimeout(t); t=setTimeout(function(){ fn.apply(null,a); },ms); }; }

    function fetchCustomers(q){
        if (!q){ custList.innerHTML=''; return; }
        var url = ctx + '/api/customers?q='+encodeURIComponent(q);
        fetch(url)
            .then(function(r){ if(!r.ok) return []; return r.json(); })
            .then(function(arr){
                custList.innerHTML = arr.map(function(c){
                    return '<option value="'+ escapeHtml(c.customerId + ' - ' + c.name) +'"></option>';
                }).join('');
            }).catch(function(){});
    }
    var searchCustomers = debounce(fetchCustomers,180);
    if (custInput) custInput.addEventListener('input', function(){ searchCustomers(custInput.value); });

    function parseCustomerValue(v){
        var parts = (v||'').split(' - ');
        if (parts.length >= 2){
            return { id: parts[0].trim(), name: parts.slice(1).join(' - ').trim() };
        }
        return null;
    }
    if (custInput) custInput.addEventListener('change', function(){
        var p = parseCustomerValue(custInput.value);
        if (p){
            custId.value = p.id; custName.value = p.name; selectedCustomer.textContent = p.name || '(none)';
            custForm.submit();
        }
    });
    if (custForm) custForm.addEventListener('submit', function(){
        if (!custId.value || !custName.value) {
            var p = parseCustomerValue(custInput.value);
            if (p){ custId.value = p.id; custName.value = p.name; }
        }
    });

    // ----- item search (datalist) -----
    var itemInput = document.getElementById('codeOrName');
    var itemList  = document.getElementById('itemList');

    function fetchItems(q){
        if (!q){ itemList.innerHTML=''; return; }
        fetch(ctx + '/api/items?q=' + encodeURIComponent(q))
            .then(function(res){ if(!res.ok) return []; return res.json(); })
            .then(function(arr){
                var html = arr.map(function(i){
                    return '<option value="'+ i.item_id +'" label="' + escapeHtml(i.item_name) + '"></option>';
                }).join('');
                itemList.innerHTML = html;
            }).catch(function(){});
    }
    var searchItems = debounce(fetchItems,180);
    if (itemInput) itemInput.addEventListener('input', function(){ searchItems(itemInput.value); });

    // ----- totals & balance -----
    var subTotalEl = document.getElementById('subTotal');
    var grandTotalEl = document.getElementById('grandTotal');
    var discountEl = document.getElementById('discount');
    var discountHidden = document.getElementById('discountHidden');
    var paidEl = document.getElementById('paidAmount');
    var balanceEl = document.getElementById('balance');

    function recalc(){
        var subTotal = num(subTotalEl.textContent);
        var discount = num(discountEl.value);
        var grand = Math.max(0, subTotal - discount);
        grandTotalEl.textContent = grand.toFixed(2);
        discountHidden.value = discount.toFixed(2);
        balanceEl.value = (num(paidEl.value) - grand).toFixed(2);
    }
    discountEl.addEventListener('input', recalc);
    paidEl.addEventListener('input', recalc);
    document.getElementById('pmCard').addEventListener('change', function(){
        var grand = parseFloat(grandTotalEl.textContent)||0; paidEl.value = grand.toFixed(2); recalc();
    });
    document.getElementById('pmCash').addEventListener('change', function(){ paidEl.value = "0.00"; recalc(); });
    recalc();

    // ----- Enter on qty => add -----
    document.getElementById('qty').addEventListener('keydown', function(e){
        if(e.key==='Enter'){ e.preventDefault(); document.getElementById('addBtn').click(); }
    });

    // ----- Summary loader + print link setter -----
    function loadSummary(){
        var d = todayLocalISO();
        fetch(ctx + '/api/summary?date=' + encodeURIComponent(d))
            .then(function(r){ if(!r.ok) throw new Error('HTTP '+r.status); return r.json(); })
            .then(function(j){
                var bills = Number(j.bills || 0);
                var total = Number(j.total || 0);
                document.getElementById('sumBills').textContent = bills;
                document.getElementById('sumTotal').textContent = total.toFixed(2);
            })
            .catch(function(err){
                console.error('Summary error:', err);
                document.getElementById('sumBills').textContent = '—';
                document.getElementById('sumTotal').textContent = '—';
            });
        var link = ctx + '/report/daily?date=' + encodeURIComponent(d);
        var btn = document.getElementById('printDaily');
        if (btn) btn.setAttribute('href', link);
    }

    function refreshSummarySoon(){ [500,1500,3000].forEach(function(ms){ setTimeout(loadSummary, ms); }); }

    // ----- Save & Print confirm + refresh summary -----
    var finishForm = document.getElementById('finishForm');
    var okToSubmit = false;
    if (finishForm) finishForm.addEventListener('submit', function (e) {
        if (okToSubmit) return;
        e.preventDefault();

        var grand = parseFloat(document.getElementById('grandTotal').textContent) || 0;
        var paid  = parseFloat(document.getElementById('paidAmount').value) || 0;
        var bal   = parseFloat(document.getElementById('balance').value) || 0;
        var label = bal < 0 ? 'Balance Due' : 'Change';
        var msg = 'Grand Total: ' + grand.toFixed(2) + '\n' +
            'Paid:        ' + paid.toFixed(2)  + '\n' +
            label + ':       ' + Math.abs(bal).toFixed(2) + '\n\n' +
            'Click OK to Save & Print.';
        if (window.confirm(msg)) {
            try {
                document.getElementById('billTableBody').innerHTML = '';
                document.getElementById('subTotal').textContent = '0.00';
                document.getElementById('grandTotal').textContent = '0.00';
                document.getElementById('discount').value = '0.00';
                document.getElementById('paidAmount').value = '0.00';
                document.getElementById('balance').value = '0.00';
            } catch (_) {}
            okToSubmit = true;
            refreshSummarySoon();
            finishForm.submit(); // target="_blank" -> opens PDF
        }
    });

    // Initial + focus refresh
    document.addEventListener('DOMContentLoaded', loadSummary);
    window.addEventListener('focus', loadSummary);
    document.addEventListener('visibilitychange', function(){ if(!document.hidden) loadSummary(); });
</script>
</body>
</html>
