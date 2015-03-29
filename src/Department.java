import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin on 3/8/2015.
 */
public class Department {
    private String departmentCode, departmentName;
    private Map<String,Course> courseCodes;
    private int courseCount;
    public Department(String departmentCode, String departmentName){
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        courseCodes=new HashMap<String,Course>();
        courseCount=0;
    }

    public boolean addCourse(Course value){
        if (value==null || courseCodes.containsKey(value)) return false;
        courseCodes.put(value.getCourseCode(),value);
        courseCount+=1;
        return true;

    }
    public String getDepartmentCode(){
        if(departmentCode !=null){
            return departmentCode;
        }
        return null;
    }

    public Map<String,Course> getCourseCodes(){
        return courseCodes;
    }

    public String getDepartmentName(){
        return departmentName;
    }

    public int getCourseCount(){
        if (courseCodes!=null) return courseCount;
        return -1;
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof Department){
            if (((Department) object).getDepartmentCode().equals(departmentCode)){
                return true;
            }
            return false;
        }
        return false;
    }
}