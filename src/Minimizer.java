import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by Pipjak on 29/05/2017.
 */
public class Minimizer {

    /* FIELDS */
    private GenerateStatistics combi;
    private Float[][] graph;
    private ArrayList<ConcurrentHashMap<Integer,Float>> aMatrix, newAmatrix;
    private Index ind, newIndex;
    private CyclicBarrier barrier;

    /* CONSTRUCTOR */
    Minimizer(GenerateStatistics combi, Float[][] graph, Index ind, Index newIndex,
              ArrayList<ConcurrentHashMap<Integer, Float>> aMatrix,
              ArrayList<ConcurrentHashMap<Integer, Float>> newAmatrix){

        this.combi = combi;
        this.graph = graph;
        this.aMatrix = aMatrix;
        this.newAmatrix = newAmatrix;
        this.ind = ind;
        this.newIndex = newIndex;

    }

    /* PUBLIC METHODS */


    /* PRIVATE CLASSES */
    private class FindMinimum extends Thread {

        public FindMinimum() {
            super();
        }

        @Override
        public void run() {
            try {
                barrier.await(); // label1
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            // calculate the minimum here
            minimum();

            try {
                barrier.await(); // label2
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        // looking for the minimum
        private void minimum() {
            while (true) {
                if (combi.lengthOfCombination == 1 && combi.lastIndex == -1) {
                    break;
                }

                int[] nextCombination = combi.getNextPermutation().clone();

                if (combi.lastIndex == -1) {
                    break;
                }

                /* loop through all except 1 in a given combination */
                for (int jIndex = 0; jIndex < nextCombination.length; jIndex++) {
                    if (nextCombination[jIndex] == 1)
                        continue;

                    int jValue = nextCombination[jIndex];
                    float minimum = findMinimumFloat(nextCombination, jValue);

                    /* calculate the index of the set S_{1j}, the S_{1j} is a set that contains 1 and j
                    *  */
                    int indexSj = index(nextCombination, jValue);
                    newAmatrix.get(newIndex.modIndex(indexSj)).put(indexSj, minimum);
                }
            }
        }
    }

    /* CALCULATE METHOD */

    public void calculate() throws BrokenBarrierException, InterruptedException {
        int processors = Runtime.getRuntime().availableProcessors();
        this.barrier = new CyclicBarrier(processors+1);

        for(int i = 0; i<processors; i++){
            new FindMinimum().start();
        }
        //this.barrier.await();
        //System.out.println("Please wait ... there are " + this.barrier.getNumberWaiting() + " awaiting");
        this.barrier.await();
        System.out.println("Finished");
        this.barrier.reset();
    }


    /* PRIVATE METHODS */
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
        int index, aNextCombination;
        float aSjKplusC, bestMinimum = Graph.INFINITY;

        for (int i = 0; i <= nextCombination.length; i++){

            if(i==0)
                aNextCombination = 1;
            else
                aNextCombination = nextCombination[i - 1];

            if (aNextCombination == j)
                continue;

            index = this.index(nextCombination, j, aNextCombination);

            aSjKplusC = this.findValueFloat(index) + this.graph[aNextCombination - 1][j - 1];

            if (aSjKplusC < bestMinimum) {
                bestMinimum = aSjKplusC;
            }
        }
        return bestMinimum;
    }

    private float findValueFloat(int index){
        // System.out.println("size of aMatrix is " + this.aMatrix.size());
        // System.out.println("index is " + this.ind.modIndex(index));
        return this.aMatrix.get(this.ind.modIndex(index)).getOrDefault(index, Graph.INFINITY);
    }



}
