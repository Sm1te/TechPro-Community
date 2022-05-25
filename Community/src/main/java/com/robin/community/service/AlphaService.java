package com.robin.community.service;

import com.robin.community.config.Alpha;
import com.robin.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;



    public AlphaService(){
        System.out.println("实例化 alpha service");
    }


    @PostConstruct
    public void init(){
        System.out.println("init alpha service");
    }

    @PreDestroy
    public void destory(){
        System.out.println("销毁 alpha service");
    }

    public String find(){
        return alphaDao.select();
    }
}
