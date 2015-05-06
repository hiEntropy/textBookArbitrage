
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin on 3/8/2015.
 */
public class Course {
    private String courseCode;
    private Map<String,Section> sections;
    private Department parent;

    public Course(String courseCode,Department parent){
        this.courseCode=courseCode;
        sections=new HashMap<String,Section>();
        this.parent=parent;
    }

    public Department getParent(){
        return parent;
    }

    public int sectionCount(){
        return sections.size();
    }

    public boolean addSection(Section value){
        if (value==null || sections.containsKey(value.getSectionCode())) return false;
        sections.put(value.getSectionCode(), value);
        return true;
    }

    /**
     *
     * @return
     */

    public String getCourseCode(){
        return courseCode;
    }

    public Map<String,Section> getSections(){
        return sections;
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof Course){
            if (((Course) object).getCourseCode().equals(courseCode)){
                return true;
            }
            return false;
        }
        return false;
    }
}
