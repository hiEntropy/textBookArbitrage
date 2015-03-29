/**
 * Created by Austin on 3/8/2015.
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.json.*;
import org.w3c.dom.NodeList;

public class Actions {
    public static String formatTermYear(String term, String year){
        String query=null;
        if(term.toLowerCase().equals("spring")){
            query="S"+year.substring(2,year.length());
        }
        if(term.toLowerCase().equals("winter")){
            query="W"+year.substring(2,year.length());
        }
        if(term.toLowerCase().equals("fall")){
            query="F"+year.substring(2,year.length());
        }
        return query;
    }

    /**
     * this removes choice special characters from a string and returns the cleaned string.  If necessary in the future
     * this function could be made to accept an array of "forbidden" characters as a parameter making this function
     * more flexible than its current state.
     * @param value A string
     * @return a cleaned string
     */
    public static String removeSpecialChars(String value,char[] exceptions){
        String cleanedString="";
        if (value==null) return null;
        for (int index=0;index<value.length();index++){
            if(Character.isDigit(value.charAt(index))|| Character.isAlphabetic(value.charAt(index))){
                cleanedString+=String.valueOf(value.charAt(index));
            }
            if(exceptions!=null){
                for (int i=0;i<exceptions.length;i++){
                    if (value.charAt(index)==exceptions[i]){
                        cleanedString+=String.valueOf(value.charAt(index));
                    }
                }
            }
        }
        return cleanedString;
    }

    public static String removeSpecialChars(String value){
        char[] exceptions=null;
        return removeSpecialChars(value,exceptions);
    }

    /**
     * This is used primarily to make urls formed with spaces readable by the receiving server.  This replaces spaces
     * with the "%20" characters.
     * @param value
     * @return string with "%20" where spaces used to be
     */

    public static String fillSpace(String value){
        for(int i=0;i<value.length();i++){
            if(value.charAt(i)==' '){
                value=value.substring(0,i)+"%20"+value.substring(i+1,value.length());
                i--;
            }
        }
        return value;
    }

    /**
     * Accepts an ArrayList of strings, concatenates the strings into a single long string, checks to make sure the
     * location parameter has a file extension and then writes to the file location if it has a file extension.  This
     * will break if the location has an invalid file extension for text such as JPEG or some other nonsense type file
     * extension.
     * @param saveList
     * @param location
     * @return
     */
    public static boolean save(ArrayList<String> saveList,String location) {
        if(location==null || !hasFileHandle(location)) return false;
        if (saveList == null || saveList.size() == 0) return false;
        String saveString = "";
        for (int i = 0; i < saveList.size(); i++) {
            saveString += saveList.get(i);
        }
        if (write(saveString, location)) {
            return true;
        }
        return false;
    }

    /**
     * checks to see if a file exists by making sure that it isn't a directory and that it does infact exist
     * @param value
     * @return true if the file exists and isn't a directory and returns false if either of those parameters are false
     */
    public static boolean fileExists(String value){
        File newFile= new File(value);
        if(newFile.exists() && !newFile.isDirectory()) return true;
        return false;
    }

    /**
     * This uses the fileExists function to determine if a file location does exist.  If a files does exist this function
     * will change the file name with a counter until the fileExists() returns false.
     * @param name String
     * @param type String
     * @return String of the file name when the first unique filename is formulated
     */
    public static String fileNameAssignment(String name,String type){
        int count=1;
        String fileName=name+type;
        while(fileExists(fileName)){
            fileName=fileName+String.valueOf(count)+type;
            count++;
        }
        return fileName.toLowerCase();
    }

    /**
     * Takes a string and file location and saves it to the specified location
     * @param value
     * @param location
     * @return
     */

    public static boolean save(String value, String location) {
        return write(value,location);
    }

    /**
     * Fulfils a very simple but repeated role of determining if a book is required for a class or not.  the word
     * "required" is used by the WWU Bookstore to denote that a book is not optional
     * @param value
     * @return true if it is required false it it is not
     */
    public static boolean bookStatus(String value){
        if(value.toLowerCase().equals("required")) return true;
        return false;
    }

    /**
     * Takes a Map<String,String> then formats keys and value to the specified save format for the load() function in
     * Departments to read in the future.
     * @param data
     * @return
     */
    public static JSONObject convertToJson(Map<String,String> data){
        try {
            JSONObject outer= new JSONObject();
            JSONArray holderArray= new JSONArray();
            JSONObject dataObject= new JSONObject();
            Iterator iterator= data.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry entry= (Map.Entry)iterator.next();
                dataObject.put((String)entry.getKey(),entry.getValue());
            }
            holderArray.put(dataObject);
            outer.put("book",holderArray);
            return outer;
        }catch (JSONException e){
            return null;
        }

    }


    /**
     * Takes a url and reads the response, converts the response to text and returns it.
     * @param url
     * @return String
     */
    public static String getTXTFromURL(URL url){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String curr;
            String jsonSTR = "";
            while ((curr = reader.readLine()) != null) {
                jsonSTR += curr;
            }
            return jsonSTR;
        }catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        }catch (IOException e){
            return null;
        }
    }

    /**
     * This function reads a specified file and returns the a string of the file.  This is used to read saved JSON in the
     * Departments load()
     * @param location
     * @return String
     */
    public static String readFile(String location){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));
            String current="";
            String savedData="";
            while((current=reader.readLine())!=null){
                savedData+=current;
            }
            return savedData;
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return null;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNumeric(String value){
        for (int i=0;i<value.length();i++){
            if (!Character.isDigit(value.charAt(i)))return false;
        }
        return true;
    }

    /**
     * This takes the data stored in a book object and puts it into a Map<string,string> That will be sent to the
     * convertToJson() so that it can be saved.
     * @param book
     * @return map representing the data describing the book
     */
    public static Map<String,String> bookToMap(Book book){
        Map<String,String> map= new HashMap<String, String>();
        Section section=book.getParent();
        Course course= section.getParent();
        Department department=course.getParent();
        map.put("deptCode",department.getDepartmentCode());
        map.put("deptName",department.getDepartmentName());
        map.put("courseCode",course.getCourseCode());
        map.put("sectionCode",section.getSectionCode());
        map.put("instructor",section.getInstructor());
        map.put("title",book.getTitle());
        map.put("isbn",book.getIsbn());
        map.put("usedBookPrice",String.valueOf(book.getWwuUsedPrice()));
        map.put("newBookPrice",String.valueOf(book.getWwuNewPrice()));
        map.put("azUsedPrice",String.valueOf(book.getAzUsedPrice()));
        map.put("azNewPrice",String.valueOf(book.getAzNewPrice()));
        map.put("bookStatus",String.valueOf(book.getIsMandatory()));
        return map;
    }

    /**
     * Used in the UI to get input from the user.  Just a simple scanner does the trick here.
     * @return String
     */
    public static String getUserInput(){
        Scanner scanner= new Scanner(System.in);
        String value=scanner.nextLine();
        value=removeSpecialChars(value);
        return value;
    }

    /**
     * Finds the lowest number and returns it
     * @param value ArrayList<Double>
     * @return double lowest in the ArrayList
     */
    public static double lowestNumber(ArrayList<Double> value){
        if(value.size()<1) return  -1.00;
        double lowest=value.get(0);
        for (int i=0; i<value.size();i++){
            if (lowest>value.get(i))lowest=value.get(i);
        }
        return lowest/100.00;
    }

    /*
    PRIVATE METHODS BELOW HERE
     */

    /**
     * This writes files to the specified location.  The file location is checked for a file extension wit the hasFileExtension()
     * If the file does have a valid extension and the string passed as a parameter is not null the data is written to
     * the specified location.  This function assumes that it is okay to overwrite the file location if it exists.
     * @param value
     * @param location
     * @return
     */
    private static boolean write(String value, String location){
        if (value==null || location==null)return false;
        if(!hasFileHandle(location))return false;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(location));
            writer.write(value);
            writer.close();
            return true;
        }catch (IOException e){
            return false;
        }
    }


    /**
     * Uses the a substring of the last four characters of the location variable to determine if the file extension exists
     * @param location
     * @return
     */

    private static boolean hasFileHandle(String location){
        String lastFour=location.substring(location.length()-4,location.length()).toLowerCase();
        if (lastFour.equals(".txt") || lastFour.equals(".pdf") ||lastFour.equals("jpeg")) return true;
        return false;
    }

}