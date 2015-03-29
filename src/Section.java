import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin on 3/9/2015.
 */
public class Section {

    private Map<String,Book> books;
    private String instructor;
    private String sectionCode;
    private Course parent;
    public Section(String sectionCode,String instructor,Course parent){
        this.sectionCode=sectionCode;
        this.instructor=instructor;
        books=new HashMap<String, Book>();
        this.parent=parent;
    }

    public Course getParent(){
        return parent;
    }

    public Section(String sectionCode,Course parent){
        this.sectionCode=sectionCode;
        books=new HashMap<String, Book>();
        this.parent=parent;
    }

    public boolean addBook(Book value){
        if (value==null || books.containsKey(value.getIsbn()))return false;
        books.put(value.getIsbn(), value);
        return true;
    }

    public String getInstructor(){
        return instructor;
    }

    public String getSectionCode(){
        return sectionCode;
    }

    public Map<String,Book> getBooks(){
        return books;
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof Section){
            if (((Section) object).getSectionCode().equals(sectionCode)){
                return true;
            }
            return false;
        }
        return false;
    }
}
