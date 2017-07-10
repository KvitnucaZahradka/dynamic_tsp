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
    static int MAX;
    static int MULTI = 1000;
    static float INFINITY = Float.MAX_VALUE;
    private static int THRESHOLD = 1000000;


    /* GLOBAL NON-STATIC FIELDS */
    private int numberOfNodes;
    private Float[][] graph;

    /* CONCURRENT SOLUTION */
    private List<ConcurrentHashMap<Integer, Float>> aMatrix;
    private Index ind;

    /* CONSTRUCTORS */
    Graph(String nameOfFile) throws IOException {
        ReadData read = new ReadData();

        this.numberOfNodes = read.readNumberOfCities(nameOfFile);
        this.graph = read.readPositions(nameOfFile, this.numberOfNodes);

        Graph.MAX =  (int) Math.pow(2, this.numberOfNodes - 1);

        /* CONCURRENT SOLUTION */
        this.ind = new Index(1, Graph.THRESHOLD);
        this.aMatrix = new ArrayList<>(ind.mod);

        for(int i = 0; i < ind.mod; i++ ){
            this.aMatrix.add(new ConcurrentHashMap<>(Graph.THRESHOLD));
        }



        /*
         seeding the combinations, combinations are produced concurrently and dynamically
          */
        int[] combination = {1};

        /*
          index == any combination has an integer index that is used as index for a hash map
          I am using hash map instead of matrix
          */
        int index = this.index(combination, 1);

        /*
          the aMatrix is the hash map where the temporary solutions are kept
          */
        this.aMatrix.get(ind.modIndex(index)).put(index,(float) 0.0);
    }


    /* PUBLIC METHODS */

    /**
     * I.
     * method that returns the best TSP path length
     */
    int calculateTSPdistance() throws BrokenBarrierException, InterruptedException, ExecutionException {
        /*
          I want to keep only the most recent hash map, that is needed to calculate the next hash map in the
          iteration of the algorithm (dynamic programming)
          */
        List<ConcurrentHashMap<Integer, Float>> newAmatrix;

        /*
          also I will replace old Index that I call "ind" with a new Index called "newIndex"
          */
        Index newIndex;

        /*
          because the time is of extreme importance I want to measure the time increase in every iteration
          */
        Runtime run = Runtime.getRuntime();

        int startM = 2;
        FindMinimum minimum;

        /* since I have just two cores to use, I will have just two futures. This part is easily scalable to many cores.
          */
        Future future1, future2;
        /*
          the ExecutorService takes care of my threads && processors
          */
        ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        /* first look whether you have some previously computed matrix
          NOTE: at the end I have not used this functionality. Might be used in some cases. Was too slow for what I
          wanted.
          */
        if(FileOpen.isFileIn(this.numberOfNodes)){
            System.out.println("Found and opening the file with name " + FileOpen.FILENUMBER.toString() + ".temp");
            this.aMatrix = (new FileOpen<ConcurrentHashMap<Integer,Float>>()).open();
            startM = FileOpen.FILENUMBER + 1;
        }

        /*
          the most demanding part of the whole algorithm:
          iterating through the subset of different sizes m in {2, 3, 4 ..., n}
          */
        for(int m = startM; m <= this.numberOfNodes; m++){

            System.out.println("doing the combination length : " + m);
            System.out.println("I still have: " + run.freeMemory());


            /* initialize combinatorial object
              you want to have it of the length smaller than m, since 1 is added automatically
              */
            GenerateStatistics combi = new GenerateStatistics(m - 1, this.numberOfNodes, 2);


            /* create a new A matrix
              */
            int size = (int) Math.floor(GenerateStatistics.binomialApproximation(this.numberOfNodes, m));

            newIndex = new Index(size, Graph.THRESHOLD);
            newAmatrix = new ArrayList<>(newIndex.mod);

            /* initialize the list of hash maps
              */
            for(int i = 0; i<newIndex.mod; i++){
                newAmatrix.add(new ConcurrentHashMap<>(Graph.THRESHOLD));
            }

            System.out.println("size is " + size);

            minimum = new FindMinimum(combi, this.graph, this.ind, newIndex,
                    (ArrayList<ConcurrentHashMap<Integer,Float>>) this.aMatrix,
                    (ArrayList<ConcurrentHashMap<Integer,Float>>) newAmatrix);

            /* submitting the minimum class CONCURRENTLY to future1 and future2 */
            future1 = e.submit(minimum);
            // System.out.println(" number of active threads: " + ((ThreadPoolExecutor) e).getActiveCount());

            future2 = e.submit(minimum);
            // System.out.println(" number of active threads: " + ((ThreadPoolExecutor) e).getActiveCount());

            /*
              this step is really crucial, by the future.get() you are getting out at the right time,
              concurrently
              */
            future1.get();
            future2.get();


            /*
              swapping the index "ind" for a "newIndex"
              */
            this.ind = newIndex;

            /*
              probably unnecessary, but I was desperate :) at some point
              */
            this.aMatrix = null;

            /* try to call the garbage collector, not needed probably, but I was desperate for resources :)
               */
            System.gc();

            /* setting the new matrix instead of old one
              */
            this.aMatrix = newAmatrix;
        }
        /*
          do not forget to bring the towel!
          */
        e.shutdown();
        return this.findTrueMinimum();
    }


    /* PRIVATE METHODS */

    /**
     * This is a last step in the program it looks for the true minimum in the graph
     * */
    private int findTrueMinimum(){
        int index;
        float tempMin = Graph.INFINITY;

        int[] fullCombination =  IntStream.iterate(2, n -> n + 1).limit(this.numberOfNodes - 1).toArray();

        for(int j = 1; j < this.numberOfNodes; j++){
            index = this.index(fullCombination, j + 1);
            float min = this.findValueFloat(index) + this.graph[j][0];

            if(min < tempMin)
                tempMin = min;
        }
        return (int) Math.floor(tempMin);
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


    private float findValueFloat(int index){
        return this.aMatrix.get(this.ind.modIndex(index)).getOrDefault(index, Graph.INFINITY);
    }

}
