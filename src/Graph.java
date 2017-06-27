import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Created by Pipjak on 06/04/2017.
 */

class Graph {

    /* GLOBAL STATIC FIELDS */
    /**
     * I am implementing static value for infinity, in some cases one might want to change this.
     * For example if you have guaranteed that any graph edge length is smaller than some number
     * smaller than Integer.MAX_VALUE
     * */
    static float INFINITY = Float.MAX_VALUE;
    public static int MULTI = 1000;
    private static int THRESHOLD = 1000000;


    /* GLOBAL NON-STATIC FIELDS */
    private int numberOfNodes;

    /* NEW SOLUTION */
    static int MAX;
    // private int min;

    // private Double[][] graph;
    //private Integer[][] graph;
    Float[][] graph;

    /* BEFORE SOLUTION */
    // private HashMap<Integer, Float> aMatrix;
    //private HashMap<List<Integer>, Double> aMatrix;

    /* MOD SOLUTION */
    private List<ConcurrentHashMap<Integer, Float>> aMatrix;
    private Index ind;

    /* CONSTRUCTORS */
    Graph(String nameOfFile) throws IOException {
        ReadData read = new ReadData();

        this.numberOfNodes = read.readNumberOfCities(nameOfFile);

        //OLD SOLUTION
        //this.graph = read.readPositions(nameOfFile, this.numberOfNodes);
        //this.graph = read.readPositionsInteger(nameOfFile, this.numberOfNodes);
        this.graph = read.readPositionsFloat(nameOfFile, this.numberOfNodes);

        Graph.MAX =  (int) Math.pow(2, this.numberOfNodes - 1);

        /* BEFORE SOLUTION */
        //this.aMatrix = new HashMap<>(1000);


        /* MOD SOLUTION */
        this.ind = new Index(1, Graph.THRESHOLD);
        this.aMatrix = new ArrayList<>(ind.mod);

        for(int i = 0; i < ind.mod; i++ ){
            this.aMatrix.add(new ConcurrentHashMap<>(Graph.THRESHOLD));
        }




        int[] combination = {1};

        /* BEFORE SOLUTION */
        int index = this.index(combination, 1);

        //this.aMatrix.put(index, 0.0);

        /* NEW SOLUTION */
        this.aMatrix.get(ind.modIndex(index)).put(index,(float) 0.0);





        // do the same as before but for the bMatrix
        // this.calculateMaxAndMin(1);

        // int lenRow = this.rowLength();
        // int lenCol = this.numberOfNodes;


        // this.bMatrix = new Double[lenRow][lenCol];


        // int matrixIndex = this.matrixIndex(combination);
        // this.bMatrix[matrixIndex][0] = 0.0;

    }


    /* PUBLIC METHODS */

