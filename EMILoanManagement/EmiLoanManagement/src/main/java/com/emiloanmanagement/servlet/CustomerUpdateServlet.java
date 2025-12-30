package com.emiloanmanagement.servlet;

import com.emiloanmanagement.dao.CustomerDao;
import com.emiloanmanagement.model.Customers;
import com.emiloanmanagement.util.DbConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet("/customer/update")
public class CustomerUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Connection con = null;

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

            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
