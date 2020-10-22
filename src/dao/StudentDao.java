package dao;

import domain.Student;
import orm.RowMapper;
import orm.SQLSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

//负责读写数据库
//类中全部是纯粹的JDBC
public class StudentDao {
    private SQLSession session = new SQLSession();
    //======================方式二=================================
//    //一个新增的方法
//    public void insert(StudentDao student) throws Exception {
//        String sql = "insert into student values(#{sid},#{sname},#{ssex},#{sage})";
//        session.insert(sql,student);
//    }
//
//    //一个删除方法
//    public void delete(Integer sid) throws Exception {
//        String sql = "delete from student where sid = #{sid}";
//        session.delete(sql,sid);
//    }
//
//    //一个修改方法
//    public void update(StudentDao student) throws Exception {
//        String sql = "update student set sname = #{sname},ssex=#{ssex},sage=#{sage} where sid=#{sid}";
//        session.update(sql,student);
//    }

//    public StudentDao selectOne(int sid){
//        String sql = "select * from student where sid = #{sid}";
//        return session.selectOne(sql, sid, StudentDao.class);
//    }
    public Map<String,Object> selectOne(int sid){
        String sql = "select * from student where sid = #{sid}";
        return session.selectOne(sql, sid, Map.class);
    }
    public List<Student> selectList(){
        String sql = "select * from student";
        return session.selectList(sql, Student.class);
    }






    //======================方式一=================================

    //一个新增的方法
//    public void insert(StudentDao student) throws SQLException {
//        String sql = "insert into student values (?,?,?,?)";
//        session.update(sql,student.getSid(),student.getSname(),student.getSsex(),student.getSage());
//    }
//
//    //一个删除方法
//    public void delete(Integer sid) throws SQLException {
//        String sql = "delete from student where sid = ?";
//        session.update(sql,sid);
//    }
//
//    //一个修改方法
//    public void update(StudentDao student) throws SQLException {
//        String sql = "update student set sname = ?,ssex=?,sage=? where sid=?";
//       session.update(sql,student.getSname(),student.getSsex(),student.getSage(),student.getSid());
//    }

    //方式一：学生的单条查询
//    public StudentDao selectOne(Integer sid){
//        //需要提供一条sql,提供sql语句上的问号信息，查询的结果按照什么策略组装成什么对象
//        String sql = "select * from student where sid = ?";
//        RowMapper rm = new RowMapper() {
//            public Object mapperRow(ResultSet rs) throws SQLException {
//                StudentDao student = new StudentDao();
//                student.setSid(rs.getInt("sid"));
//                student.setSname(rs.getString("sname"));
//                student.setSsex(rs.getString("ssex"));
//                student.setSage(rs.getInt("sage"));
//                return student;
//            }
//        };
//        //调用sqlSession对象里的方法帮我们查询
//        StudentDao student = session.selectOne(sql, rm, sid);
//        return student;
//    }
//    //方式一：学生的多条查询
//    public <T> List<T> selectList(){
//        String sql = "select * from student";
//        RowMapper rm = new RowMapper() {
//            @Override
//            public Object mapperRow(ResultSet rs) throws SQLException {
//                StudentDao student = new StudentDao();
//                student.setSid(rs.getInt("sid"));
//                student.setSname(rs.getString("sname"));
//                student.setSsex(rs.getString("ssex"));
//                student.setSage(rs.getInt("sage"));
//                return student;
//            }
//        };
//        //调用session对象里面的方法帮我们查询
//        return session.selectList(sql,rm);
//    }
}
