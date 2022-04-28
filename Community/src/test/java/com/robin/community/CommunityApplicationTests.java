package com.robin.community;


import com.robin.community.config.Alpha;
import com.robin.community.dao.AlphaDao;
import com.robin.community.dao.AlphaDaoMyBatisImpl;
import com.robin.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Test
    void contextLoads() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println("alphaDao.select() = " + alphaDao.select());

        AlphaDao alphaDao1 = applicationContext.getBean("alphaHiber", AlphaDao.class);
        System.out.println("alphaDao1 = " + alphaDao1);
    }

    @Test
    public void testBeanManagement(){
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);

        AlphaService alphaService1 = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService1);
    }

    @Test
    public void testBeanConfig(){
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    @Autowired
    @Qualifier("alphaHiber")
    private AlphaDao alphadao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    AlphaDaoMyBatisImpl alphaDaoMyBatis;

    @Test
    public void testDI(){
        System.out.println(alphadao);
        System.out.println("alphaDaoMyBatis = " + alphaDaoMyBatis);
        System.out.println("alphaService = " + alphaService);
    }
    /*
    com.robin.community.dao.AlphaDaoHibernateImpl@61f377d1
    alphaDaoMyBatis = com.robin.community.dao.AlphaDaoMyBatisImpl@6c538eb2
    alphaService = com.robin.community.service.AlphaService@738a5848
    */
}
