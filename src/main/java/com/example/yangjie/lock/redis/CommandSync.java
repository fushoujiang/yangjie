package com.example.yangjie.lock.redis;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基于Redis的AQS的实现类
 * @author wenqi.wu
 */
public class CommandSync {
    private static final Logger logger = LoggerFactory.getLogger(CommandSync.class);
    private final Pool<Jedis> jedisPool  ;

    public CommandSync(Pool<Jedis> jedisPool) {
        Preconditions.checkArgument(jedisPool != null );
        this.jedisPool = jedisPool;
    }

    /**
     * 向redis server获取加锁许可
     * @param key   要加锁的key
     * @param lockRandValue 加锁的随机值
     * @param millisToLeaseTime 锁持有有效期
     * @return  boolean
     */
    boolean tryAcquire(String key,String lockRandValue,long millisToLeaseTime){
        Jedis jedis = null;
        try{
            jedis = getResource();
            String luaScript = ""
                    + "\nlocal r = tonumber(redis.call('SETNX', KEYS[1],ARGV[1]));"
                    + "\nredis.call('PEXPIRE',KEYS[1],ARGV[2]);"
                    + "\nreturn r";
            List<String> args = new ArrayList<String>();
            args.add(lockRandValue);
            args.add(String.valueOf(millisToLeaseTime));
            Long ret = (Long) jedis.eval(luaScript, Collections.singletonList(key), args);
            return ret == 1;
        }catch (Exception e){
            logger.error("try acquire by redis occurs error",e);
        } finally {
            if(jedis!=null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 向redis server释放加锁许可
     * @param key 要加锁的key
     * @param lockRandValue   加锁的随机值
     * @return  boolean
     */
    boolean tryRelease(String key,String lockRandValue) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            String luaScript = ""
                    + "\nlocal v = redis.call('GET', KEYS[1]);"
                    + "\nlocal r= 0;"
                    + "\nif v == ARGV[1] then"
                    + "\nr =redis.call('DEL',KEYS[1]);"
                    + "\nend"
                    + "\nreturn r";
            List<String> args = new ArrayList<String>();
            args.add(lockRandValue);
            Long ret = (Long) jedis.eval(luaScript, Collections.singletonList(key), args);
            return ret > 0;
        } catch (Exception e) {
            logger.error("try release by redis occurs error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 获取jedis对象
     * @return  Jedis
     */
    private Jedis getResource(){
        Preconditions.checkArgument(jedisPool != null );
        return jedisPool.getResource();
    }
}
