package com.pahana.presentation.servlet;

import com.pahana.business.dto.ItemDTO;
import com.pahana.business.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/items")
public class ItemServlet extends HttpServlet {

    private final ItemService itemService = new ItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ItemDTO> items = itemService.getAllItems();
        req.setAttribute("items", items);
        req.getRequestDispatcher("item-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            ItemDTO item = new ItemDTO(0,
                    req.getParameter("itemName"),
                    req.getParameter("description"),
                    Double.parseDouble(req.getParameter("costPrice")),
                    Double.parseDouble(req.getParameter("retailPrice")),
                    Integer.parseInt(req.getParameter("quantity"))
            );
            itemService.addItem(item);

        } else if ("update".equals(action)) {
            ItemDTO item = new ItemDTO(
                    Integer.parseInt(req.getParameter("itemId")),
                    req.getParameter("itemName"),
                    req.getParameter("description"),
                    Double.parseDouble(req.getParameter("costPrice")),
                    Double.parseDouble(req.getParameter("retailPrice")),
                    Integer.parseInt(req.getParameter("quantity"))
            );
            itemService.updateItem(item);

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("itemId"));
            itemService.deleteItem(id);
        }

        resp.sendRedirect("items");
    }
}
