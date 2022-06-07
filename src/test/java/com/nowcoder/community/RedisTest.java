package com.nowcoder.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * 测试对redis数据的访问方式
 * 下面测试五种redis数据类型 分别是：String hash 列表 set 有序集合
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;


    //String
    @Test
    public void testStrings(){
        String redisKey="test:count";

        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    //列表
    @Test
    public void testLists(){
        String redisKey="test:ids";

        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));


        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));



    }
    //hash
    @Test
    public void testHashes(){
        String redisKey="test:user";

        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    //集合
    @Test
    public void testSets(){
        String redisKey="test:teachers";

        redisTemplate.opsForSet().add(redisKey,"刘备","关羽","张飞","赵云","诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));

    }
    //有序集合
    @Test
    public void testSortedSets(){
        String redisKey="test:students";

        redisTemplate.opsForZSet().add(redisKey,"唐僧",80);
        redisTemplate.opsForZSet().add(redisKey,"悟净",70);
        redisTemplate.opsForZSet().add(redisKey,"悟能",80);
        redisTemplate.opsForZSet().add(redisKey,"悟空",85);
        redisTemplate.opsForZSet().add(redisKey,"白龙马",70);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"悟能"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"悟能"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2));

    }
    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
    }
    //多次访问同一个key
    @Test
    public void testBoundOperations(){
        String redisKey="test:count";
        BoundValueOperations operations=redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    //编程式事务
    @Test
    public void testTransactional(){
        Object oj=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey="test:ts";
                operations.multi();
                operations.opsForSet().add(redisKey,"zhangsan");
                operations.opsForSet().add(redisKey,"lisi");
                operations.opsForSet().add(redisKey,"wangwu");
                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();

            }
        });
        System.out.println(oj);
    }

    /**
     * Redis高级数据类型
     * HyperLogLog
     * 采用一种技术算法，用于完成独立总数的统计
     * 占据空间小，无论统计多少个数据，只占12k的内存空间
     * 不精确的统计算法，标准误差为0.81
     *
     * Bitmap
     * 不是一种独立的数据结构，实际上就是字符串
     * 支持按位存取数据，可以将其看成是byte数组
     * 适合存储大量的连续的数据布尔值
     *
     */
    /**
     * UV(Unique Visitor)
     * 独立访客，需要通过用户IP排重统计数据
     * 每次访问都要进行统计
     * HyperLogLog性能好，且存储空间小
     *
     * DAU（Daily Active User）
     * 日活跃用户，需要通过用户ID排重统计数据
     * 访问一次，则认为其为活跃
     * Bitmap,性能好，且可以统计精确的结果
     */

    //统计20万个重复数据的独立总数

    @Test
    public void testHyperLogLog(){
        String redisKey="test:hll:01";

        for(int i=1;i<=100000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for(int i=1;i<=100000;i++){
            int r=(int)(Math.random()*100000+1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        long size=redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    //将3组数据合并，再统计合并后的重复数据的独立总数
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2="test:hll:02";
        for(int i=1;i<=10000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3="test:hll:03";
        for(int i=5001; i<=15000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4="test:hll:04";
        for(int i=10001;i<=20000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }

        String unionKey="test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);

        long size=redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }

    //统计一组数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey="test:bm:01";

        //记录
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);

        //查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));

        //统计
        Object obj=redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

    }

    //统计3组数据的布尔值，并对这三组数据做or运算
    @Test
    public void testBitMapOperation(){
        String redisKey2="test:bm:02";

        redisTemplate.opsForValue().setBit(redisKey2,0,true);
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);

        String redisKey3="test:bm:03";

        redisTemplate.opsForValue().setBit(redisKey2,2,true);
        redisTemplate.opsForValue().setBit(redisKey2,3,true);
        redisTemplate.opsForValue().setBit(redisKey2,4,true);

        String redisKey4="test:bm:04";

        redisTemplate.opsForValue().setBit(redisKey2,4,true);
        redisTemplate.opsForValue().setBit(redisKey2,5,true);
        redisTemplate.opsForValue().setBit(redisKey2,6,true);

        String redisKey="test:bm:or";
        Object obj=redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),redisKey2.getBytes(),redisKey3.getBytes(),redisKey4.getBytes());
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));

    }


}
