package com.palme.GroupMeBot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableList;

public class HeartbeatsDao extends SqliteDao {
    private static final String CREATE_HEARTBEATS_SQL = "create table IF NOT EXISTS heartbeats (system_name text PRIMARY KEY,  heartbeat_timestamp timestamp);";
    private static final String ADD_SYSTEM_HEARTBEAT_SQL = "instert into heartbeats (?, ?);";
    private static final String UPDATE_HEARTBEAT_SQL = "update heartbeats set heartbeat_timestamp = ? where system_name = '%s'";
    private static final String GET_HEARTBEAT_SQL = "select heartbeat_timestamp from heartbeats where system_name = '%s';";

    public HeartbeatsDao(final Connection jdbcConnection) throws SQLException {
        super(jdbcConnection);
    }

    public void updateHeartbeat(final String systemName) throws SQLException {
        final Instant now = Instant.now();
        final PreparedStatement updateHeartbeat = getJdbcConnection().prepareStatement(String.format(UPDATE_HEARTBEAT_SQL, systemName));
        updateHeartbeat.setTimestamp(1, new Timestamp(now.getMillis()));
        updateHeartbeat.executeUpdate();
    }

    public Instant getHeartbeat(final String systemName) throws SQLException {
        final ResultSet resultSet = getJdbcConnection().createStatement().executeQuery(String.format(GET_HEARTBEAT_SQL, systemName));
        if(resultSet.next()){
            return new Instant(resultSet.getTimestamp(1).getTime());
        } else {
            return createHeartbeatsForSystem(systemName);
        }
    }

    public Instant createHeartbeatsForSystem(final String systemName) throws SQLException {
        final Instant now = Instant.now();
        final PreparedStatement createHeartbeatSystem = getJdbcConnection().prepareStatement(ADD_SYSTEM_HEARTBEAT_SQL);
        createHeartbeatSystem.setString(1, systemName);
        createHeartbeatSystem.setTimestamp(2, new Timestamp(now.getMillis()));
        createHeartbeatSystem.executeUpdate();
        return now;
    }


    @Override
    List<String> getSchemaSql() {
        return ImmutableList.of(CREATE_HEARTBEATS_SQL);
    }
}
