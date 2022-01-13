package com.example.yangjie.lock.factory;


import com.example.yangjie.id.idworker.HostPIdAndTimestampIdGenerator;
import com.example.yangjie.id.idworker.IdGenerator;
import com.example.yangjie.id.idworker.twitter.SnowflakeIdGenerator;
import com.example.yangjie.lock.DLock;
import com.example.yangjie.lock.DLockManager;
import com.example.yangjie.lock.redis.CommandSync;
import com.example.yangjie.lock.redis.RedisDLock;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class RedisDistributedLockFactory extends AbsLockFactory{

    private final Pool<Jedis> jedisPool;
    private final IdGenerator<String> idGenerator ;

    public RedisDistributedLockFactory(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.idGenerator = new HostPIdAndTimestampIdGenerator();
    }

    @Override
    public Lock initLock(String key) {
        return new RedisDLock(new CommandSync(jedisPool),key,idGenerator);
    }

    @Override
    public boolean useCache() {
        return false;
    }

}
