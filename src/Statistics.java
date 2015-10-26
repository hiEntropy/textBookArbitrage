import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Austin on 5/8/2015.
 */
public class Statistics {

    /**
     * This method assumes that the items are not distinguishable
     * This works for sampling without replacement
     * @param n number of objects total
     * @param r number of objects selected at one time
     * @return the number of possible combinations give n and r
     */
    public static BigDecimal permutation(int n, int r ){
        return factorial(n).divide(factorial(n - r));
    }

    public static int permutationInt(int n, int r ){
        return permutation(n,r).intValue();
    }

    /**
     * This assumes that items are distinguishable
     * @param n total number of items in your set
     * @param choose is the number of items you wish to choose at onces
     * @return the number of possible combinations as a BigDecimal
     */
    public static BigDecimal combinations(int n, int choose){
        BigDecimal numerator= factorial(n);
        return numerator.divide(factorial(choose).multiply(factorial(n - choose)));
    }

    /**
     * This assumes that items are distinguishable
     * @param n total number of items in your set as an int
     * @param choose is the number of items you wish to choose at once as an int
     * @return the number of possible combinations as an int
     */
    public static int combinationsInt(int n, int choose) {
        return combinations(n,choose).intValue();
    }

    /**
     * This is a way to find out possible outcomes, outcomes being the order of things as they are chosen. For example,
     * if we need a straight we can get in Poker from A,2,3,4,5 there are 5*4 cards that would be necessary but they can
     * come inn any order.  This is an unorderedSet Problem
     * @param n number of items in your set as an int
     * @param r number of items to be chosen at once as an int
     * @return number of different ways that the desired r items could arrive for as a BigDecimal.
     */
    public static BigDecimal distinguishablePermutations(int n, int r){
        return combinations(n-1+r,r);
    }

    public static int distinguishablePermutationsInt(int n, int r){
        return combinations(n-1+r,r).intValue();
    }

    public static BigDecimal factorial(int n){
        if (n==0) return BigDecimal.valueOf(0);
        if (n<0) return BigDecimal.valueOf(-1);
        BigDecimal result=new BigDecimal(1.0);
        for (int i=1;i<=n;i++)result=result.multiply(BigDecimal.valueOf(i));
        return result;
    }

    /**
     *The sad part:
     *--The method makes a second copy of the list doubling the memory requirements.  Issue being that Lists only carry
     * objects and there isn't any default way to determine which object is greater than another
     * @param list Will only work with a list of Integers, Doubles, Floats and Shorts
     * @param scalar a double value that will be applied to the IQR and therefore on the lower and upper boundaries
     * @return returns an empty map if the Parameters list does not contain a primitive numbers.
     **/
    public static Map<String,Double>boxAndWhisker(List list,double scalar){
        Map<String,Double> map=new TreeMap<String, Double>();
        Double Q1;
        double Q2;
        double Q3;
        if (scalar==0)return map;
        if (list.size()<3)return map;
        if (list.get(0) instanceof Double || list.get(0) instanceof Integer || list.get(0) instanceof Float ||
                list.get(0) instanceof Short ) {
            double[] array= toArrayRemoveNegNums(list);
            quickSort(array);
            if (list.size()%2==0){
                Q1=(array[list.size()/4]+array[((list.size()/4)-1)])/2;//averages the two numbers
                Q2=(array[(list.size()/2)]+array[((list.size()/2)-1)])/2;
                Q3=(array[3*list.size()/4]+array[3*(list.size()/4)-1])/2;//multiplies by 3 divides by 4 or multiply by .75
                //keeping the convention consistent with the two lines above

            }else{
                Q1=array[list.size()/4];
                Q2=array[list.size()/2];
                Q3=array[3*list.size()/4];
            }
            map.put("Q1",Q1);
            map.put("Q2",Q2);
            map.put("Q3",Q3);
            map.put("IQR",(Q3-Q1));
            map.put("lowerBound",Q1-(map.get("IQR")*scalar));
            map.put("upperBound",Q3+(map.get("IQR")*scalar));
        }
        return map;
    }

