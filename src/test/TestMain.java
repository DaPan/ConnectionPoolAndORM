package test;

import pool.ConnectionPool;
import pool.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestMain {
    public static void main(String[] args) throws SQLException {


        //------------------------------------------------------------
//        //1.导包
//        //2.加载驱动
//        ConnectionPool pool = ConnectionPool.getInstance();
//        //3.获取连接
//        Connection conn = pool.getConnection();
//        //4.创建状态参数
//        PreparedStatement pstat = conn.prepareStatement("select * from student");
//        //5.执行SQL语句
//        ResultSet rs = pstat.executeQuery();
//        while(rs.next()){
//            System.out.println(rs.getString("sname"));
//        }
//        //6.关闭连接
//        rs.close();
//        pstat.close();
//        conn.close();










        //---------------------------------------------
//        TestThread t1 = new TestThread();
//        TestThread t2 = new TestThread();
//        TestThread t3 = new TestThread();
//        TestThread t4 = new TestThread();
//        TestThread t5 = new TestThread();
//        TestThread t6 = new TestThread();
//        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();
//        t5.start();
//        t6.start();










//-------------------------------------------------------------------------------
//        //1.导包
//        //2.创建一个连接池对象
//        ConnectionPool pools = ConnectionPool.getInstance();
//        //3.调用连接池提供的方法获取一个可用连接
//        MyConnection mc = pools.getMC();
//        //4.将mc中的真实连接获取出来
//        Connection conn = mc.getConn();
//        //5.状态参数
//        PreparedStatement pstat = conn.prepareStatement("select * from user ");
//        //6.执行操作
//        ResultSet rs = pstat.executeQuery();
//        while (rs.next()){
//            System.out.println(rs.getInt("员工编号"));
//            System.out.println(rs.getString("姓名"));
//        }
//        //7.关闭
//        rs.close();
//        pstat.close();
//        mc.setUsed(false);  //释放连接(将状态切换为空闲状态)
    }
}