import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 Departments is a class that directs data collection, storage and some reporting functions.  This class is the top
 of the organization chart for modeling the structure at WWU.
 The structure is Departments--->Department---->Course--->Section--->Book
 Every Department object has a map of Course Objects, every Course Object has a map of Section Objects, every Section Object
 has a map of Book Objects.

 Departments Class contains the functions that are responsible for data collection because it is the head of the
 organization chart allowing it easy top down access to every level of data.

 Some reporting tasks are here also because this is were the data "lives."  Although a reference could be passed to
 another class like the Reports class to handle reporting just as easily.  This is a structural decision that is
 under contemplation
 **/
public class Departments {
    //master storage for all departments
    private Map<String,Department> departments;
    //This is built from instantiate() or populateBooksFromWWU() functions depending on the desired execution
    private ArrayList<Book> uniqueBooks;
    private String term;
    String title;
    String sku;
    String newBookPrice;
    String usedBookPrice;
    String bookStatus;
    private JSONObject holder;
    private JSONArray bookHolder;
    private int bookCount;
    private int booksQueried=0;
    private List<Map<String,String>>list;

    public Departments(String term, String year){
        this.term=Actions.formatTermYear(term,year);
        departments=new HashMap<String, Department>();
        holder= new JSONObject();
        bookHolder= new JSONArray();
        uniqueBooks=new ArrayList<Book>();
        bookCount=0;
        list= new ArrayList<Map<String, String>>();
        try {
            holder.put("data",bookHolder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Map<String,Department> getDepartments(){
        return departments;
    }

    /**
     * makes uniqueBooks Array available to other methods
     * @return ArrayList of Book Objects
     */
    public ArrayList<Book> getUniqueBooks(){
        return uniqueBooks;
    }

    /**
     *
     * @return formatted term code for example s15 for spring of 2015.  This is used in the URL to WWU Bookstore
     */
    public String getTerm(){
        return term;
    }

    /**
     * orders all books in the uniqueBooks ArrayList by cost difference relative to Amazon.  If the wwuBookstore charges
     * less for the book the difference will be positive books that are cheapest on amazon will be at the top of the
     * queue
     * @return MaxPriorityQueue<Book>
     */
    public PriorityQueue<Book> getPriorityQueueByCostDifference(){
        if (uniqueBooks.size()>0) {
            Comparator<Book> comparator1 = new Comparator<Book>() {
                @Override
                public int compare(Book o1, Book o2) {
                    if (o1 instanceof Book && o2 instanceof Book) {
                        double one=o1.getWwuUsedPrice()-o1.getAzUsedPrice();
                        double two=o2.getWwuUsedPrice()-o2.getAzUsedPrice();
                        if (one<two) {
                            return -1;
                        }
                        if (one==two) {
                            return 0;
                        }
                        if (one > two) {
                            return 1;
                        }
                    }
                    throw new ClassCastException();
                }
            };
            PriorityQueue<Book> books= new PriorityQueue<Book>(comparator1.reversed());
            books.addAll(uniqueBooks);
            return books;
        }
        return null;
    }

    /**
     * Orders Book objects from greatest to least based on Return on investment if bought on amazon and sold to the
     * wwu Bookstore at the maximum payoff minus shipping of 3.99
     * @return PriorityQueue
     */
    public PriorityQueue<Book> getPriorityQueueByROI(){
        if (uniqueBooks.size()>0){
            Comparator<Book> comparator=new Comparator<Book>() {
                @Override
                public int compare(Book o1, Book o2) {
                    if (o1 instanceof Book && o2 instanceof Book){
                        if (o1.getROI()<o2.getROI()){return -1;}
                        if (o1.getROI()==o2.getROI()){return 0;}
                        if (o1.getROI()>o2.getROI()){return 1;}
                    }
                    throw new ClassCastException();
                }
            };
            PriorityQueue<Book> books=new PriorityQueue<Book>(comparator.reversed());
            books.addAll(uniqueBooks);
            return books;
        }
        return null;
    }

    public int getBookCount(){
        return bookCount;
    }

    /**
     * This is a procedural method for extracting new data from the Western Washington University Bookstore's web server
     * this will write a new JSON file of data to the disk.
     **/
    public void requestDataFromWWU(){
        boolean deptStatus= populateDepartmentsFromWWU();
        System.out.println("Departments Loaded "+deptStatus);
        boolean courseStatus= populateCoursesFromWWU();
        System.out.println("Courses Loaded "+courseStatus);
        boolean sectionsStatus= populateSectionsFromWWU();
        System.out.println("Sections Loaded "+sectionsStatus);
        boolean booksStatus= populatedBooksFromWWU();
        System.out.println("Books Loaded "+booksStatus);
        boolean saveStatus=Actions.save(holder.toString(),"S15.txt");
        System.out.println("Content Saved "+saveStatus);
    }

    /**
     * Adds a new department to the map of Department Objects called departments
     * @param value Deptartment object
     * @return true if successfully added false otherwise
     */
    public boolean addDepartment(Department value){
        if(value==null)return false;
        if(departments.containsKey(value))return false;
        departments.put(value.getDepartmentCode(), value);
        return true;
    }

    /**
     The load() creates a JSONArray out of the data tag which is a holder of sorts for all the books, departments,
     courses, and section numbers.  Each book is stored independently with the corresponding departments, courses,
     and section numbers so that the correct data is associated with the correct book.
     The book data is stored under the book tag.  The next JSONArray is created out of the book tag.  The book data
     is then extracted from the tag and passed to a map of keys and values.  The JSONKey is used as the map key and the
     map is passed to the instantiate().
     **/
    public void load(String location){
        String savedData=Actions.readFile(location);
        Map<String,String> map= new HashMap<String, String>();
        try{
            JSONObject jsonObject= new JSONObject(savedData);
            JSONObject currentObject;
            JSONArray jsonArray= jsonObject.getJSONArray("data");
            JSONArray currentArray;
            for(int i=0; i<jsonArray.length();i++){
                currentObject=jsonArray.getJSONObject(i);
                currentArray=currentObject.getJSONArray("book");
                for(int k=0;k<currentArray.length();k++){
                    JSONObject current=currentArray.getJSONObject(k);
                    Iterator iterator= current.keys();
                    while(iterator.hasNext()){
                        String key=iterator.next().toString();
                        String value= current.getString(key);
                        map.put(key,value);
                    }
                    instantiate(map);
                    map.clear();
                }
            }

        }catch (JSONException e){
            e.printStackTrace();

        }
    }

    /**
     The getAmazonPrices() function cycles through all books and contacts the amazon product api in order to get the
     lowest used and new price for each book.  Currently this function ignores repeat requests for the same isbn which
     will be corrected.  The requests are only allowed once every second and the function limits the requests further by
     limiting sleeping program execution for 1.1*1000 milliseconds 1.1 seconds.  This method limits requests are limited further to
     avoid HTTP 503 errors being generated by the Amazon server.  The unfortunate part of this increased reliability
     is that the maximum request per hour
     falls to 1800 per hour instead of the full 3600 which makes the full execution of this part of the algorithm approximately
     45 minutes.  Thankfully once we get the once duplicated requests are ignored
     the reduction in maximum requests per hour should not hurt efficiency to bad.

     Improvements to be made:
     since we have a list of unique books we can refactor this to not require iteration through the entire set.
     the uniqueBooks list could be used instead saving the trouble of iterating over everything again.
     */

    public void getAmazonPrices(){
        Department currentDepartment;
        Course currentCourse;
        Section currentSection;
        Book currentBook;
        if (departments.size()>0){
            Iterator deptIterator=departments.entrySet().iterator();
            while(deptIterator.hasNext()){
                Map.Entry deptPair=(Map.Entry) deptIterator.next();
                currentDepartment=departments.get(deptPair.getKey());
                Iterator courseIterator= currentDepartment.getCourseCodes().entrySet().iterator();
                while(courseIterator.hasNext()){
                    Map.Entry coursePair=(Map.Entry) courseIterator.next();
                    currentCourse=currentDepartment.getCourseCodes().get(coursePair.getKey());
                    Iterator sectionIterator=currentCourse.getSections().entrySet().iterator();
                    while(sectionIterator.hasNext()){
                        Map.Entry sectionPair=(Map.Entry) sectionIterator.next();
                        currentSection=currentCourse.getSections().get(sectionPair.getKey());
                        Iterator booksIterator=currentSection.getBooks().entrySet().iterator();
                        while(booksIterator.hasNext()){
                            Map.Entry bookPair=(Map.Entry) booksIterator.next();
                            currentBook=(Book)bookPair.getValue();
                            if (!uniqueBooks.contains(currentBook)) {
                                try {
                                    Amazon amazon = new
                                            Amazon(currentBook.getIsbn(), "apiKey", "awsKey");
                                    amazon.queryAmazon();
                                    currentBook.setAzUsedPrice(amazon.getLowestUsedPrice());
                                    currentBook.addHistoricPriceAz(amazon.getLowestUsedPrice());
                                    currentBook.setAzNewPrice(amazon.getLowestNewPrice());
                                    currentBook.setProfitAndROI();
                                    Thread.sleep((long)1.1*1000);
                                    booksQueried+=1;
                                    addToUniqueBooks(currentBook);
                                    save(currentBook);
                                    System.out.println(booksQueried);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            System.out.println(booksQueried+" books queried");
                        }
                    }
                }
            }
            boolean saveStatus=Actions.save(holder.toString(),term+".txt");
            System.out.println(booksQueried+" books queried");
        }
    }

    /**
     * This does the same thing as getAmazonPrices() except it relies on the uniqueBooks ArrayList being filled with
     * the necessary Book objects while the getAmazonPrices() method relies on the map of Department Objects called
     * departments
     */
    public void getAmazonPricesFromLoad(){
        if (uniqueBooks==null)return;
        for (int book=0;book<uniqueBooks.size();book++) {
            Book currentBook=uniqueBooks.get(book);
            try {
                Amazon amazon = new
                        Amazon(currentBook.getIsbn(), "apiKey", "awsKey");
                amazon.queryAmazon();
                currentBook.setAzUsedPrice(amazon.getLowestUsedPrice());
                currentBook.setAzNewPrice(amazon.getLowestNewPrice());
                currentBook.addHistoricPriceAz(amazon.getLowestUsedPrice());
                currentBook.setProfitAndROI();
                Thread.sleep((long) 1.1 * 1000);
                booksQueried += 1;
                addToUniqueBooks(currentBook);
                save(currentBook);
                System.out.println(booksQueried);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(booksQueried+" books queried");
        boolean saveStatus=Actions.save(holder.toString(),term+".txt");
    }

    /**
     This function is really part of the load function.  It was placed in its own method for the sake of possible
     code re-use in the future.  It is conceivable that the necessity to make an instance of a book and all of its
     corresponding data sometime in the future.
     This method has a top down type logic checking to see if a department is instantiated already if it is then
     then the method uses that instantiated object.  This process is repeated for each level of the organizational tree
     books are always instantiated.
     */
    private void instantiate(Map<String,String> map){
        Department currentDept=new Department(map.get("deptCode"),map.get("deptName"));
        Course currentCourse=new Course(map.get("courseCode"),currentDept);
        Section currentSection=new Section(map.get("sectionCode"),currentCourse);
        Book currentBook=new Book(map.get("title"),map.get("isbn"),Actions.bookStatus(map.get("bookStatus")),
                Double.valueOf(map.get("wwuNewPrice")),Double.valueOf(map.get("wwuUsedPrice")),currentSection);
        Iterator mapIterator= map.entrySet().iterator();
        while (mapIterator.hasNext()){
            Map.Entry mapEntry=(Map.Entry) mapIterator.next();
            String key= mapEntry.getKey().toString();
            if (key.contains("azPrice")){
                key=key.substring(7,key.length());
                currentBook.addHistoricPriceAz(key,Double.valueOf((String)mapEntry.getValue()));
            }
            else if (key.contains("wwuPrice")){
                key=key.substring(8,key.length());
                currentBook.addHistoricPriceWwu(key,Double.valueOf((String)mapEntry.getValue()));
            }
            else if (key.equals("azUsedPrice")){
                currentBook.setAzUsedPrice(Double.valueOf((String)mapEntry.getValue()));
            }
            else if(key.equals("azNewPrice")){
                currentBook.setAzNewPrice(Double.valueOf((String)mapEntry.getValue()));
            }

        }
        //if there is an object already created these
        // if statements will replace the new one with the old one.
        //ALso if there isn't a dept course or section this will add a new one.
        if (departments.containsKey(currentDept.getDepartmentCode())){
            currentDept=departments.get(currentDept.getDepartmentCode());
        }else{
            addDepartment(currentDept);
        }
        if (currentDept.getCourseCodes().containsKey(currentCourse.getCourseCode())){
            currentCourse=currentDept.getCourseCodes().get(currentCourse.getCourseCode());
        }else{
            currentDept.addCourse(currentCourse);
        }
        if (currentCourse.getSections().containsKey(currentSection.getSectionCode())){
            currentSection=currentCourse.getSections().get(currentSection.getSectionCode());
        }else{
            currentCourse.addSection(currentSection);
        }
        if (!currentSection.getBooks().containsValue(currentBook)){currentSection.addBook(currentBook);}
        addToUniqueBooks(currentBook);
        currentBook.setProfitAndROI();
    }

    /**
     This method makes a single request to the WWU Bookstore server.  The response will be a JSON format text string
     detailing the departments that the bookstore is handling merchandise for.  Fortunately the bookstore returns all departments
     in one response.  This is not the case with the other data we need and takes substantially more time to acquire because
     of this fact.
     */
    private boolean populateDepartmentsFromWWU(){
        URL url;
        if(term!=null) {
            try {
                url = new URL("https://secure3.sequoiars.com/ePOS?form=" +
                        "shared3/textbooks/json/json_depts.html&term="+term+"&deptSort=ACDEPT_NAME&store=444");
                String jsonSTR=Actions.getTXTFromURL(url);
                JSONObject jsonObject = new JSONObject(jsonSTR);
                Actions.parseJSON(jsonObject,"depts",list);
                for(int listIndex=0; listIndex<list.size();listIndex++){
                    String code=list.get(listIndex).get("code");
                    String deptName=list.get(listIndex).get("name");
                    if (!code.toLowerCase().equals("zlast")) {
                        addDepartment(new Department(Actions.fillSpace(code), deptName));
                    }
                }
                list.clear();//readies the list for the next round
                return true;
            } catch (MalformedURLException e) {
                return false;
            }catch (JSONException e){
                e.printStackTrace();

            }
        }
        return false;
    }

    /**
     * This method iterates through previously acquired department codes.  This method is entirely dependent on the
     populateDepartmentsFromWWU() being run, if it isn't this method will fail unless an old department list is
     instantiated first although I have not made a method to do that yet.  Its an all or nothing type process.
     Either load all the necessary data from a JSON text file or the application must request the data from the WWU
     bookstore server.
     * @return boolean  false if the method fails, true when its done
     */
    private boolean populateCoursesFromWWU(){
        URL url=null;
        Iterator iterator= departments.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry deptEntry=(Map.Entry) iterator.next();
            Department current=departments.get(deptEntry.getKey());
            String departmentCode=current.getDepartmentCode();
            try {
                url = new URL("https://secure3.sequoiars.com/ePOS?form=shared3/" +
                        "textbooks/json/json_courses.html&term=" + term + "&department=" + departmentCode + "&store=444");
                String jsonSTR=Actions.getTXTFromURL(url);
                int count=0;
                while(jsonSTR==null && count<4){
                    jsonSTR=Actions.getTXTFromURL(url);
                    count++;
                }
                if (jsonSTR!=null) {
                    JSONObject jsonObject = new JSONObject(jsonSTR);
                    Actions.parseJSON(jsonObject, "courses", list);
                    for (int course = 0; course < list.size(); course++) {
                        String code = list.get(course).get("code");
                        if (!code.toLowerCase().equals("zlast")) {
                            current.addCourse(new Course(code, current));
                        }
                    }
                    list.clear();
                }
            } catch (MalformedURLException e) {
                System.out.println("Malformed URl");
                return false;
            } catch (JSONException e) {
                System.out.println("JSON Exception");
                System.out.println(url.toString()+" failed to load");
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Iterates through departments and courses to get the section numbers.  dependent on populateDepartmentsFromWWU()
     * and populateCoursesFromWWU()
     * @return
     */
    private boolean populateSectionsFromWWU(){
        Department currentDept;
        Course currentCourse;
        String currentDeptCode;
        String currentCourseCode;
        URL url=null;
        Iterator iterator=departments.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry deptEntry=(Map.Entry) iterator.next();
            currentDept=departments.get(deptEntry.getKey());
            currentDeptCode=currentDept.getDepartmentCode();
            Iterator currentCourseCodeIterator= currentDept.getCourseCodes().entrySet().iterator();
            while(currentCourseCodeIterator.hasNext()) {
                Map.Entry courseEntry=(Map.Entry) currentCourseCodeIterator.next();
                currentCourse=currentDept.getCourseCodes().get(courseEntry.getKey());
                currentCourseCode=currentCourse.getCourseCode();
                try {
                    url = new URL("https://secure3.sequoiars.com/ePOS?form=shared3/textbooks/json/json_sections." +
                            "html&term=" + term + "&department=" + currentDeptCode + "&course=" + currentCourseCode + "&store=444");
                    String jsonSTR=Actions.getTXTFromURL(url);
                    int count=0;
                    while(jsonSTR==null && count<4){
                        jsonSTR=Actions.getTXTFromURL(url);
                        count++;
                    }
                    if (jsonSTR!=null) {
                        JSONObject jsonObject = new JSONObject(jsonSTR);
                        Actions.parseJSON(jsonObject, "sections", list);
                        for (int section = 0; section < list.size(); section++) {
                            Map<String, String> map = list.get(section);
                            String code = map.get("code");
                            if (!code.toLowerCase().equals("zlast")) {
                                String instructor = map.get("instructor");
                                currentCourse.addSection(new Section(code, instructor, currentCourse));
                            }
                        }
                        list.clear();
                    }
                } catch (MalformedURLException e) {
                    return false;
                }catch(JSONException e){
                    System.out.println(url.toString());
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * Gets the book data from WWU books store.  This is dependent on having run the populateDepartmentsFromWWU(),
     * populateCoursesFromWWU() and populateSectionsFromWWU() being run.
     * unlike the previously mentioned functions this function implements the first steps in the automated save process.
     *
     * Improvements:
     * The JSON parsing could be done in a more elegant way similar to the XML parsing
     * @return
     **/
    private boolean populatedBooksFromWWU(){
        URL url=null;
        Iterator iterator= departments.entrySet().iterator();
        try {
            while (iterator.hasNext()) {
                Map.Entry deptEntry=(Map.Entry)iterator.next();
                Department currentDept=departments.get(deptEntry.getKey());
                String deptCode=currentDept.getDepartmentCode();
                Iterator courseIterator=currentDept.getCourseCodes().entrySet().iterator();
                while(courseIterator.hasNext()){
                    Map.Entry courseEntry=(Map.Entry) courseIterator.next();
                    Course currentCourse=currentDept.getCourseCodes().get(courseEntry.getKey());
                    String currentCourseCode=currentCourse.getCourseCode();
                    if (!currentCourseCode.toLowerCase().equals("zlast")){
                        Iterator sectionsIterator= currentCourse.getSections().entrySet().iterator();
                        while(sectionsIterator.hasNext()) {
                            Map.Entry sectionEntry= (Map.Entry) sectionsIterator.next();
                            Section currentSection = currentCourse.getSections().get(sectionEntry.getKey());
                            String currentSectionCode = currentSection.getSectionCode();
                            url = new URL("http://store444.collegestoreonline.com/ePOS?form=shared3/textbooks/json/json_books.html&" +
                                    "term=" + term + "&dept=" + deptCode + "&crs=" + currentCourseCode + "&sec=" + currentSectionCode +
                                    "&store=444&dti=YES&desc=&bSug=YES&cSug=&H=N");
                            String jsonSTR=Actions.getTXTFromURL(url);
                            int count=0;
                            while(jsonSTR==null && count<4){
                                jsonSTR=Actions.getTXTFromURL(url);
                                count++;
                            }
                            if (jsonSTR!=null) {
                                JSONObject jsonObject = new JSONObject(jsonSTR);
                                Actions.parseJSON(jsonObject, "books", list);
                                //finding title
                                for (int listIndex = 0; listIndex < list.size(); listIndex++) {
                                    if (list.get(listIndex).containsKey("title")) {
                                        title = list.get(listIndex).get("title");
                                    }
                                }
                                if (!title.toLowerCase().equals("no books found")) {
                                    for (int listIndex = 0; listIndex < list.size(); listIndex++) {
                                        //data that is the same for new and used books
                                        if (list.get(listIndex).containsKey("title")) {
                                            sku = list.get(listIndex).get("sku");
                                            bookStatus = list.get(listIndex).get("bookstatus");
                                        }
                                        //data applicable to used books only
                                        if (list.get(listIndex).containsKey("isUsed") && list.get(listIndex).get("isUsed").equals("1")) {
                                            usedBookPrice = list.get(listIndex).get("salePrice");
                                        }
                                        //data applicable to new books only
                                        if (list.get(listIndex).containsKey("isNew") && list.get(listIndex).get("isNew").equals("1")) {
                                            newBookPrice = list.get(listIndex).get("salePrice");
                                        }
                                    }
                                    Book currentBook = new Book(title, sku, Actions.bookStatus(bookStatus),
                                            Double.valueOf(newBookPrice), Double.valueOf(usedBookPrice), currentSection);
                                    currentSection.addBook(currentBook);
                                    addToUniqueBooks(currentBook);
                                    currentBook.addHistoricPriceWwu(Double.valueOf(usedBookPrice));
                                    list.clear();//ready list for next use
                                }
                            }
                        }
                    }
                }
            }
        }catch(MalformedURLException e){
            return false;
        }catch(JSONException e){
            System.out.println(url.toString());
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This is used in the instantiate() and getAmazonPrices().  The objective of the uniqueBooks ArrayList<Book> is to
     * create a list of prices so that the program can make fast ROI calculations.
     * @param book
     */
    private void addToUniqueBooks(Book book){
        if (!uniqueBooks.contains(book) && uniqueBooks!=null){
            uniqueBooks.add(book);
            bookCount+=1;
        }
    }

    /**
     * procedural for saving books.  Used in populateBooksFromWWU() and getAmazonPrices()
     * @param book
     */
    private void save(Book book){
        Map<String,String> map=Actions.bookToMap(book);
        JSONObject jsonSave=Actions.convertToJson(map);
        bookHolder.put(jsonSave);
    }
}