package com.emiloanmanagement.servlet;

import com.emiloanmanagement.dao.EmiDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/pay")
public class EmiPaymentServlet extends HttpServlet {

    private static final long serialVersionUID= 1L;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long emiId = Long.parseLong(req.getParameter("emiId"));

        EmiDao emi = new EmiDao();

        try {
            emi.payEmi(emiId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
