package com.palme.GroupMeBot.handler;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.palme.GroupMeBot.dao.AchievementsDao;
import com.palme.GroupMeBot.dao.PoopDao;
import com.palme.GroupMeBot.dao.UsersDao;
import com.palme.GroupMeBot.dao.model.PoopInfo;
import com.palme.GroupMeBot.dao.model.PoopMetrics;
import com.palme.GroupMeBot.dao.model.UserInfo;
import com.palme.GroupMeBot.groupme.client.UserDetails;

public class PoopHandler {
    private static final String STATUS_FORMAT = "[%s] has pooped %d times. I wish I could shit that much!";
    private final PoopDao poopDao;
    private final AchievementsDao achievementsDao;
    private final UsersDao usersDao;

    public PoopHandler(final PoopDao poopDao,
            final AchievementsDao achievementsDao, final UsersDao usersDao) {
        super();
        this.poopDao = poopDao;
        this.achievementsDao = achievementsDao;
        this.usersDao = usersDao;
    }

    public Optional<String> handleThePoop(final UserDetails userDetails, final PoopInfo newPoop) throws NumberFormatException, SQLException {
        final UserInfo userInfo;
        final Optional<UserInfo> optionalUserInfo = Iterables.getOnlyElement(
                usersDao.getUserInfos(
                        ImmutableList.of(Integer.valueOf(userDetails.getId()))).values());
        if(optionalUserInfo.isPresent()) {
            userInfo = optionalUserInfo.get();
        } else {
            usersDao.createUser(Integer.valueOf(userDetails.getId()), userDetails.getName());
            userInfo = Iterables.getOnlyElement(usersDao.getUserInfos(ImmutableList.of(Integer.valueOf(userDetails.getId()))).values()).get();
        }

        final int lastPoopRowId = poopDao.createPoop(newPoop, userInfo);
        userInfo.setLastPooRowIndex(lastPoopRowId);
        usersDao.updateUser(userInfo);

        final String result = achievementsDao.getAchievementForPoopMetrics(newPoop, poopDao.getPoopMetrics(userInfo.getUserId()));
        if(result == null) {
            return Optional.absent();
        } else {
            return Optional.of(result);
        }
    }

    public Optional<String> getUserStatus(final UserDetails userDetails) throws SQLException {
            final UserInfo userInfo;
            try {
                userInfo = Iterables.getOnlyElement(usersDao.getUserInfos(ImmutableList.of(Integer.valueOf(userDetails.getId()))).values()).get();
                System.out.println(userInfo);
            } catch(Exception e) {
                usersDao.createUser(Integer.valueOf(userDetails.getId()), userDetails.getName());
                return Optional.absent();
            }
            usersDao.updateUser(userInfo);
            final PoopMetrics metrics = poopDao.getPoopMetrics(userInfo.getUserId());
            System.out.println("metrics fetched" + metrics);
            final String message = String.format(STATUS_FORMAT, userInfo.getLogin(), metrics.getPooCount());
            System.out.println(message  + "message?!?!?!" );
            return Optional.of(message );
    }

    public String getLeaderBoard() throws SQLException {
        final List<PoopMetrics> allPoopMetrics = ImmutableList.copyOf(this.poopDao.getAllPoopMetrics());
        int i = 0;

        final StringBuilder builder = new StringBuilder();
        builder.append("Champions of the Poop\n----------------------------\nName-Count-Consistency\n");
        for(final PoopMetrics poopMetrics : allPoopMetrics) {
            if(i<3) {
                builder.append(String.format("%s-%d-%.02f%n", usersDao.getUserInfo(poopMetrics.getUserId()).getLogin(), poopMetrics.getPooCount(), poopMetrics.getConsistencyAvg()));
            }
            i++;
        }
        return builder.toString();
    }
}