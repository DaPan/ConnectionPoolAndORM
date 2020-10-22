# ConnectionPoolAndORM
简单的封装JDBC连接池和ORM框架
连接池的目的是为了提升底层的性能，避免获取连接时都创建一个新的连接，这样底层的性能很不好，因为创建连接是很费时间的
本项目的连接池通过
  ConnectionPool pool = ConnectionPool.newInstance();//获取连接池对象
  Connection connection = pool.getConnection();//从连接池中获取连接
连接池基本流程
 1.导包
 2.配置configuration.properties配置文件
 3.获取连接池对象
 4.获取连接
 5.创建状态参数
 6.执行SQL语句
 7.关闭连接
上述流程中应用到了静态代理设计模式，例如关闭连接，并不是真正的关闭，而是将连接释放回连接池中

ORM框架最终实现将dao层抽象成接口，通过接口中抽象方法上的注解配置sql语句，通过动态代理来解析SQL，执行操作，简化了dao层代码冗余问题
 
