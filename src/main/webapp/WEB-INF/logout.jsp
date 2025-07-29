<%--
  Created by IntelliJ IDEA.
  User: Buddhi
  Date: 29/07/2025
  Time: 22:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
  HttpSession sessionLogout = request.getSession(false);
  if (sessionLogout != null) {
    sessionLogout.invalidate();
  }
  response.sendRedirect("login.jsp");
%>

