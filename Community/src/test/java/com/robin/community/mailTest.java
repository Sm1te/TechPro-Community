package com.robin.community;

import com.robin.community.utility.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class mailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "robin");

        String process = templateEngine.process("/mail/email_demo", context);
        System.out.println(process);

        mailClient.sendMail("liyijiannice@outlook.com", "TestHtml", process);
    }
    @Test
    public void Test(){
        mailClient.sendMail("liyijiannice@outlook.com", "Test", "Welcome.");
    }

}
