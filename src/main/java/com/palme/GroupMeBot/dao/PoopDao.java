package com.palme.GroupMeBot.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.palme.GroupMeBot.dao.model.PoopInfo;
import com.palme.GroupMeBot.dao.model.PoopMetrics;
import com.palme.GroupMeBot.dao.model.UserInfo;

/**
 * Contains sql and data access for both tables
 *  poop_metrics and poops
 * @author Chris
 *
 */
public class PoopDao extends SqliteDao{
    private static final Logger logger = LoggerFactory.getLogger(PoopDao.class);
    static private final String CREATE_SEQUENCE = "CREATE SEQUENCE POOP_ID_SEQ START 1";
    static private final String CREATE_POOPS_SQL = "create table IF NOT EXISTS poops (poop_id integer PRIMARY KEY,  user_id integer, consistency integer, creation_date timestamp);";
    static private final String CREATE_POOP_METRICS_SQL = "create table IF NOT EXISTS poop_metrics ( user_id integer primary key, poo_count integer, consistency_avg real, frequency_avg real);";

    static private final String INSERT_POOP_METRICS_SQL = "INSERT INTO POOP_METRICS (user_id, poo_count, consistency_avg, frequency_avg) VALUES (?,?,?,?);";
    static private final String UPDATE_POOP_METRICS = "UPDATE POOP_METRICS SET poo_count = ? , consistency_avg = ? , frequency_avg = ? WHERE USER_ID = %s;";
    static private final String GET_ALL_POOP_METRICS_SQL = "SELECT user_id, poo_count, consistency_avg, frequency_avg FROM POOP_METRICS;";
    static private final String GET_POOP_METRICS_SQL = "SELECT user_id, poo_count, consistency_avg, frequency_avg FROM POOP_METRICS WHERE USER_ID = %s;";

    static private final String INSERT_POOP_SQL = "INSERT INTO poops (user_id, consistency, creation_date, poop_id) VALUES (?, ?, ?, ? );";
    static private final String GET_POOP_SQL = "SELECT consistency, creation_date FROM POOPS WHERE poop_id = %s;";
    static private final String GET_POOPS_SQL = "SELECT user_id, consistency, creation_date FROM POOPS WHERE user_id = %s;";

    static private final String NEXT_VAL_POOP_ID_SEQ = "SELECT nextval('POOP_ID_SEQ');";

    public PoopDao(final Connection jdbcConnection) throws SQLException {
        super(jdbcConnection);
    }

    public Integer createPoop(final PoopInfo newPoop, final UserInfo userInfo) throws SQLException {
        logger.debug("Creating poop for user{}! {} ", userInfo, newPoop);
        System.out.println("Creating poop" + userInfo + newPoop);
        final PoopInfo oldPoopInfo;
        if(userInfo.getLastPooRowIndex() != null) {
            System.out.println(String.format(GET_POOP_SQL, userInfo.getLastPooRowIndex()));
            ResultSet result;
            try{
                 result = getJdbcConnection().createStatement().executeQuery(String.format(GET_POOP_SQL, userInfo.getLastPooRowIndex()));
            } catch(Exception e ) {
                e.printStackTrace();
                result = null;
            }
            if(result.next()) {
                System.out.println("Got some old poop info lol");
                oldPoopInfo = new PoopInfo();
                oldPoopInfo.setConsistency(result.getInt(1));
                oldPoopInfo.setCreationDate(new Instant(result.getDate(2)));
                oldPoopInfo.setUserId(userInfo.getUserId());
            } else {
                System.out.println("Got no old poop info");
                oldPoopInfo = null;
                initPoopMetricsTable(userInfo.getUserId());
            }
        } else {
            System.out.println("No other poops");
            oldPoopInfo = null;
            initPoopMetricsTable(userInfo.getUserId());
        }
//        POOP_ID_SEQ

        final int poop_id = getNextSequenceVal();

        final PoopMetrics metrics = getPoopMetrics(userInfo.getUserId());
        System.out.println("Inserting poop");
        final PreparedStatement insertPoop = getJdbcConnection().prepareStatement(INSERT_POOP_SQL);
        insertPoop.setInt(1, userInfo.getUserId());
        if(newPoop.getConsistency() != null) {
            insertPoop.setInt(2, newPoop.getConsistency());
        } else {
            insertPoop.setNull(2, Types.INTEGER);
        }
        insertPoop.setTimestamp(3, new Timestamp(newPoop.getCreationDate().getMillis()));
        insertPoop.setInt(4, poop_id);

        insertPoop.executeUpdate();

        final PoopMetrics newMetrics = metrics.updateMetrics(getAllPoopsForUser(userInfo.getUserId()));
        updatePoopMetrics(newMetrics);
        System.out.println("Update metrics" + newMetrics);

        return poop_id;
    }
    public SortedSet<PoopInfo> getAllPoopsForUser(final Integer userId) throws SQLException {
        final Statement getPoops = getJdbcConnection().createStatement();
        final ResultSet resultSet = getPoops.executeQuery(String.format(GET_POOPS_SQL, userId));
        final ImmutableSortedSet.Builder<PoopInfo> poops = ImmutableSortedSet.naturalOrder();
        while(resultSet.next()) {
            final PoopInfo poopInfo = poopRowMapper(resultSet);
            poops.add(poopInfo);
        }

        return poops.build();
    }

