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

@WebServlet("/customers/create")
public class CustomerCreationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        Connection con = null;
        try {
            String name = request.getParameter("customerName");
            String email = request.getParameter("email");
            String dobParam = request.getParameter("dob");
            String gender = request.getParameter("gender");

            if (name == null || email == null || dobParam == null || gender == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid data");
                return;
            }

            Customers customers = new Customers();
            customers.setCustomer_name(name);
            customers.setEmail(email);
            customers.setDob(Date.valueOf(dobParam));
            customers.setGender(gender);

            con = DbConnection.dbConnect();
            con.setAutoCommit(false);

            CustomerDao  customerDao = new CustomerDao();
            long customerId = customerDao.createCustomers(con,customers);
            con.commit();

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().println("Customer Created Successfully");
        }
        catch (Exception e){
            try{
                if(con!=null){
                    con.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal Server Error");
        }
        finally {
            try {
                if(con!=null){
                    con.close();
                }
            }catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
