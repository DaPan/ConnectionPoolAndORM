package test;

import domain.Student;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;
import service.StudentService;

import java.util.List;
import java.util.Map;

public class TestService {
    public static void main(String[] args) throws Exception {

        StudentService service = new StudentService();
        //service.insert(new Student(5,"周五五","男",20));

//        Student student = service.selectOne(1);
//        System.out.println(student);

        List<Student> list = service.selectList();
        for (Student student : list) {
            System.out.println(student);
        }
















//        StudentService service = new StudentService();
//        service.insert(new StudentDao(4,"李思思","女",20));

//        service.delete(4);

       // service.update(new StudentDao(2,"钱二二","男",19));

//        StudentDao student = service.selectOne(1);

//        List<Student> list = service.selectList();
//        for (Student student : list) {
//            System.out.println(student);
//        }
    }

}
