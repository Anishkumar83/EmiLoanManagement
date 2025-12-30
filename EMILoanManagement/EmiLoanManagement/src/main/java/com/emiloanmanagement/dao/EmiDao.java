package com.emiloanmanagement.dao;

import com.emiloanmanagement.exceptions.EmiPersistenceException;
import com.emiloanmanagement.model.Emi;
import com.emiloanmanagement.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class EmiDao {


    private static final Logger logger = LoggerFactory.getLogger(EmiDao.class);

    private static final int LOAN_ID_IDX =1;
    private static final int EMI_AMOUNT_IDX=2;
    private static final int INTEREST_COMPONENT=3;
    private static final int PRINCIPAL_COMPONENT_IDX=4;
    private static final int OUTSTANDING_BALANCE_IDX=5;
    private static final int DUE_DATE_IDX=6;
    private static final int STATUS_IDX=7;

    public void saveEmi(Connection con, Emi emi) {

        String sql = """
                INSERT INTO emi_schedule
                (loan_id, emi_amount, interest_component, principal_component,
                 outstanding_balance, due_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        logger.debug("Saving emi for loan_id={} dueDate ={}",emi.getLoanId() , emi.getDueDate());
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(LOAN_ID_IDX, emi.getLoanId());
            ps.setDouble(EMI_AMOUNT_IDX, emi.getEmiAmount());
            ps.setDouble(INTEREST_COMPONENT, emi.getInterest_component());
            ps.setDouble(PRINCIPAL_COMPONENT_IDX, emi.getPrincipal_component());
            ps.setDouble(OUTSTANDING_BALANCE_IDX, emi.getOutstanding_balance());
            ps.setDate(DUE_DATE_IDX, Date.valueOf(emi.getDueDate()));
            ps.setString(STATUS_IDX, emi.getStatus());

            ps.executeUpdate();
            logger.info("Emi saved for loan_id={} with outstanding balance={}",emi.getLoanId(),emi.getOutstanding_balance());

        } catch (Exception e) {
            logger.error("Failed to save emi for loanid={}",emi.getLoanId());
            throw new EmiPersistenceException("Failed to insert EMI", e);
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
