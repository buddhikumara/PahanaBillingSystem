<%--
  Created by IntelliJ IDEA.
  User: Buddhi
  Date: 29/07/2025
  Time: 22:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
  // Use a different variable name to avoid conflict
  HttpSession currentSession = request.getSession(false);

  // If no session or user not logged in, redirect to login
  if (currentSession == null || currentSession.getAttribute("username") == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  String username = (String) currentSession.getAttribute("username");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Dashboard</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background: #f4f4f9;
      text-align: center;
      padding: 50px;
    }
    h1 {
      color: #333;
    }
    .logout-btn {
      display: inline-block;
      margin-top: 20px;
      padding: 10px 20px;
      background-color: #ff4b5c;
      color: #fff;
      text-decoration: none;
      border-radius: 5px;
    }
    .logout-btn:hover {
      background-color: #e63e4d;
    }
  </style>
</head>
<body>
<h1>Welcome, <%= username %>!</h1>
<p>You have successfully logged in to the Pahana Billing System Dashboard.</p>
<a href="logout.jsp" class="logout-btn">Logout</a>
</body>
</html>
