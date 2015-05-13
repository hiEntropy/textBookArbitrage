/**
 * Created by Austin on 3/8/2015.
 */
import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
     * This removes choice special characters from a string and returns the cleaned string.  If necessary in the future
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

    /**
     *This method used the JSONObject and JSONArray classes provided by java to represent JSON text.  This method
     * has the ability to extract JSON values and keys from a specified target tag regardless of whether the target is
     * an element within an object or an entire JSONArray.
     * @param jsonObject Must be of the JSONObject class found here http://www.json.org/javadoc/org/json/JSONObject.html
     * @param target this is a String representing the tagName of the element, object or array in the JSONArray, or
     *               JSONObject
     * @param list Must be a List<Map<String,String>>.  Nested objects and arrays will be stored in their own separate
     *             Map<String,String>.  This preserves the structure of the original JSON text
     * @throws JSONException is thrown by my method because its thrown by the original JSONArray and JSONObject classes
     * if you are to use the JSONObject or JSONArray classes you will have to handle the exception anyway so I might as
     * well throw another JSONException instead of adding another try catch statement to the mix
     */
    public static void parseJSON(JSONObject jsonObject,String target,List<Map<String,String>>list)throws JSONException{
        if (jsonObject==null)return;
        Iterator iterator= jsonObject.keys();
        String key;
        while(iterator.hasNext()){
            key=(String)iterator.next();//gets all the keys from JSONObject
            if (key.toLowerCase().equals(target.toLowerCase())) {
                if (isJSONArray(jsonObject.getString(key))) {//if its a JSONArray send it to the extractJSONArrayElements
                    Map<String,String>newContainer=new TreeMap<String, String>();
                    list.add(newContainer);
                    extractJSONArrayElements(jsonObject.getJSONArray(key),newContainer,list);
                }
                else if (isJSONObject(jsonObject.getString(key))) {//if its a JSONObject send it to the extract JSONObjectElements
                    Map<String,String>newContainer=new TreeMap<String, String>();
                    list.add(newContainer);
                    extractJSONObjectElements(jsonObject.getJSONObject(key),newContainer,list);
                }
                else{//if its neither a JSONObject or JSONArray it must be a JSONElement so add it to the list
                    Map<String,String>newContainer=new TreeMap<String, String>();
                    list.add(newContainer);
                    newContainer.put(key,jsonObject.getString(key));
                }
            }else{//the target is not equal to the current key
                if (isJSONArray(jsonObject.getString(key))){//if JSONArray
                    JSONArray array=jsonObject.getJSONArray(key);
                    for (int i=0;i<array.length();i++){//send the JSONObjects and JSONArrays to their respective parsers
                        if (isJSONObject(array.getString(i))){
                            parseJSON(array.getJSONObject(i),target,list);//parse JSONObjects
                        }
                        if (isJSONArray(array.getString(i))){
                            parseJSON(array.getJSONArray(i),target,list);//parse JSONArray
                        }
                    }
                }
                if (isJSONObject(jsonObject.getString(key))){//if it an object send it to the parseJSON that takes a JSONObject
                    parseJSON(jsonObject.getJSONObject(key),target,list);
                }
                if (isJSONArray(jsonObject.getString(key))){//if it an object send it to the parseJSON that takes a JSONArray
                    parseJSON(jsonObject.getJSONArray(key),target,list);
                }
            }
        }
        // remove all empty containers
        for (int index=0;index<list.size();index++){
            if (list.get(index).isEmpty()){
                list.remove(index);
            }
        }
    }

    /**
     * This methods job is to take in a JSONArray, extract the objects from it and send it back to the parseJSON method
     * that takes a JSONObject as an argument.
     * @param jsonArray from JSONArray standard java class found http://www.json.org/javadoc/org/json/JSONArray.html
     * @param target String representation of the desired tag
     * @param list List of Map<String,String> to hold the results
     * @throws JSONException is thrown by my method because its thrown by the original JSONArray and JSONObject classes
     * if you are to use the JSONObject or JSONArray classes you will have to handle the exception anyway so I might as
     * well throw another JSONException instead of adding another try catch statement to the mix
     */
    public static void parseJSON(JSONArray jsonArray,String target,List<Map<String,String>>list)throws JSONException{
        if (jsonArray==null)return;
        for(int i=0;i<jsonArray.length();i++){//go through all the indexes of the array
            if (isJSONArray(jsonArray.getString(i))){//if its an array make a recursive call to this method
                parseJSON(jsonArray.getJSONArray(i),target,list);
            }
            if (isJSONObject(jsonArray.getString(i))){//if its an object call parseJSON
                parseJSON(jsonArray.getJSONObject(i), target, list);
            }
        }
    }

    /**
     *
     * @param jsonArray from JSONArray standard java class found http://www.json.org/javadoc/org/json/JSONArray.html
     * @param container Map<String,String> used to hold the elements of the targeted JSON tag
     * @param list the container parameter is added to this list so that all results can be used by the user
     * @throws JSONException is thrown by my method because its thrown by the original JSONArray and JSONObject classes
     * if you are to use the JSONObject or JSONArray classes you will have to handle the exception anyway so I might as
     * well throw another JSONException instead of adding another try catch statement to the mix
     */
    public static void extractJSONArrayElements(JSONArray jsonArray,Map<String,String> container,List<Map<String,String>>list) throws JSONException{
        if (jsonArray==null)return;
        for (int i=0;i<jsonArray.length();i++){//go through all indexes
            if (isJSONArray(jsonArray.getString(i))){//of its an array
                Map<String,String>newContainer=new TreeMap<String, String>();//create new Map
                list.add(newContainer);//add the map to the list
                extractJSONArrayElements(jsonArray.getJSONArray(i), newContainer, list);//recursive call to this function
            }
            if (isJSONObject(jsonArray.getString(i))){//if its an object
                Map<String,String>newContainer=new TreeMap<String, String>();//create a map
                list.add(newContainer);//add the map to list
                extractJSONObjectElements(jsonArray.getJSONObject(i), newContainer, list);// sent it to be parsed by the
                //extractJSONObjectElements function
            }
        }
    }

    /**
     * @param jsonObject as documented at http://www.json.org/javadoc/org/json/JSONObject.html
     * @param container Map<String,String> used to hold the elements of the targeted JSON tag
     * @param list the container parameter is added to this list so that all results can be used by the user
     * @throws JSONException is thrown by my method because its thrown by the original JSONArray and JSONObject classes
     * if you are to use the JSONObject or JSONArray classes you will have to handle the exception anyway so I might as
     * well throw another JSONException instead of adding another try catch statement to the mix
     */
    public static void extractJSONObjectElements(JSONObject jsonObject,Map<String,String>container,List<Map<String,String>>list)throws JSONException{
        if (jsonObject==null)return;
        Iterator iterator=jsonObject.keys();// get the keys
        while(iterator.hasNext()){//look at all keys
            String key=(String)iterator.next();
            if (!isJSONArray(jsonObject.getString(key)) && !isJSONObject(jsonObject.getString(key))){// if its a JSONElement
                container.put(key,jsonObject.getString(key));//add it to the provided container
            }
            if (isJSONArray(jsonObject.getString(key))){// if its a JSONArray
                Map<String,String>newContainer=new TreeMap<String, String>();//make a new map
                list.add(newContainer);//add the map
                extractJSONArrayElements(jsonObject.getJSONArray(key), newContainer, list);//send it to extractJSONArrayElements
                //for parsing
            }
        }
    }

    /**
     * determines if a String fits the format of a JSONObject
     * @param value String representation of a JSONObject
     * @return true if the string conforms to JSONObject format otherwise returns false
     */
    public static boolean isJSONObject(String value){
        JSONObject test;
        try{
            test=new JSONObject(value);
            return true;
        }catch (JSONException e){
            return false;
        }
    }

    /**
     * determines if a String fits the format of a JSONArray
     * @param value String representation of a JSONArray
     * @return true if the string conforms to JSONArray format otherwise returns false
     */
    public static boolean isJSONArray(String value){
        JSONArray test;
        try{
            test=new JSONArray(value);
            return true;
        }catch (JSONException e){
            return false;
        }
    }

    /**
     * NEEDS TESTING--totally untested... don't trust it...use at your own risk... may produce unexpected output...
     *
     * @param object
     * @param target
     * @return
     */
    public static JSONObject getJsonObject(JSONObject object,String target){
        if(object!=null) return null;
        Iterator iterator= object.keys();
        while (iterator.hasNext()){
            String key= (String)iterator.next();
            if (key.equals(target)){
                try {
                    JSONArray current = object.getJSONArray(key);
                    return new JSONObject(current);
                }catch (JSONException e){
                }
            }
            else{
                try {
                    JSONArray current = object.getJSONArray(key);
                    getJsonObject(new JSONObject(current),target);
                }catch (JSONException e){

                }
            }
        }
        return null;
    }


    /**
     * Needs more testing.
     * @param list
     * @return
     */
    public static double[] toArray(List list){
        if (list==null)return null;
        if (list.get(0) instanceof Double) {
            double[] rValue = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                rValue[i] = (Double) list.get(i);
            }
            return rValue;
        }
        if (list.get(0) instanceof Integer){
            double[] rValue = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                rValue[i] = ((Integer)(list.get(i))).doubleValue();
            }
            return rValue;
        }
        if (list.get(0) instanceof Short){
            double[] rValue = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                rValue[i] = ((Short)(list.get(i))).doubleValue();
            }
            return rValue;
        }
        if (list.get(0) instanceof Float){
            double[] rValue = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                rValue[i] = ((Float)(list.get(i))).doubleValue();
            }
            return rValue;
        }
        return null;
    }

    /**
     * Untested
     * @param nodeList
     * @param endTag
     * @param nestTag
     * @param list
     * @return
     */
    public static boolean XmlParser(NodeList nodeList,String endTag,String nestTag,List<String> list){
        if (nodeList.getLength()==0)return false;
        for (int i=0; i< nodeList.getLength();i++){
            if (nodeList.item(i).getNodeName().equals(endTag)){
                NodeList current=nodeList.item(i).getChildNodes();
                for(int items=0;items<current.getLength();items++){
                    if (current.item(items).getNodeName().equals(nestTag)){
                        String target=current.item(items).getTextContent();
                        if (!target.isEmpty() && !target.equals(null) && Actions.isNumeric(target)){
                            list.add(target);
                            return true;
                        }
                    }
                }
            }
            XmlParser(nodeList.item(i).getChildNodes(), endTag, nestTag, list);
        }
        return false;
    }

    public static boolean inspectXML(NodeList nodeList,String endTag,String nestTag,List<Double> list){
        if (nodeList.getLength()==0)return false;
        for (int i=0; i< nodeList.getLength();i++){
            if (nodeList.item(i).getNodeName().equals(endTag)){
                NodeList lowestPriceList=nodeList.item(i).getChildNodes();
                for(int items=0;items<lowestPriceList.getLength();items++){
                    if (lowestPriceList.item(items).getNodeName().equals(nestTag)){
                        String price=lowestPriceList.item(items).getTextContent();
                        if (!price.isEmpty() && !price.equals(null) && Actions.isNumeric(price)){
                            list.add(Double.valueOf(price));
                            return true;
                        }
                    }
                }
            }
            inspectXML(nodeList.item(i).getChildNodes(), endTag, nestTag, list);
        }
        return false;
    }

    /**
     * Takes a XML document1 and prints out the document1 to the console
     * @param doc
     * @throws TransformerException
     */
    public static void printXMLResponse(Document doc) throws TransformerException{
        try {
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            Properties props = new Properties();
            props.put(OutputKeys.INDENT, "yes");
            trans.setOutputProperties(props);
            StreamResult res = new StreamResult(new StringWriter());
            DOMSource src = new DOMSource(doc);
            trans.transform(src, res);
            String toString = res.getWriter().toString();
            System.out.println(toString);
        }catch (TransformerException e){
            e.printStackTrace();
        }
    }

    /**
     * calculates ROI as a percentage
     * @param costs
     * @param grossProfit
     * @return
     */
    public static double calcROI(double costs,double grossProfit){
        return 100*(grossProfit-costs)/costs;
    }

    public static double calcROISellingOnAz(Book book, double mark){
        double costs=costsSellingSelfFulfilledBook(book);
        return 100*(setMyAzSalePrice(book,mark)-costs)/costs;
    }


    /**
     * depends on a non-null book.  it will crash if you send it a null book
     * @param book
     * @return
     */
    public static double profitFromSelfFulfilledBook(Book book,double mark){
        double azSalePrice=setMyAzSalePrice(book,mark);
        double profit=azSalePrice-costsSellingSelfFulfilledBook(book);
        return profit;
    }

    /**
     * It will crash if you send it a null book
     * @param book
     * @return
     */
    public static double profitFromAzFulfilled(Book book){
        double azSalePrice=book.getAzUsedPrice()-5.00;//undercut the competition
        double profit=(azSalePrice)-costsSellingAzFulfilled(book);
        return profit;
    }

    /**
     * mark is how much you want to undercut or over-cut
     * @param book
     * @param mark
     * @return
     */
    public static double setMyAzSalePrice(Book book,double mark){
        if (mark<1) {
            return book.getLowestAZPrice() * (1 - mark);
        }
        if (mark>1){
            return book.getLowestAZPrice() *(1+mark);
        }
        return book.getLowestAZPrice();
    }
    /**
     * It will crash if you send it a null book
     * @param book
     * @return
     */
    public static double costsSellingSelfFulfilledBook(Book book){
        double acquisitionCost=book.getLowestWWUPrice();
        double shippingCost=3.17;//USPS media mail rate
        double trackingCost=1.05;//USPS tracking costs
        double envelope=1.89;//USPS cost to buy a bubble wrapped shipping envelope
        double tax=acquisitionCost*.087;
        double commission=book.getLowestAZPrice()*.945*.15;
        double listFee=.99;
        return acquisitionCost+shippingCost+trackingCost+envelope+tax+commission+listFee;
    }

    public static double costsSellingAzFulfilled(Book book){
        double acquisitionCost=book.getWwuUsedPrice();
        double shipToAZ=3.99;
        double azCommission=.15;
        double listFee=.99;
        double shipToUPurchaser=14.99;
        double tax=book.getWwuUsedPrice()*.087;
        return acquisitionCost+shipToAZ+(azCommission*book.getAzUsedPrice())+listFee+shipToUPurchaser+tax;
    }

    /**
     * This is an overloaded function that takes only a single parameter instead of an Array of exceptions.  The
     * function automatically passes a null char[] to removeSpecialChars(String, char[]) so that if the client wishes
     * to have no exceptions doesn't have to instantiate a null array.
     * @param value
     * @return
     */
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
        if (value==null) return null;
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
            fileName=name+"("+String.valueOf(count)+")"+type;
            count++;
        }
        return fileName.toLowerCase();
    }

    /**
     * Takes a string and file location and saves it to the specified location
     * @param value String of input that needs to be saved
     * @param location String representation of the file location
     * @return true if the write is complete otherwise false
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
     * @param data map of strings
     * @return a JSON object
     **/
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
     * @param url as defined in the URL class
     * @return String
     **/
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
     * @param location String file location
     * @return String
     **/
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

    /**
     *
     * @param value String of chars
     * @return true if there is a single digit otherwise false
     */
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
     **/
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
        map.put("wwuUsedPrice",String.valueOf(book.getWwuUsedPrice()));
        map.put("wwuNewPrice",String.valueOf(book.getWwuNewPrice()));
        map.put("azUsedPrice",String.valueOf(book.getAzUsedPrice()));
        map.put("azNewPrice",String.valueOf(book.getAzNewPrice()));
        map.put("bookStatus",String.valueOf(book.getIsMandatory()));
        Iterator iteratorAz= book.getHistoricPricesAz().entrySet().iterator();
        if (iteratorAz!=null) {
            while (iteratorAz.hasNext()) {
                Map.Entry current = (Map.Entry) iteratorAz.next();
                map.put("azPrice"+current.getKey().toString(), String.valueOf(current.getValue()));
            }
        }
        Iterator iteratorWwu=book.getHistoricPricesWwu().entrySet().iterator();
        if (iteratorWwu!=null){
            while(iteratorWwu.hasNext()){
                Map.Entry current=(Map.Entry) iteratorWwu.next();
                map.put("wwuPrice"+current.getKey().toString(),String.valueOf(current.getValue()));
            }
        }
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
    public static double lowestNumber(List<Double> value){
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
     **/
    private static boolean hasFileHandle(String location){
        String lastFour=location.substring(location.length()-4,location.length()).toLowerCase();
        if (lastFour.equals(".txt") || lastFour.equals(".pdf") ||lastFour.equals("jpeg")) return true;
        return false;
    }
}