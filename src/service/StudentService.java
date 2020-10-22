package service;


import domain.Student;
import orm.SQLSession;
import newdao.StudentDao;

import java.util.List;
import java.util.Map;

public class StudentService {

    //    private SQLSession sqlSession = new SQLSession();
//    private Student dao = sqlSession.getMapper(StudentDao.class);
    //只有Service获取dao时改变了
    //原来dao是new出来的                      是StudentDao对象
    //现在dao是通过getMapper()方法获取出来的    是StudentDao对象的代理对象
    private StudentDao dao = new SQLSession().getMapper(StudentDao.class);

    public void insert(Student student){
        dao.insert(student);
    }

    public Student selectOne(Integer sid) {
        return dao.selectOne(sid);
    }

    public List<Student> selectList(){
        return dao.selectList();
    }






    //业务层
//    private StudentDao dao = new StudentDao();

    //    //业务方法  新增 修改 删除 查询单条 查询多条
//    public void insert(StudentDao student) throws Exception {
//        dao.insert(student);
//        System.out.println("插入成功！");
//    }
//
//    public void delete(Integer sid) throws Exception {
//        dao.delete(sid);
//        System.out.println("删除第"+sid+"条记录成功！");
//    }

//    public void update(StudentDao student) throws Exception {
//        dao.update(student);
//    }

//    public StudentDao selectOne(int sid){
//        return dao.selectOne(sid);
//    }

//    public Map<String, Object> selectOne(int sid) {
//        return dao.selectOne(sid);
//    }

//    public List<Student> selectList(){
//       return dao.selectList();
//    }
}
