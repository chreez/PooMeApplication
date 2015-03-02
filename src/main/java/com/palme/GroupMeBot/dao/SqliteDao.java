package com.palme.GroupMeBot.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.management.RuntimeErrorException;

import com.google.common.base.Preconditions;

public abstract class SqliteDao {

    private final Connection jdbcConnection;

    public SqliteDao(final Connection jdbcConnection) throws SQLException {
        Preconditions.checkNotNull(jdbcConnection);
        this.jdbcConnection = jdbcConnection;
        this.setup();
    }

    public  void tearDown() throws SQLException {
        this.jdbcConnection.close();
    }

    protected Connection getJdbcConnection() throws SQLException {
        return jdbcConnection;
    }

    /**
     * Use this method for setting up any necessary tables.
     * @throws SQLException
     */
    private void setup() {
        for(String createTableQuery : getSchemaSql()) {
            if(createTableQuery == null) {
                continue;//SHOULDNT HAPPEN EVER
            }
            try{
                final Connection connection = getJdbcConnection();
                final Statement tablesQuery = connection.createStatement();
                System.out.println(tablesQuery + " asdasd ");
                System.out.println(createTableQuery + "asdasd");

                tablesQuery.executeUpdate(createTableQuery);
                tablesQuery.close();
//                connection.close(); BAD
            } catch(SQLException sqlException) {
                //No action table already created
//                throw new RuntimeException(sqlException);
            }
        }
    };

    abstract List<String> getSchemaSql();



}
