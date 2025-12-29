package com.emiLoanManagement.servlet;

import com.emiLoanManagement.dao.EmiDao;
import com.emiLoanManagement.dao.LoanDao;
import com.emiLoanManagement.model.Emi;
import com.emiLoanManagement.model.Loan;
import com.emiLoanManagement.util.DbConnection;
import com.emiLoanManagement.util.EmiCalculator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDate;

@WebServlet("/create")
public class LoanCreateServlet extends HttpServlet {

    private static final int SCALE = 2;

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        Connection con = null;

        try {
            String pStr = req.getParameter("principal");
            String rStr = req.getParameter("rate");
            String mStr = req.getParameter("months");

            if (pStr == null || rStr == null || mStr == null) {
                throw new IllegalArgumentException("Missing request parameters");
            }

            BigDecimal principal = new BigDecimal(pStr).setScale(SCALE, RoundingMode.HALF_UP);
            BigDecimal annualRate = new BigDecimal(rStr);
            int months = Integer.parseInt(mStr);

            if (principal.compareTo(BigDecimal.ZERO) <= 0 ||
                    annualRate.compareTo(BigDecimal.ZERO) < 0 ||
                    months <= 0) {
                throw new IllegalArgumentException("Invalid loan inputs");
            }


            Loan loan = new Loan();
            loan.setPrincipal(principal.doubleValue());
            loan.setInterestRate(annualRate.doubleValue());
            loan.setTenureMonths(months);


            con = DbConnection.dbConnect();
            con.setAutoCommit(false);

            LoanDao loanDao = new LoanDao();
            EmiDao emiDao = new EmiDao();


            loanDao.createLoan(con, loan);
            long loanId = loan.getLoanId();

            if (loanId <= 0) {
                throw new IllegalStateException("Loan ID not generated");
            }


            BigDecimal emiAmount = EmiCalculator.calculateEmi(
                    principal, annualRate, months
            );

            BigDecimal balance = principal;
            BigDecimal monthlyRate =
                    annualRate.divide(BigDecimal.valueOf(1200),
                            10, RoundingMode.HALF_UP);

            LocalDate dueDate = LocalDate.now().plusMonths(1);


            for (int month = 1; month <= months; month++) {

                BigDecimal interest = balance.multiply(monthlyRate)
                        .setScale(SCALE, RoundingMode.HALF_UP);

                BigDecimal principalPaid = emiAmount.subtract(interest)
                        .setScale(SCALE, RoundingMode.HALF_UP);


                if (month == months) {
                    principalPaid = balance;
                    emiAmount = interest.add(balance)
                            .setScale(SCALE, RoundingMode.HALF_UP);
                    balance = BigDecimal.ZERO;
                } else {
                    balance = balance.subtract(principalPaid)
                            .setScale(SCALE, RoundingMode.HALF_UP);
                }

                Emi emi = new Emi();
                emi.setLoanId(loanId);
                emi.setEmiAmount(emiAmount.doubleValue());
                emi.setInterest_component(interest.doubleValue());
                emi.setPrincipal_component(principalPaid.doubleValue());
                emi.setOutstanding_balance(balance.doubleValue());
                emi.setDueDate(dueDate);
                emi.setStatus("PENDING");

                emiDao.saveEmi(con, emi);

                dueDate = dueDate.plusMonths(1);
            }


            con.commit();

            resp.setContentType("text/plain");
            resp.getWriter().println(
                    "Loan & EMI schedule created successfully. Loan ID = " + loanId
            );

        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new ServletException(e);

        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
