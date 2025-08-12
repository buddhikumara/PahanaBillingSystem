<%--
  Created by IntelliJ IDEA.
  User: Buddhi
  Date: 29/07/2025
  Time: 22:55
  To change this template use File | Settings | File Templates.
--%>
<%
  javax.servlet.http.HttpSession s = request.getSession(false);
  if (s != null) s.invalidate();
  response.sendRedirect(request.getContextPath() + "/login.jsp");
%>

