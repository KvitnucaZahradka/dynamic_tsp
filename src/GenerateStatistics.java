import org.jetbrains.annotations.Contract;

import java.util.stream.IntStream;


/**
 * Created by Pipjak on 06/04/2017.
 */
public class GenerateStatistics {

    /* NON-STATIC FIELDS */
    private int counter = 0;
    private int[] initialCombination;
    private int upperBound;

    int lastIndex;
    int lengthOfCombination;

    /* CONSTRUCTOR */
    GenerateStatistics(int lengthOfCombination, int upperBound, int startIndex){
        synchronized(this) {
        /* create the initial seed */
            this.initialCombination = IntStream.iterate(startIndex, n -> n + 1).limit(lengthOfCombination).toArray();

            this.upperBound = upperBound;
            this.lengthOfCombination = lengthOfCombination;
            this.lastIndex = lengthOfCombination - 1;
        }
    }

    /* STATIC METHODS */

    /**
     * Binomial coefficient n choose k approximation:
     * */
    static int binomialApproximation(int n, int k){
        if(k == 0){
            return 1;
        }
        else {
            if(k <= n/2)
                return ((int) Math.pow((((double) (n)) / ((double) k)) * Math.E, (double) k));
            else
                return ((int) Math.pow((((double) (n)) / ((double) (n - k))) * Math.E, (double) (n - k)));
        }
    }


    /**
     * I. print array of interest
     * */
    static void printArray(int[] array){
        for (int anArray : array) System.out.print(anArray + " ");
            System.out.println();
    }

    /**
     * I. unique index for every subset
     * */
    static int listIndex(int[] combination){
        Double result = 0.0;

        if(combination == null)
            return result.intValue();

        for(int i: combination){
            result += Math.pow(2.0, (double) i);
        }
        return result.intValue();
    }

    /**
     * II.
     * calculates unique index for every subset
     */
    static int index(int[] combination){
        int result = 0;

        if(combination == null)
            return result;

        if(combination.length == 1 && combination[0] == 1) {
            return 1;
        }
        else{
            for (int i : combination) {
                result += Math.pow(2, i - 1);
            }
            return result + 1;
        }
    }


    synchronized int[] getNextPermutation(){
        if(this.counter == 0){
            this.counter++;
            return this.initialCombination;
        }
        else{
            this.nextPermut();
            this.counter++;
            return this.initialCombination;
        }
    }


    /** generate permutations dynamically */
    private synchronized void nextPermut(){

        /* if last index is smaller than 0, return */
        if(this.lastIndex != this.lengthOfCombination - 1){
            this.lastIndex = this.rewire();
        }
        else if(this.initialCombination[this.lastIndex] < this.upperBound){
            this.initialCombination[this.lastIndex]++;
        }
        else{
            this.lastIndex = this.rewire();
        }
    }


    /** rewire method */
    private synchronized int rewire(){

        int tempIndex = this.rewireIndex();

        if(tempIndex != -1){
            int temp = this.initialCombination[tempIndex] + 1;
            int counter = 0;

            for(int i = tempIndex; i < this.lengthOfCombination; i++){
                this.initialCombination[i] = temp + counter;
                counter++;
            }
        }
        tempIndex = this.rewireIndex();
        return tempIndex;
    }


    /** rewireIndex method */
    @Contract(pure = true)
    private synchronized int rewireIndex(){
        for(int i = this.lengthOfCombination-1; i>=0; i--){
            if(this.initialCombination[i] != this.upperBound - this.lengthOfCombination + i + 1){
                return i;
            }
        }
        return -1;
    }

    /**
     * initialize matrix
     * */
    private static void initial(int[][] matrix){
        for(int i = 0; i<matrix.length; i++){
            for(int j = 0; j<matrix[0].length; j++)
                matrix[i][j] = Integer.MAX_VALUE;
        }
    }


    /* MAIN CLASS FOR TESTING */
    /**
     * should be ignored, just for private trial usages
     * */
    @Deprecated
    public static void main(String[] args) {

        for (int k = 1; k <= 3; k++){
            GenerateStatistics gr = new GenerateStatistics(k, 15, 2);

            for (int i = 0; i < 777777777; i++) {
                int[] comb = gr.getNextPermutation();

                // awful solution but ok, what can I do
                if(gr.lengthOfCombination==1 && gr.lastIndex == -1){
                    break;
                }

                GenerateStatistics.printArray(comb);

                if (gr.lastIndex == -1) {
                    break;
                }

                //GenerateStatistics.printArray(comb);
            }
        }
        System.out.println("FULL ARRAY");
        int[] fullCombination =  IntStream.iterate(1, n -> n + 1).limit(20).toArray();
        GenerateStatistics.printArray(fullCombination);

        System.out.println("the upper bound is " + Integer.MAX_VALUE);

        System.out.println("equals??");
        int[][] lal = new int[2][3];

        System.out.println("0, 1 element is: " + lal[0][1]);

        System.out.println("the sizes " + lal.length + " " + lal[0].length);

        GenerateStatistics.initial(lal);

        System.out.println("0, 1 element is: " + lal[0][1]);

        System.out.println("combinatorial utils ");
        System.out.println("n over two "+ GenerateStatistics.binomialApproximation(25, 13));

        System.out.println("number of processors " + Runtime.getRuntime().availableProcessors());
    }
}
