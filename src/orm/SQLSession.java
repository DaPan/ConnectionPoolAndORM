package orm;

import orm.annotation.Delete;
import orm.annotation.Insert;
import orm.annotation.Select;
import orm.annotation.Update;
import pool.ConnectionPool;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLSession {
    private Handler handler = new Handler();
    //方案一
    public void update(String sql,Object... objects) throws SQLException {
        //1.导包
        //2.配置文件 driver url name
        //3.获取连接池对象
        ConnectionPool pool = ConnectionPool.getInstance();
        //4.获取连接
        Connection conn = pool.getConnection();
        //5.状态参数
        PreparedStatement pstat = conn.prepareStatement(sql);
        //6.将SQL语句和那些问号值组装完整
        for(int i=0;i<objects.length;i++){
            pstat.setObject(i+1,objects[i]);
        }
        //7.组合完毕，可以执行了
        pstat.executeUpdate();
        //关闭
        pstat.close();
        conn.close();
    }
    public void insert(String sql,Object... objects) throws SQLException {this.update(sql, objects);}
    public void delete(String sql,Object... objects) throws SQLException {this.update(sql, objects);}
    //上述myUpdate有两个不好的地方
    //1.传递objects数组里面的值有顺序
    //2.objects可读性不强

    //方案一  查询单条
    //参数    1.String sql    2.SQL语句上的值 Object...
    //返回值   Object
    //select * from student where sid = ? and sname = ?
    public <T>T selectOne(String sql,RowMapper rm,Object... objects){
        Object obj = null;
        try {
            //1.导包
            //2.加载配置文件
            //3.获取连接池对象
            ConnectionPool pool = ConnectionPool.getInstance();
            //4.获取连接
            Connection conn = pool.getConnection();
            //5.创建状态参数
            PreparedStatement pstat = conn.prepareStatement(sql);
            //6.拼接sql和问号信息
            for(int i=0;i<objects.length;i++){
                pstat.setObject(i+1,objects[i]);
            }
            //7.RS = 执行查询操作
            ResultSet rs = pstat.executeQuery();
            //8.处理结果集（将结果集的数据取出来 放在一个domain对象中）
            if(rs.next()){
                obj = rm.mapperRow(rs);
            }
            //9.关闭结果集 ---->  释放连接
            rs.close();
            pstat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (T)obj;
    }
    public <T> List<T> selectList(String sql, RowMapper rm, Object... objects){
        List<T> list = new ArrayList<>();
        try {
            //1.导包
            //2.加载配置文件
            //3.获取连接池对象
            ConnectionPool pool = ConnectionPool.getInstance();
            //4.获取连接
            Connection conn = pool.getConnection();
            //5.创建状态参数
            PreparedStatement pstat = conn.prepareStatement(sql);
            //6.拼接sql和问号信息
            for(int i=0;i<objects.length;i++){
                pstat.setObject(i+1,objects[i]);
            }
            //7.RS = 执行查询操作
            ResultSet rs = pstat.executeQuery();
            //8.处理结果集（将结果集的数据取出来 放在一个domain对象中）
            while (rs.next()){
                T obj = (T)rm.mapperRow(rs);
                list.add(obj);
            }
            //9.关闭结果集 ---->  释放连接
            rs.close();
            pstat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //==================================


    //方案二
    public void update(String sql,Object obj) throws Exception {
        //1.需要将用户提供给我们的特殊结构的SQL先做一个判断
        //SQL语句上有问号，且不止一个  问号本身没有名字
        //insert into student values(#{sid},#{sname},#{ssex},#{sage});
        //obj对象中属性的值给某一个问号附上  反射找到属性名字
        //解析这个SQL语句
        //1.将这个SQL中的4个key获取出来
        //2.将这个SQL中的4个key替换成问号

        //调用小弟解析
        SQLAndKey sqlAndKey = handler.parseSQL(sql);
//        System.out.println(sqlAndKey.getSQL());//insert into student values(?,?,?,?)
//        System.out.println(sqlAndKey.getKeyList());//[sid, sname, ssex, sage]

        //1.导包
        //2.配置文件 driver url name
        //3.获取连接池对象
        ConnectionPool pool = ConnectionPool.getInstance();
        //4.获取连接
        Connection conn = pool.getConnection();
        //5.状态参数
        PreparedStatement pstat = conn.prepareStatement(sqlAndKey.getSQL());
        //6.将SQL语句和那些问号值组装完整----反射
        //  pstat已经拿到了一条带问号的sql
        //  pstat负责的事情是将新sql和obj对象中的值组合在一起
        if(obj != null){
            handler.handleParameter(pstat,sqlAndKey.getKeyList(),obj);
        }
        //7.组合完毕，可以执行了
        pstat.executeUpdate();
        //关闭
        pstat.close();
        conn.close();
    }
    public void insert(String sql,Object obj) throws Exception {
        this.update(sql, obj);
    }
    public void delete(String sql,Object obj) throws Exception {
        this.update(sql, obj);
    }
    //参数1 sql     参数2 sql上面的值     参数3 查询以后的返回结果
    public <T>T selectOne(String sql,Object obj,Class resultType){
        Object result = null;
        try {
            //sql--- select * from student where sid=#{sid} and sname=#{sname};

            //1.解析SQL
            SQLAndKey sqlAndKey = handler.parseSQL(sql);
            //2.获取连接
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection conn = pool.getConnection();
            //3.状态参数
            PreparedStatement pstat = conn.prepareStatement(sqlAndKey.getSQL());
            //4.把sql和问号拼接在一起
            if (obj != null) {
                handler.handleParameter(pstat,sqlAndKey.getKeyList(),obj);
            }
            //5.执行操作
            ResultSet rs = pstat.executeQuery();
            //6.处理结果（将结果集的值取出来 存入一个domain对象）
            if(rs.next()){
                result = handler.handleResult(rs, resultType);
            }
            //7.关闭
            rs.close();
            pstat.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T)result;
    }
    public <T>List<T> selectList(String sql,Object obj,Class resultType){
        List<T> list = new ArrayList<>();
        try {
            //sql--- select * from student where sid=#{sid} and sname=#{sname};

            //1.解析SQL
            SQLAndKey sqlAndKey = handler.parseSQL(sql);
            //2.获取连接
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection conn = pool.getConnection();
            //3.状态参数
            PreparedStatement pstat = conn.prepareStatement(sqlAndKey.getSQL());
            //4.把sql和问号拼接在一起
            if (obj != null) {
                handler.handleParameter(pstat,sqlAndKey.getKeyList(),obj);
            }
            //5.执行操作
            ResultSet rs = pstat.executeQuery();
            //6.处理结果（将结果集的值取出来 存入一个domain对象）
            while (rs.next()){
                Object result = handler.handleResult(rs, resultType);
                list.add((T) result);
            }
            //7.关闭
            rs.close();
            pstat.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public void update(String sql) throws SQLException {this.update(sql,null);}
    public void insert(String sql) throws SQLException {this.update(sql,null);}
    public void delete(String sql) throws SQLException {this.update(sql,null);}

    public <T> T selectOne(String sql, Class resultType) {
        return this.selectOne(sql, null, resultType);
    }

    public <T> List<T> selectList(String sql, Class resultType) {
        return this.selectList(sql, null, resultType);
    }

    //==============================================================================

    //让SQLSession帮DAO创建一个小弟(代理对象)
    //返回值   对象-代理对象-代理DAO做事
    //参数    哪个DAO？
    public <T>T getMapper(Class cla){
        //如果想要使用动态代理对象帮忙做事  被代理的DAO必须是个接口
        //proxy创建需要3个条件
        //1.类加载器
        ClassLoader loader = cla.getClassLoader();
        //2.Class[] 加载的类    通常数组就一个元素
        Class[] interfaces = new Class[]{cla};
        //3.具体该怎么做事InvocationHandler    接口  具体实现接口  告知具体该如何做事
        InvocationHandler h = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //用来描述代理对象具体如何做事
                //invoke方法是代理对象具体做事的方式
                //帮助原来的DAO做的事情  调用自己的增删改查方法
                //invoke方法中有三个参数    proxy代理对象   method被代理的方法    args被代理方法的参数

                //Dao原来就调用sqlSession中某个方法
                //  代理也是调用sqlSession中某个方法
                //  1.需要帮助DAO代理哪个方法?---取决于注解名

                //获取方法上面的注解
                //method.getAnnotation()中需要一个参数---具体的注解名，但是我们不知道是谁
                //所以用method.getAnnotations()[0]，因为方法上就只有一个注解
                Annotation an = method.getAnnotations()[0];
                //找到这个注解的类型
                Class type = an.annotationType();
                //找到Dao方法上的注解类型---确定该调用哪个方法了
                //但是由于调用上面方法的时候需要SQL
                //  所以我们需要在调用上面方法之前 解析注解---得到SQL
                //找寻当前type注解类型中的哪个value方法
                Method valueMethod = type.getDeclaredMethod("value");
                //执行这个value方法获取里面搬运过来的SQL
                String sql = (String) valueMethod.invoke(an);
                //调用DAO方法之前 除了方法名 和SQL以外  还需要提供SQL上面的值
                //值只有几种情况   1.基本类型  2.domain    3.map   4.没有
                Object param = args==null?null:args[0];
                //根据type判断该调用上述的哪个方法
                if(type == Insert.class){
                    SQLSession.this.insert(sql,param);
                }else if(type == Delete.class){
                    SQLSession.this.delete(sql,param);
                }else if(type == Update.class){
                    SQLSession.this.update(sql,param);
                }else if(type == Select.class){
                    //根据注解名是无法确定调用哪个方法
                    //可以根据method反射 寻找返回值来判断 domain List<domain>
                    //获取method的返回值类型
                    Class methodReturnTypeClass = method.getReturnType();
                    if(methodReturnTypeClass == List.class){
                        //解析methodReturnTypeClass里面的那个泛型
                        Type returnType = method.getGenericReturnType();//返回值的具体类型（java.util.List<domain.StudentDao>）
                        //上述方法的返回值类型正常应该是个Class
                        //由于Class没有办法操作泛型
                        //所以用Type来接收，Type是一个接口，好多子类实现
                        //需要将type还原成可以操作泛型的那个类型
                        ParameterizedTypeImpl realReturnType = (ParameterizedTypeImpl) returnType;
                        //操作返回值类型中的泛型类
                        Type[] patternTypes = realReturnType.getActualTypeArguments();//可以获取泛型类[]
                        //获取泛型类中的第一个元素
                        Type patternType = patternTypes[0];
                        //还原成需要的Class
                        Class resultType = (Class) patternType;
                        return SQLSession.this.selectList(sql, param, resultType);
                    }else{//单条
                        return SQLSession.this.selectOne(sql, param, methodReturnTypeClass);
                    }
                }else{
                    System.out.println("没有这个注解，处理不了");
                }
                return null;
            }
        };

        //创建一个代理对象
        return (T)Proxy.newProxyInstance(loader,interfaces,h);
    }
}
