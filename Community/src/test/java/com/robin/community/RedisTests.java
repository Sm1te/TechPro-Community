package com.robin.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        redisTemplate.opsForValue().set("test:count", 1);

        System.out.println(redisTemplate.opsForValue().get("test:count"));
        System.out.println(redisTemplate.opsForValue().increment("test:count"));
        System.out.println(redisTemplate.opsForValue().decrement("test:count"));
    }


    @Test
    public void testHash() {
        redisTemplate.opsForHash().put("test:hash", "name", "robin");
        redisTemplate.opsForHash().put("test:hash", "age", "18");
        redisTemplate.opsForHash().put("test:hash", "address", "beijing");

        System.out.println(redisTemplate.opsForHash().get("test:hash", "name"));
        System.out.println(redisTemplate.opsForHash().get("test:hash", "age"));
        System.out.println(redisTemplate.opsForHash().get("test:hash", "address"));
    }

    @Test
    public void testList(){
        String redisKey = "test:list";

        redisTemplate.opsForList().leftPush(redisKey, "robin");
        redisTemplate.opsForList().leftPush(redisKey, "18");
        redisTemplate.opsForList().leftPush(redisKey, "beijing");

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().index(redisKey, 1));
        System.out.println(redisTemplate.opsForList().index(redisKey, 2));

        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        redisTemplate.opsForList().leftPop(redisKey);
        System.out.println(redisTemplate.opsForList().size(redisKey));
    }

    @Test
    public void testSet(){
        String redisKey = "test:set";

        redisTemplate.opsForSet().add(redisKey, "robin");
        redisTemplate.opsForSet().add(redisKey, "18");
        redisTemplate.opsForSet().add(redisKey, "beijing");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSet(){
        String redisKey = "test:sortedSet";

        redisTemplate.opsForZSet().add(redisKey, "robin", 1);
        redisTemplate.opsForZSet().add(redisKey, "18", 2);
        redisTemplate.opsForZSet().add(redisKey, "beijing", 3);

        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForZSet().rangeByScore(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "robin"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "robin"));
        System.out.println(redisTemplate.opsForZSet().remove(redisKey, "robin"));
        System.out.println(redisTemplate.opsForZSet().size(redisKey));

    }

    // ?????????????????????key???????????????RedisTemplate???watch????????????????????????????????????key??????????????????????????????????????????????????????????????????key???????????????????????????????????????
    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        System.out.println(operations.get());

    }

    // ???????????????
    @Test
    public void testTransactional(){
        Object  obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi();// ????????????

                operations.opsForSet().add(redisKey, "robin");
                operations.opsForSet().add(redisKey, "18");
                operations.opsForSet().add(redisKey, "beijing");

                System.out.println(operations.opsForSet().members(redisKey));// ???????????????????????????????????????????????????

                return operations.exec();// ????????????
            }
        });
        System.out.println(obj);
    }

}
