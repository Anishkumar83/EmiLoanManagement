package com.emiLoanManagement.dao;

import com.emiLoanManagement.model.Loan;
import com.emiLoanManagement.util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDao {

    public void createLoan(Connection con, Loan loan) {

        String sql = """
                INSERT INTO loan (principal, interest_rate, tenure_months)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, loan.getPrincipal());
            ps.setDouble(2, loan.getInterestRate());
            ps.setInt(3, loan.getTenureMonths());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    loan.setLoanId(rs.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to insert loan", e);
        }
    }

    public List<Loan> getLoansPaginated(int page, int size) {

        List<Loan> loans = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql = """
            SELECT loan_id, principal, interest_rate, tenure_months
            FROM loan
            ORDER BY loan_id
            LIMIT ? OFFSET ?
            """;

        try (Connection con = DbConnection.dbConnect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getLong("loan_id"));
                loan.setPrincipal(rs.getDouble("principal"));
                loan.setInterestRate(rs.getDouble("interest_rate"));
                loan.setTenureMonths(rs.getInt("tenure_months"));

                loans.add(loan);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paginated loans", e);
        }

        return loans;
    }

    public int getTotalLoanCount() {

        String sql = "SELECT COUNT(*) FROM loan";

        try (Connection con = DbConnection.dbConnect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to count loans", e);
        }

        return 0;
    }


}
