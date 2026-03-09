package com.pxczxn.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");

            try (var stmt = connection.createStatement();
                 var rs = stmt.executeQuery("SELECT 1")) {
                assertTrue(rs.next(), "ResultSet should have at least one row");
                assertEquals(1, rs.getInt(1), "Query result should be 1");
            }

            System.out.println("✅ Database connection successful!");
            System.out.println("   Database: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("   Version: " + connection.getMetaData().getDatabaseProductVersion());
        }
    }
}
