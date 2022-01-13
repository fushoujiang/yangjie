package com.example.yangjie.id.idworker;

/**
 * 分布式ID生成器接口
 * @author wenqi.wu
 */
public interface IdGenerator<T> {

    /**
     * 初始化
     */
    public void init();

    /**
     * 获取下一次生成ID
     * @return  generateNextId
     */
    public T generateNextId();

    /**
     * 资源销毁
     */
    public void destroy();
}
