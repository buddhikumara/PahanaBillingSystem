<%--
  Created by IntelliJ IDEA.
  User: Buddhi
  Date: 29/07/2025
  Time: 22:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Login - Pahana Billing</title>
  <style>
    body {
      font-family: 'Poppins', sans-serif;
      background: linear-gradient(to right, #1e3c72, #2a5298);
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    }
    .login-box {
      background: white;
      padding: 40px;
      border-radius: 15px;
      box-shadow: 0 10px 25px rgba(0,0,0,0.2);
      width: 350px;
    }
    .login-box h2 {
      text-align: center;
      margin-bottom: 30px;
      color: #2a5298;
    }
    .login-box input {
      width: 100%;
      padding: 12px;
      margin: 10px 0;
      border-radius: 8px;
      border: 1px solid #ccc;
    }
    .login-box button {
      width: 100%;
      padding: 12px;
      border: none;
      border-radius: 8px;
      background: #2a5298;
      color: white;
      font-weight: bold;
      cursor: pointer;
      transition: 0.3s;
    }
    .login-box button:hover {
      background: #1e3c72;
    }
    .error {
      color: red;
      text-align: center;
    }
  </style>
</head>
<body>
<div class="login-box">
  <h2>Login</h2>
  <form name="loginForm" action="login" method="post" onsubmit="return validateForm()">
    <input type="text" name="username" placeholder="Username" />
    <input type="password" name="password" placeholder="Password" />
    <button type="submit">Login</button>
    <div class="error">${errorMessage}</div>
  </form>
</div>

<script>
  function validateForm() {
    let username = document.forms["loginForm"]["username"].value;
    let password = document.forms["loginForm"]["password"].value;
    if (username === "" || password === "") {
      alert("Both fields are required!");
      return false;
    }
    return true;
  }
</script>
</body>
</html>
