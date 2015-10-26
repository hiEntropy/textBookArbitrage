import java.util.Iterator;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        menu();
        String userInput=Actions.getUserInput();
        Departments departments=null;
        while(userInput!=null && !userInput.equals("quit")){
            if(userInput.equals("1")){
                System.out.println("Select a Quarter");
                String term=Actions.getUserInput();
                System.out.println("Select a Year");
                String year=Actions.getUserInput();
                String fileName=Actions.formatTermYear(term,year)+".txt";
                departments=new Departments(term,year);
                Reports report=new Reports(departments);
                departments.load(fileName);
                report.reportROISellingToWwuBestCase();
                report.reportROISellingOnAz();
                report.reportDepartmentBoxAndWhisker();
                report.pricedDifferences();
                System.out.println("Data for "+term+" "+year+" successfully loaded.");
            }
            if (userInput.equals("2")){
                System.out.println("Select Quarter");
                String term=Actions.getUserInput();
                System.out.println("Select Year");
                String year=Actions.getUserInput();
                departments= new Departments(term,year);
                Reports report = new Reports(departments);
                System.out.println("Beginning... You may want to go do something else while I work");
                departments.requestDataFromWWU();
                departments.getAmazonPrices();
                report.reportROISellingToWwuBestCase();
                report.reportROISellingOnAz();
                report.pricedDifferences();
            }

            if (userInput.equals("3")){
                long startTime=System.nanoTime();
                System.out.println("Select Quarter");
                String term= Actions.getUserInput();
                System.out.println("Select Year");
                String year=Actions.getUserInput();
                departments= new Departments(term,year);
                Reports report = new Reports(departments);
                departments.load(Actions.formatTermYear(term,year)+".txt");
                System.out.println("Getting prices from Amazon");
                departments.getAmazonPricesFromLoad();
                System.out.println("Generating Reports");
                report.reportROISellingToWwuBestCase();
                report.reportROISellingOnAz();
                report.pricedDifferences();
                long endTime=System.nanoTime();
                System.out.println("Completed in "+String.valueOf((endTime-startTime)/60000000000.00)+" minutes");//converts to minutes
            }
            if (userInput.equals("4")){
                System.out.println("Select Quarter");
                String term= Actions.getUserInput();
                System.out.println("Select Year");
                String year=Actions.getUserInput();
                departments= new Departments(term,year);
                departments.load(Actions.formatTermYear(term, year) + ".txt");
                System.out.println("Loading Complete");
            }
            if (userInput.equals("5")){
                if(departments!=null) {
                    Map<String, Department> departmentMap = departments.getDepartments();
                    Map<String,Course> courseMap;
                    Map<String,Section> sectionMap;
                    Map<String,Book> bookMap;
                    Department department;
                    Course course;
                    Section section;
                    Iterator departmentMapIterator= departmentMap.entrySet().iterator();
                    while (departmentMapIterator.hasNext()){
                        Map.Entry entry=(Map.Entry)departmentMapIterator.next();
                        department= departmentMap.get(entry.getKey());
                        System.out.println(department.getDepartmentCode());
                    }
                    System.out.println("Select Department");
                    String departmentCode = Actions.getUserInput().toUpperCase();
                    while (!departmentMap.containsKey(departmentCode)){
                        System.out.println("Please Enter a Department Code");
                        departmentCode = Actions.getUserInput().toUpperCase();
                    }
                    department=departmentMap.get(departmentCode);
                    courseMap=department.getCourseCodes();
                    Iterator courseIterator=courseMap.entrySet().iterator();
                    while(courseIterator.hasNext()){
                        Map.Entry courseEntry=(Map.Entry)courseIterator.next();
                        course=courseMap.get(courseEntry.getKey());
                        System.out.println(course.getCourseCode());
                    }
                    System.out.println("Select Course");
                    String courseCode=Actions.getUserInput();
                    while(!courseMap.containsKey(courseCode)){
                        courseCode=Actions.getUserInput();
                    }
                    sectionMap=courseMap.get(courseCode).getSections();
                    Iterator sectionIterator=sectionMap.entrySet().iterator();
                    while(sectionIterator.hasNext()){
                        Map.Entry sectionEntry=(Map.Entry)sectionIterator.next();
                        section=sectionMap.get(sectionEntry.getKey());
                        System.out.println(section.getSectionCode());
                    }
                    System.out.println("Select Section Code");
                    String sectionCode=Actions.getUserInput();
                    while(!sectionMap.containsKey(sectionCode)){
                        sectionCode=Actions.getUserInput();
                    }
                    bookMap=sectionMap.get(sectionCode).getBooks();
                    Iterator bookIterator=bookMap.entrySet().iterator();
                    while(bookIterator.hasNext()){
                        Map.Entry bookEntry=(Map.Entry)bookIterator.next();
                        Book book=bookMap.get(bookEntry.getKey());
                        System.out.println("Title: "+book.getTitle()+" ISBN: "+book.getIsbn()+" Az UsedPrice "
                                +book.getAzUsedPrice()+" WWU Used Price: "+book.getWwuUsedPrice());
                    }
                }else{
                    System.out.println("You must load first");
                }

            }

            menu();
            userInput=Actions.getUserInput();
            if (userInput.toLowerCase().equals("quit")){
                System.out.println("Good Bye");
            }
        }
    }

    public static void menu(){
        System.out.println("Options");
        System.out.println("<1> Load Existing and Generate Reports");
        System.out.println("<2> Request Data From WWU and Amazon (get new data)");
        System.out.println("<3> Collect Data ---Get updated prices from Amazon only");
        System.out.println("<4> Load Existing Data");
        System.out.println("<5> Browse Data---Must load data first");
        System.out.println("<Quit> to stop the application");
    }

}
