package com.emiLoanManagement.dao;

import com.emiLoanManagement.model.Emi;
import com.emiLoanManagement.util.DbConnection;

import java.sql.*;
import java.time.LocalDate;

public class EmiDao {

    public void saveEmi(Emi emi){
        String sql = """
                INSERT into emi_schedule
                (loan_id, emi_amount,interest_component,principal_component,
                outstanding_component, due_date, status)
                VALUES (?,?,?,?,?,?,?)
                """;

        try{
            Connection con= DbConnection.dbConnect();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1,emi.getLoanId());
            ps.setDouble(2,emi.getEmiAmount());
            ps.setDouble(3,emi.getInterest_component());
            ps.setDouble(4, emi.getPrincipal_component());
            ps.setDouble(5,emi.getOutstanding_component());
            ps.setDate(6,Date.valueOf(emi.getDueDate()));
            ps.setString(7,"PENDING");

            ps.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void payEmi(long emiId) throws SQLException {
        String sql = """
                UPDATE emi_schedule SET status = "PAID"
                WHERE emi_id=?
                """;

        Connection con= DbConnection.dbConnect();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1,emiId);
        ps.executeUpdate();
    }
}
