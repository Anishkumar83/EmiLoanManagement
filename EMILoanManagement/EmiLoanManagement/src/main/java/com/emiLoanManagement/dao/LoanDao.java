package com.emiLoanManagement.dao;

import com.emiLoanManagement.model.Loan;
import com.emiLoanManagement.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoanDao {
    public void createLoan(Loan loan){
        String sql = """
                INSERT into loan (loan_id,customer_name,principal, interest_rate,tenure_months)values (?,?,?,?,?);
                """;
        try {
            Connection con = DbConnection.dbConnect();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1,loan.getLoanId());
            ps.setString(2,loan.getCustomerName());
            ps.setDouble(3,loan.getPrincipal());
            ps.setDouble(4,loan.getInterestRate());
            ps.setInt(5,loan.getTenureMonths());

            int rows=ps.executeUpdate();
            System.out.println("rows inserted into loan table "+ rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
