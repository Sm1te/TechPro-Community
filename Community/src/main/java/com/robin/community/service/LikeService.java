package com.robin.community.service;

import com.robin.community.utility.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞
    public void like(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
        if(isMember){
            redisTemplate.opsForSet().remove(likeKey, userId);
        }else{
            redisTemplate.opsForSet().add(likeKey, userId);
        }
    }

    // 查询某个实体的赞数量
    public long getLikeCount(int entityType, int entityId){
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeKey);
    }

    // 查询某人对某个实体的赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
        if(isMember){
            return 1;
        }else{
            return 0;
        }
    }
}
