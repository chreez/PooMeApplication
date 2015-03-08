package com.palme.GroupMeBot.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Preconditions;

import org.joda.time.Instant;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.palme.GroupMeBot.dao.model.UserInfo;

public class UsersDao extends SqliteDao {
    final private static String CREATE_USERS_SQL = "create table IF NOT EXISTS users (user_id integer primary key, login text, last_updated_date timestamp, last_poo_row_id integer);";
    final private static String INSERT_USER_SQL = "INSERT INTO USERS (user_id, login, last_updated_date) VALUES (?, ?, ?);";
    final private static String GET_USERS_SQL = "SELECT * FROM users WHERE user_id in (%s);";
    final private static String GET_ALL_USERS_SQL = "SELECT * FROM users WHERE user_id in (%s);";
    final private static String UPDATE_USERS_SQL = "UPDATE users SET LOGIN = ?,  LAST_UPDATED_DATE = ?, LAST_POO_ROW_ID = ? WHERE USER_ID = ?;";

    public UsersDao(final Connection jdbcConnection) throws SQLException {
        super(jdbcConnection);
    }

    public static void main(final String[] args) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:GroupMeBotDB_331.db");
            final UsersDao users = new UsersDao(c);
            final Map<Integer, Optional<UserInfo>> result = users
                    .getUserInfos(ImmutableList.of(123, 123123));
            System.out.println(result);

            final UserInfo me = result.get(123).orNull();
            me.setLogin("1231123123");
            me.setLastPooRowIndex(1);
            users.updateUser(me);
            Map<Integer, Optional<UserInfo>> result2 = users
                    .getUserInfos(ImmutableList.of(123, 123123));
            System.out.println(result2);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    /**
     * Upsert a user, will not throw exception if user is already in db
     * @param user_id
     * @param login
     * @return
     */
    public Integer createUser(final Integer user_id, final String login) {
        try {
            final PreparedStatement insertUser = getJdbcConnection().prepareStatement(INSERT_USER_SQL);
            insertUser.setLong(1, user_id.longValue());;
            insertUser.setString(2, login);
            insertUser.setTimestamp(3, new Timestamp(Instant.now().getMillis()));
            final int rowId = insertUser.executeUpdate();
            return rowId == 0 ? null : rowId;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserInfo getUserInfo(final Integer userId) throws SQLException {
        return Iterables.getOnlyElement(getUserInfos(ImmutableList.of(userId)).entrySet()).getValue().get();
    }

    /**
     * Safely fetch the user infos for the given ids.
     *
     * @param userIds
     * @return Absent when the user id is not in the db, else user info
     * @throws SQLException
     *             If we cant open connection
     */
    public Map<Integer, Optional<UserInfo>> getUserInfos(final List<Integer> userIds) throws SQLException {
        Preconditions.checkNotNull(userIds);

        final PreparedStatement getUsers = getJdbcConnection()
                .prepareStatement(
                        String.format(GET_USERS_SQL,
                                Joiner.on(",").join(userIds)));

        final ResultSet resultSet = getUsers.executeQuery();

        final ImmutableMap.Builder<Integer, Optional<UserInfo>> userInfos = ImmutableMap.builder();
        final ImmutableList.Builder<Integer> userIdsThatWereFetched = ImmutableList.builder();
        while(resultSet.next()) {
            final UserInfo userInfo = new UserInfo();
            userInfo.setUserId(resultSet.getInt(1));
            userInfo.setLogin(resultSet.getString(2));
            userInfo.setLastUpdatedDate(new Instant(resultSet.getTimestamp(3)));
            userInfo.setLastPooRowIndex(resultSet.getInt(4) == 0 ? null: resultSet.getInt(4));

            userInfos.put(userInfo.getUserId(), Optional.of(userInfo));
            userIdsThatWereFetched.add(userInfo.getUserId());
        }

        // Scan for users that were not successfully fetched in db
        final List<Integer> successfullyFetchedUserIds = userIdsThatWereFetched.build();
        for(Integer id : userIds) {
            if(!successfullyFetchedUserIds.contains(id)) {
                userInfos.put(id, Optional.<UserInfo>absent());
            }
        }

        return userInfos.build();
    }

    public List<UserInfo> getAllUsers() throws SQLException {
        final PreparedStatement getUsers = getJdbcConnection().prepareStatement(GET_ALL_USERS_SQL);

        final ResultSet resultSet = getUsers.executeQuery();

        final ImmutableList.Builder<UserInfo> userInfos = ImmutableList.builder();
        while(resultSet.next()) {
            final UserInfo userInfo = new UserInfo();
            userInfo.setUserId(resultSet.getInt(1));
            userInfo.setLogin(resultSet.getString(2));
            userInfo.setLastUpdatedDate(new Instant(resultSet.getTimestamp(3)));
            userInfo.setLastPooRowIndex(resultSet.getInt(4) == 0 ? null: resultSet.getInt(4));

            userInfos.add(userInfo);
        }

        return userInfos.build();
    }

//    final public static String UPDATE_USERS_SQL = "UPDATE USERS SET LOGIN = ?,  LAST_UPDATED_DATE = ?, LAST_POO_ROW_ID = ? WHERE USER_ID = ?";
    public void updateUser(final UserInfo userInfo) throws SQLException {
        Preconditions.checkNotNull(userInfo);
        Preconditions.checkNotNull(userInfo.getUserId());
        Preconditions.checkNotNull(userInfo.getLogin());

        final PreparedStatement insertUser = getJdbcConnection().prepareStatement(
                String.format(UPDATE_USERS_SQL, userInfo.getUserId()));

        insertUser.setString(1, userInfo.getLogin());
        insertUser.setTimestamp(2, new Timestamp(Instant.now().getMillis()));
        if(userInfo.getLastPooRowIndex() == null) {
            insertUser.setNull(3, Types.INTEGER);
        } else {
            insertUser.setInt(3,  userInfo.getLastPooRowIndex() );
        }
        insertUser.setInt(4, userInfo.getUserId());
        insertUser.executeUpdate();
        //        return rowId == 0 ? null : rowId;
    }



    @Override
    List<String> getSchemaSql() {
        return ImmutableList.of(CREATE_USERS_SQL);
    }
}
