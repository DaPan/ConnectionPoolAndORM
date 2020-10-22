package newdao;

import domain.Student;
import orm.annotation.Insert;
import orm.annotation.Select;

import java.util.List;

/**
 * 这个接口的目的是测试注解
 * 如果查询的是多个表格联合
 * 接口的方式不行了
 * 只能使用自定义规则的方式
 */
public interface StudentDao {

    @Insert("insert into student values(#{sid},#{sname},#{ssex},#{sage})")
    public void insert(Student student);

    @Select("select * from student where sid = #{sid}")
    public Student selectOne(Integer sid);

    @Select("select * from student")
    public List<Student> selectList();
}
