import java.util.*;

public class Reports {
    Departments departments;
    public Reports (Departments departments){
        this.departments= departments;
    }

    public void reportDepartmentBoxAndWhisker(){
        List prices = new ArrayList();
        String saveString=String.format("%-10s %-10s %-10s %-10s %-10s %-10s %-10s","Department","Q1","Q2","Q3","IQR","lowerBound","upperBound")+"\n";
        String fileName=Actions.fileNameAssignment("departmentBoxWhisker",".txt");
        Iterator departmentsIterator=departments.getDepartments().entrySet().iterator();
        while(departmentsIterator.hasNext()){
            Map.Entry departmentEntry=(Map.Entry)departmentsIterator.next();
            Department department=departments.getDepartments().get(departmentEntry.getKey());
            List listBooks=departments.getBooksByDepartment(department);
            for (int i=0;i< listBooks.size();i++){
                Book book=(Book)listBooks.get(i);
                if(book.getAzUsedPrice()>-1){
                    prices.add(book.getAzUsedPrice());
                }
            }
            Map<String,Double> boxAndWhisker=Statistics.boxAndWhisker(prices,1.5);
            if (boxAndWhisker.get("Q1")!=null) {
                saveString += Statistics.boxAndWhiskerToString(boxAndWhisker,department.getDepartmentCode()) + "\n";
            }

        }
        Actions.save(saveString,fileName);
    }

