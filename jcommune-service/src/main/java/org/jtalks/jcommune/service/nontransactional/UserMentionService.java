/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.service.nontransactional;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;


/**
 * This service provides send email notifications to all mentioned users
 * in some components of forum:topics, posts.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class UserMentionService {
    private static final Pattern ALL_USERS_BB_CODE_PATTERN = 
            Pattern.compile("\\[user\\].*?\\[/user\\]|\\[user notified=true\\].*?\\[/user\\]");
    private static final Pattern NOT_NOTIFIED_USERS_BB_CODE_PATTERN = 
            Pattern.compile("\\[user\\].*?\\[/user\\]");
    private static final String MENTIONED_NOT_NOTIFIED_USER_BB_CODE_TEMPLATE = "[user]%s[/user]";
    private static final String MENTIONED_NOTIFIED_USER_BB_CODE_TEMPLATE = "[user notified=true]%s[/user]";
    private MailService mailService;
    private UserDao userDao;
    private PostDao postDao;
    
    /**
     * @param mailService to send email notifications
     * @param userDao to find mentioned user
     * @param postDao to save post after some changes
     */
    public UserMentionService(MailService mailService, UserDao userDao, PostDao postDao) {
        this.mailService = mailService;
        this.userDao = userDao;
        this.postDao = postDao;
    }
    
    /**
     * Extract all usernames that were mentioned in passed text.
     * 
     * @return extracted user names
     */
    public List<String> extractAllMentionedUsers(String canContainMentionedUsers) {
        if (!StringUtils.isEmpty(canContainMentionedUsers)) {
            Matcher matcher = ALL_USERS_BB_CODE_PATTERN.matcher(canContainMentionedUsers);
            List<String> mentionedUsernames = new ArrayList<String>();
            while (matcher.find()) {
                String userBBCode = matcher.group();
                String mentionedUser = userBBCode.replaceAll("\\[.*?\\]", StringUtils.EMPTY);
                mentionedUsernames.add(mentionedUser);
            }
            return mentionedUsernames;
        } 
        return Collections.emptyList();
    }

    /**
     * Extract all not mentioned users and notify them.
     * 
     * @param post where user was mentioned
     */
    public void notifyNotMentionedUsers(Post mentioningPost) {
        String postContent = mentioningPost.getPostContent();
        List<String> mentionedUsersNames = extractNotNotifiedMentionedUsers(postContent);
        if (!CollectionUtils.isEmpty(mentionedUsersNames)) {
            sendNotificationToMentionedUsers(mentionedUsersNames, mentioningPost);
        }
    }
    
    /**
     * Extract all not notified usernames that were mentioned in passed text.
     * 
     * @return extracted not notified usernames
     */
    private List<String> extractNotNotifiedMentionedUsers(String canContainMentionedUsers) {
        return extractMentionedUsers(canContainMentionedUsers, NOT_NOTIFIED_USERS_BB_CODE_PATTERN);
    }
    
    /**
     * Extract all usernames that were mentioned in passed text by given pattern.
     * 
     * @return extracted usernames
     */
    private List<String> extractMentionedUsers(String canContainMentionedUsers, Pattern mentionedUserPattern) {
        if (!StringUtils.isEmpty(canContainMentionedUsers)) {
            Matcher matcher = mentionedUserPattern.matcher(canContainMentionedUsers);
            List<String> mentionedUsernames = new ArrayList<String>();
            while (matcher.find()) {
                String userBBCode = matcher.group();
                String mentionedUser = userBBCode.replaceAll("\\[.*?\\]", StringUtils.EMPTY);
                mentionedUsernames.add(mentionedUser);
            }
            return mentionedUsernames;
        } 
        return Collections.emptyList();
    }
    
    /**
     * Send notification for all mentioned users.
     * 
     * @param mentionedUsernames the list of names of mentioned users
     * @param mentioningPost post where users where mentioned
     */
    private void sendNotificationToMentionedUsers(List<String> mentionedUsernames, Post mentioningPost) {
        List<JCUser> mentionedUsers = userDao.getByUsernames(mentionedUsernames);
        for (JCUser mentionedUser: mentionedUsers) {
            boolean isNotificationAlreadySent = mentioningPost.getTopicSubscribers().contains(mentionedUser);
            if (!isNotificationAlreadySent && mentionedUser.isMentioningNotificationsEnabled()) {
                String username = mentionedUser.getUsername();
                String initialUserMentioning = format(MENTIONED_NOT_NOTIFIED_USER_BB_CODE_TEMPLATE, username);
                String notifiedUserMentioing = format(MENTIONED_NOTIFIED_USER_BB_CODE_TEMPLATE, username);
                mailService.sendUserMentionedNotification(mentionedUser, mentioningPost.getId());
                String newPostContent = mentioningPost.getPostContent().replace(initialUserMentioning, notifiedUserMentioing);
                mentioningPost.setPostContent(newPostContent);
                postDao.update(mentioningPost);
            }
        }
    }
    
    
}
