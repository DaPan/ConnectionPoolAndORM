package pool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 这是一个用来管理连接的类
 * 连接默认先创建10个--->放在一个集合里（通过配置文件可以修改）
 * 用户使用的时候，去集合里寻找可用连接拿去使用
 */
public class ConnectionPool {
    //实现一个单例设计模式
    //1.构造方法私有
    private ConnectionPool(){}
    //2.私有静态的当前类作为属性
    private static volatile ConnectionPool connectionPool;//延迟加载机制
    //3.公有静态的方法返回当前对象
    public static ConnectionPool getInstance(){
        if(connectionPool == null){
            synchronized(ConnectionPool.class){
                if(connectionPool == null) {
                    connectionPool = new ConnectionPool();  //双重检测模型
                }
            }

        }
        return connectionPool;
    }

    //连接池中默认连接的个数
    private static final int DEFAULT_CONNECTION = 10;

    //属性---集合
    //为了更好的遍历集合中的元素，使用List集合
    //集合的泛型为MyConnection,因为我们包装的MyConnection中有connection的状态
    //拿去使用时将conn设置为true,使用完将其设置为false，以达到连接不释放的目的
    private List<Connection> connectionList = new ArrayList<Connection>();

    //设计一个块,为了往集合里放入连接
    {
        int count = 0;
        String minCount = ConfigurationReader.getStringValue("minCount");
        if(minCount == null){
            count = DEFAULT_CONNECTION;
        }else{
            count = Integer.parseInt(minCount);
        }
        for(int i=0;i<count;i++){
            connectionList.add(new MyConnection());
        }
    }

    //需要给用户提供一个方法  获取一个可用连接
    private synchronized Connection getMyConnection(){
        Connection result = null;
        for (int i=0;i<connectionList.size();i++){
            //去连接池中每次循环获取一个连接
            MyConnection mc = (MyConnection) connectionList.get(i);
            //判断mc的状态
            if(!mc.isUsed()){       //等价于mc.isUsed() == false 当前获取连接是空闲的
                mc.setUsed(true);   //将当前连接先占为己有
                result = mc;
                break;
            }
        }
        return result;
    }
    //为了让用户体验更好，添加一个排队等待机制
    //5秒钟
    public  Connection getConnection(){
        Connection result = this.getMyConnection();
        int count = 0;
        while(result == null && count<ConfigurationReader.getIntValue("waitTime")*10){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = this.getMyConnection();
            count++;
        }
        if(result == null){//等待5秒钟还是没有连接
            //1.输出告诉用户
            //2.自定义异常告诉用户 系统正忙，请刷新重试
            throw new ConnectionPoolBusyException("系统正忙，请刷新重试");

        }
        return result;
    }
}