    public static String boxAndWhiskerToString(Map<String,Double> boxAndWhisker,String name) {
        String saveString=null;
        if (boxAndWhisker!=null && boxAndWhisker.size()==6){
            double Q1=boxAndWhisker.get("Q1");
            double Q2=boxAndWhisker.get("Q2");
            double Q3=boxAndWhisker.get("Q3");
            double IQR=boxAndWhisker.get("IQR");
            double lowerBound=boxAndWhisker.get("lowerBound");
            double upperBound=boxAndWhisker.get("upperBound");
            saveString = String.format("%-10s %-10.2f %-10.2f %-10.2f %-10.2f %-10.2f %-10.2f",name,
                    Q1,Q2,Q3,IQR,lowerBound,upperBound) + "\n";
        }
        return saveString;
    }

    /**BUG HERE!!!! solution... remove negative values upstream and make this only convert to double[]
     * Takes in a list of any primitive numeric type and converts it to an array of doubles.  Any non-numeric primitive
     * will result in a null array being sent.  This also removed negative values and zero from the array.  This is
     * useful in our price analysis
     * @param list of objects
     * @return double[] or null
     */
    public static double[] toArrayRemoveNegNums(List list){
        if (list==null)return null;
        double[] rValue = new double[list.size()];
        if (list.get(0) instanceof Double) {
            for (int i = 0; i < list.size(); i++) {
                if ((Double)list.get(i)>0) {
                    rValue[i] = (Double) list.get(i);
                }
            }
            return rValue;
        }
        if (list.get(0) instanceof Integer){
            for (int i = 0; i < list.size(); i++) {
                if ((Integer) list.get(i)>0.0) {
                    rValue[i] = ((Integer) (list.get(i))).doubleValue();
                }
            }
            return rValue;
        }
        if (list.get(0) instanceof Short){
            for (int i = 0; i < list.size(); i++) {
                if ((Short)list.get(i)>0.0) {
                    rValue[i] = ((Short) (list.get(i))).doubleValue();
                }
            }
            return rValue;
        }
        if (list.get(0) instanceof Float){
            for (int i = 0; i < list.size(); i++) {
                if ((Float)list.get(i)>0.0) {
                    rValue[i] = ((Float) (list.get(i))).doubleValue();
                }
            }
            return rValue;
        }
        return null;
    }

