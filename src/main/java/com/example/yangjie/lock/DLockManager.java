package com.example.yangjie.lock;

/**
 * 分布式锁资源管理器（DLM）接口
 * @author wenqi.wu
 */
public interface DLockManager {
    /**
     * 获取指定命名的锁实例
     * @param name  锁实例
     * @return  DLock
     */
    public DLock getDLock(String name);

    /**
     * 在容器关闭的时候，主动释放资源
     */
    public void destroy();
}
