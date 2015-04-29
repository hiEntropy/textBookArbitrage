/**
 * Created by Austin on 3/21/2015.
 */

public class Main {
    public static void main(String[] args) {
        menu();
        String userInput=Actions.getUserInput();
        while(userInput!=null && !userInput.equals("quit")){
            if(userInput.equals("1")){
                System.out.println("Select a Quarter");
                String term=Actions.getUserInput();
                System.out.println("Select a Year");
                String year=Actions.getUserInput();
                String fileName=Actions.formatTermYear(term,year)+".txt";
                Departments departments1=new Departments(term,year);
                departments1.load(fileName);
                departments1.reportROIRanks();
                departments1.reportPriceDifferences();
                System.out.println("Data for "+term+" "+year+" successfully loaded.");
            }
            if (userInput.equals("2")){
                System.out.println("Select Quarter");
                String term=Actions.getUserInput();
                System.out.println("Select Year");
                String year=Actions.getUserInput();
                String designator=Actions.formatTermYear(term,year)+".txt";
                Departments departments= new Departments(term,year);
                System.out.println("Beginning... You may want to go do something else while I work");
                departments.requestDataFromWWU();
                departments.getAmazonPrices();
                departments.reportROIRanks();
                departments.reportPriceDifferences();
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
        System.out.println("<3> Collect Data");
        System.out.println("<Quit> to stop the application");
    }

}
