package com.emiLoanManagement.servlet;

import com.emiLoanManagement.dao.EmiDao;
import com.emiLoanManagement.dao.LoanDao;
import com.emiLoanManagement.model.Emi;
import com.emiLoanManagement.model.Loan;
import com.emiLoanManagement.util.EmiCalculator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/create")
public class LoanCreateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Loan loan= new Loan(Long.parseLong(req.getParameter("loanId")),
                req.getParameter("name"),
                Double.parseDouble(req.getParameter("principal")),
                Double.parseDouble(req.getParameter("rate")),
                Integer.parseInt(req.getParameter("months"))
        );

        LoanDao dao= new LoanDao();
        dao.createLoan(loan);

        double emi = EmiCalculator.calculateEmi(loan.getPrincipal(),
                loan.getInterestRate(),
                loan.getTenureMonths());
        double balance=loan.getPrincipal();
        double monthlyRate = loan.getInterestRate() / (12 *100);
        LocalDate dueDate = LocalDate.now().plusMonths(1);
        EmiDao emiDao= new EmiDao();
        for(int i=1;i<=loan.getTenureMonths();i++){
            double interest = balance * monthlyRate;
            double principalPaid = emi - interest;
            balance = balance - principalPaid;

            Emi emiSchedule = new Emi();
            emiSchedule.setLoanId(loan.getLoanId());
            emiSchedule.setEmiAmount(emi);
            emiSchedule.setInterest_component(interest);
            emiSchedule.setPrincipal_component(principalPaid);

            emiSchedule.setOutstanding_component(Math.max(balance,0));
            emiSchedule.setDueDate(dueDate);

            emiDao.saveEmi(emiSchedule);
            dueDate= dueDate.plusMonths(1);
        }
    }
}
