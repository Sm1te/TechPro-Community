package com.robin.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHiber")
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
