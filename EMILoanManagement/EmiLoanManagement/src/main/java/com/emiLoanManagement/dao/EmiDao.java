package com.emiLoanManagement.dao;

import com.emiLoanManagement.model.Emi;
import com.emiLoanManagement.util.DbConnection;

import java.sql.*;

public class EmiDao {

    public void saveEmi(Connection con, Emi emi) {

        String sql = """
                INSERT INTO emi_schedule
                (loan_id, emi_amount, interest_component, principal_component,
                 outstanding_balance, due_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, emi.getLoanId());
            ps.setDouble(2, emi.getEmiAmount());
            ps.setDouble(3, emi.getInterest_component());
            ps.setDouble(4, emi.getPrincipal_component());
            ps.setDouble(5, emi.getOutstanding_balance());
            ps.setDate(6, Date.valueOf(emi.getDueDate()));
            ps.setString(7, emi.getStatus());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to insert EMI", e);
        }
    }

    public void payEmi(long emiId) throws SQLException {

        String sql = """
                UPDATE emi_schedule
                SET status = 'PAID'
                WHERE emi_id = ?
                """;

        try (Connection con = DbConnection.dbConnect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, emiId);
            ps.executeUpdate();
        }
    }
}
