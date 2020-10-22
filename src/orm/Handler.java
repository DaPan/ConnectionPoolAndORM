package orm;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 是为了SQLSession的方案二服务
 */
public class Handler {
    //为方案二提供一个小弟，解析一条特殊结构的SQL
    //参数是SQL
    //返回值  包装对象SQLAndKey
    //  一堆key  ArrayList<String>
    //  一条原来那样的SQL
    public SQLAndKey parseSQL(String sql){
        //解析之前有两个变量 存储解析SQL最终的两个部分
        StringBuilder newSQL = new StringBuilder();
        List<String> keyList = new ArrayList<>();
        //解析SQL  insert into student values(#{sid},#{sname},#{ssex},#{sage});
        while(true){
            //按照规定的索引位置寻找
            int left = sql.indexOf("#{");
            int right = sql.indexOf("}");
            //判断两个符号的位置是否是合法
            if (left != -1 && right != -1 && left < right) {
                newSQL.append(sql.substring(0, left));
                newSQL.append("?");
                keyList.add(sql.substring(left + 2, right));//中间key存入集合中
            }else {//证明此时SQL中已经没有key了
                newSQL.append(sql);//最终的部分添加到newSQL中
                break;
            }
            sql = sql.substring(right+1);
        }
        //将上面解析过的变量组合成对象
        return new SQLAndKey(newSQL, keyList);
    }

    //*************两个小小弟为handleParameter服务***************
    //分别负责map和domain对象的拼接
    private void setMap(PreparedStatement pstat, List<String> keyList, Object obj) throws SQLException {
        Map map = (Map)obj;
        for(int i=0;i<keyList.size();i++){
            pstat.setObject(i+1,map.get(keyList.get(i)));
        }
    }
    private void setObject(PreparedStatement pstat,List<String> keyList,Object obj) throws Exception {
        Class cla = obj.getClass();
        for(int i=0;i<keyList.size();i++){
            //先找到key
            String key = keyList.get(i);
            //通过key反射  找到obj对象中的属性  取值
            Field field = cla.getDeclaredField(key);
            //设置私有属性的值  找到私有属性对应的那个get方法
            field.setAccessible(true);
            //获取私有属性的值
            Object value = field.get(obj);
            pstat.setObject(i+1,value);
        }
    }

    //为方案二提供第二个小弟，负责将SQL和问号组装完整
    //参数   pstat对象 keyList全部的key  Object对象
    public void handleParameter(PreparedStatement pstat,List<String> keyList,Object obj) throws Exception {
        //获取obj对象的那个Class
        Class cla = obj.getClass();
        //cla通常的类型有
        //  基本类型 int---Integer  float---Float   String
        //  domain类型    StudentDao Teacher....
        //  map类型
        if (cla == Integer.class || cla == int.class) {
            pstat.setInt(1,(Integer)obj);
        }else if(cla == Float.class || cla == float.class){
            pstat.setFloat(1,(Float)obj);
        }else if(cla == Double.class || cla == double.class){
            pstat.setDouble(1,(Double) obj);
        }else if(cla == String.class){
            pstat.setString(1,(String) obj);
        }else if(cla.isArray()){
            //数组，不支持
        }else{
            //剩下的只有两种可能
            //1.map
            if(obj instanceof Map){
                //再找个小小弟
                this.setMap(pstat,keyList,obj);
            }else{//domain
                //再找个小小弟
                this.setObject(pstat, keyList, obj);
            }
        }
    }
    //上面这个过程是解析对象  对象中的数据取出来  交给pstat进行拼接

    //------------------------------------------------
    //------------------------------------------------
    //------------------------------------------------
    //------------------------------------------------
    //------------------------------------------------

    //下面这个过程是解析对象  将结果集的信息取出来  交给对象组合
    //小小弟
    //将结果集的信息组合成一个map
    private Map getMap(ResultSet rs){
        //创建Map
        Map<String,Object> result = new HashMap<>();
        try {
            //获取结果集中的全部信息
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            //遍历结果集中的全部列
            for(int i=1;i<=resultSetMetaData.getColumnCount();i++){
                //获取每一个列名字
                String columnName = resultSetMetaData.getColumnName(i);
                //根据每一个列名 获取值
                Object value = rs.getObject(columnName);
                //存入map中
                result.put(columnName, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //将结果集中的信息组合成一个domain
    private Object getObject(ResultSet rs, Class resultType) {
        Object obj = null;
        try {
            //通过反射创建对象
            obj = resultType.newInstance();
            //获取结果集中的全部信息
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            //遍历循环结果集
            for(int i=1;i<=resultSetMetaData.getColumnCount();i++){
                //获取结果集的每一列名字
                String columnName = resultSetMetaData.getColumnName(i);
                //反射找到列名字对应的那个属性
                Field field = resultType.getDeclaredField(columnName);
                //操作私有属性
                field.setAccessible(true);
                field.set(obj,rs.getObject(columnName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    //方法负责将结果集的值组合成一个domain对象
    //参数    ResultSet   Class
    Object handleResult(ResultSet rs,Class resultType){
            //1.反射创建对象
            Object result = null;
            try {
                if (resultType == int.class || resultType == Integer.class) {
                    result = rs.getInt(1);
                } else if (resultType == float.class || resultType == Float.class) {
                    result = rs.getFloat(1);
                }else if (resultType == double.class || resultType == Double.class) {
                    result = rs.getDouble(1);
                }else if (resultType == String.class) {
                    result = rs.getFloat(1);
                }else{
                    //是个对象  map  domain
                    if (resultType == Map.class) {
                        result = this.getMap(rs);
                    }else{
                        result = this.getObject(rs, resultType);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
    }
}
