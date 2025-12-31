package com.emiloanmanagement.servlet;

import com.emiloanmanagement.dao.CustomerDao;
import com.emiloanmanagement.model.Customers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/customers/get")
public class CustomerPaginationServlet extends HttpServlet {

    private static final Logger LOGGER= LoggerFactory.getLogger(CustomerPaginationServlet.class);

    @Serial
    private static final long serialVersionUID = 1L;
    private final ObjectMapper mapper = new ObjectMapper();
    private final CustomerDao customerDao = new CustomerDao();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page =1;
        int size =5;

        if (req.getParameter("page") != null) {
            page = Integer.parseInt(req.getParameter("page"));
        }
        if (req.getParameter("size") != null) {
            size = Integer.parseInt(req.getParameter("size"));
        }
        if (page <= 0 || size <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Page and Size must be greater than 0");
            return;
        }

        List<Customers> customers= null;

            customers = customerDao.getAllCustomers(page, size);

        int totalRecords= customerDao.getTotalCount();
        int totalPages= (int) Math.ceil((double)totalRecords/(double)size);

        Map<String,Object> map = new HashMap<>();
        map.put("totalPages", page);
        map.put("totalRecords", size);
        map.put("totalRecordsTotal", totalRecords);
        map.put("totalRecordsPages", totalPages);
        map.put("customers", customers) ;

        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(map));
    }
}
