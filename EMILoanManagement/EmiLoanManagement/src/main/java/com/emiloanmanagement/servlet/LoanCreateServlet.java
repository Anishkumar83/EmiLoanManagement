package com.emiloanmanagement.servlet;

import com.emiloanmanagement.dao.EmiDao;
import com.emiloanmanagement.dao.LoanDao;
import com.emiloanmanagement.model.Emi;
import com.emiloanmanagement.model.Loan;
import com.emiloanmanagement.util.DbConnection;
import com.emiloanmanagement.util.EmiCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDate;

@WebServlet("/create")
public class LoanCreateServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int SCALE = 2;

    private static final Logger log =
            LoggerFactory.getLogger(LoanCreateServlet.class);

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        log.info("Loan creation request received");

        try (Connection con = DbConnection.dbConnect()) {

            con.setAutoCommit(false);

            long customer_id= Long.parseLong(req.getParameter("cus_id"));
            BigDecimal principal = readPrincipal(req);
            BigDecimal rate = readRate(req);
            int months = readMonths(req);

            validateLoanInputs(customer_id,principal, rate, months);

            long loanId = createLoan(con, customer_id,principal, rate, months);
            createEmiSchedule(con, loanId, principal, rate, months);

            con.commit();

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("Loan created successfully. LoanId=" + loanId);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("Loan creation failed", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to create loan");
        }
    }



    private BigDecimal readPrincipal(HttpServletRequest req) {
        String value = req.getParameter("principal");
        if (value == null) {
            throw new IllegalArgumentException("Missing principal");
        }
        return new BigDecimal(value).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal readRate(HttpServletRequest req) {
        String value = req.getParameter("rate");
        if (value == null) {
            throw new IllegalArgumentException("Missing rate");
        }
        return new BigDecimal(value);
    }

    private int readMonths(HttpServletRequest req) {
        String value = req.getParameter("months");
        if (value == null) {
            throw new IllegalArgumentException("Missing months");
        }
        return Integer.parseInt(value);
    }


    private void validateLoanInputs(Long cus_id,BigDecimal principal,
                                    BigDecimal rate,
                                    int months) {

        if(cus_id <= 0 ){
            throw new IllegalArgumentException("customer id must be positive");
        }
        if (principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Principal must be positive");
        }
        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rate cannot be negative");
        }
        if (months <= 0) {
            throw new IllegalArgumentException("Months must be positive");
        }
    }



    private long createLoan(Connection con,
                            long customer_id,
                            BigDecimal principal,
                            BigDecimal rate,
                            int months) {

        Loan loan = new Loan();
        loan.setCustomer_id(customer_id);
        loan.setPrincipal(principal.doubleValue());
        loan.setInterestRate(rate.doubleValue());
        loan.setTenureMonths(months);

        LoanDao loanDao = new LoanDao();
        loanDao.createLoan(con, loan);

        log.info("Loan created with loanId={}", loan.getLoanId());
        return loan.getLoanId();
    }



    private void createEmiSchedule(Connection con,
                                   long loanId,
                                   BigDecimal principal,
                                   BigDecimal rate,
                                   int months) {

        EmiDao emiDao = new EmiDao();

        BigDecimal emiAmount =
                EmiCalculator.calculateEmi(principal, rate, months);

        BigDecimal balance = principal;
        BigDecimal monthlyRate =
                rate.divide(BigDecimal.valueOf(1200),
                        10, RoundingMode.HALF_UP);

        LocalDate dueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= months; i++) {

            BigDecimal interest = balance.multiply(monthlyRate)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal principalPaid = emiAmount.subtract(interest)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            balance = balance.subtract(principalPaid)
                    .max(BigDecimal.ZERO);

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
    }
}