    public SortedSet<PoopMetrics> getAllPoopMetrics() throws SQLException {
        final ImmutableSortedSet.Builder<PoopMetrics> resultBuilder = ImmutableSortedSet.naturalOrder();
        final ResultSet resultSet = getJdbcConnection().createStatement().executeQuery(String.format(GET_ALL_POOP_METRICS_SQL));
        while(resultSet.next()) {
            resultBuilder.add(poopMetricsRowMapper(resultSet));
        }
        return resultBuilder.build();
    }

    public PoopMetrics getPoopMetrics(final Integer userId) throws SQLException {
        final ResultSet resultSet = getJdbcConnection().createStatement().executeQuery(String.format(GET_POOP_METRICS_SQL, userId));
        if(resultSet.next()){
            return poopMetricsRowMapper(resultSet);
        } else {
            return null;
        }
    }

    private PoopMetrics poopMetricsRowMapper(final ResultSet resultSet) throws SQLException {
        final PoopMetrics result = new PoopMetrics();
            result.setUserId(resultSet.getInt(1));
            result.setPooCount(resultSet.getInt(2));
            result.setConsistencyAvg(resultSet.getFloat(3));
            result.setFrequencyAvgMillis(resultSet.getInt(4));
        return result;
    }

    public void initPoopMetricsTable(final Integer userId) {
        System.out.println("initing poop metrics table for user" + userId);
        try {
            final PreparedStatement insertMetrics = this.getJdbcConnection().prepareStatement(INSERT_POOP_METRICS_SQL);
            insertMetrics.setInt(1, userId);
            insertMetrics.setInt(2, 0);
            insertMetrics.setInt(3, 0);
            insertMetrics.setInt(4, 0);
            insertMetrics.executeUpdate();
        } catch (SQLException e) {
            // if it exists
            e.printStackTrace();
        }
    }

    public void updatePoopMetrics(final PoopMetrics poopMetrics) throws SQLException {
        final PreparedStatement insertPoop = getJdbcConnection().prepareStatement(String.format(UPDATE_POOP_METRICS, poopMetrics.getUserId()));
        insertPoop.setInt(1, poopMetrics.getPooCount());
        insertPoop.setFloat(2, poopMetrics.getConsistencyAvg());
        insertPoop.setInt(3, poopMetrics.getFrequencyAvgMillis());
        insertPoop.executeUpdate();
    }

    public int getNextSequenceVal() throws SQLException {
        final Statement getPoops = getJdbcConnection().createStatement();
        final ResultSet resultSet = getPoops.executeQuery(NEXT_VAL_POOP_ID_SEQ);
        if(resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            throw new RuntimeException("Unable to get next sequence number for poops dao");

        }
    }

    private PoopInfo poopRowMapper(final ResultSet resultSet)
             {
        try{
            final PoopInfo poopInfo = new PoopInfo();
            poopInfo.setConsistency(resultSet.getInt(2));
            poopInfo.setCreationDate(new Instant(resultSet.getTimestamp(3)));
            poopInfo.setUserId(resultSet.getInt(1));
            return poopInfo;
        } catch(Exception e){
            System.out.println("something wrong in row mapper");
            throw new RuntimeException(e);
        }
    }

    @Override
    List<String> getSchemaSql() {
        return ImmutableList.of(CREATE_SEQUENCE, CREATE_POOP_METRICS_SQL, CREATE_POOPS_SQL);
    }
}
