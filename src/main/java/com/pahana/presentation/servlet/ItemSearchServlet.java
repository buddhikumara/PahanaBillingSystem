package com.pahana.presentation.servlet;

import com.pahana.persistence.dao.ItemDAOImpl;
import com.pahana.persistence.model.Item;
import com.pahana.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

@WebServlet("/api/items")
public class ItemSearchServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String q = req.getParameter("q");
        if (q == null) q = "";
        resp.setContentType("application/json;charset=UTF-8");
        try (Connection c = DBUtil.getConnection()) {
            ItemDAOImpl dao = new ItemDAOImpl(c);
            List<Item> items = dao.search(q); // must exist in your DAO
            PrintWriter out = resp.getWriter();
            out.write("[");
            for (int i=0;i<items.size();i++){
                Item it = items.get(i);
                out.write("{\"item_id\":"+it.getItemId()+",\"item_name\":"+json(it.getItemName())+"}");
                if (i<items.size()-1) out.write(",");
            }
            out.write("]");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":"+json(e.getMessage())+"}");
        }
    }
    private static String json(String s){
        if (s == null) return "null";
        return "\"" + s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n") + "\"";
    }

    private static String jsonQuote(String s){
        if (s == null) return "null";
        return "\"" + s.replace("\\","\\\\").replace("\"","\\\"") + "\"";
    }
}
