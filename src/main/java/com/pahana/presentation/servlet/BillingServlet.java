// BillingServlet.java
package com.pahana.presentation.servlet;

import com.pahana.business.dto.ItemDTO;
import com.pahana.business.service.ItemService;
import com.pahana.persistence.dao.impl.BillDAOImpl;
import com.pahana.persistence.model.Bill;
import com.pahana.persistence.model.BillItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/billing")
public class BillingServlet extends HttpServlet {

    private final ItemService itemService = new ItemService();
    private final BillDAOImpl billDAO = new BillDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ItemDTO> items = itemService.getAllItems();
        req.setAttribute("items", items);
        req.getRequestDispatcher("billing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] itemIds = req.getParameterValues("itemId");
        String[] quantities = req.getParameterValues("quantity");
        String[] prices = req.getParameterValues("price");
        String[] totals = req.getParameterValues("total");

        if (itemIds == null || itemIds.length == 0) {
            req.setAttribute("error", "No items selected.");
            doGet(req, resp);
            return;
        }

        List<BillItem> billItems = new ArrayList<>();
        double totalAmount = 0;

        for (int i = 0; i < itemIds.length; i++) {
            try {
                int itemId = Integer.parseInt(itemIds[i]);
                int qty = Integer.parseInt(quantities[i]);
                double price = Double.parseDouble(prices[i]);
                double total = Double.parseDouble(totals[i]);

                BillItem item = new BillItem();
                item.setItemId(itemId);
                item.setQuantity(qty);
                item.setUnitPrice(price);
                item.setTotalPrice(total);

                billItems.add(item);
                totalAmount += total;
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("error", "Invalid input in billing form.");
                doGet(req, resp);
                return;
            }
        }

        Bill bill = new Bill();
        bill.setItems(billItems);
        bill.setTotalAmount(totalAmount);

        boolean saved = billDAO.saveBill(bill);
        if (saved) {
            req.setAttribute("success", "Bill submitted successfully!");
        } else {
            req.setAttribute("error", "Failed to save the bill.");
        }

        doGet(req, resp);
    }
}
