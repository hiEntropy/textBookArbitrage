
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
                Reports report=new Reports(departments1);
                departments1.load(fileName);
                report.reportROIRanks();
                report.reportPriceDifferences();
                System.out.println("Data for "+term+" "+year+" successfully loaded.");
            }
            if (userInput.equals("2")){
                System.out.println("Select Quarter");
                String term=Actions.getUserInput();
                System.out.println("Select Year");
                String year=Actions.getUserInput();
                Departments departments= new Departments(term,year);
                Reports report = new Reports(departments);
                System.out.println("Beginning... You may want to go do something else while I work");
                departments.requestDataFromWWU();
                departments.getAmazonPrices();
                report.reportROIRanks();
                report.reportPriceDifferences();
            }

            if (userInput.equals("3")){
                long startTime=System.nanoTime();
                System.out.println("Select Quarter");
                String term= Actions.getUserInput();
                System.out.println("Select Year");
                String year=Actions.getUserInput();
                Departments departments= new Departments(term,year);
                Reports report = new Reports(departments);
                departments.load(Actions.formatTermYear(term,year)+".txt");
                System.out.println("Getting prices from Amazon");
                departments.getAmazonPricesFromLoad();
                System.out.println("Generating Reports");
                report.reportROIRanks();
                report.reportPriceDifferences();
                long endTime=System.nanoTime();
                System.out.println("Completed in "+String.valueOf((endTime-startTime)/60000000000.00)+" minutes");//converts to minutes
            }
            if (userInput.equals("4")){
                System.out.println("Select Quarter");
                String term= Actions.getUserInput();
                System.out.println("Select Year");
                String year=Actions.getUserInput();
                Departments departments= new Departments(term,year);
                departments.load(Actions.formatTermYear(term, year) + ".txt");
                System.out.println("Loading Complete");
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
        System.out.println("<Quit> to stop the application");
    }

}
