package com.pahana.presentation.servlet;

import com.pahana.business.dto.CustomerDTO;
import com.pahana.business.service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/addCustomer")
public class CustomerServlet extends HttpServlet {

    private final CustomerService service = new CustomerService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String account = req.getParameter("accountNumber");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        int units = Integer.parseInt(req.getParameter("units"));

        CustomerDTO dto = new CustomerDTO(account, name, address, phone, units);
        service.addCustomer(dto);

        // Redirect or message
        resp.sendRedirect("addCustomer.jsp?success=true");
    }
}