    /**
     * I.
     * method that returns the best TSP path length
     */
    int calculateTSPdistance() throws BrokenBarrierException, InterruptedException, ExecutionException {
        List<ConcurrentHashMap<Integer, Float>> newAmatrix;

        Index newIndex;
        Runtime run = Runtime.getRuntime();
        int startM = 2;
        ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        FindMinimum minimum;
        Future future1, future2;
        // Minimizer mini;

        /* this loops through all possible sets */
        //ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        /* first look whether you have some previously computed matrix */
        if(FileOpen.isFileIn(this.numberOfNodes)){
            System.out.println("Found and opening the file with name " + FileOpen.FILENUMBER.toString() + ".temp");
            this.aMatrix = (new FileOpen<ConcurrentHashMap<Integer,Float>>()).open();
            startM = FileOpen.FILENUMBER + 1;
        }

        /* iterating through the subset of different sizes m in {2, 3, 4 ..., n} */
        for(int m = startM; m <= this.numberOfNodes; m++){
            System.out.println("doing the combination length : " + m);
            System.out.println("I still have: " + run.freeMemory());

            /* NEW SOLUTION */
            // this.calculateMaxAndMin(m);


            /* initialze combinatorial object */
            /* you want to have it of the length smaller than m, since 1 is added automatically */
            GenerateStatistics combi = new GenerateStatistics(m - 1, this.numberOfNodes, 2);


            /* BEFORE SOLUTION*/
            /*create a new A matrix */
            int size = (int) Math.floor(GenerateStatistics.binomialApproximation(this.numberOfNodes, m));

            newIndex = new Index(size, Graph.THRESHOLD);
            newAmatrix = new ArrayList<>(newIndex.mod);

            /* initialize the list of hash maps */
            for(int i = 0; i<newIndex.mod; i++){
                newAmatrix.add(new ConcurrentHashMap<>(Graph.THRESHOLD));
            }

            System.out.println("size is " + size);

            minimum = new FindMinimum(combi, this.graph, this.ind, newIndex,
                    (ArrayList<ConcurrentHashMap<Integer,Float>>) this.aMatrix,
                    (ArrayList<ConcurrentHashMap<Integer,Float>>) newAmatrix);

            future1 = e.submit(minimum);

            // System.out.println(" number of active threads: " + ((ThreadPoolExecutor) e).getActiveCount());


            future2 = e.submit(minimum);


            // System.out.println(" number of active threads: " + ((ThreadPoolExecutor) e).getActiveCount());

            future1.get();
            future2.get();


            //e.shutdown();

            /*
            mini = new Minimizer(combi, this.graph, this.ind, newIndex,
                    (ArrayList<ConcurrentHashMap<Integer,Float>>) this.aMatrix,
                    (ArrayList<ConcurrentHashMap<Integer,Float>>) newAmatrix);

            mini.calculate();
            */


            /*
            int processors = Runtime.getRuntime().availableProcessors();
            FindMinimum[] threads = new FindMinimum[processors];

            for(int i=0; i < processors; i++) {
                threads[i] = new FindMinimum(combi, this.graph, this.ind, newIndex,
                        (ArrayList<HashMap<Integer,Float>>) this.aMatrix, (ArrayList<HashMap<Integer,Float>>) newAmatrix);

                // You may need to pass in parameters depending on what work you are doing and how you setup your thread.
                threads[i].start();           // Start the Thread;
            }

            if(threads[0].isAlive()){
                System.out.println("still running");
            }
            */


                /* -------------------------------------------------------------------------------- */
                /* IDEA: the WHOLE PROCESS WILL BE DONE CONCURENTLY BY TWO THREADS */
                /* ---------- */

                /*
                // this is not ideal solution, but just a quick fix
                if(combi.lengthOfCombination == 1 && combi.lastIndex == -1){
                    break;
                }

                int[] nextCombination = combi.getNextPermutation();

                // break if you exhaust all combination of given length and containing 1
                if(combi.lastIndex == -1){
                    break;
                }

                // loop through all except 1 in a given combination
                for(int jIndex = 0; jIndex < nextCombination.length; jIndex++){
                    if(nextCombination[jIndex] == 1)
                        continue;

                    int jValue = nextCombination[jIndex];
                    float minimum = this.findMinimumFloat(nextCombination, jValue);

                    // calculate the index of the set S_{1j}, the S_{1j} is a set that contains 1 and j
                    int indexSj = this.index(nextCombination, jValue);

                    newAmatrix.get(newIndex.modIndex(indexSj)).put(indexSj, minimum);
                }

                */


                /* ------------------------------------------------------------ */

            //}
            /* BEFORE SOLUTION */
            this.ind = newIndex;
            this.aMatrix = null;

            /* try to call the garbage collector */
            System.gc();

            /* setting the new matrix instead of old one */
            this.aMatrix = newAmatrix;

            /* saving the new matrix to the file */
            /*
            if( m >= 11) {
                FileSave.FileSave(((Integer) m).toString(), newAmatrix);
            }
            */

        }
        e.shutdown();
        return this.findTrueMinimum();
    }


    /* PRIVATE METHODS */
    private void initializeBmatrix(Double[][] bMatrix){
        for(int i = 0; i<bMatrix.length; i++){
            for(int j = 0; j<bMatrix[0].length; j++){
                bMatrix[i][j] = (double) Graph.INFINITY;
            }
        }
    }


    /*
    private void calculateMaxAndMin(int combinationLength){
        // calculate min and MAX for given combination length
        this.MAX = ((Double) ( (Math.pow(2.0,(double) (this.numberOfNodes - combinationLength + 1))*
                ( -1.0 + Math.pow(2.0,(double) combinationLength))) + 2.0)).intValue();

        this.min = ((Double) (2.0*(Math.pow(2.0, (double) combinationLength) - 1.0))).intValue();
    }
    */


    /*
    private int rowLength(){
        return this.MAX - this.min + 1;
    }
    */


