import java.util.Comparator;

/**
 * Created by Austin on 3/8/2015.
 */
public class Book implements Comparator{
    private String isbn;
    private String title;
    private double wwuNewPrice;
    private double wwuUsedPrice;
    private double azUsedPrice;//az short for amazon
    private double azNewPrice;
    private double profit;
    private boolean isMandatory;

    private Section parent;
    private double ROI;

    public Book(String title,String isbn, boolean isMandatory,double newPrice, double usedPrice,Section parent){
        this.isbn=isbn;
        this.isMandatory=isMandatory;
        this.wwuUsedPrice =usedPrice;
        this.wwuNewPrice =newPrice;
        this.title=title;
        this.parent=parent;
    }
    public  Section getParent(){return parent;}

    public String getTitle(){
        return title;
    }

    public double getProfit(){return profit;}

    public double getWwuNewPrice(){return wwuNewPrice;}

    public double getWwuUsedPrice(){
        return wwuUsedPrice;
    }

    public boolean getIsMandatory(){return isMandatory;}

    public double getAzUsedPrice(){
        return azUsedPrice;
    }

    public double getAzNewPrice(){return azNewPrice;}

    public double getROI(){return ROI;}

    public void setAzUsedPrice(double price){
        azUsedPrice=price;
    }

    public void setAzNewPrice(double price){
        azNewPrice=price;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getIsbn(){
        return isbn;
    }

    /**
     * uses the Return on investment formula (gainFromInvestment-costOfInvestment)/costOfInvestment;
     * @return ROI or -1 if the azUsedPrice is 0.00 meaning that the price has not been retrieved yet or -1 meaning that amazon
     * does not have the book. wwuUsedPrice and wwuNewPrice will only ever be equal to or greater than zero.
     */
    public void setProfitAndROI(){
        if(azUsedPrice>0 && wwuUsedPrice >0){
            double shipping=3.99;//slowest shipping
            double buyBackPercentage=.5;//best buyback price
            double costOfInvestment=azUsedPrice+shipping;
            double possibleGain= (wwuUsedPrice*buyBackPercentage)-costOfInvestment;
            ROI=(possibleGain-azUsedPrice)/costOfInvestment;
            profit=possibleGain;
        }
        else {
            ROI = -1;
        }
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof Book){
            if (((Book) object).getIsbn().equals(isbn)){
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     *For the purposes of this application we are comparing books based on their estimated ROI.
     * @param one is the Object that two will be compared to.  Decisions will be made in a One>Two or One==Two or One<Two
     *            one is were the comparison starts
     * @param two is always compared to one never one to two;
     * @return -1 if one<two 0 if one==two 1 if one>two
     * @throws throws a ClassCastException if the two objects are not the same type of object
     */

    public int compare(Object one,Object two){
        if (one instanceof Book && two instanceof Book){
            if (((Book) one).getROI()<((Book) two).getROI()){return -1;}
            if (((Book) one).getROI()==((Book) two).getROI()){return 0;}
            if (((Book) one).getROI()>((Book) two).getROI()){return 1;}
        }
        throw new ClassCastException();
    }

    /**
     * Implements the comparable interface.  compares everything to the current object.  the passed in object is being
     * compared to the instance of the object that the compareTo Method is being called from.
     * @param object
     * @return
     */
    public int compareTo(Object object){
        if (object instanceof Book){
            if (getROI()<((Book) object).getROI()) return -1;
            if (getROI()==((Book) object).getROI()) return 0;
            if (getROI()> ((Book) object).getROI()) return 1;
        }
        return 1;
    }
}
