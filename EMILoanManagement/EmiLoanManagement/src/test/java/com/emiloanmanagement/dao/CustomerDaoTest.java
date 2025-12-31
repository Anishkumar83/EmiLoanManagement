package com.emiloanmanagement.dao;

import com.emiloanmanagement.exceptions.EmiPersistenceException;
import com.emiloanmanagement.model.Customers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDaoTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
    }

    @Test
    void testCreateCustomers() throws Exception {

        Customers customers = new Customers();
        customers.setCustomer_name("Anish");
        customers.setEmail("anish@gmail.com");
        customers.setDob(Date.valueOf("2001-07-16"));
        customers.setGender("Male");

        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(1L);

        long id = customerDao.createCustomers(connection, customers);

        assertEquals(1L, id);

        verify(preparedStatement).setString(1, "Anish");
        verify(preparedStatement).setString(2, "anish@gmail.com");
        verify(preparedStatement).setDate(eq(3), any(Date.class));
        verify(preparedStatement).setString(4, "Male");
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Failure of creating customers based on sql exception")
    void testFailureCreateCustomers() throws Exception{
        Customers customers= new Customers();
        customers.setCustomer_name("Anish");
        customers.setEmail("anish@gmail.com");
        customers.setDob(Date.valueOf("2001-07-16"));
        customers.setGender("Male");

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("DB exception"));

        EmiPersistenceException exception=assertThrows(EmiPersistenceException.class,()->
                customerDao.createCustomers(connection,customers));

        assertEquals("Customer failed to save",exception.getMessage());

        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Failure of creating customers based on generating ids")
    void testFailureByGeneratingIds() throws Exception{
        Customers customers= new Customers();
        customers.setCustomer_name("Anish");
        customers.setEmail("anish@gmail.com");
        customers.setDob(Date.valueOf("2001-07-16"));
        customers.setGender("Male");

        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);


        long id = customerDao.createCustomers(connection, customers);

        assertEquals(-1L, id);

    }




}