    /**
     * This is a last step in the program it looks for the true minimum in the graph
     * */
    private int findTrueMinimum(){
        /* BEFORE SOLUTION */
        int index;

        /* OLD SOLUTION */
        // double tempMin = (double) Graph.INFINITY;

        // NEW SOLUTION
        //int tempMin = Graph.INFINITY;
        float tempMin = Graph.INFINITY;

        int[] fullCombination =  IntStream.iterate(2, n -> n + 1).limit(this.numberOfNodes - 1).toArray();

        for(int j = 1; j < this.numberOfNodes; j++){

            /* BEFORE SOLUTION */
            index = this.index(fullCombination, j + 1);


            /* BEFORE SOLUTION */
            //double min = this.findValue(index) + this.graph[j][0];

            /* NEW SOLUTION */
            float min = this.findValueFloat(index) + this.graph[j][0];

            if(min < tempMin)
                tempMin = min;
        }
        return (int) Math.floor(tempMin);
    }


    /**
     * 0.
     * try solving using the explicit matrices and matrix index
     */

    /*
    private int matrixIndex(int[] combination){
        return GenerateStatistics.listIndex(combination) - this.min;
    }
    */

    /*
    private int matrixIndex(int[] combination, int j){
        Double temp = Math.pow(2.0, (double) j) - Math.pow(2.0, (double) combination.length);
        return GenerateStatistics.listIndex(combination) - this.min - temp.intValue();
    }
    */

    /**
    * I.
     * try index as a List<Integer>
     *     list is going to be 0: index of a combination
     *                         1: will be the second index
    * */
    private List<Integer> listIndex(int[] combination, int k){
        List<Integer> ret = new LinkedList<>();

        ret.add(GenerateStatistics.listIndex(combination));
        ret.add(k);

        return ret;
    }

    private List<Integer> listIndex(int[] combination, int j, int k){
        List<Integer> ret = new LinkedList<>();

        Double jIndex = Math.pow(2.0, (double) j);

        ret.add(GenerateStatistics.listIndex(combination) - jIndex.intValue());
        ret.add(k);

        return ret;
    }

    /**
     * II.
     * calculate index that will flatten the matrix A[S\{j}, k] ---> HahsMap{index --> val}
     * where index = GenerateStatistics.index(S\{j}) + Math.pow(2,k).intValue()
     * */
    private int index(int[] combination, int k){
        int kIndex = (int) Math.pow(2, k - 1) + (k-1)*Graph.MAX;

        return GenerateStatistics.index(combination) + kIndex;
    }


    private int index(int[] combination, int j, int k){
        int jIndex = (int) Math.pow(2, j - 1);
        int kIndex = (int) Math.pow(2, k - 1) + (k-1)*Graph.MAX;

        return GenerateStatistics.index(combination) + kIndex - jIndex;
    }

    /**
     * III.
     * function that calculates the minimum in the last loop of the algorithm
     * */

    private float findMinimumFloat(int[] nextCombination, int j){
        float aSjKplusC;
        float bestMinimum = Graph.INFINITY;

        /* BEFORE SOLUTIONS */
        int index;
        int aNextCombination;

        /* there is always a zero step: compare the nextK = 1 */
        //for (int aNextCombination : nextCombination) {
        for (int i = 0; i<= nextCombination.length; i++){

            /* NEW SOLUTION */
            /* building up a next combination */
            if(i==0)
                aNextCombination = 1;
            else
                aNextCombination = nextCombination[i - 1];

            /* main part of looking for minimum */
            if (aNextCombination == j)
                continue;

            /* BEFORE SOLUTION */
            index = this.index(nextCombination, j, aNextCombination);
            // index = this.listIndex(nextCombination, j, aNextCombination);


            /* NEW SOLUTION */
            // index = this.matrixIndex(nextCombination, j);
            // System.out.println("index is " + index);

            /* calculate index */
            /* BEFORE SOLUTION */
            aSjKplusC = this.findValueFloat(index) + this.graph[aNextCombination - 1][j - 1];
            //aSjKplusC = this.graph[aNextCombination - 1][j - 1];

            /* NEW SOLUTION */
            // System.out.println("dimensions are " + this.bMatrix.length + " " + this.bMatrix[0].length);
            // aSjKplusC = this.bMatrix[index][aNextCombination - 1] + this.graph[aNextCombination - 1][j - 1];

            if (aSjKplusC < bestMinimum) {
                bestMinimum = aSjKplusC;
            }
        }
        return bestMinimum;
    }

    /**
     * IV.
     * function that returns the proper value from the "aMatrix"
     * */

    /*
    private double findValue(Object index){
        if(!this.aMatrix.containsKey(index)){
            return Graph.INFINITY;
        }
        else {
            return this.aMatrix.get(index);
        }
    }
    */

    private float findValueFloat(int index){
        return this.aMatrix.get(this.ind.modIndex(index)).getOrDefault(index, Graph.INFINITY);
    }

}
