package com.example.yangjie.lock.zk;

import com.example.yangjie.lock.DLock;
import com.example.yangjie.lock.DLockManager;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 对外提供ZkDLock的实例
 * @author wenqi.wu
 */
public class ZkDLockManager implements DLockManager {
    private static final Logger logger = LoggerFactory.getLogger(ZkDLockManager.class);
    private static final int SESSION_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final long DELAY_TIME_IN_MILIS = 1000;
    private static final String ROOT = "/zy/dlock/";
    private final CuratorFramework client;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);//删除不使用的节点

    //构造器
    public ZkDLockManager(String zkAddress){
        Preconditions.checkArgument(StringUtils.isNotBlank(zkAddress));
        CuratorFrameworkFactory.Builder builder  = CuratorFrameworkFactory.builder()
                .connectString(zkAddress).retryPolicy(new RetryNTimes(60, 1000))
                .connectionTimeoutMs(CONNECTION_TIMEOUT).sessionTimeoutMs(SESSION_TIMEOUT);
        client = builder.build();
        client.start();
        try {
            boolean connected = client.getZookeeperClient().blockUntilConnectedOrTimedOut();
            if(connected){
                logger.info("the zk client connected ok");
                return;
            }
        } catch (InterruptedException e) {
            logger.error("start zk client error, zk address:[{}]",zkAddress,e);
        }
        throw new IllegalStateException("error start zk DLock manager");
    }

    @Override
    public DLock getDLock(String name) {
        //TODO 生成的节点路径过多ls /zy/dlock/order_123
        return new ZkDLock(client, ZKPaths.makePath(ROOT,name),this);
    }

    /**
     * 在锁释放后提交定时任务删除创建的节点
     * @param path  path
     */
    protected void addCleanLockPath(final String path){
        Preconditions.checkArgument(StringUtils.isNotBlank(path));
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                cleanLockPath(path);
            }
        },DELAY_TIME_IN_MILIS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        if(client != null){
            client.close();
        }
        executorService.shutdown();
    }

    /**
     * 在锁释放后删除对应的锁节点
     */
    private void cleanLockPath(String path){
        try {
            List list = client.getChildren().forPath(path);
            if (list == null || list.isEmpty()) {
                client.delete().forPath(path);
            }
        } catch (KeeperException.NoNodeException e1) {
            //nothing
        } catch (KeeperException.NotEmptyException e2) {
            //nothing
        } catch (Exception e) {
            logger.error(e.getMessage(), e);//准备删除时,正好有线程创建锁
        }
    }
}
