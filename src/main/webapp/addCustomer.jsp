<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Add Customer</title></head>
<body>
<h2>Add New Customer</h2>

<form method="post" action="addCustomer">
    Account Number: <input type="text" name="accountNumber" required><br>
    Name: <input type="text" name="name" required><br>
    Address: <input type="text" name="address"><br>
    Phone: <input type="text" name="phone"><br>
    Units Consumed: <input type="number" name="units" required><br>
    <button type="submit">Save</button>
</form>

<% if ("true".equals(request.getParameter("success"))) { %>
<p style="color:green;">Customer added successfully!</p>
<% } %>

</body>
</html>
