package pool;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 这个类是我自己描述的
 * 这个类中的每一个对象，都有一个真实连接并且绑定着一个状态
 * 目的是为了将一个真实连接和状态绑定在一起，以达到连接复用的效果
 * 这个类的连接不是单例的，每次调用MyConnection，都需要获取一个连接对象
 */
public class MyConnection extends AbstractConnection{
    //真实连接
    //conn赋不上初值，原因有2
    //1.在获取连接之前，需要先加载驱动。Class.forName();
    //2.Connection conn = DriverManager.getConnection("","","");有异常，属性不能抛异常
    private Connection conn;

    //连接的状态     false表示空闲(可用)   true表示被占用(不能用)
    private boolean used = false;   //boolean属性默认值也是false

    //加载驱动和创建连接需要的字符串
    private static String driver = ConfigurationReader.getStringValue("driver");
    private static String url = ConfigurationReader.getStringValue("url");
    private static String username = ConfigurationReader.getStringValue("username");
    private static String password = ConfigurationReader.getStringValue("password");

    //静态块的目的是我们发现加载驱动这件事好像只需要做一次就可以了，所以使用静态块
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //一般块的目的  为了给Connection属性赋值  属性中不能处理异常
    {
        try {
            conn = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //以下是两个私有属性对应的get和set方法
    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public boolean isUsed() {   //属性是boolean,get方法叫做isXXX()
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
    //----------------------------------------------------------------
    //----------------------------------------------------------------
    //----------------------------------------------------------------
    @Override
    public Statement createStatement() throws SQLException {
        return this.conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement pstat = this.conn.prepareStatement(sql);
        return pstat;
    }

    @Override
    public void close() throws SQLException {
        this.used = false;
    }


}
