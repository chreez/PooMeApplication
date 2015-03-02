package com.palme.GroupMeBot.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.Instant;

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
    static private final String CREATE_POOPS_SQL = "create table IF NOT EXISTS poops (user_id integer, consistency integer, creation_date timestamp);";
    static private final String CREATE_POOP_METRICS_SQL = "create table IF NOT EXISTS poop_metrics ( user_id integer primary key, poo_count integer, consistency_avg real, frequency_avg real);";

    static private final String INSERT_POOP_METRICS_SQL = "INSERT INTO POOP_METRICS (user_id, poo_count, consistency_avg, frequency_avg) VALUES (?,?,?,?);";
    static private final String GET_POOP_METRICS_SQL = "SELECT user_id, poo_count, consistency_avg, frequency_avg FROM POOP_METRICS WHERE USER_ID = %s;";
    static private final String UPDATE_POOP_METRICS = "UPDATE POOP_METRICS SET poo_count = ? , consistency_avg = ? , frequency_avg = ? WHERE USER_ID = %s;";

    static private final String INSERT_POOP_SQL = "INSERT INTO poops (user_id, consistency, creation_date) VALUES (?, ?, ?);";
    static private final String GET_POOP_SQL = "SELECT consistency, creation_date FROM POOPS WHERE rowid = %s;";
    static private final String GET_POOPS_SQL = "SELECT * FROM POOPS WHERE user_id = %s;";

    public static void main(final String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        final Connection c = DriverManager.getConnection("jdbc:sqlite:GroupMeBotDB_331.db");
        final PoopDao poopDao = new PoopDao(c);
        poopDao.initPoopMetricsTable(12322);
    }

    public PoopDao(final Connection jdbcConnection) throws SQLException {
        super(jdbcConnection);
    }

    public Integer createPoop(final PoopInfo newPoop, final UserInfo userInfo) throws SQLException {
        final PoopInfo oldPoopInfo;
        if(userInfo.getLastPooRowIndex() != null) {
            final ResultSet result = getJdbcConnection().createStatement().executeQuery(String.format(GET_POOP_SQL, userInfo.getLastPooRowIndex()));
            if(result.next()) {
                oldPoopInfo = new PoopInfo();
                oldPoopInfo.setConsistency(result.getInt(1));
                oldPoopInfo.setCreationDate(new Instant(result.getDate(1)));
                oldPoopInfo.setUserId(userInfo.getUserId());
            } else {
                oldPoopInfo = null;
                initPoopMetricsTable(userInfo.getUserId());
            }
        } else {
            oldPoopInfo = null;
            initPoopMetricsTable(userInfo.getUserId());
        }

        final PoopMetrics metrics = getPoopMetrics(userInfo.getUserId());

        final PreparedStatement insertPoop = getJdbcConnection().prepareStatement(INSERT_POOP_SQL);
        insertPoop.setInt(1, userInfo.getUserId());
        insertPoop.setInt(2, newPoop.getConsistency());
        insertPoop.setTimestamp(3, new Timestamp(newPoop.getCreationDate().getMillis()));
        final Integer rowId = insertPoop.executeUpdate();

        final PoopMetrics newMetrics = metrics.updateMetrics(oldPoopInfo, newPoop);
        updatePoopMetrics(newMetrics);

        return rowId;
    }
    public SortedSet<PoopInfo> getAllPoopsForUser(final Integer userId) throws SQLException {
        final Statement getPoops = getJdbcConnection().createStatement();
        final ResultSet resultSet = getPoops.executeQuery(String.format(GET_POOPS_SQL, userId));

        final ImmutableSortedSet.Builder<PoopInfo> poops = ImmutableSortedSet.naturalOrder();
        while(resultSet.next()) {
            final PoopInfo poopInfo = new PoopInfo();
            poopInfo.setConsistency(resultSet.getInt(2));
            poopInfo.setCreationDate(new Instant(resultSet.getTimestamp(3)));
            poopInfo.setUserId(resultSet.getInt(1));
            poops.add(poopInfo);
        }

        return poops.build();
    }

    public PoopMetrics getPoopMetrics(final Integer userId) throws SQLException {
        final ResultSet resultSet = getJdbcConnection().createStatement().executeQuery(String.format(GET_POOP_METRICS_SQL, userId));
        final PoopMetrics result = new PoopMetrics();
        if(resultSet.next()){
            result.setUserId(userId);
            result.setPooCount(resultSet.getInt(2));
            result.setConsistencyAvg(resultSet.getFloat(3));
            result.setFrequencyAvgMillis(resultSet.getInt(4));
        }
        return result;
    }

    public void initPoopMetricsTable(final Integer userId) {
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

    @Override
    List<String> getSchemaSql() {
        return ImmutableList.of(CREATE_POOP_METRICS_SQL, CREATE_POOPS_SQL);
    }
}
