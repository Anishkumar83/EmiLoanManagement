package com.emiloanmanagement.dao;

import com.emiloanmanagement.exceptions.EmiPersistenceException;
import com.emiloanmanagement.exceptions.IdNotFoundException;
import com.emiloanmanagement.exceptions.NothingFoundException;
import com.emiloanmanagement.model.Customers;
import com.emiloanmanagement.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    private static final Logger LOGGER= LoggerFactory.getLogger(CustomerDao.class);

    private static final int CUSTOMER_NAME_IDX=1;
    private static final int EMAIL_IDX=2;
    private static final int DOB_IDX=3;
    private static final int GENDER_IDX=4;
    public long createCustomers(Connection con, Customers customers) {

        LOGGER.info("Establishing Sql query");

        String sql = """
                INSERT INTO customers (customer_name, email, dob, gender)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(CUSTOMER_NAME_IDX, customers.getCustomer_name());
            ps.setString(EMAIL_IDX, customers.getEmail());
            ps.setDate(DOB_IDX, new java.sql.Date(customers.getDob().getTime()));
            ps.setString(GENDER_IDX, customers.getGender());

            ps.executeUpdate();


            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                   return rs.getLong(1);
                }
            }
        }
        catch ( SQLException e){
            throw new EmiPersistenceException("Customer failed to save");
        }
        return -1;
    }

    private static final int SIZE_IDX=1;
    private static final int OFFSET_IDX=2;

    public List<Customers>  getAllCustomers(int page, int size){
        List<Customers> customers = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql=
                """
                        SELECT * FROM customers
                        ORDER BY customer_id
                        LIMIT ? OFFSET ?;
                        """;

        try(Connection con= DbConnection.dbConnect();
            PreparedStatement ps = con.prepareStatement(sql);){

            ps.setInt(SIZE_IDX, size);
            ps.setInt(OFFSET_IDX, offset);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customers customer = new Customers();
                    customer.setCustomer_id(rs.getInt("customer_id"));
                    customer.setCustomer_name(rs.getString("customer_name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setDob(rs.getDate("dob"));
                    customer.setGender(rs.getString("gender"));
                    customers.add(customer);
                }
            }
        }catch (SQLException e){
          throw new NothingFoundException("Nothing in the db");
        }
        return customers;
    }

    public int getTotalCount(){

        LOGGER.info("Writing sql query for total count of customers");
        String sql = """
                SELECT COUNT(*) FROM customers;
                """;
        try(Connection con= DbConnection.dbConnect()){
            PreparedStatement ps = con.prepareStatement(sql);
            try(ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                return rs.getInt(1);
            }}
        } catch (SQLException e) {
            throw new NothingFoundException("Nothing in the db");
        }


        return 0;
    }

    public static final int ID_IDX=1;
    public Customers findByID(Connection con, Long id){

        LOGGER.info("Writing sql query for customers find by id ");
        String sql= """
                SELECT customer_id, customer_name, email, dob, gender
                FROM customers
                WHERE customer_id =?
                """;

        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(ID_IDX,id);

            ResultSet rs= ps.executeQuery();
            if(rs.next()){
                Customers customer= new Customers();

                customer.setCustomer_id(rs.getLong("customer_id"));
                customer.setCustomer_name(rs.getString("customer_name"));
                customer.setEmail(rs.getString("email"));
                customer.setDob(rs.getDate("dob"));
                customer.setGender(rs.getString("gender"));
                return customer;
            }

        } catch (SQLException e) {
            throw new IdNotFoundException("Customer id is not found id");
        }
        return null;
    }

    private static final int CUSTOMER_ID_IDX=5;
    public void updateCustomerById(Connection con, long id, String name, String email, Date dob, String gender){

        LOGGER.info("Inside the update customer method");
        Customers existingCustomer = findByID(con,id);

        LOGGER.info("Checking whether the customer is already present in the id");
        if(existingCustomer == null){
            return;
        }

        LOGGER.info("Writing sql query for updating the customer by id ={}",id);
        String sql = """
                UPDATE customers
                SET customer_name=?,
                    email=?,
                    dob=?,
                    gender=?
                WHERE customer_id=?
                """;
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(CUSTOMER_NAME_IDX,name);
            ps.setString(EMAIL_IDX, email);
            ps.setDate(DOB_IDX, dob);
            ps.setString(GENDER_IDX, gender);
            ps.setLong(CUSTOMER_ID_IDX,id);
            ps.executeUpdate();
            LOGGER.info("Successfully update the customer by id={}",id);
        } catch (SQLException e) {
            throw new IdNotFoundException("id is not found to update the customer");
        }



    }
}
