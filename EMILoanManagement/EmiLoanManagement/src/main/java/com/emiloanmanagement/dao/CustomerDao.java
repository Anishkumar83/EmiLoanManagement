package com.emiloanmanagement.dao;

import com.emiloanmanagement.model.Customers;
import com.emiloanmanagement.util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    public long createCustomers(Connection con, Customers customers){
        String sql = """
                INSERT INTO customers (customer_name, email, dob, gender)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customers.getCustomer_name());
            ps.setString(2, customers.getEmail());
            ps.setDate(3, new java.sql.Date(customers.getDob().getTime()));
            ps.setString(4, customers.getGender());

            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                   return rs.getLong(1);
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public List<Customers>  getAllCustomers(int page, int size){
        List<Customers> customers = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql=
                """
                        SELECT * FROM customers
                        ORDER BY customer_id
                        LIMIT ? OFFSET ?;
                        """;

        try(Connection con= DbConnection.dbConnect()){
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, page);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Customers customer = new Customers();
                customer.setCustomer_id(rs.getInt("customer_id"));
                customer.setCustomer_name(rs.getString("customer_name"));
                customer.setEmail(rs.getString("email"));
                customer.setDob(rs.getDate("dob"));
                customer.setGender(rs.getString("gender"));
                customers.add(customer);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return customers;
    }

    public int getTotalCount(){
        String sql = """
                SELECT COUNT(*) FROM customers;
                """;
        try(Connection con= DbConnection.dbConnect()){
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public Customers findByID(Connection con, Long id){
        String sql= """
                SELECT customer_id, customer_name, email, dob, gender
                FROM customers
                WHERE customer_id =?
                """;

        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1,id);

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

        }catch (Exception e){
            throw new RuntimeException("Failed to fetch data of id "+id);
        }
        return null;
    }

    public boolean updateCustomerById(Connection con, long id, String name, String email, Date dob, String gender){
        Customers existingCustomer = findByID(con,id);

        if(existingCustomer == null){
            return false;
        }

        String sql = """
                UPDATE customers
                SET customer_name=?,
                    email=?,
                    dob=?,
                    gender=?
                WHERE customer_id=?
                """;
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1,name);
            ps.setString(2, email);
            ps.setDate(3, dob);
            ps.setString(4, gender);
            ps.setLong(5,id);
            ps.executeUpdate();
            return true;
        }catch (Exception e){
            throw new RuntimeException("Failed to update a customer for an id");
        }


    }
}