    public boolean reportROISellingOnAz(){
        PriorityQueue<Book> books=departments.getPriorityQueueByROISellingOnAz(.055);
        String fileName=Actions.fileNameAssignment("ROI_selling_on_az_"+departments.getTerm(),".txt");
        double amazonCheaper=0;
        double wwuCheaper=0;
        double amazonNoOffer=0;//This is the amount of times the WWU has the book used but amazon does not
        double wwuNoOffer=0;//This is the amount of times wwu does not offer a used book but Amazon does
        if (books!=null){
            String saveString= String.format("%-10s %-10s %-8s %-10s %-15s %-15s %-15s %-15s %-15s %-15s","Dept","Course","Section","ISBN","WWU Price","Amazon Price",
                    "MyPrice","Difference","Profit", "ROI")+"\n";
            String formattedData="";
            while(books.peek()!=null){
                Book current=books.poll();
                String dept=current.getParent().getParent().getParent().getDepartmentCode();
                String course=current.getParent().getParent().getCourseCode();
                String section=current.getParent().getSectionCode();
                if (current.getAzUsedPrice()==-1 && current.getWwuUsedPrice()>0){
                    amazonNoOffer+=1;
                }
                else if (current.getAzUsedPrice()>0 && current.getWwuUsedPrice()==0){
                    wwuNoOffer+=1;
                }
                else if(current.getAzUsedPrice()!=-1 && current.getWwuUsedPrice()!=0) {
                    if(current.getAzUsedPrice()>current.getWwuUsedPrice()){
                        wwuCheaper+=1.0;
                    }
                    else{
                        amazonCheaper+=1.0;
                    }
                    formattedData = String.format("%-10s %-10s %-8s %-10s %-15.2f %-15.2f %-15.2f %-15.2f %-15.2f %-15.2f",dept,course,section, current.getIsbn(),
                            current.getLowestWWUPrice(),current.getLowestAZPrice(),Actions.setMyAzSalePrice(current,.055), current.getLowestAZPrice() - current.getLowestWWUPrice(),
                            Actions.profitFromSelfFulfilledBook(current,.055),Actions.calcROISellingOnAz(current, .055))+ "\n";
                    saveString += formattedData;
                }
            }
            double percentAmazonCheaper=(amazonCheaper / (amazonCheaper + wwuCheaper))*100;
            double percentWwuCheaper=(wwuCheaper/(amazonCheaper+wwuCheaper))*100.00;
            double amazonNoOfferPercent=((amazonNoOffer)/(amazonCheaper+wwuCheaper))*100.00;
            double wwuNoOfferPercent=((wwuNoOffer)/(amazonCheaper+wwuCheaper))*100.00;
            saveString+="\n"+String.format("%-25s %-35s ","Times WWU is Cheaper","Times AZ is Cheaper")+"\n"+"\n";
            saveString+=String.format("%-25.0f %-35.0f",wwuCheaper,amazonCheaper)+"\n";
            saveString+=String.format("%-25s %-35s","Percent WWU is Cheaper","Percent AZ is Cheaper")+"\n\n";
            saveString+=String.format("%-25.2f %-35.2f",percentWwuCheaper,percentAmazonCheaper)+"\n\n";
            saveString+=String.format("%-25s %-35s","Amazon No Offer","WWU No Offer")+"\n\n";
            saveString+=String.format("%-25.2f %-35.2f",amazonNoOffer,wwuNoOffer)+"\n\n";
            saveString+=String.format("%-25s %-35s","Percent AZ No Offer","Percent WWU No Offer")+"\n\n";
            saveString+=String.format("%-25.2f %-35.2f",amazonNoOfferPercent,wwuNoOfferPercent)+"\n\n";
            if (Actions.save(saveString,fileName)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean reportROISellingToWwuBestCase(){
        PriorityQueue<Book> books=departments.getPriorityQueueByROI();
        String fileName=Actions.fileNameAssignment("best_case_selling_to_wwu_"+departments.getTerm(),".txt");
        if (books!=null) {
            String saveString =
                    String.format("%-15s %-15s %-15s %-14s %-14s %-14s %-15s","Dept","Course", "ISBN", "Amazon", "WWU", "Profit", "ROI");
            String formattedData = "";
            while (books.peek() != null) {
                Book current = books.poll();
                String isbn = current.getIsbn();
                String dept= current.getParent().getParent().getParent().getDepartmentCode();
                String course=current.getParent().getParent().getCourseCode();
                double azUsedPrice = current.getAzUsedPrice();
                double wwuUsedPrice = current.getWwuUsedPrice();
                double profit = current.getProfit();
                double ROI = current.getROI() * 100;
                formattedData = "\n" + String.format("%-15s %-15s %-15s$%-14.2f $%-14.2f $%-14.2f %-13.2f",dept, course,
                        isbn, azUsedPrice, wwuUsedPrice, profit, ROI);
                saveString += formattedData;
            }
            if (Actions.save(saveString, fileName)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    /**
     * Untested
     * @param departments
     * @return
     */
    public PriorityQueue<Department> largestDepartments(Departments departments){
        Map<String,Department>departmentMap=departments.getDepartments();
        Comparator<Department> comparator= new Comparator<Department>() {
            @Override
            public int compare(Department o1, Department o2) {
                if (o1.size()>o2.size())return 1;
                else if (o1.size()==o2.size()) return 0;
                else return -1;
            }
        };
        PriorityQueue<Department> orderedDepartments=new PriorityQueue<Department>(comparator);
        Iterator iterator= departmentMap.entrySet().iterator();
        while (iterator.hasNext()){
            orderedDepartments.add(departmentMap.get(iterator.next()));
        }
        return orderedDepartments;
    }

    /**
     * uses the getLowestAzPrice() and getLowestWwuPrice() so that the lowest difference is logged.  AZ prices are
     * always subtracted from WWU prices so that the majority of the price differences will be positive.
     */

    public void pricedDifferences(){
        ArrayList<Book> uniqueBooks=departments.getUniqueBooks();
        ArrayList<Double> differences= new ArrayList<Double>();
        Map<String,Double> boxAndWhisker;
        String saveString=String.format("%-10s %-10s %-10s %-10s %-10s %-10s %-10s","Name","Q1","Q2","Q3","IQR","lowerBound","upperBound")+"\n";
        String fileName=Actions.fileNameAssignment("priceDifferenceBoxAndWhisker",".txt");
        for(int i=0;i<uniqueBooks.size();i++){
            differences.add(uniqueBooks.get(i).getLowestWWUPrice()-uniqueBooks.get(i).getLowestAZPrice());
        }
        boxAndWhisker=Statistics.boxAndWhisker(differences,1.5);
        saveString+=Statistics.boxAndWhiskerToString(boxAndWhisker,"Delta");
        Actions.save(saveString,fileName);
    }


}
