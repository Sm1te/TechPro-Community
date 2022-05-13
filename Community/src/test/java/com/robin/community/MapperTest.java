package com.robin.community;

import com.robin.community.dao.DiscussPostMapper;
import com.robin.community.dao.LoginTicketMapper;
import com.robin.community.dao.UserMapper;
import com.robin.community.entity.DiscussPost;
import com.robin.community.entity.LoginTicket;
import com.robin.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser(){
        User user1 = userMapper.selectById(101);
        User user2 = userMapper.selectByEmail("nowcoder101@sina.com");
        User user3 = userMapper.selectByName("liubei");

        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setPassword("231312313");
        user.setUsername("robin");
        user.setSalt("abss");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(user);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost post : discussPosts){
            System.out.println(post);
        }

        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println("i = " + i);
    }

    @Test
    public void testInsertLoginMapper(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("robin");
        loginTicket.setUeserId(110);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("robin");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("robin", 1);
        loginTicket = loginTicketMapper.selectByTicket("robin");
        System.out.println("loginTicket = " + loginTicket);
    }
}
