package com.pahana.presentation.servlet;

import com.pahana.persistence.dao.CustomerDAOImpl;
import com.pahana.persistence.model.Customer;
import com.pahana.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

// com.pahana.presentation.servlet.CustomerSearchServlet
@WebServlet("/api/customers")
public class CustomerSearchServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String q = Optional.ofNullable(req.getParameter("q")).orElse("");
        resp.setContentType("application/json;charset=UTF-8");
        try (Connection c = DBUtil.getConnection()) {
            List<Customer> rows = new CustomerDAOImpl(c).search(q);
            PrintWriter out = resp.getWriter(); out.write("[");
            for (int i=0;i<rows.size();i++){ Customer cu = rows.get(i);
                out.write("{\"customerId\":"+json(cu.getCustomerId())+",\"name\":"+json(cu.getName())+"}");
                if(i<rows.size()-1) out.write(",");
            } out.write("]");
        } catch (Exception e){ resp.setStatus(500); resp.getWriter().write("{\"error\":"+json(e.getMessage())+"}"); }
    }
    private static String json(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
  }
