package com.emiloanmanagement.servlet;

import com.emiloanmanagement.dao.CustomerDao;
import com.emiloanmanagement.model.Customers;
import com.emiloanmanagement.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet("/customer/update")
public class CustomerUpdateServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID= 1L;

    private static final Logger LOGGER= LoggerFactory.getLogger(CustomerUpdateServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Connection con = null;

        LOGGER.info("Inside the customer update servlet");
        long id = Long.parseLong(req.getParameter("id"));
        String name = req.getParameter("name");
        String email=req.getParameter("email");
        String dobStr= req.getParameter("dob");
        String gender= req.getParameter("gender");

        try {
            con= DbConnection.dbConnect();
            con.setAutoCommit(false);

            CustomerDao customerDao= new CustomerDao();
            Customers existingCustomer= customerDao.findByID(con,id);
            if(existingCustomer==null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Id not found");
                return;
            }

            customerDao.updateCustomerById(con,
                    id,name,email,Date.valueOf(dobStr),gender);

            con.commit();

            LOGGER.info("Customer updated successfully");
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException e) {
            LOGGER.error("Connection error occurs");
            throw new RuntimeException(e);
        }

    }
}
