import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Austin on 3/8/2015.
 */
public class Department {
    private String departmentCode, departmentName;
    private Map<String,Course> courseCodes;
    public Department(String departmentCode, String departmentName){
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        courseCodes=new HashMap<String,Course>();
    }

    public boolean addCourse(Course value){
        if (value==null || courseCodes.containsKey(value)) return false;
        courseCodes.put(value.getCourseCode(),value);
        return true;
    }
    public String getDepartmentCode(){
        if(departmentCode !=null){
            return departmentCode;
        }
        return null;
    }

    public int size(){
        int size=0;
        Iterator iterator= courseCodes.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry current=(Map.Entry)iterator.next();
            String key=(String)current.getKey();
            Course course=courseCodes.get(key);
            size+=course.sectionCount();
        }
        return size+getCourseCount();
    }

    public Map<String,Course> getCourseCodes(){
        return courseCodes;
    }

    public String getDepartmentName(){
        return departmentName;
    }

    public int getCourseCount(){
        if (courseCodes!=null) return courseCodes.size();
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