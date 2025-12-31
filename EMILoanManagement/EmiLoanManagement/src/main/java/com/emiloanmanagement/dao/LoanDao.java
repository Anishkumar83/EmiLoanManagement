package com.emiloanmanagement.dao;

import com.emiloanmanagement.exceptions.LoanPersistenceException;
import com.emiloanmanagement.exceptions.NothingFoundException;
import com.emiloanmanagement.model.Loan;
import com.emiloanmanagement.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoanDao {

    private static final Logger logger = LoggerFactory.getLogger(LoanDao.class);

    public static final int CUSTOMER_ID_IDX=1;
    public static final int PRINCIPAL_IDX=2;
    public static final int INTEREST_RATE_IDX=3;
    public static final int MONTHS_IDX=4;

    public void createLoan(Connection con, Loan loan) {


        String sql = """
                INSERT INTO loan (customer_id,principal, interest_rate, tenure_months)
                VALUES (?, ?, ?,?)
                """;

        logger.debug("Inserting into the db");
        try (PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(CUSTOMER_ID_IDX,loan.getCustomer_id());
            ps.setDouble(PRINCIPAL_IDX, loan.getPrincipal());
            ps.setDouble(INTEREST_RATE_IDX, loan.getInterestRate());
            ps.setInt(MONTHS_IDX, loan.getTenureMonths());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    loan.setLoanId(rs.getLong(1));
                }
            }

        } catch (Exception e) {
            logger.error("failed to insert loan");
            throw new LoanPersistenceException("Failed to insert loan", e);
        }
    }

    public static final int SIZE_IDX=1;
    public static final int OFFSET_IDX=2;

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

            ps.setInt(SIZE_IDX, size);
            ps.setInt(OFFSET_IDX, offset);

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
            throw new NothingFoundException("Failed to fetch paginated loans", e);
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
            throw new NothingFoundException("Failed to count loans", e);
        }

        return 0;
    }


}