    /**
     *
     * @param p
     * @return
     */
    public static double invNorm(double p) {
        // Coefficients in rational approximations
        double[] a= new double[]{-3.969683028665376e01,  2.209460984245205e02,
                -2.759285104469687e02,  1.383577518672690e02,
                -3.066479806614716e01,  2.506628277459239e00};

        double[] b = new double[]{-5.447609879822406e01,  1.615858368580409e02,
                -1.556989798598866e02,  6.680131188771972e01,
                -1.328068155288572e01 };

        double[] c = new double[]{-7.784894002430293e-03, -3.223964580411365e-01,
                -2.400758277161838e00, -2.549732539343734e00,
                4.374664141464968e00,  2.938163982698783e00};

        double[] d = new double[] {7.784695709041462e-03, 3.224671290700398e-01,
                2.445134137142996e00,  3.754408661907416e00};

        // Define break-points.
        double pLow  = 0.02425;
        double pHigh = 1 - pLow;

        // Rational approximation for lower region:
        if ( p < pLow ) {
            double q  = Math.sqrt(-2*Math.log(p));
            return (((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
                    ((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
        }

        // Rational approximation for upper region:
        if ( pHigh < p ) {
            double q  = Math.sqrt(-2*Math.log(1-p));
            return -(((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
                    ((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
        }

        // Rational approximation for central region:
        double  q = p - 0.5;
        double r = q*q;
        return (((((a[0]*r+a[1])*r+a[2])*r+a[3])*r+a[4])*r+a[5])*q /
                (((((b[0]*r+b[1])*r+b[2])*r+b[3])*r+b[4])*r+1);
    }

    public static double mean(List<Long> data){
        double sum=0;
        int i;
        for (i=0;i<data.size();i++){
            sum+=data.get(i);
        }
        return sum/((double)i);
    }

    /**
     * This calculates the sample variance of a set of data.  Formula used is summation of pow(X-mean,2)/n X being the
     * data point mean being the Sample mean and n being the total number of samples.
     * @param data
     * @return
     */
    public static double variance(List<Long> data){
        double mean=mean(data);
        double variance=0;
        int i;
        for (i=0;i<data.size();i++){
            variance+=Math.pow(data.get(i)-mean,2);
        }
        return variance/(double)(i);
    }

    /**
     * does the same thing as the other variance function except this one does not calculate mean and must be passed to
     * the function; a nice thing if you have already calculated it since calculating mean is a O(n) operation.
     * @param data
     * @param mean
     * @return
     */
    public static double variance(List<Long> data, double mean){
        double variance=0;
        int i;
        for (i=0;i<data.size();i++){
            variance+=Math.pow(data.get(i)-mean,2);
        }
        return variance/(double)i;
    }

    /**
     * Use this to find the standard deviation of a list of data in the event that variance has not already been
     * calculated. This is an O(n) operation
     * @param data
     * @return
     */
    public static double standardDeviation(List<Long> data){
        return Math.sqrt(variance(data));
    }

    /**
     * Use this in the case that you have already calculated variance.  This is an O(1) operation since all the
     * algorithm does is take the square root of the variance.
     * @param variance
     * @return
     */
    public static double standardDeviation(double variance){
        return Math.sqrt(variance);
    }

    public static int necessaryTrials(double sigma, double standardDeviations,double confidenceInterval){
        double confidence=(1-confidenceInterval)/2;
        return roundUp(Math.pow(invNorm(confidence)*sigma/standardDeviations,2));
    }

    /**
     * Generic swap method for an integer array.  In this method order doesn't matter so swap(array,b,c) will do the same
     * as swap(array,c,b).
     * @param array
     * @param indexOne
     * @param indexTwo
     */
    public static void swap(int[] array,int indexOne, int indexTwo){
        if (array!=null && indexOne!=indexTwo && indexOne>-1 && indexTwo>-1) {
            int tempValue = array[indexOne];
            array[indexOne] = array[indexTwo];
            array[indexTwo] = tempValue;
        }
    }

    public static void swap(double[] array,int indexOne, int indexTwo){
        if (array!=null && indexOne!=indexTwo && indexOne>-1 && indexTwo>-1) {
            double tempValue = array[indexOne];
            array[indexOne] = array[indexTwo];
            array[indexTwo] = tempValue;
        }
    }
    public static int partition(int[] array, int start, int end){
        if (array.length>1) {
            int pivotValue = array[end];
            int partition=start;
            for (int i=start;i<end;i++){
                if (array[i]<=pivotValue){
                    swap(array,partition,i);
                    partition++;
                }
            }
            if (array[partition]>pivotValue) {
                swap(array, partition, end);
            }
            return partition;
        }
        return 0;
    }
    public static int partition(double[] array, int start, int end){
        if (array.length>1) {
            double pivotValue = array[end];
            int partition=start;
            for (int i=start;i<end;i++){
                if (array[i]<=pivotValue){
                    swap(array,partition,i);
                    partition++;
                }
            }
            if (array[partition]>pivotValue) {
                swap(array, partition, end);
            }
            return partition;
        }
        return 0;
    }

    public static void quickSort(int[] array){
        if (array==null) return;
        if (array.length<2)return;
        quickSort(array,0,array.length-1);
    }
    public static void quickSort(double[] array){
        if (array==null) return;
        if (array.length<2)return;
        quickSort(array,0,array.length-1);
    }
    private static void quickSort(double[] array,int start,int end){
        if (start<end) {
            int partition = partition(array,start,end);
            quickSort(array, start, partition - 1);
            quickSort(array, partition + 1, end);
        }
    }

    private static void quickSort(int[] array,int start,int end){
        if (start<end) {
            int partition = partition(array,start,end);
            quickSort(array, start, partition - 1);
            quickSort(array, partition + 1, end);
        }
    }

    public static int roundUp(double number){
        if (number%1==0){
            return (int)number;
        }
        else{
            return (int)number+1;
        }
    }


}
