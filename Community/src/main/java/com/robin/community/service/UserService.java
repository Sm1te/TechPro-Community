package com.robin.community.service;

import com.robin.community.dao.LoginTicketMapper;
import com.robin.community.dao.UserMapper;
import com.robin.community.entity.LoginTicket;
import com.robin.community.entity.User;
import com.robin.community.utility.CommunityConstant;
import com.robin.community.utility.CommunityUtil;
import com.robin.community.utility.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;


    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(user == null){
            throw new IllegalArgumentException("No null argument");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "Null username!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "Null email!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "Null password!");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // Register
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // Send email
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, long expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        // Null values
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "Please enter your username!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "Please enter your password!");
            return map;
        }

        // Validate account
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "Please enter your valid username!");
            return map;
        }

        if(user.getStatus() == 0){
            map.put("usernameMsg", "Please activate your account first!");
            return map;
        }

        //Validate password
//        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "Wrong password!");
            return map;
        }

        // Generate login ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }

    public Map<String, Object> updatePassword(String password, String newPassword, int id){
        Map<String, Object> map = new HashMap<>();
        User user = userMapper.selectById(id);
        //password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "Wrong Password");
            return map;
        }else{
            //注意：存入新密码要以加密后的形式存进去
            //newPassword = CommunityUtil.md5(newPassword + user.getSalt());
            userMapper.updateHeader(id, newPassword);
        }
        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

}
