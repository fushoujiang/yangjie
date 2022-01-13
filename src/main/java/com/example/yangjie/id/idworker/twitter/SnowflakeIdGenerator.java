package com.example.yangjie.id.idworker.twitter;


import com.example.yangjie.id.idworker.IdGenerateException;
import com.example.yangjie.id.idworker.IdGenerator;
import com.example.yangjie.util.CloseUtils;
import com.example.yangjie.util.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 本算法来自于twitter的snowflake的IdWorker
 * @author wenqi.wu
 */
public class SnowflakeIdGenerator implements IdGenerator<Long> {
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeIdGenerator.class);
    private static final String ZK_ROOT = "/snowflake";
    private static final String LOCAL_WORKER_ID_FN = "my_worker_id";
    private static final int BLOCK_CONNECTED_UNTIL_TIMEOUT = 5;//最长阻塞5分钟
    private static final int NOT_FOUND_MYID = -1;
    private static final int MIN_DATA_CENTER_ID = 0;
    private static final int MAX_DATA_CENTER_ID = 31;
    private static final int [] WORKER_IDS = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,30,31};
    private final long dataCenterId;//数据中心ID，统一由架构来分配
    private volatile IdWorker idWorker;//workerId
    private CuratorFramework client;
    /**
     * 构造器
     * @param zkAddress    zookeeper的集群地址
     * @param dataCenterId  dataCenterId数据中心Id
     */
    public SnowflakeIdGenerator(String zkAddress, long dataCenterId){
        if(StringUtils.isBlank(zkAddress) || !zkAddress.contains(":")){
            throw new IllegalArgumentException("非法的zookeeper集群地址，格式应为ip:port");
        }
        if(dataCenterId <MIN_DATA_CENTER_ID || dataCenterId > MAX_DATA_CENTER_ID){
            throw new IllegalArgumentException("非法的dataCenterId数据中心ID，数值应在0~31的闭区间里");
        }
        this.client = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000).build();
        this.dataCenterId = dataCenterId;
    }

    /**
     * 构造器
     * @param dataCenterId  dataCenterId数据中心Id
     */
    public SnowflakeIdGenerator( long dataCenterId){
        if(dataCenterId <MIN_DATA_CENTER_ID || dataCenterId > MAX_DATA_CENTER_ID){
            throw new IllegalArgumentException("非法的dataCenterId数据中心ID，数值应在0~31的闭区间里");
        }
        this.dataCenterId = dataCenterId;
    }


    /**
     * 启动生成器
     */
    @Override
    public void init(){
        try {
            //启动zookeeper
            startZkClient();
            //确认数据中心已注册
            ensureDataCenterIdPathOfZk();
            //获取myWorkerId
            int workerId = readWorkerIdFromLocal();
            if(!verifyWorkerIdIsValid(workerId)){
                workerId = registerWorkerIdOfZk();
                //同步到本地文件
                saveWorkerId2Local(workerId);//考虑异步
            }
            idWorker = new IdWorker(workerId,dataCenterId);
            logger.info("the current dataCenterId is:[{}] and workerId is:[{}],IDGenerator init ok",dataCenterId,workerId);
        } catch (Exception e) {
            logger.error("init twitter id generator occur error",e);
            IdGenerateException ex = new IdGenerateException("init twitter id generator occur error",e);
            ex.setSource("twitter");
            throw ex;
        }
    }

    /**
     * 启动zookeeper
     */
    private void startZkClient() throws Exception {
        if (Objects.isNull(client)){
            logger.info(" zk is null no start");
            return;
        }
        final CountDownLatch latch = new CountDownLatch(1);
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if(connectionState == ConnectionState.CONNECTED){
                    latch.countDown();
                }
                if(connectionState == ConnectionState.RECONNECTED){
                    //TODO 重连检查workerId
                }
            }
        });
        client.start();
        try {
            if(!latch.await(BLOCK_CONNECTED_UNTIL_TIMEOUT, TimeUnit.MINUTES)){
                throw new IllegalStateException("connect to zk cluster server block until time out");
            }
        } catch (InterruptedException e) {
            logger.error("start zk client connect error", e);
            throw e;
        }
    }

    /**
     * 检查数据中心在zk上注册的路径
     */
    private void ensureDataCenterIdPathOfZk() throws Exception {
        if (Objects.isNull(client)){
            logger.info("the data center id path of zk is null");
            return;
        }
        String dataCenterIdPath = ZKPaths.makePath(ZK_ROOT,dataCenterId+"");
        logger.info("the data center id path of zk is {}",dataCenterIdPath);
        EnsurePath ensurePath = new EnsurePath(dataCenterIdPath);
        try {
            ensurePath.ensure(client.getZookeeperClient());
        } catch (Exception e) {
            logger.error("ensure data center id path occur error");
            throw e;
        }
    }

    @Override
    public Long generateNextId() {
        assert idWorker != null;
        return idWorker.nextId();
    }

    /**
     * 从客户端本地文件
     * @return  int
     */
    private int readWorkerIdFromLocal(){
        URL fileResource = Thread.currentThread().getContextClassLoader().getResource(LOCAL_WORKER_ID_FN);
        if(fileResource == null){
            logger.info("cannot found the local file");
            return NOT_FOUND_MYID;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileResource.getPath()),"utf-8"));
            String myIdString = reader.readLine();
            return Integer.parseInt(myIdString);
        } catch (Exception e) {
            logger.warn("when parsed my worker id file content occur error");
            return NOT_FOUND_MYID;
        }finally {
            CloseUtils.close(reader);
        }
    }

    /**
     * 验证workerId是否合法
     * @param workerId  workerId
     * @return  boolean
     */
    private boolean verifyWorkerIdIsValid(int workerId){
        if(workerId  == NOT_FOUND_MYID){
            logger.info("workerId is not be installed");
            return false;
        }
        boolean contained = false;
        for(int w:WORKER_IDS){
            if(w == workerId){
                contained = true;
                break;
            }
        }
        if(!contained){
            logger.info("workerId is not in range [{}]",WORKER_IDS);
            return false;
        }
        String workerIdPath = ZKPaths.makePath(ZKPaths.makePath(ZK_ROOT,dataCenterId+""),workerId+"");
        return StringUtils.equalsIgnoreCase(NetUtils.getLocalHost(),readWorkerIdPathDataFromZk(workerIdPath));
    }

    /**
     * 读取workerIdPath下的path
     * @param workerIdPath  workerIdPath
     * @return  String
     */
    private String readWorkerIdPathDataFromZk(String workerIdPath){
        try {
            byte [] data = client.getData().forPath(workerIdPath);
            return new String(data,"UTF-8");
        } catch (Exception e) {
            logger.error("read workerId data occur error,worker id path is :[{}]",workerIdPath);
            return StringUtils.EMPTY;
        }
    }

    /***
     * 从zookeeper服务器上获取ip对应的workerId,如果没有新建
     * @return  int
     */
    private int registerWorkerIdOfZk(){
        String dataCenterIdPath = ZKPaths.makePath(ZK_ROOT,dataCenterId+"");
        for(int w:WORKER_IDS){
            String workerIdPath = ZKPaths.makePath(dataCenterIdPath,w+"");
            try {
                client.create().withMode(CreateMode.PERSISTENT).forPath(workerIdPath);
            } catch (Exception e) {
                if(e instanceof KeeperException.NodeExistsException){
                    String workerIdData = readWorkerIdPathDataFromZk(workerIdPath);
                    if(StringUtils.equalsIgnoreCase(NetUtils.getLocalHost(),workerIdData)){
                        logger.info("the current machine has registered workerIdPath,workerId is:[{}]",w);
                        return w;
                    }
                }
                continue;
            }
            try {
                client.setData().forPath(workerIdPath,NetUtils.getLocalHost().getBytes());
                return w;
            } catch (Exception e) {
                logger.error("the current machine cannot be set data into the worker id path,ignored");
            }
        }
        throw new IdGenerateException("cannot register workerId to zk");
    }

    /**
     * 将workerId同步到本地文件中
     * @param workerId workerId
     */
    private void saveWorkerId2Local(int workerId){
        //读取资源文件路径
        String localWorkerIdFile = Thread.currentThread().getContextClassLoader().getResource("").getPath() + LOCAL_WORKER_ID_FN ;
        File file = new File(localWorkerIdFile);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(workerId+"");
            bw.close();
        } catch (IOException e) {
            logger.error("save worker id 2 local file occurs error");
        }
    }
    /**
     * 销毁生成器
     */
    public void destroy(){
        if(client != null){
            client.close();
            logger.info("zk client connect is closed");
        }
        logger.info("the twitter id generator is destroyed");
    }
}
